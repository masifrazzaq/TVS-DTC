package Support;

import java.util.Vector;

import org.opencv.core.Point;

/**
 * Track.java TODO:
 * 
 * @Asif Email:asif@khu.ac.kr
   */

public class Track {

	public Vector<Point> trace;
	public Vector<Point> history;
	public static int NextTrackID;
	public int track_id;
	public int skipped_frames;
	public int crossBorder;
	public Point prediction;
	public Kalman KF;

	public Track(Point pt, float dt, float Accel_noise_mag, int id) {
		trace = new Vector<>();
		history = new Vector<>();
		track_id=id;
		KF = new Kalman(pt,dt,Accel_noise_mag);

		prediction = pt;
		skipped_frames = 0;
		crossBorder = 0;
	}	
}
