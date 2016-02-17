package org.paradise.etrc.view.chart.traindrawing;

import org.paradise.etrc.data.Chart;
import org.paradise.etrc.data.Stop;
import org.paradise.etrc.data.Train;
import org.paradise.etrc.view.chart.ChartView;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

/**
 * @author lguo@sina.com
 * @version 1.0
 */

public class TrainDrawing {
    public Train train;

    ChartView chartView;

    private Stop drawStops[];

    //实际存在于图上的各部件
    //行车线和停车线
    private Vector<TrainLine> lines = new Vector<TrainLine>();
    //下面的数组用来存放每个停站点的时刻框
    private Vector<TrainNameTB> namelr = new Vector<TrainNameTB>();
    //车次框

    //private TrainNameRect firstRect = null;
    //private TrainNameRect lastRect = null;
    //更改下面为public为了输入车次查找时屏幕能自动滚动到该车次图线处！
    public TrainNameRect firstRect = null;
    public TrainNameRect lastRect = null;
    //左右空白区域的车次
    private TrainNameLR leftName = null;
    private TrainNameLR rightName = null;

    //是否要画，buildUp的时候判断是否要画（两个站以下的不画）
    private boolean drawMe = true;

    //是否当前选中车次
    boolean isActive = false;
    //是否是反向车次
    private boolean isUnderDrawing = false;

    //  private static final Color selectedColor = Color.black;
    static final Color notSchedularColor = new Color(192, 192, 0);

    public TrainDrawing(ChartView _chartView, Train _train, boolean _active, boolean _under) {
        chartView = _chartView;
        train = _train;
        isActive = _active;
        isUnderDrawing = _under;

        buildUp();
    }

    private void buildUp() {
        Chart chart = chartView.mainFrame.chart;
        int direction = train.isDownTrain(chart.circuit);
        String trainName = getTrainName();

        //首先计算要画的停站
        drawStops = chartView.getDrawStops(train);
        //少于两个停站的车次不画
        if (drawStops.length < 2)
            drawMe = false;

        //int lastX = -1;
        //int lastY = -1;
        ChartPoint lastPoint = new ChartPoint(this, -1, -1, ChartPoint.UNKOW, true);
        for (int i = 0; i < drawStops.length; i++) {
            String arriveClock = drawStops[i].arrive;
            int x0 = chartView.getPelsX(arriveClock);
            String leaveClock = drawStops[i].leave;
            int x1 = chartView.getPelsX(leaveClock);

            int y0 = chartView.getPelsY(drawStops[i].stationName);
            int y1 = chartView.getPelsY(drawStops[i].stationName);

            //2015-07-04增加以下对非本线车站（车站level为-1）铺画运行线(进，出两段)(停站线除外)自动采用虚线的代码
            //System.out.println( chart.circuit.getStationDist(drawStops[i].stationName) );
            //System.out.println( chart.circuit.getStation(drawStops[i].stationName).level );
            boolean isMainTrack;
            if (chart.circuit.getStation(drawStops[i].stationName).level == -1) {
                isMainTrack = false;
            } else if (i > 0 && chart.circuit.getStation(drawStops[i - 1].stationName).level == -1) {
                isMainTrack = false;
            } else {
                isMainTrack = true;
            }


            ChartPoint p0 = new ChartPoint(this, x0, y0, ChartPoint.STOP_ARRIVE, drawStops[i].isPassenger);
            ChartPoint p1 = new ChartPoint(this, x1, y1, ChartPoint.STOP_LEAVE, drawStops[i].isPassenger);


            String aa = train.getStartStation();
            String aaa;
            String aaaa;

            String bb = train.getTerminalStation();
            String bbb;
            String bbbb;

            //2015-06增加以下调整车次头尾信息标注框
            //如果含有（高），（城）字样，则替换掉
            if (aa.indexOf("（高）") >= 0 || aa.indexOf("（城）") >= 0) {
                aaa = aa.replaceAll("（高）", "");
                aaaa = aaa.replaceAll("（城）", "");
            } else {
                aaaa = aa;
            }


            if (bb.indexOf("（高）") >= 0 || bb.indexOf("（城）") >= 0) {
                bbb = bb.replaceAll("（高）", "");
                bbbb = bbb.replaceAll("（城）", "");
            } else {
                bbbb = bb;
            }

            String aaaa_tmp = trainName + aaaa;
            String bbbb_tmp = trainName + bbbb;
            String aaaa_space = "";
// 将少于9位的位用空格补齐，以便显示的时候对齐
            for (int c = 0; c < (9 - aaaa_tmp.length()); c++) {
                aaaa_space = aaaa_space + " ";
            }


            String bbbb_space = "";
// 将少于9位的位用空格补齐，以便显示的时候对齐	 
            for (int c = 0; c < (9 - bbbb_tmp.length()); c++) {
                bbbb_space = bbbb_space + " ";
            }

            if (chartView.SHOWTRAIN_StartEnd == true) {
                //do nothing, keep aaaa, aaaa_space, bbbb, bbbb_space value
            } else {
                //清空以上四个值
                aaaa = "";
                bbbb = "";
                aaaa_space = "";
                bbbb_space = "";
            }

            //显示停站点时间

            String s0 = arriveClock.substring(4, 5);
            String s1 = leaveClock.substring(4, 5);
            //System.out.println(arriveClock);
            //System.out.println(leaveClock);
            //System.out.println(s0);
            //System.out.println(s1);


            //p0.type =  ChartPoint.EDGE;
            //p0.type = ChartPoint.START;

//添加停站时刻文本框至数组中
//到达框一般靠右，出发框一般靠左
//上行框
            if (direction == 1) {
                //下行！
                //到达框
                namelr.add(new TrainNameTB(p0, s0, true, false));
                //出发框
                namelr.add(new TrainNameTB(p1, s1, false, true));
            } else {
                //上行！
                //到达框
                namelr.add(new TrainNameTB(p0, s0, false, false));
                //出发框
                namelr.add(new TrainNameTB(p1, s1, true, true));
            }


            //设定为入图
            //第一个停站的到达点，判断p0是入图还是始发
            if (i == 0) {
                //始发
                if (drawStops[i].stationName.equalsIgnoreCase(train.getStartStation())) {
                    p0.type = ChartPoint.START;
                    firstRect = new TrainNameRect(p0, trainName + aaaa_space + aaaa, direction);
                }
                //入图
                else {
                    p0.type = ChartPoint.ARRIVE_IN_CHART;
                    firstRect = new TrainNameRect(p0, trainName + aaaa_space + aaaa, direction);
                }
            }

            //最后一个停站的出发点，判断p1是出图还是终到
            if (i == drawStops.length - 1) {
                //终到
                if (drawStops[i].stationName.equalsIgnoreCase(train.getTerminalStation())) {
                    p1.type = ChartPoint.TERMINAL;
                    lastRect = new TrainNameRect(p1, trainName + bbbb_space + bbbb, direction);
                }
                //出图
                else {
                    p1.type = ChartPoint.LEAVE_OUT_CHART;
                    lastRect = new TrainNameRect(p1, trainName + bbbb_space + bbbb, direction);
                }
            }

            ChartPoint pe0, pe1;
            //先画与上一点的连线
            if (lastPoint.type != ChartPoint.UNKOW) {
                lines.add(new TrainLine(this, lastPoint, p0, TrainLine.RUN_LINE, true, isMainTrack));
                //以下可调整不办客车站的运行线的颜色
                //lines.add(new TrainLine(this, lastPoint, p0, TrainLine.RUN_LINE, drawStops[i].isPassenger, isMainTrack));
            }

            //在停车过程中过边界：停车线分两段画
            if (x1 < x0) {
                pe0 = new ChartPoint(this, chartView.getPelsX(24 * 60), y0, ChartPoint.EDGE, drawStops[i].isPassenger);
                pe1 = new ChartPoint(this, chartView.getPelsX(0), y0, ChartPoint.EDGE, drawStops[i].isPassenger);
                //lines.add(new TrainLine(this, p0, pe0, TrainLine.STOP_LINE, drawStops[i].isPassenger, isMainTrack));
                lines.add(new TrainLine(this, p0, pe0, TrainLine.STOP_LINE, drawStops[i].isPassenger, true));
                //lines.add(new TrainLine(this, pe1, p1, TrainLine.STOP_LINE, drawStops[i].isPassenger, isMainTrack));
                lines.add(new TrainLine(this, pe1, p1, TrainLine.STOP_LINE, drawStops[i].isPassenger, true));
                leftName = new TrainNameLR(pe1, trainName, true);
                rightName = new TrainNameLR(pe0, trainName, false);
                lastPoint = p1;
            }
            //最后一站（不需要判断下站过界问题）
            else if (i == drawStops.length - 1) {
                //lines.add(new TrainLine(this, p0, p1, TrainLine.STOP_LINE, drawStops[i].isPassenger, isMainTrack));
                lines.add(new TrainLine(this, p0, p1, TrainLine.STOP_LINE, drawStops[i].isPassenger, true));
                lastPoint = p1;
            } else {
                //下一到站将过边界：停车线正常，但下段的行车线要先画一半到边界
                if (chartView.getPelsX(drawStops[i + 1].arrive) < x1) {
                    int x2 = chartView.getPelsX(drawStops[i + 1].arrive) +
                            24 * 60 * chart.minuteScale;
                    int y2 = chartView.getPelsY(drawStops[i + 1].stationName);

                    //计算右边界点坐标
                    int bx = chartView.getPelsX(24 * 60);
                    int by = y1 + (bx - x1) * (y2 - y1) / (x2 - x1);
                    pe0 = new ChartPoint(this, bx, by, ChartPoint.EDGE, drawStops[i].isPassenger);
                    pe1 = new ChartPoint(this, chartView.getPelsX(0), by, ChartPoint.EDGE, drawStops[i].isPassenger);
                    leftName = new TrainNameLR(pe1, trainName, true);
                    rightName = new TrainNameLR(pe0, trainName, false);

                    //停车线
                    //lines.add(new TrainLine(this, p0, p1, TrainLine.STOP_LINE, drawStops[i].isPassenger, isMainTrack));
                    lines.add(new TrainLine(this, p0, p1, TrainLine.STOP_LINE, drawStops[i].isPassenger, true));
                    //行车线的上半段
                    lines.add(new TrainLine(this, p1, pe0, TrainLine.RUN_LINE, drawStops[i].isPassenger, isMainTrack));

                    //行车线下半段的起始坐标
                    lastPoint = pe1;
                }
                //一般停站(下一站不过边界)
                else {
                    //lines.add(new TrainLine(this, p0, p1, TrainLine.STOP_LINE, drawStops[i].isPassenger, isMainTrack));
                    lines.add(new TrainLine(this, p0, p1, TrainLine.STOP_LINE, drawStops[i].isPassenger, true));
                    lastPoint = p1;
                }
            }
        }
    }

    public void draw(Graphics g) {
        if (!drawMe)
            return;

        if (isActive)
            drawActive(g);
        else if (isUnderDrawing)
            drawUnder(g);
        else
            drawNormal(g);
    }

    private void drawUnder(Graphics g) {
        if (chartView.underDrawingColor == null)
            return;

        Color oldColor = g.getColor();
        g.setColor(chartView.underDrawingColor);
        //System.out.println(train.getTrainName(chart.circuit) + color.toString());

        for (Enumeration<TrainLine> e = lines.elements(); e.hasMoreElements(); ) {
            Object obj = e.nextElement();
            if (obj instanceof TrainLine)
                ((TrainLine) obj).draw(g);
        }

        firstRect.drawInactive(g);
        lastRect.drawInactive(g);

        if (leftName != null)
            leftName.draw(g);
        if (rightName != null)
            rightName.draw(g);

        g.setColor(oldColor);
    }

    private void drawNormal(Graphics g) {
        Color oldColor = g.getColor();
        g.setColor(train.color);
        // System.out.println(train.getTrainName(chart.circuit) + color.toString());
        //Joe 2015-06-18 add
//System.out.println(train.color); 
// print the current train's linestytletype ( such as dash line, dot line, etc)
//System.out.println(train.linestytletype); 
        for (Enumeration<TrainLine> e = lines.elements(); e.hasMoreElements(); ) {
            Object obj = e.nextElement();
            if (obj instanceof TrainLine)
                ((TrainLine) obj).draw(g);
        }

        firstRect.drawInactive(g);
        lastRect.drawInactive(g);


        if (leftName != null)
            leftName.draw(g);
        if (rightName != null)
            rightName.draw(g);

        //设定黑色颜色
        g.setColor(new Color(0, 0, 0));
        //画出每个停站时刻信息框
        if (chartView.SHOWTRAIN_DetailTime) {
            for (Enumeration<TrainNameTB> e = namelr.elements(); e.hasMoreElements(); ) {
                Object obj = e.nextElement();
                if (obj instanceof TrainNameTB)
                    ((TrainNameTB) obj).draw(g);
            }
        }
        g.setColor(oldColor);
    }

    /**
     * drawActive
     *
     * @param g Graphics
     */
    private void drawActive(Graphics g) {
        Color oldColor = g.getColor();
        g.setColor(train.color);

        for (Enumeration<TrainLine> e = lines.elements(); e.hasMoreElements(); ) {
            Object obj = e.nextElement();
            if (obj instanceof TrainLine)
                ((TrainLine) obj).draw(g);
        }

        firstRect.drawActive(g);
        lastRect.drawActive(g);


        if (leftName != null)
            leftName.drawActive(g);
        if (rightName != null)
            rightName.drawActive(g);


//    if(selectedTrainLine != null) {
//      selectedTrainLine.drawSelected(g);
//    }

//    if(movingPoint != null) {
//      movingPoint.drawMoving(g);
//    }

        //System.out.println("DrawA"+i+++" lineNum:"+lines.size());


        //设定黑色颜色
        g.setColor(new Color(0, 0, 0));
        //画出每个停站时刻信息框
        if (chartView.SHOWTRAIN_DetailTime) {
            for (Enumeration<TrainNameTB> e = namelr.elements(); e.hasMoreElements(); ) {
                Object obj = e.nextElement();
                if (obj instanceof TrainNameTB)
                    ((TrainNameTB) obj).draw(g);
            }

        }


        g.setColor(oldColor);
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!(obj instanceof TrainDrawing))
            return false;

        TrainDrawing td = (TrainDrawing) obj;
        return td.getTrainName().equalsIgnoreCase(this.getTrainName());
    }

//  public boolean isSameTrain(Train train) {
//    if(train == null)
//      return false;
//
//    return train.equals(this.train);
//  }

//  public void setMovingPoint(String stationName, int pointType) {
//    for(Enumeration e = lines.elements(); e.hasMoreElements(); ) {
//      TrainLine line = (TrainLine)e.nextElement();
//      if(line.p1.getStationName().equalsIgnoreCase(stationName)
//         &&line.p1.type == pointType)
//        movingPoint = line.p1;
//      else if(line.p2.getStationName().equalsIgnoreCase(stationName)
//        &&line.p2.type == pointType)
//        movingPoint = line.p2;
//    }
//  }

//  public void setMovingPoint(int x, int y, int pointType, boolean isSchedular) {
//    this.movingPoint = new ChartPoint(x, y, pointType, isSchedular);
//  }

    /**
     * pointOnMe
     *
     * @param p Point
     * @return boolean
     */
    public boolean pointOnMe(Point p) {
        for (Enumeration<TrainLine> e = lines.elements(); e.hasMoreElements(); ) {
            TrainLine line = (TrainLine) e.nextElement();
            if (line.pointOnMe(p))
                return true;
        }

        return false;
    }

    public boolean pointOnMyRect(Point p) {
        if (firstRect == null || lastRect == null)
            return false;

        if (firstRect.pointOnMe(p) || lastRect.pointOnMe(p))
            return true;
        else
            return false;
    }

    public TrainLine findNearTrainLine(Point p) {
        for (Enumeration<TrainLine> e = lines.elements(); e.hasMoreElements(); ) {
            TrainLine line = (TrainLine) e.nextElement();
            if (line.pointOnMe(p))
                return line;
        }
        return null;
    }

    String getTrainName() {
        return train.getTrainName(chartView.mainFrame.chart.circuit);
    }

    /**
     * getBounds
     *
     * @return Rectangle
     */
    public Rectangle getPreferredBounds() {
        int left = Integer.MAX_VALUE;
        int top = Integer.MAX_VALUE;
        int right = Integer.MIN_VALUE;
        int bottom = Integer.MIN_VALUE;
        for (Enumeration<TrainLine> e = lines.elements(); e.hasMoreElements(); ) {
            TrainLine line = (TrainLine) e.nextElement();
            left = Math.min(Math.min(left, line.p1.x), line.p2.x);
            top = Math.min(Math.min(top, line.p1.y), line.p2.y);
            right = Math.max(Math.max(right, line.p1.x), line.p2.x);
            bottom = Math.max(Math.max(bottom, line.p1.y), line.p2.y);
        }
        left = Math.min(Math.min(left, firstRect.getBounds().x), lastRect.getBounds().x);
        top = Math.min(Math.min(top, firstRect.getBounds().y), lastRect.getBounds().y);
        right = Math.max(Math.max(right, firstRect.getBounds().x), lastRect.getBounds().x);
        bottom = Math.max(Math.max(left, firstRect.getBounds().y), lastRect.getBounds().y);
        return new Rectangle(left - 10, top - 10, right - left + 20, bottom - top + 20);
    }

    /**
     * getInfo
     */
    public String getInfo() {
        Stop stop[] = chartView.getDrawStops(train);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");

        long min = 0;
        try {
            Date in = df.parse(stop[0].arrive);
            Date out = df.parse(stop[stop.length - 1].leave);

            long time = out.getTime() - in.getTime();
            min = time / (1000 * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (min < 0)
            min += 24 * 60;

        Chart chart = chartView.mainFrame.chart;
        int dist = Math.abs(chart.circuit.getStationDist(stop[0].stationName)
                - chart.circuit.getStationDist(stop[stop.length - 1].stationName));

        if (min == 0)
            return train.getTrainName() + "Error!";

        return train.getTrainName() + "次 "
                + train.getStartStation() + "至" + train.getTerminalStation()
                + " 在" + chart.circuit.name + "行驶" + dist + "公里 "
                + "耗时" + min + "分钟 旅行速度" + dist * 60 / min + "公里/小时";
    }


}

