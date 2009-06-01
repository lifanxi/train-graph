package org.paradise.etrc.data.skb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;


public class ETRCLCB {
	private String path;
	private Vector xl;
	private Vector lc;
	
	public ETRCLCB(String _path) throws IOException {
		path = _path;
		
		loadxl();
		loadlc();
	}

	private LCBStation decodeLC(String line) {
		String xianluID = line.substring(0,2);
		int level = ETRCData.decode(line.charAt(2));
		int dist = ETRCData.decode(line.charAt(3)) * 64 +
				   ETRCData.decode(line.charAt(4));
		String name = line.substring(5,line.length());
		
		LCBStation station = new LCBStation(name, dist, level, false);
		station.xianlu = getXianlu(xianluID);

		return station;
	}

	private String getXianlu(String xianluID) {
		for(int i=0; i<xl.size(); i++) {
			String xianlu = (String) xl.get(i);
			if(xianlu.startsWith(xianluID))
				return xianlu;
		}
		return "";
	}
	
//	public Vector findXianlu(String xianluName) {
//		for(int i=0; i<xl.size(); i++) {
//			String xianluNameWithID = (String) xl.get(i);
//			if(xianluNameWithID.endsWith(xianluName)) {
//				return findLCBStation(xianluNameWithID.substring(0,2));
//			}
//		}
//		
//		return null;
//	}
	
	public Vector findXianlu(char head) {
		Vector xlFound = new Vector();
		
		for(int i=0; i<xl.size(); i++) {
			String theXL = (String) xl.get(i);
			if((theXL.charAt(0) == head) || (head == '*'))
				xlFound.add(theXL.substring(2,theXL.length()));
		}
		
		return xlFound;
	}

	public Vector findLCBStation(String xlName) {
		Vector stFound = new Vector();
		
		for(int i=0; i<lc.size(); i++) {
			LCBStation theStation = (LCBStation) lc.get(i);
			String xlInST = theStation.xianlu.substring(2, theStation.xianlu.length());
			
			if(xlInST.equalsIgnoreCase(xlName)) {
				stFound.add(theStation);
				//System.out.println((LCBStation) st);
			}
		}
		
		return stFound;
	}

	private void loadlc() throws IOException {
		lc = new Vector();
		File f = new File(path + "elc.eda");
		
		BufferedReader in = new BufferedReader(new FileReader(f));
		
		String line = in.readLine();
		while(line != null) {
			lc.add(decodeLC(line));
			
			line = in.readLine();
		}
	}

	private void loadxl() throws IOException {
		xl = new Vector();
		File f = new File(path + "exl.eda");
		
		BufferedReader in = new BufferedReader(new FileReader(f));
		
		String line = in.readLine();
		while(line != null) {
			xl.add(line);
			
			line = in.readLine();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ETRCLCB lcb = new ETRCLCB("C:\\trains\\");
			
			System.out.println("lc = " + lcb.lc.size());
			System.out.println("xl = " + lcb.xl.size());
			
			System.out.println(lcb.findLCBStation("京沪线"));
			
			System.out.println(lcb.findXianlu('D'));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
