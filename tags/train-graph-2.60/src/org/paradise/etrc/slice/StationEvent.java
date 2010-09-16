package org.paradise.etrc.slice;

import org.paradise.etrc.data.Train;

public class StationEvent extends ChartEvent {
	public static final int PASS = 1;   //通
	public static final int ARRIVE = 2; //到
	public static final int LEAVE = 3;  //发
	public static final int START = 11; //始发
	public static final int END = 12;   //终到 
	
	//停站（通过）类型
	int stopType; //图定办客（来自时刻表）
	public static final int PASSENGER_STOP = 1;
	public static final int FIXED_STOP = 2;
	public static final int CALCULATE_STOP = 3;

	public StationEvent(int _time, int _dist, String sta, String tra, int etype, int stype) {
		time = _time;
		dist = _dist;
		station = sta;
		train = tra;
		eventType = etype;
		stopType = stype;
	}
	
	public String toString() {
		String stype;
		switch(stopType) {
		case PASSENGER_STOP: 
			stype = "（图定办客）"; 
			if(eventType == LEAVE)
				stype = "";
			break;
		case FIXED_STOP: 
			stype = "（图定）"; break;
		case CALCULATE_STOP: 
			stype = "（推算）"; break;
		default:
			stype = "（未知）";
		}
		
		String etype;
		switch(eventType) {
		case PASS:
			etype = "通过"; break;
		case ARRIVE:
			etype = "到达"; break;
		case LEAVE:
			etype = "发车"; break;
		case START:
			etype = "始发"; break;
		case END:
			etype = "终到"; break;
		default:
			etype = "未知";	
		}
		return 	"\n" + formatDist(dist) + ". "
		             + Train.intToTrainTime(time) + " " 
		             + formatTrainName(train) + formatStationName(station) + " " +etype + stype;
	}
}
