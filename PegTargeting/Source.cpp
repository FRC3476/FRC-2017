#include <opencv2/opencv.hpp>
#include <vector>
#include <ntcore.h>
#include <networktables/NetworkTable.h>
#include <chrono>
#include <stdlib.h>
#include <thread>


using namespace std;
using namespace cv;

shared_ptr<NetworkTable> table;
void processImage(Mat &frame);

bool sortByArea(const vector<Point> &lhs, const vector<Point> &rhs) {
	return (contourArea(lhs) > contourArea(rhs));
}

bool sortByY(const Point &lhs, const Point &rhs) {
	return lhs.y > rhs.y;
}
/*
void makeServer()
{
	system("mjpg_streamer -i 'input_file.so -f ./mjpg' -o 'output_http.so -w /usr/local/www -p 8080'");
}
*/
int main()
{
	cout << "launched" << endl;
	Mat frame, frame2;
	VideoCapture cam, cam2;
	cam.open(0);
	cam2.open(1);
	cout << "camera opened" << endl;
	
	system("v4l2-ctl -d 0 -c exposure_auto=1 -c exposure_absolute=5 -c brightness=30");
	system("v4l2-ctl -d 1 -c exposure_auto=1 -c exposure_absolute=5 -c brightness=30");
	cout << "camera set" << endl;
	NetworkTable::SetClientMode();
	NetworkTable::SetTeam(3476);
	
	cout << "networktables" << endl;
	table = NetworkTable::GetTable("");
	table->PutNumber("isOn", 1);
	
	while(1){

		auto begin = chrono::high_resolution_clock::now();
		cam.read(frame);		
		cam2.read(frame2);
		//processImage(frame);
		
		//VideoWriter writer ("./mjpg/out.mjpg", CV_FOURCC('M','J','P','G'), 2, Size(cam.get(3), cam.get(4)), -1);
		auto end = chrono::high_resolution_clock::now();    
		auto dur = end - begin;
		auto ms = std::chrono::duration_cast<std::chrono::milliseconds>(dur).count();
		
		table->PutNumber("gearCapturedAgo", ms);
		NetworkTable::Flush();
		imshow("a", frame);
		imshow("b", frame2);
		if(waitKey(1) == 27){
			break;
		}
		//writer.write(frame);
		
		
		
	}
}


void processImage(Mat &frame){
	
	double yCameraFOV = 38; //USB:38 ZED:45 Kinect:43
	double xCameraFOV = 60; //USB:60 ZED:58 Kinect:57 USB:52 @720
	
	double xResolution = frame.cols;
	double yResolution = frame.rows;
	Mat thres;
	vector<vector<Point> > contours, allContours;
	vector<Vec4i> hierarchy;	
	/*
	Mat green;
	Mat bgr[3];
	//	Scalar LOWER = {65, 60, 40};
	//	Scalar UPPER = {75, 255, 255};
	split(frame, bgr);
	
	threshold(bgr[1], green, 100, 255, THRESH_BINARY);
	
	
		
	morphologyEx(green, green, MORPH_OPEN, Mat(3, 3, CV_8UC1, Scalar(10)), Point(-1, -1), 1);
	
	morphologyEx(thres, thres, MORPH_OPEN, Mat(3, 3, CV_8UC1, Scalar(10)), Point(-1, -1), 1);
	
	imshow("b", green);
	
	findContours(green, allContours, hierarchy, RETR_LIST, CHAIN_APPROX_SIMPLE, Point(0, 0));	
	*/
	
	cvtColor(frame, thres, COLOR_BGR2HSV);
	inRange(thres, Scalar(50, 100, 70), Scalar(90, 255, 255), thres);
	morphologyEx(thres, thres, MORPH_OPEN, Mat(3, 3, CV_8UC1, Scalar(10)), Point(-1, -1), 1);
	
	morphologyEx(thres, thres, MORPH_OPEN, Mat(3, 3, CV_8UC1, Scalar(10)), Point(-1, -1), 1);

	//imshow("cvt", thres);	
	findContours(thres, allContours, hierarchy, RETR_LIST, CHAIN_APPROX_SIMPLE, Point(0, 0));	

	contours.clear();
	for(auto contour : allContours){
		if(contourArea(contour) > 100 && contourArea(contour) < 40000){
			contours.push_back(contour);	
		} 
	}
	
	sort(contours.begin(), contours.end(), sortByArea);

	//cout << "0" << endl;
	if (contours.size() > 1) {
		vector<vector<Point> > hulls(2);
		
		Moments mu = moments(contours[0], false);
		int cX = mu.m10 / mu.m00;
		int cY = mu.m01 / mu.m00;
		
		int midX = cX;
		int midY = cY;
		// Maximum of three correct contours
	
		double firstEps = 0.001 * arcLength(contours[0], true);
		approxPolyDP(contours[0], hulls[0], firstEps, true);	
		drawContours(frame, hulls, 0, Scalar(255, 255, 255), 1, 1);
		Moments mus = moments(contours[1], false);
		
		int scX = mus.m10 / mus.m00;
		int scY = mus.m01 / mus.m00;

		midX = cX + (scX-cX)/2;
		midY = cY + (scY-cY)/2;
		/*
		if(contours.size() > 2){
			// combine em
		
			Moments mut = moments(contours[2], false);
			int tcX = mut.m10 / mut.m00;
			int tcY = mut.m01 / mut.m00;

			if(pow(cX - tcX, 2) > pow(scX - tcX, 2)){
				contours[1].insert(contours[1].end(), contours[2].begin(), contours[2].end());
				// fix to find only tl tr bl br 
				// lul
				Moments mus = moments(contours[1], false);					
				scX = mus.m10 / mus.m00;
				scY = mus.m01 / mus.m00;
			} else {
				contours[0].insert(contours[0].end(), contours[2].begin(), contours[2].end());
					
				Moments mu = moments(contours[0], false);
				cX = mu.m10 / mu.m00;
				cY = mu.m01 / mu.m00;
	
			}			
				
			midX = cX + (scX-cX)/2;
			midY = cY + (scY-cY)/2;
		
		}
		*/
		double secondEps = 0.000001 * arcLength(contours[1], true);
		approxPolyDP(contours[1], hulls[1], secondEps, true);
		drawContours(frame, hulls, 1, Scalar(255, 255, 255), 2);
		
		contours[0].insert(contours[0].end(), contours[1].begin(), contours[1].end());
	
	
		//};
	
	Rect box = boundingRect(contours[0]);
	double distance = (705 * 5) / box.height;
	//cout << distance << endl;
	// f = pd /h
	// d = hf /p
	
	table->PutNumber("angle", ((midX - xResolution/2)/xResolution) * xCameraFOV);
	table->PutNumber("distance", distance);
	table->PutNumber("isVisible", 1);
	table->PutNumber("isNew", 1);
	//cout << ((midX - xResolution/2)/xResolution) * xCameraFOV  << endl;
		
	} else {
		table->PutNumber("angle", 0);
		table->PutNumber("isVisible", 0);
		table->PutNumber("isNew", 1);	
	}
}

/*
int main(int argc, char *argv[]){
	thread server (makeServer);
	thread vision (visionTracking);
	

	server.join();
	vision.join();
}
*/


