package Support;

import java.awt.Toolkit;

import org.opencv.core.Scalar;

/**
 * CONFIG.java
 * TODO: 
 *
 * @Asif Email:asif@khu.ac.kr
 */

public class CONFIG {
	public static String filename = "E:/EclipseWorkSpace";  
	public static int FRAME_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width / 2;
	public static int FRAME_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height / 2;
	
	///////////// FOR ALL ACTIVITIES 
	public static double MIN_BLOB_AREA =  3300; 
	public static double MAX_BLOB_AREA = 20000; 

	
	
	public static Scalar Colors[] = { new Scalar(255, 0, 0), new Scalar(0, 255, 0),
		new Scalar(0, 0, 255), new Scalar(255, 255, 0),
		new Scalar(0, 255, 255), new Scalar(255, 0, 255),
		new Scalar(255, 127, 255), new Scalar(127, 0, 255),
		new Scalar(127, 0, 127) };
	
	public static double learningRate = 0.005; //0.005;
	
	public static double _dt = 0.2;   // 0.2;
	public static double _Accel_noise_mag = 0.5;     //0.5;
	public static double _dist_thres = 150;    
	public static int _maximum_allowed_skipped_frames = 10;    //= 10
	public static int _max_trace_length = 10;         //10;
}
