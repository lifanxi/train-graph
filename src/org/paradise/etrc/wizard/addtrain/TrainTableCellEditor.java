package org.paradise.etrc.wizard.addtrain;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;

import org.paradise.etrc.data.Stop;
import org.paradise.etrc.data.Train;

public class TrainTableCellEditor extends AbstractCellEditor implements TableCellEditor {
	private static final long serialVersionUID = 1L;

	private JTextField editor;
	private String oldTime;
	private Stop stop;
	private boolean isArrive;
	
	public Component getTableCellEditorComponent(final JTable table, Object value, boolean isSelected, final int row, final int column) {
		editor = new JTextField();
		
		editor.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				editor.selectAll();
			}
			public void focusLost(FocusEvent arg0) {
			}
		});

		editor.setBorder(BorderFactory.createEmptyBorder());
		editor.setHorizontalAlignment(SwingConstants.CENTER);
		
		//设置文本
		stop = (Stop) value;
		isArrive = (column == 1);
		oldTime = (value == null) ? "" : (isArrive ? stop.arrive: stop.leave);
		//Joe 2014-07-10 Joe added to format time in add train dialog GUI.	
		// String time = Train.formatTime(oldTime, editor.getText());
				// //判断原来是否有数据，既原来的stop是否为null
		// if(stop == null) {
			// //原来没有数据，并且用户输入了时间－－设置标志，通知DataModel加入
			// if(!time.equals("")) {
				// stop = new Stop(null, time, time, false);
			// }
			// //原来没有数据，并且没有输入时间则直接返回空值（什么也不做，让stop=null返回）
		// }
		// else {
			// //原来有数据，并且用户删除了时间－－设置标志，通知DataModel删除
			// if(!oldTime.equals("") && time.equals("")) {
				// stop.arrive = "DEL"; 
				// stop.leave  = "DEL"; 
			// }
			// else {
				// if(isArriveLine)
					// stop.arrive = time;
				// else
					// stop.leave = time;
			// }
		// }
		
if (stop == null)
{
oldTime = Train.formatTime(oldTime, editor.getText());
}
else
{		
			
}
		editor.setText(oldTime);
		
		return editor;
	}

	public Object getCellEditorValue() {
		String time = Train.formatTime(oldTime, editor.getText());
		
		if(isArrive)
			stop.arrive = time;
		else
			stop.leave = time;
		
		return stop;
	}
	
}
