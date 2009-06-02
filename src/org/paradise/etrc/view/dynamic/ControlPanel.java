package org.paradise.etrc.view.dynamic;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * @author lguo@sina.com
 * @version 1.0
 */

public class ControlPanel extends JPanel {
	private static final long serialVersionUID = 2449059376608773861L;

	private DynamicView dView;
	private JButton btmd;
	private ImageIcon imStart;
	private ImageIcon imPause;
	
	public ControlPanel(DynamicView _dView) {
		dView = _dView;
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
		
		imStart = new ImageIcon(this.getClass().getResource("/pic/start.gif"));
		imPause = new ImageIcon(this.getClass().getResource("/pic/pause.gif"));
		
		//快进
		JButton btdl = createButton("double_left");
		btdl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dView.increaseTime(-10);
			}
		});
		
		//单步前进
		JButton btsl = createButton("single_left");
		btsl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dView.increaseTime(-1);
			}
		});
		
		//单步后退
		JButton btsr = createButton("single_right");
		btsr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dView.increaseTime(1);
			}
		});
		
		//快退
		JButton btdr = createButton("double_right");
		btdr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dView.increaseTime(10);
			}
		});

		btmd = createButton("pause");
		btmd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dView.changeRunState();
			}
		});
		
		add(btdl);
		add(btsl);
		add(btmd);
		add(btsr);
		add(btdr);
	}
	
	public JButton createButton(String pic) {
		ImageIcon im = new ImageIcon(this.getClass().getResource("/pic/" + pic + ".gif"));
		JButton jb = new JButton(im);
		
		jb.setFocusPainted(false);
		jb.setBorderPainted(false);
		
		return jb;
	}


	public Dimension getPreferredSize() {
		int w, h;
		w = dView.clockPanelWidth;
		h = dView.ditancePanelHeight;
		return new Dimension(w, h);
	}

	public void resetButton() {
		if(dView.isRunning())
			btmd.setIcon(imPause);
		else
			btmd.setIcon(imStart);
	}
}
