package ThermalImageObjectTracking;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import ThermalImageObjectTracking.FloorRtFrame.JsonFloor;
import ThermalImageObjectTracking.FloorRtFrame.JsonFloor.JsonCell;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
/**
 * @author Javier Medina & Asif
 * Utils for handling data processing
 */

public class Utils {
	static Gson gson = new Gson();
	
	static public ByteArrayOutputStream  getByteFromURL(String s_url) throws Exception{
		URL url = new URL(s_url);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = null;
		try {
		  is = url.openStream ();
		  byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
		  int n;

		  while ( (n = is.read(byteChunk)) > 0 ) {
		    baos.write(byteChunk, 0, n);
		  }
		}
		catch (IOException e) {
		  System.err.printf ("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage());
		  e.printStackTrace ();
		  // Perform any other exception handling that's appropriate.
		}
		finally {
		  if (is != null) { is.close(); }
		}
		return baos;
	}
	
	 public static void showResult(Mat img) {
//		    Imgproc.resize(img, img, new Size(640, 480));
		    MatOfByte matOfByte = new MatOfByte();
		    Imgcodecs.imencode(".bmp", img, matOfByte);
		    byte[] byteArray = matOfByte.toArray();
		    BufferedImage bufImage = null;
		    try {
		        InputStream in = new ByteArrayInputStream(byteArray);
		        bufImage = ImageIO.read(in);
		        JFrame frame = new JFrame();
		        frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
		        frame.pack();
		        frame.setVisible(true);
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
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
	   
	   public static void deleteFolder(File folder) {
		   folder.mkdirs();
		    File[] files = folder.listFiles();
		    if(files!=null) { //some JVMs return null for empty dirs
		        for(File f: files) {
		            if(f.isDirectory()) {
		                deleteFolder(f);
		            } else {
		                f.delete();
		            }
		        }
		    }
		 //   folder.delete();
		}   
	   
		static public String getS(float v) {
			return String.format("%.2f", v);
		}	
		
		static public List<JsonFloor> getJsonFloorsFromFolder(String sFolder) throws JsonSyntaxException, IOException{
			List<JsonFloor> ret=new LinkedList<JsonFloor>();
			File folder = new File(sFolder);
		    for (final File fileEntry : folder.listFiles()) {
			        if (!fileEntry.isDirectory()) {
			      //      System.out.println(fileEntry.getName());
			            if(fileEntry.getName().endsWith(".json"))
			            	ret.add(
			            			Utils.gson.fromJson(getContentFromFile(sFolder+fileEntry.getName()), JsonFloor.class)
			            			);
			        }
			    }
		    return ret;
			}
		
		static public String getContentFromFile(String sFile) throws IOException {
		    StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new FileReader(sFile));
			try {
			    String line = br.readLine();

			    while (line != null) {
			        sb.append(line);
			        sb.append(System.lineSeparator());
			        line = br.readLine();
			    }
			} finally {
			    br.close();
			}
		    return sb.toString();
		}

		static public void writeTimeStampFile(String folder, String text) throws FileNotFoundException {
			try (PrintStream out = new PrintStream(new FileOutputStream(folder+"/"+System.currentTimeMillis()+".json"))) {
			    out.print(text);
			    out.close();
			}
		}

		
		public static void displayImage(Image image) {
			ImageIcon icon = new ImageIcon(image);
			JFrame frame = new JFrame("OpenCV");
			frame.setLayout(new FlowLayout());
			frame.setSize(image.getWidth(null) + 30, image.getHeight(null) + 45);
			JLabel lbl = new JLabel();
			lbl.setIcon(icon);
			frame.add(lbl);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		
}
