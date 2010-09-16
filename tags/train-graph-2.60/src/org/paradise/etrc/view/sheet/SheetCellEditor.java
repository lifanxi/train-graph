package org.paradise.etrc.view.sheet;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;

import org.paradise.etrc.data.Stop;
import org.paradise.etrc.data.Train;

public class SheetCellEditor extends AbstractCellEditor implements TableCellEditor {
	private static final long serialVersionUID = 1L;

	private Stop stop;
	private boolean isArriveLine;
	private JTextField editor;
	private String oldTime;
	
	public Component getTableCellEditorComponent(final JTable table, Object value, boolean isSelected, final int row, final int column) {
		isArriveLine = row % 2 == 0;
		stop = (Stop) value;
		
		editor = new JTextField();
		
		//响应双击事件，左键双击改为办客，右键双击改为非办客
		editor.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				if(me.getClickCount() >= 2 && me.getButton() == MouseEvent.BUTTON3) {
					if(stop!=null) {
						stop.isPassenger = false;
						table.updateUI();
						setEditorColor();
					}
				}
				else if(me.getClickCount() >= 2 && me.getButton() == MouseEvent.BUTTON1){
					if(stop!=null) {
						stop.isPassenger = true;
						table.updateUI();
						setEditorColor();
					}
				}
			}
			
			private void setEditorColor() {
				//设置文字颜色
				if(stop!=null)
					if(!stop.isPassenger) {
						editor.setForeground(Color.red);
						editor.setSelectedTextColor(Color.red);
					}
					else{
						editor.setForeground(Color.black);
						editor.setSelectedTextColor(Color.white);
					}
				else{
					editor.setForeground(Color.black);
					editor.setSelectedTextColor(Color.white);
				}
			}
		});
		
		editor.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				editor.selectAll();
			}
			public void focusLost(FocusEvent arg0) {
			}
		});

		table.changeSelection(row, column, false, false);
		table.repaint();
		table.getTableHeader().repaint();
//		((SheetTable) table).getRowHeader().setSelectedIndex(row);
//		((SheetTable) table).getRowHeader().repaint();
		
		editor.setBorder(BorderFactory.createEmptyBorder());
		editor.setHorizontalAlignment(SwingConstants.CENTER);
		
		//设置文字颜色
		if(stop!=null)
			if(!stop.isPassenger) {
				editor.setForeground(Color.red);
				editor.setSelectedTextColor(Color.red);
			}
			else {
				editor.setForeground(Color.black);
				editor.setSelectedTextColor(Color.white);
			}
		else{
			editor.setForeground(Color.black);
			editor.setSelectedTextColor(Color.white);
		}
		
		//设置文本
		oldTime = (value == null) ? "" : 
 			      (isArriveLine ? stop.arrive : stop.leave);
		editor.setText(oldTime);
		
		return editor;
	}

	public Object getCellEditorValue() {
		String time = Train.formatTime(oldTime, editor.getText());
		
		//判断原来是否有数据，既原来的stop是否为null
		if(stop == null) {
			//原来没有数据，并且用户输入了时间－－设置标志，通知DataModel加入
			if(!time.equals("")) {
				stop = new Stop(null, time, time, false);
			}
			//原来没有数据，并且没有输入时间则直接返回空值（什么也不做，让stop=null返回）
		}
		else {
			//原来有数据，并且用户删除了时间－－设置标志，通知DataModel删除
			if(!oldTime.equals("") && time.equals("")) {
				stop.arrive = "DEL"; 
				stop.leave  = "DEL"; 
			}
			else {
				if(isArriveLine)
					stop.arrive = time;
				else
					stop.leave = time;
			}
		}
		
		return stop;
	}
}
