package org.paradise.etrc.view.chart.traindrawing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Line2D;

import org.paradise.etrc.view.chart.ChartView;

public class TrainLine {
    public static final int STOP_LINE = 0;
    public static final int RUN_LINE = 1;
    public int lineType;

    ChartPoint p1,p2;
    
    //是否图定
    boolean isSchedular = true;

    //所属的列车图线对象
    TrainDrawing trainDrawing;
    
    public TrainLine(TrainDrawing _trainDrawing, ChartPoint _p1, ChartPoint _p2, int _type, boolean _isSchedular) {
      trainDrawing = _trainDrawing;
      p1 = _p1;
      p2 = _p2;
      lineType = _type;
      isSchedular = _isSchedular;
    }
    
    public void draw(Graphics g) {
    	if(trainDrawing.isActive)
    		drawActive(g);
    	else
    		drawNormal(g);
    }

    private void drawNormal(Graphics g) {
    	Color oldColor = g.getColor();
    	
    	if(!isSchedular && !oldColor.equals(trainDrawing.chartView.underDrawingColor))
    		g.setColor(TrainDrawing.notSchedularColor);

    	g.drawLine(p1.x, p1.y, p2.x, p2.y);
    	
    	p1.draw(g);
    	p2.draw(g);
    	
    	g.setColor(oldColor);
    }

    private void drawActive(Graphics g) {
    	Color oldColor = g.getColor();
    	
    	if(!isSchedular)
    		g.setColor(TrainDrawing.notSchedularColor);

    	g.drawLine(p1.x, p1.y, p2.x, p2.y);
    	//画差1个像素的线加重
    	if(lineType == STOP_LINE)
    		g.drawLine(p1.x, p1.y+1, p2.x, p2.y+1);
    	else
    		g.drawLine(p1.x+1, p1.y, p2.x+1, p2.y);

    	p1.draw(g);
    	p2.draw(g);

    	g.setColor(oldColor);
    }

//    public void drawSelected(Graphics g) {
//    	Color oldColor = g.getColor();
//    	
//    	if(!isSchedular)
//    		g.setColor(notSchedularColor);
//
//    	g.drawLine(p1.x, p1.y, p2.x, p2.y);
//
//    	//以特殊颜色画差1个像素的线
//    	Color drawingColor = g.getColor();
//    	g.setColor(selectedColor);
//    	if(lineType == STOP_LINE) {
//    		g.drawLine(p1.x, p1.y - 1, p2.x, p2.y - 1);
//    		g.drawLine(p1.x, p1.y + 1, p2.x, p2.y + 1);
//    	}
//    	else {
//    		g.drawLine(p1.x - 1, p1.y, p2.x - 1, p2.y);
//    		g.drawLine(p1.x + 1, p1.y, p2.x + 1, p2.y);
//    	}
//    	g.setColor(drawingColor);
//
//    	p1.drawSelected(g);
//    	p2.drawSelected(g);
//    	
//    	g.setColor(oldColor);
//    }

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
    	ChartView chartView = trainDrawing.chartView;
      if(lineType == STOP_LINE) {
        int stopMinutes = Math.abs(chartView.getMinute(p2.x) - chartView.getMinute(p1.x));
        String stationName = chartView.getStationName(p1.y);
        return trainDrawing.getTrainName() + "次 "
            + stationName + "站 停车" + stopMinutes + "分钟 ("
            + chartView.getTime(p1.x) + "到 " + chartView.getTime(p2.x) + "发)";
      }
      else{
        String station1 = chartView.getStationName(p1.y);
        String station2 = chartView.getStationName(p2.y);
        int dist = Math.abs(chartView.getDist(p2.y) - chartView.getDist(p1.y));
        int runMinutes = chartView.getMinute(p2.x) - chartView.getMinute(p1.x);
        return trainDrawing.getTrainName() + "次 "
            +station1+"至"+station2
            +" 区间"+dist+"公里"
            +" 行车"+runMinutes+"分钟"
            +"("+ chartView.getTime(p1.x) + "发" + chartView.getTime(p2.x) + "到)"
            +" 平均时速"+dist*60/runMinutes+"公里";
      }
    }
  }
