package ThermalImageObjectTracking;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;

public class TrackHistory extends HashMap<Long, Point>{
	
	
	public TrackHistory() {
		super();
		this.reset();
	}
	long t=0;
//	int t=0;
	//public long t0;
	public void reset() {
		this.clear();
		this.t=0;
	}
	public boolean isVoid() {
		return this.t==0;
	}
	public boolean isMoreFirst() {
		return this.t>1;
	}
	public boolean isGood() {
		return this.size()>1;
	}
	
	public void addTrack(long tn, Point p) {
		if(p!=null) 
			this.put(tn, p);
		t=tn;		
	}
	public SortedSet<Long> getKeys() {
		return new TreeSet<Long>(keySet());
	}
	

	
	public void printTrack() {
		for (long k:getKeys()) {
			Point point = this.get(k);
			System.out.println("\t"+k+"#\t "+point);
		    // ...
		}
		System.out.println();
	}
	
	public long getFirstTime() {
		for (long k:getKeys()) {
			if(k>0) return k;
		}
		return 0;
	}
	
	public long getLastTime() {
		long tlast=0;
		for (long k:getKeys()) {
			if(k>0) tlast=k;
		}
		return tlast;
	}

}
