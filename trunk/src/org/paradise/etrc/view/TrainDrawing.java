package org.paradise.etrc.view;

import java.util.*;

import java.awt.*;
import java.awt.geom.*;

import org.paradise.etrc.data.*;

/**
 * @author lguo@sina.com
 * @version 1.0
 */

public class TrainDrawing {
  public Stop drawStops[];

  public Train train;
  public Chart chart;
  public MainView mainView;
  public Vector lines = new Vector();
  public TrainLine selectedTrainLine = null;
  public ChartPoint movingPoint = null;
  //private Vector points = new Vector();
  public TrainNameRect firstRect = null;
  public TrainNameRect lastRect = null;
  private TrainNameLR leftName = null;
  private TrainNameLR rightName = null;

  private boolean drawMe = true;

  //public boolean isActive = false;

  public static final Color selectedColor = Color.black;

  public TrainDrawing(Chart _c, MainView _m, Train _t) {
    chart = _c;
    mainView = _m;
    train = _t;
    //train.color = _color;
    //isActive = _active;

    buildUp();
  }

  private void buildUp() {
    int direction = train.isDownTrain(chart.circuit);
    String trainName = getTrainName();

    //首先计算要画的停站
    drawStops = mainView.getDrawStops(train);
    //少于两个停站的车次不画
    if(drawStops.length < 2)
      drawMe = false;

    //int lastX = -1;
    //int lastY = -1;
    ChartPoint lastPoint = new ChartPoint(-1, -1, ChartPoint.UNKOW);
    for (int i = 0; i < drawStops.length; i++) {
      Date arriveClock = drawStops[i].arrive;
      int x0 = mainView.getPelsX(arriveClock);
      Date leaveClock = drawStops[i].leave;
      int x1 = mainView.getPelsX(leaveClock);

      int y0 = mainView.getPelsY(drawStops[i].stationName);
      int y1 = mainView.getPelsY(drawStops[i].stationName);

      ChartPoint p0 = new ChartPoint(x0, y0, ChartPoint.STOP_ARRIVE);
      ChartPoint p1 = new ChartPoint(x1, y1, ChartPoint.STOP_LEAVE);

      //第一个停站的到达点，判断p0是入图还是始发
      if (i == 0) {
        //始发
        if (drawStops[i].stationName.equalsIgnoreCase(train.getStartStation())) {
          p0.type = ChartPoint.START;
          firstRect = new TrainNameRect(p0, trainName, direction);
        }
        //入图
        else {
          p0.type = ChartPoint.ARRIVE_IN_CHART;
          firstRect = new TrainNameRect(p0, trainName, direction);
        }
      }

      //最后一个停站的出发点，判断p1是出图还是终到
      if (i == drawStops.length - 1) {
        //终到
        if (drawStops[i].stationName.equalsIgnoreCase(train.getTerminalStation())) {
          p1.type = ChartPoint.TERMINAL;
          lastRect = new TrainNameRect(p1, trainName, direction);
        }
        //出图
        else {
          p1.type = ChartPoint.LEAVE_OUT_CHART;
          lastRect = new TrainNameRect(p1, trainName, direction);
        }
      }

      ChartPoint pe0, pe1;
      //先画与上一点的连线
      if (lastPoint.type != ChartPoint.UNKOW) {
        lines.add(new TrainLine(lastPoint, p0, TrainLine.RUN_LINE));
      }

      //在停车过程中过边界：停车线分两段画
      if (x1 < x0) {
        pe0 = new ChartPoint(mainView.getPelsX(24 * 60), y0, ChartPoint.EDGE);
        pe1 = new ChartPoint(mainView.getPelsX(0), y0, ChartPoint.EDGE);
        lines.add(new TrainLine(p0, pe0, TrainLine.STOP_LINE));
        lines.add(new TrainLine(pe1, p1, TrainLine.STOP_LINE));
        leftName = new TrainNameLR(pe1, trainName, true);
        rightName = new TrainNameLR(pe0, trainName, false);
        lastPoint = p1;
      }
      //最后一站（不需要判断下站过界问题）
      else if (i == drawStops.length - 1) {
        lines.add(new TrainLine(p0, p1, TrainLine.STOP_LINE));

        lastPoint = p1;
      }
      else {
        //下一到站将过边界：停车线正常，但下段的行车线要先画一半到边界
        if (mainView.getPelsX(drawStops[i + 1].arrive) < x1) {
          int x2 = mainView.getPelsX(drawStops[i + 1].arrive) +
              24 * 60 * chart.minuteScale;
          int y2 = mainView.getPelsY(drawStops[i + 1].stationName);

          //计算右边界点坐标
          int bx = mainView.getPelsX(24 * 60);
          int by = y1 + (bx - x1) * (y2 - y1) / (x2 - x1);
          pe0 = new ChartPoint(bx, by, ChartPoint.EDGE);
          pe1 = new ChartPoint(mainView.getPelsX(0), by, ChartPoint.EDGE);
          leftName = new TrainNameLR(pe1, trainName, true);
          rightName = new TrainNameLR(pe0, trainName, false);

          //停车线
          lines.add(new TrainLine(p0, p1, TrainLine.STOP_LINE));
          //行车线的上半段
          lines.add(new TrainLine(p1, pe0, TrainLine.RUN_LINE));

          //行车线下半段的起始坐标
          lastPoint = pe1;
        }
        //一般停站(下一站不过边界)
        else {
          lines.add(new TrainLine(p0, p1, TrainLine.STOP_LINE));

          lastPoint = p1;
        }
      }
    }
  }

  public void drawUnder(Graphics g) {
    if(!drawMe)
      return;

    if(mainView.underDrawingColor == null)
      return;

    Color oldColor = g.getColor();
    g.setColor(mainView.underDrawingColor);
    //System.out.println(train.getTrainName(chart.circuit) + color.toString());

    for (Enumeration e = lines.elements(); e.hasMoreElements(); ) {
      Object obj = e.nextElement();
      if (obj instanceof TrainLine)
        ( (TrainLine) obj).draw(g);
    }

    firstRect.drawInactive(g);
    lastRect.drawInactive(g);

    if(leftName != null)
      leftName.draw(g);
    if(rightName != null)
      rightName.draw(g);

    g.setColor(oldColor);
  }

  public void drawInactive(Graphics g) {
    if(!drawMe)
      return;

    Color oldColor = g.getColor();
    g.setColor(train.color);
    //System.out.println(train.getTrainName(chart.circuit) + color.toString());

    for (Enumeration e = lines.elements(); e.hasMoreElements(); ) {
      Object obj = e.nextElement();
      if (obj instanceof TrainLine)
        ( (TrainLine) obj).draw(g);
    }

    firstRect.drawInactive(g);
    lastRect.drawInactive(g);

    if(leftName != null)
      leftName.draw(g);
    if(rightName != null)
      rightName.draw(g);

    g.setColor(oldColor);
  }

  /**
   * drawActive
   *
   * @param g Graphics
   */
  public void drawActive(Graphics g) {
    if(!drawMe)
      return;

    Color oldColor = g.getColor();
    g.setColor(train.color);

    for (Enumeration e = lines.elements(); e.hasMoreElements(); ) {
      Object obj = e.nextElement();
      if (obj instanceof TrainLine)
        ( (TrainLine) obj).drawActive(g);
    }

    firstRect.drawActive(g);
    lastRect.drawActive(g);

    if(leftName != null)
      leftName.drawActive(g);
    if(rightName != null)
      rightName.drawActive(g);

    if(selectedTrainLine != null) {
      selectedTrainLine.drawSelected(g);
    }

    if(movingPoint != null) {
      movingPoint.drawMoving(g);
    }

    //System.out.println("DrawA"+i+++" lineNum:"+lines.size());

    g.setColor(oldColor);
  }
  //static int i = 1;

  public boolean equals(Object obj) {
    if(obj == null)
      return false;

    if(!(obj instanceof TrainDrawing))
      return false;

    TrainDrawing td = (TrainDrawing)obj;
    return td.getTrainName().equalsIgnoreCase(this.getTrainName());
  }

  public boolean isSameTrain(Train train) {
    if(train == null)
      return false;

    return train.equals(this.train);
  }

  public void setMovingPoint(String stationName, int pointType) {
    for(Enumeration e = lines.elements(); e.hasMoreElements(); ) {
      TrainLine line = (TrainLine)e.nextElement();
      if(line.p1.getStationName().equalsIgnoreCase(stationName)
         &&line.p1.type == pointType)
        movingPoint = line.p1;
      else if(line.p2.getStationName().equalsIgnoreCase(stationName)
        &&line.p2.type == pointType)
        movingPoint = line.p2;
    }
  }

  public void setMovingPoint(int x, int y, int pointType) {
    this.movingPoint = new ChartPoint(x, y, pointType);
  }

  /**
   * pointOnMe
   *
   * @param p Point
   * @return boolean
   */
  public boolean pointOnMe(Point p) {
    for(Enumeration e = lines.elements(); e.hasMoreElements(); ) {
      TrainLine line = (TrainLine)e.nextElement();
      if(line.pointOnMe(p))
        return true;
    }

    return false;
  }

  public boolean pointOnMyRect(Point p) {
    if(firstRect == null || lastRect == null)
      return false;

    if(firstRect.pointOnMe(p) || lastRect.pointOnMe(p))
      return true;
    else
      return false;
  }

  public TrainLine findNearTrainLine(Point p) {
    for(Enumeration e = lines.elements(); e.hasMoreElements(); ) {
      TrainLine line = (TrainLine)e.nextElement();
      if(line.pointOnMe(p))
        return line;
    }
    return null;
  }

  public String getTrainName() {
    return train.getTrainName(chart.circuit);
  }

  /**
   * getBounds
   *
   * @return Rectangle
   */
  public Rectangle getBounds() {
    int left = Integer.MAX_VALUE;
    int top = Integer.MAX_VALUE;
    int right = Integer.MIN_VALUE;
    int bottom = Integer.MIN_VALUE;
    for(Enumeration e = lines.elements(); e.hasMoreElements(); ) {
      TrainLine line = (TrainLine)e.nextElement();
      left = Math.min(Math.min(left, line.p1.x), line.p2.x);
      top = Math.min(Math.min(top, line.p1.y), line.p2.y);
      right = Math.max(Math.max(right, line.p1.x), line.p2.x);
      bottom = Math.max(Math.max(bottom, line.p1.y), line.p2.y);
    }
    left = Math.min(Math.min(left, firstRect.getBounds().x), lastRect.getBounds().x);
    top = Math.min(Math.min(top, firstRect.getBounds().y), lastRect.getBounds().y);
    right = Math.max(Math.max(right, firstRect.getBounds().x), lastRect.getBounds().x);
    bottom = Math.max(Math.max(left, firstRect.getBounds().y), lastRect.getBounds().y);
    return new Rectangle(left-10, top-10, right - left + 20, bottom - top + 20);
  }

  /**
   * getInfo
   */
  public String getInfo() {
    Stop stop[] = mainView.getDrawStops(train);
    Date in = stop[0].arrive;
    Date out = stop[stop.length-1].leave;

    long time = out.getTime() - in.getTime();
    long min = time/(1000*60);
    if(min < 0)
      min += 24*60;

    int dist = Math.abs(chart.circuit.getStationDist(stop[0].stationName)
                      - chart.circuit.getStationDist(stop[stop.length-1].stationName));
    
    if(min == 0)
    	return train.getTrainName() + "Error!";

    return train.getTrainName() + "次 "
        + train.getStartStation() + "至" + train.getTerminalStation()
        + " 在" + chart.circuit.name + "行驶" + dist + "公里 "
        + "耗时" + min + "分钟 旅行速度" + dist*60/min +"公里/小时";
  }

  public class TrainLine {
    public static final int STOP_LINE = 0;
    public static final int RUN_LINE = 1;
    public int lineType;

    public ChartPoint p1,p2;
    public TrainLine(ChartPoint _p1, ChartPoint _p2, int _type) {
      p1 = _p1;
      p2 = _p2;
      lineType = _type;
    }

    public void draw(Graphics g) {
      g.drawLine(p1.x, p1.y, p2.x, p2.y);
      if(lineType == STOP_LINE) {
        //终到站的stop线（0长度）不画arrive点
        if(p2.type != ChartPoint.TERMINAL)
          p1.draw(g);
        //始发站的stop线（0长度）不画leave点
        if(p1.type != ChartPoint.START)
          p2.draw(g);
      }
    }

    public void drawActive(Graphics g) {
      g.drawLine(p1.x, p1.y, p2.x, p2.y);
      //画差1个像素的线加重
      if(lineType == STOP_LINE)
        g.drawLine(p1.x, p1.y+1, p2.x, p2.y+1);
      else
        g.drawLine(p1.x+1, p1.y, p2.x+1, p2.y);

      //如果是停车线则画arrive点和leave点
      if(lineType == STOP_LINE) {
        //终到站的stop线（0长度）不画arrive点
        if(p2.type != ChartPoint.TERMINAL)
          p1.drawActive(g);
        //始发站的stop线（0长度）不画leave点
        if(p1.type != ChartPoint.START)
          p2.drawActive(g);
      }
    }

    public void drawSelected(Graphics g) {
      g.drawLine(p1.x, p1.y, p2.x, p2.y);

      //以特殊颜色画差1个像素的线
      Color oldColor = g.getColor();
      g.setColor(selectedColor);
      if(lineType == STOP_LINE) {
        g.drawLine(p1.x, p1.y - 1, p2.x, p2.y - 1);
        g.drawLine(p1.x, p1.y + 1, p2.x, p2.y + 1);
      }
      else {
        g.drawLine(p1.x - 1, p1.y, p2.x - 1, p2.y);
        g.drawLine(p1.x + 1, p1.y, p2.x + 1, p2.y);
      }
      g.setColor(oldColor);

      p1.drawSelected(g);
      p2.drawSelected(g);
    }

    public boolean pointOnMe(Point p) {
      final int MAX_GAP = 5;
      if((p.x <= Math.min(p1.x, p2.x) - MAX_GAP)
         || (p.x >= Math.max(p1.x, p2.x) + MAX_GAP)
         || (p.y <= Math.min(p1.y, p2.y) - MAX_GAP)
         || (p.y >= Math.max(p1.y, p2.y) + MAX_GAP)) {
        return false;
      }

      if((p1.x == p2.x)&&(p1.y == p2.y))
        return (p.x == p1.x)&&(p.y == p1.y);
      else {
        double gap = Line2D.ptLineDist(p1.x, p1.y, p2.x, p2.y, p.x, p.y);
        //System.out.println(trainDrawing.train.getTrainName(trainDrawing.chart.circuit) + ":" + gap);
        if (!Double.isNaN(gap))
          return gap < MAX_GAP;
        return false;
      }
    }

    public String getInfo() {
      if(lineType == STOP_LINE) {
        int stopMinutes = Math.abs(mainView.getMinute(p2.x) - mainView.getMinute(p1.x));
        String stationName = mainView.getStationName(p1.y);
        return TrainDrawing.this.getTrainName() + "次 "
            + stationName + "站 停车" + stopMinutes + "分钟 ("
            + mainView.getTime(p1.x) + "到 " + mainView.getTime(p2.x) + "发)";
      }
      else{
        String station1 = mainView.getStationName(p1.y);
        String station2 = mainView.getStationName(p2.y);
        int dist = Math.abs(mainView.getDist(p2.y) - mainView.getDist(p1.y));
        int runMinutes = mainView.getMinute(p2.x) - mainView.getMinute(p1.x);
        return TrainDrawing.this.getTrainName() + "次 "
            +station1+"至"+station2
            +" 区间"+dist+"公里"
            +" 行车"+runMinutes+"分钟"
            +"("+ mainView.getTime(p1.x) + "发" + mainView.getTime(p2.x) + "到)"
            +" 平均时速"+dist*60/runMinutes+"公里";
      }
    }
  }

  public class ChartPoint extends Point {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2370637241346543214L;
	
	public static final int UNKOW = -99;
    public static final int EDGE = -1;
    public static final int PASS = 0;
    public static final int START = 1;
    public static final int TERMINAL = 2;
    public static final int STOP_ARRIVE = 3;
    public static final int STOP_LEAVE = 4;
    public static final int ARRIVE_IN_CHART = 5;
    public static final int LEAVE_OUT_CHART = 6;
    int type;

    public ChartPoint(int _x, int _y, int _type) {
      super(_x, _y);
      type = _type;
    }

    public ChartPoint(int _x, int _y) {
      this(_x, _y, PASS);
    }

    /**
     * DrawStopPoit
     *
     * @param g Graphics
     * @param x0 int
     * @param y0 int
     */
    private void drawStopPoint(Graphics g) {
      g.fillRect(x-1, y-1, 3, 3);
    }

    private void drawStopPointActive(Graphics g) {
      g.fillRect(x-2, y-2, 5, 5);
    }

    private void drawStopPointSelected(Graphics g) {
      Color oldColor = g.getColor();
      g.setColor(selectedColor);
      g.fillRect(x-2, y-2, 5, 5);
      g.setColor(oldColor);
      g.fillRect(x-1, y-1, 3, 3);
    }

    /**
     * drawMoving
     *
     * @param g Graphics
     */
    private void drawMoving(Graphics g) {
      Color oldColor = g.getColor();
      g.setColor(selectedColor);

      g.drawLine(x-3, y-3, x+3, y+3);
      g.drawLine(x+3, y-3, x-3, y+3);

      g.setColor(oldColor);
    }

    /**
     * draw
     *
     * @param g Graphics
     * @param isDownTrain int
     */
    private void draw(Graphics g) {
      switch(type) {
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
    switch(type) {
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

  private void drawSelected(Graphics g) {
    switch(type) {
      case EDGE:
      case PASS:
        break;
      case STOP_ARRIVE:
      case STOP_LEAVE:
      case START:
      case TERMINAL:
      case ARRIVE_IN_CHART:
      case LEAVE_OUT_CHART:
        drawStopPointSelected(g);
        break;
    }
  }

  public String getStationName() {
    return chart.circuit.getStationName(mainView.getDist(y));
  }

  public int getDrawStopIndex() {
    return mainView.getDrawStopIndex(train, getStationName());
  }

  }

  public class TrainNameLR {
    private ChartPoint anchor;
    private String trainName;
    private boolean onLeft;

    public TrainNameLR(ChartPoint _p, String _name, boolean _left) {
      anchor = _p;
      trainName = _name;
      onLeft = _left;
    }

    public void draw(Graphics g) {
      int x = anchor.x;
      int y = anchor.y;
      if(onLeft)
        x -= g.getFontMetrics().stringWidth(trainName)+1;
      else
        x += 3;
      g.drawString(trainName, x, y);
    }

    public void drawActive(Graphics g) {
      int x = anchor.x;
      int y = anchor.y;

      if(onLeft)
        x -= g.getFontMetrics().stringWidth(trainName)+1;
      else
        x += 3;

      g.drawString(trainName, x, y);

      if(onLeft)
        g.drawLine(x-1, y+1, anchor.x, anchor.y+1);
      else
        g.drawLine(anchor.x, anchor.y+1, x+g.getFontMetrics().stringWidth(trainName), y+1);
    }
  }

  public class TrainNameRect {
    private ChartPoint anchor;
    private int isDownTrain;
    private String trainName;

    /**
     * TrainNameRect
     *
     * @param chartPoint ChartPoint
     * @param i int
     */
    public TrainNameRect(ChartPoint _p, String _name, int _dir) {
      anchor = _p;
      trainName = _name;
      isDownTrain = _dir;
    }

    /**
     * pointOnMe
     *
     * @return boolean
     */
    private boolean pointOnMe(Point p) {
      switch(isDownTrain) {
        case Train.DOWN_TRAIN:
          switch(anchor.type) {
            case ChartPoint.ARRIVE_IN_CHART:
            case ChartPoint.START:
              return pointOnMeTop(p);
            case ChartPoint.LEAVE_OUT_CHART:
            case ChartPoint.TERMINAL:
              return pointOnMeBottom(p);
            default:
              return false;
          }
        case Train.UP_TRAIN:
          switch(anchor.type) {
            case ChartPoint.ARRIVE_IN_CHART:
            case ChartPoint.START:
              return pointOnMeBottom(p);
            case ChartPoint.LEAVE_OUT_CHART:
            case ChartPoint.TERMINAL:
              return pointOnMeTop(p);
            default:
              return false;
          }
        default:
          return false;
      }
    }

    private boolean pointOnMeTop(Point p) {
      if((p.x > anchor.x - 5)
         &&(p.x < anchor.x + 5)
         &&(p.y > anchor.y - mainView.trainNameRecHeight - 10)
         &&(p.y < anchor.y))
        return true;
      else
        return false;
    }

    private boolean pointOnMeBottom(Point p) {
      if((p.x > anchor.x - 5)
         &&(p.x < anchor.x + 5)
         &&(p.y < anchor.y + mainView.trainNameRecHeight + 10)
         &&(p.y > anchor.y))
        return true;
      else
        return false;
    }

    public void drawActive(Graphics g) {
      draw(g, true);
    }

    public void drawInactive(Graphics g) {
      draw(g, false);
    }

    private void draw(Graphics g, boolean isActive) {
      //isActive = _isActive;
      switch(anchor.type) {
        case ChartPoint.ARRIVE_IN_CHART:
          drawInRect(g, isActive);
          break;
        case ChartPoint.LEAVE_OUT_CHART:
          drawOutRect(g, isActive);
          break;
        case ChartPoint.START:
          drawStartRect(g, isActive);
          break;
        case ChartPoint.TERMINAL:
          drawTerminalRect(g, isActive);
          break;
      }
    }

    public Rectangle getBounds() {
      Rectangle rec = new Rectangle();
      int left = anchor.x - 5;
      int top = anchor.y;
      switch(isDownTrain) {
        case Train.DOWN_TRAIN:
          top  = anchor.y - mainView.trainNameRecMargin
                 - 10 - mainView.trainNameRecHeight;
          break;
        case Train.UP_TRAIN:
          top = anchor.y + mainView.trainNameRecMargin + 10;
          break;
      }
      rec.x = left;
      rec.y = top;
      rec.width = 10;
      rec.height = mainView.trainNameRecMargin + mainView.trainNameRecMargin + 10;
      return rec;
    }

    //入图
    private void drawInRect(Graphics g, boolean isActive) {
      if(isDownTrain == Train.DOWN_TRAIN)
        drawInRectDown(g, isActive);
      else
        drawInRectUp(g, isActive);
    }

    //下行入图
    private void drawInRectDown(Graphics g, boolean isActive) {
      int x = anchor.x;
      int y = anchor.y;

      int y1 = y - mainView.trainNameRecMargin;
      int px[] = {x, x-5, x+5};
      int py[] = {y1, y1-10, y1-10};
      g.fillPolygon(px, py, 3);
      //g.drawLine(x, y, x, y1);
      drawNameRec(g, x-5, y1-10-mainView.trainNameRecHeight, isActive);
    }

    //上行入图
    private void drawInRectUp(Graphics g, boolean isActive) {
      int x = anchor.x;
      int y = anchor.y;

      int y1 = y + mainView.trainNameRecMargin;
      int px[] = {x, x-5, x+5};
      int py[] = {y1, y1+10, y1+10};
      g.fillPolygon(px, py, 3);
      //g.drawLine(x, y, x, y1);
      this.drawNameRec(g, x-5, y1+10, isActive);
    }

    //出图
    private void drawOutRect(Graphics g, boolean isActive) {
      if(isDownTrain == Train.DOWN_TRAIN)
        drawOutRectDown(g, isActive);
      else
        drawOutRectUp(g, isActive);
    }

    //下行出图
    private void drawOutRectDown(Graphics g, boolean isActive) {
      int x = anchor.x;
      int y = anchor.y;

      int y1 = y + mainView.trainNameRecMargin;
      int y2 = y1 + mainView.trainNameRecHeight;
      int px[] = {
          x, x - 5, x + 5};
      int py[] = {
          y2 + 10, y2, y2};
      g.fillPolygon(px, py, 3);
      drawNameRec(g, x-5, y1, isActive);
    }

    //上行出图
    private void drawOutRectUp(Graphics g, boolean isActive) {
      int x = anchor.x;
      int y = anchor.y;

      int y1 = y - mainView.trainNameRecHeight - mainView.trainNameRecMargin;
      int px[] = {x, x-5, x+5};
      int py[] = {y1-10, y1, y1};
      g.fillPolygon(px, py, 3);
      drawNameRec(g, x-5, y1, isActive);
    }

    //始发
    private void drawStartRect(Graphics g, boolean isActive) {
      if(isDownTrain == Train.DOWN_TRAIN)
        drawStartRectDown(g, isActive);
      else
        drawStartRectUp(g, isActive);
    }

    //下行始发
    private void drawStartRectDown(Graphics g, boolean isActive) {
      int x = anchor.x;
      int y = anchor.y;

      int y1 = y - mainView.trainNameRecMargin;

      g.fillRect(x-5, y1-mainView.trainNameRecHeight-5, 11, 4);

      drawNameRec(g, x-5, y1-mainView.trainNameRecHeight, isActive);
    }

    //上行始发
    private void drawStartRectUp(Graphics g, boolean isActive) {
      int x = anchor.x;
      int y = anchor.y;

      int y1 = y + mainView.trainNameRecMargin;

      g.fillRect(x-5, y1+mainView.trainNameRecHeight+2, 11, 4);

      drawNameRec(g, x-5, y1, isActive);
    }

    //终到
    private void drawTerminalRect(Graphics g, boolean isActive) {
      if(isDownTrain == Train.DOWN_TRAIN)
        drawTerminalRectDown(g, isActive);
      else
        drawTerminalRectUp(g, isActive);
    }

    //下行终到
    private void drawTerminalRectDown(Graphics g, boolean isActive) {
      int x = anchor.x;
      int y = anchor.y;

      int y1 = y + mainView.trainNameRecMargin;

      g.fillRect(x-5, y1+mainView.trainNameRecHeight+2, 11, 4);

      drawNameRec(g, x-5, y1, isActive);
    }

    //上行终到
    private void drawTerminalRectUp(Graphics g, boolean isActive) {
      int x = anchor.x;
      int y = anchor.y;

      int y1 = y - mainView.trainNameRecMargin;

      g.fillRect(x-5, y1-mainView.trainNameRecHeight-5, 11, 4);

      drawNameRec(g, x-5, y1-mainView.trainNameRecHeight, isActive);
    }

    private void drawNameRec(Graphics g, int x, int y, boolean isActive) {
      if(isActive)
        drawNameRecActive(g, x, y);
      else
        drawNameRecInactive(g, x, y);
    }

    //画不活跃车次框
    private void drawNameRecInactive(Graphics g, int x, int y) {
      Color oldColor = g.getColor();
      g.setColor(Color.white);
      g.fillRect(x, y, 10, mainView.trainNameRecHeight);
      g.setColor(oldColor);

      g.drawRect(x, y, 10, mainView.trainNameRecHeight);

      int h = g.getFontMetrics().getHeight() - 5;
      int xt = x + 3;
      int yt = y + h + 2;
      for(int i=0; i<trainName.length(); i++) {
        g.drawString(trainName.substring(i,i+1), xt, yt+h*i);
      }
    }

    //画活跃车次框
    private void drawNameRecActive(Graphics g, int x, int y) {
      //Color oldColor = g.getColor();
      //g.setColor(Color.white);
      g.fillRect(x, y, 11, mainView.trainNameRecHeight+1);
      //g.setColor(oldColor);

      //g.drawRect(x, y, 10, chart.trainNameRecHeight);

      Color oldColor = g.getColor();
      g.setColor(Color.white);
      int h = g.getFontMetrics().getHeight() - 5;
      int xt = x + 3;
      int yt = y + h + 2;
      for(int i=0; i<trainName.length(); i++) {
        g.drawString(trainName.substring(i,i+1), xt, yt+h*i);
      }
      g.setColor(oldColor);
    }
  }
}

