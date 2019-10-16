package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;



public class ConvertMillSecTextToDateTime {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");

    long now = System.currentTimeMillis();

    Calendar calendar = Calendar.getInstance();
    
    File file = new File("E://TestData//");
    BufferedReader br = new BufferedReader(new FileReader(file));
  
    String st;
    while ((st = br.readLine()) != null){
    	calendar.setTimeInMillis(Long.parseLong(st));
    	System.out.println(formatter.format(calendar.getTime())+", ");   //    	System.out.println(now + " = " + formatter.format(calendar.getTime()));
    	}

	}

}



   