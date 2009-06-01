package org.paradise.etrc.view;

import java.awt.*;
import javax.swing.JPanel;

import org.paradise.etrc.data.*;

/**
 * @author lguo@sina.com
 * @version 1.0
 */

public class ClockPanel extends JPanel {
	private static final long serialVersionUID = 2449059376608773861L;

	BorderLayout borderLayout1 = new BorderLayout();

	private Chart chart;
	private MainView mainView;

	public ClockPanel(Chart _chart, MainView _mainView) {
		chart = _chart;
		mainView = _mainView;
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		this.setLayout(borderLayout1);
	}

	public void paint(Graphics g) {
		super.paint(g);

		int h = chart.startHour;
		for (int i = 0; i < 24; i++) {
			if (h >= 24)
				h -= 24;

			//System.out.println("Clock: " + h);
			DrawHour(g, h);
			h++;
		}

		if (chart.startHour == 0)
			DrawEndHour(g, 24);
		else
			DrawEndHour(g, chart.startHour);
	}

	public Dimension getPreferredSize() {
		int w, h;
		w = 60 * 24 * chart.minuteScale + mainView.leftMargin + mainView.rightMargin;
		h = mainView.clockPanelHeight;
		return new Dimension(w, h);
	}

	/**
	 * DrawHour
	 *
	 * @param g Graphics
	 */
	private void DrawHour(Graphics g, int clock) {
		int coordinate = getCoordinate(clock);

		int startPos = coordinate * 60 * chart.minuteScale + mainView.leftMargin
				- 12;
		String stClock = clock + ":00";
		g.drawString(stClock, startPos, mainView.clockPanelHeight - 2);
	}

	private void DrawEndHour(Graphics g, int clock) {
		int start = 24 * 60 * chart.minuteScale + mainView.leftMargin - 12;
		String stClock = clock + ":00";
		g.drawString(stClock, start, mainView.clockPanelHeight - 2);
	}

	private int getCoordinate(int clock) {
		int drawClock = clock - chart.startHour;
		if (drawClock < 0)
			drawClock += 24;

		return drawClock;
	}
}
