#include <opencv2/opencv.hpp>
#include <vector>
#include <ntcore.h>
#include <networktables/NetworkTable.h>
#include <chrono>


using namespace std;
using namespace cv;

bool sortByArea(const vector<Point> &lhs, const vector<Point> &rhs) {
	return (contourArea(lhs) > contourArea(rhs));
}

bool sortByY(const Point &lhs, const Point &rhs) {
	return lhs.y > rhs.y;
}


void processImage(Mat &frame){
	
	double yCameraFOV = 38; //USB:38 ZED:45 Kinect:43
	double xCameraFOV = 45; //USB:45 ZED:58 Kinect:57
	double cameraAngle = 0;
	
	double xResolution = frame.cols;
	double yResolution = frame.rows;
	

	Mat green;
	Mat bgr[3];
	//	Scalar LOWER = {65, 60, 40};
	//	Scalar UPPER = {75, 255, 255};
	split(frame, bgr);
	//green = bgr[1];
	vector<vector<Point> > contours, allContours;
	vector<Vec4i> hierarchy;	
	
	
	//GaussianBlur(green, green, Size(3, 3), 0, 0, 0);
	threshold(bgr[1], green, 255, 255, THRESH_BINARY_INV);	
	
	
	//cvtColor(frame, thres, COLOR_BGR2HSV);
	//inRange(thres, Scalar(50, 100, 65), Scalar(90, 255, 255), thres);
		
	morphologyEx(green, green, MORPH_OPEN, Mat(3, 3, CV_8UC1, Scalar(10)), Point(-1, -1), 1);
	/*
	morphologyEx(thres, thres, MORPH_OPEN, Mat(3, 3, CV_8UC1, Scalar(10)), Point(-1, -1), 1);
	*/
	
	findContours(green, allContours, hierarchy, RETR_LIST, CHAIN_APPROX_SIMPLE, Point(0, 0));	
	
	contours.clear();
	for(auto contour : allContours){
		if(contourArea(contour) < 0){
			contours.push_back(contour);	
		} 
	}
	
	sort(contours.begin(), contours.end(), sortByArea);

	if (contours.size() > 0) {
		vector<vector<Point> > hulls(1);
		
		Moments mu = moments(contours[0], false);
		int cX = mu.m10 / mu.m00;
		int cY = mu.m01 / mu.m00;
		
		int midX = cX;
		int midY = cY;
		// Maximum of three correct contours
		/*
		if(contours.size() > 1){
			Moments mus = moments(contours[1], false);
			Moments mut = moments(contours[2], false);
			
			int scX = mus.m10 / mus.m00;
			int scY = mus.m01 / mus.m00;

			int tcX = mut.m10 / mut.m00;
			int tcY = mut.m01 / mut.m00;

			if(contours.size() > 2){
				// combine em
			
				if(pow(cY - tcY, 2) > pow(scY - tcY, 2)){
					contours[1].insert(contours[1].end(), contours[2].begin(), contours[2].end());
					
					Moments mus = moments(contours[1], false);					
					int scX = mus.m10 / mus.m00;
					int scY = mus.m01 / mus.m00;
				} else {
					contours[0].insert(contours[0].end(), contours[2].begin(), contours[2].end());
						
					Moments mu = moments(contours[0], false);
					int cX = mu.m10 / mu.m00;
					int cY = mu.m01 / mu.m00;
		
				}				
				
				double secondEps = 0.05 * arcLength(contours[1], true);
				approxPolyDP(contours[1], hulls[1], secondEps, true);
				
				int midX = cX + (scX-cX)/2;
				int midY = cY + (scY-cY)/2;

			}
			*/
				
			/*
			
			
			
			//drawContours(frame, hulls, 1, Scalar(0, 0, 255), 2);		

	double firstEps = 0.0001 * arcLength(contours[0], true);
	approxPolyDP(contours[0], hulls[0], firstEps, true);	
	drawContours(frame, hulls, 0, Scalar(0, 0, 0), 2);
	*/
		//}
	
	
	/*
	NetworkTable::SetClientMode();
	NetworkTable::SetTeam(3476);
	shared_ptr<NetworkTable> table;
	table = NetworkTable::GetTable("");
	table->PutNumber("angle", ((midX - xResolution/2)/xResolution) * xCameraFOV);
	*/
	//cout << ((midX - xResolution/2)/xResolution) * xCameraFOV  << endl;
		
	}
}

int main(int argc, char *argv[]){
	Mat frame, orig;
	VideoCapture cam;
	//cam.open(1);
	//cam.read(frame);
	//cam.open("aot.mp4");
	frame = imread("b.jpg", CV_LOAD_IMAGE_COLOR);
	 
	while(1){
		//frame = imread("http://10.84.76.20:8080/stream.mjpg", CV_LOAD_IMAGE_COLOR
		auto begin = chrono::high_resolution_clock::now(); ;
		//cam.read(frame);
		processImage(frame);
		
		//imshow("a", frame);
		auto end = chrono::high_resolution_clock::now();    
		auto dur = end - begin;
		auto ms = std::chrono::duration_cast<std::chrono::milliseconds>(dur).count();
		cout << ms << endl;
	}
}



