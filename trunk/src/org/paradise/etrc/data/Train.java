package org.paradise.etrc.data;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import java.awt.*;

import static org.paradise.etrc.ETRC._;

/**
 * @author lguo@sina.com
 * @version 1.0
 */

public class Train {
	public static int MAX_STOP_NUM = 100;

	public String trainNameDown = "";

	public String trainNameUp = "";
	
	/**
	 * LGuo 20070114 added
	 * 用于部分解决三车次以上的问题，暂时还不能存储
	 * 
	 * LGuo 20070119
	 * 解决存储问题，trf文件新版本
	 */
	public String trainNameFull = null;

//	改成getStartStation()方法，直接取stop[0]的站名
//	再次添加，以便加入不全的点单，但采用set方法，不直接存取，get方法优先取本值
	private String startStation = "";
//	改成getTerminalStation()方法，直接取stop[stopNum-1]的站名
//	再次添加，以便加入不全的点单，但采用set方法，不直接存取，get方法优先取本值
	private String terminalStation = "";

	public int stopNum = 0;

	public Stop[] stops = new Stop[MAX_STOP_NUM];

	public Color color = null;

	public Train() {
	}

	public Train copy() {
		Train tr = new Train();
		tr.color = color;
//		tr.startStation = startStation;
//		tr.terminalStation = terminalStation;
		tr.trainNameDown = trainNameDown;
		tr.trainNameUp = trainNameUp;
		tr.trainNameFull = trainNameFull;
		tr.stopNum = stopNum;
		for (int i = 0; i < stopNum; i++) {
			tr.stops[i] = stops[i].copy();
		}
		return tr;
	}
	
	public void setStartStation(String sta) {
		startStation = sta;
	}
	
	public void setTerminalStation(String sta) {
		terminalStation = sta;
	}
	
	public String getStartStation() {
		if(!startStation.equalsIgnoreCase(""))
			return startStation;
		else if(stopNum > 0)
			return stops[0].stationName;
		else
			return "";
	}
	
	public String getTerminalStation() {
		if(!terminalStation.equalsIgnoreCase(""))
			return terminalStation;
		else if(stopNum > 0)
			return stops[stopNum - 1].stationName;
		else
			return "";
	}

	/**
	 * 在index前插入新的停站stop
	 * @param stop Stop
	 * @param index int
	 */
	public void insertStop(Stop stop, int index) {
		if ((index < 0) || (index >= MAX_STOP_NUM))
			return;

		Stop[] newStops = new Stop[MAX_STOP_NUM];

		int j = 0;
		for (int i = 0; i < index; i++) {
			newStops[j++] = stops[i];
		}

		newStops[j++] = stop;

		for (int i = index; i < stopNum; i++) {
			newStops[j++] = stops[i];
		}

		stops = newStops;
		stopNum++;
	}

	/**
	 * 在最后添加新的停站stop
	 * @param stop Stop
	 */
	public void appendStop(Stop stop) {
		Stop[] newStops = new Stop[MAX_STOP_NUM];

		int j = 0;
		for (int i = 0; i < stopNum; i++) {
			newStops[j++] = stops[i];
		}

		newStops[j++] = stop;

		stops = newStops;
		stopNum++;
	}

	public void delStop(int index) {
		if ((index < 0) || (index >= MAX_STOP_NUM))
			return;

		Stop[] newStops = new Stop[MAX_STOP_NUM];

		int j = 0;
		for (int i = 0; i < index; i++) {
			newStops[j++] = stops[i];
		}

		for (int i = index + 1; i < stopNum; i++) {
			newStops[j++] = stops[i];
		}

		stops = newStops;
		stopNum--;
	}

	public void delStop(String name) {
		Stop[] newStops = new Stop[MAX_STOP_NUM];

		int j = 0;
		for (int i = 0; i < stopNum; i++) {
			if (!stops[i].stationName.equalsIgnoreCase(name)) {
				newStops[j++] = stops[i];
			}
		}

		stops = newStops;
		stopNum--;
	}

	public void loadFromFile(String file) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new BOMStripperInputStream(new FileInputStream(file)),"UTF-8"));

		String line;
		//车次
		if ((line = in.readLine()) != null) {
			paserTrainNameLine(line);
		} else {
			throw new IOException(_("Error reading train number."));
		}

		//始发站
		if ((line = in.readLine()) != null) {
			this.setStartStation(line);
		} else {
			throw new IOException(_("Error reading departure station."));
		}

		//终到站
		if ((line = in.readLine()) != null) {
			this.setTerminalStation(line);
		} else {
			throw new IOException(_("Error reading terminal station."));
		}

		//停站
		while ((line = in.readLine()) != null) {
			parseStopLine(line);
		}

		if (stopNum < 2)
			throw new IOException(_("Data incomplete in:" + file ));
	}
	
	public void writeTo(String fileName) throws IOException {
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
		
		this.writeTo(out);
		
		out.close();
	}

	public void writeTo(BufferedWriter out) throws IOException {
//		//旧版
//		//车次，需要判断上下行车次是否有空
//		if (trainNameDown.equalsIgnoreCase("")) {
//			out.write(trainNameUp);
//		} else if (trainNameUp.equalsIgnoreCase("")) {
//			out.write(trainNameDown);
//		} else {
//			out.write(trainNameDown + "," + trainNameUp);
//		}
		//新版本
		out.write("trf2," + getTrainName() + "," +
				  trainNameDown + "," +
				  trainNameUp);
		out.newLine();
		//始发站
		out.write(getStartStation());
		out.newLine();
		//终到站
		out.write(getTerminalStation());
		out.newLine();
		//停站
		for (int i = 0; i < stopNum; i++) {
			out.write(stops[i].stationName + ","
					+ stops[i].arrive + ","
					+ stops[i].leave + ","
					+ stops[i].isPassenger); //20070224新增，是否图定
			out.newLine();
		}
	}

	public void parseLine(String line, int lineNum) throws IOException {
		switch (lineNum) {
		case 0:
			paserTrainNameLine(line);
			break;
		case 1:
			this.setStartStation(line);
			break;
		case 2:
			this.setTerminalStation(line);
			break;
		default:
			parseStopLine(line);
		}
	}

	private void parseStopLine(String line) throws IOException {
//		SimpleDateFormat df = new SimpleDateFormat("H:mm");

		String stStop[] = line.split(",");
		if (stStop.length < 3)
			throw new IOException(String.format(_("Station %d data error in line %s"), (stopNum + 1), line));
		
		//20070224增加是否图定
		boolean isSchedular = true;
		if (stStop.length >= 4) {
			isSchedular = Boolean.valueOf(stStop[3]).booleanValue();
		}

		//站名
		String stName = stStop[0];

		//到点
		String stArrive = stStop[1];
//		Date arrive = new Date(0);
//		try {
//			arrive = df.parse(stArrive);
//		} catch (ParseException e) {
//			System.err.print("E");
//			throw new IOException(stName + "站到点读取错");
//		}

		//发点
		String stLeave = stStop[2];
//		Date leave = new Date(0);
//		try {
//			leave = df.parse(stLeave);
//		} catch (ParseException e) {
//			throw new IOException(stName + "站发点读取错");
//		}

		Stop stop = new Stop(stName, stArrive, stLeave, isSchedular);
		stops[stopNum] = stop;
		stopNum++;
	}

	private void paserTrainNameLine(String line) throws IOException {
		String trainName[] = line.split(",");
		
		//新版trf文件
		if (line.startsWith("trf2")) {
			trainNameFull = trainName[1];
			
			if(trainName.length == 4) {
				if(isDownName(trainName[2]))
					trainNameDown = trainName[2];
				else if(isUpName(trainName[2]))
					trainNameUp = trainName[2];

				if(isDownName(trainName[3]))
					trainNameDown = trainName[3];
				else if(isUpName(trainName[3]))
					trainNameUp = trainName[3];
			}
			else if(trainName.length == 3) {
				trainNameDown = isDownName(trainName[2]) ? trainName[2] : "";
				trainNameUp = isDownName(trainName[2]) ? "" : trainName[2];
			}
		}
		// 旧版trf文件
		else {
			if (trainName.length == 1) {
				trainNameDown = isDownName(trainName[0]) ? trainName[0] : "";
				trainNameUp = isDownName(trainName[0]) ? "" : trainName[0];
			} else if (trainName.length == 2) {
				trainNameDown = isDownName(trainName[0]) ? trainName[0]
						: trainName[1];
				trainNameUp = isDownName(trainName[0]) ? trainName[1]
						: trainName[0];
			} else
				throw new IOException("车次读取错");
		}
	}

	public static boolean isDownName(String trainName) {
		if (trainName.endsWith("1") || trainName.endsWith("3")
				|| trainName.endsWith("5") || trainName.endsWith("7")
				|| trainName.endsWith("9"))
			return true;

		return false;
	}

	public static boolean isUpName(String trainName) {
		if (trainName.endsWith("2") || trainName.endsWith("4")
				|| trainName.endsWith("6") || trainName.endsWith("8")
				|| trainName.endsWith("0"))
			return true;

		return false;
	}

	public String getTrainName() {
		//LGuo 20070114 added 如果有全称则返回全称（主要用于三车次以上以及AB车的情形）
		if (trainNameFull != null)
			return trainNameFull;
		else if (trainNameDown.trim().equalsIgnoreCase(""))
			return trainNameUp;
		else if (trainNameUp.trim().equalsIgnoreCase(""))
			return trainNameDown;
		else
			return trainNameDown.compareToIgnoreCase(trainNameUp) < 0 ? 
					trainNameDown + "/" + trainNameUp : 
					trainNameUp + "/" + trainNameDown;
	}

	public String getTrainName(Circuit c) {
		switch (isDownTrain(c)) {
		case DOWN_TRAIN:
			return trainNameDown == "" ? trainNameUp : trainNameDown;
		case UP_TRAIN:
			return trainNameUp == ""? trainNameDown : trainNameUp;
		default:
			return getTrainName();
		}
	}
	
	public void insertStopAfter(Stop afterStop, String newStopName,	String arrive, String leave, boolean isSchedular) {
		Stop newStop = new Stop(newStopName, arrive, leave, isSchedular);
		
		insertStopAfter(afterStop, newStop);
	}

	public void insertStopAfter(Stop afterStop, Stop newStop) {
		if(afterStop == null)
			insertStopAtFirst(newStop);
		
		Stop newStops[] = new Stop[MAX_STOP_NUM];
		int newStopNum = 0;
		for (int i = 0; i < stopNum; i++) {
			newStops[newStopNum] = stops[i];
			newStopNum++;

			if (stops[i].equals(afterStop)) {
				newStops[newStopNum] = newStop;
				newStopNum++;
			}
		}

		stops = newStops;
		stopNum = newStopNum;
	}

	private void insertStopAtFirst(Stop newStop) {
		Stop newStops[] = new Stop[MAX_STOP_NUM];
		int newStopNum = 0;
		
		newStops[0] = newStop;
		newStopNum++;
		for (int i = 0; i < stopNum; i++) {
			newStops[newStopNum] = stops[i];
			newStopNum++;
		}

		stops = newStops;
		stopNum = newStopNum;
	}

	public void replaceStop(String oldName, String newName) {
		for (int i = 0; i < stopNum; i++) {
			if (stops[i].stationName.equalsIgnoreCase(oldName))
				stops[i].stationName = newName;
		}
	}

	public void setArrive(String name, String _arrive) {
		for (int i = 0; i < stopNum; i++) {
			if (stops[i].stationName.equalsIgnoreCase(name))
				stops[i].arrive = _arrive;
		}
	}

	public void setLeave(String name, String _leave) {
		for (int i = 0; i < stopNum; i++) {
			if (stops[i].stationName.equalsIgnoreCase(name))
				stops[i].leave = _leave;
		}
	}

	public static final int UNKOWN = 0;

	public static final int DOWN_TRAIN = 1;

	public static final int UP_TRAIN = 2;

	public int isDownTrain(Circuit c) {
		return isDownTrain(c, true);
	}
	public int isDownTrain(Circuit c, boolean isGuessByTrainName) {
		int lastDist = -1;
		for (int i = 0; i < stopNum; i++) {
			int thisDist = c.getStationDist(stops[i].stationName);
			//当上站距离不为-1时，即经过本线路第二站时可以判断上下行
			if ((lastDist != -1) && (thisDist != -1)) {
				//本站距离大于上站距离，下行
				if (thisDist > lastDist)
					return DOWN_TRAIN;
				else
					return UP_TRAIN;
			}
			lastDist = thisDist;
		}
		//遍历完仍然未能确定
		return isGuessByTrainName?isDownTrainByTrainName(c):UNKOWN;
	}

	private int isDownTrainByTrainName(Circuit c) {
		String name = getTrainName();
		if((name.endsWith("1")) ||
		   (name.endsWith("3")) ||
		   (name.endsWith("5")) ||
		   (name.endsWith("7")) ||
		   (name.endsWith("9")))
			return Train.DOWN_TRAIN;
		else
			return Train.UP_TRAIN;
	}

	/**
	 * 取时间字符串
	 * @param time Date
	 * @return String
	 */
//	public static String toTrainFormat(Date time) {
//		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
//		return df.format(time);
//	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (!(obj instanceof Train))
			return false;

		return ((Train) obj).getTrainName().equalsIgnoreCase(
				this.getTrainName());
	}

	public String toString() {
		String strRt = getTrainName() + "次从" + getStartStation() + "到"
				+ getTerminalStation() + "，共经停" + stopNum + "个车站\r\n";

		for (int i = 0; i < stopNum; i++)
			strRt += stops[i].stationName + "站 "
					+ stops[i].arrive + " 到 "
					+ stops[i].leave + " 发\r\n";

		return strRt;
	}

	//测试用
	public static void main(String argv[]) {
		Train t = new Train();
		try {
			t.loadFromFile("c:\\N518_519_w.trf");

			System.out.println(t.getTrainName() + "次从" + t.getStartStation() + "到"
					+ t.getTerminalStation() + "，共经停" + t.stopNum + "个车站");
			for (int i = 0; i < t.stopNum; i++)
				System.out.println(t.stops[i].stationName + "站 "
						+ t.stops[i].arrive + " 到 "
						+ t.stops[i].leave + " 发");

			File f = new File("c:\\test_w.trf");
			BufferedWriter out = new BufferedWriter(new FileWriter(f));
			t.writeTo(out);
			out.flush();
			out.close();
		} catch (IOException ex) {
			System.out.println("Error:" + ex.getMessage());
		}
	}

	/**
	 * getNextStopName
	 *
	 * @param string String
	 * @return int
	 */
	public String getNextStopName(String stopName) {
		int i = 0;
		for (i = 0; i < stopNum; i++) {
			if (stops[i].stationName.equalsIgnoreCase(stopName))
				break;
		}
		if (i < stopNum - 1)
			return stops[i + 1].stationName;
		else
			return null;
	}

	public String getPrevStopName(String stopName) {
		int i = 0;
		for (i = 0; i < stopNum; i++) {
			if (stops[i].stationName.equalsIgnoreCase(stopName))
				break;
		}
		if (i > 1)
			return stops[i - 1].stationName;
		else
			return null;
	}
	
	public static Color getTrainColorByName(String trainName) {
		char type = trainName.toUpperCase().charAt(0);
		switch(type) {
		case 'G':
			return new Color(255, 0, 190);
		case 'Z':
		case 'D':
		case 'C':
			return new Color(128, 0, 128);
		case 'T':
			return Color.BLUE;
		case 'K':
		case 'N':
			return Color.RED;
		case 'L':
		case 'A':
			return new Color(128, 64, 0);
		default:
			return new Color(0, 128, 0);
		}
	}
	
	public Color getDefaultColor() {
		return getTrainColorByName(getTrainName());
	}

	public static String makeFullName(String[] names) {
		String name1 = "ZZZ";
		String name2 = "ZZZ";
		String name3 = "ZZZ";
		String name4 = "ZZZ";

		for(int i=0; i<names.length; i++) {
			String theName = (String) names[i];
			if(theName.compareTo(name1) < 0) {
				name4 = name3;
				name3 = name2;
				name2 = name1;
				name1 = theName;
			}
			else if(theName.compareTo(name2) < 0) {
				name4 = name3;
				name3 = name2;
				name2 = theName;
			}
			else if(theName.compareTo(name3) < 0) {
				name4 = name3;
				name3 = theName;
			}
			else if(theName.compareTo(name4) < 0)
				name4 = theName;
		}
		
		String name = name1;
		if(!name2.equals("ZZZ"))
			name += "/" + name2;
		if(!name3.equals("ZZZ"))
			name += "/" + name3;
		if(!name4.equals("ZZZ"))
			name += "/" + name4;
		
		return name;
	}
	
	public static String makeFullName(Vector<String> names) {
		return makeFullName((String[]) names.toArray());
	}

	//格式化输入的时间，如果格式有误则用原来的时间
	//时分间隔可以用空格（任意多个），全角或者半角的分号、句号
	//当输入3位或者4位纯数字时解析为后两位分钟，前一、两位小时
	public static String formatTime(String oldTime, String input) {
		input = input.trim();
		
		//允许为空
		if(input.equals(""))
			return input;

		input = input.replaceAll(" ", "");
		if(input.length() == 3 
		   && input.charAt(0) >= '0' && input.charAt(0) <= '9'
		   && input.charAt(1) >= '0' && input.charAt(1) <= '9'
		   && input.charAt(2) >= '0' && input.charAt(2) <= '9') {
			
			input = "0" + input.charAt(0) + ":" 
			       + input.charAt(1) + input.charAt(2);
		}
		else if(input.length() == 4 
				   && input.charAt(0) >= '0' && input.charAt(0) <= '9'
				   && input.charAt(1) >= '0' && input.charAt(1) <= '9'
				   && input.charAt(2) >= '0' && input.charAt(2) <= '9'
				   && input.charAt(3) >= '0' && input.charAt(3) <= '9') {
					
			input = "" + input.charAt(0) + input.charAt(1) + ":" 
				      + input.charAt(2) + input.charAt(3);
		}
		else {
			input = input.replace('：', ':');
			input = input.replace('；', ':');
			input = input.replace('，', ':');
			input = input.replace('。', ':');
			input = input.replace(';', ':');
			input = input.replace(',', ':');
			input = input.replace('.', ':');
		}

		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		
		Date date = null;
		try {
			date = df.parse(input);
		} catch (ParseException e) {
		}
		
		//解析不成功则返回原来的时间，解析成功则返回标准格式的时间
		if(date == null)
			return oldTime;
		else
			return df.format(date);
	}
	
	public static String[] formatName(String input) {
		input = input.trim();

		input = input.replace('\\', '/');

		input = input.replace('、', '/');
		
		input = input.replace('，', '/');
		input = input.replace(',', '/');
		
		input = input.replace('。', '/');			
		input = input.replace('.', '/');

		String[] names = input.split("/");
		if(names.length > 4)
			return null;
		
		String[] myNames = new String[names.length];
		myNames[0] = names[0].toUpperCase();
		for(int i=1; i<names.length; i++) {
			if(names[i].length() <=2 && myNames[0].length() > names[i].length()) {
				myNames[i] = (myNames[0].substring(0, myNames[0].length() - names[i].length()) + names[i]).toUpperCase();
			}
			else {
				myNames[i] = names[i].toUpperCase();
			}
		}
		
		return myNames;
	}

	//工具方法
	public static int trainTimeToInt(String strTime) {
		String strH = strTime.split(":")[0];
		String strM = strTime.split(":")[1];
		
		int h = Integer.parseInt(strH);
		int m = Integer.parseInt(strM);
		
		return h*60 + m;
	}

	public static String intToTrainTime(int minutes) {
		int hours = minutes < 0 ? -1 : minutes / 60;

		int clockMinute = minutes - hours * 60;
		if (clockMinute < 0)
			clockMinute += 60;

		String strMinute = clockMinute < 10 ? "0" + clockMinute : "" + clockMinute;

		int clockHour = hours;
		if (clockHour < 0)
			clockHour += 24;
		if (clockHour >= 24)
			clockHour -= 24;

		String strHour = clockHour < 10 ? "0" + clockHour : "" + clockHour;

		return strHour + ":" + strMinute;
	}

	public boolean hasStop(String staName) {
		for (int i = 0; i < stopNum; i++) {
			if (stops[i].stationName.equalsIgnoreCase(staName))
				return true;
		}
		
		return false;
	}
	
	public int findStopIndex(String staName) {
		for (int i = 0; i < stopNum; i++) {
			if (stops[i].stationName.equalsIgnoreCase(staName))
				return i;
		}
		
		return -1;
	}

	public Stop findStop(String staName) {
		for (int i = 0; i < stopNum; i++) {
			if (stops[i].stationName.equalsIgnoreCase(staName))
				return stops[i];
		}
		
		return null;
	}

	public void setTrainNames(String[] myNames) {
		trainNameFull = makeFullName(myNames);
		for(int i=0; i<myNames.length; i++) {
			if(isDownName(myNames[i]))
				trainNameDown = myNames[i];
			else if(isUpName(myNames[i]))
				trainNameUp = myNames[i];
		}
	}

	public static int getDefaultVelocityByName(String trainName) {
		char type = trainName.toUpperCase().charAt(0);
		switch(type) {
		case 'G':
			return 250;
		case 'D':
		case 'Z':
		case 'C':
			return 120;
		case 'T':
			return 100;
		case 'K':
		case 'N':
			return 85;
		case 'L':
		case 'A':
			return 60;
		default:
			return 70;
		}
	}

}
