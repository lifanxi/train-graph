package org.paradise.etrc.data;

import java.io.*;

/**
 * @author lguo@sina.com
 * @version 1.0
 * 
 * 运行图的区间，可以是一整条线路，也可以是某条干线的一段
 * 一个circuit内的上下行应当一致（南京西－南京理解为下行） 
 * 距离一律以下行为递增方向，如沪宁线以南京（西）站为0坐标，沪杭线以上海站为0坐标
 */

public class Circuit {
	public static int MAX_STATION_NUM = 256;

	public String name = "";

	public int length = 0;

	public int stationNum = 0;

	public Station[] stations = new Station[MAX_STATION_NUM];

	public Circuit() {
	}

	public Circuit copy() {
		Circuit cir = new Circuit();

		cir.name = this.name;
		cir.length = this.length;
		cir.stationNum = this.stationNum;

		for (int i = 0; i < stationNum; i++)
			cir.stations[i] = this.stations[i].copy();

		return cir;
	}

	/**
	 * 在index前插入新的车站
	 * @param station Station
	 * @param index int
	 */
	public void insertStation(Station station, int index) {
		if ((index < 0) || (index >= MAX_STATION_NUM))
			return;

		Station[] newStations = new Station[MAX_STATION_NUM];

		int j = 0;
		for (int i = 0; i < index; i++) {
			newStations[j++] = stations[i];
		}

		newStations[j++] = station;

		for (int i = index; i < stationNum; i++) {
			newStations[j++] = stations[i];
		}

		stations = newStations;
		stationNum++;
	}

	/**
	 * 在最后添加新的停站station
	 * @param station Station
	 */
	public void appendStation(Station station) {
		Station[] newStations = new Station[MAX_STATION_NUM];

		int j = 0;
		for (int i = 0; i < stationNum; i++) {
			newStations[j++] = stations[i];
		}

		newStations[j++] = station;

		stations = newStations;
		stationNum++;
	}

	public void delStation(int index) {
		if ((index < 0) || (index >= MAX_STATION_NUM))
			return;

		Station[] newStations = new Station[MAX_STATION_NUM];

		int j = 0;
		for (int i = 0; i < index; i++) {
			newStations[j++] = stations[i];
		}

		for (int i = index + 1; i < stationNum; i++) {
			newStations[j++] = stations[i];
		}

		stations = newStations;
		stationNum--;
	}

	public void delStation(String name) {
		if(!haveTheStation(name))
			return;
		
		Station[] newStations = new Station[MAX_STATION_NUM];

		int j = 0;
		for (int i = 0; i < stationNum; i++) {
			if (!stations[i].name.equalsIgnoreCase(name)) {
				newStations[j++] = stations[i];
			}
		}

		stations = newStations;
		stationNum--;
	}
	
	public boolean haveTheStation(String theName) {
		for (int i = 0; i < stationNum; i++) {
			if (theName.equalsIgnoreCase(stations[i].name))
				return true;
		}

		return false;
	}

	public void loadFromFile(String file) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(file));

		String line;

		// 线路名
		if ((line = in.readLine()) != null) {
			this.name = line;
		} else {
			throw new IOException("线路名读取错");
		}

		// 线路总长
		if ((line = in.readLine()) != null) {
			String stLength = line;
			try {
				this.length = Integer.parseInt(stLength);
			} catch (NumberFormatException e) {
				throw new IOException("线路总长数据格式错");
			}
		} else {
			throw new IOException("线路总长读取错");
		}

		// 站点
		while ((line = in.readLine()) != null) {
			parseStationLine(line);
		}
	}

	/**
	 * 根据站名获取该站与线路起始站之间的距离 若该站不在本线路上则返回-1
	 * 
	 * @param stationName
	 *            String
	 * @return int
	 */
	public int getStationDist(String stationName) {
		for (int i = 0; i < stationNum; i++)
			if (stations[i].name.equalsIgnoreCase(stationName))
				return stations[i].dist;
		return -1;
	}

	/*
	 * public int[] getStationIndexs(ChartPanel chart) { int indexs[] = new
	 * int[stationNum]; for(int i = 0; i < stationNum; i++ } }
	 */

	public int getStationIndex(String stationName) {
		for (int i = 0; i < stationNum; i++) {
			// 被隐藏的站不返回
			if (stations[i].hide)
				continue;

			if (stations[i].name.equalsIgnoreCase(stationName))
				return i;
		}
		return -1;
	}

	// 查找下一个可停站，下行递增，上行递减
	public int getNextStationIndex(Train train, int index) {
		if (train.isDownTrain(this) == Train.DOWN_TRAIN) {
			for (int i = index + 1; i < stationNum; i++) {
				// 被隐藏的站不返回跳过
				if (stations[i].hide)
					continue;
				else
					return i;
			}
		} else {
			for (int i = index - 1; i > 0; i--) {
				// 被隐藏的站不返回跳过
				if (stations[i].hide)
					continue;
				else
					return i;
			}
		}
		return -1;
	}

	// 查找上一个可停站，上行递增，下行递减
	public int getPrevStationIndex(Train train, int index) {
		if (train.isDownTrain(this) == Train.UP_TRAIN) {
			for (int i = index + 1; i < stationNum; i++) {
				// 被隐藏的站不返回跳过
				if (stations[i].hide)
					continue;
				else
					return i;
			}
		} else {
			for (int i = index - 1; i > 0; i--) {
				// 被隐藏的站不返回跳过
				if (stations[i].hide)
					continue;
				else
					return i;
			}
		}
		return -1;
	}

	public int getStationIndex(int dist) {
		int gap[] = new int[stationNum];
		// 计算给定距离值与各站距离值之间的差
		for (int i = 0; i < stationNum; i++) {
			gap[i] = Math.abs(dist - stations[i].dist);
		}
		// 找出距离差最小的那一站
		int minIndex = 0;
		int minGap = gap[0];
		for (int i = 1; i < stationNum; i++) {
			// 被隐藏的站不参加比较
			if (stations[i].hide)
				continue;
			if (gap[i] < minGap) {
				minIndex = i;
				minGap = gap[i];
			}
		}

		return minIndex;
	}

	public String getStationName(int dist, boolean addSuffix) {
		int gap[] = new int[stationNum];
		// 计算给定距离值与各站距离值之间的差
		for (int i = 0; i < stationNum; i++) {
			gap[i] = Math.abs(dist - stations[i].dist);
		}
		// 找出距离差最小的那一站
		int minIndex = 0;
		int minGap = gap[0];
		for (int i = 1; i < stationNum; i++) {
			// 被隐藏的站不参加比较
			if (stations[i].hide)
				continue;
			if (gap[i] < minGap) {
				minIndex = i;
				minGap = gap[i];
			}
		}

		if (addSuffix) {
			if (minGap > 1)
				return stations[minIndex].name + "站附近";
			else
				return stations[minIndex].name + "站";
		} else
			return stations[minIndex].name;
	}

	public String getStationName(int dist) {
		return getStationName(dist, false);
	}

	public void writeTo(BufferedWriter out) throws IOException {
		out.write(name);
		out.newLine();
		out.write(length + "");
		out.newLine();
		for (int i = 0; i < stationNum; i++) {
			out.write(stations[i].name + "," + stations[i].dist + ","
					+ stations[i].level + "," + stations[i].hide);
			out.newLine();
		}
	}

	public void parseLine(String line, int lineNum) throws IOException {
		switch (lineNum) {
		case 0:
			this.name = line;
			break;
		case 1:
			try {
				this.length = Integer.parseInt(line);
			} catch (NumberFormatException e) {
				throw new IOException("线路总长数据格式错");
			}
			break;
		default:
			parseStationLine(line);
		}
	}

	private void parseStationLine(String line) throws IOException {
		String stStation[] = line.split(",");
		if (stStation.length < 2)
			throw new IOException("第" + (stationNum + 1) + "站数据有误");

		// 站名
		String stName = stStation[0];

		// 公里数
		int dist = 0;
		String stDist = stStation[1];
		try {
			dist = Integer.parseInt(stDist);
		} catch (NumberFormatException e) {
			throw new IOException(stName + "站公里数数据格式错");
		}

		// 等级，特等站为0；读不到的则认为是特等站
		int level = 0;
		if (stStation.length > 2) {
			String stLevel = stStation[2];
			try {
				level = Integer.parseInt(stLevel);
			} catch (NumberFormatException e) {
				throw new IOException(stName + "站等级数据格式错");
			}
		}

		// 是否隐藏，为1、t、true是隐藏，为0、f、false不隐藏；读不到则认为不隐藏
		boolean hide = false;
		if (stStation.length > 3) {
			String stHide = stStation[3];
			if (stHide.equals("1") || stHide.equalsIgnoreCase("t")
					|| stHide.equalsIgnoreCase("true"))
				hide = true;
			else if (stHide.equals("0") || stHide.equalsIgnoreCase("f")
					|| stHide.equalsIgnoreCase("false"))
				hide = false;
			else
				throw new IOException(stName + "站是否隐藏数据格式错");
		}

		Station st = new Station(stName, dist, level, hide);
		stations[stationNum] = st;
		stationNum++;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer(this.name);

		sb.append("共").append(this.stationNum).append("个车站，总长：").append(
				this.length).append("公里\n");

		for (int i = 0; i < this.stationNum; i++)
			sb.append(this.stations[i].name).append("站 距离：").append(
					this.stations[i].dist).append(" 等级:").append(
					this.stations[i].level).append(" 隐藏：").append(
					this.stations[i].hide).append("\n");

		return sb.toString();
	}

	public static void main(String argv[]) {
		Circuit c = new Circuit();
		try {
			c.loadFromFile("c:\\沪宁线.cir");
			System.out.println(c.name + "共" + c.stationNum + "个车站，总长："
					+ c.length);
			for (int i = 0; i < c.stationNum; i++)
				System.out.println(c.stations[i].name + "站" + " 距离："
						+ c.stations[i].dist + " 等级:" + c.stations[i].level
						+ " 隐藏：" + c.stations[i].hide);
		} catch (IOException ex) {
			System.out.println("Error:" + ex.getMessage());
		}
	}
}
