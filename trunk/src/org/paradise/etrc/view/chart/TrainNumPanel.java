package org.paradise.etrc.view.chart;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class TrainNumPanel extends JPanel {
	private static final long serialVersionUID = -5916279280631048174L;

	private JLabel trainNum = new JLabel("(0/0)");
	private ChartView chartView;
	
	public TrainNumPanel(ChartView _view) {
		chartView = _view;
		
		this.setLayout(new BorderLayout());
		trainNum.setHorizontalAlignment(SwingConstants.CENTER);
		trainNum.setVerticalAlignment(SwingConstants.CENTER);
		add(trainNum, BorderLayout.CENTER);
		

		JButton jb1 = createScaleButton("+");
		JButton jb2 = createScaleButton("-");
		add(jb1, BorderLayout.WEST);
		add(jb2, BorderLayout.EAST);
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
					increaseDistGap(1);
				}
				else {
					increaseDistGap(-1);
				}
			}
		});
		
		jb.setPreferredSize(new Dimension(12, 16));
		
		return jb;
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

	public void setText(String str) {
		trainNum.setText(str);
	}

}
