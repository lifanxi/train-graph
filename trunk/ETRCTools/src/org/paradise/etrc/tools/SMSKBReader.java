package org.paradise.etrc.tools;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.*;

import org.paradise.etrc.data.Circuit;
import org.paradise.etrc.data.Stop;
import org.paradise.etrc.data.Train;
import org.paradise.etrc.data.skb.ETRCData;
import org.paradise.etrc.tools.db.DBTrain;

public class SMSKBReader {
	/**
	 * 车次列表
	 * 
	 * value是一个描述一趟列车的String数组
	 * train[0] 全车次，最长14位
	 * train[1] 1位，3表示普快，4表示普客，A表示快速，2表示特快，1表示直特
	 * train[2] 2位，不明
	 * train[3] 5位，停靠数据列表起始记录Index  //tk_start_index
	 * train[4] 5位，停靠数据列表末尾记录Index  //tk_end_index
	 * train[5] 1位，0表示普车，1表示新空
	 */
	private Vector cc;
	
	/**
	 * 车站列表
	 */
	private Vector zm;
	
	/**
	 * 从单一车次名到实际车次Index的映射关系
	 * 
	 * key是单一车次名
	 * value是车次列表中得Index
	 */
	private Hashtable ccIndex;
	
	/**
	 * 车次停靠站列表
	 * 
	 * valuse是一个描述一个停靠信息的String数组
	 * stop[0] 停靠站车站Index
	 * stop[1] 行使里程
	 * stop[2] 到站时刻
	 * stop[3] 出发时刻
	 */
	private Vector cctk;
	
	private String path;
	
	public SMSKBReader(String _path) throws IOException {
		path = _path;
		
		ccIndex = new Hashtable();

		cc = new Vector();
		zm = new Vector();
		cctk = new Vector();
		
		readccIndex();

		readcc();
		readzm();
		
		for(int i=0; i<=20; i++)
			readcctk(i);
	}
	
	//读zmhzsy文件，获取车站中文名列表
	private void readzm() throws IOException {
		File file = new File(path + "zmhzsy.txt");
		InputStream in = new DataInputStream(new FileInputStream(file));

		in.skip(3);
		
		int len = 25;
		byte buffer[]= new byte[len];
		int num = (in.available() - 3) / len + 1;

		for (int i = 0; i < num; i++) {
			in.read(buffer);
			String record = new String(buffer, "UTF-8");
			
			String name = record.substring(0,record.length() - 10).trim();
//			String id = record.substring(record.length() - 10, record.length()); //用途不明
//			System.out.println(i + ": " + id + " : " + name);
			
			zm.add(name);
		}

		in.close();
	}

	//读ccdz和ccdzzm文件获取单一车子在cc文件中的index
	private void readccIndex() throws IOException {
		readccdz("ccdz");
		readccdz("ccdzzm");
	}
	
	//读ccdz或ccdzzm文件获取单一车子在cc文件中的index
	private void readccdz(String fileName) throws IOException {
		File file = new File(path + fileName + ".txt");
		InputStream in = new DataInputStream(new FileInputStream(file));

		in.skip(3);
		
		int len = 8;
		byte buffer[]= new byte[len];
		int num = (in.available() - 3) / len + 1;

		for (int i = 0; i < num; i++) {
			in.read(buffer);
			String record = new String(buffer, "UTF-8");
			
			String trainName = record.substring(0, 4).trim();				
			String index = record.substring(record.length() - 4).trim();
			
//			System.out.println(index + " : " + trainName);
			
			ccIndex.put(trainName, index);
		}

		in.close();
	}
	
	private void findStranger() throws IOException {
		File file = new File(path + "cc.txt");
		InputStream in = new DataInputStream(new FileInputStream(file));

		in.skip(3);
		
		int len = 33;
		byte buffer[]= new byte[len];
		int num = (in.available() - 3) / len + 1;

		for (int i = 0; i < num; i++) {
			in.read(buffer);
			String record = new String(buffer, "UTF-8");

			String name = record.substring(0, record.length() - 14).trim();
			findStrangerByName(name);
		}

		in.close();
	}

	private void findStrangerByName(String name) {
		if((name.endsWith("A")) || (name.endsWith("B")))
			name = name.substring(0, name.length()-1);
		
		String[] ns = name.split("/");
		
		Train train = null;
		if(ns.length >= 2){
			if(ns.length >= 3){
//				System.out.println(name);
				train = getTrain(name);
			}
			else {				
				int n1 = Integer.parseInt(ns[0].substring(1,ns[0].length()));
				int n2 = Integer.parseInt(ns[1].substring(1,ns[1].length()));
				
				if((n1%2==0 && n2%2==0) || (n1%2==1 && n2%2==1))
//					System.out.println(name);
					train = getTrain(name);
			}
		}
		
		if(train != null) {
			System.out.print(train.getTrainName() + ",");
			System.out.print(train.getStartStation() + ",");
			System.out.println(train.getTerminalStation());
		}
	}

	//读cc.txt文件获取带索引的车次全名列表
	private void readcc() throws IOException {
		File file = new File(path + "cc.txt");
		InputStream in = new DataInputStream(new FileInputStream(file));

		in.skip(3);
		
		int len = 33;
		byte buffer[]= new byte[len];
		int num = (in.available() - 3) / len + 1;

		for (int i = 0; i < num; i++) {
			in.read(buffer);
			String record = new String(buffer, "UTF-8");

			String name = record.substring(0, record.length() - 14).trim();
			String id = record.substring(record.length() - 14);
			
			String train[] = new String[6];
			
			train[0] = name;
			train[1] = id.substring(0,1);
			train[2] = id.substring(1,3);
			train[3] = id.substring(3,8);  //tk_start_index
			train[4] = id.substring(8,13); //tk_end_index
			train[5] = id.substring(13,14);
			
//			if(i<490)
//			System.out.println(i + " | "
//					+ train[1] + " : "
//					+ train[2] + " : "
//					+ train[3] + " : "
//					+ train[4] + " : "
//					+ train[5] + " => "
//					+ train[0]);
			
			cc.add(train);
		}

		in.close();
	}
	
	private void readcctk(int fileNo) throws IOException {
		InputStream in = null;
		
		try {
			String fileName = "cctksy" + fileNo;
			File file = new File(path + fileName + ".txt");
			in = new DataInputStream(new FileInputStream(file));
		}
		catch(FileNotFoundException e) {
//			e.printStackTrace();
		}
		
		if(in==null)
			return;

		in.skip(3);
		
		int len = 16;
		byte buffer[]= new byte[len];
		int num = (in.available() - 3) / len + 1;

		for (int i = 0; i < num; i++) {
			in.read(buffer);
			String record = new String(buffer, "UTF-8");
			
			String stop[] = new String[4];
			stop[0] = record.substring(0, 4);
			stop[1] = record.substring(4, 8);
			stop[2] = record.substring(8, 10) + ":" + record.substring(10, 12);
			stop[3] = record.substring(12, 14) + ":" + record.substring(14, 16);
	        
//	        System.out.println(
//	        		record + " = " +
//	        		stop[0] + " : <" +
//	        		stop[1] + " < " +
//	        		stop[2] + " | " +
//	        		stop[3] + " | "
//	        		);
	        
	        cctk.add(stop);
		}

		in.close();
	}
	
	/**
	 * 查找经过circuit线路的车次
	 */
	public Vector findTrains(Circuit cir) {
		Vector selTrains = new Vector();
		
		for(int i=0; i<cir.stationNum; i++) {
			Vector trains = findTrains(cir.stations[i].name);
			
			Enumeration en = trains.elements();
			while(en.hasMoreElements()) {
				Object tr = en.nextElement();
				if(!selTrains.contains(tr)) {
					selTrains.add(tr);
				}
			}
		}
		
		return selTrains;
	}
	
	/**
	 * 查找停靠station站的车次列表
	 */
	public Vector findTrains(String station) {
		Vector selTrains = new Vector();
		
		for(int trainIndex = 0; trainIndex < cc.size(); trainIndex++) {
			String trainInfo[] = (String[]) cc.get(trainIndex);
			
			int startIndex = Integer.parseInt(trainInfo[3]);
			int endIndex = Integer.parseInt(trainInfo[4]);
						
			for(int i=startIndex; i<=endIndex; i++) {
				String tkInfo[] = (String [])cctk.get(i);
				
				String tkName = (String)zm.get(Integer.parseInt(tkInfo[0]));

				if(tkName.equalsIgnoreCase(station))
					selTrains.add(trainInfo[0]);
			}
		}
		
		return selTrains;
	}

	public Train getTrain(String trainName) {
		if(trainName.indexOf("/")>0)
			trainName = trainName.split("/")[0];
		
		if(trainName.endsWith("A") || trainName.endsWith("B"))
			trainName = trainName.substring(0, trainName.length() - 1);
		
		String trainIndexStr = (String) ccIndex.get(trainName);
		if(trainIndexStr == null)
			return null;
		
		Train train = new Train();

		int trainIndex = Integer.parseInt(trainIndexStr);
		String trainInfo[] = (String[]) cc.get(trainIndex);
		
		int startIndex = Integer.parseInt(trainInfo[3]);
		int endIndex = Integer.parseInt(trainInfo[4]);
		
//		System.out.println(trainInfo[0] + "次列车：");
		for(int i=startIndex; i<=endIndex; i++) {
			String tkInfo[] = (String [])cctk.get(i);
			
			String tkName = (String)zm.get(Integer.parseInt(tkInfo[0]));
			String str_arrive = (String)tkInfo[2];
			String str_leave = (String)tkInfo[3];
			
//			System.out.println(tkName + "站 " + str_arrive + "到 " + str_leave + "发");
			
//			SimpleDateFormat df = new SimpleDateFormat("H:mm");
//			Date arrive = null;
//			Date leave = null;
//			
//			try {
//				arrive = df.parse(str_arrive);
//			} catch (ParseException e) {
//				//e.printStackTrace();
//			}
//			
//			try {
//				leave = df.parse(str_leave);
//			} catch (ParseException e) {
//				//e.printStackTrace();
//			}
//			
//			if(arrive == null)
//				arrive = leave;
//			
//			if(leave == null)
//				leave = arrive;
			
			train.appendStop(Stop.makeStop(tkName, str_arrive, str_leave, true));
		}
		
//		train.startStation = train.stops[0].stationName;
//		train.terminalStation = train.stops[train.stopNum - 1].stationName;
		train.trainNameFull = trainInfo[0];
		
		String names[] = train.trainNameFull.split("/");
		for(int i=0; i<names.length; i++)
			if(Train.isDownName(names[i]))
				train.trainNameDown = names[i];
			else
				train.trainNameUp = names[i];
		
		return train;
	}
	
	private String buildeTK(int trainIndex, int stationIndex, String arrive, String leave) {
		return ETRCData.encode2(trainIndex) + 
		       ETRCData.encode2(stationIndex) + 
		       ETRCData.encode1( Integer.parseInt(arrive.split(":")[0]) ) + 
		       ETRCData.encode1( Integer.parseInt(arrive.split(":")[1]) ) +
		       ETRCData.encode1( Integer.parseInt(leave.split(":")[0]) ) + 
		       ETRCData.encode1( Integer.parseInt(leave.split(":")[1]) );
	}
	
	public void dumpToSTDB(STDB db) throws SQLException {
		db.connectDB();
		
		//车次信息
		for(int trainIndex = 0; trainIndex < cc.size(); trainIndex++) {
			String trainInfo[] = (String[]) cc.get(trainIndex);
			/*
			 * trainInfo[0] 全车次，最长14位
			 * trainInfo[1] 1位，3表示普快，4表示普客，A表示快速，2表示特快，1表示直特
			 * trainInfo[2] 2位，不明
			 * trainInfo[3] 5位，停靠数据列表起始记录Index  //tk_start_index
			 * trainInfo[4] 5位，停靠数据列表末尾记录Index  //tk_end_index
			 * trainInfo[5] 1位，0表示普车，1表示新空
			 */
			String trainName = trainInfo[0];
			int type = Integer.parseInt(trainInfo[5]);
			int level;
			switch(trainInfo[1].charAt(0)) {
			case '3':
				level = DBTrain.LEVEL_PKa; break;
			case '4':
				level = DBTrain.LEVEL_PKe; break;
			case 'A':
				level = DBTrain.LEVEL_KS; break;
			case '2':
				level = DBTrain.LEVEL_TK; break;
			case '1':
				level = DBTrain.LEVEL_ZT; break;
			default:
				level = DBTrain.LEVEL_PKa;
			}

			db.insertTrain(trainIndex, trainName, type, level);

			//取该车次的停站信息
			int startIndex = Integer.parseInt(trainInfo[3]);
			int endIndex = Integer.parseInt(trainInfo[4]);
						
			for(int stopIndex=startIndex; stopIndex<=endIndex; stopIndex++) {
				String tkInfo[] = (String [])cctk.get(stopIndex);
				/*
				 * tkInfo[0] 停靠站车站Index
				 * tkInfo[1] 行使里程
				 * tkInfo[2] 到站时刻
				 * tkInfo[3] 出发时刻
				 */
				int stationID = Integer.parseInt(tkInfo[0]);
				int distance = Integer.parseInt(tkInfo[1]);
				String arrive = tkInfo[2];
				String leave = tkInfo[3];
				
				db.insertStop(trainIndex, stationID, arrive, leave, distance);
			}
		}
		
		//车次映射关系
		Enumeration en = ccIndex.keys();
		while(en.hasMoreElements()){
			String TrainNameSingle = (String) en.nextElement();
			String strIndex = (String) ccIndex.get(TrainNameSingle);
			int index = Integer.parseInt(strIndex);
			
			db.insertTrainIndex(TrainNameSingle, index);
		}
		
		//车站列表
		for(int stationIndex = 0; stationIndex < zm.size(); stationIndex++) {
			String stationName = (String) zm.get(stationIndex);
			
			db.insertStation(stationIndex, stationName);
		}
		
		db.closeDB();
	}
	
	public void dumpToETRC(String path) throws IOException {
		File etrcFile = new File(path + "etrc.eda");
		File eccFile = new File(path + "ecc.eda");
		File ezmFile = new File(path + "ezm.eda");
		
		PrintStream etrcOut = new PrintStream(new FileOutputStream(etrcFile));
		PrintStream eccOut = new PrintStream(new FileOutputStream(eccFile));
		PrintStream ezmOut = new PrintStream(new FileOutputStream(ezmFile));
		
		for(int i=0; i<zm.size(); i++) {
			ezmOut.println( zm.get(i) );
		}
		
		for(int trainIndex = 0; trainIndex < cc.size(); trainIndex++) {
			String trainInfo[] = (String[]) cc.get(trainIndex);
			
			int startIndex = Integer.parseInt(trainInfo[3]);
			int endIndex = Integer.parseInt(trainInfo[4]);
			
			String trainName = trainInfo[0];
//			System.out.println(trainName);
//			if(trainName.endsWith("A") || trainName.endsWith("B"))
//				trainName = trainName.substring(0, trainName.length()-1);
//			System.out.println(trainName);
			eccOut.println(trainName);
			
			for(int i=startIndex; i<=endIndex; i++) {				
				String tkInfo[] = (String [])cctk.get(i);
				
				int stationIndex = Integer.parseInt(tkInfo[0]);
				String str_arrive = (String)tkInfo[2];
				String str_leave = (String)tkInfo[3];
				
				if(str_arrive.indexOf(' ') >= 0)
					str_arrive = str_leave;
				
				if(str_leave.indexOf(' ') >= 0)
					str_leave = str_arrive;
				
				etrcOut.print(buildeTK(trainIndex, stationIndex, str_arrive, str_leave));
			}
		}
		
		etrcOut.close();
		eccOut.close();
		ezmOut.close();
	}
	
	public void buildTRF(String cirFile, String trfPath) throws IOException {
		Circuit cir = new Circuit();
		cir.loadFromFile(cirFile);
		Vector trains = findTrains(cir);
		for(int i=0; i<trains.size(); i++) {
			String trainName = (String)trains.get(i);
			Train train = getTrain(trainName);
			
			File f = new File(trfPath);
			if(!f.exists()) {
				f.mkdir();
			}
			
			System.out.println(trainName);
			train.writeTo(trfPath + trainName.replace('/', '_') + ".trf");
		}
	}

	public static void main(String[] args) {
		try {
			SMSKBReader sm = new SMSKBReader("C:\\trains\\04.盛名时刻表\\smdata\\data\\");
			
			System.out.println("cc = " + sm.cc.size());
			System.out.println("ccIndex = " + sm.ccIndex.size());
			System.out.println("zm = " + sm.zm.size());
			System.out.println("cctk = " + sm.cctk.size());

			//倒出到ETRC
//			sm.dumpToETRC("C:\\trains\\etrc_data_20070226\\");
			
			//倒出到ST数据库
			//sm.dumpToSTDB(new STDB());
			
//			sm.findStranger();
//			System.out.println(sm.getTrain("N95"));
		} catch (IOException e) {
			e.printStackTrace();
		} //catch (SQLException e) {
//			e.printStackTrace();
//		}
	}

}
