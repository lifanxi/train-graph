package org.paradise.etrc.view.chart.traindrawing;

import java.awt.Graphics;

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
