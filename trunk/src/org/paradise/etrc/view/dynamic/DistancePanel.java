package org.paradise.etrc.view.dynamic;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
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

	public DistancePanel(DynamicView _dView) {
		dView = _dView;
		chart = _dView.mainFrame.chart;

		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		this.setFont(new Font("Dialog", 0, 10));
		this.setLayout(new BorderLayout());
		
		JPanel scalePanel = new JPanel();
		scalePanel.setLayout(new GridLayout(1,2));
		JButton jb1 = createScaleButton("+");
		JButton jb2 = createScaleButton("-");
		scalePanel.setPreferredSize(new Dimension(24, 16));
		scalePanel.add(jb1);
		scalePanel.add(jb2);
		add(scalePanel, BorderLayout.WEST);
		
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				dView.changeDistUpDownState();
			}
		});
	}

	private JButton createScaleButton(String func) {
		ImageIcon im;
		if(func.equals("+"))
			im = new ImageIcon(this.getClass().getResource("/pic/add.gif"));
		else
			im = new ImageIcon(this.getClass().getResource("/pic/sub.gif"));
		
		JButton jb = new JButton(im);
		
		jb.setFocusPainted(false);
		jb.setBorderPainted(false);
		
		jb.setActionCommand(func);
		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(ae.getActionCommand().equals("+")) {
					dView.increaseScale(1);
				}
				else {
					dView.increaseScale(-1);
				}
			}
		});
		
		return jb;
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		if (dView.showUpDownDistState == DynamicView.SHOW_DOWN_DIST) {
			drawGridDown(g);
		}
		else {
			drawGridUp(g);
		}
	}
	
	private void drawGridUp(Graphics g) {
		for (int dist = chart.circuit.length; dist >= 0 ; dist--) {
			drawDistanceGrid(g, dist);
		}
		
		int x = dView.getPelsX(0);
		drawDistanceString(g, chart.circuit.length, x);
	}

	private void drawGridDown(Graphics g) {
		for (int dist = 0; dist <= chart.circuit.length; dist++) {
			drawDistanceGrid(g, dist);
		}
		
		int x = dView.getPelsX(chart.circuit.length);
		drawDistanceString(g, chart.circuit.length, x);
	}
	
	private void drawDistanceGrid(Graphics g, int dist) {
		int drawingDist;
		
		//下行里程数
		if (dView.showUpDownDistState == DynamicView.SHOW_DOWN_DIST) {
			drawingDist = dist;
		}
		//上行里程数
		else {
			drawingDist = (chart.circuit.length - dist);
		}

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

		//下行里程数
		if (dView.showUpDownDistState == DynamicView.SHOW_DOWN_DIST) {
			drawColor = dView.downDistColor;
		}
		//上行里程数
		else {
			drawColor = dView.upDistColor;
		}
		
		//尾数为1,2,3,4的公里数不画
		//主要用于全长的公里数，以免与整数刻度重叠
		if(drawingDist % 10 > 0 && drawingDist % 10 < 5)
			return;
		
		g.setColor(drawColor);
		g.drawString(drawingDist == 0 ? drawingDist+"km" : drawingDist+"", x + 2, 9);
		g.setColor(oldColor);
	}

	public Dimension getPreferredSize() {
		int w, h;
		w = chart.circuit.length * dView.scale + dView.leftMargin + dView.rightMargin;
		h = dView.ditancePanelHeight;
		return new Dimension(w, h);
	}
}
