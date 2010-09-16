package org.paradise.etrc.view.sheet;

import javax.swing.AbstractListModel;

import org.paradise.etrc.data.Chart;

public class RowHeaderModel extends AbstractListModel {
	private static final long serialVersionUID = 547009998890792058L;
	
	private Chart chart;
	
	public RowHeaderModel(Chart _chart) {
		chart = _chart;
	}
	
    public int getSize() { 
    	return chart.circuit.stationNum * 2;
    }
    
    public Object getElementAt(int index) { 
		String sta = chart.circuit.stations[index / 2].name;
		sta += index % 2 == 0 ? "站 到" : "站 发";
		return sta;
    }
}
