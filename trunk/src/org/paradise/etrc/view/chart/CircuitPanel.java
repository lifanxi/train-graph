package org.paradise.etrc.view.chart;

import java.awt.*;

import javax.swing.JPanel;
import java.awt.event.*;

import static org.paradise.etrc.ETRC._;
import org.paradise.etrc.data.*;
import org.paradise.etrc.slice.ChartSlice;

/**
 * @author lguo@sina.com
 * @version 1.0
 */

public class CircuitPanel extends JPanel {
	private static final long serialVersionUID = -4260259395001588542L;

	BorderLayout borderLayout1 = new BorderLayout();

	int myLeftMargin = 5;

	private Chart chart;
	private ChartView chartView;

	public CircuitPanel(Chart _chart, ChartView _mainView) {
		chart = _chart;
		chartView = _mainView;

		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		this.setFont(new java.awt.Font(_("FONT_NAME"), 0, 12));
		this.setLayout(borderLayout1);
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getPoint().x > ChartView.circuitPanelWidth - 25)
					chartView.changeDistUpDownState();
				else {
					chartView.setActiveSation(e.getPoint().y + 12);
					if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() >= 2)
						if(chartView.activeStation != null)
							new ChartSlice(chartView.mainFrame.chart).makeStationSlice(chartView.activeStation);
				}
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
			return new Dimension(ChartView.circuitPanelWidth, 480);

		int w = ChartView.circuitPanelWidth;
		int h = chart.circuit.length * chart.distScale + chartView.topMargin
				+ chartView.bottomMargin;
		return new Dimension(w, h);
	}

	public void DrawStation(Graphics g, Station station) {
		if (station.hide)
			return;

		int y = station.dist * chart.distScale + chartView.topMargin;

		if (station.level <= chart.displayLevel) {
			//设置坐标线颜色
			Color oldColor = g.getColor();
			
			if(station.equals(chartView.activeStation))
				g.setColor(chartView.activeGridColor);
			else
				g.setColor(chartView.gridColor);

			//画坐标线
			g.drawLine(myLeftMargin, y, ChartView.circuitPanelWidth, y);
			if (station.level <= chart.boldLevel) {
				g.drawLine(myLeftMargin, y + 1, ChartView.circuitPanelWidth,
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
		if (chartView.distUpDownState == ChartView.SHOW_DOWN) {
			stDist = station.dist == 0 ? "0km" : "" + station.dist;
			g.setColor(chartView.downDistColor);
		}
		//上行里程
		else {
			stDist = station.dist == chart.circuit.length ? "0km" : "" + (chart.circuit.length - station.dist);
			g.setColor(chartView.upDistColor);
		}
		Font oldFont = g.getFont();
		g.setFont(new Font("Dialog", 0, 10));
		int x = ChartView.circuitPanelWidth
				- g.getFontMetrics().stringWidth(stDist);
		g.drawString(stDist, x, y - 2);
		g.setFont(oldFont);

		g.setColor(oldColor);
	}
}
