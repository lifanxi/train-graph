package org.paradise.etrc.view.chart.traindrawing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class ChartPoint extends Point {
	private static final long serialVersionUID = -2370637241346543214L;

	//点类型
	public static final int UNKOW = -99;
	public static final int EDGE = -1;
	public static final int PASS = 0; //实际上没有用到PASS类型
	public static final int START = 1;
	public static final int TERMINAL = 2;
	public static final int STOP_ARRIVE = 3;
	public static final int STOP_LEAVE = 4;
	public static final int ARRIVE_IN_CHART = 5;
	public static final int LEAVE_OUT_CHART = 6;

	int type;

	//是否图定
	public boolean isSchedular = true;
	//所属的列车图线
	private TrainDrawing trainDrawing;
	
	public ChartPoint(TrainDrawing _trainDrawing, int _x, int _y, int _type, boolean _isSchedular) {
		super(_x, _y);
		trainDrawing = _trainDrawing;
		type = _type;
		isSchedular = _isSchedular;
	}

	/**
	 * DrawStopPoit
	 *
	 * @param g Graphics
	 * @param x0 int
	 * @param y0 int
	 */
	private void drawStopPoint(Graphics g) {
		Color oldColor = g.getColor();
		if (!isSchedular && !oldColor.equals(trainDrawing.chartView.underDrawingColor)) {
			g.setColor(TrainDrawing.notSchedularColor);
		}

		g.fillRect(x - 1, y - 1, 3, 3);

		g.setColor(oldColor);
	}

	private void drawStopPointActive(Graphics g) {
		Color oldColor = g.getColor();
		if (!isSchedular && !oldColor.equals(trainDrawing.chartView.underDrawingColor)) {
			g.setColor(TrainDrawing.notSchedularColor);
		}

		g.fillRect(x - 2, y - 2, 5, 5);

		g.setColor(oldColor);
	}

	//    private void drawStopPointSelected(Graphics g) {
	//		Color oldColor = g.getColor();
	//    	if(!isSchedular && !oldColor.equals(chartView.underDrawingColor)) {
	//    		g.setColor(notSchedularColor);
	//    	}
	//		
	//		Color drawingColor = g.getColor();
	//		g.setColor(selectedColor);
	//		g.fillRect(x - 2, y - 2, 5, 5);
	//		g.setColor(drawingColor);
	//		g.fillRect(x - 1, y - 1, 3, 3);
	//
	//		g.setColor(oldColor);
	//    }

	//    /**
	//     * drawMoving 画一个叉
	//     *
	//     * @param g Graphics
	//     */
	//    private void drawMoving(Graphics g) {
	//      Color oldColor = g.getColor();
	//      g.setColor(selectedColor);
	//
	//      g.drawLine(x-3, y-3, x+3, y+3);
	//      g.drawLine(x+3, y-3, x-3, y+3);
	//
	//      g.setColor(oldColor);
	//    }

	void draw(Graphics g) {
		if (trainDrawing.isActive)
			drawActive(g);
		else
			drawNormal(g);
	}

	private void drawNormal(Graphics g) {
		if (!trainDrawing.chartView.isDrawNormalPoint)
			return;
		
		switch (type) {
		case EDGE:
		case PASS:
			break;
		case STOP_ARRIVE:
		case STOP_LEAVE:
		case START:
		case TERMINAL:
		case ARRIVE_IN_CHART:
		case LEAVE_OUT_CHART:
			drawStopPoint(g);
			break;
		}
	}

	private void drawActive(Graphics g) {
		switch (type) {
		case EDGE:
		case PASS:
			break;
		case STOP_ARRIVE:
		case STOP_LEAVE:
		case START:
		case TERMINAL:
		case ARRIVE_IN_CHART:
		case LEAVE_OUT_CHART:
			drawStopPointActive(g);
			break;
		}
	}

	//  private void drawSelected(Graphics g) {
	//    switch(type) {
	//      case EDGE:
	//      case PASS:
	//        break;
	//      case STOP_ARRIVE:
	//      case STOP_LEAVE:
	//      case START:
	//      case TERMINAL:
	//      case ARRIVE_IN_CHART:
	//      case LEAVE_OUT_CHART:
	//        drawStopPointSelected(g);
	//        break;
	//    }
	//  }

//	public String getStationName() {
//		return chartView.mainFrame.chart.circuit.getStationName(chartView
//				.getDist(y));
//	}
//
//	public int getDrawStopIndex() {
//		return chartView.getDrawStopIndex(train, getStationName());
//	}

}
