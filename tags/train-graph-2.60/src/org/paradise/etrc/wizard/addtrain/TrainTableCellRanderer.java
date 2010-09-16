package org.paradise.etrc.wizard.addtrain;

import java.awt.*;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import org.paradise.etrc.data.Stop;

public class TrainTableCellRanderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = -3005467491964709634L;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		if(!(comp instanceof JLabel))
			return comp;

		JLabel lb = (JLabel) comp;
		
		Stop stop = (Stop) value;
		// 设置文字
		lb.setText((value == null) ? "" : ((column == 1) ? stop.arrive	: stop.leave));
		lb.setHorizontalAlignment(SwingConstants.CENTER);

		return lb;
	}
}
