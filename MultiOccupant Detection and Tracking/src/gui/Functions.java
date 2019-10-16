package gui;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.videoio.VideoCapture;

import Algo.CONFIG;

/**
 * Functions.java
 * TODO: 
 *
 * @author Asif
 * Email:asif@khu.ac.kr
 */

public class Functions {
	 private static final long MEGABYTE = 1024L * 1024L;

	public static void processFrame 
	( Mat mRgba, Mat mFGMask, BackgroundSubtractorMOG2 mBGSub) //VideoCapture capture, 

		{

		mBGSub.apply(mRgba, mFGMask, CONFIG.learningRate);
		Imgproc.cvtColor(mFGMask, mRgba, Imgproc. COLOR_GRAY2BGRA, 0); //COLOR_GRAY2BGRA

		Mat erode = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8, 8));  //new Size(8, 8));
		Mat dilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(8, 8)); // new Size(8, 8));

		Mat openElem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(3, 3), new Point(1, 1));
		Mat closeElem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(7, 7), new Point(3, 3));

		Imgproc.threshold(mFGMask, mFGMask, 127, 255, Imgproc.THRESH_BINARY);
		Imgproc.morphologyEx(mFGMask, mFGMask, Imgproc.MORPH_OPEN, erode);
		Imgproc.morphologyEx(mFGMask, mFGMask, Imgproc.MORPH_OPEN, dilate);
		Imgproc.morphologyEx(mFGMask, mFGMask, Imgproc.MORPH_OPEN, openElem);
		Imgproc.morphologyEx(mFGMask, mFGMask, Imgproc.MORPH_CLOSE, closeElem);
	}

	public static BufferedImage Mat2bufferedImage(Mat image) {
		MatOfByte bytemat = new MatOfByte();
		Imgcodecs.imencode(".jpg", image, bytemat);
		byte[] bytes = bytemat.toArray();
		InputStream in = new ByteArrayInputStream(bytes);
		BufferedImage img = null;
		try {
			img = ImageIO.read(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return img;
	}

	public static Vector<Rect> detectionContours(Mat outmat) {
		Mat v = new Mat();
		Mat vv = outmat.clone();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(vv, contours, v, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		int maxAreaIdx = -1;
		Rect r = null;
		Vector<Rect> rect_array = new Vector<Rect>();

		for (int idx = 0; idx < contours.size(); idx++) {
			Mat contour = contours.get(idx);
			double contourarea = Imgproc.contourArea(contour);

			if (contourarea > CONFIG.MIN_BLOB_AREA && contourarea < CONFIG.MAX_BLOB_AREA) {

				maxAreaIdx = idx;
				r = Imgproc.boundingRect(contours.get(maxAreaIdx));
				rect_array.add(r);
			}

		}
		v.release();
		return rect_array;
	}


	public static Vector<Rect> ContoursFeatures(Mat outmat) {
		Mat v = new Mat();
		Mat vv = outmat.clone();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

		Imgproc.findContours(vv, contours, v, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		
		for (MatOfPoint cnt : contours) {
		    MatOfPoint2f newPoint = new MatOfPoint2f(cnt.toArray());
		    double perimeter = Imgproc.arcLength(newPoint, true);
		    System.out.println("The Perimeter: " + String.valueOf(perimeter));
		}   
		    
		
		int maxAreaIdx = -1;
		Rect r = null;
		Vector<Rect> rect_array = new Vector<Rect>();
 
	
		for (int idx = 0; idx < contours.size(); idx++) {
			double sum_contourarea=0;
			
			Mat contour = contours.get(idx);
			double contourarea = Imgproc.contourArea(contour);
			
			sum_contourarea +=contourarea;
			
			System.out.println("Contour ID: "+idx +" & Contour size: "+contours.size() + 
					" & Contour Area: "+ contourarea);   // Asif Razzaq     // + " Total Area: "+sum_contourarea
			
			
			
			if (contourarea > CONFIG.MIN_BLOB_AREA && contourarea < CONFIG.MAX_BLOB_AREA) {

				maxAreaIdx = idx;
				r = Imgproc.boundingRect(contours.get(maxAreaIdx));
				rect_array.add(r);
			}
	
		}
		System.out.println("BoundingRec rect_array " + rect_array);
		v.release();
		return rect_array;
	}
	
	
	   public static JLabel getJLabel(Mat img) throws IOException {
		    Imgproc.resize(img, img, new Size(320, 240));   //(640, 480) changed by ASIF
		    MatOfByte matOfByte = new MatOfByte();
		    Imgcodecs.imencode(".bmp", img, matOfByte);
		    byte[] byteArray = matOfByte.toArray();
		    BufferedImage bufImage = null;

	        InputStream in = new ByteArrayInputStream(byteArray);
	        bufImage = ImageIO.read(in);
	        return new JLabel(new ImageIcon(bufImage));
		}   


	   static public ArrayList<MatOfPoint> findContours(Mat mat, int areaMin, boolean circle) {
			ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			Mat hierarchy=new Mat();
			Imgproc.findContours ( mat, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE );
			ArrayList<MatOfPoint> ret = new ArrayList<MatOfPoint>();
			for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
				if(circle) {
					Circle bounding=getCircleBox(contours.get(contourIdx));
				    if(bounding.area()>areaMin) 
				    	ret.add(contours.get(contourIdx));
				}else{
				    Rect bounding=getBoundingBox(contours.get(contourIdx));
				    if(bounding.area()>areaMin) 
				    	ret.add(contours.get(contourIdx));
				}
			}
			return ret;		
		}

		static public Circle getCircleBox(MatOfPoint contour) {
			float[] radius = new float[1];
			Point center = new Point();
			Imgproc.minEnclosingCircle(new MatOfPoint2f( contour.toArray()), center, radius);
			return new Circle(center, radius[0]);
		}
		static public Point getCenter(MatOfPoint contour) {
			float[] radius = new float[1];
			Point center = new Point();
			Imgproc.minEnclosingCircle(new MatOfPoint2f( contour.toArray()), center, radius);
			return center;
		}
		static public Rect getBoundingBox(MatOfPoint contour) {
			MatOfPoint2f         approxCurve = new MatOfPoint2f();
	        MatOfPoint2f contour2f = new MatOfPoint2f( contour.toArray() );
	        double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
	        Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

	        MatOfPoint points = new MatOfPoint( approxCurve.toArray() );

	        // Get bounding rect of contour
	        return Imgproc.boundingRect(points);
		}
		
		static public class Circle{
			public Point center;
			public float radius;
			public Circle(Point center, float radius) {
				this.center=new Point(center.x,center.y);
				this.radius=radius;
			}
			public float area() {
				return (float)(Math.PI*2f*this.radius);
			}
			public boolean isInside(Point p) {
				return Math.sqrt((p.x-center.x)*(p.x-center.x)+(p.y-center.y)*(p.y-center.y))<=radius;
			}
		}
		static public class Rectangle{
			public Point p0;
			public Point pN;
			public Rectangle(Point p0, Point pN) {
				this.p0=p0;
				this.pN=pN;
			}
			public boolean isInside(Point p) {
				return p0.x<=p.x && p.x<=pN.x &&
						p0.y<=p.y && p.y<=pN.y;
			}
		}
		
		
		static public boolean isInside(Point p, List<Rectangle> entrances) {
			for(Rectangle entrance:entrances)
				if(entrance.isInside(p)) return true;
			return false;
		}

	   
	   
	   

		 
	static public Mat watershed(Mat img)            //Assertion failed 
		 
		 
		 {

	        Mat markers = new Mat(img.size(),CvType.CV_8UC1, new Scalar(0));
	        
	         Mat result1=new Mat();
	         
	        WatershedSegmenter segmenter = new WatershedSegmenter();
	        segmenter.setMarkers(markers);
	         result1 = segmenter.process(img);
	        return result1;
		 }
		 
		 
static		 public class WatershedSegmenter
		     {
			    public Mat markers=new Mat();

			    public void setMarkers(Mat markerImage)
			    {
			    	
			        markerImage.convertTo(markers, CvType.CV_8UC1);
			    }

			    public Mat process(Mat image)
			    {
			        Imgproc.watershed(image,markers);
			        markers.convertTo(markers,CvType.CV_8UC1);
			        return markers;
			    }
			}

static public Point toInt(Point P)
{

	int pt_x = (int) P.x ;
	int pt_y = (int) P.y;
	return new Point(pt_x, pt_y );
}

static public Point roundOffDouble(Point P)
{

	DecimalFormat df = new DecimalFormat("#.###");
	double ptx= Double.parseDouble(df.format(P.x));
	double pty= Double.parseDouble(df.format(P.y));
	return new Point(ptx,pty);

}


public static long bytesToMegabytes(long bytes) {
    return bytes / MEGABYTE;
}
}
