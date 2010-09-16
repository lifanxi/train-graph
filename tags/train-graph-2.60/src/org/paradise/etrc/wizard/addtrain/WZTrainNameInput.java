package org.paradise.etrc.wizard.addtrain;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

import org.paradise.etrc.data.Train;
import org.paradise.etrc.wizard.WizardDialog;

import static org.paradise.etrc.ETRC._;

public class WZTrainNameInput extends WizardDialog {
	private static final long serialVersionUID = -874417475779045052L;

	public WZTrainNameInput(JFrame _frame, int _step, String _wizardTitle, String _stepTitle) {
		super(_frame, _step, _wizardTitle, _stepTitle);

		canPrev = false;
		canNext = false;
		canFinish = false;
		canCancel = true;
	}
	
	protected void updateStepPane() {
		((TrainNamePanel) getStepPane()).setupComponent();
	}
	
	protected JPanel createStepPane() {
		return new TrainNamePanel();
	}
	
	private class TrainNamePanel extends JPanel implements FocusListener {
		private static final long serialVersionUID = -5916279280631048174L;

		private JTextField tfFull;
		private JComboBox  cbDown;
		private JComboBox  cbUp;
		private JTextArea info;
		
		private String[] myNames = new String[4];
		
		public TrainNamePanel() {
			setLayout(new BorderLayout());
			add(createInputField(), BorderLayout.NORTH);
			add(createInfoField(), BorderLayout.CENTER);
			
			setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
//			setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		}
		
		public void setupComponent() {
			tfFull.addFocusListener(this);
			cbDown.addFocusListener(this);
			cbUp.addFocusListener(this);
			
			nextButton.addFocusListener(this);
			cancelButton.addFocusListener(this);

			tfFull.requestFocus();
		}

		public Dimension getPreferredSize() {
			return new Dimension(280, 120);
		}

		public void focusGained(FocusEvent fge) {
			Object src = fge.getSource();
			if(src.equals(tfFull)) {
				canNext = false;
				updateButtonState();
				info.setText(_("  Please input full train number, the separator for the train number can be slash, comma or period, <Tab> to finish input"));
				info.setForeground(new Color(40,80,40));
			}
			else if(src.equals(cbDown)) {
				if(cbDown.getModel().getSize() >= 2) {
					info.setText(_("  Select the down-going train number for this circuit, use Mouse or Up/Down arrow key, <Tab> to finish selection."));
					info.setForeground(new Color(80,40,40));
				}
				else
					cbUp.requestFocus();
			}
			else if(src.equals(cbUp)) {
				if(cbUp.getModel().getSize() >= 2) {
					info.setText(_("  Select the up-going train number for this circuit, use Mouse or Up/Down arrow key, <Tab> to finish selection."));
					info.setForeground(new Color(40,40,80));
				}
				else
					nextButton.requestFocus();
			}
			else if(src.equals(nextButton)) {
				info.setText(String.format(_("  Next Step: Input or read train information for the train %s. (Press Enter or Spacebar)"), tfFull.getText()));
				info.setForeground(new Color(40,80,40));
			}
			else if(src.equals(cancelButton)) {
				info.setText(_("  Cancel adding train."));
				info.setForeground(new Color(40,80,40));
			}
		}

		public void focusLost(FocusEvent fle) {
			Object src = fle.getSource();
			if(src.equals(tfFull)) {
				boolean inOK = formatName(tfFull.getText());
				if(inOK) {
					canNext = true;
					updateButtonState();
				}
				else {
					tfFull.requestFocus();
					canNext = false;
					updateButtonState();
				}
			}
		}

		private JComponent createInputField() {
			JPanel input = new JPanel();
			
			tfFull = new JTextField("");
			cbDown = new JComboBox();
			cbUp   = new JComboBox();
			
			JPanel down = new JPanel();
			down.setLayout(new GridLayout(1,2));
			down.add(createNamedPanel(_("Down-going"), cbDown));
			down.add(createNamedPanel(_("Up-going"), cbUp));
			
			input.setLayout(new GridLayout(2,1));
			input.add(createNamedPanel(_("Train Number"), tfFull));
			input.add(down);
			
			return input;
		}
		
		private boolean formatName(String input) {
			input = input.trim();

			input = input.replace('\\', '/');

			input = input.replace('、', '/');
			
			input = input.replace('，', '/');
			input = input.replace(',', '/');
			
			input = input.replace('。', '/');			
			input = input.replace('.', '/');
			
			String[] names = input.split("/");
			if(names.length > 4)
				return false;
			
			myNames = new String[names.length];
			myNames[0] = names[0].toUpperCase();
			for(int i=1; i<names.length; i++) {
				if(names[i].length() <=2 && myNames[0].length() > names[i].length()) {
					myNames[i] = (myNames[0].substring(0, myNames[0].length() - names[i].length()) + names[i]).toUpperCase();
				}
				else {
					myNames[i] = names[i].toUpperCase();
				}
			}
			
			String fullName = Train.makeFullName(myNames);
//			String fullName = myNames[0];
//			for(int i=1; i<myNames.length; i++) {
//				if(myNames[i] != null && myNames[i].length()>0)
//				fullName = fullName + "/" + myNames[i];
//			}
			
			tfFull.setText(fullName);
			
			Vector<String> upNames = new Vector<String>();
			Vector<String> downNames = new Vector<String>();
			for(int i = 0; i < myNames.length; i++) {
				if(myNames[i] != null) {
					if(Train.isDownName(myNames[i])) {
						downNames.add(myNames[i]);
					}
					else if(Train.isUpName(myNames[i])) {
						upNames.add(myNames[i]);
					}
				}
			}
			
			if(upNames.size() == 0 && downNames.size() == 0)
				return false;
			
			cbDown.setModel(new DefaultComboBoxModel(downNames));
			cbUp.setModel(new DefaultComboBoxModel(upNames));

			return true;
		}
		
		private JPanel createNamedPanel(String name, JComponent comp) {
			JPanel pane = new JPanel();
			
			JLabel lbName = new JLabel(name);
			lbName.setFont(new Font(_("FONT_NAME"), Font.PLAIN, 12));
			
			pane.setLayout(new BorderLayout());
			pane.add(lbName, BorderLayout.WEST);
			pane.add(comp, BorderLayout.CENTER);
			
			pane.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
			
			return pane;
		}
		
		private JComponent createInfoField() {
			info = new JTextArea();
			
//			info.setText("    请输入全车次，复车次的间隔符可以是左右斜杠，顿号，逗号，句号（点），后续车次可以只输入最后一位或者两位，Tab键结束输入。");
			info.setFont(new Font(_("FONT_NAME"), Font.PLAIN, 12));
			info.setCaret(new DefaultCaret() {  
				private static final long serialVersionUID = 1L;
				public boolean isVisible() {  
					return false;
				}
			});
			info.setEditable(false);
			info.setFocusable(false);
			info.setLineWrap(true);
//			info.setForeground(new Color(40,80,40));
			info.setBackground(this.getBackground());
			info.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
			
			return info;
		}
	}

	public String getFullName() {
		return ((TrainNamePanel) getStepPane()).tfFull.getText();
	}

	public String getDownName() {
		return (String) ((TrainNamePanel) getStepPane()).cbDown.getSelectedItem();
	}

	public String getUpName() {
		return (String) ((TrainNamePanel) getStepPane()).cbUp.getSelectedItem();
	}
}
