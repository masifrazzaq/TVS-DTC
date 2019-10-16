package gui;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFrame;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import ThermalImageObjectTracking.MatProcessing;

public class ConvertImagesfromFoldertoMatricesText {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
	    Mat mat = Mat.eye( 3, 3, CvType.CV_8UC1 );
	    System.out.println( "mat = " + mat.dump() );
//	    System.out.println("Frame:"+MatProcessing.getMatsFromFolder("frames/").size());

	    JFrame frame = new JFrame();
	    for(Mat m0:MatProcessing.getMatsFromFolder("E://TestData//")){   
//		  System.out.println( "mat = " + m0.dump() ); //ASIF
	  	 
	        FileWriter fw = new FileWriter("E://TestData/",true);
	        PrintWriter out = new PrintWriter(fw);   //ASIF
	        out.println(m0.dump());  
	        out.close();   

	        
	}

}
}
