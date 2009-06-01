package org.paradise.etrc.view.sheet;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.JTableHeader;

public class RowHeaderRenderer extends JLabel implements ListCellRenderer {
	private static final long serialVersionUID = -3720951686492113933L;

	JTable table;
    public RowHeaderRenderer(JTable _table) {
    	table = _table;
        JTableHeader header = table.getTableHeader();
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
        setHorizontalAlignment(RIGHT);
        setFont(header.getFont());
    }
    
    public void paint(Graphics g) {
    	super.paint(g);
    	
    	g.setColor(Color.gray);
    	//下横线
    	g.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
    	//左竖线
    	g.drawLine(0, 0, 0, getHeight()-1);
    	//右竖线
    	g.drawLine(getWidth()-1, 0, getWidth()-1, getHeight());
    }
    
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		setText((value == null) ? "" : value.toString());
		
		if(isSelected) {
			setBackground(SheetView.selectBK);
		}
		else {
			setBackground(index % 2 == 0 ? SheetView.lineBK1 : SheetView.lineBK2);
		}
		
		return this;
	}
}
