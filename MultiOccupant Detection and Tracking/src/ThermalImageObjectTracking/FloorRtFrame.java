package ThermalImageObjectTracking;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import ThermalImageObjectTracking.FloorRtFrame.JsonFloor.JsonCell;
import ThermalImageObjectTracking.SensorRtFrame.JsonFrame;

import com.google.gson.Gson;

/**
 * @author Javier Medina & Asif
 *
 */
public class FloorRtFrame {
	
	public String url;
	FloorRtFrame( String url){
		this.url=url;
	}
	
	public JsonFloor getFrame() throws Exception {
		ByteArrayOutputStream baos=Utils.getByteFromURL(url);
		
		String data=new String(baos.toByteArray());

		
		JsonFloor ret=new JsonFloor(Utils.gson.fromJson(data, JsonCell[].class));

		
		return ret;
	}

	static public Point [] getCirclePoints(float x0, float y0, float rX, float rY){
		Point [] ret=new Point [8];
		
		float incA=(float)(Math.PI/4.0f);
		float a=incA/2f;
		for(int i=0;i<8;i++) {
			ret[i]=new Point(
					rX*(float)Math.sin(a)+x0,
					rY*(float)Math.cos(a)+y0
					);
			a+=incA;
		}
		return ret;
	}

	
	static public class JsonFloor{
		static public int height=320;
		static public int width=320;
		
		static public float wH=(float)height/7f;
		static public float wW=(float)width/4f;
		
		JsonCell [] cells;
		
		public JsonFloor(JsonCell [] cells) {
			this.cells=cells;			
		}
		
		static public class JsonCell{
			int coord0;
			int coord1;
			String floorID;
			long timeStamp;
			int s0,s1,s2,s3,s4,s5,s6,s7;
			public JsonCell() {
				coord0=-1;
				coord1=-1;
				s0=0;
				s1=0;
				s2=0;
				s3=0;
				s4=0;
				s5=0;
				s6=0;
				s7=0;				
			}
			
			static public int getV(int v0) {
				return Math.abs(v0)*2;
			}
			
			
			public void makePositive() {
				s0=Math.abs(s0);
				s1=Math.abs(s1);
				s2=Math.abs(s2);
				s3=Math.abs(s3);
				s4=Math.abs(s4);
				s5=Math.abs(s5);
				s6=Math.abs(s6);
				s7=Math.abs(s7);
			}
			
			@Override
			public String toString() {
				return Utils.gson.toJson(this);
			}			
			
			public int iCenter() {
				return height-(int)((coord1-1)*wH+wH/2);
			}
			
			public int jCenter() {
				return (int)((coord0-1)*wW+wW/2);
			}
			
			public int max() {
				return 
						Math.max(s0, 
								Math.max(s1,
										Math.max(s2,
												Math.max(s3,
														Math.max(s4,
																Math.max(s5,
																		Math.max(s6,s7)
																		)
																)
														)
												)
										)
								);
			}
			
			public boolean outOfRange() {
				return coord0>4;
			}
			
		}
		

		
		@Override
		public String toString() {
			return Utils.gson.toJson(this);
		}
		
		public Mat toMat() {
			Mat mat = new Mat(height, width, CvType.CV_8UC1, new Scalar(0,0,0));
			for(JsonCell cell:cells) {
				System.out.println("("+cell.coord0+","+cell.coord1+") -> ("+cell.iCenter()+","+ cell.jCenter()+")");
				
				if(cell.outOfRange()) continue;
				
				Point [] ps=getCirclePoints(cell.iCenter(),cell.jCenter(),(float)height/14f,(float)width/8f);
			}
			return mat;
		}
	}

	
}
