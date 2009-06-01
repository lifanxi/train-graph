package org.paradise.etrc.slice;

import org.paradise.etrc.data.Stop;
import org.paradise.etrc.data.Train;

public class StopLine extends ChartLine {
	public StopLine(Stop theStop, int timeArrive, int timeLeave, int dist) {
		super(timeArrive, dist, timeLeave, dist);
		
		stop1 = theStop;
		stop2 = theStop;
		
//		time1 = timeArrive;
//		dist1 = dist;
//		
//		time2 = timeLeave;
//		dist2 = dist;
	}
	
	public String toString() {
		return "S(" + Train.intToTrainTime(time1) + " -> " 
		           + Train.intToTrainTime(time2) + " @ " + dist1 + ")";
	}
}
