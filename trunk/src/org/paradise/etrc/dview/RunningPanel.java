package org.paradise.etrc.dview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JPanel;

import org.paradise.etrc.data.Chart;
import org.paradise.etrc.data.Station;
import org.paradise.etrc.data.Stop;
import org.paradise.etrc.data.Train;

public class RunningPanel extends JPanel {
	private static final int SPACE_AT_STATION = 20;

	private Chart chart;
	private DynamicView dView;
	

	public RunningPanel(Chart _chart, DynamicView _dView) {
		chart = _chart;
		dView = _dView;
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		this.setBackground(Color.white);
		this.setLayout(new BorderLayout());
		this.setFont(new Font("宋体", 0, 12));
	}

	public void paint(Graphics g) {
		super.paint(g);
		//上行线
		g.drawLine(0, 
				   dView.topMargin, 
				   this.getWidth(), 
				   dView.topMargin);
		
		//下行线
		g.drawLine(0, 
				   this.getHeight() - dView.bottomMargin, 
				   this.getWidth(), 
				   this.getHeight() - dView.bottomMargin);
		
		//车站线
//		g.drawLine(dView.leftMargin, 
//				   this.getHeight()/2, 
//				   this.getWidth() - dView.rightMargin, 
//				   this.getHeight()/2);
		
		if (chart == null)
			return;

		if (chart.circuit != null)
			for (int i = 0; i < chart.circuit.stationNum; i++) {
				drawStation(g, chart.circuit.stations[i]);
			}
		
		drawTrains(g);
	}

	public Dimension getPreferredSize() {
		int w, h;
		w = chart.circuit.length * dView.scale + dView.leftMargin + dView.rightMargin;
		h = dView.runningPanelHeight;
		return new Dimension(w, h);
	}
	
	public void drawStation(Graphics g, Station station) {
		if (station.hide)
			return;

		int x = station.dist * dView.scale + dView.leftMargin;
		
		int y1 = 0;
		int y2 = getHeight();

		if (station.level <= chart.displayLevel) {
			//设置坐标线颜色
			Color oldColor = g.getColor();
			g.setColor(dView.gridColor);

			//画坐标线
			g.drawLine(x, y1, x, y2);
//			if (station.level <= chart.boldLevel) {
//				g.drawLine(x + 1, y1, x + 1, y2);
//			}

			//恢复原色
			g.setColor(oldColor);

			//画站名
			drawStationName(g, station, x);
		}
	}

	private void drawStationName(Graphics g, Station station, int x) {
		int y = getHeight() / 2;
		//站名
		Color oldColor = g.getColor();
		
//		if (dView.distUpDownState == DynamicView.SHOW_DOWN) {
//			g.setColor(dView.downDistColor);
//		}
//		else {
//			g.setColor(dView.upDistColor);
//		}
		g.setColor(Color.BLACK);
		
		g.drawString(station.name, x + 2, y + 5);

		g.setColor(oldColor);
	}
	
	private Hashtable runningTrains = new Hashtable();
	private Hashtable atStationTrains = new Hashtable();
	
	private void drawTrains(Graphics g) {
		//找出停靠中的车
		atStationTrains = findTrainsAtStation(dView.currentTime);
//		System.out.println(atStationTrains.size());
		//遍历一下，看看哪些车是运行中的，哪些车是停靠中的
		runningTrains.clear();
		for(int i = 0; i < chart.trainNum; i++) {
			int dist = getDistOfTrain(chart.trains[i], dView.currentTime);
			
			if(dist >= 0) {
				if(!atStationTrains.keySet().contains(chart.trains[i]))
					runningTrains.put(chart.trains[i], "" + dist);
			}
			
//			String station = chart.circuit.getStationName(dist, true);
////			System.out.println(dist + ":" + station);
//			if(station.endsWith("附近")) {
//				runningTrains.put(chart.trains[i], "" + dist);
////				System.out.println("R: " + chart.trains[i].getTrainName());
//			}
//			else {
//				atStationTrains.put(chart.trains[i], station);
////				System.out.println("A: " + chart.trains[i].getTrainName());
//			}
		}
		
		//画运行中的列车
		Enumeration en = runningTrains.keys();
		while(en.hasMoreElements()) {
			Train theRunningTrain = (Train) en.nextElement();
			int theDist = Integer.parseInt((String) runningTrains.get(theRunningTrain));
			if(theRunningTrain.isDownTrain(chart.circuit) == Train.DOWN_TRAIN) {
				drawRunningTrainDown(g, theRunningTrain, theDist);
			}
			else {
				drawRunningTrainUP(g, theRunningTrain, theDist);
			}
		}

		//画停站的列车
		for(int i=0; i<chart.circuit.stationNum; i++) {
			String theStation = chart.circuit.stations[i].name;
			int stationDist = chart.circuit.stations[i].dist;
			Vector myTrains = new Vector();
			
			Enumeration stoped = atStationTrains.keys();
			while(stoped.hasMoreElements()) {
				Train theTrain = (Train) stoped.nextElement();
				String sta = (String) atStationTrains.get(theTrain);
				
				if(sta.equalsIgnoreCase(theStation))
					myTrains.add(theTrain);
			}
			
			drawTrainsAtStation(g, myTrains, stationDist);
		}
	}
	
	private Hashtable findTrainsAtStation(int time) {
		Hashtable trains = new Hashtable();
		for(int i = 0; i < chart.trainNum; i++) {
			String station = getStationNameAtTheTime(chart.trains[i], time);
			
			if(!station.equalsIgnoreCase("")) {
				trains.put(chart.trains[i], station);
			}
		}
		return trains;
	}

	private void drawTrainsAtStation(Graphics g, Vector myTrains, int dist) {
		int dNum = 0;
		int uNum = 0;
		for(int i = 0; i < myTrains.size(); i++) {
			Train train = (Train) myTrains.get(i);
			
			if(train.isDownTrain(chart.circuit) == Train.DOWN_TRAIN) {
				drawTrainAtStationDown(g, dNum, train, dist);
				dNum ++;
			}
			else {
				drawTrainAtStationUp(g, uNum, train, dist);
				uNum ++;
			}
		}
	}

	private void drawTrainAtStationDown(Graphics g, int i, Train train, int dist) {
//		int dist = getDistOfTrain(train, currentTime);
		
		int x = dView.getPelsX(dist);
		int y = getHeight() - dView.bottomMargin + (i+1) * SPACE_AT_STATION;
		
		g.setColor(train.getDefaultColor());
		g.fillRect(x-5, y-2, 11, 5);
		
		String trainName = train.getTrainName(chart.circuit);
		
		Font oldFont = g.getFont();
		g.setFont(new Font("Dialog", Font.PLAIN, 10));
		
		int w = (int) g.getFontMetrics().getStringBounds(trainName, g).getWidth();
		g.drawString(trainName, x - w/2, y + 13);
		
		g.setFont(oldFont);
	}

	private void drawTrainAtStationUp(Graphics g, int i, Train train, int dist) {
		int x = dView.getPelsX(dist);
		int y = dView.topMargin - (i+1) * SPACE_AT_STATION;
		
		g.setColor(train.getDefaultColor());
		g.fillRect(x-5, y-2, 11, 5);
		
		String trainName = train.getTrainName(chart.circuit);
		
		Font oldFont = g.getFont();
		g.setFont(new Font("Dialog", Font.PLAIN, 10));
		
		int w = (int) g.getFontMetrics().getStringBounds(trainName, g).getWidth();
		g.drawString(trainName, x - w/2, y - 5);
		
		g.setFont(oldFont);
	}

	private void drawRunningTrainDown(Graphics g, Train train, int dist) {
//		int dist = getDistOfTrain(train, currentTime);
		
		int x = dView.getPelsX(dist);
		int y = getHeight() - dView.bottomMargin;
		
		g.setColor(train.getDefaultColor());
		g.fillRect(x-5, y-2, 11, 5);
		
		String trainName = train.getTrainName(chart.circuit);
		
		Font oldFont = g.getFont();
		g.setFont(new Font("Dialog", Font.PLAIN, 10));
		
		int w = (int) g.getFontMetrics().getStringBounds(trainName, g).getWidth();
		g.drawString(trainName, x - w/2, y + 13);
		
		g.setFont(oldFont);
	}
	
	private void drawRunningTrainUP(Graphics g, Train train, int dist) {
//		int dist = getDistOfTrain(train, currentTime);
		
		int x = dView.getPelsX(dist);
		int y = dView.topMargin;
		
//		g.setColor(train.getDefaultColor());
		g.setColor(train.color);
		g.fillRect(x-5, y-2, 11, 5);
		
		String trainName = train.getTrainName(chart.circuit);
		
		Font oldFont = g.getFont();
		g.setFont(new Font("Dialog", Font.PLAIN, 10));
		
		int w = (int) g.getFontMetrics().getStringBounds(trainName, g).getWidth();
		g.drawString(trainName, x - w/2, y - 5);
		
		g.setFont(oldFont);
	}

	private String getStationNameAtTheTime(Train train, int time) {
		for(int i=0; i<train.stopNum; i++) {
			int t1 = trainTimeToInt(train.stops[i].arrive);
			int t2 = trainTimeToInt(train.stops[i].leave);
			if(t1<= time && t2 >= time && t1 != t2) {
				int d = chart.circuit.getStationDist(train.stops[i].stationName);
//				System.out.println(train.getTrainName() + "^" + time + "~" + train.stops[i].stationName + 
//						"~" + t1 + "~" + train.stops[i].arrive + 
//						"~" + t2 + "~" + train.stops[i].leave +
//						"~" + d);
				if(d >= 0)
					return train.stops[i].stationName;
			}
		}
		
		return "";
	}
	
	private int getDistOfTrain(Train train, int time) {
		//停站的情况
		for(int i=0; i<train.stopNum; i++) {
			int t1 = trainTimeToInt(train.stops[i].arrive);
			int t2 = trainTimeToInt(train.stops[i].leave);
			
			//跨越0点情况的处理
			if(t2 < t1)
				t2 += 24 * 60;

			if(t1<= time && t2 >= time ) {
//				System.out.println(train.getTrainName() + "^" + time + "~" + train.stops[i].stationName + 
//						"~" + t1 + "~" + train.stops[i].arrive + 
//						"~" + t2 + "~" + train.stops[i].leave);
				int d = chart.circuit.getStationDist(train.stops[i].stationName);
				
				if(d >= 0)
					return d;
			}
		}
		
		//运行中的情况
		for(int i=0; i<train.stopNum-1; i++) {
			Stop s1 = train.stops[i];
			Stop s2 = train.stops[i+1];
			
			int d1 = chart.circuit.getStationDist(s1.stationName);
			int d2 = chart.circuit.getStationDist(s2.stationName);
			
			int t1 = trainTimeToInt(s1.leave);
			int t2 = trainTimeToInt(s2.arrive);
			
			//不在本线路上-继续找（可能下一天会在本线路上的）
			if(d1<0 || d2<0)
				continue;
			
			//跨越0点情况的处理
			if(t2 < t1)
				t2 += 24 * 60;
			
			if(t1 <= time && t2 >= time) {
				int dist = (d2-d1)*(time-t1)/(t2-t1) + d1;
//				System.out.println(train.getTrainName() + "^" + time + "~" + train.stops[i].stationName + 
//						"~" + t1 + "~" + train.stops[i].arrive + 
//						"~" + t2 + "~" + train.stops[i].leave + "*****" 
//						+ d1 + "~" + d2 + "~" + dist);
				return dist;
			}
		}
		
		return -1;
	}
	
	//工具方法
	private int trainTimeToInt(String strTime) {
		String strH = strTime.split(":")[0];
		String strM = strTime.split(":")[1];
		
		int h = Integer.parseInt(strH);
		int m = Integer.parseInt(strM);
		
		return h*60 + m;
	}
}
