package org.paradise.etrc.slice;

import org.paradise.etrc.data.Train;

public class TrainEvent extends ChartEvent {
	public static final int MEET = -1;    //会
	public static final int EXCEED = -2;  //越
	public static final int YIELD = -3;   //让
	public static final int CROSS = -4;   //外 站外越行
	public static final int LEAD = -11;   //领 同向的两车在同一个站遇到，我先进先出－－领跑
	public static final int FOLLOW = -12; //跟 同向的两车在同一个站遇到，我后进后出－－跟随
	
	//是否是在站内会、越、让
	public int crossType;
	public static final int BOTH_STOP = 1; //站内双方停车（各种情况都有可能）
	public static final int ME_STOP = 2;   //站内我停他不停（被踩，如果是同向，肯定是让行）
	public static final int HE_STOP = 3;   //站内他停我不停（踩他，如果是同向，肯定是越行）
	public static final int NONE_STOP = 4; //站外（如果是同向肯定是错误<站外越行>，如果是反向：单线线路，错误<站外会车>；双线线路，站外会车）
	
	TrainEvent(int _time, int _dist, String _station, String _train, int etype, int ctype) {
		time = _time;
		dist = _dist;
		station = _station;
		train = _train;
		eventType = etype;
		crossType = ctype;
	}
	
	public String toString() {
		String ctype = "";
		switch(crossType) {
		case BOTH_STOP: 
//			ctype = "（双方停车）"; 
			break;
		case ME_STOP: 
//			ctype = "（仅我方停车）"; 
//			if(eventType == YIELD)
//				ctype = "";
			break;
		case HE_STOP: 
//			ctype = "（对方停车）"; 
//			if(eventType == EXCEED)
//				ctype = "";
			break;
		case NONE_STOP:
			if(eventType == CROSS)
				ctype = "（<站外越行错>！！！）"; 
//			else
//				ctype = "";
//				ctype = "（单线区间<站外会车错>！）";
			break;
		}
		
		String etype;
		switch(eventType) {
		case MEET:
			etype = " 会 "; break;
		case EXCEED:
			etype = " 越 "; break;
		case YIELD:
			etype = " 让 "; break;
		case CROSS:
			etype = " 与 "; break;
		case LEAD:
			etype = " 领 "; break;
		case FOLLOW:
			etype = " 跟 "; break;
		default:
			etype = " ?? ";	
		}
		
		String place = formatStationName(station);
		if(crossType == NONE_STOP)
			place = formatAreaName(station);
		
		return 	"\n" + formatDist(dist) + ". "
		             + Train.intToTrainTime(time) + " " 
					 + place + " " + etype 
		             + formatTrainName(train) + ctype;
	}
}
