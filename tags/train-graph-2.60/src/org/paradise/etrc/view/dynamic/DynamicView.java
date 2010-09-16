package org.paradise.etrc.view.dynamic;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.paradise.etrc.*;

/**
 * @author lguo@sina.com
 * @version 1.0
 */
public class DynamicView extends JPanel implements KeyListener, Runnable {
	private static final long serialVersionUID = -2507031211251679563L;

	private static final int DEFAULT_LEFT_MARGIN = 45;
	private static final int DEFAULT_RIGHT_MARGIN = 45;
	private static final int DEFAULT_TOP_MARGIN = 48;
	private static final int DEFAULT_BOTTOM_MARGIN = 48;

	public int leftMargin = DEFAULT_LEFT_MARGIN;
	public int rightMargin = DEFAULT_RIGHT_MARGIN;
	public int topMargin = DEFAULT_TOP_MARGIN;
	public int bottomMargin = DEFAULT_BOTTOM_MARGIN;

	public int ditancePanelHeight = 16;
	public int runningPanelHeight = topMargin + bottomMargin + 28;
	public int clockPanelWidth = 80;

	private DistancePanel panelDistance;
	private ClockPanel panelClock;
	private RunningPanel panelRunning;

	private JScrollPane spRunning = new JScrollPane();
	private static final int ScrollUnitIncrement = 16;

	public Color gridColor = Color.GRAY;

	public MainFrame mainFrame;
	public int scale = DEFAULT_SCALE;
	private static final int DEFAULT_SCALE = 5;
	private static final int MIN_SCALE = 3;
	private static final int MAX_SCALE = 10;
	public void increaseScale(int i) {
		scale += i;
		
		if(scale < MIN_SCALE) {
			scale = MIN_SCALE;
			return;
		}
		
		if(scale > MAX_SCALE) {
			scale = MAX_SCALE;
			return;
		}
		
		refresh();
	}

	public DynamicView(MainFrame _mainFrame) {
		mainFrame = _mainFrame;
		
		panelDistance = new DistancePanel(this);
		panelClock = new ClockPanel(this);
		panelRunning = new RunningPanel(this);

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

	/**
	 * 从X坐标计算距离值并查找附近车站
	 * @param py int
	 * @return int
	 */
	public String getStationName(int px) {
		return mainFrame.chart.circuit.getStationName(getDist(px));
	}

	/*
	 * 控件初始化
	 */
//	private JLabel cornerControl = new JLabel(toTrainFormat(DEFAULT_START_TIME));
	private ControlPanel controlPanel;
	private SpeedPanel speedPanel;
	private JLabel cornerRunState = new JLabel();
	private JLabel cornerInfo = new JLabel();
//	private JLabel cornerSpeed = new JLabel(DEFAULT_GAP + "ms");

	void jbInit() throws Exception {
		this.setLayout(new BorderLayout());		
		this.add(spRunning, BorderLayout.NORTH);

		spRunning.getViewport().add(panelRunning);
		spRunning.setRowHeaderView(panelClock);
		spRunning.setColumnHeaderView(panelDistance);
		
		controlPanel = new ControlPanel(this);
		spRunning.setCorner(JScrollPane.LOWER_LEFT_CORNER, controlPanel);
		
		speedPanel = new SpeedPanel(this);
		spRunning.setCorner(JScrollPane.UPPER_LEFT_CORNER, speedPanel);

		//在右上角调出信息窗口的按钮
		ImageIcon imInfo = new ImageIcon(this.getClass().getResource("/pic/info.gif"));
//		spRunning.setCorner(JScrollPane.UPPER_RIGHT_CORNER, cornerInfo);
		cornerInfo.setIcon(imInfo);
		cornerInfo.setHorizontalAlignment(SwingConstants.CENTER);
		cornerInfo.setVerticalAlignment(SwingConstants.CENTER);
		//处理上下行状态角鼠标点击事件 点击一下切换运行状态
		cornerInfo.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
//				changeRunState();
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

		//永远显示横竖滚动条，以便右下角的运行状态角能出现
		spRunning.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		spRunning.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		spRunning.getVerticalScrollBar().setUnitIncrement(ScrollUnitIncrement);
		spRunning.getHorizontalScrollBar().setUnitIncrement(ScrollUnitIncrement);
		
		addKeyListener(this);
	}

	public void refresh() {
		panelRunning.setSize(panelRunning.getPreferredSize());
		panelDistance.setSize(panelDistance.getPreferredSize());
		panelRunning.repaint();
	}
	
	/**
	 * keyPressed
	 *
	 * @param e KeyEvent
	 */
	public void keyPressed(KeyEvent e) {
	}

	/**
	 * keyReleased
	 *
	 * @param e KeyEvent
	 */
	public void keyReleased(KeyEvent e) {
		//TODO: 移到MainFrame？或者让MainFrame把焦点传过来
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			this.increaseGap(-GAP_STEP);
			break;
			
		case KeyEvent.VK_DOWN:
			this.increaseGap(GAP_STEP);
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
			this.increaseTime(1);
			break;
			
		case KeyEvent.VK_SPACE:
			this.changeRunState();
			break;
			
		case KeyEvent.VK_PAGE_UP:
			this.increaseTime(60);
			break;
			
		case KeyEvent.VK_PAGE_DOWN:
			this.increaseTime(-60);
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

	/*
	 * 当前运行时间
	 */
	private int currentTime = DEFAULT_START_TIME;
	private static final int DEFAULT_START_TIME = 18*60;
	
	public int getCurrentTime() {
		return currentTime;
	}
	
	public void increaseTime(int i) {
		currentTime += i;
		updateCurrentTime();
	}
	
	public void setCurrentTime(int i) {
		currentTime = i;
		updateCurrentTime();
	}
	
	private void updateCurrentTime() {
		if(currentTime >= 24*60)
			currentTime -= 24*60;
		
		if(currentTime < 0)
			currentTime += 24*60;
		
		panelRunning.repaint();
		panelClock.repaint();
	}
	
	/*
	 * 运行步长，既速度，实际的每分钟在系统内用多少毫秒来跑
	 */
	public long timeGap = DEFAULT_GAP;
	public static final long DEFAULT_GAP = 500L;
	public static final long MAX_GAP = 2000L;
	public static final long MIN_GAP = 50L;
	public static final long GAP_STEP = 50L;
	
	public void increaseGap(long l) {
		timeGap += l;
		
		if(timeGap > MAX_GAP)
			timeGap = MAX_GAP;
		
		if(timeGap < MIN_GAP)
			timeGap = MIN_GAP;
		
		speedPanel.resetSpeed();
	}

	public void run() {
		while(true) {
			if(isRunning) {
				this.increaseTime(1);
				try {
					Thread.sleep(timeGap);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				try {
					Thread.sleep(MIN_GAP);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 *是否在运行状态 
	 */
	private boolean isRunning = true;
	public boolean isRunning() {
		return isRunning;
	}
	
	private ImageIcon imRun = new ImageIcon(this.getClass().getResource("/pic/state_run.gif"));
	private ImageIcon imStop = new ImageIcon(this.getClass().getResource("/pic/state_stop.gif"));
	
	public void setRunState(boolean state) {
		isRunning = state;
		updateRunState();
	}
	
	public void changeRunState() {
		this.isRunning = this.isRunning ? false : true;
		updateRunState();
	}
	
	private void updateRunState() {
		if(isRunning) {
			cornerRunState.setIcon(imRun);
		}
		else {
			cornerRunState.setIcon(imStop);
		}
		controlPanel.resetButton();
	}
	
	/*
	 * 里程表是显示上行还是下行
	 */
	public int showUpDownDistState = SHOW_DOWN_DIST;

	public static final int SHOW_DOWN_DIST = 1;
	public static final int SHOW_UP_DIST = 2;

	public Color downDistColor = Color.blue;
	public Color upDistColor = Color.red;

	public void changeDistUpDownState() {
		showUpDownDistState = showUpDownDistState == SHOW_DOWN_DIST ? SHOW_UP_DIST : SHOW_DOWN_DIST;
//		if (showUpDownDistState == SHOW_DOWN_DIST)
//			cornerControl.setForeground(downDistColor);
//		else
//			cornerControl.setForeground(upDistColor);
		
//		panelRunning.repaint();
		panelDistance.repaint();
	}
}
