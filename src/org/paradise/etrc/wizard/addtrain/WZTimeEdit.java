package org.paradise.etrc.wizard.addtrain;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

import org.paradise.etrc.data.Stop;
import org.paradise.etrc.data.Train;
import org.paradise.etrc.wizard.WizardDialog;

import static org.paradise.etrc.ETRC._;

public class WZTimeEdit extends WizardDialog {
	private static final long serialVersionUID = -1097100938159035740L;
	private Train train;
	private TrainTable table;
	private TrainTableModel model;
	private JTextArea info;
	
	public WZTimeEdit(JFrame _frame, int _step, String _wizardTitle, String _stepTitle) {
		super(_frame, _step, _wizardTitle, _stepTitle);

		canPrev = false;
		canFinish = false;
	}

	public void setTrain(Train _train, String upName, String downName) {
		train = _train;
		model.myTrain = train;
		model.fireTableDataChanged();
		
		if(train == null) 
			setTitle(wizardTitle);
		else
			setTitle(wizardTitle + " (" +train.getTrainName() + ")");
	}

	protected JPanel createStepPane() {
		JPanel mainPanel = new JPanel();
		
		table = new TrainTable();

		model = new TrainTableModel(train);
		table.setModel(model);
		
		JScrollPane spTable = new JScrollPane(table);
		
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(spTable, BorderLayout.CENTER);
		
		mainPanel.add(createInfoField(), BorderLayout.SOUTH);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(mainPanel, BorderLayout.CENTER);
		panel.add(createButtonPane(), BorderLayout.SOUTH);
		
		return panel;
	}

	protected JPanel createButtonPane() {
		JPanel btPane = new JPanel();
		JPanel right = new JPanel();
		btPane.setLayout(new BorderLayout());
		btPane.add(right, BorderLayout.EAST);
		
		JButton addFButton = new JButton(_("Add(Before)"));
		JButton addBButton = new JButton(_("Add(After)"));
		JButton delButton = new JButton(_("Delete"));
		
		addFButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(table.getCellEditor() != null)
					table.getCellEditor().stopCellEditing();

				int row = table.getSelectedRow();
				model.myTrain.insertStop(new Stop(_("Station"), "00:00", "00:00", false), row);
				model.fireTableDataChanged();
				
				table.getSelectionModel().setSelectionInterval(row, row);
			}
		});
		addBButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(table.getCellEditor() != null)
					table.getCellEditor().stopCellEditing();

				int row = table.getSelectedRow() + 1;
				model.myTrain.insertStop(new Stop(_("Station"), "00:00", "00:00", false), row);
				model.fireTableDataChanged();
				
				table.getSelectionModel().setSelectionInterval(row, row);
			}
		});
		delButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(table.getCellEditor() != null)
					table.getCellEditor().stopCellEditing();

				int row = table.getSelectedRow();
				model.myTrain.delStop(row);
				model.fireTableDataChanged();

				table.getSelectionModel().setSelectionInterval(row, row);
			}
		});
		
//		right.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		
		right.add(addFButton);
		right.add(addBButton);
		right.add(delButton);
				
		return btPane;
	}
	
	private JComponent createInfoField() {
		info = new JTextArea();
		
		info.setText(_("  The separator between the hour and minute can be space, colon, comma or period. 3 or 4 digital number can also be accepted. Click on the next text box will complete the current input. "));
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
		info.setForeground(new Color(40,80,40));
		info.setBackground(this.getBackground());
		info.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		return info;
	}
	
	protected void updateStepPane() {
		// TODO Auto-generated method stub
		
	}

}
