package org.paradise.etrc.data;

  public class Station {
    public String name = "";
    public boolean hide = false;
    public int level = 0;
    public int dist = 0;

    public Station(String _name, int _dist, int _level, boolean _hide) {
      name = _name;
      level = _level;
      dist = _dist;
      hide = _hide;
    }
    
    public Station copy() {
    	return new Station(this.name, this.dist, this.level, this.hide);
    }

    public Station(String _name, int _dist, int _level) {
      this(_name, _dist, _level, false);
    }

    public Station(String _name, int _dist) {
      this(_name, _dist, 0, false);
    }
    
    public String getOneName() {
 		if(name.startsWith("北京"))
			return "京";
		else if(name.startsWith("上海"))
			return "沪";
		else if(name.startsWith("南京"))
			return "宁";
		else if(name.startsWith("天津"))
			return "津";
		else if(name.startsWith("重庆"))
			return "渝";
		else if(name.startsWith("宁波"))
			return "甬";
		else if(name.startsWith("无锡"))
			return "锡";
		else if(name.length() <= 0)
    		return "";
    	else 
    		return name.substring(0,1);
    }
    
    public String toString() {
    	return name + ":" + level + ":" + dist + ":" + hide;
    }
  }
