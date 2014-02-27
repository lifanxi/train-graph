package org.paradise.etrc.slice;

import java.awt.Point;
import java.util.Vector;

import org.paradise.etrc.data.*;

public class TrainSlice {
	Train train;
	Circuit circuit;
	
	Vector<ChartLine> runLines  = new Vector<ChartLine>();
	Vector<ChartLine> stopLines = new Vector<ChartLine>();
	
	public TrainSlice(Train _train, Circuit _circuit) {
		train = _train;
		circuit = _circuit;
		buildup();
	}
	
	//车次切片 反向 会车
	public Vector<TrainEvent> getTrainEventsOfDiffDir(TrainSlice he) {
		Vector<TrainEvent> events = new Vector<TrainEvent>();
		
		//同一趟车
		if(he.train.equals(train))
			return events;
		
		//反向的列车
		if(train.isDownTrain(circuit) != he.train.isDownTrain(circuit)) {
			//遍历我的停车线
			for(int myStopIndex=0; myStopIndex<stopLines.size(); myStopIndex++) {
				StopLine myStopLine = (StopLine) stopLines.get(myStopIndex);
				//遍历他的停车线 （大家都停车，站内会车，四种情况不用分）
				for(int hisStopIndex=0; hisStopIndex<he.stopLines.size(); hisStopIndex++) {
					StopLine hisStopLine = (StopLine) he.stopLines.get(hisStopIndex);
					
					int myArriveTime = myStopLine.time1;
					int myLeaveTime = myStopLine.time2;
					int hisArriveTime = hisStopLine.time1;
					int hisLeaveTime = hisStopLine.time2;
					
					if(!myStopLine.stop1.equals(hisStopLine.stop1))
						continue;
					
					//先进后出
					if(myArriveTime<hisArriveTime && myLeaveTime>hisLeaveTime) {
//						System.out.println(myStopLine.stop1.stationName + " " + train.getTrainName(circuit) + " ss站内会车（先到后走） " + he.train.getTrainName(circuit));
						events.add(new TrainEvent(hisStopLine.time2, myStopLine.dist1, myStopLine.stop1.stationName, he.train.getTrainName(circuit), TrainEvent.MEET, TrainEvent.BOTH_STOP));
					}
					//后进先出
					else if(myArriveTime>hisArriveTime && myLeaveTime<hisLeaveTime) {
//						System.out.println(myStopLine.stop1.stationName + " " + train.getTrainName(circuit) + " ss站内会车（后到先走） " + he.train.getTrainName(circuit));
						events.add(new TrainEvent(myStopLine.time2, hisStopLine.dist1, hisStopLine.stop1.stationName, he.train.getTrainName(circuit), TrainEvent.MEET, TrainEvent.BOTH_STOP));
					}
					//先进先出
					else if(myArriveTime<hisArriveTime && myLeaveTime<hisLeaveTime && myLeaveTime>=hisArriveTime) {
//						System.out.println(myStopLine.stop1.stationName + " " + train.getTrainName(circuit) + " ss站内会车（先到先走） " + he.train.getTrainName(circuit));
						events.add(new TrainEvent(myStopLine.time2, myStopLine.dist1, myStopLine.stop1.stationName, he.train.getTrainName(circuit), TrainEvent.MEET, TrainEvent.BOTH_STOP));
					}
					//后进后出
					else if(myArriveTime>hisArriveTime && myLeaveTime>hisLeaveTime && myArriveTime<=hisLeaveTime) {
//						System.out.println(myStopLine.stop1.stationName + " " + train.getTrainName(circuit) + " ss站内会车（后到后走） " + he.train.getTrainName(circuit));
						events.add(new TrainEvent(myStopLine.time1, myStopLine.dist1, myStopLine.stop1.stationName, he.train.getTrainName(circuit), TrainEvent.MEET, TrainEvent.BOTH_STOP));
					}
				}
				
				//遍历他的行车线
				for(int hisRunIndex=0; hisRunIndex<he.runLines.size(); hisRunIndex++) {
					RunLine hisRunLine = (RunLine) he.runLines.get(hisRunIndex);
					
					//计算他的行车线通过本站的时间
					int myStationDist = myStopLine.dist1;
					int hisPassTime = hisRunLine.getTimeOfTheDist(myStationDist);
					//他的行车线不经过我的停站
					if(hisPassTime < 0)
						continue;
					
					//他经过本站的时候我在站上（站内会车我停他不停：我让他）
					int myArriveTime = myStopLine.time1;
					int myLeaveTime = myStopLine.time2;
					if(myArriveTime<=hisPassTime && hisPassTime<=myLeaveTime) {
//						System.out.println(myStopLine.stop1.stationName + " " + train.getTrainName(circuit) + " sr站内会车（我停他不停，如果单线就是被踩） " + he.train.getTrainName(circuit));
						events.add(new TrainEvent(hisPassTime, myStopLine.dist1, myStopLine.stop1.stationName, he.train.getTrainName(circuit), TrainEvent.MEET, TrainEvent.ME_STOP));
					}
				}
			}
			//遍历我的行车线
			for(int myRunIndex=0; myRunIndex<runLines.size(); myRunIndex++) {
				RunLine myRunLine = (RunLine) runLines.get(myRunIndex);
				//遍历他的停车线
				for(int hisStopIndex=0; hisStopIndex<he.stopLines.size(); hisStopIndex++) {
					StopLine hisStopLine = (StopLine) he.stopLines.get(hisStopIndex);
					
					//计算我的行车线通过他所停靠车站的时间
					int hisStationDist = hisStopLine.dist1;
					int myPassTime = myRunLine.getTimeOfTheDist(hisStationDist);
					//我的行车线不经过他的停站
					if(myPassTime < 0)
						continue;
					
					//我经过这个停站时他在站上（站内会车他停我不停：他让我）
					int hisArriveTime = hisStopLine.time1;
					int hisLeaveTime = hisStopLine.time2;
					if(hisArriveTime<=myPassTime && myPassTime<=hisLeaveTime) {
//						System.out.println(hisStopLine.stop1.stationName + " " + train.getTrainName(circuit) + " rs站内会车（他停我不停，如果单线就是踩他） " + he.train.getTrainName(circuit));
						events.add(new TrainEvent(myPassTime, hisStopLine.dist1, hisStopLine.stop1.stationName, he.train.getTrainName(circuit), TrainEvent.MEET, TrainEvent.HE_STOP));
					}
				}
				
				//遍历他的行车线
				for(int hisRunIndex=0; hisRunIndex<he.runLines.size(); hisRunIndex++) {
					RunLine hisRunLine = (RunLine) he.runLines.get(hisRunIndex);
					
					//计算行车线的交点
					Point p = ChartLine.getIntersect(myRunLine, hisRunLine);
					
					//站外会车（双线很常见，单线则是错误的情况）
					if(p!=null) {
//						System.out.println(train.getTrainName(circuit) + " 站外会车 " + he.train.getTrainName(circuit) + myRunLine.stop1.stationName + myRunLine.stop2.stationName + hisRunLine.stop1.stationName + hisRunLine.stop2.stationName + Train.intToTrainTime(p.x) + p.y);
						events.add(new TrainEvent(p.x, p.y, myRunLine.stop1.stationName + "-" + myRunLine.stop2.stationName, he.train.getTrainName(circuit), TrainEvent.MEET, TrainEvent.NONE_STOP));
					}
				}
			}
		}
		
		return events;
	}
	
	// 车次切片：同向 越 让 领 跟
	public Vector<TrainEvent> getTrainEventsOfSameDir(TrainSlice he) {
		Vector<TrainEvent> events = new Vector<TrainEvent>();
		
		//同一趟车
		if(he.train.equals(train))
			return events;
		
		//同向的列车
		if(train.isDownTrain(circuit) == he.train.isDownTrain(circuit)) {
			//遍历我的停车线
			for(int myStopIndex=0; myStopIndex<stopLines.size(); myStopIndex++) {
				StopLine myStopLine = (StopLine) stopLines.get(myStopIndex);

				//如果我是始发或者终到 则不判断 “越 让 领 跟”
				if((train.getTerminalStation().equalsIgnoreCase(myStopLine.stop1.stationName)) ||
				   (train.getStartStation().equalsIgnoreCase(myStopLine.stop1.stationName)))
							continue;

				//遍历他的停车线（停车线对停车线共有4种情况）
				for(int hisStopIndex=0; hisStopIndex<he.stopLines.size(); hisStopIndex++) {
					StopLine hisStopLine = (StopLine) he.stopLines.get(hisStopIndex);
					
					int myArriveTime = myStopLine.time1;
					int myLeaveTime = myStopLine.time2;
					int hisArriveTime = hisStopLine.time1;
					int hisLeaveTime = hisStopLine.time2;
					
					if(!myStopLine.stop1.equals(hisStopLine.stop1))
						continue;
					
					//先进后出－－被踩
					if(myArriveTime<hisArriveTime && myLeaveTime>hisLeaveTime) {
						//如果他是始发站或者终到站就不算我被踩
						if((he.train.getTerminalStation().equalsIgnoreCase(hisStopLine.stop1.stationName)) ||
						   (he.train.getStartStation().equalsIgnoreCase(hisStopLine.stop1.stationName)))
							continue;
						
//						System.out.println(myStopLine.stop1.stationName + " " + train.getTrainName(circuit) + " ss被踩 " + he.train.getTrainName(circuit));
						events.add(new TrainEvent(hisStopLine.time2, myStopLine.dist1, myStopLine.stop1.stationName, he.train.getTrainName(circuit), TrainEvent.YIELD, TrainEvent.BOTH_STOP));
					}
					//后进先出－－踩他
					else if(myArriveTime>hisArriveTime && myLeaveTime<hisLeaveTime) {
						//如果我是始发或者终到站就不算踩他
						if((train.getTerminalStation().equalsIgnoreCase(myStopLine.stop1.stationName)) ||
						   (train.getStartStation().equalsIgnoreCase(myStopLine.stop1.stationName)))
									continue;

//						System.out.println(myStopLine.stop1.stationName + " " + train.getTrainName(circuit) + " ss踩他 " + he.train.getTrainName(circuit));
						events.add(new TrainEvent(myStopLine.time2, hisStopLine.dist1, hisStopLine.stop1.stationName, he.train.getTrainName(circuit), TrainEvent.EXCEED, TrainEvent.BOTH_STOP));
					}
					//先进先出，我走的时候他已经来了－－领跑
					else if(myArriveTime<hisArriveTime && myLeaveTime<hisLeaveTime && myLeaveTime>=hisArriveTime) {
//						System.out.println(myStopLine.stop1.stationName + " " + train.getTrainName(circuit) + " ss领跑 " + he.train.getTrainName(circuit));
						events.add(new TrainEvent(myStopLine.time2, myStopLine.dist1, myStopLine.stop1.stationName, he.train.getTrainName(circuit), TrainEvent.LEAD, TrainEvent.BOTH_STOP));
					}
					//后进后出，我来的时候他还没走－－跟随
					else if(myArriveTime>hisArriveTime && myLeaveTime>hisLeaveTime && myArriveTime<=hisLeaveTime) {
//						System.out.println(myStopLine.stop1.stationName + " " + train.getTrainName(circuit) + " ss跟随 " + he.train.getTrainName(circuit));
						events.add(new TrainEvent(myStopLine.time1, myStopLine.dist1, myStopLine.stop1.stationName, he.train.getTrainName(circuit), TrainEvent.FOLLOW, TrainEvent.BOTH_STOP));
					}
				}
				
				//遍历他的行车线（停车线对行车线，肯定是我被踩了）
				for(int hisRunIndex=0; hisRunIndex<he.runLines.size(); hisRunIndex++) {
					RunLine hisRunLine = (RunLine) he.runLines.get(hisRunIndex);
					
					//计算他的行车线通过本站的时间
					int myStationDist = myStopLine.dist1;
					int hisPassTime = hisRunLine.getTimeOfTheDist(myStationDist);
					//他的行车线不经过我的停站
					if(hisPassTime < 0)
						continue;
					
					//他经过本站的时候我在站上－－被踩
					//如果他是始发站或者终到站就不算我被踩
					if((he.train.getTerminalStation().equalsIgnoreCase(myStopLine.stop1.stationName)) ||
					   (he.train.getStartStation().equalsIgnoreCase(myStopLine.stop1.stationName)))
						continue;

					int myArriveTime = myStopLine.time1;
					int myLeaveTime = myStopLine.time2;
					if(myArriveTime<hisPassTime && hisPassTime<myLeaveTime) {
//						System.out.println(myStopLine.stop1.stationName + " " + train.getTrainName(circuit) + " sr被踩 " + he.train.getTrainName(circuit));
						events.add(new TrainEvent(hisPassTime, myStopLine.dist1, myStopLine.stop1.stationName, he.train.getTrainName(circuit), TrainEvent.YIELD, TrainEvent.ME_STOP));
					}
				}
			}
			//遍历我的行车线
			for(int myRunIndex=0; myRunIndex<runLines.size(); myRunIndex++) {
				RunLine myRunLine = (RunLine) runLines.get(myRunIndex);
				//遍历他的停车线（行车线对停车线，肯定是我踩他了）
				for(int hisStopIndex=0; hisStopIndex<he.stopLines.size(); hisStopIndex++) {
					StopLine hisStopLine = (StopLine) he.stopLines.get(hisStopIndex);
					
					//计算我的行车线通过他所停靠车站的时间
					int hisStationDist = hisStopLine.dist1;
					int myPassTime = myRunLine.getTimeOfTheDist(hisStationDist);
					//我的行车线不经过他的停站
					if(myPassTime < 0)
						continue;
					
					//我经过这个停站时他在站上－－踩他
					//如果我是始发或者终到站就不算踩他
					if((train.getTerminalStation().equalsIgnoreCase(hisStopLine.stop1.stationName)) ||
					   (train.getStartStation().equalsIgnoreCase(hisStopLine.stop1.stationName)))
								continue;
					int hisArriveTime = hisStopLine.time1;
					int hisLeaveTime = hisStopLine.time2;
					if(hisArriveTime<myPassTime && myPassTime<hisLeaveTime) {
//						System.out.println(hisStopLine.stop1.stationName + " " + train.getTrainName(circuit) + " rs踩他 " + he.train.getTrainName(circuit));
						events.add(new TrainEvent(myPassTime, hisStopLine.dist1, hisStopLine.stop1.stationName, he.train.getTrainName(circuit), TrainEvent.EXCEED, TrainEvent.HE_STOP));
					}
				}
				
				//遍历他的行车线
				for(int hisRunIndex=0; hisRunIndex<he.runLines.size(); hisRunIndex++) {
					RunLine hisRunLine = (RunLine) he.runLines.get(hisRunIndex);
					
					//计算行车线的交点
					Point p = ChartLine.getIntersect(myRunLine, hisRunLine);
					
					//站外越行
					if(p!=null) {
//						System.out.println(train.getTrainName(circuit) + " 站外越行 " + he.train.getTrainName(circuit) + myRunLine.stop1.stationName + myRunLine.stop2.stationName + hisRunLine.stop1.stationName + hisRunLine.stop2.stationName + Train.intToTrainTime(p.x) + p.y);
						events.add(new TrainEvent(p.x, p.y, myRunLine.stop1.stationName + "-" + myRunLine.stop2.stationName, he.train.getTrainName(circuit), TrainEvent.CROSS, TrainEvent.NONE_STOP));
					}
				}
			}
		}
		
		return events;
	}
	
	//车次切片：通、到、发
	public Vector<ChartEvent> getStationEvents() {
		Vector<ChartEvent> events = new Vector<ChartEvent>();
		
		//用车站横线扫描列车线
		for(int staIndex=0; staIndex<circuit.stationNum; staIndex++) {
			Station station = circuit.stations[staIndex];
			
			events.addAll(getStationEventsAt(station));
		}
		
		return events;
	}
	
	//本次列车在station站 上的事件
	public Vector<ChartEvent> getStationEventsAt(Station station) {
		Vector<ChartEvent> events = new Vector<ChartEvent>();
		Vector<ChartLine> sLines = getStopLineByDist(station.dist);
		
		//扫描到多于1条停车线：（同一趟列车用一个车次在本线路停两次？）
		if(sLines.size() > 1) {
			System.out.println("同一趟列车用一个车次在本线路停两次？");
		}
		//扫描到一条停车线
		else if(sLines.size() == 1) {
			StopLine line = (StopLine) sLines.get(0);
			
			//处理同距离隐藏站的情况
			if(!line.stop1.stationName.equalsIgnoreCase(station.name))
				return events;
			
			//到发点相同：始发、终到、图定通过
			if(line.time1 == line.time2) {
				//始发
				if(train.getStartStation().equalsIgnoreCase(line.stop1.stationName)) {
					events.add(new StationEvent(line.time1, station.dist, station.name, train.getTrainName(circuit), StationEvent.START, StationEvent.PASSENGER_STOP));
				}
				//终到
				else if(train.getTerminalStation().equalsIgnoreCase(line.stop1.stationName)) {
					events.add(new StationEvent(line.time2, station.dist, station.name, train.getTrainName(circuit), StationEvent.END, StationEvent.PASSENGER_STOP));
				}
				//图定通过
				else {
					events.add(new StationEvent(line.time1, station.dist, station.name, train.getTrainName(circuit), StationEvent.PASS, StationEvent.FIXED_STOP));
				}
			}
			//到发点不同：图定停靠
			else {
				int pType;
				//图定办客
				if(line.stop1.isPassenger) {
					pType = StationEvent.PASSENGER_STOP;
				}
				//图定停车不办客
				else {
					pType = StationEvent.FIXED_STOP;
				}
				events.add(new StationEvent(line.time1, station.dist, station.name, train.getTrainName(circuit), StationEvent.ARRIVE, pType));
				events.add(new StationEvent(line.time2, station.dist, station.name, train.getTrainName(circuit), StationEvent.LEAVE,  pType));
			}
		}
		//没有扫描到停车线，扫描行车线
		else {
			Vector<ChartLine> rLines = getRunLineByDist(station.dist);
			//确有两次通过一个车站的情况发生，所以要遍历所有行车线
			for(int lineIndex=0; lineIndex<rLines.size(); lineIndex++) {
				RunLine line = (RunLine) rLines.get(lineIndex);
				int time = line.getTimeOfTheDist(station.dist);
				events.add(new StationEvent(time, station.dist, station.name, train.getTrainName(circuit), StationEvent.PASS, StationEvent.CALCULATE_STOP));
			}
		}
		
		return events;
	}

//以时间步进的方式切片，算法复杂，效率低下，已经放弃
//	public Vector getStationEvents0() {
//		Vector events = new Vector();
//		
//		int lastDist = -1;
//		for(int time=0; time<1440; time++) {
//			Vector lines = getChartLinesByTime(time);
//			int curDist = circuit.getDistOfTrain(train, time);
//
//			if(curDist >= 0) {
////				System.out.println("(" + Train.intToTrainTime(time) + "," 
////							           + curDist + ") "
////							           + lines);
//				//只有一根线
//				if(lines.size() == 1) {
//					//一根运行线的时候判断是否通过了时刻表上没有的车站
//					if(lines.get(0) instanceof RunLine) {
//						if(!isDistOnTheLinesEnd(lines, curDist)) {
//							Station station = circuit.getStationBetweenTheDists(lastDist, curDist);
//							if(station!=null) {
////								System.out.println("(" + Train.intToTrainTime(time) + "," 
////								           + curDist + ") "
////								           + station.name + "站 通过（推算）T");
//								events.add(new StationEvent(time, curDist, station.name, train.getTrainName(circuit), StationEvent.PASS, StationEvent.CALCULATE_STOP));
//							}
//						}
//					}
//				}
//				//两根线的情况
//				else if(lines.size() == 2) {
//					//应该第一根是运行线，第二根是停站线
//					if(lines.get(0) instanceof RunLine && 
//					   lines.get(1) instanceof StopLine) {
//						
//						RunLine  rline = (RunLine)  lines.get(0);
//						StopLine sline = (StopLine) lines.get(1); 
//						int rl = rline.time1;
//						int ra = rline.time2;
//						int sa = sline.time1;
//						int sl = sline.time2;
//						String station = sline.stop1.stationName;
//						boolean isSchedular = sline.stop1.isPassenger;
//						
////						System.out.print("(" + Train.intToTrainTime(time) + "," 
////								           + curDist + ") " + station + "站");
//						int etype = 0;
//						if(sl == rl) {
////							System.out.print(" 发车");
//							etype = train.getStartStation().equalsIgnoreCase(sline.stop1.stationName)
//							      ? StationEvent.START : StationEvent.LEAVE;
//						}
//						else if(sa == ra) {
////							System.out.print(" 到达");
//							etype = train.getTerminalStation().equalsIgnoreCase(sline.stop1.stationName) 
//							      ? StationEvent.END : StationEvent.ARRIVE;
//						}
//						
//						int stype = 0;
//						if(isSchedular) {
////							System.out.println("（办客）KKK");
//							stype = StationEvent.PASSENGER_STOP;
//						}
//						else {
////							System.out.println("（图定）DD");
//							stype = StationEvent.FIXED_STOP;
//						}
//
//						events.add(new StationEvent(time, curDist, station, train.getTrainName(circuit), etype, stype));
//					}
//				}
//				//三根线的情况
//				else if(lines.size() ==3) {
//					//应该是两根运行线，一根停站线（到发点同）－－通过
//					if(lines.get(0) instanceof RunLine && 
//					   lines.get(1) instanceof RunLine && 
//					   lines.get(2) instanceof StopLine) {
//
//						StopLine sline = (StopLine) lines.get(2); 
//						String station = sline.stop1.stationName;
////						boolean isPassenger = sline.stop1.isPassenger;
////						System.out.print("(" + Train.intToTrainTime(time) + "," 
////						           + curDist + ") " + station + "站 通过");
////
////						System.out.println("（图定）DD");
//						
//						events.add(new StationEvent(time, curDist, station, train.getTrainName(circuit), StationEvent.PASS, StationEvent.FIXED_STOP));
//					}
//				}
//			}
//
//			lastDist = curDist;
//		}
//		
//		return events;
//	}
//
//	private boolean isDistOnTheLinesEnd(Vector lines, int dist) {
//		for(int i=0; i<lines.size(); i++) {
//			if (((ChartLine) lines.get(i)).isDistOnMyEnd(dist))
//				return true;
//		}
//		return false;
//	}
//	
//	public Vector getChartLinesByTime(int time) {
//		Vector found = new Vector();
//		
//		found.addAll(getRunLineByTime(time));
//		found.addAll(getStopLineByTime(time));
//		
//		return found;
//	}
	
//	public Vector getChartLinesByDist(int dist) {
//		Vector found = new Vector();
//		
//		found.addAll(getRunLineByDist(dist));
//		found.addAll(getStopLineByDist(dist));
//		
//		return found;
//	}
	
	private void buildup() {
		int lastDist = -1;
		int lastLeave_t = -1;
		Stop lastStop = null;
		for(int i=0; i<train.stopNum; i++) {
			Stop curStop = train.stops[i];
			int arrive_t = Train.trainTimeToInt(curStop.arrive);
			int leave_t  = Train.trainTimeToInt(curStop.leave);
			
			int curDist = -1;
			if(circuit.haveTheStation(curStop.stationName))
				curDist = circuit.getDistOfTrain(train, arrive_t);
			
			if(curDist >=0)
				stopLines.add(new StopLine(curStop, arrive_t, leave_t, curDist));
			
			if(curDist >= 0 && lastDist >= 0 && lastLeave_t >=0) 
				runLines.add(new RunLine(lastStop, curStop, lastLeave_t, arrive_t, lastDist, curDist));
			
			lastDist = curDist;
			lastLeave_t = leave_t;
			lastStop = curStop;
		}
	}
	
//	private Vector getRunLineByTime(int time) {
//		return ChartLine.getChartLineByTime(runLines, time);
//	}
	
	//运行线不考虑端点
	private Vector<ChartLine> getRunLineByDist(int dist) {
		return ChartLine.getChartLineByDistWithoutEnd(runLines, dist);
	}
	
//	private Vector getStopLineByTime(int time) {
//		return ChartLine.getChartLineByTime(stopLines, time);
//	}
	
	//停站线考虑端点
	private Vector<ChartLine> getStopLineByDist(int dist) {
		return ChartLine.getChartLineByDistWithEnd(stopLines, dist);
	}
}
