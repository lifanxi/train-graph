package org.paradise.etrc.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import static org.paradise.etrc.ETRC._;
import org.paradise.etrc.ETRC;
import org.paradise.etrc.MainFrame;
import org.paradise.etrc.data.Circuit;
import org.paradise.etrc.data.Station;
import org.paradise.etrc.filter.CIRFilter;
import org.paradise.etrc.filter.CSVFilter;

public class CircuitEditDialog extends JDialog {
	private static final long serialVersionUID = 8501387955756137148L;
	MainFrame mainFrame;

	StationTable table;
	
	private JTextField tfName;

	public CircuitEditDialog(MainFrame _mainFrame, Circuit circuit) {
		super(_mainFrame, circuit.name, true);
		
		mainFrame = _mainFrame;
		
		table = new StationTable();
		table.setModel(new StationTableModel(circuit));

		try {
			jbInit();
			pack();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * doLoadCircuit
	 */
	private Circuit doLoadCircuit() {
		JFileChooser chooser = new JFileChooser();
		ETRC.setFont(chooser);

		chooser.setDialogTitle(_("Load Circuit"));
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setMultiSelectionEnabled(false);
		chooser.addChoosableFileFilter(new CSVFilter());
		chooser.addChoosableFileFilter(new CIRFilter());
		chooser.setFont(new java.awt.Font(_("FONT_NAME"), 0, 12));

		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
//			System.out.println(f);

			Circuit c = new Circuit();
			try {
				c.loadFromFile(f.getAbsolutePath());
			} catch (IOException ex) {
				System.err.println("Error: " + ex.getMessage());
			}
			return c;
		}
		else
			return null;
	}

	/**
	 * doSaveCircuit
	 */
	private void doSaveCircuit(Circuit circuit) {
		JFileChooser chooser = new JFileChooser();
		ETRC.setFont(chooser);

		chooser.setDialogTitle(_("Save Circuit"));
		chooser.setApproveButtonText(_("Save"));
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		chooser.setMultiSelectionEnabled(false);
		chooser.addChoosableFileFilter(new CIRFilter());
		chooser.setFont(new java.awt.Font(_("FONT_NAME"), 0, 12));
		chooser.setSelectedFile(new File(circuit.name));

		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String f = chooser.getSelectedFile().getAbsolutePath();
			if(!f.endsWith(".cir"))
				f += ".cir";

			try {
				circuit.name = tfName.getText();
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
				circuit.writeTo(out);
				
				out.close();
			} catch (IOException ex) {
				System.err.println("Error: " + ex.getMessage());
			}
		}
	}
	
	private void jbInit() throws Exception {
		table.setFont(new Font("Dialog", 0, 12));
		table.getTableHeader().setFont(new Font("Dialog", 0, 12));
//		JScrollPane spCircuit = new JScrollPane(table);

//		JPanel circuitPanel = new JPanel();
		//trainPanel.add(underColorPanel,  BorderLayout.SOUTH);

		JButton btOK = new JButton(_("OK"));
		btOK.setFont(new Font("dialog", 0, 12));
		btOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getCellEditor() != null)
					table.getCellEditor().stopCellEditing();
				
				// Normalize
				Circuit c = ((StationTableModel)table.getModel()).circuit;
				if (c.stationNum < 2)
				{
					new MessageBox(mainFrame, _("A circut must have at least two stations.")).showMessage();
					return;
				}
				int offset = c.stations[0].dist;
				if (offset != 0) {
					if (new YesNoBox(mainFrame, _("The distance of the first station is not zero, do normalization?")).askForYes()) {
						for (int i = 0; i < c.stationNum; ++i) {
							c.stations[i].dist -= offset; 
						}	
					}
				}
				c.length = c.stations[c.stationNum - 1].dist;
				
				mainFrame.chart.circuit = c;
				mainFrame.chart.circuit.name = tfName.getText();

				CircuitEditDialog.this.setVisible(false);
			}
		});

		JButton btCancel = new JButton(_("Cancel"));
		btCancel.setFont(new Font("dialog", 0, 12));
		btCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CircuitEditDialog.this.setVisible(false);
			}
		});

		JButton btLoad = new JButton(_("Load"));
		btLoad.setFont(new Font("dialog", 0, 12));
		btLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Circuit cir = doLoadCircuit();
				if(cir != null) {
					((StationTableModel)table.getModel()).circuit = cir;
					tfName.setText(cir.name);
					table.revalidate();
					mainFrame.isNewCircuit = true;
				}
			}
		});

		JButton btSave = new JButton(_("Save"));
		btSave.setFont(new Font("dialog", 0, 12));
		btSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doSaveCircuit(((StationTableModel)table.getModel()).circuit);
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(btLoad);
		buttonPanel.add(btSave);
		buttonPanel.add(btOK);
		buttonPanel.add(btCancel);

		JPanel rootPanel = new JPanel();
		rootPanel.setLayout(new BorderLayout());
		rootPanel.add(buildCircuitPanel(), BorderLayout.CENTER);
		rootPanel.add(buttonPanel, BorderLayout.SOUTH);

		getContentPane().add(rootPanel);
	}
	
	private JPanel buildCircuitPanel() {
		JButton btDel = new JButton(_("Delete"));
		btDel.setFont(new Font("dialog", 0, 12));
		btDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getCellEditor() != null)
					table.getCellEditor().stopCellEditing();

				((StationTableModel)table.getModel()).circuit.delStation(table.getSelectedRow());
				//System.out.println(((StationTableModel)table.getModel()).circuit);

				table.revalidate();
			}
		});

		JButton btAdd = new JButton(_("Add(Before)"));
		btAdd.setFont(new Font("dialog", 0, 12));
		btAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//        table.getCellEditor().stopCellEditing();
				Circuit cir = ((StationTableModel)table.getModel()).circuit;
				
				String name = _("Station");
				int dist = 0;
				int level = 2;
				boolean hide = false;
				cir.insertStation(new Station(name, dist, level, hide), table.getSelectedRow());
				//System.out.println(cir);

				table.revalidate();
			}
		});

		JButton btApp = new JButton(_("Add(After)"));
		btApp.setFont(new Font("dialog", 0, 12));
		btApp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//        table.getCellEditor().stopCellEditing();
				
				String name = _("Station");
				int dist = 0;
				int level = 2;
				boolean hide = false;
				
				int curIndex = table.getSelectedRow();
				if(curIndex < 0)
					return;
				
				Circuit cir = ((StationTableModel)table.getModel()).circuit;
				
				cir.insertStation(new Station(name, dist, level, hide), curIndex+1);
				//System.out.println(cir);

				table.revalidate();
			}
		});

		JLabel lbName = new JLabel(_("Circuit Name:"));
		lbName.setFont(new Font("dialog", 0, 12));
		
		tfName = new JTextField(12);
		tfName.setFont(new Font("dialog", 0, 12));
		tfName.setText(((StationTableModel)table.getModel()).circuit.name);
		
		JPanel namePanel = new JPanel();
		namePanel.add(lbName);
		namePanel.add(tfName);
	    namePanel.setBorder(new EmptyBorder(1,1,1,1));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(btAdd);
		buttonPanel.add(btApp);
		buttonPanel.add(btDel);

		JScrollPane spCircuit = new JScrollPane(table);

		JPanel circuitPanel = new JPanel();
		circuitPanel.setLayout(new BorderLayout());
		circuitPanel.add(spCircuit, BorderLayout.CENTER);
		circuitPanel.add(buttonPanel, BorderLayout.SOUTH);
		circuitPanel.add(namePanel, BorderLayout.NORTH);
//		circuitPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		
		return circuitPanel;
	}
	
	public void showDialog() {
		Dimension dlgSize = this.getPreferredSize();
		Dimension frmSize = mainFrame.getSize();
		Point loc = getLocation();
		this.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
				(frmSize.height - dlgSize.height) / 2 + loc.y);
		this.setModal(true);
		this.pack();
		this.setVisible(true);
	}

	public class StationTableModel extends AbstractTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6136704973824924463L;

		public Circuit circuit; 
		
		StationTableModel(Circuit _circuit) {
			circuit = _circuit.copy();
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
			return circuit.stationNum;
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
					|| (columnIndex == 2)|| (columnIndex == 3);
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
				return String.class;
			case 1:
			case 2:
				return Integer.class;
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
				return circuit.stations[rowIndex].name;
			case 1:
				return new Integer(circuit.stations[rowIndex].dist);
			case 2:
				return new Integer(circuit.stations[rowIndex].level);
			case 3:
				return new Boolean(circuit.stations[rowIndex].hide);
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
			switch (columnIndex) {
			case 0:
				circuit.stations[rowIndex].name = (String) aValue;
				break;
			case 1:
				circuit.stations[rowIndex].dist = ((Integer) aValue).intValue();
				if (rowIndex == circuit.stationNum - 1)
					circuit.length = ((Integer) aValue).intValue();
				break;
			case 2:
				circuit.stations[rowIndex].level = ((Integer) aValue).intValue();
				break;
			case 3:
				circuit.stations[rowIndex].hide = ((Boolean) aValue).booleanValue();
				break;
			default:
			}
			
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
				return _("Station");
			case 1:
				return _("Distance");
			case 2:
				return _("Level");
			case 3:
				return _("Hidden");
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

	public class StationTable extends JTable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8627865729136595002L;

		public StationTable() {
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}

		public Dimension getPreferredScrollableViewportSize() {
			int r = this.getRowCount();
			int h = this.getRowHeight() * Math.min(r, 15);
			int w = super.getPreferredScrollableViewportSize().width;
			return new Dimension(w, h);
		}

		public boolean isRowSelected(int row) {
			//      return chart.trains[row].equals(chart.getActiveTrain());
			return super.isRowSelected(row);
		}
	}
}
