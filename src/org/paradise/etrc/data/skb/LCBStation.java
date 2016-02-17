package org.paradise.etrc.data.skb;

import org.paradise.etrc.data.Station;

public 	class LCBStation extends Station{
	public LCBStation(String _name, int _dist, int _level, boolean _hide, String _following, boolean _doubletrack) {
		super(_name, _dist, _level, _hide, _following, _doubletrack);
	}

	public String xianlu;
	
	public String toString() {
		return xianlu + "-" + super.toString();
	}
}

