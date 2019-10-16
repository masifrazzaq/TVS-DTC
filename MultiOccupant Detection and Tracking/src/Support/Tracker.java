package Support;

import gui.Functions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Tracker.java TODO:
 * 
 * @Asif Email:asif@khu.ac.kr
  //* Credits: https://github.com/son-oh-yeah
 */

public class Tracker extends JTracker {
	int nextTractID = 0;
	Vector<Integer> assignment = new Vector<>();

	public Tracker(float _dt, float _Accel_noise_mag, double _dist_thres, int _maximum_allowed_skipped_frames, int _max_trace_length) {
		tracks = new Vector<>();
		dt = _dt;
		Accel_noise_mag = _Accel_noise_mag;
		dist_thres = _dist_thres;
		maximum_allowed_skipped_frames = _maximum_allowed_skipped_frames;
		max_trace_length = _max_trace_length;
		track_removed = 0;
	}
	
	static Scalar Colors[] = { new Scalar(255, 0, 0), new Scalar(0, 255, 0),
		new Scalar(0, 0, 255), new Scalar(255, 255, 0),
		new Scalar(0, 255, 255), new Scalar(255, 0, 255),
		new Scalar(255, 127, 255), new Scalar(127, 0, 255),
		new Scalar(127, 0, 127) };

	double euclideanDist(Point p, Point q) {
		Point diff = new Point(p.x - q.x, p.y - q.y);
		return Math.sqrt(diff.x * diff.x + diff.y * diff.y);
	}

	

	
	public void update(Vector<Rect> rectArray, Vector<Point> detections, Mat imag) {			
		if (tracks.size() == 0) {
			// If no tracks yet
			for (int i = 0; i < detections.size(); i++) {
				Track tr = new Track(detections.get(i), dt, Accel_noise_mag, nextTractID++);		
				tracks.add(tr);
			}
		}

		// -----------------------------------
		// Number of tracks and detections
		// -----------------------------------
		int N = tracks.size();
		int M = detections.size();
		
		// Cost matrix.
		double[][] Cost = new double[N][M]; // size: N, M
		// Vector<Integer> assignment = new Vector<>(); // assignment according to Hungarian algorithm
		assignment.clear();
		// -----------------------------------
		// Caculate cost matrix (distances)
		// -----------------------------------
		for (int i = 0; i < tracks.size(); i++) {
			for (int j = 0; j < detections.size(); j++) {
				Cost[i][j] = euclideanDist(tracks.get(i).prediction, detections.get(j)); //https://www.mathworks.com/help/vision/ref/assigndetectionstotracks.html
			}
		}


		AssignmentOptimal APS = new AssignmentOptimal();
		APS.Solve(Cost, assignment);
		Vector<Integer> not_assigned_tracks = new Vector<>();

		for (int i = 0; i < assignment.size(); i++) {
			if (assignment.get(i) != -1) {
				if (Cost[i][assignment.get(i)] > dist_thres) {     //150
					assignment.set(i, -1);
					not_assigned_tracks.add(i);
				}
			} else {
				tracks.get(i).skipped_frames++;
			}
		}
		
		
		for (int i = 0; i < tracks.size(); i++) {
			if (tracks.get(i).skipped_frames > maximum_allowed_skipped_frames) {				//10
				tracks.remove(i);
				assignment.remove(i);
				track_removed++;
				i--;
			}
		}
		
		// -----------------------------------
		// Search for unassigned detects
		// -----------------------------------
		Vector<Integer> not_assigned_detections = new Vector<>();
		for (int i = 0; i < detections.size(); i++) {
			if (!assignment.contains(i)) {
				not_assigned_detections.add(i);
			}
		}

		// -----------------------------------
		// and start new tracks for them.
		// -----------------------------------
		if (not_assigned_detections.size() > 0) {
			for (int i = 0; i < not_assigned_detections.size(); i++) {
				Track tr = new Track(detections.get(not_assigned_detections.get(i)), dt, Accel_noise_mag, nextTractID++);
				tracks.add(tr);
			}
		}
		
		// Update Kalman Filters state
		updateKalman(imag, detections);
				
		for (int j = 0; j < assignment.size(); j++) {  //j=0 to 1 Asif
			if (assignment.get(j) != -1) {
				Point pt1 = new Point((int) ((rectArray.get(assignment.get(j)).tl().x + rectArray.get(assignment.get(j)).br().x) / 2), rectArray.get(assignment.get(j)).br().y);
				Point pt2 = new Point((int) ((rectArray.get(assignment.get(j)).tl().x + rectArray.get(assignment.get(j)).br().x) / 2), rectArray.get(assignment.get(j)).tl().y);
				
						

				int Tr_ID = (Integer) tracks.get(j).track_id;       //Tr_ID = tracks.get(j).track_id only to give one increment
				Tr_ID++;
				Imgproc.putText(imag, Tr_ID + "", pt2,  Core.FONT_HERSHEY_PLAIN, 1, new Scalar(255, 255, 255), 1);  //2 * Core.FONT_HERSHEY_PLAIN          

				
				if (tracks.get(j).history.size() < 200)
					tracks.get(j).history.add(pt1);
				else {
					tracks.get(j).history.remove(0);
					tracks.get(j).history.add(pt1);
				}
			}
		}
	}
	
	
	

	
	
	public void updateKalman(Mat imag, Vector<Point> detections) {
		
		

		if(detections.size()==0)
			for(int i = 0; i < assignment.size(); i++)
				assignment.set(i, -1);
		for (int i = 0; i < assignment.size(); i++) {
			tracks.get(i).prediction=tracks.get(i).KF.getPrediction();

			if (assignment.get(i) != -1) // If we have assigned detect, then
											// update using its coordinates,
			{
				tracks.get(i).skipped_frames = 0;
				tracks.get(i).prediction = tracks.get(i).KF.update(
						detections.get(assignment.get(i)), true);
			} else // if not continue using predictions
			{
				tracks.get(i).prediction = tracks.get(i).KF.update(new Point(0,0), false);
			}

			if (tracks.get(i).trace.size() > max_trace_length) {
				for (int j = 0; j < tracks.get(i).trace.size()- max_trace_length; j++)
					tracks.get(i).trace.remove(j);
			}

			tracks.get(i).trace.add(tracks.get(i).prediction);
			tracks.get(i).KF.setLastResult(tracks.get(i).prediction);

	
			int Tr_ID = (Integer) tracks.get(i).track_id;     
			Tr_ID++;
			
			
			
			Point ptRndOff= Functions.roundOffDouble(tracks.get(i).prediction);     


			Imgproc.putText(imag, "Occ: "+ Tr_ID +" " + ptRndOff, tracks.get(i).prediction, Core.FONT_HERSHEY_PLAIN, .8, new Scalar(255, 255, 255), 1);

						
		}
	}
}
