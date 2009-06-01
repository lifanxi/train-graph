package org.paradise.etrc.tools;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
	
	private HttpURLConnection connect(String urlString)	throws IOException {
		System.out.println("Connect to: " + urlString);
		
		HttpURLConnection httpConn = (HttpURLConnection) (new URL(urlString)).openConnection();

		httpConn.setDoOutput(true);
		httpConn.setRequestMethod("GET");
		httpConn.addRequestProperty("Connection", "Keep-Alive");
		httpConn.addRequestProperty("Content-Type", "text/xml");

		return httpConn;
	}
	
	public Train getTrainInfo(String fromTrainNo) throws IOException {
		String url = URL_getTrainInfo + fromTrainNo;

		HttpURLConnection connection = connect(url);
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		String line;
		int count = 0;
		int status = 0;
		StringBuffer buffer = new StringBuffer();
		while ( (line = in.readLine()) != null) {
			if(line.equalsIgnoreCase("<TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>"))
				status = 1;
			
			if(line.equalsIgnoreCase("</TABLE>"))
				status = 2;
			
			if(status == 1) {
				buffer.append(line);
//				if(!line.startsWith("<TD")) {
//					System.out.print(count + " ---- ");
//					System.out.println(line);
//					count++;
//				}
			}
		}
		
		return paserLine(buffer.toString());
	}
	
	private String dataBeginPatten = "<TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=";
	private String dataEndPatten = "</TD>";
	private Train paserLine(String line) {
		Train theTrain = new Train();
		String remain = line;
		int count = 0;
		Vector trainName = new Vector();

		String stationName = "";
		String strArrive = "";
		String strLeave = "";
		String strLevel = "";
		String strType = "";
		int dist = 0;

		while(remain.indexOf(dataBeginPatten) >= 0) {
			int begin = remain.indexOf(dataBeginPatten) + 8;
			int end = remain.indexOf(dataEndPatten);
			String data = remain.substring(begin + dataBeginPatten.length(), end);
			remain = remain.substring(end + dataEndPatten.length());

			switch(count%8){
			case 0:
				if(!trainName.contains(data))
					trainName.add(data);
			break;
			case 1:
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

				theTrain.appendStop(Stop.makeStop(stationName, strArrive, strLeave));
			break;
			}
			
			//System.out.println(data);
			count++;
		}
		
		if(theTrain.stopNum == 0)
			return null;
		
		theTrain.trainNameFull = Train.makeFullName(trainName);
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

	public static void main(String[] args) {
		NetInfoShenTie net = new NetInfoShenTie();
		
		try {
//			net.getPriceInfo("上海", "杭州东");
//			
//			net.getBianzu();
			
//			BufferedReader allTrainListHtm = new BufferedReader(new FileReader("C:\\trains\\srInfo\\TrainAllList.htm"));
//			net.paserAllTrainList(allTrainListHtm);
			
			Train theTrain = net.getTrainInfo("2258");
//			Train theTrain = net.paserLine("<TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>T758</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>01</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>泰州</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>11:42</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC></TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>0</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>特快</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>新型有空调</TD></TR><TR><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>T758</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>02</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>江都</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>12:11</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>12:14</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>0</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>特快</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>新型有空调</TD></TR><TR><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>T758</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>03</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>扬州</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>12:31</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>12:35</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>0</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>特快</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>新型有空调</TD></TR><TR><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>T755</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>04</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>南京</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>13:43</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>13:50</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>15</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>特快</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>新型有空调</TD></TR><TR><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>T755</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>05</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>镇江</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>14:23</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>14:27</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>79</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>特快</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>新型有空调</TD></TR><TR><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>T755</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>06</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>常州</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>15:07</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>15:11</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>151</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>特快</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>新型有空调</TD></TR><TR><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>T755</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>07</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>无锡</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>15:34</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>15:38</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>190</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>特快</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>新型有空调</TD></TR><TR><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>T755</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>08</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>苏州</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>15:58</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>16:02</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>232</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>特快</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>新型有空调</TD></TR><TR><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>T755</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>09</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>昆山</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>16:19</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>16:21</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>267</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>特快</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>新型有空调</TD></TR><TR><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>T755</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>10</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>上海南</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>17:05</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>17:30</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>330</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>特快</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#EEEEEE>新型有空调</TD></TR><TR><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>T755</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>11</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>杭州</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>19:00</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC></TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>503</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>特快</TD><TD ALIGN=CENTER VALIGN=CENTER BGCOLOR=#FFFFCC>新型有空调</TD></TR>");
			System.out.println(theTrain);
			
//			throw new IOException("OKOKOK!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
