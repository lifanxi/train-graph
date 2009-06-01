package org.paradise.etrc.view.dynamic;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * @author lguo@sina.com
 * @version 1.0
 */

public class SpeedPanel extends JPanel {
	private static final long serialVersionUID = 2449059376608773861L;

	private DynamicView dView;
	private JLabel lbSpeed;
	
	public SpeedPanel(DynamicView _dView) {
		dView = _dView;
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		this.setLayout(new BorderLayout());

		setBorder(BorderFactory.createLineBorder(Color.lightGray));
				
		JButton btup = createButton("double_up");
		JButton btdown = createButton("double_down");
		
		btup.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				dView.increaseGap(-DynamicView.GAP_STEP);				
			}
		});
		
		
		btdown.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				dView.increaseGap(DynamicView.GAP_STEP);				
			}
		});

		lbSpeed = new JLabel(DynamicView.DEFAULT_GAP + "ms");
		lbSpeed.setHorizontalAlignment(SwingConstants.CENTER);
		lbSpeed.setVerticalAlignment(SwingConstants.CENTER);
		
		lbSpeed.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					dView.timeGap = DynamicView.DEFAULT_GAP;
					SpeedPanel.this.resetSpeed();
				}
			}
		});
		
		add(btup, BorderLayout.WEST);
		add(lbSpeed, BorderLayout.CENTER);
		add(btdown, BorderLayout.EAST);
	}
	
	private JButton createButton(String pic) {
		ImageIcon im = new ImageIcon(this.getClass().getResource("/pic/" + pic + ".gif"));
		JButton jb = new JButton(im);
		
		jb.setFocusPainted(false);
		jb.setBorderPainted(false);
		
		jb.setPreferredSize(new Dimension(16, 16));
		
		return jb;
	}

	public Dimension getPreferredSize() {
		int w, h;
		w = dView.clockPanelWidth;
		h = dView.ditancePanelHeight;
		return new Dimension(w, h);
	}

	public void resetSpeed() {
		lbSpeed.setText(dView.timeGap + "ms");
	}
}
