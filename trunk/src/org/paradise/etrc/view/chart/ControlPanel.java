package org.paradise.etrc.view.chart;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.paradise.etrc.dialog.ChartSetDialog;

/**
 * @author lguo@sina.com
 * @version 1.0
 */

public class ControlPanel extends JPanel {
	private static final long serialVersionUID = 2449059376608773861L;

	private ChartView chartView;

	public ControlPanel(ChartView _chartView) {
		chartView = _chartView;
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		this.setLayout(new GridLayout(1, 5));
		
		setBorder(BorderFactory.createCompoundBorder(
       				BorderFactory.createLineBorder(Color.lightGray), 
					BorderFactory.createEmptyBorder(2, 0, 0, 0)));
		
		//横加
		JButton btdl = createButton("hAdd");
		btdl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				increaseMinuteGap(1);
			}
		});
		
		//竖加
		JButton btsl = createButton("vAdd");
		btsl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				increaseDistGap(1);
			}
		});
		
		//竖减
		JButton btsr = createButton("vDel");
		btsr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				increaseDistGap(-1);
			}
		});
		
		//横减
		JButton btdr = createButton("hDel");
		btdr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				increaseMinuteGap(-1);
			}
		});

		//设置
		JButton btmd = createButton("hvDefault");
		btmd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setup();
			}
		});
		
		add(btdl);
		add(btsl);
		add(btmd);
		add(btsr);
		add(btdr);
	}
	
	public JButton createButton(String pic) {
		ImageIcon im = new ImageIcon(this.getClass().getResource("/pic/" + pic + ".png"));
		JButton jb = new JButton(im);
		
		jb.setFocusPainted(false);
		jb.setBorderPainted(false);
		
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

	private void increaseDistGap(int i) {
		chartView.mainFrame.chart.distScale += i;
		
		if(chartView.mainFrame.chart.distScale > 10) {
			chartView.mainFrame.chart.distScale = 10;
			return;
		}
		
		if(chartView.mainFrame.chart.distScale < 1) {
			chartView.mainFrame.chart.distScale = 1;
			return;
		}

		chartView.resetSize();
	}
	
	private void setup() {
		ChartSetDialog dlg = new ChartSetDialog(chartView.mainFrame);
		dlg.editSettings();
	}

	public Dimension getPreferredSize() {
		int w, h;
		w = ChartView.circuitPanelWidth;
		h = ChartView.clockPanelHeight;
		return new Dimension(w, h);
	}
}
