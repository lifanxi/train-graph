package org.paradise.etrc.slice;

import java.util.Vector;

public class ChartEvent {
	public int time;
	public int dist;
	
	String train;
	String station;
	
	//事件类型
	//TrainEvent是：会 越 让
	//StationEvent是：通 到 发
	int eventType;

	String desc;
	String comment;
	
	public static Vector<ChartEvent> sortByTime(Vector<ChartEvent> events) {
		Vector<ChartEvent> newEvents = new Vector<ChartEvent>();
		
		for(int time=0; time<1440; time++) {
			for(int j=0; j<events.size(); j++) {
				ChartEvent event = (ChartEvent) events.get(j);
				if(event.time == time)
					newEvents.add(event);
			}
		}
		
		return newEvents;
	}
	
	public static Vector<ChartEvent> sortByDistAsc(Vector<ChartEvent> events) {
		for(int i=0; i<events.size(); i++) {
			ChartEvent Ei = (ChartEvent) events.get(i);
			for(int j=i+1; j<events.size(); j++) {
				ChartEvent Ej = (ChartEvent) events.get(j);
				if(Ej.dist < Ei.dist) {
					exchange(events, i, j);
					Ei = (ChartEvent) events.get(i);
					Ej = (ChartEvent) events.get(j);
				}
			}
		}
		
		return sortByTimeInSameDist(events);
	}
	
	public static Vector<ChartEvent> sortByDistDesc(Vector<ChartEvent> events) {
		for(int i=0; i<events.size(); i++) {
			ChartEvent Ei = (ChartEvent) events.get(i);
			for(int j=i+1; j<events.size(); j++) {
				ChartEvent Ej = (ChartEvent) events.get(j);
				if(Ej.dist > Ei.dist) {
					exchange(events, i, j);
					Ei = (ChartEvent) events.get(i);
					Ej = (ChartEvent) events.get(j);
				}
			}
		}
		
		return sortByTimeInSameDist(events);
	}
	
	//把距离相等的事件 按时间先后排列
	public static Vector<ChartEvent> sortByTimeInSameDist(Vector<ChartEvent> events) {
		for(int i=0; i<events.size(); i++) {
			ChartEvent Ei = (ChartEvent) events.get(i);
			for(int j=i+1; j<events.size(); j++) {
				ChartEvent Ej = (ChartEvent) events.get(j);
				if((Ej.dist == Ei.dist) && (!isAscTime(Ei.time , Ej.time))) {
						exchange(events, i, j);
				}
			}
		}

		return events;
	}
	
	private static void exchange(Vector<ChartEvent> vec, int i, int j) {
		ChartEvent oi = vec.get(i);
		ChartEvent oj = vec.get(j);
		
		vec.set(i, oj);
		vec.set(j, oi);
	}
	
	//检测时间顺序，需要处理跨日的情况
	//最大跨日间距两端各4小时（若有超长的行车或者停车线本算法无效）
	//20－1不是升序的，21－1是升序的
	//23－4不是升序的，23－3是升序的
	//最大的跨日升序位20:01－3:59
	public static boolean isAscTime(int time1, int time2) {
		if(time1 > 20 * 60 && time2 < 4 * 60) {
			return true;
		}
		else if(time2 > 20 * 60 && time1 < 4 * 60) {
			return false;
		}
		else {
			return time1 < time2;
		}
	}
	
	protected String formatDist(int dist) {
		String strDist = "" + dist;
		while(strDist.length() < 4) {
			strDist = " " + strDist;
		}
		return strDist;
	}
	
	protected String formatTrainName(String oldName) {
		oldName += "次";
		while(oldName.length() < 6) {
			oldName += " ";
		}
		return oldName;
	}
	
	protected String formatStationName(String oldName) {
		oldName += "站";
		while(oldName.length() < 4) {
			oldName += "　";
		}
		return oldName;
	}
	
	protected String formatAreaName(String oldName) {
		oldName += "区间";
		return oldName;
	}
}
