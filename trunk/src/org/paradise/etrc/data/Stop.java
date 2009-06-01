package org.paradise.etrc.data;

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

}
