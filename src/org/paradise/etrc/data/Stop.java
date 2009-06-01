package org.paradise.etrc.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Stop {
	public String stationName;
	public String arrive;
	public String leave;
//	public String trainName;
//	public int dist;

	public Stop(String _name, String _arrive, String _leave) {
		stationName = _name;
		arrive = _arrive;
		leave = _leave;
		
//		trainName = "";
//		dist = 0;
	}

	public Stop copy() {
		Stop st = new Stop(this.stationName, this.arrive, this.leave);
		
//		st.trainName = this.trainName;
//		st.dist = this.dist;
		
		return st;
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (!(obj instanceof Stop))
			return false;

		return ((Stop) obj).stationName.equalsIgnoreCase(this.stationName);
	}
	
	public static Stop makeStop(String theName, String strArrive, String strLeave) {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		Date theArrive = null;
		Date theLeave = null;
		String myArrive = "";
		String myLeave = "";
		
		try {
			theArrive = df.parse(strArrive);
		} catch (ParseException e) {
			//e.printStackTrace();
		}
		
		try {
			theLeave = df.parse(strLeave);
		} catch (ParseException e) {
			//e.printStackTrace();
		}

		//如果到点解析不成功就把到点设为发点
		if(theArrive == null)
			myArrive = df.format(theLeave);
		else
			myArrive = df.format(theArrive);
		
		//如果发点解析不成功就把发点设为到点
		if(theLeave == null)
			myLeave = df.format(theArrive);
		else
			myLeave = df.format(theLeave);
		
		return new Stop(theName, myArrive, myLeave);
	}
}
