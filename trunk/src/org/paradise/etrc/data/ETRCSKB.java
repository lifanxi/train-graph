package org.paradise.etrc.data;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

public class ETRCSKB {
	private String path;

	/**
	 * 车次列表
	 */
	private Vector cc;
	
	/**
	 * 车站列表
	 */
	private Vector zm;
	
	/**
	 * 停靠信息列表
	 */
	private Vector tk;

	public ETRCSKB(String _path) throws IOException {
		path = _path;
		
		cc = new Vector();
		zm = new Vector();
		tk = new Vector();
		
		loadcc();
		loadzm();
		loadtk();
	}
	
	private void loadtk() throws IOException {
		File f = new File(path + "etrc.eda");
		
		DataInputStream in = new DataInputStream(new FileInputStream(f));
		
		while(in.available() > 0) {
			byte[] buffer = new byte[8];
			in.read(buffer);
			
			tk.add(decodeTK(new String(buffer)));
		}
	}

	private void loadzm() throws IOException {
		File f = new File(path + "ezm.eda");
		
		BufferedReader in = new BufferedReader(new FileReader(f));
		
		String line = in.readLine();
		while(line != null) {
			zm.add(line);
			
			line = in.readLine();
		}
	}
	
	private void loadcc() throws IOException {
		File f = new File(path + "ecc.eda");
		
		BufferedReader in = new BufferedReader(new FileReader(f));
		
		String line = in.readLine();
		while(line != null) {
			cc.add(line);
			
			line = in.readLine();
		}
	}
	
	private String[] decodeTK(String tk) {
		if(tk.length() != 8)
			return new String[] {"0000", "错误", "00:00", "00:00"};
		
		int trainIndex = ETRCData.decode(tk.charAt(0)) * 64 
		               + ETRCData.decode(tk.charAt(1));
		
		int stationIndex = ETRCData.decode(tk.charAt(2)) * 64
		                 + ETRCData.decode(tk.charAt(3));
		
		int h_arrive = ETRCData.decode(tk.charAt(4));
		int m_arrive = ETRCData.decode(tk.charAt(5));

		int h_leave = ETRCData.decode(tk.charAt(6));
		int m_leave = ETRCData.decode(tk.charAt(7));
		
		return new String[] {
			(String)cc.get(trainIndex),
			(String)zm.get(stationIndex),
			h_arrive + ":" + m_arrive,
			h_leave + ":" + m_leave
		};
	}
	
	/**
	 * 查找经过某个circuit的车次
	 */
	public Vector findTrains(Circuit cir) {
		Vector trains = new Vector();
		
		for(int i=0; i<cir.stationNum; i++) {
			Vector newTrains = findTrains(cir.stations[i].name);
			
			Enumeration en = newTrains.elements();
			while(en.hasMoreElements()) {
				Object train = en.nextElement();
				if(!trains.contains(train))
					trains.add(train);
			}
		}
		
		return trains;
	}
	
	/**
	 * 查找经过某个车站的车次
	 * @param stationName
	 * @return
	 */
	public Vector findTrains(String stationName) {
		Vector trains = new Vector();
		
		Enumeration en = tk.elements();
		while(en.hasMoreElements()) {
			String tkInfo[] = (String[]) en.nextElement();
			
			String trainName = tkInfo[0];
			String tkName = tkInfo[1];

			if(tkName.equalsIgnoreCase(stationName)) {
				Train aTrain = getTrainByFullName(trainName);
				if(!trains.contains(aTrain))
					trains.add(aTrain);
			}
		}
		
		return trains;
	}
	
	/**
	 * 获取指定车次（可能重复，包括A B）
	 * @param trainName
	 * @return
	 */
	public Vector getTrains(String trainName) {
		Vector trains = new Vector();
		
		Enumeration en = cc.elements();
		while(en.hasMoreElements()) {
			String myFullName = (String)en.nextElement();
			String myNames[] = myFullName.split("/");
			for(int i=0; i<myNames.length; i++) {
				String theName = myNames[i];
				if(theName.endsWith("A") || theName.endsWith("B"))
					theName = theName.substring(0, theName.length()-1);
				
				if(theName.equalsIgnoreCase(trainName)) {
					trains.add(getTrainByFullName(myFullName));
				}
			}
		}
		
		return trains;
	}
	
	/**
	 * 根据全称取车次（不会重复）
	 * @param trainName
	 * @return
	 */
	private Train getTrainByFullName(String trainName) {
		Train train = new Train();
		
		Enumeration en = tk.elements();
		while(en.hasMoreElements()) {
			String tkInfo[] = (String[]) en.nextElement();
			
			String myName = tkInfo[0];

			if(myName.equalsIgnoreCase(trainName)) {
				String tkName = tkInfo[1];
				String str_arrive = tkInfo[2];
				String str_leave = tkInfo[3];
	
				SimpleDateFormat df = new SimpleDateFormat("H:mm");
				Date arrive = null;
				Date leave = null;
				
				try {
					arrive = df.parse(str_arrive);
				} catch (ParseException e) {
					//e.printStackTrace();
				}
				
				try {
					leave = df.parse(str_leave);
				} catch (ParseException e) {
					//e.printStackTrace();
				}
				
				if(arrive == null)
					arrive = leave;
				
				if(leave == null)
					leave = arrive;
				
				train.appendStop(new Stop(tkName, arrive, leave));
			}
		}
		
//		train.startStation = train.stops[0].stationName;
//		train.terminalStation = train.stops[train.stopNum - 1].stationName;
		train.trainNameFull = trainName;
		
		String names[] = train.trainNameFull.split("/");
		for(int i=0; i<names.length; i++) {
			if(names[i].endsWith("A") || names[i].endsWith("B"))
				names[i] = names[i].substring(0, names[i].length()-1);

			if(Train.isDownName(names[i]))
				train.trainNameDown = names[i];
			else
				train.trainNameUp = names[i];
		}
		
		return train;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ETRCSKB data = new ETRCSKB("C:\\trains\\");
			System.out.println(data.cc.size());
			System.out.println(data.zm.size());
			System.out.println(data.tk.size());
			
			Train train = (Train) (data.getTrains("N552").get(0));
			System.out.println(train);
			
//			System.out.println(data.findTrains("南通"));
			
			Circuit cir = new Circuit();
			cir.loadFromFile("C:\\trains\\沪杭线.cir");
			Vector trains = data.findTrains(cir);
			System.out.println("沪杭线：" + trains.size() + "\r\n");
//			System.in.read();
//			System.out.println(trains);
			
			System.out.println(train.isDownTrain(cir));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
