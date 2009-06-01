package org.paradise.etrc.dview;

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
public class DynamicView extends JPanel implements KeyListener, Runnable {
	private static final long serialVersionUID = -2507031211251679563L;

	public static final int DEFAULT_LEFT_MARGIN = 45;
	public static final int DEFAULT_RIGHT_MARGIN = 45;
	public static final int DEFAULT_TOP_MARGIN = 100;
	public static final int DEFAULT_BOTTOM_MARGIN = 100;

	public int leftMargin = DEFAULT_LEFT_MARGIN;
	public int rightMargin = DEFAULT_RIGHT_MARGIN;
	public int topMargin = DEFAULT_TOP_MARGIN;
	public int bottomMargin = DEFAULT_BOTTOM_MARGIN;

	public int ditancePanelHeight = 16;
	public int runningPanelHeight = topMargin + bottomMargin + 28;
	public int directPanelWeight = 50;

	public static final int ScrollUnitIncrement = 16;

	public Vector trainDrawings = new Vector();
	public Vector underDrawings = new Vector();

	private DistancePanel panelDistance;
	private DirectPanel panelDirect;
	private RunningPanel panelRunning;
	private InfoPanelRight panelInfoRight;
	private InfoPanelLeft panelInfoLeft;

	private JScrollPane spRunning = new JScrollPane();

//	public Color gridColor = Color.darkGray;
	public Color gridColor = Color.GRAY;

	public MainFrame mainFrame;
	public int scale = DEFAULT_SCALE;
	public static final int DEFAULT_SCALE = 5;

	public DynamicView(MainFrame _mainFrame) {
		mainFrame = _mainFrame;
		
		panelDistance = new DistancePanel(mainFrame.chart, this);
		panelDirect = new DirectPanel(mainFrame.chart, this);
		panelRunning = new RunningPanel(mainFrame.chart, this);
		panelInfoRight = new InfoPanelRight();
		panelInfoLeft = new InfoPanelLeft();
		
//		scale = mainFrame.chart.distScale * 2;
//		
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		Thread thr = new Thread(this);
		thr.start();
	}

	/**
	 * 从距离值计算X坐标
	 * @param dist int
	 * @return int
	 */
	public int getPelsX(int dist) {
		return dist * scale + leftMargin;
	}

	/**
	 * 从站名计算X坐标
	 * @param stationName String
	 * @return int
	 */
	public int getPelsX(String stationName) {
		return getPelsX(mainFrame.chart.circuit.getStationDist(stationName));
	}

	/**
	 * 从X坐标计算距离值
	 * @param py int
	 * @return int
	 */
	public int getDist(int px) {
		return (px - leftMargin) / scale;
	}

	public String getStationName(int px) {
		return mainFrame.chart.circuit.getStationName(getDist(px));
	}

//	public static final int SHOW_NONE = 0; //0000

	public static final int SHOW_DOWN = 1; //0001

	public static final int SHOW_UP = 2; //0010

//	public static final int SHOW_ALL = 3; //0011 = SHOW_UP | SHOW_DOWN

//	public int showUpDownState = SHOW_NONE;

//	private ImageIcon imAll = new ImageIcon(this.getClass().getResource("/pic/all.png"));
//	private ImageIcon imDown = new ImageIcon(this.getClass().getResource("/pic/down.png"));
//	private ImageIcon imUp = new ImageIcon(this.getClass().getResource("/pic/up.png"));
//	private ImageIcon imNone = new ImageIcon(this.getClass().getResource("/pic/none.png"));
	private ImageIcon imRun = new ImageIcon(this.getClass().getResource("/pic/run.png"));
	private ImageIcon imStop = new ImageIcon(this.getClass().getResource("/pic/stop.png"));
	
	private boolean isRunning = true;
	
	public void setRunState(boolean state) {
		isRunning = state;
		
		updateRunState();
	}
	
	public void changeRunState() {
		this.isRunning = this.isRunning ? false : true;
		
		updateRunState();
	}
	
	private void updateRunState() {
		if(isRunning)
			cornerRunState.setIcon(imRun);
		else
			cornerRunState.setIcon(imStop);
	}

//	public void changeShowUpDownState() {
//		showUpDownState = showUpDownState >= 3 ? 0 : showUpDownState + 1;
//		updateUpDownDisplay();
//	}
//
//	public void changeShowDown() {
//		showUpDownState ^= SHOW_DOWN;
//		updateUpDownDisplay();
//	}
//
//	public void changShownUp() {
//		showUpDownState ^= SHOW_UP;
//		updateUpDownDisplay();
//	}
//
//	private void updateUpDownDisplay() {
//		switch (showUpDownState) {
//		case SHOW_ALL:
//			cornerUpDown.setIcon(imAll);
//			mainFrame.jtButtonDown.setSelected(true);
//			mainFrame.jtButtonUp.setSelected(true);
//			mainFrame.prop.setProperty(MainFrame.Prop_Show_Down, "Y");
//			mainFrame.prop.setProperty(MainFrame.Prop_Show_UP, "Y");
//			break;
//		case SHOW_DOWN:
//			cornerUpDown.setIcon(imDown);
//			mainFrame.jtButtonDown.setSelected(true);
//			mainFrame.jtButtonUp.setSelected(false);
//			mainFrame.prop.setProperty(MainFrame.Prop_Show_Down, "Y");
//			mainFrame.prop.setProperty(MainFrame.Prop_Show_UP, "N");
//			break;
//		case SHOW_UP:
//			cornerUpDown.setIcon(imUp);
//			mainFrame.jtButtonDown.setSelected(false);
//			mainFrame.jtButtonUp.setSelected(true);
//			mainFrame.prop.setProperty(MainFrame.Prop_Show_Down, "N");
//			mainFrame.prop.setProperty(MainFrame.Prop_Show_UP, "Y");
//			break;
//		case SHOW_NONE:
//			cornerUpDown.setIcon(imNone);
//			mainFrame.jtButtonDown.setSelected(false);
//			mainFrame.jtButtonUp.setSelected(false);
//			mainFrame.prop.setProperty(MainFrame.Prop_Show_Down, "N");
//			mainFrame.prop.setProperty(MainFrame.Prop_Show_UP, "N");
//			break;
//		}
//		
//		panelRunning.repaint();
//	}

	public int distUpDownState = SHOW_DOWN;

	public Color downDistColor = Color.blue;
	public Color upDistColor = Color.red;

	public void changeDistUpDownState() {
		distUpDownState = distUpDownState == SHOW_DOWN ? SHOW_UP : SHOW_DOWN;
		if (distUpDownState == SHOW_DOWN)
			cornerClock.setForeground(downDistColor);
		else
			cornerClock.setForeground(upDistColor);
		
		panelRunning.repaint();
		panelDistance.repaint();
	}

//	private String getDistString(int dist) {
//		if (distUpDownState == SHOW_DOWN)
//			return "" + dist;
//		else
//			return "" + (mainFrame.chart.circuit.length - dist);
//	}

	private JLabel cornerClock = new JLabel(toTrainFormat(DEFAULT_START_TIME));
	private JLabel cornerRunState = new JLabel();
	private JLabel cornerSpeed = new JLabel(DEFAULT_GAP + "ms");

	void jbInit() throws Exception {
		JPanel panelInfo = new JPanel();
		panelInfo.setLayout(new GridLayout());
		panelInfo.add(panelInfoLeft);
		panelInfo.add(panelInfoRight);
		
		this.setLayout(new BorderLayout());		
//		this.add(panelInfo, BorderLayout.CENTER);
		this.add(spRunning, BorderLayout.NORTH);

		spRunning.getViewport().add(panelRunning);
		spRunning.setRowHeaderView(panelDirect);
		spRunning.setColumnHeaderView(panelDistance);

		//在左上角显示当前时间
		spRunning.setCorner(JScrollPane.UPPER_LEFT_CORNER, cornerClock);
		cornerClock.setHorizontalAlignment(SwingConstants.CENTER);
		cornerClock.setVerticalAlignment(SwingConstants.CENTER);
		cornerClock.setForeground(downDistColor);
		cornerClock.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		cornerClock.setFont(new Font("Dialog", Font.BOLD, 12));
		//处理坐标角鼠标点击事件 左键前进1小时，右键后退1小时
		cornerClock.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1)
					DynamicView.this.timeAdd(60);
				else
					DynamicView.this.timeAdd(-60);
			}
		});

		//在右下角显示是否在运行状态
		spRunning.setCorner(JScrollPane.LOWER_RIGHT_CORNER, cornerRunState);
		cornerRunState.setIcon(imRun);
		cornerRunState.setHorizontalAlignment(SwingConstants.CENTER);
		cornerRunState.setVerticalAlignment(SwingConstants.CENTER);
		//处理上下行状态角鼠标点击事件 点击一下切换运行状态
		cornerRunState.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				changeRunState();
			}
		});

		//在左下角显示当前速度
		spRunning.setCorner(JScrollPane.LOWER_LEFT_CORNER, cornerSpeed);
		cornerSpeed.setHorizontalAlignment(SwingConstants.CENTER);
		cornerSpeed.setVerticalAlignment(SwingConstants.CENTER);
		cornerSpeed.setBorder(BorderFactory
				.createLineBorder(Color.lightGray));
		//处理速度显示角鼠标点击事件 左键加速，右键减速
		cornerSpeed.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1)
					DynamicView.this.gapAdd(-GAP_STEP);
				else
					DynamicView.this.gapAdd(GAP_STEP);
			}
		});

		//永远显示横竖滚动条，以便右下角的运行状态角能出现
		spRunning.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		spRunning.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		spRunning.getVerticalScrollBar().setUnitIncrement(ScrollUnitIncrement);
		spRunning.getHorizontalScrollBar().setUnitIncrement(ScrollUnitIncrement);
//		setTrainNum();
		
		addKeyListener(this);
	}

	public void setTrainNum() {
//		cornerSpeed.setText(mainFrame.chart.dNum + " / " + mainFrame.chart.uNum);
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
//	public void keyPressed(KeyEvent e) {
//		int state = panelLines.getState();
//		switch (state) {
//		case LinesPanel.STATE_ADD_STOP:
//			keyPressedAddStop(e);
//			break;
//		case LinesPanel.STATE_CHANGE_ARRIVE:
//			keyPressedChangeArrive(e);
//			break;
//		case LinesPanel.STATE_CHANGE_LEAVE:
//			keyPressedChangeLeave(e);
//			break;
//		default:
//			keyPressedNormal(e);
//		}
//	}

	public void keyPressed(KeyEvent e) {
	}

	/**
	 * keyReleased
	 *
	 * @param e KeyEvent
	 */
	public void keyReleased(KeyEvent e) {
//		System.out.println(e.getKeyCode());
		switch (e.getKeyCode()) {
//		case KeyEvent.VK_D:
//			changeShowDown();
//			break;
//			
//		case KeyEvent.VK_U:
//			changShownUp();
//			break;
			
		case KeyEvent.VK_UP:
			this.gapAdd(-GAP_STEP);
			break;
			
		case KeyEvent.VK_DOWN:
			this.gapAdd(GAP_STEP);
			break;
			
		case KeyEvent.VK_LEFT:
			int oldH = spRunning.getHorizontalScrollBar().getValue();
			spRunning.getHorizontalScrollBar().setValue(
					oldH - ScrollUnitIncrement);
			break;
			
		case KeyEvent.VK_RIGHT:
			oldH = spRunning.getHorizontalScrollBar().getValue();
			spRunning.getHorizontalScrollBar().setValue(
					oldH + ScrollUnitIncrement);
			break;
			
		case KeyEvent.VK_ENTER:
			this.timeAdd(1);
			break;
			
		case KeyEvent.VK_SPACE:
			this.changeRunState();
			break;
			
		case KeyEvent.VK_PAGE_UP:
			this.timeAdd(60);
			break;
			
		case KeyEvent.VK_PAGE_DOWN:
			this.timeAdd(-60);
			break;
		}
	}

	/**
	 * keyTyped
	 *
	 * @param e KeyEvent
	 */
	public void keyTyped(KeyEvent e) {
	}

	public boolean findAndMoveToTrain(String trainToFind) {
		// TODO Auto-generated method stub
		return false;
	}

	private String toTrainFormat(int time) {
		if(time > 24*60)
			return "hh:mm";
		
		int h = time / 60;
		int m = time % 60;
		
		String strH = h<10 ? "0"+h : ""+h;
		String strM = m<10 ? "0"+m : ""+m;
		
		return strH + ":" + strM;
	}
	
	public int currentTime = DEFAULT_START_TIME;
	public static final int DEFAULT_START_TIME = 0;
	public void timeAdd(int i) {
		currentTime += i;
		if(currentTime > 24*60)
			currentTime -= 24*60;
		
		if(currentTime < 0)
			currentTime += 24*60;
		
		panelRunning.repaint();
		
		String strTime = toTrainFormat(currentTime);
		cornerClock.setText(strTime);
	}
	
	public void gapAdd(long l) {
		timeGap += l;
		
		if(timeGap > MAX_GAP)
			timeGap = MAX_GAP;
		
		if(timeGap < MIN_GAP)
			timeGap = MIN_GAP;
		
		cornerSpeed.setText(timeGap + "ms");
	}

	public long timeGap = DEFAULT_GAP;
	public static final long DEFAULT_GAP = 500L;
	public static final long MAX_GAP = 2000L;
	public static final long MIN_GAP = 50L;
	public static final long GAP_STEP = 50L;
	public void run() {
		while(true) {
			if(isRunning) {
				this.timeAdd(1);
				try {
					Thread.sleep(timeGap);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void refresh() {
		panelRunning.setSize(panelRunning.getPreferredSize());
		panelDistance.setSize(panelDistance.getPreferredSize());
	}
}
