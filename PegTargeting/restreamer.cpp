#include <stdio.h>
#include <opencv2/opencv.hpp>
#include <thread>
#include <vector>
#include <chrono>
#include <stdlib.h>
#include <ntcore.h>
#include <networktables/NetworkTable.h>


using namespace cv;
using namespace std;

shared_ptr<NetworkTable> table;
void processGear(Mat &frame);
vector<double> runningAverage(5);

void makeGearServer()
{
	system("/home/ubuntu/Documents/PegTargeting/mjpg_streamer -i \"input_file.so -f /home/ubuntu/Documents/PegTargeting/gear\" -o \"output_http.so -w ./www -p 1181\"");
	
}

void makeBoilerServer()
{
	system("/home/ubuntu/Documents/PegTargeting/mjpg_streamer -i \"input_file.so -f /home/ubuntu/Documents/PegTargeting/boiler\" -o \"output_http.so -w ./www -p 1182\"");
	
}

bool sortByArea(const vector<Point> &lhs, const vector<Point> &rhs) {
	return (contourArea(lhs) < contourArea(rhs));
}

bool sortByY(const Point &lhs, const Point &rhs) {
	return lhs.y > rhs.y;
}


void processGear(Mat &frame){
	double yCameraFOV = 38; //USB:38 ZED:45 Kinect:43
	double xCameraFOV = 60; //USB:60 ZED:58 Kinect:57 USB:52 @720
	
	double xResolution = frame.cols;
	double yResolution = frame.rows;
	Mat thres;
	vector<vector<Point> > contours, allContours;
	vector<Vec4i> hierarchy;	
	
	cvtColor(frame, thres, COLOR_BGR2HSV);
	inRange(thres, Scalar(45, 100, 65), Scalar(85, 255, 255), thres);

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
		double secondEps = 0.000001 * arcLength(contours[1], true);
		approxPolyDP(contours[1], hulls[1], secondEps, true);
		drawContours(frame, hulls, 1, Scalar(255, 255, 255), 2);
		
		contours[0].insert(contours[0].end(), contours[1].begin(), contours[1].end());
	
		Rect box = boundingRect(contours[0]);
		double distance = (634 * 5) / box.height;
   //f = d * p / h;
   // double f = 52 * box.height / 5;
	
		table->PutNumber("gearAngle", ((midX - xResolution/2)/xResolution) * xCameraFOV);
		table->PutNumber("gearDistance", distance);
		table->PutBoolean("isGearVisible", true);		
	} else {		
		table->PutBoolean("isGearVisible", false);
	}
}

void processBoiler(Mat &frame){
	double yCameraFOV = 38; //USB:38 ZED:45 Kinect:43
	double xCameraFOV = 60; //USB:60 ZED:58 Kinect:57 USB:52 @720
	
	double xResolution = frame.cols;
	double yResolution = frame.rows;
	Mat thres;
	vector<vector<Point> > contours, allContours;
	vector<Vec4i> hierarchy;	
	
	cvtColor(frame, thres, COLOR_BGR2HSV);
	inRange(thres, Scalar(45, 95, 60), Scalar(85, 255, 255), thres);
  //morphologyEx(thres, thres, MORPH_CLOSE, Mat(3, 3, CV_8UC1, Scalar(10)), Point(-1, -1), 1);
	


	//imshow("cvt", thres);	
	findContours(thres, allContours, hierarchy, RETR_LIST, CHAIN_APPROX_SIMPLE, Point(0, 0));	

	contours.clear();
	for(auto contour : allContours){
		if(contourArea(contour) > 100 && contourArea(contour) < 30000){
			contours.push_back(contour);	
		} 
	}
	
	sort(contours.begin(), contours.end(), sortByArea);

	if (contours.size() > 1) {
		vector<vector<Point> > hulls(2);
		
		Moments mu = moments(contours[0], false);
		int cX = mu.m10 / mu.m00;
		int cY = mu.m01 / mu.m00;
		// Maximum of three correct contours
		Moments mus = moments(contours[1], false);
		int tX = mus.m10 / mus.m00;
		int tY = mus.m01 / mus.m00;
	
		double firstEps = 0.001 * arcLength(contours[0], true);
		approxPolyDP(contours[0], hulls[0], firstEps, true);	
		drawContours(frame, hulls, 0, Scalar(255, 255, 255), 1, 1);
		
		double secondEps = 0.000001 * arcLength(contours[1], true);
		approxPolyDP(contours[1], hulls[1], secondEps, true);
		drawContours(frame, hulls, 1, Scalar(255, 255, 255), 2);
		
   
		contours[0].insert(contours[0].end(), contours[1].begin(), contours[1].end());	
	
		Rect box = boundingRect(contours[0]);
		double midX = box.x + box.width/2;
		//to get the height from the bottom
		double midY = yResolution - box.y;
   
   
		double distance = (539 * 7.0) / (cY - tY);
    
    runningAverage.erase(runningAverage.begin());
    runningAverage.push_back(distance);
    distance = 0;
    for(auto d : runningAverage){
      distance += d;
    }
    distance /= 5;
    
   //f = d * p / h;
    //double f = 98 * (cY - tY) / 7;
   
		table->PutNumber("boilerXAngle", ((midX - xResolution/2)/xResolution) * xCameraFOV);
		table->PutNumber("boilerYAngle", distance);
		table->PutBoolean("isBoilerVisible", true);
	} else {
		table->PutBoolean("isBoilerVisible", false);
	}
}


void gearVision()
{
	Mat gearFrame;
	VideoCapture gearCam;
	gearCam.open(0);
	if(!gearCam.isOpened()){
     cout << "gearcam not opened " << endl;
     return;
	}

	while(1){
	  system("v4l2-ctl -d 0 -c exposure_auto=1 -c exposure_absolute=5 -c brightness=30");
		
		table->PutBoolean("isJetsonOn", true);
		auto gearBegin = chrono::high_resolution_clock::now(); 
		//frame = imread("http://10.84.76.20:8080/stream.mjpg", CV_LOAD_IMAGE_COLOR
		gearCam.read(gearFrame);		
		processGear(gearFrame);
		
		VideoWriter writer ("/home/ubuntu/Documents/PegTargeting/gear/out.mjpg", CV_FOURCC('M','J','P','G'), 2, Size (gearCam.get(3), gearCam.get(4)), -1);
		if (!writer.isOpened())
		{
			cout << "failed to open stream writer" << endl;
			return;
		}
		writer.write(gearFrame);
		auto gearEnd = chrono::high_resolution_clock::now();    
		auto gearDur = gearEnd - gearBegin;
		auto gearMs = std::chrono::duration_cast<std::chrono::milliseconds>(gearDur).count();
		table->PutNumber("gearTimeAgo", gearMs);		
	}
} 

void boilerVision()
{
	Mat boilerFrame;
	VideoCapture boilerCam;
	boilerCam.open(1);

  if(!boilerCam.isOpened()){
     cout << "boiler not opened " << endl;
     return;
	}
	
	
	while(1){
  	system("v4l2-ctl -d 1 -c exposure_auto=1 -c exposure_absolute=5 -c brightness=30");
	
		auto boilerBegin = chrono::high_resolution_clock::now();
		boilerCam.read(boilerFrame);
		processBoiler(boilerFrame);
		
		VideoWriter writer ("/home/ubuntu/Documents/PegTargeting/boiler/out.mjpg", CV_FOURCC('M','J','P','G'), 2, Size (boilerCam.get(3), boilerCam.get(4)), -1);
		if (!writer.isOpened())
		{
			cout << "failed to open stream writer" << endl;
			return;
		}
		writer.write(boilerFrame);
		auto boilerEnd = chrono::high_resolution_clock::now();    
		auto boilerDur = boilerEnd - boilerBegin;
		auto boilerMs = std::chrono::duration_cast<std::chrono::milliseconds>(boilerDur).count();
		table->PutNumber("boilerTimeAgo", boilerMs);
		
	}
} 


int main(int argc, char** argv )
{

	NetworkTable::SetClientMode();
	NetworkTable::SetTeam(3476);
	table = NetworkTable::GetTable("");

	thread gearServer(makeGearServer);
	thread boilerServer(makeBoilerServer);
	thread gear(gearVision);
	thread boiler(boilerVision);
	gearServer.join();
	boilerServer.join();

	return 0;
}
