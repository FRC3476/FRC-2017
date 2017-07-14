#include <stdio.h>
#include <opencv2/opencv.hpp>
#include <thread>
#include <vector>
#include <list>
#include <chrono>
#include "Udp.h"

using namespace cv;
using namespace std;

const double yCameraFOV = 38; //USB:38 ZED:45 Kinect:43
const double xCameraFOV = 60; //USB:60 ZED:58 Kinect:57 USB:52 @720

void makeGearServer()
{
	system("/home/ubuntu/Documents/Vision/mjpg_streamer -i \"input_file.so -f /home/ubuntu/Documents/Vision/gear\" -o \"output_http.so -w ./www -p 1183\"");
	
}

void makeBoilerServer()
{
	system("/home/ubuntu/Documents/Vision/mjpg_streamer -i \"input_file.so -f /home/ubuntu/Documents/Vision/boiler\" -o \"output_http.so -w ./www -p 1184\"");
	
}

bool sortByArea(const vector<Point> &lhs, const vector<Point> &rhs) {
	return (contourArea(lhs) < contourArea(rhs));
}

bool SortByX(const cv::Point& a, const cv::Point& b) {
    return a.x < b.x;
}

bool sortByY(const Point &lhs, const Point &rhs) {
	return lhs.y > rhs.y;
}

bool IsLessThanTwo(const std::vector<cv::Point>>& group) {
	return group.size() < 2;
}

void processGear(Mat &frame){
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
	
	} else {		
	
	}
}

void processBoiler(Mat &frame){	
	double xResolution = frame.cols;
	double yResolution = frame.rows;
	Mat thres;
	vector<vector<Point> > contours;
	vector<Vec4i> hierarchy;	
	
	cvtColor(frame, thres, COLOR_BGR2HSV);
	inRange(thres, Scalar(40, 90, 100), Scalar(80, 255, 2), thres);
	findContours(thres, contours, hierarchy, RETR_LIST, CHAIN_APPROX_SIMPLE, Point(0, 0));
	std::vector<cv::Point> centers;
	for(auto contour : contours) {
		cv::Rect target = cv::boundingRect(contour);
		int x = target.x + (target.width / 2);
		int y = target.y + (target.heigth / 2);
		centers.push_back(cv::Point(x, y));
	}
	std::sort(centers.begin(), centers.end(), SortByX);
    std::list<std::vector<cv::Point>> sortedX;
    for (auto it = std::begin(centers); it != std::end(centers); ) {
        std::vector<cv::Point> currentX;
        currentX.push_back(*it);
        ++it;
        while (std::abs(it->x - std::prev(it)->x) < 50 && it != centers.end()) {
            currentX.push_back(*it);
            ++it;            
        }        
        sortedX.push_back(currentX);
    }
	sortedX.remove_if(IsLessThanTwo);
	
}


void gearVision(){
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
		
		VideoWriter writer ("/home/ubuntu/Documents/Vision/gear/out.mjpg", CV_FOURCC('M','J','P','G'), 2, Size (gearCam.get(3), gearCam.get(4)), -1);
		if (!writer.isOpened()){
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

void boilerVision(){
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
		
		VideoWriter writer ("/home/ubuntu/Documents/Vision/boiler/out.mjpg", CV_FOURCC('M','J','P','G'), 2, Size (boilerCam.get(3), boilerCam.get(4)), -1);
		if (!writer.isOpened()){
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


int main(int argc, char** argv ) {
	UDPClient sender("10.34.76.2", 5800);
	std::string message("hello");
	sender.Send(message, sizeof(string));

	thread gearServer(makeGearServer);
	thread boilerServer(makeBoilerServer);
	thread gear(gearVision);
	thread boiler(boilerVision);
	gearServer.join();
	boilerServer.join();

	return 0;
}
