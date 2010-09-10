package org.paradise.etrc.view.chart.traindrawing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import static org.paradise.etrc.ETRC._;
import org.paradise.etrc.data.Train;
import org.paradise.etrc.view.chart.ChartView;

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
    boolean pointOnMe(Point p) {
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
         &&(p.y > anchor.y - ChartView.trainNameRecHeight - 10)
         &&(p.y < anchor.y))
        return true;
      else
        return false;
    }

    private boolean pointOnMeBottom(Point p) {
      if((p.x > anchor.x - 5)
         &&(p.x < anchor.x + 5)
         &&(p.y < anchor.y + ChartView.trainNameRecHeight + 10)
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
          top  = anchor.y - ChartView.trainNameRecMargin
                 - 10 - ChartView.trainNameRecHeight;
          break;
        case Train.UP_TRAIN:
          top = anchor.y + ChartView.trainNameRecMargin + 10;
          break;
      }
      rec.x = left;
      rec.y = top;
      rec.width = 10;
      rec.height = ChartView.trainNameRecMargin + ChartView.trainNameRecMargin + 10;
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

      int y1 = y - ChartView.trainNameRecMargin;
      int px[] = {x, x-5, x+5};
      int py[] = {y1, y1-10, y1-10};
      g.fillPolygon(px, py, 3);
      //g.drawLine(x, y, x, y1);
      drawNameRec(g, x-5, y1-10-ChartView.trainNameRecHeight, isActive);
    }

    //上行入图
    private void drawInRectUp(Graphics g, boolean isActive) {
      int x = anchor.x;
      int y = anchor.y;

      int y1 = y + ChartView.trainNameRecMargin;
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

      int y1 = y + ChartView.trainNameRecMargin;
      int y2 = y1 + ChartView.trainNameRecHeight;
      int px[] = {x, x - 5, x + 5};
      int py[] = {y2 + 10, y2, y2};
      g.fillPolygon(px, py, 3);
      drawNameRec(g, x-5, y1, isActive);
    }

    //上行出图
    private void drawOutRectUp(Graphics g, boolean isActive) {
      int x = anchor.x;
      int y = anchor.y;

      int y1 = y - ChartView.trainNameRecHeight - ChartView.trainNameRecMargin;
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

      int y1 = y - ChartView.trainNameRecMargin;

//    g.fillRect(x-5, y1-mainView.trainNameRecHeight-5, 11, 4);
      drawTailRecDown(g, x-5, y1-ChartView.trainNameRecHeight-5);
      
      drawNameRec(g, x-5, y1-ChartView.trainNameRecHeight, isActive);
    }

    //上行始发
    private void drawStartRectUp(Graphics g, boolean isActive) {
      int x = anchor.x;
      int y = anchor.y;

      int y1 = y + ChartView.trainNameRecMargin;

//    g.fillRect(x-5, y1+mainView.trainNameRecHeight+2, 11, 4);
      drawTailRecUp(g, x-5, y1+ChartView.trainNameRecHeight+2);

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

      int y1 = y + ChartView.trainNameRecMargin;

//    g.fillRect(x-5, y1+mainView.trainNameRecHeight+2, 11, 4);
      drawTailRecDown(g, x-5, y1+ChartView.trainNameRecHeight+2);

      drawNameRec(g, x-5, y1, isActive);
    }

    //上行终到
    private void drawTerminalRectUp(Graphics g, boolean isActive) {
      int x = anchor.x;
      int y = anchor.y;

      int y1 = y - ChartView.trainNameRecMargin;

//    g.fillRect(x-5, y1-mainView.trainNameRecHeight-5, 11, 4);
      drawTailRecUp(g, x-5, y1-ChartView.trainNameRecHeight-5);

      drawNameRec(g, x-5, y1-ChartView.trainNameRecHeight, isActive);
    }
    
    //画下行始发终到车的尾框
    private void drawTailRecDown(Graphics g, int x, int y) {
        g.fillRect(x, y, 11, 4);
        
        Color drawingColor = g.getColor();
        g.setColor(Color.white);
        
        g.drawLine(x+1, y, x+5, y+4);
        g.drawLine(x+9, y, x+5, y+4);
        
        g.setColor(drawingColor);
    }
    
    //画上行始发终到车的尾框
    private void drawTailRecUp(Graphics g, int x, int y) {
        g.fillRect(x, y, 11, 4);
        
        Color drawingColor = g.getColor();
        g.setColor(Color.white);
        
        g.drawLine(x+1, y+4, x+5, y);
        g.drawLine(x+9, y+4, x+5, y);
        
        g.setColor(drawingColor);
    }

    //画车次框
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
      g.fillRect(x, y, 10, ChartView.trainNameRecHeight);
      g.setColor(oldColor);
      g.setFont(new Font(_("FONT_NAME_FIXED"), 0, 10));
      g.drawRect(x, y, 10, ChartView.trainNameRecHeight);

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
      g.fillRect(x, y, 11, ChartView.trainNameRecHeight+1);
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
