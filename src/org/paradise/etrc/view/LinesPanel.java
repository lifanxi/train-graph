package org.paradise.etrc.view;

import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.*;

import org.paradise.etrc.ETRC;
import org.paradise.etrc.data.*;
import org.paradise.etrc.dialog.*;

/**
 * @author lguo@sina.com
 * @version 1.0
 */


public class LinesPanel extends JPanel implements MouseListener,MouseMotionListener {

/**********************************************************************
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
 **********************************************************************/

	private static final long serialVersionUID = 6196666089237432404L;

	private Chart chart;
	private MainView mainView;

	public static final int STATE_NORMAL = 0;

	public static final int STATE_ADD_STOP = 1;

	public static final int STATE_CHANGE_ARRIVE = 2;

	public static final int STATE_CHANGE_LEAVE = 3;

	private int state = STATE_NORMAL;

	public void setState(int _state) {
		state = _state;
		String state_msg;
		switch (state) {
		case STATE_ADD_STOP:
			new MessageBox(mainView.mainFrame, "用上下键选择新增的停站，回车键确定。")
					.showMessage();
			state_msg = "新增停站";
			break;
		case STATE_CHANGE_ARRIVE:
			new MessageBox(mainView.mainFrame, "用左右键调整到站时间（每次一分钟），回车键确定。")
					.showMessage();
			state_msg = "调整到站时间";
			break;
		case STATE_CHANGE_LEAVE:
			new MessageBox(mainView.mainFrame, "用左右键调整发车时间（每次一分钟），回车键确定。")
					.showMessage();
			state_msg = "调整发车时间";
			break;
		default:
			state_msg = "正常";
		}
		mainView.mainFrame.statusBarRight.setText("状态：" + state_msg + " <lguo@sina.com>");
	}

	public int getState() {
		return state;
	}

	public LinesPanel(Chart _chart, MainView _mainView) {
		chart = _chart;
		mainView = _mainView;
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		this.setBackground(Color.white);
		this.setFont(new java.awt.Font("宋体", 0, 12));
		this.setDebugGraphicsOptions(0);
		this.setLayout(new BorderLayout());
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
	}

	public void paint(Graphics g) {
		super.paint(g);

		for (int i = 0; i < 24; i++) {
			drawClockLine(g, i);
		}

		if (chart.circuit != null) {
			for (int i = 0; i < chart.circuit.stationNum; i++) {
				drawStationLine(g, chart.circuit.stations[i], chart.distScale);
			}
		}

		drawTrains(g);
	}

	/**
	 * DrawTrains
	 * 
	 * @param g
	 *            Graphics
	 */
	private void drawTrains(Graphics g) {
		mainView.buildTrainDrawings();

		// 先画水印部分
		for (Enumeration e = mainView.underDrawings.elements(); e
				.hasMoreElements();) {
			Object obj = e.nextElement();
			if (obj instanceof TrainDrawing) {
				TrainDrawing trainDrawing = ((TrainDrawing) obj);
				// 当前选中的车次放在最后画，确保在最上面
				if (!(trainDrawing.equals(mainView.activeTrainDrawing))) {
					trainDrawing.drawUnder(g);
				}
			}
		}

		// 再画非选中部分
		for (Enumeration e = mainView.trainDrawings.elements(); e
				.hasMoreElements();) {
			Object obj = e.nextElement();
			if (obj instanceof TrainDrawing) {
				TrainDrawing trainDrawing = ((TrainDrawing) obj);
				// 当前选中的车次放在最后画，确保在最上面
				if (!(trainDrawing.equals(mainView.activeTrainDrawing))) {
					trainDrawing.drawInactive(g);
				}
				// 把选中车次删掉再添加，以便在最后比较容易选择TrainLine
				else {
					// trainDrawings.removeElement(trainDrawing);
					// trainDrawings.add(trainDrawing);
				}
			}
		}

		// 最后画当前选中的车次
		if (mainView.activeTrainDrawing != null) {
			// &&(mainFrame.chart.trainDrawings.contains(mainFrame.chart.activeTrain))) {
			// System.out.println("Ac " + activeTrain.getTrainName());
			mainView.activeTrainDrawing.drawActive(g);
		}
	}

	public Dimension getPreferredSize() {
		int w, h;
		w = 60 * 24 * chart.minuteScale + mainView.leftMargin + mainView.rightMargin;
		if (chart.circuit != null)
			h = chart.circuit.length * chart.distScale + mainView.topMargin
					+ mainView.bottomMargin;
		else
			h = 480;
		return new Dimension(w, h);
	}

	/**
	 * DrawHour
	 * 
	 * @param g
	 *            Graphics
	 */
	private void drawClockLine(Graphics g, int clock) {
		// 设置坐标线颜色
		Color oldColor = g.getColor();
		g.setColor(mainView.gridColor);

		int start = clock * 60 * chart.minuteScale + mainView.leftMargin;
		int h = chart.circuit.length * chart.distScale;
		// 0～23点整点竖线
		g.drawLine(start, 0, start, h + mainView.topMargin + mainView.bottomMargin);
		g.drawLine(start + 1, 0, start + 1, h + mainView.topMargin
				+ mainView.bottomMargin);
		// 整点之间的timeInterval分钟间隔竖线
		for (int i = 1; i < 60 / chart.timeInterval; i++) {
			int x = start + chart.timeInterval * chart.minuteScale * i;
			int y1 = mainView.topMargin;
			int y2 = y1 + h;
			g.drawLine(x, y1, x, y2);
			// System.out.print(i+".");
		}
		// 24点整点竖线
		if (clock == 23) {
			int end = 24 * 60 * chart.minuteScale + mainView.leftMargin;
			g.drawLine(end, 0, end, h + mainView.topMargin + mainView.bottomMargin);
			g.drawLine(end + 1, 0, end + 1, h + mainView.topMargin
					+ mainView.bottomMargin);
		}

		// 恢复原色
		g.setColor(oldColor);
	}

	private void drawStationLine(Graphics g, Station st, int scale) {
		if (st.hide)
			return;

		// 设置坐标线颜色
		Color oldColor = g.getColor();
		g.setColor(mainView.gridColor);

		// 画坐标线
		int y = st.dist * scale + mainView.topMargin;
		int w = 60 * 24 * chart.minuteScale + mainView.leftMargin
				+ mainView.rightMargin;

		if (st.level <= chart.displayLevel) {
			g.drawLine(0, y, w, y);
			if (st.level <= chart.boldLevel) {
				g.drawLine(0, y + 1, w, y + 1);
			}
		}

		// 恢复原色
		g.setColor(oldColor);
	}

	/**
	 * mouseDragged
	 * 
	 * @param e
	 *            MouseEvent
	 */
	public void mouseDragged(MouseEvent e) {
	}

	/**
	 * mouseMoved
	 * 
	 * @param e
	 *            MouseEvent
	 */
	public void mouseMoved(MouseEvent e) {
		Point p = e.getPoint();
		mainView.setCoordinateCorner(p);
		Train nearTrain[] = findTrains(p);
		for (int i = 0; i < nearTrain.length; i++)
			mainView.mainFrame.statusBarMain.setText(nearTrain[i]
					.getTrainName(chart.circuit)
					+ "次 " + mainView.mainFrame.statusBarMain.getText());
	}

	/**
	 * findTrain
	 * 
	 * @param p
	 *            Point
	 * @return Train[]
	 */
	private Train[] findTrains(Point p) {
		Vector trainsFound = new Vector();
		// 正常部分
		for (Enumeration e = mainView.trainDrawings.elements(); e
				.hasMoreElements();) {
			TrainDrawing trainDrawing = (TrainDrawing) e.nextElement();
			if (trainDrawing.pointOnMe(p)) {
				trainsFound.add(trainDrawing.train);
			}
		}
		
		// 水印部分
		// 如果“水印显示反向车次”未选中则不给选水印车次
		if(mainView.underDrawingColor != null) {
			for (Enumeration e = mainView.underDrawings.elements(); e
					.hasMoreElements();) {
				TrainDrawing trainDrawing = (TrainDrawing) e.nextElement();
				if (trainDrawing.pointOnMe(p)) {
					trainsFound.add(trainDrawing.train);
				}
			}
		}
		
		Train array[] = new Train[trainsFound.size()];
		int i = 0;
		for (Enumeration e = trainsFound.elements(); e.hasMoreElements();) {
			array[i++] = (Train) e.nextElement();
		}
		return array;
	}

	/**
	 * mouseClicked
	 * 
	 * @param e
	 *            MouseEvent
	 */
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			if(e.getClickCount() == 1)
				selectTrain(e.getPoint());
			else
				modifyTrain(e.getPoint());
		} else if (e.getButton() == MouseEvent.BUTTON2) {
			// System.out.println("Click Button2");
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			// System.out.println("Click Button3");
			popupMenu(e.getPoint());
		}
	}
	
	private void modifyTrain(Point p) {
		//先按照单击选中车次
		selectTrain(p);
		
		//如果没有选中车次则不做啥
		if(mainView.activeTrainDrawing == null)
			return;

		editActiveTrain();
	}

	/**
	 * 选择p点附近的TrainDrawing作为ActiveTrain 若p点不靠近任何一个车次则ActiveTrain设为null
	 * 
	 * @param p
	 *            Point
	 */
	private void selectTrain(Point p) {
		TrainDrawing selectedTrain = null;

		// 先查水印部分的
		// 如果“水印显示反向车次”未选中则不给选水印车次
		if(mainView.underDrawingColor != null) {
			for (Enumeration e = mainView.underDrawings.elements(); e.hasMoreElements();) {
				TrainDrawing trainDrawing = (TrainDrawing) e.nextElement();
				if (trainDrawing.pointOnMyRect(p) || trainDrawing.pointOnMe(p)) {
					// System.out.println(trainDrawing.getTrainName()+ " A");
					selectedTrain = trainDrawing;
				}
			}
		}

		// 后查正常部分的，这样正常部分比较容易选中
		for (Enumeration e = mainView.trainDrawings.elements(); e
				.hasMoreElements();) {
			TrainDrawing trainDrawing = (TrainDrawing) e.nextElement();
			if (trainDrawing.pointOnMyRect(p) || trainDrawing.pointOnMe(p)) {
				// System.out.println(trainDrawing.getTrainName()+ " A");
				selectedTrain = trainDrawing;
			}
		}

		if (selectedTrain != null) {
			TrainDrawing.TrainLine line = selectedTrain.findNearTrainLine(p);
			selectedTrain.selectedTrainLine = line;
		}

		selectTrain(selectedTrain);
	}

	public void selectTrain(TrainDrawing trainDrawing) {
		mainView.activeTrainDrawing = trainDrawing;
		// 设置MainFrame的标题和ToolTip
		if (mainView.activeTrainDrawing != null) {
			mainView.mainFrame
					.setActiceTrainName(mainView.activeTrainDrawing.getTrainName());

			TrainDrawing.TrainLine line = mainView.activeTrainDrawing.selectedTrainLine;
			if (line != null)
				setToolTipText(line.getInfo());
			else
				setToolTipText(trainDrawing.getInfo());
		} else {
			mainView.mainFrame.setActiceTrainName("");
			setToolTipText(null);
		}

		// 重绘
		repaint();
	}

	private void popupMenu(Point p) {
		final Point _p = p;
		
		// 选择ActiveTrain，如果没有选中则返回
		if (mainView.activeTrainDrawing == null)
			return;
		
		// 如果不在ActiveTrain上则返回
		if (!mainView.activeTrainDrawing.pointOnMe(p) &&
			!mainView.activeTrainDrawing.pointOnMyRect(p))
			return;
		
		// 弹出PopupMenu
		PopupMenu pop = new PopupMenu();
		MenuItem miColor = new MenuItem("更改颜色");
		miColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// mainFrame.chart.mainFrame.doColorSet();
				setColor();
			}
		});
		MenuItem miEditTimes = new MenuItem("编辑点单");
		miEditTimes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				editTimes(_p);
				editActiveTrain();
			}
		});
		MenuItem miAddPass = new MenuItem("添加通过");
		miAddPass.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		MenuItem miAddStop = new MenuItem("添加停站");
		miAddStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addStop(_p);
			}
		});
		MenuItem miDelStop = new MenuItem("删除停站");
		miDelStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				delStop(_p);
			}
		});
		MenuItem miChangeTime = new MenuItem("调整到发点");
		miChangeTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeTime();
			}
		});
		pop.add(miColor);
		pop.add(miEditTimes);
		pop.addSeparator();
		// pop.add(miAddPass);
		pop.add(miChangeTime);
		pop.add(miAddStop);
		pop.add(miDelStop);
		this.add(pop);
		pop.show(this, p.x, p.y);
	}

	private void changeTime() {
		if (mainView.activeTrainDrawing == null)
			return;

		TrainDrawing.TrainLine line = mainView.activeTrainDrawing.selectedTrainLine;
		if (line == null)
			return;

		if (line.lineType != TrainDrawing.TrainLine.STOP_LINE) {
			Frame frame = mainView.mainFrame;
			MessageBox dlg = new MessageBox(frame, "行车线不能调整到发时间，请选择停车线！");
			dlg.showMessage();
			return;
		}

		// 跨边界停车线不允许调整
		if ((line.p1.type == TrainDrawing.ChartPoint.EDGE)
				|| (line.p2.type == TrainDrawing.ChartPoint.EDGE)) {
			MessageBox dlg = new MessageBox(mainView.mainFrame,
					"跨界停车线不能调整到发时间，请调整时间轴！");
			dlg.showMessage();
			return;
		}

		// line.p1
		mainView.activeTrainDrawing.setMovingPoint(line.p1.getStationName(),
				TrainDrawing.ChartPoint.STOP_ARRIVE);
		setState(STATE_CHANGE_ARRIVE);
		repaint();
	}

	private void delStop(Point p) {
		if (mainView.activeTrainDrawing == null)
			return;

		TrainDrawing.TrainLine line = mainView.activeTrainDrawing.selectedTrainLine;
		if (line == null)
			return;

		if (line.lineType != TrainDrawing.TrainLine.STOP_LINE) {
			Frame frame = mainView.mainFrame;
			MessageBox dlg = new MessageBox(frame, "行车线不能删除停站，请选择停车线！");
			dlg.showMessage();
			return;
		}

		// 跨边界停车线不允许调整
		if ((line.p1.type == TrainDrawing.ChartPoint.EDGE)
				|| (line.p2.type == TrainDrawing.ChartPoint.EDGE)) {
			MessageBox dlg = new MessageBox(mainView.mainFrame,
					"跨界停车线不能删除，请调整时间轴！");
			dlg.showMessage();
			return;
		}

		String stationName = line.p1.getStationName();
		Train newTrain = mainView.activeTrainDrawing.train.copy();
		newTrain.delStop(stationName);
		chart.updateTrain(newTrain);

		mainView.activeTrainDrawing = new TrainDrawing(chart, mainView, newTrain);
		mainView.repaint();
	}

	private void addStop(Point p) {
		if (mainView.activeTrainDrawing == null)
			return;

		TrainDrawing.TrainLine line = mainView.activeTrainDrawing.selectedTrainLine;
		if (line == null)
			return;

		if (line.lineType == TrainDrawing.TrainLine.STOP_LINE) {
			MessageBox dlg = new MessageBox(mainView.mainFrame,
					"停车线不能添加停站，请选择行车线！");
			dlg.showMessage();
			return;
		}

		// 跨边界行车线不允许添加
		if ((line.p1.type == TrainDrawing.ChartPoint.EDGE)
				|| (line.p2.type == TrainDrawing.ChartPoint.EDGE)) {
			MessageBox dlg = new MessageBox(mainView.mainFrame,
					"跨界行车线不能添加停战，请调整时间轴！");
			dlg.showMessage();
			return;
		}

		// if(line.p1.getStationName())
		int stopIndex1 = chart.circuit
				.getStationIndex(line.p1.getStationName());
		int stopIndex2 = chart.circuit
				.getStationIndex(line.p2.getStationName());
		if ((stopIndex1 == -1) || (stopIndex2 == -1))
			return;

		// if(stopIndex2 <= addIndex)
		// return;

		Train train = mainView.activeTrainDrawing.train;
		int addIndex = chart.circuit.getNextStationIndex(train, stopIndex1);
		if (((train.isDownTrain(chart.circuit) == Train.DOWN_TRAIN) && (addIndex >= stopIndex2))
				|| ((train.isDownTrain(chart.circuit) == Train.UP_TRAIN) && (addIndex <= stopIndex2)))
			return;

		Stop afterStop = mainView.getDrawStops(train)[line.p1.getDrawStopIndex()];
		String arriveTime = mainView.getTime((line.p2.x - line.p1.x) / 2
				+ line.p1.x);
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		try {
			Date arrive = df.parse(arriveTime);
			Date leave = arrive;
			String newStationName = chart.circuit.stations[addIndex].name;
			train.insertStopAfter(afterStop, newStationName, arrive, leave);

			setState(STATE_ADD_STOP);
			mainView.activeTrainDrawing = new TrainDrawing(chart, mainView, train);
			mainView.activeTrainDrawing.setMovingPoint(newStationName,
					TrainDrawing.ChartPoint.STOP_ARRIVE);

			repaint();

			System.out.println("line:" + line.getInfo() + "\r\nAfter: "
					+ afterStop.stationName
					// +"p1:"+line.p1.getStationName()
					// +"p2:"+line.p2.getStationName()
					+ ", NewStation: " + chart.circuit.stations[addIndex].name
					// +",leave1:"+m1
					// +",arrive2:"+m2
					+ ", newTime: " + arrive);
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
	}

	private void setColor() {
		final JColorChooser colorChooser = new JColorChooser();
		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainView.activeTrainDrawing.train.color = colorChooser.getColor();
				LinesPanel.this.repaint();
			}
		};

		JDialog dialog = JColorChooser.createDialog(mainView.mainFrame,
				"请选择行车线颜色", true, // modal
				colorChooser, listener, // OK button handler
				null); // no CANCEL button handler
		colorChooser.setColor(mainView.activeTrainDrawing.train.color);
		ETRC.setFont(dialog);

		Dimension dlgSize = dialog.getPreferredSize();
		Dimension frmSize = mainView.mainFrame.getSize();
		Point loc = mainView.mainFrame.getLocation();
		dialog.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
				(frmSize.height - dlgSize.height) / 2 + loc.y);
		dialog.setVisible(true);
	}

	// private boolean showToolTip = false;
//	private void editTimes(Point p) {
//		if (mainView.activeTrainDrawing == null)
//			return;
//
//		JDialog dialog = new TimeSheetEditDialog(chart, 
//				mainView, mainView.activeTrainDrawing.train);
//
//		Dimension dlgSize = dialog.getPreferredSize();
//		Dimension frmSize = mainView.mainFrame.getSize();
//		Point loc = mainView.mainFrame.getLocation();
//		dialog.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
//				(frmSize.height - dlgSize.height) / 2 + loc.y);
//		dialog.setVisible(true);
//	}
	
	private void editActiveTrain() {
		TrainDialog dialog = new TrainDialog(mainView.mainFrame, mainView.activeTrainDrawing.train);

		dialog.editTrain();
		
		if(!dialog.isCanceled) {
			Train editedTrain = dialog.getTrain();
			//没有改车次的情况，更新
			if(chart.isLoaded(editedTrain)) {
				chart.updateTrain(editedTrain);
			}
			//改了车次的情况，删掉原来的，增加新的
			else {
				chart.delTrain(mainView.activeTrainDrawing.train);
				chart.addTrain(editedTrain);
			}
			
			mainView.activeTrainDrawing = new TrainDrawing(chart, mainView, editedTrain);
			//mainView.repaint();
		}
	}

	/**
	 * mouseEntered
	 * 
	 * @param e
	 *            MouseEvent
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * mouseExited
	 * 
	 * @param e
	 *            MouseEvent
	 */
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * mousePressed
	 * 
	 * @param e
	 *            MouseEvent
	 */
	public void mousePressed(MouseEvent e) {
	}

	/**
	 * mouseReleased
	 * 
	 * @param e
	 *            MouseEvent
	 */
	public void mouseReleased(MouseEvent e) {
	}

	public String getToolTipText(MouseEvent event) {
		Point p = event.getPoint();

		if (mainView.activeTrainDrawing != null) {
			TrainDrawing.TrainLine line = mainView.activeTrainDrawing
					.findNearTrainLine(p);
			if (line != null)
				return line.getInfo();
			else if (mainView.activeTrainDrawing.pointOnMyRect(p))
				return mainView.activeTrainDrawing.getInfo();
		}

		return null;
	}

	public JToolTip createToolTip() {
		JToolTip toolTip = new JToolTip();
		toolTip.setFont(new Font("Dialog", 0, 12));
		return toolTip;
	}

	public void moveToTrainDrawing(TrainDrawing trainDrawing) {
		Rectangle bounds = trainDrawing.getBounds();
		bounds.y = 0;
		bounds.height = this.getHeight();
//		Rectangle bounds = trainDrawing.firstRect.getBounds();
		scrollRectToVisible(bounds);
		selectTrain(trainDrawing);
	}
}
