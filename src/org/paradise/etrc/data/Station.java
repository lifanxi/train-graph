package org.paradise.etrc.data;

  public class Station {
    public String name = "";
    public boolean hide = false;
    public int level = 0;
    public int dist = 0;
//2015-09-06增加车站的接续站属性，用来描述当前车站所通达的跨线车站
	public String following = "";
//2015-09-19增加车站（实际上是铁路区间的）复线属性，
    public boolean doubletrack  = true; 
    public Station(String _name, int _dist, int _level, boolean _hide, String _following, boolean _doubletrack) {
      name = _name;
      level = _level;
      dist = _dist;
      hide = _hide;
	  following = _following;
	  doubletrack = _doubletrack;
    }
    
    public Station copy() {
    	return new Station(this.name, this.dist, this.level, this.hide,this.following, this.doubletrack);
    }

    public Station(String _name, int _dist, int _level) {
      this(_name, _dist, _level, false,"",true);
    }

    public Station(String _name, int _dist) {
      this(_name, _dist, 0, false,"",true);
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
