package org.paradise.etrc.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.paradise.etrc.ETRC;
import static org.paradise.etrc.ETRC._;

/**
 * @author lguo@sina.com
 * @version 1.0
 */

public class SliceDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;

	JPanel mainPanel = new JPanel();

	JButton button1 = new JButton();

	JLabel imageLabel = new JLabel();

	JLabel msgLabel = new JLabel();

	ImageIcon image1 = new ImageIcon();

	String msg = _("Message Content");

	String title = _("Slice Output");
	
	String name;
	
	BorderLayout borderLayout1 = new BorderLayout();

	JPanel buttonPanel = new JPanel();

	JPanel contentPanel = new JPanel();
	
	JTextArea taSlice = new JTextArea();

	Frame frame;

//	public SliceDialog(Frame parent, String _msg, String _title) {
//	}

//	public SliceDialog(Frame parent, String _msg) {
//		this(parent, _msg, "运行图切片输出窗口（临时）");
//	}

	public SliceDialog(String _name, String _msg) {
		super(ETRC.getInstance().getMainFrame());
		name = _name;
		msg = _msg;
		title = _("Slice Output");
		frame = ETRC.getInstance().getMainFrame();
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Component initialization
	private void jbInit() throws Exception {
		buildSliceText();
		
		image1 = new ImageIcon(org.paradise.etrc.MainFrame.class
				.getResource("/pic/msg.png"));
		imageLabel.setIcon(image1);
		this.setTitle(title);
		mainPanel.setLayout(borderLayout1);
		button1.setFont(new java.awt.Font("Dialog", 0, 12));
		button1.setActionCommand("Ok");
		button1.setText(_("OK"));
		button1.addActionListener(this);

		msgLabel = new JLabel(name, image1, JLabel.LEFT);
		msgLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		msgLabel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));

		mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.add(button1, null);
		
		
		mainPanel.add(contentPanel, BorderLayout.CENTER);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		contentPanel.setLayout(new BorderLayout());
		contentPanel.add(msgLabel, BorderLayout.NORTH);
		JScrollPane sp = new JScrollPane();
		sp.getViewport().add(taSlice);
		sp.getVerticalScrollBar().setUnitIncrement(16);
		contentPanel.add(sp, BorderLayout.CENTER);
		
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);

		setResizable(true);
	}

	private void buildSliceText() {
		taSlice.setEditable(false);
		taSlice.setFont(new Font(_("FONT_NAME"), Font.PLAIN, 12));
		taSlice.setText(msg);
//		taSlice.selectAll();
	}

	//Overridden so we can exit when window is closed
	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			cancel();
		}
		super.processWindowEvent(e);
	}

	public Dimension getPreferredSize() {
		return new Dimension(480, 320);
	}
	
	public void showMessage() {
		Dimension dlgSize = this.getPreferredSize();
		Dimension frmSize;
		Point loc;
		if (frame != null) {
			frmSize = frame.getSize();
			
			if(frmSize.width == 0 || frmSize.height == 0)
				frmSize = Toolkit.getDefaultToolkit().getScreenSize();
			
			loc = frame.getLocation();
		}
		else {
			frmSize = Toolkit.getDefaultToolkit().getScreenSize();
			loc = new Point(0,0);
		}
		this.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
				(frmSize.height - dlgSize.height) / 2 + loc.y);
		this.setModal(false);
		this.taSlice.selectAll();
		this.pack();
		super.setVisible(true);
	}

	//Close the dialog
	private void cancel() {
		dispose();
	}

	//Close the dialog on a button event
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == button1) {
			cancel();
		}
	}
}
