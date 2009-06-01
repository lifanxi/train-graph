package org.paradise.etrc.data;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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

	public Chart(File f) throws IOException {
		loadFromFile(f);
	}

	public void addTrain(Train loadingTrain) {
		if (trainNum >= MAX_TRAIN_NUM)
			return;

		if (isLoaded(loadingTrain)) {
			updateTrain(loadingTrain);
			return;
		}

		if (loadingTrain.color == null)
			loadingTrain.color = loadingTrain.getDefaultColor();
//			loadingTrain.color = getNextTrainColor();

		this.trains[trainNum] = loadingTrain;

		switch (loadingTrain.isDownTrain(circuit)) {
		case Train.DOWN_TRAIN:
			dNum++;
			break;
		case Train.UP_TRAIN:
			uNum++;
			break;
		}
		trainNum++;

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

		int j = 0;
		for (int i = 0; i < index; i++) {
			newTrains[j++] = trains[i];
		}

		for (int i = index + 1; i < trainNum; i++) {
			newTrains[j++] = trains[i];
		}

		trains = newTrains;
		trainNum--;
	}
	
	public void delTrain(Train theTrain) {
		if(!isLoaded(theTrain))
			return;
		
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
	public static final String setupPattern = "...Setup...";

	public void saveToFile(File f) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(f));

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
		BufferedReader in = new BufferedReader(new FileReader(f));

		//读取文件状态
		final int READING_CIRCUIT = 1;
		final int READING_TRAIN = 2;
		final int READING_COLOR = 3;
		final int READING_SETUP = 4;

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
			throw new IOException("运行图设置读取错误");
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
			throw new IOException("颜色读取错");

		int r = 255;
		int g = 255;
		int b = 255;
		try {
			r = Integer.parseInt(colorLine[1]);
			g = Integer.parseInt(colorLine[2]);
			b = Integer.parseInt(colorLine[3]);
		} catch (Exception e) {
			throw new IOException(colorLine[0] + "次颜色读取错误");
		}

		for (int i = 0; i < readTrainNum; i++) {
			if (readingTrains[i].getTrainName().equalsIgnoreCase(colorLine[0])) {
				readingTrains[i].color = new Color(r, g, b);
				//System.out.println(readingTrains[i].getTrainName()+":"+r+","+g+","+b);
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
	}
}
