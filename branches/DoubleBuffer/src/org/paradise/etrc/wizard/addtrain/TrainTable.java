package org.paradise.etrc.wizard.addtrain;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.paradise.etrc.data.Stop;

public class TrainTable extends JTable {
	private static final long serialVersionUID = 356612888441359876L;

	public TrainTable() {
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		setDefaultEditor(Stop.class, new TrainTableCellEditor());
		setDefaultRenderer(Stop.class, new TrainTableCellRanderer());
		
		setDefaultEditor(String.class, new TrainTableCellEditor() {
			private static final long serialVersionUID = 1L;
			private JTextField editor;
			
			public Component getTableCellEditorComponent(final JTable table, Object value, boolean isSelected, final int row, final int column) {
				editor = (JTextField) new JTextField((String)value);
				
				editor.addFocusListener(new FocusListener() {
					public void focusGained(FocusEvent arg0) {
						editor.selectAll();
					}
					public void focusLost(FocusEvent arg0) {
					}
				});

				editor.setBorder(BorderFactory.createEmptyBorder());
				editor.setHorizontalAlignment(SwingConstants.CENTER);
				
				return editor;
			}
			
			public Object getCellEditorValue() {
				return editor.getText().trim();
			}
		});
		
		setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;
			
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				
				if(!(comp instanceof JLabel))
					return comp;

				JLabel lb = (JLabel) comp;
				lb.setHorizontalAlignment(SwingConstants.CENTER);

				return lb;
			}
		});

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setupColumnWidth();
	}
	
	public Dimension getPreferredScrollableViewportSize() {
		int h = this.getRowHeight() * 12;
		int w = 0;
		for (int i = 0; i < getColumnCount(); i++) {
			w += getColumnModel().getColumn(i).getPreferredWidth();
		}
		return new Dimension(w, h);
	}

	private int getPreferredWidthForCloumn(JTable table, int icol) {
		TableColumnModel tcl = table.getColumnModel();
		TableColumn col = tcl.getColumn(icol);
		int c = col.getModelIndex(), width = 0, maxw = 0;

		for (int r = 0; r < table.getRowCount(); ++r) {

			TableCellRenderer renderer = table.getCellRenderer(r, c);
			Component comp = renderer.getTableCellRendererComponent(table,
					table.getValueAt(r, c), false, false, r, c);
			width = comp.getPreferredSize().width;
			maxw = width > maxw ? width : maxw;
		}
		return maxw;
	}
	
	public void setupColumnWidth() {
		//设置列宽
		for (int i = 0; i < getColumnCount(); i++) {
			int with = getPreferredWidthForCloumn(this, i) + 16;
			getColumnModel().getColumn(i).setPreferredWidth(with);
		}
	}			
}
