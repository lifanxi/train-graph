package org.paradise.etrc.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import static org.paradise.etrc.ETRC._;

/**
 * @author lguo@sina.com
 * @version 1.0
 */

public class AboutBox extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;

	JPanel panel1 = new JPanel();

	JButton button1 = new JButton();

	JLabel imageLabel = new JLabel();

	JLabel label1 = new JLabel();
	JLabel label2 = new JLabel();
	JLabel label3 = new JLabel();
	JLabel label4 = new JLabel();

	ImageIcon image1 = new ImageIcon();

	JLabel label5 = new JLabel();
	JLabel label6 = new JLabel();
	GridBagLayout gridBagLayout1 = new GridBagLayout();

	public AboutBox(Frame parent) {
		super(parent);
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	AboutBox() {
		this(null);
	}

	//Component initialization
	private void jbInit() throws Exception {
		image1 = new ImageIcon(org.paradise.etrc.MainFrame.class
				.getResource("/pic/about.png"));
		imageLabel.setIcon(image1);
		this.setTitle(_("About"));
		panel1.setLayout(gridBagLayout1);
		label1.setFont(new java.awt.Font("Dialog", 0, 12));
		label1.setText(_("LGuo's Train Graph"));

		label2.setText("Version 2.60");
		label3.setText("Build 20100916 Revision 102");

		label4.setFont(new java.awt.Font("Dialog", 0, 12));
		label4.setText(_("Author: Guo Lei (HASEA ID:lguo)"));
		label5.setText(_("Thanks to: achen1 on HASEA"));
		label5.setFont(new java.awt.Font("Dialog", 0, 12));
		label6.setText("http://train-graph.googlecode.com    ");
		label6.setFont(new java.awt.Font("Dialog", 0, 12));

		button1.setActionCommand("Ok");
		button1.setText(_("OK"));
		button1.setFont(new java.awt.Font("Dialog", 0, 12));
		button1.addActionListener(this);

		panel1.add(label4, new GridBagConstraints(1, 3, 2, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						0, 0, 0, 30), 0, 0));
		panel1.add(label2, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
						0, 0, 0, 0), 0, 0));
		panel1.add(label3, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0));
		panel1.add(label5, new GridBagConstraints(1, 4, 3, 1, 0.0, 0.0,
				GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE,
				new Insets(0, 0, 20, 0), 0, 0));
		panel1.add(label6, new GridBagConstraints(1, 5, 3, 1, 0.0, 0.0,
				GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE,
				new Insets(0, 0, 20, 0), 0, 0));
		panel1.add(label1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE,
				new Insets(20, 0, 0, 0), 0, 0));
		panel1.add(button1, new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						0, 0, 10, 0), 0, 0));
		panel1.add(imageLabel, new GridBagConstraints(0, 1, 1, 5, 0.0, 0.0,
				GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(
						-30, 10, 30, 30), 0, 0));
		this.getContentPane().add(panel1, BorderLayout.CENTER);
		setResizable(true);
	}

	//Overridden so we can exit when window is closed
	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			cancel();
		}
		super.processWindowEvent(e);
	}

	//Close the dialog
	void cancel() {
		dispose();
	}

	//Close the dialog on a button event
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == button1) {
			cancel();
		}
	}
}
