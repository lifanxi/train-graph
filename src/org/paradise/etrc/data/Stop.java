package org.paradise.etrc.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Stop {
	public String stationName;
	public String arrive;
	public String leave;
	
	//20070224新增，是否图定
	public boolean isPassenger;

	public Stop(String _name, String _arrive, String _leave, boolean _schedular) {
		stationName = _name;
		arrive = _arrive;
		leave = _leave;
		isPassenger = _schedular;
	}

//	public Stop(String _name, String _arrive, String _leave) {
//		this(_name, _arrive, _leave, true); //默认是图定的，向下兼容 -- 取消，所有使用的地方显示指定true
//	}

	public Stop copy() {
		Stop st = new Stop(this.stationName, this.arrive, this.leave, this.isPassenger);
		
		return st;
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (!(obj instanceof Stop))
			return false;

		return ((Stop) obj).stationName.equalsIgnoreCase(this.stationName);
	}
	
	public static Stop makeStop(String theName, String strArrive, String strLeave, boolean isSchedular) {
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
		
		return new Stop(theName, myArrive, myLeave, isSchedular);
	}
}
