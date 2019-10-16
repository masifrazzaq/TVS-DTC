package test;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.video.KalmanFilter;

/**
 * Kalman.java
 * TODO: 
 *
 * @author asif Email:asif@khu.ac.kr */

public class Kalman {
	public static void main(String[] args){
		double[][] samplesArr = {{0,0},{1,0},{1,1},{2,1},{2,2}};

		Mat meas = new Mat(2,1,CvType.CV_32F);

		KalmanFilter kf = new KalmanFilter(4, 2);

		Mat mm = Mat.eye(2,4,CvType.CV_32F);
		kf.set_measurementMatrix(mm);  

		for(int i = 0; i < samplesArr.length; i++)
		{
		    meas.put(0, 0, samplesArr[i]);
		    Mat pre = kf.predict();
		    System.out.println(pre.t().dump());
		}
	}

}
