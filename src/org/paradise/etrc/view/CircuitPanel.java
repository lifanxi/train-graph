package org.paradise.etrc.view;

import java.awt.*;
import javax.swing.JPanel;
import java.awt.event.*;

import org.paradise.etrc.data.*;

/**
 * @author lguo@sina.com
 * @version 1.0
 */

public class CircuitPanel extends JPanel {
	private static final long serialVersionUID = -4260259395001588542L;

	BorderLayout borderLayout1 = new BorderLayout();

	int myLeftMargin = 5;

	private Chart chart;
	private MainView mainView;

	public CircuitPanel(Chart _chart, MainView _mainView) {
		chart = _chart;
		mainView = _mainView;

		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		this.setFont(new java.awt.Font("宋体", 0, 12));
		this.setLayout(borderLayout1);
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getPoint().x > mainView.circuitPanelWidth - 25)
					mainView.changeDistUpDownState();
			}
		});
	}

	public void paint(Graphics g) {
		super.paint(g);

		if (chart == null)
			return;

		if (chart.circuit != null)
			for (int i = 0; i < chart.circuit.stationNum; i++) {
				DrawStation(g, chart.circuit.stations[i]);
			}
	}

	public Dimension getPreferredSize() {
		if (chart == null)
			return new Dimension(640, 480);

		if (chart.circuit == null)
			return new Dimension(mainView.circuitPanelWidth, 480);

		int w = mainView.circuitPanelWidth;
		int h = chart.circuit.length * chart.distScale + mainView.topMargin
				+ mainView.bottomMargin;
		return new Dimension(w, h);
	}

	public void DrawStation(Graphics g, Station station) {
		if (station.hide)
			return;

		int y = station.dist * chart.distScale + mainView.topMargin;

		if (station.level <= chart.displayLevel) {
			//设置坐标线颜色
			Color oldColor = g.getColor();
			g.setColor(mainView.gridColor);

			//画坐标线
			g.drawLine(myLeftMargin, y, mainView.circuitPanelWidth, y);
			if (station.level <= chart.boldLevel) {
				g.drawLine(myLeftMargin, y + 1, mainView.circuitPanelWidth,
						y + 1);
			}

			//恢复原色
			g.setColor(oldColor);

			//画站名与里程
			DrawName(g, station, y);
		}
	}

	private void DrawName(Graphics g, Station station, int y) {
		//站名
		g.drawString(station.name, myLeftMargin + 2, y - 2);

		Color oldColor = g.getColor();
		String stDist;
		//下行里程
		if (mainView.distUpDownState == MainView.SHOW_DOWN) {
			stDist = "" + station.dist;
			g.setColor(mainView.downDistColor);
		}
		//上行里程
		else {
			stDist = "" + (chart.circuit.length - station.dist);
			g.setColor(mainView.upDistColor);
		}
		Font oldFont = g.getFont();
		g.setFont(new Font("Dialog", 0, 10));
		int x = mainView.circuitPanelWidth
				- g.getFontMetrics().stringWidth(stDist);
		g.drawString(stDist, x, y - 2);
		g.setFont(oldFont);

		g.setColor(oldColor);
	}

}
