package org.paradise.etrc.data;

import java.io.*;
import java.text.*;
import java.util.*;

import java.awt.*;

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

	public String startStation = "";

	public String terminalStation = "";

	public int stopNum = 0;

	public Stop[] stops = new Stop[MAX_STOP_NUM];

	public Color color = null;

	public Train() {
	}

	public Train copy() {
		Train tr = new Train();
		tr.color = color;
		tr.startStation = startStation;
		tr.terminalStation = terminalStation;
		tr.trainNameDown = trainNameDown;
		tr.trainNameUp = trainNameUp;
		tr.trainNameFull = trainNameFull;
		tr.stopNum = stopNum;
		for (int i = 0; i < stopNum; i++) {
			tr.stops[i] = stops[i].copy();
		}
		return tr;
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
		BufferedReader in = new BufferedReader(new FileReader(file));

		String line;
		//车次
		if ((line = in.readLine()) != null) {
			paserTrainNameLine(line);
		} else {
			throw new IOException("车次读取错");
		}

		//始发站
		if ((line = in.readLine()) != null) {
			this.startStation = line;
		} else {
			throw new IOException("始发站读取错");
		}

		//终到站
		if ((line = in.readLine()) != null) {
			this.terminalStation = line;
		} else {
			throw new IOException("终到读取错");
		}

		//停站
		while ((line = in.readLine()) != null) {
			parseStopLine(line);
		}

		if (stopNum < 2)
			throw new IOException("车次不完整");
	}
	
	public void writeTo(String fileName) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
		
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
		out.write(startStation);
		out.newLine();
		//终到站
		out.write(terminalStation);
		out.newLine();
		//停站
		for (int i = 0; i < stopNum; i++) {
			out.write(stops[i].stationName + ","
					+ toTrainFormat(stops[i].arrive) + ","
					+ toTrainFormat(stops[i].leave));
			out.newLine();
		}
	}

	public void parseLine(String line, int lineNum) throws IOException {
		switch (lineNum) {
		case 0:
			paserTrainNameLine(line);
			break;
		case 1:
			startStation = line;
			break;
		case 2:
			terminalStation = line;
			break;
		default:
			parseStopLine(line);
		}
	}

	private void parseStopLine(String line) throws IOException {
		SimpleDateFormat df = new SimpleDateFormat("H:mm");

		String stStop[] = line.split(",");
		if (stStop.length < 3)
			throw new IOException("第" + (stopNum + 1) + "站数据有误=>" + line);

		//站名
		String stName = stStop[0];

		//到点
		String stArrive = stStop[1];
		Date arrive = new Date(0);
		try {
			arrive = df.parse(stArrive);
		} catch (ParseException e) {
			System.err.print("E");
			throw new IOException(stName + "站到点读取错");
		}

		//发点
		String stLeave = stStop[2];
		Date leave = new Date(0);
		try {
			leave = df.parse(stLeave);
		} catch (ParseException e) {
			throw new IOException(stName + "站发点读取错");
		}

		Stop stop = new Stop(stName, arrive, leave);
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

	public void insertStopAfter(Stop afterStop, String newStopName,
			Date arrive, Date leave) {
		Stop newStop = new Stop(newStopName, arrive, leave);
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

	public void replaceStop(String oldName, String newName) {
		for (int i = 0; i < stopNum; i++) {
			if (stops[i].stationName.equalsIgnoreCase(oldName))
				stops[i].stationName = newName;
		}
	}

	public void setArrive(String name, Date _arrive) {
		for (int i = 0; i < stopNum; i++) {
			if (stops[i].stationName.equalsIgnoreCase(name))
				stops[i].arrive = _arrive;
		}
	}

	public void setLeave(String name, Date _leave) {
		for (int i = 0; i < stopNum; i++) {
			if (stops[i].stationName.equalsIgnoreCase(name))
				stops[i].leave = _leave;
		}
	}

	public static final int UNKOWN = 0;

	public static final int DOWN_TRAIN = 1;

	public static final int UP_TRAIN = 2;

	public int isDownTrain(Circuit c) {
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
		return UNKOWN;
	}

	/**
	 * 取时间字符串
	 * @param time Date
	 * @return String
	 */
	public static String toTrainFormat(Date time) {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		return df.format(time);
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (!(obj instanceof Train))
			return false;

		return ((Train) obj).getTrainName().equalsIgnoreCase(
				this.getTrainName());
	}

	public String toString() {
		String strRt = getTrainName() + "次从" + startStation + "到"
				+ terminalStation + "，共经停" + stopNum + "个车站\r\n";

		for (int i = 0; i < stopNum; i++)
			strRt += stops[i].stationName + "站 "
					+ Train.toTrainFormat(stops[i].arrive) + " 到 "
					+ Train.toTrainFormat(stops[i].leave) + " 发\r\n";

		return strRt;
	}

	//测试用
	public static void main(String argv[]) {
		Train t = new Train();
		try {
			t.loadFromFile("c:\\N518_519_w.trf");

			System.out.println(t.getTrainName() + "次从" + t.startStation + "到"
					+ t.terminalStation + "，共经停" + t.stopNum + "个车站");
			for (int i = 0; i < t.stopNum; i++)
				System.out.println(t.stops[i].stationName + "站 "
						+ Train.toTrainFormat(t.stops[i].arrive) + " 到 "
						+ Train.toTrainFormat(t.stops[i].leave) + " 发");

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
	
	public Color getDefaultColor() {
		char type = getTrainName().toUpperCase().charAt(0);
		switch(type) {
		case 'Z':
			return Color.MAGENTA;
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
}
