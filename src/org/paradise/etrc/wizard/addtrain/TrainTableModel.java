package org.paradise.etrc.wizard.addtrain;

import javax.swing.table.AbstractTableModel;

import org.paradise.etrc.data.Stop;
import org.paradise.etrc.data.Train;

import static org.paradise.etrc.ETRC._;

public class TrainTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1014817974495127589L;

	public Train myTrain;

	TrainTableModel(Train _train) {
		myTrain = _train;
	}
	
	public int getColumnCount() {
		return 4;
	}

	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return _("Station");
		case 1:
			return _("Arrival");
		case 2:
			return _("Leave");
		case 3:
			return _("Passenger");
		default:
			return null;
		}
	}
	
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return String.class;
		case 1:
		case 2:
			return Stop.class;
		case 3:
			return Boolean.class;
		default:
			return null;
		}
	}

	public int getRowCount() {
		return (myTrain == null) ? 0 : myTrain.stopNum;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return (myTrain == null) ? "" : myTrain.stops[rowIndex].stationName;
		case 1:
			return (myTrain == null) ? null : myTrain.stops[rowIndex];
		case 2:
			return (myTrain == null) ? null : myTrain.stops[rowIndex];
		case 3:
			return (myTrain == null) ? new Boolean(true) : Boolean.valueOf(myTrain.stops[rowIndex].isPassenger);
		default:
			return null;
		}
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if(myTrain == null)
			return;
		
		switch (columnIndex) {
		case 0:
			myTrain.stops[rowIndex].stationName = (String) aValue;
			break;
		case 1:
			myTrain.stops[rowIndex] = (Stop) aValue;
			break;
		case 2:
			myTrain.stops[rowIndex] = (Stop) aValue;
			break;
		case 3:
			myTrain.stops[rowIndex].isPassenger = ((Boolean) aValue).booleanValue();
			break;
		default:
		}

		fireTableCellUpdated(rowIndex, columnIndex);
	}
}
