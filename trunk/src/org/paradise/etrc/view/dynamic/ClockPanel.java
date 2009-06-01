package org.paradise.etrc.view.dynamic;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * @author lguo@sina.com
 * @version 1.0
 */

public class ClockPanel extends JPanel {
	private static final long serialVersionUID = 2449059376608773861L;

	private DynamicView dView;

	public ClockPanel(DynamicView _dView) {
		dView = _dView;
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		this.setLayout(new BorderLayout());

		JPanel hPanel1 = createHourPanel(0);
		JPanel hPanel2 = createHourPanel(12);
		
		JPanel cPanel = createClockPanel();
		
		add(hPanel1, BorderLayout.NORTH);
		add(hPanel2, BorderLayout.SOUTH);

		add(cPanel, BorderLayout.CENTER);
	}
	
	private JPanel createHourPanel(int startHour) {
		JPanel hPanel = new JPanel();
		GridLayout layout = new GridLayout(3,4);
		layout.setHgap(1);
		layout.setVgap(1);
		hPanel.setLayout(layout);
		hPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

		for(int i=0; i<12; i++) {
			hPanel.add(createHourButton(startHour + i));
		}
		
		return hPanel;
	}
	
	private JPanel createClockPanel() {
		JPanel cPanel = new JPanel() {
			private static final long serialVersionUID = 820684485438672893L;
			public void paint(Graphics g) {
				int time = dView.getCurrentTime();
				
				int h1 = time / 60 / 10;
				int h2 = time / 60 % 10;
				
				int m1 = time % 60 / 10;
				int m2 = time % 60 % 10;
				
				ImageIcon imH1 = new ImageIcon(this.getClass().getResource("/pic/" + h1 + ".gif"));
				ImageIcon imH2 = new ImageIcon(this.getClass().getResource("/pic/" + h2 + ".gif"));
				ImageIcon imM1 = new ImageIcon(this.getClass().getResource("/pic/" + m1 + ".gif"));
				ImageIcon imM2 = new ImageIcon(this.getClass().getResource("/pic/" + m2 + ".gif"));
				
				ImageIcon imMH;
				if(time % 2 == 0)
					imMH= new ImageIcon(this.getClass().getResource("/pic/MaoHao0.gif"));
				else
					imMH= new ImageIcon(this.getClass().getResource("/pic/MaoHao1.gif"));
				
				int xh1 = 2;
				int xh2 = xh1 + 16;
				int xmh = xh2 + 16;
				int xm1 = xmh + 10;
				int xm2 = xm1 + 16;
				int y = getHeight() / 2 - 10;
				
				g.drawImage(imH1.getImage(), xh1, y, null);
				g.drawImage(imH2.getImage(), xh2, y, null);
				g.drawImage(imMH.getImage(), xmh, y, null);
				g.drawImage(imM1.getImage(), xm1, y, null);
				g.drawImage(imM2.getImage(), xm2, y, null);
			}
		};
		
		return cPanel;
	}
	
	private JButton createHourButton(int hour) {
		JButton jb = new JButton(hour < 10 ? "0" + hour : "" + hour);
		jb.setFont(new Font("Dialog", Font.PLAIN, 9));
		jb.setActionCommand("" + hour);
		
		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int hour = Integer.parseInt(ae.getActionCommand());
				dView.setCurrentTime(hour * 60);
			}
		});
		
		jb.setFocusPainted(false);
		jb.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		jb.setForeground(Color.gray);
		jb.setHorizontalAlignment(SwingConstants.CENTER);
		jb.setVerticalAlignment(SwingConstants.CENTER);
		jb.setMargin(new Insets(0, 0, 0, 0));
		
		jb.setPreferredSize(new Dimension(16, 16));
		
		return jb;
	}

	public Dimension getPreferredSize() {
		int w, h;
		w = dView.clockPanelWidth;
		h = dView.runningPanelHeight;
		return new Dimension(w, h);
	}
}
