package org.paradise.etrc.view.chart.traindrawing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.RenderingHints;

import org.paradise.etrc.view.chart.ChartView;

public class TrainLine {
    public static final int STOP_LINE = 0;
    public static final int RUN_LINE = 1;
    public int lineType;

    ChartPoint p1,p2;
    
    //是否图定
    boolean isSchedular = true;
	
	boolean isMainTrack = true;

    //所属的列车图线对象
    TrainDrawing trainDrawing;
    
    public TrainLine(TrainDrawing _trainDrawing, ChartPoint _p1, ChartPoint _p2, int _type, boolean _isSchedular, boolean _isMainTrack) {
      trainDrawing = _trainDrawing;
      p1 = _p1;
      p2 = _p2;
      lineType = _type;
      isSchedular = _isSchedular;
	  isMainTrack = _isMainTrack;
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

	    //根据全局设置来设定线宽	
		//float lineWidth2 = trainDrawing.chartView.lineWidth;
		float lineWidth2 ;		
		// train的linewidth为0表明当前为新建图，则取全局默认linewidth设置（prop文件）图，同时并反标该train的linewidth以保存
		if (trainDrawing.train.linewidth == 0 ) 
		{
		lineWidth2 = trainDrawing.chartView.lineWidth;
		trainDrawing.train.linewidth = lineWidth2;
		} else
		// train的linewidth不为0表明当前为导入图，则取etrc文件中定义的train linewidth
		{
		lineWidth2 = trainDrawing.train.linewidth ;			
		}
		//System.out.println(trainDrawing.train.linewidth);
		((Graphics2D)g).setStroke(new BasicStroke(lineWidth2));
		
		
		    	//joe 2015-06-18
		//System.out.println(trainDrawing.train.color);
		//BasicStroke(float width, int cap, int join, float miterlimit, float[] dash, float dash_phase) 
        //   构造一个具有指定属性的新 BasicStroke。
		//http://www.cjsdn.net/Doc/JDK50/java/awt/BasicStroke.html   http://momsbaby1986.iteye.com/blog/1462901
		//System.out.println(trainDrawing.train.linestytletype);
    			// 默认dashline为false，右键单击change to dash line时该值为true
				//如果etrc文件中设定图线线型为1，则为虚线，2则为点划线，0为实线
				//或者如果当前运行线涉及到非本线正线车站（level为-1），则设为虚线
				if (trainDrawing.train.linestytletype == 1 || (!isMainTrack && trainDrawing.chartView.DASHSIDING))
			   {
			   ((Graphics2D)g).setStroke(new BasicStroke(lineWidth2,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1f,new float[]{15,10,},0f));
			
			   } else if (trainDrawing.train.linestytletype == 2)
			   {
			   ((Graphics2D)g).setStroke(new BasicStroke(lineWidth2,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1f,new float[]{20,9,7,9},0f));
			
			   }  else
			   {
			   ((Graphics2D)g).setStroke(new BasicStroke(lineWidth2));
			  
			   }
	
	
	
	
	//((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
	((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF); 
	//((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
	//((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
	((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	((Graphics2D)g).drawLine(p1.x, p1.y, p2.x, p2.y);		
    	//g.drawLine(p1.x, p1.y, p2.x, p2.y);  	
    	p1.draw(g);
    	p2.draw(g);
    	
    	g.setColor(oldColor);
		//恢复线宽为1.0f,以免影响文本标注框线宽（默认为1.0f)
		((Graphics2D)g).setStroke(new BasicStroke(1.0f));
    }

    private void drawActive(Graphics g) {
    	Color oldColor = g.getColor();
    	//joe 2015-06-18
		//System.out.println(trainDrawing.train.color);
		//System.out.println(trainDrawing.train.linestytletype);
    	if(!isSchedular)
    		g.setColor(TrainDrawing.notSchedularColor);
			
//取线宽为全局设置
//		float lineWidth2 = trainDrawing.chartView.lineWidth;
float lineWidth2 ;		
		// train的linewidth为0表明当前为新建图，则取全局默认linewidth设置（prop文件）图,同时并反标该train的linewidth以保存
		if (trainDrawing.train.linewidth == 0 ) 
		{
lineWidth2 = trainDrawing.chartView.lineWidth;

trainDrawing.train.linewidth = lineWidth2;
		} else
		// train的linewidth不为0表明当前为导入图，则取etrc文件中定义的train linewidth
		{
lineWidth2 = trainDrawing.train.linewidth ;			
		}
//System.out.println(trainDrawing.train.linewidth);
                // 默认dashline为false，右键单击change to dash line时该值为true
				//if (trainDrawing.chartView.dashline)
			   //{
				   //下面一句自动将当前选中图线变为虚线， 2015-06-10禁用
			   //((Graphics2D)g).setStroke(new BasicStroke(lineWidth2,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1f,new float[]{15,10,},0f));
			   //}
			   //else
			   //{
			   //((Graphics2D)g).setStroke(new BasicStroke(lineWidth2));
			   //}
			   
			   if (trainDrawing.train.linestytletype == 1)
			   {
			   ((Graphics2D)g).setStroke(new BasicStroke(lineWidth2,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1f,new float[]{15,10,},0f));
			
			   } else if (trainDrawing.train.linestytletype == 2)
			   {
			   ((Graphics2D)g).setStroke(new BasicStroke(lineWidth2,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1f,new float[]{20,9,7,9},0f));
			
			   }  else
			   {
			   ((Graphics2D)g).setStroke(new BasicStroke(lineWidth2));
			  
			   }  
			  

			   g.drawLine(p1.x, p1.y, p2.x, p2.y);
    	//画差1个像素的线加重
    	if(lineType == STOP_LINE)
    		((Graphics2D)g).drawLine(p1.x, p1.y+1, p2.x, p2.y+1);
    	else
    		((Graphics2D)g).drawLine(p1.x+1, p1.y, p2.x+1, p2.y);

    	p1.draw(g);
    	p2.draw(g);

    	g.setColor(oldColor);
		//恢复线宽为1.0f,以免影响文本标注框线宽（默认为1.0f)
		((Graphics2D)g).setStroke(new BasicStroke(1.0f));
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
