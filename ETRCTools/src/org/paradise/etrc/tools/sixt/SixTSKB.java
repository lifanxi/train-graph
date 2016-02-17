package org.paradise.etrc.tools.sixt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

import org.paradise.etrc.data.Chart;
import org.paradise.etrc.data.Circuit;
import org.paradise.etrc.data.Stop;
import org.paradise.etrc.data.Train;
import org.paradise.etrc.tools.CircuitMake;

public class SixTSKB {
	Vector stations;
	Vector trains;
	String[][] times;
	File f;
	
	private boolean isDown;
	
	public SixTSKB(String fileName, boolean _isDown) throws IOException {
		isDown = _isDown;
		f = new File (fileName);
		
		stations = new Vector();
		trains = new Vector();
		
		readCZ();
		System.out.println(stations);
		readCC();
		System.out.println(trains);
		
		times = new String[trains.size()][stations.size()*2];
		readTK();
		
		formatTimes();
	}
	
	private void formatTimes() {
		for(int tra=0; tra<trains.size(); tra++) {
			Train train = (Train) trains.get(tra);
			System.out.print(train.getTrainName());
			
			int hour = -1;
			for(int sta=0; sta<stations.size(); sta++) {
				String arrive = times[tra][sta*2];
				String leave = times[tra][sta*2+1];
				
				//双空，不做
				if(isEmpty(arrive) && isEmpty(leave)) {
					continue;
				}
				//仅leave空，把arrive给leave
				else if(isEmpty(leave)) {
					leave = arrive;
				}
				//仅arrive空，把leave给arrive
				else if(isEmpty(arrive)) {
					arrive = leave;
				}
				
				hour = getHour(hour, arrive);
				int hourA = hour;
				int minA = getMin(arrive);
				
				hour = getHour(hour, leave);
				int hourL = hour;
				int minL = getMin(leave);
				
				String myStaName;
				if(isDown)
					myStaName = (String) stations.get(sta);
				else
					myStaName = (String) stations.get(stations.size() - sta - 1);
				
				String myArrive = Train.intToTrainTime(hourA * 60 + minA);
				String myLeave = Train.intToTrainTime(hourL * 60 + minL);

				System.out.print(" <" + myStaName + ">");
				System.out.print(myArrive + "|");
				System.out.print(myLeave + "|");

				Stop stop = new Stop(myStaName, myArrive, myLeave, true);
				
				train.appendStop(stop);
			}
			System.out.println();
		}
	}
	
	private boolean isEmpty(String time) {
		if(time == null)
			return true;
		
		if(time.equalsIgnoreCase(""))
			return true;
		
		if(time.equalsIgnoreCase("..."))
			return true;

		if(time.equalsIgnoreCase("--"))
			return true;
		
		return false;
	}
	
	private int getMin(String time) {
		if(time.indexOf(":") >= 0)
			time = time.split(":")[1];
		
		if(time.length() == 4) {
			String minStr = time.substring(0,2);
			String secStr = time.substring(2,4);
			int min = Integer.parseInt(minStr);
			int sec = Integer.parseInt(secStr);
			return (sec>=30) ? min + 1 : min;
		}
		else
			return Integer.parseInt(time);
	}

	private int getHour(int oldHour, String time) {
		if(time.indexOf(":") >= 0)
			return Integer.parseInt(time.split(":")[0]);
		else
			return oldHour;
	}
	
	void readCZ() throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(f));
		String line = in.readLine();
		int lineNum = 1;
		while(line != null) {	
			paserLineCZ(lineNum, line);
			
			line = in.readLine();
			lineNum ++;
		}
		
		stations.removeElementAt(stations.size()-1);
	}
	
	private void paserLineCZ(int lineNum, String line) {
		String cell[] = line.split(",");
		
		if(lineNum >= 3 && lineNum % 2 == 1) {
			System.out.println(cell.length + ":" + cell[0]);
			stations.add(cell[0]);
		}
	}

	private void readCC() throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(f));
		String firstLine = in.readLine();
		String secondLine = in.readLine();
		int lineNum = 2;
		String line = secondLine;
		String lastLine = "";
		while(line != null) {	
			lastLine = line;
			line = in.readLine();
			lineNum ++;
		}
		
		System.out.println(firstLine);
		System.out.println(secondLine);
		System.out.println(lastLine);
		
		String[] starts = firstLine.split(",");
		String[] ends = lastLine.split(",");
		String[] traNames = secondLine.split(",");
		
		for(int i=0; i<traNames.length; i++) {
			String[] myNames = Train.formatName(traNames[i]);
			
//			String fullName = Train.makeFullName(myNames);

			Train train = new Train();
//			train.trainNameFull = fullName;
			train.setTrainNames(myNames);
			train.setStartStation(starts[i]);
			train.setTerminalStation(ends[i]);
			
			trains.add(train);
		}
		
		trains.removeElementAt(0);
	}

	private void readTK() throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(f));
		String line = in.readLine();
		int lineNum = 1;
		while(line != null) {	
			paserLineTK(lineNum, line);
			
			line = in.readLine();
			lineNum ++;
		}
	}

	private void paserLineTK(int lineNum, String line) {
		String cell[] = line.split(",");
		
		int timeIndex = (lineNum - 3);
		int timeNum = stations.size()*2;
		if(timeIndex >= 0 && timeIndex < timeNum) {
			for(int trainIndex=1; trainIndex<cell.length; trainIndex++) {
//				System.out.println(((Train) trains.get(trainIndex-1)).getTrainName() + "-" 
//						           + stations.get(timeIndex/2) + "***" 
//						           + cell[trainIndex]);
				String time = cell[trainIndex].replaceAll(" ", "");

				if(isDown)
					times[trainIndex-1][timeIndex] = time;
				//上行车，反向读入时刻表
				else
					times[trainIndex-1][timeNum - timeIndex  - 1] = time;
			}
		}
	}

	public static void main(String argv[]) {
		try {
			String path = "C:\\trains\\00. lgData\\418\\";
			String skbFile = path + "418宁沪上.csv";
			String lcbFile = path + "宁沪线路.csv";
			String trcFile = path + "418宁沪上.trc";
			
			Circuit cir = CircuitMake.readCircuit(lcbFile, "418宁沪上");
			SixTSKB six = new SixTSKB(skbFile, false);

			Chart chart = new Chart(cir);
			for(int i=0; i<six.trains.size(); i++) {
				System.out.println(six.trains.get(i));
				chart.addTrain((Train) six.trains.get(i));
			}
			
			chart.saveToFile(new File(trcFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
