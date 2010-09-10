package org.paradise.etrc.wizard;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.*;

import static org.paradise.etrc.ETRC._;

public abstract class WizardDialog extends JDialog {
	private static final long serialVersionUID = -5409636948664788767L;

	public int step;
	protected JButton finishButton;
	protected JButton cancelButton;
	protected JButton nextButton;
	protected JButton prevButton;
	
	public boolean canFinish = true;
	public boolean canNext = true;
	public boolean canPrev = true;
	public boolean canCancel = true;
	
	private JFrame frame;
	protected String wizardTitle;
	protected String stepTitle;
	
	public static final int FINISH = 0;
	public static final int CANCEL = -1;
	public static final int NEXT = 1;
	public static final int PREV = 2;
	private int rtState = -1;
	
	public WizardDialog(JFrame _frame, int _step, String _wizardTitle, String _stepTitle) {
		super(_frame, _wizardTitle);
		frame = _frame;
		step = _step;
		wizardTitle = _wizardTitle;
		stepTitle = _stepTitle;
//		stepPanel = panel;
		
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		init();
	}
	
	protected void init() {
		ImageIcon image = new ImageIcon(getClass().getResource("/pic/wiz_" + step + ".gif"));
		JLabel lbNumberPic = new JLabel(image); 
		lbNumberPic.setVerticalAlignment(JLabel.TOP);
		lbNumberPic.setBorder(BorderFactory.createEmptyBorder(20, 2, 20, 2));
		
		JLabel lbStepTitle = new JLabel(String.format(_("Step %d: %s"), step, stepTitle));
		lbStepTitle.setFont(new Font(_("FONT_NAME"), Font.PLAIN, 12));
		lbStepTitle.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		JPanel right = new JPanel();
		right.setLayout(new BorderLayout());
		right.add(lbStepTitle, BorderLayout.NORTH);
		right.add(getStepPane(), BorderLayout.CENTER);
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BorderLayout());
		mainPane.setBorder(BorderFactory.createEmptyBorder(5, 2, 2, 5));
		mainPane.add(lbNumberPic, BorderLayout.WEST);
		mainPane.add(right, BorderLayout.CENTER);
		mainPane.add(getButtonPane(), BorderLayout.SOUTH);

		this.getContentPane().add(mainPane);
		
		JRootPane rp = this.getRootPane();
		KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
        InputMap inputMap = rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(stroke, "ESCAPE");
        ActionMap actionMap = rp.getActionMap();
        actionMap.put("ESCAPE", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent arg0) {
				if(canCancel)
					WizardDialog.this.setVisible(false);
			}
        });

	}
	
	protected JPanel stepPanel;
	
	protected JPanel getStepPane() {
		if(stepPanel == null)
			stepPanel = createStepPane();

		return stepPanel;
	}
	
	protected abstract JPanel createStepPane();
	
	protected JPanel getButtonPane() {
		JPanel btPane = new JPanel();
		JPanel right = new JPanel();
		btPane.setLayout(new BorderLayout());
		btPane.add(right, BorderLayout.EAST);
		
		finishButton = new JButton(_("Finish"));
		cancelButton = new JButton(_("Cancel"));
		nextButton = new JButton(_("Next >"));
		prevButton = new JButton(_("< Previous"));
		
		finishButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doFinish();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doCancel();
			}
		});
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doNext();
			}
		});
		prevButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doPrev();
			}
		});
		
		right.add(prevButton);
		right.add(nextButton);
		right.add(cancelButton);
		right.add(finishButton);
				
		return btPane;
	}
	
	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			doCancel();
		}
		super.processWindowEvent(e);
	}


	protected void doFinish() {
		rtState = FINISH;
		setVisible(false);
	}

	protected void doCancel() {
		rtState = CANCEL;
		setVisible(false);
	}

	protected void doNext() {
		rtState = NEXT;
		setVisible(false);
	}

	protected void doPrev() {
		rtState = PREV;
		setVisible(false);
	}

	protected void updateButtonState() {
		finishButton.setEnabled(canFinish);
		cancelButton.setEnabled(canCancel);
		nextButton.setEnabled(canNext);
		prevButton.setEnabled(canPrev);
		
		finishButton.setDefaultCapable(false); 
		cancelButton.setDefaultCapable(false); 
		nextButton.setDefaultCapable(false); 
		prevButton.setDefaultCapable(false); 
		
		if(canNext) {
//			nextButton.requestFocus();
			this.getRootPane().setDefaultButton(nextButton);
		}
		else if(canFinish) {
//			finishButton.requestFocus();
			this.getRootPane().setDefaultButton(finishButton);
		}
		else if(canPrev) {
//			prevButton.requestFocus();
			this.getRootPane().setDefaultButton(prevButton);
		}
//		else if(canCancel) {
//			cancelButton.requestFocus();
//			this.getRootPane().setDefaultButton(cancelButton);
//		}
	}

	abstract protected void updateStepPane();

	private void updateUI() {
		updateStepPane();
		updateButtonState();
	}

//	public void doModal(boolean b) {
//	setModal(b);
	public int doModal() {
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

		updateUI();
		pack();
		
		setModal(true);
		setVisible(true);
		updateUI();

		return rtState;
	}


}
