package org.paradise.etrc.test;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;

public class Test extends JFrame {
	private static final long serialVersionUID = -1479387181318873760L;
	JTable table = new JTable(10, 10);

	public Test() {
		Container contentPane = getContentPane();

		contentPane.add(new JScrollPane(table), BorderLayout.CENTER);

		table.getModel().addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				int firstRow = e.getFirstRow(), column = e.getColumn();

				String properties = "   source="
						+ e.getSource()
						+ "   firstRow=   "
						+ (firstRow == TableModelEvent.HEADER_ROW ? "HEADER_ROW"
								: Integer.toString(firstRow))
						+

						"   lastRow=   "
						+ e.getLastRow()
						+

						"   column=   "
						+ (firstRow == TableModelEvent.ALL_COLUMNS ? "ALL_COLUMNS"
								: Integer.toString(column));

				String typeString = new String();
				int type = e.getType();

				switch (type) {
				case TableModelEvent.DELETE:
					typeString = "DELETE";
					break;
				case TableModelEvent.INSERT:
					typeString = "INSERT";
					break;
				case TableModelEvent.UPDATE:
					typeString = "UPDATE";
					break;
				}
				properties += "   type=" + typeString;

				JOptionPane.showMessageDialog(Test.this, e.getClass().getName()
						+ "[" + properties + "]");
			}
		});
	}

	public static void main(String args[]) {
		new Test().show();

	}
}
