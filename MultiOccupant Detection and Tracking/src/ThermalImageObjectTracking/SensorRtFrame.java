package ThermalImageObjectTracking;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import com.google.gson.Gson;
/**
 * @author Javier Medina
 * Json class adapter
 */

public class SensorRtFrame {
	
	public String url;
	public SensorRtFrame( String url){
		this.url=url;
	}
	
	public JsonFrame getFrame() throws Exception {
		ByteArrayOutputStream baos=Utils.getByteFromURL(url);
		
		String data=new String(baos.toByteArray());
		//System.out.println("data:"+data);
		
		return Utils.gson.fromJson(data, JsonFrame[].class)[0];
		
		
	}
	
	static public class JsonFrame{
		int [] frameData;
		int height;
		int width;
		String sensorID;
		public JsonFrame() {}
		
		@Override
		public String toString() {
			return Utils.gson.toJson(this);
		}
		
		public Mat toMat() {
			byte[] raw_data = new byte[frameData.length];
			for(int i=0;i<raw_data.length;i++)
				raw_data[i]=(byte)frameData[i];
			Mat mat = new Mat(height, width, CvType.CV_8UC1);
			mat.put(0, 0, raw_data);
			return mat;
		}
	}

	

}
