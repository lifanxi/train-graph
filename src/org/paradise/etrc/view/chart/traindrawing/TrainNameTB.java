package org.paradise.etrc.view.chart.traindrawing;

import java.awt.Graphics;

public class TrainNameTB {
    private ChartPoint anchor;
    private String trainName;
    private boolean onTop;
	private boolean onLeft; 

    public TrainNameTB(ChartPoint _p, String _name, boolean _top, boolean _left) {
      anchor = _p;
      trainName = _name;
      onTop = _top;
	  onLeft = _left;
    }

    public void draw(Graphics g) {
      int x = anchor.x;
      int y = anchor.y;
      if(onTop)
        //x -= g.getFontMetrics().stringWidth(trainName)+1;
	     y -= 5;
      else
        //x += 3;
	     y += 15;
	  if (onLeft)
	 {
		 //x -= 2; 
		 x -= 5;
	 }
	 else 
	 {
		//x += 2; 
		x += 1;
	 }	 
		 
      g.drawString(trainName, x, y);
    }

    public void drawActive(Graphics g) {
      int x = anchor.x;
      int y = anchor.y;

      if(onTop)
        //x -= g.getFontMetrics().stringWidth(trainName)+1;
	    y -= 5;
      else
        //x += 3;
        y += 15;
		//下行列车
	 if (onLeft)
	 {
		 x -= 2; 
	 }
	 else 
	 {
		x += 2; 
	 }

		 
		
		
		
      g.drawString(trainName, x, y);
     // if(onTop)
     //   g.drawLine(x-1, y+1, anchor.x, anchor.y+1);
     // else
     //   g.drawLine(anchor.x, anchor.y+1, x+g.getFontMetrics().stringWidth(trainName), y+1);
    }
  }
