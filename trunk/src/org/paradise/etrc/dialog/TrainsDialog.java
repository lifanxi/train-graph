package org.paradise.etrc.dialog;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.TableModelListener;
import java.awt.event.*;

import javax.swing.event.*;

import org.paradise.etrc.ETRC;
import org.paradise.etrc.MainFrame;
import org.paradise.etrc.data.Chart;
import org.paradise.etrc.data.Stop;
import org.paradise.etrc.data.Train;
import org.paradise.etrc.view.chart.ChartView;

import static org.paradise.etrc.ETRC._;

/**
 * @author lguo@sina.com
 * @version 1.0
 */

public class TrainsDialog extends JDialog {
	private static final long serialVersionUID = -6188814889727919832L;

	Chart chart;

	MainFrame mainFrame;

	TrainsTable table;

	JCheckBox cbUnderColor;

	public TrainsDialog(MainFrame _mainFrame) {
		super(_mainFrame, _("Train Information"), true);
		
		mainFrame = _mainFrame;
		chart = mainFrame.chart;
		
		table = new TrainsTable();

		try {
			jbInit();
			pack();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		table.setModel(new TrainsTableModel());
		table.setDefaultRenderer(Color.class, new ColorCellRenderer());
		table.setDefaultEditor(Color.class, new ColorCellEditor());

		table.setFont(new Font("Dialog", 0, 12));
		table.getTableHeader().setFont(new Font("Dialog", 0, 12));
		JScrollPane spColorTable = new JScrollPane(table);

		cbUnderColor = new JCheckBox();
		cbUnderColor.setFont(new java.awt.Font("Dialog", 0, 12));
		cbUnderColor.setText(_("Display opposite direction train using watermark"));
		cbUnderColor.setSelected(!(mainFrame.chartView.underDrawingColor == null));
		cbUnderColor.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (((JCheckBox) e.getSource()).isSelected())
					mainFrame.chartView.underDrawingColor = ChartView.DEFAULT_UNDER_COLOR;
				else
					mainFrame.chartView.underDrawingColor = null;
				
				mainFrame.chartView.repaint();
			}
		});

		JPanel underColorPanel = new JPanel();
		underColorPanel.setLayout(new BorderLayout());
		underColorPanel.add(cbUnderColor, BorderLayout.WEST);

		JPanel colorPanel = new JPanel();
		colorPanel.setLayout(new BorderLayout());
		colorPanel.add(spColorTable, BorderLayout.CENTER);
		colorPanel.add(underColorPanel, BorderLayout.SOUTH);

		JButton btOK = new JButton(_("OK"));
		btOK.setFont(new Font("dialog", 0, 12));
		btOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				mainFrame.mainView.repaint();
				TrainsDialog.this.setVisible(false);
			}
		});

//		JButton btCancel = new JButton("取 消");
//		btCancel.setFont(new Font("dialog", 0, 12));
//		btCancel.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				TrainsDialog.this.setVisible(false);
//			}
//		});

		JButton btAdd = new JButton(_("Add"));
		btAdd.setFont(new Font("dialog", 0, 12));
		btAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doNewTrain();
			}
		});
		
		JButton btLoad = new JButton(_("Load"));
		btLoad.setFont(new Font("dialog", 0, 12));
		btLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.doLoadTrain();
			}
		});
		
		JButton btEdit = new JButton(_("Edit"));
		btEdit.setFont(new Font("dialog", 0, 12));
		btEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				TrainsDialog.this.setVisible(false);
				if(table.getSelectedRow() < 0)
					return;
				doEditTrain(chart.trains[table.getSelectedRow()]);
			}
		});
		
		JButton btDel = new JButton(_("Delete"));
		btDel.setFont(new Font("dialog", 0, 12));
		btDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getCellEditor() != null)
					table.getCellEditor().stopCellEditing();

				chart.delTrain(table.getSelectedRow());

				table.revalidate();
				mainFrame.chartView.repaint();
		        mainFrame.runView.refresh();
			}
		});
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(btLoad);
		buttonPanel.add(btAdd);
		buttonPanel.add(btEdit);
		buttonPanel.add(btDel);
		buttonPanel.add(btOK);

		JPanel rootPanel = new JPanel();
		rootPanel.setLayout(new BorderLayout());
		rootPanel.add(colorPanel, BorderLayout.CENTER);
		rootPanel.add(buttonPanel, BorderLayout.SOUTH);

		getContentPane().add(rootPanel);
	}

	protected void doEditTrain(Train train) {
		TrainDialog dialog = new TrainDialog(mainFrame, train);

		dialog.editTrain();
		
		if(!dialog.isCanceled) {
			Train editedTrain = dialog.getTrain();
			//没有改车次的情况，更新
			if(chart.isLoaded(editedTrain)) {
				chart.updateTrain(editedTrain);
			}
			//改了车次的情况，删掉原来的，增加新的
			else {
				chart.delTrain(train);
				chart.addTrain(editedTrain);
			}
			
			table.revalidate();
			mainFrame.chartView.repaint();
	        mainFrame.runView.refresh();
		}
	}
	
	protected void doNewTrain() {
		Train newTrain = new Train();
		newTrain.trainNameFull = "XXXX/YYYY";
		newTrain.trainNameDown = "DDDD";
		newTrain.trainNameUp   = "UUUU";
		newTrain.stopNum = 3;
		newTrain.stops[0] = new Stop(_("Departure"), "00:00", "00:00", false);
		newTrain.stops[1] = new Stop(_("Middle"), "00:00", "00:00", false);
		newTrain.stops[2] = new Stop(_("Terminal"), "00:00", "00:00", false);
		TrainDialog dialog = new TrainDialog(mainFrame, newTrain);

		dialog.editTrain();
		
		if(!dialog.isCanceled) {
			Train addingTrain = dialog.getTrain();
			if(chart.isLoaded(addingTrain)) {
				if(new YesNoBox(mainFrame, String.format(_("%s is already in the graph. Overwrite?"), addingTrain.getTrainName())).askForYes())
					chart.updateTrain(addingTrain);
			}
			else {
				chart.addTrain(addingTrain);
			}
			
			table.revalidate();
			mainFrame.chartView.repaint();
	        mainFrame.runView.refresh();
		}
	}

	public class ColorCellEditor extends AbstractCellEditor implements
			TableCellEditor, ActionListener {
		private static final long serialVersionUID = -5449669328932839469L;

		Color currentColor;

		JButton colorButton = new JButton();

		JColorChooser colorChooser;

		JDialog dialog;

		protected static final String EDIT = "edit";

		public ColorCellEditor() {
			//Set up the editor (from the table's point of view),
			//which is a button.
			//This button brings up the color chooser dialog,
			//which is the editor from the user's point of view.

			//Set up the dialog that the button brings up.
			colorChooser = new JColorChooser();
			dialog = JColorChooser.createDialog(TrainsDialog.this, "Select the color for the line",
					true, //modal
					colorChooser, this, //OK button handler
					null); //no CANCEL button handler
		}

		/**
		 * Handles events from the editor button and from
		 * the dialog's OK button.
		 */
		public void actionPerformed(ActionEvent e) {
			if (EDIT.equals(e.getActionCommand())) {
				//The user has clicked the cell, so
				//bring up the dialog.
				colorButton.setForeground(currentColor);
				colorChooser.setColor(currentColor);
				ETRC.setFont(dialog);
				dialog.setVisible(true);

				//Make the renderer reappear.
				fireEditingStopped();

			} else { //User pressed dialog's "OK" button.
				currentColor = colorChooser.getColor();
			}
		}

		//Implement the one CellEditor method that AbstractCellEditor doesn't.
		public Object getCellEditorValue() {
			return currentColor;
		}

		//Implement the one method defined by TableCellEditor.
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			currentColor = (Color) value;

			colorButton = new JButton() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void paint(Graphics g) {
					Rectangle clip = g.getClipBounds();
					g.setColor(currentColor);
					g.drawLine(clip.x, clip.y + clip.height - 3, clip.x
							+ clip.width, clip.y + clip.height - 3);
					super.paint(g);
				}
			};
			colorButton.setText(chart.trains[row].getTrainName(chart.circuit));
			colorButton.setForeground(currentColor);
			colorButton.setBackground(Color.white);

			colorButton.setHorizontalAlignment(SwingConstants.CENTER);
			colorButton.setVerticalAlignment(SwingConstants.CENTER);

			colorButton.setActionCommand(EDIT);
			colorButton.addActionListener(this);
			colorButton.setBorderPainted(false);

			return colorButton;
		}
	}

	public class ColorCellRenderer implements TableCellRenderer {
		/**
		 * For Table Cell
		 *
		 * @param table JTable
		 * @param value Object
		 * @param isSelected boolean
		 * @param hasFocus boolean
		 * @param row int
		 * @param column int
		 * @return Component
		 */
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (value == null)
				return new JLabel("null");
			if (!(value instanceof Color))
				return new JLabel("wrong");

			final Color color = (Color) value;
			final boolean selected = isSelected;
			final Color background = table.getSelectionBackground();

			JLabel colorLabel = new JLabel() {
				/**
				 * 
				 */
				private static final long serialVersionUID = -3994087135150899720L;

				public void paint(Graphics g) {
					Rectangle clip = g.getClipBounds();
					if (selected) {
						g.setColor(background);
						g.fillRect(clip.x, clip.y, clip.width, clip.height);
					}
					g.setColor(color);
					g.drawLine(clip.x, clip.y + clip.height - 3, clip.x
							+ clip.width, clip.y + clip.height - 3);
					super.paint(g);
				}
			};
			colorLabel.setText(chart.trains[row].getTrainName(chart.circuit));
			colorLabel.setForeground(color);
			colorLabel.setHorizontalAlignment(SwingConstants.CENTER);
			colorLabel.setVerticalAlignment(SwingConstants.CENTER);
			return colorLabel;
		}

	}

	public class TrainsTableModel extends AbstractTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3894954492927225594L;
		
		TrainsTableModel() {
		}

		/**
		 * getColumnCount
		 *
		 * @return int
		 */
		public int getColumnCount() {
			return 4;
		}

		/**
		 * getRowCount
		 *
		 * @return int
		 */
		public int getRowCount() {
			return chart.trainNum;
		}

		/**
		 * isCellEditable
		 *
		 * @param rowIndex int
		 * @param columnIndex int
		 * @return boolean
		 */
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 3;
		}

		/**
		 * getColumnClass
		 *
		 * @param columnIndex int
		 * @return Class
		 */
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 0:
			case 1:
			case 2:
				return String.class;
			case 3:
				return Color.class;
			default:
				return null;
			}
		}

		/**
		 * getValueAt
		 *
		 * @param rowIndex int
		 * @param columnIndex int
		 * @return Object
		 */
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {
			case 0:
				return chart.trains[rowIndex].getTrainName();
			case 1:
				return chart.trains[rowIndex].getStartStation();
			case 2:
				return chart.trains[rowIndex].getTerminalStation();
			case 3:
				return chart.trains[rowIndex].color;
			default:
				return null;
			}
		}

		/**
		 * setValueAt
		 *
		 * @param aValue Object
		 * @param rowIndex int
		 * @param columnIndex int
		 */
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if (columnIndex == 3) {
				chart.trains[rowIndex].color = (Color) aValue;
				//System.out.println("SET: " + ((Color)aValue));
				fireTableCellUpdated(rowIndex, columnIndex);
			}
		}

		/**
		 * getColumnName
		 *
		 * @param columnIndex int
		 * @return String
		 */
		public String getColumnName(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return _("Number");
			case 1:
				return _("Departure");
			case 2:
				return _("Terminal");
			case 3:
				return _("Color");
			default:
				return null;
			}
		}

		/**
		 * addTableModelListener
		 *
		 * @param l TableModelListener
		 */
		public void addTableModelListener(TableModelListener l) {
		}

		/**
		 * removeTableModelListener
		 *
		 * @param l TableModelListener
		 */
		public void removeTableModelListener(TableModelListener l) {
		}
	}

	public class TrainsTable extends JTable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5356639615057465995L;

		public TrainsTable() {
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			addMouseListener(new MouseAdapter() {  
				public void mouseClicked(MouseEvent e) {
					if   (e.getClickCount()   ==   2){ //双击  
	                    int row = getSelectedRow();
	                    doEditTrain(chart.trains[row]);
					}
				}
			});

		}

		public boolean isRowSelected(int row) {
			//      return chart.trains[row].equals(chart.getActiveTrain());
			return super.isRowSelected(row);
		}
	}
}
