package org.paradise.etrc.data;

public 	class LCBStation extends Station{
	public LCBStation(String _name, int _dist, int _level, boolean _hide) {
		super(_name, _dist, _level, _hide);
	}

	public String xianlu;
	
	public String toString() {
		return xianlu + "-" + super.toString();
	}
}

