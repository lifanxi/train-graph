package org.paradise.etrc.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import org.paradise.etrc.data.Circuit;
import org.paradise.etrc.data.Station;

public class CircuitMake {
	public CircuitMake() {
		
	}
	
	public Circuit readCircuit(String name) throws IOException {
		File file = new File("c:\\trains\\" + name + ".csv");
		
		BufferedReader in = new BufferedReader(new FileReader(file));

		String line;

		Circuit cir = new Circuit();
		cir.name = name;
		while ((line = in.readLine()) != null) {
			//System.out.println(parseStationLine(line));
			cir.appendStation(parseStationLine(line));
		}
		
		cir.length = cir.stations[cir.stationNum - 1].dist;
		
		return cir;
	}

	private Station parseStationLine(String line) {
//		System.out.println(line);
		String str[] = line.split(",");
		
		String name = str[0].substring(0, str[0].length()-1);
//		String name = str[0];
		
		int level = 6;
		if(str[2].equalsIgnoreCase("特等站"))
			level = 0;
		else if(str[2].equalsIgnoreCase("一等站"))
			level = 1;
		else if(str[2].equalsIgnoreCase("二等站"))
			level = 2;
		else if(str[2].equalsIgnoreCase("三等站"))
			level = 3;
		else if(str[2].equalsIgnoreCase("四等站"))
			level = 4;
		else if(str[2].equalsIgnoreCase("五等站"))
			level = 5;

		int dist = Integer.parseInt(str[3]);
		
		return new Station(name, dist, level, false);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String name = "合九线";
			
			Circuit cir = new CircuitMake().readCircuit(name);
			System.out.println(cir);
			
			File file = new File("c:\\trains\\" + name + ".cir");
			
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			cir.writeTo(out);
			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
