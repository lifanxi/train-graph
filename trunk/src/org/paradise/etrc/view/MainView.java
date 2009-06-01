package org.paradise.etrc.view;

import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.paradise.etrc.*;
import org.paradise.etrc.data.*;

/**
 * @author lguo@sina.com
 * @version 1.0
 */
public class MainView extends JPanel implements KeyListener {
	/*
	 * X轴（时间轴）
	 * 名称        类型   单位
	 * clock       Date
	 * coordinate  int    分钟    = clock - startHour
	 * point.x     int    像素    = coordinate * timeScale + leftMargin
	 *
	 * Y轴
	 * 名称         类型  单位
	 * dist        int   公里
	 * point.y     int   像素     = dist * distScale + topMargin
	 */
	private static final long serialVersionUID = -2507031211251679563L;

	public static final int DEFAULT_LEFT_MARGIN = 45;
	public static final int DEFAULT_RIGHT_MARGIN = 45;
	public static final int DEFAULT_TOP_MARGIN = 66;
	public static final int DEFAULT_BOTTOM_MARGIN = 66;

	public int leftMargin = DEFAULT_LEFT_MARGIN;
	public int rightMargin = DEFAULT_RIGHT_MARGIN;
	public int topMargin = DEFAULT_TOP_MARGIN;
	public int bottomMargin = DEFAULT_BOTTOM_MARGIN;

	public int trainNameRecMargin = 1;
	public int trainNameRecHeight = 54;
	public int circuitPanelWidth = 80;
	public int clockPanelHeight = 16;

	public static final int ScrollUnitIncrement = 16;

	public Vector trainDrawings = new Vector();
	public Vector underDrawings = new Vector();

	public TrainDrawing activeTrainDrawing;

	private CircuitPanel panelCircuit;
	private LinesPanel panelLines;
	private ClockPanel panelClock;

	private JScrollPane spLines = new JScrollPane();

//	public Color gridColor = Color.darkGray;
	public Color gridColor = Color.GRAY;

	public MainFrame mainFrame;

	public MainView(MainFrame _mainFrame) {
		mainFrame = _mainFrame;
		panelCircuit = new CircuitPanel(mainFrame.chart, this);
		panelClock = new ClockPanel(mainFrame.chart, this);
		panelLines = new LinesPanel(mainFrame.chart, this);
		
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean findAndMoveToTrain(String trainName) {
//		System.out.println("findAndMoveToTrain:" + trainName);
//		
		boolean found = false;
	    for(Enumeration e = trainDrawings.elements(); e.hasMoreElements(); ) {
	      TrainDrawing trainDrawing = (TrainDrawing) e.nextElement();
	      if((trainDrawing.train.trainNameDown.equalsIgnoreCase(trainName))
	        ||(trainDrawing.train.trainNameUp.equalsIgnoreCase(trainName))) {
	       moveToTrain(trainDrawing.train);
	       found = true;
	      }
	    }

	    for(Enumeration e = underDrawings.elements(); e.hasMoreElements(); ) {
	      TrainDrawing trainDrawing = (TrainDrawing) e.nextElement();
	      if((trainDrawing.train.trainNameDown.equalsIgnoreCase(trainName))
	        ||(trainDrawing.train.trainNameUp.equalsIgnoreCase(trainName))) {
	       moveToTrain(trainDrawing.train);
	       found = true;
	      }
	    }

	    return found;
	}
	
	/**
	 * moveToTrain
	 *
	 * @param loadingTrain Train
	 */
	public void moveToTrain(Train train) {
//		System.out.println("moveToTrain:" + train.getTrainName());
//		
		if (train == null)
			return;
		
		for (Enumeration e = trainDrawings.elements(); e.hasMoreElements();) {
			TrainDrawing trainDrawing = (TrainDrawing) e.nextElement();
			if (trainDrawing.isSameTrain(train)) {
				panelLines.moveToTrainDrawing(trainDrawing);
			}
		}

		for (Enumeration e = underDrawings.elements(); e.hasMoreElements();) {
			TrainDrawing trainDrawing = (TrainDrawing) e.nextElement();
			if (trainDrawing.isSameTrain(train)) {
				panelLines.moveToTrainDrawing(trainDrawing);
			}
		}
	}

	public void buildTrainDrawings() {
		//清空正常，水印显示列表
		trainDrawings.removeAllElements();
		underDrawings.removeAllElements();

		Chart chart = mainFrame.chart;
		for (int i = 0; i < chart.trainNum; i++) {
			int isDown = chart.trains[i].isDownTrain(chart.circuit);
			TrainDrawing trainDrawing = new TrainDrawing(mainFrame.chart, this, chart.trains[i]);
			switch (showUpDownState) {
			//全不显示
			case MainView.SHOW_NONE:
				underDrawings.add(trainDrawing);
				break;
			//显示下行列车
			case MainView.SHOW_DOWN:
				if (isDown == Train.DOWN_TRAIN)
					trainDrawings.add(trainDrawing);
				else
					underDrawings.add(trainDrawing);
				break;
			//显示上行列车
			case MainView.SHOW_UP:
				if (isDown == Train.UP_TRAIN)
					trainDrawings.add(trainDrawing);
				else
					underDrawings.add(trainDrawing);
				break;
			//缺省为全部显示
			default:
				trainDrawings.add(trainDrawing);
			}
		}
	}

	//水印颜色
	public final static Color DEFAULT_UNDER_COLOR = new Color(220, 220, 220);

	public Color underDrawingColor = DEFAULT_UNDER_COLOR;

	/**
	 * 从相对时间计算X坐标
	 * @param coordinate int
	 * @return int
	 */
	public int getPelsX(int coordinate) {
		return coordinate * mainFrame.chart.minuteScale + leftMargin;
	}

	/**
	 * 从绝对时间计算X坐标
	 * @param clock Date
	 * @return int
	 */
	public int getPelsX(String clock) {
		return getPelsX(getCoordinate(clock));
	}

	/**
	 * 从距离值计算Y坐标
	 * @param dist int
	 * @return int
	 */
	public int getPelsY(int dist) {
		return dist * mainFrame.chart.distScale + topMargin;
	}

	/**
	 * 从站名计算Y坐标
	 * @param stationName String
	 * @return int
	 */
	public int getPelsY(String stationName) {
		return getPelsY(mainFrame.chart.circuit.getStationDist(stationName));
	}

	/**
	 * 从Y坐标计算距离值
	 * @param py int
	 * @return int
	 */
	public int getDist(int py) {
		return (py - topMargin) / mainFrame.chart.distScale;
	}

	public String getStationName(int py) {
		return mainFrame.chart.circuit.getStationName(getDist(py));
	}

	public int getMinute(int px) {
		return (px - leftMargin) / mainFrame.chart.minuteScale;
	}

	/**
	 * 从绝对时间计算相对时间
	 * @param time Date
	 * @return int
	 */
	public int getCoordinate(String time) {
//		SimpleDateFormat dfHour = new SimpleDateFormat("H");
//		SimpleDateFormat dfMinute = new SimpleDateFormat("m");
		
//		int h = time.getHours() - mainFrame.chart.startHour;
		int h = Integer.parseInt(time.split(":")[0]) 
				- mainFrame.chart.startHour;
		if (h < 0)
			h += 24;

//		int m = time.getMinutes();
		int m = Integer.parseInt(time.split(":")[1]);

		return h * 60 + m;
	}

	public static final int SHOW_NONE = 0; //0000

	public static final int SHOW_DOWN = 1; //0001

	public static final int SHOW_UP = 2; //0010

	public static final int SHOW_ALL = 3; //0011 = SHOW_UP | SHOW_DOWN

	public int showUpDownState = SHOW_NONE;

	private ImageIcon imAll = new ImageIcon(MainView.class
			.getResource("/pic/all.png"));

	private ImageIcon imDown = new ImageIcon(MainView.class
			.getResource("/pic/down.png"));

	private ImageIcon imUp = new ImageIcon(MainView.class
			.getResource("/pic/up.png"));

	private ImageIcon imNone = new ImageIcon(MainView.class
			.getResource("/pic/none.png"));

	public void changeShowUpDownState() {
		showUpDownState = showUpDownState >= 3 ? 0 : showUpDownState + 1;
		updateUpDownDisplay();
	}

	public void changeShowDown() {
		showUpDownState ^= SHOW_DOWN;
		updateUpDownDisplay();
	}

	public void changShownUp() {
		showUpDownState ^= SHOW_UP;
		updateUpDownDisplay();
	}

	private void updateUpDownDisplay() {
		switch (showUpDownState) {
		case SHOW_ALL:
			cornerUpDown.setIcon(imAll);
			mainFrame.jtButtonDown.setSelected(true);
			mainFrame.jtButtonUp.setSelected(true);
			mainFrame.prop.setProperty(MainFrame.Prop_Show_Down, "Y");
			mainFrame.prop.setProperty(MainFrame.Prop_Show_UP, "Y");
			break;
		case SHOW_DOWN:
			cornerUpDown.setIcon(imDown);
			mainFrame.jtButtonDown.setSelected(true);
			mainFrame.jtButtonUp.setSelected(false);
			mainFrame.prop.setProperty(MainFrame.Prop_Show_Down, "Y");
			mainFrame.prop.setProperty(MainFrame.Prop_Show_UP, "N");
			break;
		case SHOW_UP:
			cornerUpDown.setIcon(imUp);
			mainFrame.jtButtonDown.setSelected(false);
			mainFrame.jtButtonUp.setSelected(true);
			mainFrame.prop.setProperty(MainFrame.Prop_Show_Down, "N");
			mainFrame.prop.setProperty(MainFrame.Prop_Show_UP, "Y");
			break;
		case SHOW_NONE:
			cornerUpDown.setIcon(imNone);
			mainFrame.jtButtonDown.setSelected(false);
			mainFrame.jtButtonUp.setSelected(false);
			mainFrame.prop.setProperty(MainFrame.Prop_Show_Down, "N");
			mainFrame.prop.setProperty(MainFrame.Prop_Show_UP, "N");
			break;
		}
		panelLines.repaint();
	}

	public int distUpDownState = SHOW_DOWN;

	public Color downDistColor = Color.blue;

	public Color upDistColor = Color.red;

	public void changeDistUpDownState() {
		distUpDownState = distUpDownState == SHOW_DOWN ? SHOW_UP : SHOW_DOWN;
		if (distUpDownState == SHOW_DOWN)
			cornerCoordinate.setForeground(downDistColor);
		else
			cornerCoordinate.setForeground(upDistColor);
		panelCircuit.repaint();
	}

	public void setCoordinateCorner(Point p) {
		int dist = (p.y - topMargin) / mainFrame.chart.distScale;
		int minutes = (p.x - leftMargin) / mainFrame.chart.minuteScale;
		cornerCoordinate.setText("(" + getClockString(minutes) + ","
				+ getDistString(dist) + ")");
		mainFrame.statusBarMain.setText(mainFrame.chart.circuit.getStationName(dist, true));
	}

	private String getDistString(int dist) {
		if (distUpDownState == SHOW_DOWN)
			return "" + dist;
		else
			return "" + (mainFrame.chart.circuit.length - dist);
	}

	private String getClockString(int minutes) {
		int hours = minutes < 0 ? -1 : minutes / 60;

		int clockMinute = minutes - hours * 60;
		if (clockMinute < 0)
			clockMinute += 60;

		String strMinute = clockMinute < 10 ? "0" + clockMinute : ""
				+ clockMinute;

		int clockHour = mainFrame.chart.startHour + hours;
		if (clockHour < 0)
			clockHour += 24;
		if (clockHour >= 24)
			clockHour -= 24;

		String strHour = clockHour < 10 ? "0" + clockHour : ""
			+ clockHour;

		return strHour + ":" + strMinute;
	}

	public String getTime(int px) {
		return getClockString(getMinute(px));
	}

	private JLabel cornerCoordinate = new JLabel("(0,0:00)");

	private JLabel cornerUpDown = new JLabel();

	private JLabel cornerTrainNum = new JLabel("D:0,U:0");

	void jbInit() throws Exception {
		setLayout(new BorderLayout());
		add(spLines, BorderLayout.CENTER);

		spLines.getViewport().add(panelLines);
		spLines.setRowHeaderView(panelCircuit);
		spLines.setColumnHeaderView(panelClock);

		//在左上角显示坐标
		spLines.setCorner(JScrollPane.UPPER_LEFT_CORNER, cornerCoordinate);
		cornerCoordinate.setHorizontalAlignment(SwingConstants.CENTER);
		cornerCoordinate.setVerticalAlignment(SwingConstants.CENTER);
		cornerCoordinate.setForeground(Color.blue);
		cornerCoordinate.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		cornerCoordinate.setFont(new Font("Dialog", Font.BOLD, 12));
		//处理坐标角鼠标点击事件
		cornerCoordinate.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				changeDistUpDownState();
			}
		});

		//在右下角显示“上下行”显示状态
		spLines.setCorner(JScrollPane.LOWER_RIGHT_CORNER, cornerUpDown);
		cornerUpDown.setIcon(imAll);
		cornerUpDown.setHorizontalAlignment(SwingConstants.CENTER);
		cornerUpDown.setVerticalAlignment(SwingConstants.CENTER);
		//处理上下行状态角鼠标点击事件
		cornerUpDown.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				changeShowUpDownState();
			}
		});

		//在左下角显示上行车次数和下行车次数
		spLines.setCorner(JScrollPane.LOWER_LEFT_CORNER, cornerTrainNum);
		cornerTrainNum.setHorizontalAlignment(SwingConstants.CENTER);
		cornerTrainNum.setVerticalAlignment(SwingConstants.CENTER);
		cornerTrainNum.setBorder(BorderFactory
				.createLineBorder(Color.lightGray));

		//永远显示横竖滚动条，以便右下角的“上下行”状态显示能出现
		spLines.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		spLines.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		spLines.getVerticalScrollBar().setUnitIncrement(ScrollUnitIncrement);
		spLines.getHorizontalScrollBar().setUnitIncrement(ScrollUnitIncrement);
		setTrainNum();
		
		addKeyListener(this);
	}

	public void setTrainNum() {
		cornerTrainNum.setText("(D:" + mainFrame.chart.dNum + ",U:" + mainFrame.chart.uNum + ")");
	}


	public Train getActiveTrain() {
		return (activeTrainDrawing == null) ? null : activeTrainDrawing.train;
	}

	/**
	 * 取在图chart上画的停站
	 * @param chart ChartPanel
	 * @return Stop[]
	 */
	public Stop[] getDrawStops(Train train) {
		Circuit circuit = mainFrame.chart.circuit;
		Stop[] drawStops = new Stop[circuit.stationNum];

		int iDraw = 0;
		for (int i = 0; i < train.stopNum; i++) {
			if (circuit.getStationDist(train.stops[i].stationName) >= 0) {
				drawStops[iDraw] = train.stops[i];
				iDraw++;
			}
		}

		Stop[] rtStops = new Stop[iDraw];
		for (int j = 0; j < iDraw; j++) {
			rtStops[j] = drawStops[j];
		}

		return rtStops;
	}

	public int getDrawStopIndex(Train train, String stationName) {
		Stop[] drawStops = getDrawStops(train);

		for (int i = 0; i < drawStops.length; i++) {
			if (drawStops[i].stationName.equalsIgnoreCase(stationName))
				return i;
		}

		return -1;
	}

	/**
	 * keyPressed
	 *
	 * @param e KeyEvent
	 */
	public void keyPressed(KeyEvent e) {
		int state = panelLines.getState();
		switch (state) {
		case LinesPanel.STATE_ADD_STOP:
			keyPressedAddStop(e);
			break;
		case LinesPanel.STATE_CHANGE_ARRIVE:
			keyPressedChangeArrive(e);
			break;
		case LinesPanel.STATE_CHANGE_LEAVE:
			keyPressedChangeLeave(e);
			break;
		default:
			keyPressedNormal(e);
		}
	}

	private void keyPressedNormal(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_D:
			changeShowDown();
			break;
		case KeyEvent.VK_U:
			changShownUp();
			break;
		case KeyEvent.VK_UP:
			int oldV = spLines.getVerticalScrollBar().getValue();
			spLines.getVerticalScrollBar().setValue(oldV - ScrollUnitIncrement);
			break;
		case KeyEvent.VK_DOWN:
			oldV = spLines.getVerticalScrollBar().getValue();
			spLines.getVerticalScrollBar().setValue(oldV + ScrollUnitIncrement);
			break;
		case KeyEvent.VK_LEFT:
			int oldH = spLines.getHorizontalScrollBar().getValue();
			spLines.getHorizontalScrollBar().setValue(
					oldH - ScrollUnitIncrement);
			break;
		case KeyEvent.VK_RIGHT:
			oldH = spLines.getHorizontalScrollBar().getValue();
			spLines.getHorizontalScrollBar().setValue(
					oldH + ScrollUnitIncrement);
			break;
		}
	}

	private void keyPressedAddStop(KeyEvent e) {
		if (activeTrainDrawing == null)
			return;
		if (activeTrainDrawing.movingPoint == null)
			return;

		Circuit circuit = mainFrame.chart.circuit;
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			//上行向上移动，取NextStation
			if (activeTrainDrawing.train.isDownTrain(circuit) == Train.UP_TRAIN) {
				String oldStation = activeTrainDrawing.movingPoint.getStationName();
				int oldMovingIndex = circuit.getStationIndex(oldStation);
				int newMovingIndex = circuit.getNextStationIndex(
						activeTrainDrawing.train, oldMovingIndex);
				int nextStopIndex = circuit.getStationIndex(activeTrainDrawing.train
						.getNextStopName(activeTrainDrawing.movingPoint
								.getStationName()));
				if (newMovingIndex <= nextStopIndex)
					return;
				String newStation = circuit.stations[newMovingIndex].name;
				Train train = activeTrainDrawing.train;
				train.replaceStop(oldStation, newStation);
				activeTrainDrawing = new TrainDrawing(mainFrame.chart, this, train);

				activeTrainDrawing.setMovingPoint(newStation,
						TrainDrawing.ChartPoint.STOP_ARRIVE);
				repaint();
			}
			//上行向下移动，取PrevStation
			else {
				String oldStation = activeTrainDrawing.movingPoint.getStationName();
				int oldMovingIndex = circuit.getStationIndex(oldStation);
				int newMovingIndex = circuit.getPrevStationIndex(
						activeTrainDrawing.train, oldMovingIndex);
				int prevStopIndex = circuit.getStationIndex(activeTrainDrawing.train
						.getPrevStopName(activeTrainDrawing.movingPoint
								.getStationName()));
				if (newMovingIndex <= prevStopIndex)
					return;
				String newStation = circuit.stations[newMovingIndex].name;
				Train train = activeTrainDrawing.train;
				train.replaceStop(oldStation, newStation);
				activeTrainDrawing = new TrainDrawing(mainFrame.chart, this, train);

				activeTrainDrawing.setMovingPoint(newStation,
						TrainDrawing.ChartPoint.STOP_ARRIVE);
				repaint();
			}
			break;
		case KeyEvent.VK_DOWN:
			//下行向下移动，取NextStation
			if (activeTrainDrawing.train.isDownTrain(circuit) == Train.DOWN_TRAIN) {
				String oldStation = activeTrainDrawing.movingPoint.getStationName();
				int oldMovingIndex = circuit.getStationIndex(oldStation);
				int newMovingIndex = circuit.getNextStationIndex(
						activeTrainDrawing.train, oldMovingIndex);
				int nextStopIndex = circuit.getStationIndex(activeTrainDrawing.train
						.getNextStopName(activeTrainDrawing.movingPoint
								.getStationName()));
				if (newMovingIndex >= nextStopIndex)
					return;
				String newStation = circuit.stations[newMovingIndex].name;
				Train train = activeTrainDrawing.train;
				train.replaceStop(oldStation, newStation);
				activeTrainDrawing = new TrainDrawing(mainFrame.chart, this, train);

				activeTrainDrawing.setMovingPoint(newStation,
						TrainDrawing.ChartPoint.STOP_ARRIVE);
				repaint();
			}
			//上行向上移动，取PrevStation
			else {
				String oldStation = activeTrainDrawing.movingPoint.getStationName();
				int oldMovingIndex = circuit.getStationIndex(oldStation);
				int nextMovingIndex = circuit.getPrevStationIndex(
						activeTrainDrawing.train, oldMovingIndex);
				int prevStopIndex = circuit.getStationIndex(activeTrainDrawing.train
						.getPrevStopName(activeTrainDrawing.movingPoint
								.getStationName()));
				if (nextMovingIndex >= prevStopIndex)
					return;
				String newStation = circuit.stations[nextMovingIndex].name;
				Train train = activeTrainDrawing.train;
				train.replaceStop(oldStation, newStation);
				activeTrainDrawing = new TrainDrawing(mainFrame.chart, this, train);

				activeTrainDrawing.setMovingPoint(newStation,
						TrainDrawing.ChartPoint.STOP_ARRIVE);
				repaint();
			}
			break;
		case KeyEvent.VK_ENTER:
			panelLines.setState(LinesPanel.STATE_CHANGE_ARRIVE);
		}
	}

	private void keyPressedChangeArrive(KeyEvent e) {
		if (activeTrainDrawing == null)
			return;
		if (activeTrainDrawing.movingPoint == null)
			return;

		String movingStation = activeTrainDrawing.movingPoint.getStationName();
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_RIGHT:
			int newx = 0;

			if (e.getKeyCode() == KeyEvent.VK_LEFT)
				newx = activeTrainDrawing.movingPoint.x - mainFrame.chart.minuteScale;
			else
				newx = activeTrainDrawing.movingPoint.x + mainFrame.chart.minuteScale;

			int y = activeTrainDrawing.movingPoint.y;
			String newTime = getTime(newx);
//			SimpleDateFormat df = new SimpleDateFormat("HH:mm");
//			try {
//				Date newArrive = df.parse(newTime);
//				String newArrive = newTime;
				Train train = activeTrainDrawing.train;
				train.setArrive(movingStation, newTime);

				//TrainDrawing.ChartPoint moving = activeTrain.movingPoint;
				activeTrainDrawing = new TrainDrawing(mainFrame.chart, this, train);
				activeTrainDrawing.setMovingPoint(newx, y,
						TrainDrawing.ChartPoint.STOP_ARRIVE);
				repaint();
//			} catch (ParseException ex) {
//				ex.printStackTrace();
//			}
			break;
		case KeyEvent.VK_ENTER:
			panelLines.setState(LinesPanel.STATE_CHANGE_LEAVE);
			activeTrainDrawing.setMovingPoint(movingStation,
					TrainDrawing.ChartPoint.STOP_LEAVE);
			repaint();
		}
	}

	private void keyPressedChangeLeave(KeyEvent e) {
		if (activeTrainDrawing == null)
			return;
		if (activeTrainDrawing.movingPoint == null)
			return;

		String movingStation = activeTrainDrawing.movingPoint.getStationName();
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_RIGHT:
			int newx = 0;

			if (e.getKeyCode() == KeyEvent.VK_LEFT)
				newx = activeTrainDrawing.movingPoint.x - mainFrame.chart.minuteScale;
			else
				newx = activeTrainDrawing.movingPoint.x + mainFrame.chart.minuteScale;

			int y = activeTrainDrawing.movingPoint.y;

			String newTime = getTime(newx);
//			SimpleDateFormat df = new SimpleDateFormat("HH:mm");
//			try {
//				Date newLeave = df.parse(newTime);
				Train train = activeTrainDrawing.train;
				train.setLeave(movingStation, newTime);

				//TrainDrawing.ChartPoint moving = activeTrain.movingPoint;
				activeTrainDrawing = new TrainDrawing(mainFrame.chart, this, train);
				activeTrainDrawing.setMovingPoint(newx, y,
						TrainDrawing.ChartPoint.STOP_LEAVE);
				repaint();
//			} catch (ParseException ex) {
//				ex.printStackTrace();
//			}
			break;
		case KeyEvent.VK_ENTER:
			panelLines.setState(LinesPanel.STATE_NORMAL);
			activeTrainDrawing.movingPoint = null;
			repaint();
		}
	}

	/**
	 * keyReleased
	 *
	 * @param e KeyEvent
	 */
	public void keyReleased(KeyEvent e) {
	}

	/**
	 * keyTyped
	 *
	 * @param e KeyEvent
	 */
	public void keyTyped(KeyEvent e) {
	}
}
