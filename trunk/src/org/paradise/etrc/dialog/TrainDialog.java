package org.paradise.etrc.dialog;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelListener;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.paradise.etrc.ETRC;
import org.paradise.etrc.MainFrame;
import org.paradise.etrc.data.*;
import org.paradise.etrc.filter.CSVFilter;
import org.paradise.etrc.filter.TRFFilter;

/**
 * @author lguo@sina.com
 * @version 1.0
 */

public class TrainDialog extends JDialog {
	private static final long serialVersionUID = 4016578609920190434L;

	private TrainTable table;
	private JTextField tfNameU;
	private JTextField tfNameD;
	private JTextField tfName;
	
	private MainFrame mainFrame;
	
	public boolean isCanceled = false;
	
	public TrainDialog(MainFrame _mainFrame, Train _train) {
		super(_mainFrame, _train.getTrainName(), true);
		
		mainFrame = _mainFrame;

		table = new TrainTable();
		table.setModel(new TrainTableModel(_train));

		try {
			jbInit();
			pack();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public Train getTrain() {
		Train train = ((TrainTableModel)table.getModel()).myTrain;
//		train.startStation = train.stops[0].stationName;
//		train.terminalStation = train.stops[train.stopNum - 1].stationName;
		train.trainNameDown = tfNameD.getText().trim();
		train.trainNameUp = tfNameU.getText().trim();
		train.trainNameFull = tfName.getText().trim();
		return train;
	}

	private void jbInit() throws Exception {
		table.setFont(new Font("Dialog", 0, 12));
		table.getTableHeader().setFont(new Font("Dialog", 0, 12));
		
		JButton btColor = new JButton("颜 色"); 
		btColor.setFont(new Font("dialog", 0, 12));
		btColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getCellEditor() != null)
					table.getCellEditor().stopCellEditing();
				
				doSetColor(getTrain());
			}
		});

		JButton btLoad = new JButton("读 取");
		btLoad.setFont(new Font("dialog", 0, 12));
		btLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getCellEditor() != null)
					table.getCellEditor().stopCellEditing();
				
				Train loadingTrain = doLoadTrain();
				if(loadingTrain != null) {
					if(loadingTrain.color == null) {
						Color c = ((TrainTableModel)table.getModel()).myTrain.color;
						loadingTrain.color = c;
					}
					
					((TrainTableModel)table.getModel()).myTrain = loadingTrain;
					tfName.setText(loadingTrain.getTrainName());
					tfNameD.setText(loadingTrain.trainNameDown);
					tfNameU.setText(loadingTrain.trainNameUp);
				}
			}
		});

		JButton btSave = new JButton("保 存");
		btSave.setFont(new Font("dialog", 0, 12));
		btSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getCellEditor() != null)
					table.getCellEditor().stopCellEditing();

				Train savingTrain = ((TrainTableModel)table.getModel()).myTrain;
				savingTrain.trainNameDown = tfNameD.getText().trim();
				savingTrain.trainNameUp = tfNameU.getText().trim();
				savingTrain.trainNameFull = tfName.getText().trim();

				doSaveTrain(savingTrain);
			}
		});

		JButton btOK = new JButton("确 定");
		btOK.setFont(new Font("dialog", 0, 12));
		btOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getCellEditor() != null)
					table.getCellEditor().stopCellEditing();

				Train train = ((TrainTableModel)table.getModel()).myTrain;
				train.trainNameDown = tfNameD.getText().trim();
				train.trainNameUp = tfNameU.getText().trim();
				train.trainNameFull = tfName.getText().trim();

				isCanceled = false;
//				TrainDialog.this.setVisible(false);
				TrainDialog.this.dispose();
			}
		});

		JButton btCancel = new JButton("取 消");
		btCancel.setFont(new Font("dialog", 0, 12));
		btCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				isCanceled = true;
//				TrainDialog.this.setVisible(false);
				TrainDialog.this.dispose();
			}
		});


		JPanel buttonPanel = new JPanel();
		buttonPanel.add(btColor);
		buttonPanel.add(btLoad);
		buttonPanel.add(btSave);
		buttonPanel.add(btOK);
		buttonPanel.add(btCancel);

		JPanel rootPanel = new JPanel();
		rootPanel.setLayout(new BorderLayout());
		rootPanel.add(buildTrainPanel(), BorderLayout.CENTER);
		rootPanel.add(buttonPanel, BorderLayout.SOUTH);

		getContentPane().add(rootPanel);
	}
	
	private JPanel buildTrainPanel() {
		JButton btDel = new JButton("删 除");
		btDel.setFont(new Font("dialog", 0, 12));
		btDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getCellEditor() != null)
					table.getCellEditor().stopCellEditing();

				((TrainTableModel)table.getModel()).myTrain.delStop(table.getSelectedRow());

				table.revalidate();
			}
		});

		JButton btAdd = new JButton("增加(前)");
		btAdd.setFont(new Font("dialog", 0, 12));
		btAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//        table.getCellEditor().stopCellEditing();
				String name = "站名";
				String arrive = "00:00";
				String leave = "00:00";
				((TrainTableModel)table.getModel()).myTrain.insertStop(new Stop(name, arrive, leave, false), 
						table.getSelectedRow());

				table.revalidate();
			}
		});

		JButton btApp = new JButton("增加(后)");
		btApp.setFont(new Font("dialog", 0, 12));
		btApp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//        table.getCellEditor().stopCellEditing();
				int curIndex = table.getSelectedRow();
				if(curIndex<0)
					return;
				
				String name = "站名";
				String arrive = "00:00";
				String leave = "00:00";
				((TrainTableModel)table.getModel()).myTrain.insertStop(new Stop(name, arrive, leave, false), curIndex+1);

				table.revalidate();
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(btAdd);
		buttonPanel.add(btApp);
		buttonPanel.add(btDel);
		
		JLabel lbNameU = new JLabel("上行车次：");
		lbNameU.setFont(new Font("dialog", 0, 12));
		JLabel lbNameD = new JLabel("本区间下行车次：");
		lbNameD.setFont(new Font("dialog", 0, 12));
		JLabel lbName = new JLabel("车次：");
		lbName.setFont(new Font("dialog", 0, 12));
		
		tfNameU = new JTextField(4);
		tfNameU.setFont(new Font("dialog", 0, 12));
		tfNameU.setText(((TrainTableModel)table.getModel()).myTrain.trainNameUp);
		tfNameD = new JTextField(4);
		tfNameD.setFont(new Font("dialog", 0, 12));
		tfNameD.setText(((TrainTableModel)table.getModel()).myTrain.trainNameDown);
		tfName = new JTextField(12);
		tfName.setFont(new Font("dialog", 0, 12));
		tfName.setText(((TrainTableModel)table.getModel()).myTrain.getTrainName());
				
		JPanel namePanel = new JPanel();
		namePanel.setBorder(new EmptyBorder(1,1,1,1));
		namePanel.add(lbName);
		namePanel.add(tfName);
		namePanel.add(lbNameD);
		namePanel.add(tfNameD);
		namePanel.add(lbNameU);
		namePanel.add(tfNameU);

		JScrollPane spTrain = new JScrollPane(table);

		JPanel trainPanel = new JPanel();
		trainPanel.setLayout(new BorderLayout());
		trainPanel.add(namePanel, BorderLayout.NORTH);
		trainPanel.add(spTrain, BorderLayout.CENTER);
		trainPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		return trainPanel;
	}

	protected void doSaveTrain(Train savingTrain) {
		JFileChooser chooser = new JFileChooser();
		ETRC.setFont(chooser);

		chooser.setDialogTitle("保存车次");
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		chooser.setMultiSelectionEnabled(false);
		chooser.addChoosableFileFilter(new CSVFilter());
		chooser.addChoosableFileFilter(new TRFFilter());
		chooser.setFont(new java.awt.Font("宋体", 0, 12));
		chooser.setApproveButtonText("保存");
		
		String savingName = savingTrain.getTrainName().replace('/', '_'); 
		chooser.setSelectedFile(new File(savingName));

		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String f = chooser.getSelectedFile().getAbsolutePath();
			if(!f.endsWith(".trf"))
				f += ".trf";

			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(f));
				savingTrain.writeTo(out);
				
				out.close();
			} catch (IOException ex) {
				System.err.println("Error: " + ex.getMessage());
			}
		}
	}

	protected Train doLoadTrain() {
		JFileChooser chooser = new JFileChooser();
		ETRC.setFont(chooser);

		chooser.setDialogTitle("载入车次");
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setMultiSelectionEnabled(false);
		chooser.addChoosableFileFilter(new CSVFilter());
		chooser.addChoosableFileFilter(new TRFFilter());
		chooser.setFont(new java.awt.Font("宋体", 0, 12));

		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();

			Train loadingTrain = new Train();
			try {
				loadingTrain.loadFromFile(f.getAbsolutePath());
			} catch (IOException ex) {
				System.err.println("Error: " + ex.getMessage());
			}

			return loadingTrain;
		}

		return null;
	}

	private void doSetColor(final Train train) {
		final JColorChooser colorChooser = new JColorChooser();
		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				train.color = colorChooser.getColor();
				mainFrame.chartView.panelLines.repaint();
			}
		};

		JDialog dialog = JColorChooser.createDialog(mainFrame,
				"请选择行车线颜色", true, // modal
				colorChooser, listener, // OK button handler
				null); // no CANCEL button handler
		ETRC.setFont(dialog);
		
		colorChooser.setColor(train.color);

		Dimension dlgSize = dialog.getPreferredSize();
		Dimension frmSize = mainFrame.getSize();
		Point loc = mainFrame.getLocation();
		dialog.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
				(frmSize.height - dlgSize.height) / 2 + loc.y);
		dialog.setVisible(true);
	}

	public void editTrain() {
		Dimension dlgSize = getPreferredSize();
		Dimension frmSize = mainFrame.getSize();
		Point loc = mainFrame.getLocation();
		setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
				(frmSize.height - dlgSize.height) / 2 + loc.y);
		setVisible(true);
	}

	public class TrainTableModel extends AbstractTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1014817974495127589L;
		
		Train myTrain;

		TrainTableModel(Train _train) {
			myTrain = _train.copy();
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
			return myTrain.stopNum;
		}

		/**
		 * isCellEditable
		 *
		 * @param rowIndex int
		 * @param columnIndex int
		 * @return boolean
		 */
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return (columnIndex == 0) || (columnIndex == 1)	
				|| (columnIndex == 2) || (columnIndex == 3);
		}

		/**
		 * getColumnClass
		 *
		 * @param columnIndex int
		 * @return Class
		 */
		public Class getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 0:
			case 1:
			case 2:
				return String.class;
			case 3:
				return Boolean.class;
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
				return myTrain.stops[rowIndex].stationName;
			case 1:
				return myTrain.stops[rowIndex].arrive;
			case 2:
				return myTrain.stops[rowIndex].leave;
			case 3:
				return Boolean.valueOf(myTrain.stops[rowIndex].isPassenger);
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
//			SimpleDateFormat df = new SimpleDateFormat("H:mm");
//			try {
				switch (columnIndex) {
				case 0:
					myTrain.stops[rowIndex].stationName = (String) aValue;
					break;
				case 1:
//					train.stops[rowIndex].arrive = df.parse((String) aValue);
					myTrain.stops[rowIndex].arrive = (String) aValue;
					break;
				case 2:
//					train.stops[rowIndex].leave = df.parse((String) aValue);
					myTrain.stops[rowIndex].leave = (String) aValue;
					break;
				case 3:
					myTrain.stops[rowIndex].isPassenger = ((Boolean) aValue).booleanValue();
					break;
				default:
				}
//			} catch (ParseException ex) {
//				ex.printStackTrace();
//			}
			fireTableCellUpdated(rowIndex, columnIndex);
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
				return "车站";
			case 1:
				return "到点";
			case 2:
				return "发点";
			case 3:
				return "办客";
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

	public class TrainTable extends JTable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2242015871442153005L;

		public TrainTable() {
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}

		public Dimension getPreferredScrollableViewportSize() {
			int h = this.getRowHeight() * 12;
			int w = super.getPreferredScrollableViewportSize().width;
			return new Dimension(w, h);
		}

		public boolean isRowSelected(int row) {
			//      return chart.trains[row].equals(chart.getActiveTrain());
			return super.isRowSelected(row);
		}
	}
}
