package org.paradise.etrc.view.chart;

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

public class ClockPanel extends JPanel {
	private static final long serialVersionUID = 2449059376608773861L;

	private Chart chart;
	private ChartView chartView;

	public ClockPanel(Chart _chart, ChartView _view) {
		chart = _chart;
		chartView = _view;
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
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
				if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					int x = e.getPoint().x;
					
					double h = chart.startHour + ((x - chartView.leftMargin)/chart.minuteScale)/60.0;
	
					if(Math.abs(h - Math.round(h)) < 16.0/chart.minuteScale/60.0) {
						int theHour = (int) (Math.round(h) >= 24 ? Math.round(h) - 24 : Math.round(h));
						chart.startHour = theHour;
						chartView.scrollToLeft();
						chartView.repaint();
					}
				}
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
					increaseMinuteGap(1);
				}
				else {
					increaseMinuteGap(-1);
				}
			}
		});
		
		return jb;
	}
	
	private int[] minuteGrids = {20,10,10,10,10,5,5,5,5,5};
	private void increaseMinuteGap(int i) {
		chartView.mainFrame.chart.minuteScale += i;
		
		if(chartView.mainFrame.chart.minuteScale > 10) {
			chartView.mainFrame.chart.minuteScale = 10;
			return;
		}
		
		if(chartView.mainFrame.chart.minuteScale < 1) {
			chartView.mainFrame.chart.minuteScale = 1;
			return;
		}
		
		chartView.mainFrame.chart.timeInterval = minuteGrids[chartView.mainFrame.chart.minuteScale-1];
		chartView.resetSize();
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
		w = 60 * 24 * chart.minuteScale + chartView.leftMargin + chartView.rightMargin;
		h = ChartView.clockPanelHeight;
		return new Dimension(w, h);
	}

	/**
	 * DrawHour
	 *
	 * @param g Graphics
	 */
	private void DrawHour(Graphics g, int clock) {
		int coordinate = getCoordinate(clock);

		int startPos = coordinate * 60 * chart.minuteScale + chartView.leftMargin - 12;
		String stClock = clock + ":00";
		g.drawString(stClock, startPos, ChartView.clockPanelHeight - 2);
	}

	private void DrawEndHour(Graphics g, int clock) {
		int start = 24 * 60 * chart.minuteScale + chartView.leftMargin - 12;
		String stClock = clock + ":00";
		g.drawString(stClock, start, ChartView.clockPanelHeight - 2);
	}

	private int getCoordinate(int clock) {
		int drawClock = clock - chart.startHour;
		if (drawClock < 0)
			drawClock += 24;

		return drawClock;
	}
}
