package org.paradise.etrc.slice;

import org.paradise.etrc.data.Stop;
import org.paradise.etrc.data.Train;

public class RunLine extends ChartLine {
	public RunLine(Stop fromStop, Stop toStop, int leaveTime, int arriveTime, int distStaLeave, int distStaArrive) {
		super(leaveTime, distStaLeave, arriveTime, distStaArrive);
		
		stop1 = fromStop;
		stop2 = toStop;
		
//		time1 = leaveTime;
//		time2 = arriveTime;
//		
//		dist1 = distStaLeave;
//		dist2 = distStaArrive;
	}
	
	public String toString() {
		return "R(" + Train.intToTrainTime(time1) + " @ " + dist1 + " -> " 
		            + Train.intToTrainTime(time2) + " @ " + dist2 + ")";
	}

	public int getTimeOfTheDist(int dist) {
		if(!((dist1<dist && dist<dist2) || (dist1>dist && dist>dist2)))
			return -1;
		
		int t1 = time1;
//		int t2 = (time2 > time1) ? time2 : time2 + 1440;
		int t2 = time2;
		int d1 = dist1;
		int d2 = dist2;
		int d  = dist;
		int t = (d-d1)*(t2-t1)/(d2-d1) + t1;
		
		if(t<0)
			t+=1440;
		else if(t>=1440)
			t-=1440;
		
		return t;
	}
}