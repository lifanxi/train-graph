package org.paradise.etrc.dview;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import org.paradise.etrc.data.*;

/**
 * @author lguo@sina.com
 * @version 1.0
 */

public class DistancePanel extends JPanel {
	private static final long serialVersionUID = 2449059376608773861L;

	private Chart chart;
	private DynamicView dView;

	public DistancePanel(Chart _chart, DynamicView _dView) {
		chart = _chart;
		dView = _dView;
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		this.setFont(new Font("Dialog", 0, 10));
		this.setLayout(new BorderLayout());
		
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				dView.changeDistUpDownState();
			}
		});
	}

	public void paint(Graphics g) {
		super.paint(g);
		
		if (dView.distUpDownState == DynamicView.SHOW_DOWN) {
			drawGridDown(g);
		}
		else {
			drawGridUp(g);
		}
	}
	
	private void drawGridUp(Graphics g) {
		for (int dist = chart.circuit.length; dist >= 0 ; dist--) {
			drawDistanceGrid(g, dist);
//			int x = dView.getPelsX(dist);
//			int y1 = this.getHeight() - 5;
//			int y2 = this.getHeight();
//			
//			if((chart.circuit.length - dist) % 10 == 0) {
//				y1 = 0;
//				drawDistanceString(g, dist, x);
//			}
//			else if((chart.circuit.length - dist) % 5 == 0) {
//				y1 = this.getHeight() - 8;
//			}
//			
//			g.drawLine(x, y1, x, y2);
		}
		
		int x = dView.getPelsX(0);
//		int y1 = 0;
//		int y2 = this.getHeight();
		drawDistanceString(g, chart.circuit.length, x);
//		g.drawLine(x, y1, x, y2);		
	}

	private void drawGridDown(Graphics g) {
		for (int dist = 0; dist <= chart.circuit.length; dist++) {
			drawDistanceGrid(g, dist);
		}
		
		int x = dView.getPelsX(chart.circuit.length);
//		int y1 = 0;
//		int y2 = this.getHeight();
		drawDistanceString(g, chart.circuit.length, x);
//		g.drawLine(x, y1, x, y2);		
	}
	
	private void drawDistanceGrid(Graphics g, int dist) {
		int drawingDist;
		
		//下行里程数
		if (dView.distUpDownState == DynamicView.SHOW_DOWN) {
			drawingDist = dist;
		}
		//上行里程数
		else {
			drawingDist = (chart.circuit.length - dist);
		}

//		System.out.println(dist+":"+drawingDist);

		int x = dView.getPelsX(dist);
		int y1 = this.getHeight() - 5;
		int y2 = this.getHeight();
		
		if(drawingDist % 10 == 0) {
			y1 = 0;
			drawDistanceString(g, drawingDist, x);
		}
		else if(drawingDist % 5 == 0) {
			y1 = this.getHeight() - 8;
		}
		
		g.drawLine(x, y1, x, y2);
	}

	private void drawDistanceString(Graphics g, int drawingDist, int x) {
		Color oldColor = g.getColor();
		Color drawColor;
//		int drawingDist;

		//下行里程数
		if (dView.distUpDownState == DynamicView.SHOW_DOWN) {
//			drawingDist = dist;
			drawColor = dView.downDistColor;
		}
		//上行里程数
		else {
//			drawingDist = (chart.circuit.length - dist);
			drawColor = dView.upDistColor;
		}
		
		//尾数为1,2,3,4的公里数不画
		//主要用于全长的公里数，以免与整数刻度重叠
		if(drawingDist % 10 > 0 && drawingDist % 10 < 5)
			return;
		
		g.setColor(drawColor);
		g.drawString(drawingDist+"", x + 2, 9);
		g.setColor(oldColor);
	}

	public Dimension getPreferredSize() {
		int w, h;
		w = chart.circuit.length * dView.scale + dView.leftMargin + dView.rightMargin;
		h = dView.ditancePanelHeight;
		return new Dimension(w, h);
	}
}
