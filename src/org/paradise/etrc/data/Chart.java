package org.paradise.etrc.data;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;

import static org.paradise.etrc.ETRC._;

public class Chart {
	//Y轴（距离）显示参数
	public int distScale = 3; //每公里像素数
	public int displayLevel = 4; //最低可显示车站等级
	public int boldLevel = 2; //最低粗线显示车站等级（特等为0）

	//X轴（时间）显示参数
	public int startHour = 18; //0坐标时刻（0-23）
	public int minuteScale = 2; //每分钟像素数
	public int timeInterval = 10; //时间轴间隔（必须是60的约数，即可以是5，6，10等，但不能是7分钟）

	//本运行图的线路
	public Circuit circuit;

	//本运行图所包含的车次，最多600趟
	public static final int MAX_TRAIN_NUM = 600;
	public Train trains[] = new Train[MAX_TRAIN_NUM];

	//本运行图的车次总数
	public int trainNum = 0;
	//下行车次数目
	public int dNum = 0;
	//上行车次数目
	public int uNum = 0;
//下行全程车数目
	public int dNum_all = 0;	
//上行全程车数目
	public int uNum_all = 0;
//下行本线车数目
	public int dNum_benxian = 0;	
//上行本线车数目
	public int uNum_benxian = 0;
	
	public Color G_color;
	public Color D_color;
	public Color C_color;
	public Color Z_color;
	public Color T_color;
	public Color K_color;
	public Color L_color;	
    public Color Y_color;
    public Color default_color;	
	
	public Chart(File f) throws IOException {
		loadFromFile(f);
	}
	
	public Chart(Circuit cir) {
		circuit = cir;
	}

	


	
	public void addTrain(Train loadingTrain) {
		if (trainNum >= MAX_TRAIN_NUM)
			return;

		if (isLoaded(loadingTrain)) {
			updateTrain(loadingTrain);
			return;
		}

		if (loadingTrain.color == null)
			{
			//loadingTrain.color = loadingTrain.getDefaultColor();

String trname = loadingTrain.getTrainName();
char cc = trname.toUpperCase().charAt(0);

if ( cc=='G')
{
loadingTrain.color =  G_color ;
}	
else if ( cc=='D')
{
loadingTrain.color =  D_color ;
}
else if ( cc=='C')
{
loadingTrain.color =  C_color ;
}
else if ( cc=='Z')
{
loadingTrain.color =  Z_color ;
}
else if ( cc=='T')
{
loadingTrain.color =  T_color ;
}
else if ( cc=='K')
{
loadingTrain.color =  K_color ;
}
else if ( cc=='L')
{
loadingTrain.color =  L_color ;
}
else if ( cc=='Y')
{
loadingTrain.color =  Y_color ;
}
else
{
loadingTrain.color =  default_color;
}
//	System.out.print(trname+ ":"+cc+":"+ loadingTrain.color+"\n");
		}
//			loadingTrain.color = getNextTrainColor();

		this.trains[trainNum] = loadingTrain;
		//2015-12-10增加
		String my_start = loadingTrain.getStartStation();
		String my_end = loadingTrain.getTerminalStation();
		String my_train = loadingTrain.getTrainName();
		String start_station_name= circuit.stations[0].name;
		String end_station_name= circuit.stations[circuit.stationNum-1].name;
		//start_match: 表征当前车次起点站是否为当前线路的车站
		//end_match: 表征当前车次终点站是否为当前线路的车站
		boolean start_match = false ;
		boolean end_match = false ;
		switch (loadingTrain.isDownTrain(circuit)) {
		case Train.DOWN_TRAIN:
			dNum++;	
			if (my_start.equals(start_station_name)&& my_end.equals(end_station_name)) {
			dNum_all++;
			}


			for(int i = 0; i<circuit.stationNum; i++) {
				if(my_start.equals(circuit.stations[i].name)) {
					start_match = true ;
				}
				if(my_end.equals(circuit.stations[i].name)) {
					end_match = true ;
				}		
			}
			
			if ( start_match && end_match) 
			{
			dNum_benxian ++ ;	
			System.out.print("下行本线车"+ my_train +"\n");			
			}
		
			break;
		case Train.UP_TRAIN:
			uNum++;
			if (my_start.equals(end_station_name)&& my_end.equals(start_station_name)) {
			uNum_all++;
			}
			
			for(int i = 0; i<circuit.stationNum; i++) {
				if(my_start.equals(circuit.stations[i].name)) {
					start_match = true ;
				}
				if(my_end.equals(circuit.stations[i].name)) {
					end_match = true ;
				}		
			}
			
			if ( start_match && end_match) 
			{
			uNum_benxian ++ ;
			System.out.print("上行本线车"+ my_train +"\n");		
			}
			
			
			break;
		}
		trainNum++;


		//System.out.print(my_train+"起点"+my_start+"终点"+my_end+"线路："+ start_station_name+"-"+end_station_name+"\n");
		//System.out.print("\n");
//	    String direction="";
//	    switch(loadingTrain.isDownTrain(circuit)){
//	      case Train.DOWN_TRAIN:
//	        direction = "下行";
//	        break;
//	      case Train.UP_TRAIN:
//	        direction = "上行";
//	        break;
//	      default:
//	        direction = "未知";
//	    }
//	    System.out.println(loadingTrain.getTrainName(circuit) + "次" + direction);
	}
	
	public Train findTrain(String trainName) {
		for(int i=0; i<trainNum; i++) {
			if(trains[i].getTrainName(circuit).equals(trainName))
				return trains[i];
		}
		
		return null;
	}
	
	public boolean containTrain(Train train) {
		for(int i=0; i<trainNum; i++) {
			if(trains[i].equals(train))
				return true;
		}
		
		return false;
	}

	public void updateTrain(Train newTrain) {
		for (int i = 0; i < trains.length; i++) {
			if (newTrain.equals(trains[i])) {
				if(newTrain.color == null)
					newTrain.color = trains[i].color;
				trains[i] = newTrain;
			}
		}
	}
	
	public void delTrain(int index) {
		if ((index < 0) || (index >= MAX_TRAIN_NUM))
			return;

		Train[] newTrains = new Train[MAX_TRAIN_NUM];
        
		//thetrain为将要删除的train
		Train theTrain = trains[index];
		int j = 0;
		for (int i = 0; i < index; i++) {
			newTrains[j++] = trains[i];
		}

		for (int i = index + 1; i < trainNum; i++) {
			newTrains[j++] = trains[i];
		}

		trains = newTrains;
		//2015-12-10修正bug:删除列车时应该更新dNum，uNum，包括主页面时刻表格的表头部分D:xx U:xx

		//2015-12-10增加
		String my_start = theTrain.getStartStation();
		String my_end = theTrain.getTerminalStation();
		String my_train = theTrain.getTrainName();
		String start_station_name= circuit.stations[0].name;
		String end_station_name= circuit.stations[circuit.stationNum-1].name;	
//System.out.print(my_train+"起点"+my_start+"终点"+my_end+"线路："+ start_station_name+"-"+end_station_name+"\n");	

		//start_match: 表征当前车次起点站是否为当前线路的车站
		//end_match: 表征当前车次终点站是否为当前线路的车站
		boolean start_match = false ;
		boolean end_match = false ;


	
		switch (theTrain.isDownTrain(circuit)) {
		case Train.DOWN_TRAIN:
			dNum--;
			if (my_start.equals(start_station_name)&& my_end.equals(end_station_name)) {
			dNum_all--;
			}
			
			for(int i = 0; i<circuit.stationNum; i++) {
				if(my_start.equals(circuit.stations[i].name)) {
					start_match = true ;
				}
				if(my_end.equals(circuit.stations[i].name)) {
					end_match = true ;
				}		
			}
			
			if ( start_match && end_match) 
			{
			dNum_benxian -- ;	
			System.out.print("删除下行本线车"+ my_train +"\n");			
			}
			
			
			break;
		case Train.UP_TRAIN:
			uNum--;
			if (my_start.equals(end_station_name)&& my_end.equals(start_station_name)) {
			uNum_all--;
			}
			
			for(int i = 0; i<circuit.stationNum; i++) {
				if(my_start.equals(circuit.stations[i].name)) {
					start_match = true ;
				}
				if(my_end.equals(circuit.stations[i].name)) {
					end_match = true ;
				}		
			}
			
			if ( start_match && end_match) 
			{
			uNum_benxian --;
			System.out.print("删除上行本线车"+ my_train +"\n");		
			}	
			
			break;
		}		
		trainNum--;
	}
	
	public void delTrain(Train theTrain) {
		if(!isLoaded(theTrain))
			return;
		

//2015-12-10修正bug:删除列车时应该更新dNum，uNum
		//2015-12-10增加
		String my_start = theTrain.getStartStation();
		String my_end = theTrain.getTerminalStation();
		String my_train = theTrain.getTrainName();
		String start_station_name= circuit.stations[0].name;
		String end_station_name= circuit.stations[circuit.stationNum-1].name;
		//start_match: 表征当前车次起点站是否为当前线路的车站
		//end_match: 表征当前车次终点站是否为当前线路的车站
		boolean start_match = false ;
		boolean end_match = false ;
		switch (theTrain.isDownTrain(circuit)) {
		case Train.DOWN_TRAIN:
			dNum--;
			if (my_start.equals(start_station_name)&& my_end.equals(end_station_name)) {
			dNum_all--;
			}
			for(int i = 0; i<circuit.stationNum; i++) {
				if(my_start.equals(circuit.stations[i].name)) {
					start_match = true ;
				}
				if(my_end.equals(circuit.stations[i].name)) {
					end_match = true ;
				}		
			}		
			if ( start_match && end_match) 
			{
			dNum_benxian -- ;	
			System.out.print("删除下行本线车"+ my_train +"\n");			
			}			
			break;
		case Train.UP_TRAIN:
			uNum--;
			if (my_start.equals(end_station_name)&& my_end.equals(start_station_name)) {
			uNum_all--;
			}
			
			for(int i = 0; i<circuit.stationNum; i++) {
				if(my_start.equals(circuit.stations[i].name)) {
					start_match = true ;
				}
				if(my_end.equals(circuit.stations[i].name)) {
					end_match = true ;
				}		
			}
			
			if ( start_match && end_match) 
			{
			uNum_benxian --;
			System.out.print("删除上行本线车"+ my_train +"\n");		
			}
			
			break;
		}
		
		Train[] newTrains = new Train[MAX_TRAIN_NUM];

		int j = 0;
		for (int i = 0; i < trainNum; i++) {
			if (!trains[i].equals(theTrain)) {
				newTrains[j++] = trains[i];
			}
		}

		trains = newTrains;
		trainNum--;
	}

	//画运行线的颜色
//	private static int trainColorIndex = 0;
//
//	public static Color trainColors[] = { Color.red, Color.blue, Color.cyan,
//			Color.green, Color.magenta, Color.orange, new Color(172, 0, 172) /*Color.gray, Color.pink*/
//	};
//
//	private Color getNextTrainColor() {
//		trainColorIndex++;
//		if (trainColorIndex >= trainColors.length) {
//			trainColorIndex = 0;
//		}
//		return trainColors[trainColorIndex];
//	}
//
	/**
	 * 判断指定的车次是否已经在本运行图中
	 * @param _t
	 * @return
	 */
	public boolean isLoaded(Train _t) {
//		for (int i = 0; i < trainNum; i++) {
//			if (_t.getTrainName(circuit).equalsIgnoreCase(
//					trains[i].getTrainName(circuit)))
//				return true;
//		}
		for (int i = 0; i < trainNum; i++) {
			if (_t.getTrainName().equalsIgnoreCase(trains[i].getTrainName()))
				return true;
		}

		return false;
	}

	/*
	 * 文件操作
	 */
	public static final String circuitPattern = "***Circuit***";
	public static final String trainPattern = "===Train===";
	public static final String colorPattern = "---Color---";
    public static final String linetypePattern = "---LineType---";
	public static final String setupPattern = "...Setup...";

	public void saveToFile(File f) throws IOException {
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));

		//线路
		out.write(circuitPattern);
		out.newLine();
		circuit.writeTo(out);
		//车次
		for (int i = 0; i < trainNum; i++) {
			out.write(trainPattern);
			out.newLine();
			trains[i].writeTo(out);
		}
		//颜色
		out.write(colorPattern);
		out.newLine();
		for (int i = 0; i < trainNum; i++) {
			out.write(trains[i].getTrainName());
			out.write("," + trains[i].color.getRed());
			out.write("," + trains[i].color.getGreen());
			out.write("," + trains[i].color.getBlue());
			out.newLine();
		}
        //线型（0~2）+线宽（1~4f）
		out.write(linetypePattern);
		out.newLine();
		for (int i = 0; i < trainNum; i++) {
			out.write(trains[i].getTrainName());
			out.write("," + trains[i].linestytletype);
            out.write("," + trains[i].linewidth );
		//	System.out.println(trains[i].linewidth);
			out.newLine();
		}

		//设置
		out.write(setupPattern);
		out.newLine();
		out.write(distScale + "," +
				  displayLevel + "," +
				  boldLevel + "," +
				  startHour + "," +
				  minuteScale + "," +
				  timeInterval);
		out.newLine();

		out.flush();
		out.close();
	}

	public void loadFromFile(File f) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new BOMStripperInputStream(new FileInputStream(f)),"UTF-8"));

		//读取文件状态
		final int READING_CIRCUIT = 1;
		final int READING_TRAIN = 2;
		final int READING_COLOR = 3;
		final int READING_LINESTYTLETYPE = 4;
        final int READING_SETUP = 5;
		int reading_state = 0;
		int lineNum = 0;

		String line = in.readLine();
		if (line == null)
			throw new IOException("运行图文件格式错");

		Circuit readingCircuit = new Circuit();
		Train readingTrains[] = new Train[MAX_TRAIN_NUM];
		int readTrainNum = 0;

		while (line != null) {
			if (line.equalsIgnoreCase(circuitPattern)) {
				reading_state = READING_CIRCUIT;
				lineNum = 0;
			} else if (line.equalsIgnoreCase(trainPattern)) {
				reading_state = READING_TRAIN;
				readingTrains[readTrainNum] = new Train();
				lineNum = 0;
				readTrainNum++;
			} else if (line.equalsIgnoreCase(colorPattern)) {
				reading_state = READING_COLOR;
				lineNum = 0;
			} else if (line.equalsIgnoreCase(linetypePattern)) {
				reading_state = READING_LINESTYTLETYPE;
				lineNum = 0;
			} else if (line.equalsIgnoreCase(setupPattern)) {
				reading_state = READING_SETUP;
				lineNum = 0;
			} else {
				switch (reading_state) {
				case READING_CIRCUIT:
					readingCircuit.parseLine(line, lineNum);
					lineNum++;
					break;
				case READING_TRAIN:
					readingTrains[readTrainNum - 1].parseLine(line, lineNum);
					lineNum++;
					break;
				case READING_COLOR:
					parseColor(line, readingTrains, readTrainNum);
					lineNum++;
					break;
				case READING_LINESTYTLETYPE:
					parseLineType(line, readingTrains, readTrainNum);
					lineNum++;
					break;
				case READING_SETUP:
					parseSetup(line);
					lineNum++;
					break;
				default:
					lineNum++;
				}
			}
			line = in.readLine();
		}

		this.circuit=readingCircuit;

		trainNum = 0;
		dNum = 0;
		uNum = 0;
		uNum_all = 0;
		dNum_all = 0;
		for (int i = 0; i < readTrainNum; i++) {
			addTrain(readingTrains[i]);
		}

		//System.out.println("下行："+dNum+"，上行："+uNum);
	}

	private void parseSetup(String line) throws IOException {
		String setup[] = line.split(",");
		try {
			distScale = Integer.parseInt(setup[0]);
			displayLevel = Integer.parseInt(setup[1]);
			boldLevel = Integer.parseInt(setup[2]);
			startHour = Integer.parseInt(setup[3]);
			minuteScale = Integer.parseInt(setup[4]);
			timeInterval = Integer.parseInt(setup[5]);

		} catch (Exception e) {
			throw new IOException(_("Unable to read chart settings."));
		}
	}
	
	/**
	 * parseColor
	 *
	 * @param readingTrains Train[]
	 */
	private void parseColor(String line, Train[] readingTrains, int readTrainNum)
			throws IOException {
		String colorLine[] = line.split(",");

		if (colorLine.length < 4)
			throw new IOException(_("Error reading color."));

		int r = 255;
		int g = 255;
		int b = 255;
		try {
			r = Integer.parseInt(colorLine[1]);
			g = Integer.parseInt(colorLine[2]);
			b = Integer.parseInt(colorLine[3]);
		} catch (Exception e) {
			throw new IOException(String.format(_("Error reading color settings for the train %s."), colorLine[0]));
		}

		for (int i = 0; i < readTrainNum; i++) {
			if (readingTrains[i].getTrainName().equalsIgnoreCase(colorLine[0])) {
				readingTrains[i].color = new Color(r, g, b);
				//System.out.println(readingTrains[i].getTrainName()+":"+r+","+g+","+b);
			}
		}
	}

	private void parseLineType(String line, Train[] readingTrains, int readTrainNum)
			throws IOException {
		String StytleLine[] = line.split(",");

		int linestytle = 0 ;
		float linewidth = 1.0f ;
		try {
		linestytle = Integer.parseInt(StytleLine[1]);	
		} catch (Exception e) {
			throw new IOException(String.format(_("Error reading line style settings for the train %s."), StytleLine[0]));
		}
		try {
		linewidth = Float.parseFloat(StytleLine[2]);
		} catch (Exception e) {
			throw new IOException(String.format(_("Error reading line width settings for the train %s."), StytleLine[0]));
		}	
		
		
		

		for (int i = 0; i < readTrainNum; i++) {
			if (readingTrains[i].getTrainName().equalsIgnoreCase(StytleLine[0])) {
				//将etrc文件中关于线型的设置赋给class train的 linestytletype
				readingTrains[i].linestytletype = linestytle;
				//将etrc文件中关于线宽的设置赋给class train的 linestytletype
				readingTrains[i].linewidth = linewidth;
			}
		}
	}


	
	  //测试用
	  public static void main(String argv[]) {
	    Chart chart = null;
	    try {
	      chart = new Chart(new File("d:\\huning2.trc"));
	    }
	    catch (IOException ex) {
	      System.out.println("Error:" + ex.getMessage());
	    }
	    
	    System.out.print(chart.circuit.toString());
	    for (int i = 0; i < chart.trainNum; i++) {
			System.out.print("==== " + i + " ==== (");
			System.out.println(chart.trains[i].color.getRed() + "," + 
					           chart.trains[i].color.getGreen() + "," + 
					           chart.trains[i].color.getBlue() + ") ====");
			System.out.print(chart.trains[i].toString());
		}
	    System.out.println("\nSettings: " + 
	    		  chart.distScale + "," +
	    		  chart.displayLevel + "," +
	    		  chart.boldLevel + "," +
	    		  chart.startHour + "," +
	    		  chart.minuteScale + "," +
	    		  chart.timeInterval);
	    
	    chart.distScale ++;
	    
	    try {
			chart.saveToFile(new File("d:\\huning3.trc"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	  }

	public void clearTrains() {
		trainNum = 0;
		dNum = 0;
		uNum = 0;
		dNum_all = 0;
		uNum_all = 0;
		
	}

	public void insertNewStopToTrain(Train theTrain, Stop stop) {
		if (theTrain.isDownTrain(circuit) == Train.DOWN_TRAIN) {
			insertNewStopToTrainDown(theTrain, stop);
		} else
			insertNewStopToTrainUp(theTrain, stop);
	}

	private void insertNewStopToTrainUp(Train theTrain, Stop stop) {
		int newDist = this.circuit.getStationDist(stop.stationName);
		
		//不在本线 返回 null
		if(newDist < 0)
			return;
		
		//新站在theTrain在本线的第一个停靠站之前 插在第一个站之前
		Station firstStop = this.circuit.getFirstStopOnMe(theTrain);
		if (firstStop == null) {
			theTrain.insertStop(stop, 0);
		}
		else {
			int firstDist = this.circuit.getStationDist(firstStop.name);
			if(newDist > firstDist)
				theTrain.insertStop(stop, theTrain.findStopIndex(firstStop.name));
		}
		//新站在theTrain在本线的最后一个停靠站之后 append在最后一个站之后
		Station lastStop = this.circuit.getLastStopOnMe(theTrain);
		if (lastStop == null) {
			theTrain.appendStop(stop);
		}
		else {
			int lastDist = this.circuit.getStationDist(lastStop.name);
			if(newDist < lastDist)
				theTrain.appendStop(stop);
		}
		//新站在theTrain的第一个停靠站和最后一个停靠站之间
		//遍历theTrain的所有停站
		for(int i=0; i<theTrain.stopNum-1; i++) {
			int dist1 = circuit.getStationDist(theTrain.stops[i].stationName);
			int dist2 = circuit.getStationDist(theTrain.stops[i+1].stationName);
			
			if(dist1 >= 0 && dist2 >=0)
				//如果新站距离在两个站之间，则应当插在第一个站之后（返回第一个站）
				if(dist1 > newDist  && newDist > dist2)
					theTrain.insertStopAfter(theTrain.stops[i], stop);
		}
	}

	private void insertNewStopToTrainDown(Train theTrain, Stop stop) {
		int newDist = this.circuit.getStationDist(stop.stationName);
		
		//不在本线 返回 null
		if(newDist < 0)
			return;
		
		//新站在theTrain在本线的第一个停靠站之前 插在第一个站之前
		Station firstStop = this.circuit.getFirstStopOnMe(theTrain);
		if (firstStop == null) {
			theTrain.insertStop(stop, 0);
		}
		else
		{
			int firstDist = this.circuit.getStationDist(firstStop.name);
			if(newDist < firstDist)
				theTrain.insertStop(stop, theTrain.findStopIndex(firstStop.name));
		}
		//新站在theTrain在本线的最后一个停靠站之后 append在最后一个站之后
		Station lastStop = this.circuit.getLastStopOnMe(theTrain);
		if (lastStop == null) {
			theTrain.appendStop(stop);
		}
		else
		{
			int lastDist = this.circuit.getStationDist(lastStop.name);
			if(newDist > lastDist)
				theTrain.appendStop(stop);
		}
		//新站在theTrain的第一个停靠站和最后一个停靠站之间
		//遍历theTrain的所有停站
		for(int i=0; i<theTrain.stopNum-1; i++) {
			int dist1 = circuit.getStationDist(theTrain.stops[i].stationName);
			int dist2 = circuit.getStationDist(theTrain.stops[i+1].stationName);
			
			if(dist1 >= 0 && dist2 >=0)
				//如果新站距离在两个站之间，则应当插在第一个站之后（返回第一个站）
				if(dist1 < newDist  && newDist < dist2)
					theTrain.insertStopAfter(theTrain.stops[i], stop);
		}
	}
}
