package org.paradise.etrc.slice;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.Vector;

import org.paradise.etrc.data.Stop;

public class ChartLine {
	protected Stop stop1;
	protected Stop stop2;

	protected int time1;
	protected int time2;
	
	protected int dist1;
	protected int dist2;
	
	public ChartLine() {
		
	}
	
	protected ChartLine(int x1, int y1, int x2, int y2) {
		time1 = x1;
		dist1 = y1;
		time2 = x2;
		dist2 = y2;
		
		if(time2 < time1)
			time2 += 1440;
	}
	
//	public boolean isTimeOnMe(int time) {
//		//跨越0点问题的处理
//		if(time2 < time1) {
////			time2 += 1440;
////			if(time < MAXGAP)
////				time += 1440;
//			return (time1<=time && time<=1440) || (0<=time && time<=time2);
//		}
//		return (time1 <= time && time <= time2);
//	}
	
	public boolean isDistOnMe(int dist) {
		return (dist1 < dist && dist < dist2) || 
		       (dist2 < dist && dist < dist1);
	}
	
	public boolean isDistOnMyEnd(int dist) {
		return (dist1 == dist || dist2 == dist);
	}
	
//	public static Vector getChartLineByTime(Vector lines, int time) {
//		Vector found = new Vector();
//		for(int i=0; i<lines.size(); i++) {
//			if(((ChartLine) lines.get(i)).isTimeOnMe(time)) {
//				found.add(lines.get(i));
//			}
//		}
//		return found;
//	}
	
	public static Vector<ChartLine> getChartLineByDistWithEnd(Vector<ChartLine> lines, int dist) {
		Vector<ChartLine> found = new Vector<ChartLine>();
		for(int i=0; i<lines.size(); i++) {
			ChartLine line = (ChartLine) lines.get(i);
			if(line.isDistOnMe(dist) || line.isDistOnMyEnd(dist)) {
				found.add(lines.get(i));
			}
		}
		return found;
	}
	
	public static Vector<ChartLine> getChartLineByDistWithoutEnd(Vector<ChartLine> lines, int dist) {
		Vector<ChartLine> found = new Vector<ChartLine>();
		for(int i=0; i<lines.size(); i++) {
			ChartLine line = (ChartLine) lines.get(i);
			if(line.isDistOnMe(dist)) {
				found.add(lines.get(i));
			}
		}
		return found;
	}
	
//	public static int getIntersectTime(ChartLine l1, ChartLine l2) {
//		Point p = getIntersect(l1, l2);
//		
//		return p == null ? -1 : p.x;
//	}
//	
//	public static int getIntersectDist(ChartLine l1, ChartLine l2) {
//		Point p = getIntersect(l1, l2);
//		
//		return p == null ? -1 : p.y;
//	}
	
	public static Point getIntersect(ChartLine l1, ChartLine l2) {
		int x1 = l1.time1;
		int y1 = l1.dist1; 
		int x2 = l1.time2;
		int y2 = l1.dist2;
		
		if(x2<x1)
			x2+=1440;
		
        int x3 = l2.time1;
        int y3 = l2.dist1;
        int x4 = l2.time2;
        int y4 = l2.dist2;
        
        if(x4<x3)
        	x4+=1440;
        
//    	System.out.println("(" + x1 + "," + y1 + ")" + 
//		           "(" + x2 + "," + y2 + ")" + 
//		           "(" + x3 + "," + y3 + ")" + 
//		           "(" + x4 + "," + y4 + ")");

        if(!Line2D.linesIntersect(x1,y1,x2,y2,x3,y3,x4,y4))
        	return null;
        
        int d = (y1-y2)*(x3-x4) - (x1-x2)*(y3-y4);
        //平行线
        if(d == 0) {
//        	System.out.println("<null>");
        	return null;
        }
        
        int x0 = ( (y1-y3)*(x1-x2)*(x3-x4)-(y1-y2)*(x3-x4)*x1 + (y3-y4)*(x1-x2)*x3 ) / (-d);
        int y0 = ( (x1-x3)*(y1-y2)*(y3-y4)-(x1-x2)*(y3-y4)*y1 + (x3-x4)*(y1-y2)*y3 ) / d;
        
        if( ((x0-x1)*(x0-x2)<=0) &&  
            ((x0-x3)*(x0-x4)<=0) &&  
            ((y0-y1)*(y0-y2)<=0) &&  
            ((y0-y3)*(y0-y4)<=0) ) {
        
//        	System.out.println("(" + x1 + "," + y1 + ")" + 
// 		           "(" + x2 + "," + y2 + ")" + 
// 		           "(" + x3 + "," + y3 + ")" + 
// 		           "(" + x4 + "," + y4 + ")");
//        	System.out.println("<" + x0 + "," + y0 + ">");
        	return new Point(x0, y0);
        }
        else {
//        	System.out.println("<null>");
        	return null;
        }
	}
	/*
	 *  求线段交点  
          x0 = [(x2-x1)*(x4-x3)*(y3-y1)+(y2-y1)*(x4-x3)*x1-(y4-y3)*(x2-x1)*x3]/d  
          y0 = [(y2-y1)*(y4-y3)*(x3-x1)+(x2-x1)*(y4-y3)*y1-(x4-x3)*(y2-y1)*y3]/(-d)  
   		求出交点后在判断交点是否在线段上，即判断以下的式子：  
          (x0-x1)*(x0-x2)<=0  
          (x0-x3)*(x0-x4)<=0  
          (y0-y1)*(y0-y2)<=0  
          (y0-y3)*(y0-y4)<=0  
         只有上面的四个式子都成立才可判定(x0,y0)是线段AB与CD的交点，否则两线段无交点  
	 */
	
	public static void main(String argv[]) {
		ChartLine l1 = new ChartLine(1084,6,1160,71);
		ChartLine l2 = new ChartLine(1074,6,1118,71);
		System.out.println(getIntersect(l1, l2));
	}
}
