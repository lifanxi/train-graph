package org.paradise.etrc.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Stop {
	public String stationName;

	public Date arrive, leave;

	public Stop(String _name, Date _arrive, Date _leave) {
		stationName = _name;
		arrive = _arrive;
		leave = _leave;
	}

	public Stop copy() {
		return new Stop(this.stationName, this.arrive, this.leave);
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (!(obj instanceof Stop))
			return false;

		return ((Stop) obj).stationName.equalsIgnoreCase(this.stationName);
	}
	
	public static Stop makeStop(String theName, String strArrive, String strLeave) {
		SimpleDateFormat df = new SimpleDateFormat("H:mm");
		Date theArrive = null;
		Date theLeave = null;
		
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
		
		if(theArrive == null)
			theArrive = theLeave;
		
		if(theLeave == null)
			theLeave = theArrive;
		
		return new Stop(theName, theArrive, theLeave);
	}
}
