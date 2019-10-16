package ThermalImageObjectTracking;

//Java program to covert a color image to gray scale
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ConvertJPGtoGrayscale {
 public static void main(String args[]) throws Exception
 {

     // To load  OpenCV core library
     System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
     String input = "F://MatlabCNN//";

     // To Read the image
     Mat source = Imgcodecs.imread(input);

     // Creating the empty destination matrix
     Mat destination = new Mat();

     // Converting the image to gray scale and 
     // saving it in the dst matrix
     Imgproc.cvtColor(source, destination, Imgproc.COLOR_RGB2GRAY);

     // Writing the image
     Imgcodecs.imwrite("F://MatlabCNN", destination);
     System.out.println("The image is successfully to Grayscale");
 }
}

