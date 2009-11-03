package org.paradise.etrc.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import org.paradise.etrc.tools.db.DBPrice;
import org.paradise.etrc.tools.db.DBTrain;

public class PJBReader {
	private String path;
	
	private static final int NUM = 114;
	private DBPrice[] xkPuKe = new DBPrice[NUM];
	private DBPrice[] xkPuKa = new DBPrice[NUM];
	private DBPrice[] xkKaSu = new DBPrice[NUM];
	private DBPrice[] xkTeKa = new DBPrice[NUM];
	private DBPrice[] xkZiTe = new DBPrice[NUM];
	private DBPrice[] pcPuKe = new DBPrice[NUM];
	private DBPrice[] pcPuKa = new DBPrice[NUM];
	private DBPrice[] pcKaSu = new DBPrice[NUM];
	private DBPrice[] pcTeKa = new DBPrice[NUM];
	private DBPrice[] pcZiTe = new DBPrice[NUM];
	
	public PJBReader(String _path) {
		path = _path;
	}

	public void readPJB() throws IOException {
		File file = new File(path + "price.csv");
		BufferedReader in = new BufferedReader(new FileReader(file));

		String line;
		int i = 0;
		while ((line = in.readLine()) != null) {
			parsePriceLine(line, i);
			i++;
		}
	}
	
	private void parsePriceLine(String line, int i) {
		String[] values = line.split(",");
		if(values.length != 43)
			return;
		int distFrom = Integer.parseInt(values[0].split("-")[0]);
		int distTo = Integer.parseInt(values[0].split("-")[1]);
		
		xkPuKe[i] = new DBPrice();
		xkPuKa[i] = new DBPrice();
		xkKaSu[i] = new DBPrice();
		xkTeKa[i] = new DBPrice();
		xkZiTe[i] = new DBPrice();
		pcPuKe[i] = new DBPrice();
		pcPuKa[i] = new DBPrice();
		pcKaSu[i] = new DBPrice();
		pcTeKa[i] = new DBPrice();
		pcZiTe[i] = new DBPrice();

		xkPuKe[i].distFrom = distFrom;
		xkPuKe[i].distTo = distTo;
		xkPuKe[i].level = DBTrain.LEVEL_PKe;
		xkPuKe[i].type = DBTrain.TYPE_XK;
		xkPuKe[i].yingzuo = (int) (Float.parseFloat(values[22]) * 10);
		xkPuKe[i].ruanzuo = (int) (Float.parseFloat(values[34]) * 10);
		xkPuKe[i].yingwoU = (int) (Float.parseFloat(values[25]) * 10);
		xkPuKe[i].yingwoM = (int) (Float.parseFloat(values[26]) * 10);
		xkPuKe[i].yingwoD = (int) (Float.parseFloat(values[27]) * 10);
		xkPuKe[i].ruanwoU = (int) (Float.parseFloat(values[37]) * 10);
		xkPuKe[i].ruanwoD = (int) (Float.parseFloat(values[38]) * 10);
//		System.out.print("\n新空普客" + xkPuKe[i]);

		xkPuKa[i].distFrom = distFrom;
		xkPuKa[i].distTo = distTo;
		xkPuKa[i].level = DBTrain.LEVEL_PKa;
		xkPuKa[i].type = DBTrain.TYPE_XK;
		xkPuKa[i].yingzuo = (int) (Float.parseFloat(values[23]) * 10);
		xkPuKa[i].ruanzuo = (int) (Float.parseFloat(values[35]) * 10);
		xkPuKa[i].yingwoU = (int) (Float.parseFloat(values[28]) * 10);
		xkPuKa[i].yingwoM = (int) (Float.parseFloat(values[29]) * 10);
		xkPuKa[i].yingwoD = (int) (Float.parseFloat(values[30]) * 10);
		xkPuKa[i].ruanwoU = (int) (Float.parseFloat(values[39]) * 10);
		xkPuKa[i].ruanwoD = (int) (Float.parseFloat(values[40]) * 10);
//		System.out.print("\n新空普快" + xkPuKa[i]);
		
		xkKaSu[i].distFrom = distFrom;
		xkKaSu[i].distTo = distTo;
		xkKaSu[i].level = DBTrain.LEVEL_KS;
		xkKaSu[i].type = DBTrain.TYPE_XK;
		xkKaSu[i].yingzuo = (int) (Float.parseFloat(values[24]) * 10);
		xkKaSu[i].ruanzuo = (int) (Float.parseFloat(values[36]) * 10);
		xkKaSu[i].yingwoU = (int) (Float.parseFloat(values[31]) * 10);
		xkKaSu[i].yingwoM = (int) (Float.parseFloat(values[32]) * 10);
		xkKaSu[i].yingwoD = (int) (Float.parseFloat(values[33]) * 10);
		xkKaSu[i].ruanwoU = (int) (Float.parseFloat(values[41]) * 10);
		xkKaSu[i].ruanwoD = (int) (Float.parseFloat(values[42]) * 10);
//		System.out.print("\n新空快速" + xkKaSu[i]);

		xkTeKa[i] = xkKaSu[i].copy();
		xkTeKa[i].level = DBTrain.LEVEL_TK;
//		System.out.print("\n新空特快" + xkTeKa[i]);
	
		xkZiTe[i] = xkKaSu[i].copy();
		xkZiTe[i].level = DBTrain.LEVEL_ZT;
//		System.out.print("\n新空直特" + xkZiTe[i]);

		pcPuKe[i].distFrom = distFrom;
		pcPuKe[i].distTo = distTo;
		pcPuKe[i].level = DBTrain.LEVEL_PKe;
		pcPuKe[i].type = DBTrain.TYPE_PC;
		pcPuKe[i].yingzuo = (int) (Float.parseFloat(values[1]) * 10);
		pcPuKe[i].ruanzuo = (int) (Float.parseFloat(values[13]) * 10);
		pcPuKe[i].yingwoU = (int) (Float.parseFloat(values[4]) * 10);
		pcPuKe[i].yingwoM = (int) (Float.parseFloat(values[5]) * 10);
		pcPuKe[i].yingwoD = (int) (Float.parseFloat(values[6]) * 10);
		pcPuKe[i].ruanwoU = (int) (Float.parseFloat(values[16]) * 10);
		pcPuKe[i].ruanwoD = (int) (Float.parseFloat(values[17]) * 10);
//		System.out.print("\n普车普客" + pcPuKe[i]);

		pcPuKa[i].distFrom = distFrom;
		pcPuKa[i].distTo = distTo;
		pcPuKa[i].level = DBTrain.LEVEL_PKa;
		pcPuKa[i].type = DBTrain.TYPE_PC;
		pcPuKa[i].yingzuo = (int) (Float.parseFloat(values[2]) * 10);
		pcPuKa[i].ruanzuo = (int) (Float.parseFloat(values[14]) * 10);
		pcPuKa[i].yingwoU = (int) (Float.parseFloat(values[7]) * 10);
		pcPuKa[i].yingwoM = (int) (Float.parseFloat(values[8]) * 10);
		pcPuKa[i].yingwoD = (int) (Float.parseFloat(values[9]) * 10);
		pcPuKa[i].ruanwoU = (int) (Float.parseFloat(values[18]) * 10);
		pcPuKa[i].ruanwoD = (int) (Float.parseFloat(values[19]) * 10);
//		System.out.print("\n普车普快" + pcPuKa[i]);

		pcKaSu[i].distFrom = distFrom;
		pcKaSu[i].distTo = distTo;
		pcKaSu[i].level = DBTrain.LEVEL_KS;
		pcKaSu[i].type = DBTrain.TYPE_PC;
		pcKaSu[i].yingzuo = (int) (Float.parseFloat(values[3]) * 10);
		pcKaSu[i].ruanzuo = (int) (Float.parseFloat(values[15]) * 10);
		pcKaSu[i].yingwoU = (int) (Float.parseFloat(values[10]) * 10);
		pcKaSu[i].yingwoM = (int) (Float.parseFloat(values[11]) * 10);
		pcKaSu[i].yingwoD = (int) (Float.parseFloat(values[12]) * 10);
		pcKaSu[i].ruanwoU = (int) (Float.parseFloat(values[20]) * 10);
		pcKaSu[i].ruanwoD = (int) (Float.parseFloat(values[21]) * 10);
//		System.out.print("\n普车快速" + pcKaSu[i]);
		
		pcTeKa[i] = pcKaSu[i].copy();
		pcTeKa[i].level = DBTrain.LEVEL_TK;
//		System.out.print("\n普车特快" + pcTeKa[i]);
		
		pcZiTe[i] = pcKaSu[i].copy();
		pcZiTe[i].level = DBTrain.LEVEL_ZT;
//		System.out.print("\n普车直特" + pcZiTe[i]);
	}
	
	public void writeToDB() throws SQLException {
		STDB db = new STDB();
		db.connectDB();
		
		for(int i=0; i<NUM; i++) {
			db.insertPJB(pcPuKe[i]);
			db.insertPJB(pcPuKa[i]);
			db.insertPJB(pcKaSu[i]);
			db.insertPJB(pcTeKa[i]);
			db.insertPJB(pcZiTe[i]);
			db.insertPJB(xkPuKe[i]);
			db.insertPJB(xkPuKa[i]);
			db.insertPJB(xkKaSu[i]);
			db.insertPJB(xkTeKa[i]);
			db.insertPJB(xkZiTe[i]);
		}
		
		db.closeDB();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PJBReader pjb = new PJBReader("C:\\trains\\");
		
		try {
			pjb.readPJB();
			pjb.writeToDB();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
