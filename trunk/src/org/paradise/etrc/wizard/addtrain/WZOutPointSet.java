package org.paradise.etrc.wizard.addtrain;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.paradise.etrc.data.Train;
import org.paradise.etrc.wizard.WizardDialog;

public class WZOutPointSet extends WizardDialog {
	private static final long serialVersionUID = 1558550027322954767L;

	public WZOutPointSet(JFrame _frame, int _step, String _wizardTitle, String _stepTitle) {
		super(_frame, _step, _wizardTitle, _stepTitle);
		// TODO Auto-generated constructor stub
	}

	public void setTrain(Train train) {
		if(train == null)
			setTitle(wizardTitle);
		else
			setTitle(wizardTitle + " (" +train.getTrainName() + ")");
	}

	protected JPanel createStepPane() {
		JPanel panel = new JPanel();
		panel = new JPanel();
		panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		panel.setPreferredSize(new Dimension(300, 200));

		return panel;
	}

	protected void updateStepPane() {
		// TODO Auto-generated method stub
		
	}

}
