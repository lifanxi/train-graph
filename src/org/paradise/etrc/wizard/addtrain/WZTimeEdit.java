package org.paradise.etrc.wizard.addtrain;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

import org.paradise.etrc.data.Stop;
import org.paradise.etrc.data.Train;
import org.paradise.etrc.wizard.WizardDialog;
import org.paradise.etrc.dialog.YesNoBox;
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
		JButton addTButton = new JButton(_("Interpolate Timetable"));
		//JButton addTButton = new JButton(_("推算时刻"));
		JButton delButton = new JButton(_("Delete"));
		
		addFButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(table.getCellEditor() != null)
					table.getCellEditor().stopCellEditing();

				int row = table.getSelectedRow();
				model.myTrain.insertStop(new Stop(_("Station"), "00:00", "00:00", false,"0"), row);
				model.fireTableDataChanged();
				
				table.getSelectionModel().setSelectionInterval(row, row);
			}
		});
		addBButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(table.getCellEditor() != null)
					table.getCellEditor().stopCellEditing();

				int row = table.getSelectedRow() + 1;
				model.myTrain.insertStop(new Stop(_("Station"), "00:00", "00:00", false,"0"), row);
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
		
		addTButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				        table.getCellEditor().stopCellEditing();
				int curIndex = table.getSelectedRow();

				if(curIndex<1)
				{
					System.out.println("当前行为首行，无法计算！");
					return;
				}


			 int t1 = Train.trainTimeToInt(model.myTrain.stops[curIndex-1].leave);
			 int t2 = Train.trainTimeToInt(model.myTrain.stops[curIndex+1].arrive);
			 
			 int d1 = Integer.parseInt(model.myTrain.stops[curIndex-1].licheng);
			 int d2 = Integer.parseInt(model.myTrain.stops[curIndex+1].licheng); 
		 
			 if (d1 >= d2) {
					System.out.println("错误：出发站里程大于等于到达站里程");
					return ;
			 }
			 if (t2< t1) {
					t2 = t2 + 24 * 60;
			 }
			double mySpeed = ((double)(d2-d1)/(double)(t2-t1));
			System.out.println(d2+ " " + d1 + " " + t2 + " " + t1 + " " + mySpeed + "km/min");
			 //如果里程仍然为0（包括输入未确认），则提示需要更新里程
			 String aa = model.myTrain.stops[curIndex].licheng ;
			 int d ;
			 int t ;
			 if (aa.equalsIgnoreCase("0")){
				 System.out.println("错误：通过站里程为0");
				 return ;
			 }
			 
			//如果里程含有#号，则为相对里程，用于计算
			if(aa.indexOf("#")>=0)
			{
					  aa = aa.replaceAll("#","");
					  d =  Integer.parseInt(aa);
					  
					  if (d >= (d2 -d1)){
						System.out.println("错误：通过站相对里程大于出发站到达站区间里程");  
						return;
					  }
					  if (d <=0){
						System.out.println("错误：通过站相对里程为非正数");  
						return;
					  }						  
					  
					  t =  (int)	((d- 0)/mySpeed + t1) ;
			}
			else
		    //里程为绝对里程，需要计算
			{
					  aa= aa;
					  d =  Integer.parseInt(aa);
					  
					  if (d <= d1 ){
						System.out.println("错误：通过站里程小于等于出发站里程");
						return ;
					  }
					  if (d >= d2 ){
						System.out.println("错误：通过站里程大于等于到达站里程");
						return ;
					  }					  
					  
					  t =  (int)	((d- d1)/mySpeed + t1) ;
					 
			}
			System.out.println(d);
			System.out.println(t);	
				

			if (t > (24*60)) {
				t = t- 24 *60 ;
			}
			System.out.println(d2+ " " + d1 + " " + t2 + " " + t1 + " " + mySpeed + "km/min, pass time:" + t);
			String oo =	Train.intToTrainTime(t);
			//String oo = "13:00";
				model.myTrain.stops[curIndex].arrive = oo;
				//model.myTrain.stops[curIndex].leave = model.myTrain.stops[curIndex].arrive;
                //model.fireTableDataChanged();				
                model.myTrain.stops[curIndex].leave = oo; 
                model.fireTableDataChanged();
				table.getSelectionModel().setSelectionInterval(curIndex, curIndex);

			}
		});
		
//		right.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		
		right.add(addFButton);
		right.add(addBButton);
		right.add(addTButton);
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
