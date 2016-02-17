package org.paradise.etrc.tools;

import java.sql.*;

import org.paradise.etrc.tools.db.DBPrice;

public class STDB {

	// The connect string 
	static final String connect_string = "jdbc:oracle:thin:train/suteng@localhost:1521:stdb";

	// The connection to the database
	private Connection conn;
	private Statement stmt;
	
	public void connectDB() throws SQLException {
		if (conn == null) {
			// Load the JDBC driver
			DriverManager.registerDriver(new oracle.jdbc.OracleDriver());

			// Connect to the databse
			System.out.println("Connecting to " + connect_string);
			conn = DriverManager.getConnection(connect_string);
			System.out.println("Connected\n");
			
			stmt = conn.createStatement();
		}
	}
	
	public void closeDB() throws SQLException {
		conn.close();
	}
	
	public void insertTrain(int id, String trainName, int type, int level) throws SQLException {		
		final String insertSQL = "insert into train values ("
			+ id + ", '"
			+ trainName + "', "
			+ type + ", "
			+ level+")";
		
		System.out.println("Executing insert train: " + trainName);
		stmt.executeQuery(insertSQL);
	}
	
	public void insertStop2(int trainID, String singleName, String stationName, String arrive, String leave, int distance) throws SQLException {
		if(stationName.length() > 8)
			stationName = stationName.substring(0, 8);
		
		final String insertSQL = "insert into stop2 values("
			+ trainID + ", '"
			+ singleName + "', '"
			+ stationName + "', '"
			+ arrive + "', '"
			+ leave + "', "
			+ distance + ")";
		
//		System.out.println("Executing insert stop2: " + insertSQL + "\n");
		stmt.executeQuery(insertSQL);
	}
	
	public void insertStop(int trainID, int stationID, String arrive, String leave, int distance) throws SQLException {
		final String insertSQL = "insert into stop values("
			+ trainID + ", "
			+ stationID + ", '"
			+ arrive + "', '"
			+ leave + "', "
			+ distance + ")";
		
		//System.out.println("Executing insert stop: " + insertSQL + "\n");
		stmt.executeQuery(insertSQL);
	}
	
	public void insertTrainIndex(String singleName, int index) throws SQLException {
		final String insertSQL = "insert into trainIndex values('"
			+ singleName + "', "
			+ index + ")";
		
		System.out.println("Executing insert trainIndex: " + singleName);
		stmt.executeQuery(insertSQL);
	}
	
	public void insertStation(int stationID, String stationName) throws SQLException {
		final String insertSQL = "insert into station values("
			+ stationID + ", '"
			+ stationName + "')";
		
		System.out.println("Executing insert staion: " + stationName);
		stmt.executeQuery(insertSQL);
	}

	public void insertPJB(DBPrice price) throws SQLException {
		final String insertSQL = "insert into price values("
			+ price.type + ", "
			+ price.level + ", "
			+ price.distFrom + ", "
			+ price.distTo + ", "
			+ price.yingzuo + ", "
			+ price.ruanzuo + ", "
			+ price.yingwoU + ", "
			+ price.yingwoM + ", "
			+ price.yingwoD + ", "
			+ price.ruanwoU + ", "
			+ price.ruanwoD + ")";
		
		System.out.println("Executing insert price: " + price);
		stmt.executeQuery(insertSQL);
	}
	
	public void ccQuery(String singleName) throws SQLException {
		singleName = singleName.split("/")[0];

		final String querySQL = 
			"select train.TRAIN_NAME, station.STATION_NAME, stop.ARRIVE_TIME, stop.LEAVE_TIME, stop.DISTANCE " + 
			"from stop, station, train, trainIndex " +
			"where trainIndex.SINGLE_NAME = '" + singleName + "' and " + 
				  "trainIndex.TRAIN_ID = train.TRAIN_ID and " + 
				  "train.TRAIN_ID = stop.TRAIN_ID and " +
				  "stop.STATION_ID = station.STATION_ID " +
			"order by stop.DISTANCE";
		
		System.out.println(querySQL);
		ResultSet res = stmt.executeQuery(querySQL);
		int i = 0;
		while(res.next()){
			String trainName = res.getString(1);
			String stationName = res.getString(2);
			String arriveTime = res.getString(3);
			String leaveTime = res.getString(4);
			int dist = res.getInt(5);
			System.out.println(i + ". " + stationName + " " + trainName + " " + arriveTime + " " + leaveTime + " " + dist);
			i++;
		}
	}
	
	public void zzQuery(String fromStation, String toStation) throws SQLException {
		if(fromStation == null || toStation == null)
			return;
		if(fromStation.trim().equalsIgnoreCase("") && toStation.trim().equalsIgnoreCase(""))
			return;
		if(fromStation.trim().equalsIgnoreCase(""))
			zzQuery1(toStation);
		else if(toStation.trim().equalsIgnoreCase(""))
			zzQuery1(fromStation);
		else
			zzQuery2(fromStation, toStation);
	}
	
	private void zzQuery1(String station) throws SQLException {
		zzQuery1A(station);
		zzQuery1B(station);
		zzQuery1C(station);
	}

	private void zzQuery1A(String station) throws SQLException {
		final String querySQL = 
			"select train.TRAIN_NAME, st.ARRIVE_TIME, st.LEAVE_TIME " + 
			   "from stop st, station sta, train " + 
			   "where sta.STATION_NAME = '" + station + "' " + 
			   		 "and sta.STATION_ID = st.STATION_ID " + 
					 "and train.TRAIN_ID = st.TRAIN_ID " + 
					 "and st.ARRIVE_TIME = '  :  ' " + 
					 "order by st.LEAVE_TIME";
					 
		System.out.println(querySQL);
		ResultSet res = stmt.executeQuery(querySQL);
		int i = 0;
		while(res.next()){
			String trainName = res.getString(1);
			String arriveTime = " - ";
			String leaveTime = res.getString(3);
			System.out.println(i + ". " + trainName + " " + arriveTime + " " + leaveTime + " - ");
			i++;
		}
	}

	private void zzQuery1B(String station) throws SQLException {
		final String querySQL = 
			"select train.TRAIN_NAME, st.ARRIVE_TIME, st.LEAVE_TIME, st.DISTANCE " + 
			   "from stop st, station sta, train " + 
			   "where sta.STATION_NAME = '" + station + "' " + 
			   		 "and sta.STATION_ID = st.STATION_ID " + 
					 "and train.TRAIN_ID = st.TRAIN_ID " + 
					 "and st.LEAVE_TIME = '  :  ' " + 
					 "order by st.ARRIVE_TIME";
				 			 
		System.out.println(querySQL);
		ResultSet res = stmt.executeQuery(querySQL);
		int i = 0;
		while(res.next()){
			String trainName = res.getString(1);
			String arriveTime = res.getString(2);
			String leaveTime = " - ";
			System.out.println(i + ". " + trainName + " " + arriveTime + " " + leaveTime + " - ");
			i++;
		}
	}

	private void zzQuery1C(String station) throws SQLException {
		final String querySQL = 
			"select train.TRAIN_NAME, st.ARRIVE_TIME, st.LEAVE_TIME, st.DISTANCE " + 
			   "from stop st, station sta, train " + 
			   "where sta.STATION_NAME = '" + station + "' " + 
			   		 "and sta.STATION_ID = st.STATION_ID " + 
					 "and train.TRAIN_ID = st.TRAIN_ID " + 
					 "and st.LEAVE_TIME <> '  :  ' " + 
					 "and st.ARRIVE_TIME <> '  :  ' " + 
					 "order by st.ARRIVE_TIME";
		
		System.out.println(querySQL);
		ResultSet res = stmt.executeQuery(querySQL);
		int i = 0;
		while(res.next()){
			String trainName = res.getString(1);
			String arriveTime = res.getString(2);
			String leaveTime = res.getString(3);
			System.out.println(i + ". " + trainName + " " + arriveTime + " " + leaveTime + " - ");
			i++;
		}
	}

	private void zzQuery2(String fromStation, String toStation) throws SQLException {
		final String querySQL = 
			"select train.TRAIN_NAME, st1.LEAVE_TIME, st2.ARRIVE_TIME, price.YINGZUO " +
				"from stop st1, stop st2, station sta1, station sta2, train, price " +
	            "where st1.STATION_ID = sta1.STATION_ID and sta1.STATION_NAME = '" + fromStation + "' and " +
	                  "st2.STATION_ID = sta2.STATION_ID and sta2.STATION_NAME = '" + toStation +"' and " +
	                  "train.TRAIN_ID = st1.TRAIN_ID and " + 
	                  "train.TRAIN_ID = st2.TRAIN_ID and " +
	                  "st1.DISTANCE < st2.DISTANCE and " + 
	        		  "price.DISTANCE_FROM <= (st2.DISTANCE - st1.DISTANCE) and " + 
	        		  "price.DISTANCE_TO   >= (st2.DISTANCE - st1.DISTANCE) and " +
	        		  "price.CAR_TYPE = train.CAR_TYPE and " +
	        		  "price.TRAIN_LEVEL = train.TRAIN_LEVEL " +
	        		  "order by st1.LEAVE_TIME";
		
		System.out.println(querySQL);
		ResultSet res = stmt.executeQuery(querySQL);
		int i = 0;
		while(res.next()){
			String trainName = res.getString(1);
			String leaveTime = res.getString(2);
			
			if(leaveTime.equalsIgnoreCase("  :  "))
				leaveTime = " - ";
			
			String arriveTime = res.getString(3);
			if(arriveTime.equalsIgnoreCase("  :  "))
				arriveTime = " - ";
			
			int price = res.getInt(4);
			
			System.out.println(i + ". " + trainName + " " + leaveTime + " " + arriveTime + " " + price/10);
			i++;
		}
	}
	
	public void xqQuery(String trainName, String fromStation, String toStation) throws SQLException {
		if(fromStation == null || toStation == null)
			return;
		if(fromStation.trim().equalsIgnoreCase("") && toStation.trim().equalsIgnoreCase(""))
			return;
		if(fromStation.trim().equalsIgnoreCase(""))
			xqQuery1(trainName, toStation);
		else if(toStation.trim().equalsIgnoreCase(""))
			xqQuery1(trainName, fromStation);
		else
			xqQuery2(trainName, fromStation, toStation);
	}
	
	private void xqQuery1(String trainName, String station) throws SQLException {
		final String querySQL = 
			"select st2.DISTANCE - st1.DISTANCE, st1.ARRIVE_TIME, st1.LEAVE_TIME, st2.ARRIVE_TIME, price.*, " + 
			   "sta1.STATION_NAME, sta2.STATION_NAME, st0.ARRIVE_TIME, st0.LEAVE_TIME " + 
			   "from stop st0, stop st1, stop st2, station sta0, station sta1, station sta2, train, price " + 
			   "where sta0.STATION_NAME = '" + station + "' " + 
			   		 "and st0.STATION_ID = sta0.STATION_ID " + 
			   		 "and st1.STATION_ID = sta1.STATION_ID " + 
					 "and st2.STATION_ID = sta2.STATION_ID " + 
					 "and train.TRAIN_NAME = '" + trainName + "' " + 
					 "and train.TRAIN_ID = st0.TRAIN_ID " + 
					 "and train.TRAIN_ID = st1.TRAIN_ID " + 
					 "and train.TRAIN_ID = st2.TRAIN_ID " + 
					 "and st1.ARRIVE_TIME = '  :  ' " + 
					 "and st2.LEAVE_TIME = '  :  ' " + 
					 "and price.DISTANCE_FROM <= (st2.DISTANCE - st1.DISTANCE) " + 
					 "and price.DISTANCE_TO   >= (st2.DISTANCE - st1.DISTANCE) " + 
					 "and price.CAR_TYPE = train.CAR_TYPE  " + 
					 "and price.TRAIN_LEVEL = train.TRAIN_LEVEL";
		
		System.out.println(querySQL);
		ResultSet res = stmt.executeQuery(querySQL);
		while(res.next()){
			int dist = res.getInt(1);
//			String arriveTimeFrom = res.getString(2);
			String leaveTimeFrom = res.getString(3);
			String arriveTimeTo = res.getString(4);
			int type = res.getInt(5);
			int level = res.getInt(6);
			int yingzuo = res.getInt(9);
			int ruanzuo = res.getInt(10);
			int yingwoU = res.getInt(11);
			int yingwoM = res.getInt(12);
			int yingwoD = res.getInt(13);
			int ruanwoU = res.getInt(14);
			int ruanwoD = res.getInt(15);
			
			String fromStation = res.getString(16);
			String toStation = res.getString(17);
			String myArriveTime = res.getString(18);
			String myLeaveTime = res.getString(19);
			
			String trainType = "";
			if(type == 1)
				trainType = "新空";
			switch(level) {
			case 0: trainType += "普客"; break;
			case 1: trainType += "普快"; break;
			case 2: trainType += "快速"; break;
			case 3: trainType += "特快"; break;
			case 4: trainType += "直特"; break;
			}
			
			System.out.println(trainName + "次" + trainType +"列车");
			System.out.println(fromStation + "站" + leaveTimeFrom + "始发");
			System.out.println(toStation + "站" + arriveTimeTo + "终到");
			System.out.println("全程行驶距离" + dist + "公里");
			
			System.out.println("全程硬座" + yingzuo/10 + "，软座" + ruanzuo/10);
			if(yingwoM != -10)
				System.out.println("全程硬卧上" + yingwoU/10 + "，中" + yingwoM/10 + "，下" + yingwoD/10);
			if(ruanwoD != -10)
				System.out.println("全程软卧上" + ruanwoU/10 + "，下" + ruanwoD/10);
			
			if(!(myArriveTime.equalsIgnoreCase("  :  ") || myLeaveTime.equalsIgnoreCase("  :  "))) 
				System.out.println(station + "站" 
						           + myArriveTime + "到达，" 
						           + myLeaveTime + "开车");
		}
	}

	private void xqQuery2(String trainName, String fromStation, String toStation) throws SQLException {
		final String querySQL = 
			"select st2.DISTANCE - st1.DISTANCE, st1.ARRIVE_TIME, st1.LEAVE_TIME, st2.ARRIVE_TIME, price.* " +
			"from stop st1, stop st2, station sta1, station sta2, train, price " +
			"where st1.STATION_ID = sta1.STATION_ID and sta1.STATION_NAME = '" + fromStation + "' and " +
				  "st2.STATION_ID = sta2.STATION_ID and sta2.STATION_NAME = '" + toStation + "' and " +
				  "train.TRAIN_NAME = '" + trainName + "' and " + 
				  "train.TRAIN_ID = st1.TRAIN_ID and " +
				  "train.TRAIN_ID = st2.TRAIN_ID and " +
				  "st1.DISTANCE < st2.DISTANCE and " +
				  "price.DISTANCE_FROM <= (st2.DISTANCE - st1.DISTANCE) and " +
				  "price.DISTANCE_TO   >= (st2.DISTANCE - st1.DISTANCE) and " +
				  "price.CAR_TYPE = train.CAR_TYPE and " +
				  "price.TRAIN_LEVEL = train.TRAIN_LEVEL";

		System.out.println(querySQL);
		ResultSet res = stmt.executeQuery(querySQL);
		while(res.next()){
			int dist = res.getInt(1);
			String arriveTimeFrom = res.getString(2);
			String leaveTimeFrom = res.getString(3);
			String arriveTimeTo = res.getString(4);
			int type = res.getInt(5);
			int level = res.getInt(6);
			int yingzuo = res.getInt(9);
			int ruanzuo = res.getInt(10);
			int yingwoU = res.getInt(11);
			int yingwoM = res.getInt(12);
			int yingwoD = res.getInt(13);
			int ruanwoU = res.getInt(14);
			int ruanwoD = res.getInt(15);
			
			String trainType = "";
			if(type == 1)
				trainType = "新空";
			switch(level) {
			case 0: trainType += "普客"; break;
			case 1: trainType += "普快"; break;
			case 2: trainType += "快速"; break;
			case 3: trainType += "特快"; break;
			case 4: trainType += "直特"; break;
			}
			
			System.out.println(trainName + " " + trainType);
			if("  :  ".equalsIgnoreCase(arriveTimeFrom))
				System.out.println(fromStation + "站" + leaveTimeFrom + "始发");
			else
				System.out.println(fromStation + "站" + arriveTimeFrom + "到，" + leaveTimeFrom + "开车");
			
			System.out.println(toStation + "站" + arriveTimeTo + "到达");
			
			System.out.println("行驶距离" + dist + "公里");
			
			System.out.println("硬座" + yingzuo/10 + "，软座" + ruanzuo/10);
			if(yingwoM != -10)
				System.out.println("硬卧上" + yingwoU/10 + "，中" + yingwoM/10 + "，下" + yingwoD/10);
			if(ruanwoD != -10)
				System.out.println("软卧上" + ruanwoU/10 + "，下" + ruanwoD/10);
		}
	}

	public static void main(String[] args) {
		STDB db = new STDB();
		try {
			db.connectDB();
			
			db.zzQuery("", "义县");
//			db.ccQuery("T755/T758");
//			db.xqQuery2("T755/T758", "南京", "上海南");
			
//			db.ccQuery("T707");
//			db.xqQuery2("T707", "南京", "上海");

//			db.ccQuery("Z13");
//			db.xqQuery2("Z13", "北京", "上海");

//			db.ccQuery("1462");
//			db.xqQuery2("1462", "南京", "北京");

//			db.ccQuery("1461");
			db.xqQuery("T755/T758", "南京", "");
			
			db.closeDB();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				db.closeDB();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
