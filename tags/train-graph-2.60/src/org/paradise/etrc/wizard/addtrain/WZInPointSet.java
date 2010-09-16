package org.paradise.etrc.wizard.addtrain;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultCaret;

import org.paradise.etrc.data.Chart;
import org.paradise.etrc.data.Station;
import org.paradise.etrc.data.Stop;
import org.paradise.etrc.data.Train;
import org.paradise.etrc.slice.ChartEvent;
import org.paradise.etrc.slice.ChartSlice;
import org.paradise.etrc.slice.TrainSlice;
import org.paradise.etrc.wizard.WizardDialog;

import static org.paradise.etrc.ETRC._;

public class WZInPointSet extends WizardDialog {
	private static final long serialVersionUID = 1558550027322954767L;
	private Train train;
	private Chart chart;
	JTextArea info;
	JTextField tfTime;
	
	String oldTime = "00:00";
	Station curStation;

	private JList circuitList;
	
	public WZInPointSet(JFrame _frame, int _step, String _wizardTitle, String _stepTitle) {
		super(_frame, _step, _wizardTitle, _stepTitle);
		
		this.canFinish = false;
	}

	public void setData(Chart _chart, Train _train) {
		chart = _chart;
		train = _train;
		
		if(train == null)
			setTitle(wizardTitle);
		else
			setTitle(wizardTitle + " (" +train.getTrainName() + ")");
	}

	protected JPanel createStepPane() {
		JPanel panel = new JPanel();

		circuitList = new JList();
		circuitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		circuitList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent lse) {
				int row = ((JList) lse.getSource()).getSelectedIndex();
				
				if(row < 0) {
					curStation = null;
					tfTime.setText("");
				}
				else {
					curStation = chart.circuit.stations[row];
					tfTime.setText(calculTime());
				}
			}
        });
		
		JScrollPane spList = new JScrollPane(circuitList);
		
		JPanel upPanel = new JPanel();
		upPanel.setLayout(new BorderLayout());
		upPanel.add(spList, BorderLayout.CENTER);
		upPanel.add(createTimePane(), BorderLayout.SOUTH);
		
		panel.setLayout(new BorderLayout());
		panel.add(upPanel, BorderLayout.CENTER);
		panel.add(createInfoField(), BorderLayout.SOUTH);

		return panel;
	}

	private Component createTimePane() {
		JPanel panel = new JPanel();
		
		JLabel lb = new JLabel(_("Departure/Start Time:"));
		tfTime = new JTextField();
		tfTime.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				tfTime.setText(calculTime());
			}

			public void focusLost(FocusEvent arg0) {
				setTime(tfTime.getText().trim());
			}
		});
		
		panel.setLayout(new BorderLayout());
		panel.add(lb, BorderLayout.WEST);
		panel.add(tfTime, BorderLayout.CENTER);
		
		panel.setBorder(BorderFactory.createEmptyBorder(4,0,0,0));
		
		return panel;
	}

	//计算当前选中车站的推算通过时间（如果有图定时间则取到达时间）
	private String calculTime() {
		if(curStation == null)
			return "";
		
		//有这个停站
		int stopIndex = train.findStopIndex(curStation.name);
		if(stopIndex >= 0) {
			return train.stops[stopIndex].arrive;
		}
		else {
			Station firstStation = chart.circuit.getFirstStopOnMe(train);
			
			if(firstStation == null)
				return "";
			
			int distGap = curStation.dist - firstStation.dist;
			int timeGap = distGap * 60 / Train.getDefaultVelocityByName(train.getTrainName());
			
			Stop stop = train.findStop(firstStation.name);
			
			int timeA = Train.trainTimeToInt(stop.arrive);
			int timeIn = 0;
			if(train.isDownTrain(chart.circuit) == Train.DOWN_TRAIN)
				timeIn = timeA + timeGap;
			else
				timeIn = timeA - timeGap;
			
			return Train.intToTrainTime(timeIn);
		}
	}
	
	protected String calculTime0() {
		//train.curStation.name
		ChartSlice chartSlice = new ChartSlice(chart);
		TrainSlice trainSlice = (TrainSlice) chartSlice.trainSlices.get(train);
		
		//FAIL! 因为train还没有加入chart，所以这里的trainSlice肯定为null
		
		if(trainSlice == null) {
			oldTime = "00:00";
			return oldTime;
		}
		
		Vector<ChartEvent> events = trainSlice.getStationEventsAt(curStation);
		if(events.size() > 0) {
			ChartEvent event = (ChartEvent) events.get(0);
			oldTime = Train.intToTrainTime(event.time);
		}
		else {
			oldTime = "00:00";
		}
		
		return oldTime;
	}
	
	protected void setTime(String input) {
		oldTime = Train.formatTime(oldTime, input.trim());
		tfTime.setText(oldTime);
	}

	private JComponent createInfoField() {
		info = new JTextArea();
		
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
		//设置List选项
		String[] dispNames = new String[chart.circuit.stationNum];
		for(int i=0; i<chart.circuit.stationNum; i++) {
			String strDist = "" + chart.circuit.stations[i].dist;
			while(strDist.length() < 4) {
				strDist = " " + strDist;
			}
			dispNames[i] = String.format(_(" %s down-going direction %s km from %s station: %s station"), 
					chart.circuit.name, strDist, chart.circuit.stations[0].name,   chart.circuit.stations[i].name); 
			
		}
		circuitList.setListData(dispNames);

		//设置当前选中的车站
		if(chart.circuit.isStartInsideMe(train)) {
			info.setText(String.format(_("  Train %s departures from %s station in this section, no need to set start point"), train.getTrainName(), train.getStartStation()));
			curStation = chart.circuit.getStation(train.getStartStation());
		}
		else {
			curStation = chart.circuit.getFirstStopOnMe(train);
			if(curStation != null) {
				info.setText(String.format(_("  The first stop of train %s in this section is %s, change the station and time if this is not correct."), train.getTrainName(), curStation.name));
			}
			else {
				info.setText(String.format(_("  The train %s passes this section, set the start point manually."), train.getTrainName()));
			}
		}

		int index = -1;
		if(curStation != null)
			index = chart.circuit.getStationIndex(curStation.name);
		
		circuitList.setSelectedIndex(index);
		circuitList.ensureIndexIsVisible(index);
	}

	public Dimension getPreferredSize() {
		return new Dimension(380, 380);
	}
	
	public void doNext() {
		String time = tfTime.getText().trim();
		if(time.equalsIgnoreCase(""))
			return;
		else {
			//TODO: train.xstop.setTime;
			Stop stop = new Stop(curStation.name, time, time, false);
			chart.insertNewStopToTrain(train, stop);
			
			super.doNext();
		}
	}
}
