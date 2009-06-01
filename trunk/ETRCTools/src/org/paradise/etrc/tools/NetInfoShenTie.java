package org.paradise.etrc.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.Vector;

import org.paradise.etrc.data.Stop;
import org.paradise.etrc.data.Train;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Vector;
//
//import org.paradise.etrc.data.Stop;
//import org.paradise.etrc.data.Train;

/**
 * 从www.sronline.com.cn网站获取信息
 */
public class NetInfoShenTie {
	private final String URL_getTrainInfo = "http://www.sronline.com.cn/smart40/ShowStopTimeTrain?fromTrainno=";
	private final String URL_getPriceInfo1 = "http://www.sronline.com.cn/smart40/ShowTicketPrice?trainStationDate=2007-01-29&fromStationName=";
	private final String URL_getPriceInfo2 = "&toStationName=";
	
	private final String URL_bianzu = "http://www.sronline.com.cn/smart40/ShowCompileTrain?train_no=770000100680";

	private final String URL_trainAll = "http://www.sronline.com.cn/smart40/TrainAllList";
	private final String URL_getTrainInfoA = "http://www.sronline.com.cn/smart40/ShowStopTimeTrain?trainTrainnoDate=2007-02-04&fromTrainno=";
	private final String URL_getTrainInfoB = "http://www.sronline.com.cn/smart40/ShowStopTimeTrain?trainTrainnoDate=2007-02-03&fromTrainno=";
	
	public Vector getAllTrain() throws IOException {
		String url = URL_trainAll;

		HttpURLConnection connection = connect(url);
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		String line;
		int count = -1;
		String fromName = "";
		String fromStation = "";
		String toStation = "";
		Vector trainFromNames = new Vector();
		
		while ( (line = in.readLine()) != null) {
//			System.out.println(line);
			if(line.equalsIgnoreCase("<TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#DFEFFF WIDTH=100 >"))
				count = 0;
			
			switch(count){
			case 2:
				fromName = line;
				break;
			case 5:
				fromStation = line;
				break;
			case 8:
				toStation = line;
//				System.out.println(fromName + ": " + fromStation + "-" + toStation);
				
				if(trainFromNames.contains(fromName)) {
					System.out.println(fromName + ": " + fromStation + "-" + toStation);
					fromName += "B";
				}
				trainFromNames.add(fromName);
				
				break;
			}
			
			count++;
		}

		//paserAllTrainLine(line);
		return trainFromNames;
	}
	
	public Train getTrainInfo(STDB db, String fromTrainNo, int trainID) throws IOException, SQLException {
		String url;
		if(fromTrainNo.endsWith("B")) {
			fromTrainNo = fromTrainNo.substring(0, fromTrainNo.length()-1);
			url = URL_getTrainInfoB + fromTrainNo.trim();
			
			System.out.println(fromTrainNo + "B");
		}
		else
			url = URL_getTrainInfoA + fromTrainNo.trim();

		HttpURLConnection connection = connect(url);
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		String line;
		int status = 0;
		StringBuffer buffer = new StringBuffer();
		while ( (line = in.readLine()) != null) {
			if(line.startsWith(trainInfoDataBeginPatten)){
				status = 1;
			}
			
			if(line.endsWith("</TABLE>"))
				status = 2;
			
			if(status == 1) {
				buffer.append(line);
			}
		}
		
		return paserTrainInfoLine(db, buffer.toString(), trainID);
	}
	
	private String trainInfoDataBeginPatten = "<TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=";
	private String trainInfoDataEndPatten = "</TD>";
	private Train paserTrainInfoLine(STDB db, String line, int trainID) throws SQLException {
		Train theTrain = new Train();
		String remain = line;
		int count = 0;
		Vector trainNames = new Vector();

		String trainNameCurrent = "";
		String stationName = "";
		String strArrive = "";
		String strLeave = "";
		String strLevel = "";
		String strType = "";
		int dist = 0;
		
		while(remain.indexOf(trainInfoDataBeginPatten) >= 0) {
			int begin = remain.indexOf(trainInfoDataBeginPatten) + 8;
			int end = remain.indexOf(trainInfoDataEndPatten);
			String data = remain.substring(begin + trainInfoDataBeginPatten.length(), end);
			remain = remain.substring(end + trainInfoDataEndPatten.length());

			switch(count%8){
			case 0:
				trainNameCurrent = data;
				if(!trainNames.contains(trainNameCurrent))
					trainNames.add(trainNameCurrent);
			break;
			case 1:
				//站序
			break;
			case 2:
				stationName = data;
			break;
			case 3:
				strArrive = data;
			break;
			case 4:
				strLeave = data;
			break;
			case 5:
				dist = Integer.parseInt(data);
			break;
			case 6:
				strLevel = data;
			break;
			case 7:
				strType = data;

				Stop stop = Stop.makeStop(stationName, strArrive, strLeave);
				theTrain.appendStop(stop);
				db.insertStop2(trainID, trainNameCurrent, stationName, stop.arrive, stop.leave, dist);
			break;
			}
			
			//System.out.println(data);
			count++;
		}
		
		if(theTrain.stopNum == 0)
			return null;
		
		theTrain.trainNameFull = Train.makeFullName(trainNames);
//		theTrain.startStation = theTrain.stops[0].stationName;
//		theTrain.terminalStation = theTrain.stops[theTrain.stopNum - 1].stationName;
		return theTrain;
	}
	
	public void getPriceInfo(String from, String to) throws IOException{
		String url = URL_getPriceInfo1 + from + URL_getPriceInfo2 + to;

		HttpURLConnection connection = connect(url);
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		String line;
		int count = 0;
		while ( (line = in.readLine()) != null) {
			System.out.println(line);
			count++;
		}
	}
	
	public void getBianzu() throws IOException {
		String url = URL_bianzu;

		HttpURLConnection connection = connect(url);
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		String line;
		int count = 0;
		while ( (line = in.readLine()) != null) {
			System.out.println(line);
			count++;
		}
	}
	
	public void paserAllTrainList(BufferedReader in) throws IOException {
		String line;
		int count = 0;
		while ( (line = in.readLine()) != null) {
			System.out.println(line);
			count++;
		}
		System.out.println(count);
	}

	private HttpURLConnection connect(String urlString)	throws IOException {
		System.out.println("Connect to: " + urlString);
		
		HttpURLConnection httpConn = (HttpURLConnection) (new URL(urlString)).openConnection();

		httpConn.setDoOutput(true);
		httpConn.setRequestMethod("GET");
		httpConn.addRequestProperty("Connection", "Keep-Alive");
		httpConn.addRequestProperty("Content-Type", "text/xml");

		return httpConn;
	}
	
	public static void main(String[] args) {
		NetInfoShenTie net = new NetInfoShenTie();
		STDB db = new STDB();
		
		try {
			db.connectDB();
//			net.getPriceInfo("上海", "杭州东");
//			
//			net.getBianzu();
			
//			BufferedReader allTrainListHtm = new BufferedReader(new FileReader("C:\\trains\\srInfo\\TrainAllList.htm"));
//			net.paserAllTrainList(allTrainListHtm);
			
//			Train theTrain = net.getTrainInfo(db, "2258");
//			System.out.println(theTrain);
			
			Vector fromNames = net.getAllTrain();
			for(int i=131; i<fromNames.size(); i++) {
				String name = (String) fromNames.get(i);
//				System.out.println(name);
				
				Train theTrain = net.getTrainInfo(db, name, i);
				if(theTrain != null)
					System.out.println(i + "> " 
						+ theTrain.getTrainName() + ": " 
						+ theTrain.getStartStation() + "-"
						+ theTrain.getTerminalStation());
			}
			
			db.closeDB();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			try {
				db.closeDB();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
