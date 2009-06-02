package org.paradise.etrc.view.sheet;

import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.*;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.paradise.etrc.data.Stop;
import org.paradise.etrc.data.Train;
//import org.paradise.etrc.dialog.MessageBox;
import org.paradise.etrc.slice.ChartSlice;

public class SheetTable extends JTable {
	private static final long serialVersionUID = 1L;
	
	private SheetView sheetView;
	public SheetTable(SheetView _sheetView) {
		sheetView = _sheetView;

		initTable();
	}
	
	public void editingStopped(ChangeEvent e) {
		super.editingStopped(e);
		sheetView.mainFrame.chartView.repaint();
	}
	
	private void initTable() {
		setFont(new Font("Dialog", 0, 12));
		getTableHeader().setFont(new Font("Dialog", 0, 12));

		//设置数据
		setModel(new SheetModel(sheetView.mainFrame.chart));

		//设置渲染器
		setDefaultRenderer(Stop.class, new SheetCellRanderer());
		//设置编辑器
		setDefaultEditor(Stop.class, new SheetCellEditor());
		
		//禁止自动调整列宽
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		//响应右键事件：开始编辑
		//注意：此处table无法响应左键事件
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				//偶尔也能响应到左键，虽然机会不多，但此时单元格会得不到焦点，因此此处不再判断是否右键
				if(me.getButton() == MouseEvent.BUTTON3) {
					Point p = me.getPoint();
					int rowIndex = SheetTable.this.rowAtPoint(p);
					int columnIndex = SheetTable.this.columnAtPoint(p);
					SheetTable.this.editCellAt(rowIndex, columnIndex);
//					SheetTable.this.getEditorComponent().requestFocus();
				}
			}
		});
		
		//响应行选择变化事件
		this.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int row = SheetTable.this.getSelectedRow();
				SheetTable.this.getRowHeader().setSelectedIndex(row);
				SheetTable.this.getRowHeader().repaint();
				SheetTable.this.editCellAt(row, SheetTable.this.getSelectedColumn());
//				SheetTable.this.getEditorComponent().requestFocus();
			}
		});
		
		//设置表头
		setupTableHeader(this.getTableHeader());

		//设置列宽
		setupColumnWidth();
	}
	
	public boolean editCellAt(int row, int col) {
		boolean rt = super.editCellAt(row, col);
		
		if(rt)
			if(getEditorComponent()!=null)
				getEditorComponent().requestFocus();
		
		return rt;
	}

	//－－－－表头设置－－－－//
	private void setupTableHeader(final JTableHeader header) {
		//限制列交换 
		header.setReorderingAllowed(false);
		//限制重置列宽 
		header.setResizingAllowed(false);
		//设置表头渲染
		header.setDefaultRenderer(new SheetHeaderRanderer());
		
		header.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				SheetTable table = SheetTable.this;
				int col = header.columnAtPoint(me.getPoint());
				int row = table.getSelectedRow();
				table.changeSelection(row, col, false, false);
				table.editCellAt(row, col);
				table.repaint();
				header.repaint();
				String trainName = SheetTable.this.getColumnName(col);

				if(me.getClickCount() >= 2 && me.getButton() == MouseEvent.BUTTON1) {
//					new MessageBox(sheetView.mainFrame, "TODO: 给出"
//							   + trainName
//							   + "次列车在 "
//							   + sheetView.mainFrame.chart.circuit.name
//							   + " 所有车站的停靠、通过（推算）时刻表。 ").showMessage();
					
					Train train = sheetView.mainFrame.chart.findTrain(trainName);

					new ChartSlice(sheetView.mainFrame.chart).makeTrainSlice(train);
				}
			}
		});
	}

	int getPreferredWidthForCloumn(JTable table, int icol) {
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
	
	public JList getRowHeader() {
		return sheetView.rowHeader;
	}

	public void setupColumnWidth() {
		//设置列宽
		for (int i = 0; i < getColumnCount(); i++) {
			int with = getPreferredWidthForCloumn(this, i) + 16;
			getColumnModel().getColumn(i).setPreferredWidth(with);
		}
	}			
}
