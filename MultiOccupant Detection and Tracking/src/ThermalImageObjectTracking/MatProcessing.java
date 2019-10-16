package ThermalImageObjectTracking;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
/**
 * @author Javier Medina & Asif
 * Operations and utils method for processing mats
 */

public class MatProcessing {
	
	public static double MIN_BLOB_AREA = 250;
	public static double MAX_BLOB_AREA = 3000;
	
	
	static public int getAvg(Mat mat) {
		int v=0;
		for (int i = 0; i < mat.size().height; i++)
		    for (int j = 0; j < mat.size().width; j++) {
		        v+=getV(mat, i, j);
		    }
		return (v/(int)(mat.size().height*mat.size().width));
	}
	
	static public int getV(Mat mat, int row, int col) {
		return (int)mat.get(row, col)[0];
	}
	static public int setV(Mat mat, int row, int col, int data) {

		return mat.put(row, col, data);
	}
	
	static public void print(Mat mat) {
		for (int i = 0; i < mat.size().height; i++)
		    for (int j = 0; j < mat.size().width; j++) {
		        System.out.print(getV(mat, i, j)+"\t");
		    }
		System.out.println();
	}	
	
	static public void lowThreshold(Mat mat, int alpha) {

		for (int i = 0; i < mat.size().height; i++)
		    for (int j = 0; j < mat.size().width; j++) {
		        if(getV(mat, i, j)<alpha)
		        	setV(mat, i, j,255); 
		        else
		        	setV(mat, i, j,0); 
		        
		    }
		for (int j = 0; j < mat.size().width; j++) {
				setV(mat, 31, j,0);
				setV(mat, 30, j,0);
		}
	}	
	
	

	static public void write(String folderMat, Mat mat) {
		Imgcodecs.imwrite(folderMat+System.currentTimeMillis()+".png", mat);
	}

	static public List<Mat> getMatsFromFolder(String sFolder){
		List<Mat> ret=new LinkedList<Mat>();
		File folder = new File(sFolder);
		int img=1;
	    for (final File fileEntry : folder.listFiles()) {
		        if (!fileEntry.isDirectory()) { 
		            System.out.println("ImageFrame "+img+" "+fileEntry.getName());     
		            if(fileEntry.getName().endsWith(".png"))
		            	ret.add(Imgcodecs.imread(sFolder+fileEntry.getName(), CvType.CV_8UC1));
		        }
		        img++;
		    }
	    return ret;
		}
	
	static public Mat getMatFromFolder(long tn, String sFolder) {
		return Imgcodecs.imread(sFolder+tn+".png", CvType.CV_8UC1);
	}

	static public List<Long> getStampsFromFolder(String sFolder){
		List<Long> ret=new LinkedList<Long>();
		File folder = new File(sFolder);
	    for (final File fileEntry : folder.listFiles()) {
		        if (!fileEntry.isDirectory()) {
		      //      System.out.println(fileEntry.getName());
		            if(fileEntry.getName().endsWith(".png"))
		            	ret.add(Long.parseLong(fileEntry.getName().replaceAll(".png", "")));
		        }
		    }
	    return ret;
	}

	static public ArrayList<MatOfPoint> findContours(Mat mat, int areaMin) {
		return findContours(mat, areaMin,true);
		
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
	
	static public Mat smooth(Mat mat, int s) {
		Imgproc.GaussianBlur(mat, mat, new Size(3,3),0);
		return mat;
	}

	static public Mat paintLine(Mat source, Point pt1, Point pt2) {
	    Imgproc.line(source, pt1, pt2, new Scalar(0,0,255),8);
	    return source;
	}
	static public Mat paintContours(Mat source, ArrayList<MatOfPoint> contours) {
		return paintContours(source,contours,true);
	}
	static public Mat paintContours(Mat source, ArrayList<MatOfPoint> contours, boolean circle) {

		for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++)
		{
			Imgproc.drawContours(source, contours, contourIdx, new Scalar(200,200,200), -1); //-1


			if(circle) {
			    Circle boundigCircle=getCircleBox(contours.get(contourIdx));
			    Imgproc.circle(source, 
			        		new Point(boundigCircle.center.x,boundigCircle.center.y),
			        		(int)boundigCircle.radius, new Scalar(255,0,0),
			        		1);
			}else {
			    Rect boundigBox=getBoundingBox(contours.get(contourIdx));

			    Imgproc.rectangle(source, 
		        		new Point(boundigBox.x,boundigBox.y),
		        		new Point(boundigBox.x+boundigBox.width,boundigBox.y+boundigBox.height), new Scalar(0,255,0),
		        		1); 
				
			}

}
		return source;
	}
	
	static public Mat paintTrack(Mat mat, TrackHistory track) {
	 return paintTrack(mat,track, new Scalar(0,255,255));
	}
	static public Mat paintTrack(Mat mat, TrackHistory track, Scalar color) {
		Point last=null;
		for (Long k:track.getKeys()) {
			Point point = track.get(k);
			Imgproc.circle(mat, point, 2, color, 5);
			if(last!=null)
				Imgproc.line(mat, last, point,color , 3);
			last=point;
		}

		return mat;
	}

	static public Mat paintText(Mat mat, String text, int h) {
		Imgproc.putText(mat, text, new Point(0,h),
                Core.FONT_HERSHEY_PLAIN, 2.0 ,new  Scalar(255,255,255));
		return mat;
	}
	
	static public Mat paintEntrance(Mat source, Rectangle r, boolean entrance) {
	    Imgproc.rectangle(source, 
        		r.p0,r.pN,
        		entrance?new Scalar(0,0,255):new Scalar(255,0,0),
        		entrance?-1:1); 
		    return source;
}	
	
	static public MatOfPoint getMaxArea(ArrayList<MatOfPoint> contours) {
		MatOfPoint ret=null;
		float maxArea=Float.MIN_VALUE;
		for(MatOfPoint contour:contours) {
		    Circle boundigCircle=getCircleBox(contour);
		    if(boundigCircle.area()>maxArea) {
		    	ret=contour;
		    }

		 return ret;			
		}
		return ret;
	}
	/////////////////////////////////asif 
	
	 static public ArrayList<MatOfPoint> allContours(MatOfPoint mp){
		 ArrayList<MatOfPoint> ret= new ArrayList<MatOfPoint>();
		return ret;
	 }
	 
	 static public ArrayList<MatOfPoint> one2Many(MatOfPoint mp){
		 ArrayList<MatOfPoint> ret= new ArrayList<MatOfPoint>();
		 if(mp!=null)
			 ret.add(mp); //  
		 return ret;
	 }

		
		static public Point scale(Point p, int scale) {
		 Point ret=p.clone();
		 ret.x=ret.x*scale;
		 ret.y=ret.y*scale;
		 return ret;
	 }
	 static public Rectangle scale(Rectangle r, int scale) {
		 return new Rectangle(scale(r.p0,scale),scale(r.pN,scale));
	 }
	 
	 static public MatOfPoint scale(MatOfPoint mp, int scale) {
		 if(mp==null) return null;
		 
		 Point[] ps=mp.toArray();
		 
		 for(Point p:ps) {
			 p.x=p.x*scale;
			 p.y=p.y*scale;
		 }
		 
		 mp.fromArray(ps);
		 return mp;
	 }
	 
		public static TrackHistory scale(TrackHistory source, int scale) {
			TrackHistory ret=new TrackHistory();
			for (Long k:source.getKeys()) 
				ret.put(k,MatProcessing.scale(source.get(k), scale));
			return ret;
		}	 
	//	
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
	
	 public static void zhangSuenThinning(Mat img) { 
	  Mat prev = Mat.zeros(img.size(), CvType.CV_8UC1); 
	  Mat diff = new Mat(); 
	  do { 
	   zhangSuenThinningIteration(img, 0); 
	   zhangSuenThinningIteration(img, 1); 
	   Core.absdiff(img, prev, diff); 
	         img.copyTo(prev); 
	     } 
	     while (Core.countNonZero(diff) > 0); 
	 } 
	 
	 private static void zhangSuenThinningIteration(Mat img, int step) { 
	     byte[] buffer = new byte[(int) img.total() * img.channels()]; 
	     img.get(0, 0, buffer); 
	      
	     byte[] markerBuffer = new byte[buffer.length]; 
	      
	  int rows = img.rows(); 
	  int cols = img.cols(); 
	   
	     for (int y = 1; y < rows-1; ++y) { 
	   for (int x = 1; x < cols-1; ++x) { 
	    int prev = cols*(y-1) + x; 
	    int cur  = cols*y     + x; 
	    int next = cols*(y+1) + x; 
	     
	    byte p2 = buffer[prev]; 
	    byte p3 = buffer[prev + 1]; 
	    byte p4 = buffer[cur  + 1]; 
	    byte p5 = buffer[next + 1]; 
	    byte p6 = buffer[next]; 
	    byte p7 = buffer[next - 1]; 
	    byte p8 = buffer[cur  - 1]; 
	    byte p9 = buffer[prev - 1]; 
	     
	    int a = 0; 
	    if (p2 == 0 && p3 == -1) { 
	     ++a; 
	    } 
	    if (p3 == 0 && p4 == -1) { 
	     ++a; 
	    } 
	    if (p4 == 0 && p5 == -1) { 
	     ++a; 
	    } 
	    if (p5 == 0 && p6 == -1) { 
	     ++a; 
	    } 
	    if (p6 == 0 && p7 == -1) { 
	     ++a; 
	    } 
	    if (p7 == 0 && p8 == -1) { 
	     ++a; 
	    } 
	    if (p8 == 0 && p9 == -1) { 
	     ++a; 
	    } 
	    if (p9 == 0 && p2 == -1) { 
	     ++a; 
	    } 
	     

	    int b  = Math.abs(p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9); 
	     
	    int c3 = step == 0 ? (p2 * p4 * p6) : (p2 * p4 * p8); 
	    int c4 = step == 0 ? (p4 * p6 * p8) : (p2 * p6 * p8); 
	     
	    markerBuffer[cur] = (byte) ((a == 1 && b >= 2 && b <= 6 && c3 == 0 && c4 == 0) ? 0 : -1); 
	   } 
	  } 
	      
	     for (int i = 0; i < buffer.length; ++i) { 
	      buffer[i] = (byte) ((buffer[i] == -1 && markerBuffer[i] == -1) ? -1 : 0); 
	     } 
	     img.put(0, 0, buffer); 
	 } 	
}
