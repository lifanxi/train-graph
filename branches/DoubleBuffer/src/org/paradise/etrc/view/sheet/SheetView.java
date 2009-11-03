package org.paradise.etrc.view.sheet;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.paradise.etrc.MainFrame;
import org.paradise.etrc.data.Circuit;
import org.paradise.etrc.data.Station;
import org.paradise.etrc.data.Train;
//import org.paradise.etrc.dialog.MessageBox;
import org.paradise.etrc.slice.ChartSlice;

public class SheetView extends JPanel {
	private static final long serialVersionUID = -341968803023065919L;

	public static final Color lineBK1 = Color.white;
	public static final Color lineBK2 = new Color(224, 255, 255);
	public static final Color headerBK = new Color(255, 224, 224);
	public static final Color selectBK = new Color(192, 192, 255);
	
	MainFrame mainFrame;
	public SheetTable table;
	JList rowHeader;
	SheetHeaderRanderer conner;

	public SheetView(MainFrame _mainFrame) {
		mainFrame = _mainFrame;

		table = new SheetTable(this);
		rowHeader = buildeRowHeader(table);
		
		JScrollPane spTable = new JScrollPane(table);
		spTable.setRowHeaderView(rowHeader);
		conner = new SheetHeaderRanderer();
//		conner.setText(("(" + mainFrame.chart.dNum + " / " +  mainFrame.chart.uNum + ")"));
//		conner.setText(("Train No."));
		spTable.setCorner(JScrollPane.UPPER_LEFT_CORNER, conner);
		updateData();
		
		BorderLayout layout = new BorderLayout();
		layout.setHgap(2);
		setLayout(layout);
		add(spTable, BorderLayout.CENTER);

//		JPanel controlPanel = buildeControlPanel();
//		add(controlPanel, BorderLayout.SOUTH);
	}
	
	

//	public void paint(Graphics g) {
//		super.paint(g);
//		
//		mainFrame.statusBarMain.setText(mainFrame.chart.circuit.name + "点单：左键双击将该停站(或通过)时间改为“图定”，右键双击将该停站(或通过)时间改为“非图定”(红色字体)。");		
//	}
	
//	private JPanel buildeControlPanel() {
//		JPanel controlPanel = new JPanel();
//		
//		controlPanel.add(new JLabel(mainFrame.chart.circuit.name));
////		controlPanel.add(new JCheckBox("上行"));
////		controlPanel.add(new JCheckBox("下行"));
//		controlPanel.add(new JLabel("点单"));
//		controlPanel.add(new TextField("    "));
//		controlPanel.add(new JButton("添加"));
//		
////		controlPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
//		org.paradise.etrc.ETRC.setFont(controlPanel);
//		
//		return controlPanel;
//	}
		
	private JList buildeRowHeader(final JTable table) {
		RowHeaderModel listModel = new RowHeaderModel(mainFrame.chart);
        JList rowHeader = new JList(listModel);
        rowHeader.setFixedCellWidth(80);
        rowHeader.setFixedCellHeight(table.getRowHeight());
        rowHeader.setCellRenderer(new RowHeaderRenderer(table));
        
        //只能选择单行
        rowHeader.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        rowHeader.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent me) {
				if(me.getClickCount() >= 2 && me.getButton() == MouseEvent.BUTTON1) {
					String rowHeaderName = (String) ((JList)me.getSource()).getSelectedValue();
					String staName = rowHeaderName.substring(0, rowHeaderName.length()-3);
					Station station = mainFrame.chart.circuit.getStation(staName);
//					new MessageBox(mainFrame, "TODO: 给出 "
//							   + station.name
//							   + "站 所有列车停靠、通过（推算）时刻表。 ").showMessage();
					
					new ChartSlice(mainFrame.chart).makeStationSlice(station);
				}
			}
        });
        
        rowHeader.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent lse) {
				int row = ((JList) lse.getSource()).getSelectedIndex();
				table.changeSelection(row, table.getSelectedColumn(), false, false);
				table.editCellAt(row, table.getSelectedColumn());
			}
        });
        
        return rowHeader;
	}

	public void selectStation(Station station) {
		Circuit circuit = mainFrame.chart.circuit;
		for(int i=0; i<circuit.stationNum; i++) {
			if(station.equals(circuit.stations[i]))
				rowHeader.setSelectedIndex(i*2);
		}
	}
	
	public void selectTrain(Train train) {
		for(int i=0; i<table.getColumnCount(); i++) {
			if(table.getColumnName(i).equals(train.getTrainName(mainFrame.chart.circuit))) {
				int row = table.getSelectedRow();
				table.changeSelection(row, i, false, false);
				table.editCellAt(row, i);
				table.repaint();
				table.getTableHeader().repaint();
			}
		}
	}
	
	public void refresh() {
		table.repaint();
		rowHeader.repaint();
		table.getTableHeader().repaint();
	}

	public void updateData() {
		conner.setText("D:" + mainFrame.chart.dNum + " U:" + mainFrame.chart.uNum + "");
		
		SheetModel model = (SheetModel) table.getModel();
		model.fireTableDataChanged();
		model.fireTableStructureChanged();
		
		table.setupColumnWidth();
	}
}


/*******************************************************************************
 * public class MyTableHeaderUI extends
 * javax.swing.plaf.basic.BasicTableHeaderUI {
 * 
 * protected void installListeners() { super.installListeners();
 * 
 * MouseInputHandler mouseInputListener = new MouseInputHandler();
 * header.addMouseListener(mouseInputListener); }
 * 
 * public class MouseInputHandler extends MouseInputAdapter {
 * 
 * public void mouseClicked(MouseEvent e) { Point p = e.getPoint(); int
 * clickCount = e.getClickCount();
 * 
 * JTable thisTable = header.getTable(); FontMetrics fm =
 * thisTable.getFontMetrics(thisTable.getFont()); TableColumnModel columnModel =
 * header.getColumnModel(); int index = getResizingColumn(p,
 * columnModel.getColumnIndexAtX(p.x)); int rows = thisTable.getRowCount(); int
 * maxLength = 0;
 * 
 * if (clickCount > 1) { if (index != -1 && rows > 0) { if (canResize(index)) {
 * for (int i=0; i<rows; i++) { Object objValue = thisTable.getValueAt(i,
 * index); if (objValue instanceof java.lang.String) { int thisLen =
 * fm.stringWidth((String) objValue); maxLength = Math.max(maxLength, thisLen); } }
 * if (maxLength > 0)
 * thisTable.getColumnModel().getColumn(index).setPreferredWidth(maxLength + 7); } } } }
 * 
 * private boolean canResize(int index) { TableColumn tblColumn =
 * header.getColumnModel().getColumn(index); return (tblColumn != null) &&
 * header.getResizingAllowed() && tblColumn.getResizable(); }
 * 
 * private int getResizingColumn(Point p, int column) { if (column == -1) {
 * return -1; } Rectangle r = header.getHeaderRect(column); r.grow(-3, 0); if
 * (r.contains(p)) { return -1; } int midPoint = r.x + r.width/2; int
 * columnIndex = (p.x < midPoint) ? column - 1 : column;
 * 
 * return columnIndex; }
 *  } }
 * 
 * 实现了类似与windows下面双击表头自动伸缩的功能。 希望对你有点参考。
 * 
 ******************************************************************************/