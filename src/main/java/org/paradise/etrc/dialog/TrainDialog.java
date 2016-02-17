package org.paradise.etrc.dialog;

import org.paradise.etrc.ETRC;
import org.paradise.etrc.MainFrame;
import org.paradise.etrc.data.Stop;
import org.paradise.etrc.data.Train;
import org.paradise.etrc.filter.CSVFilter;
import org.paradise.etrc.filter.TRFFilter;
import org.paradise.etrc.wizard.addtrain.TrainTable;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import static org.paradise.etrc.ETRC._;

//import org.paradise.etrc.wizard.addtrain.TrainTableModel;


/**
 * @author lguo@sina.com
 * @version 1.0
 */

public class TrainDialog extends JDialog {
    private static final long serialVersionUID = 4016578609920190434L;

    private TrainTable table;
    //private TrainTableModel model;
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
        //model = new TrainTableModel(_train);
        //table.setModel(model);
        try {
            jbInit();
            pack();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Train getTrain() {
        Train train = ((TrainTableModel) table.getModel()).myTrain;
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

        JButton btColor = new JButton(_("Color"));
        btColor.setFont(new Font("dialog", 0, 12));
        btColor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (table.getCellEditor() != null)
                    table.getCellEditor().stopCellEditing();

                doSetColor(getTrain());
            }
        });

        JButton btLoad = new JButton(_("Load"));
        btLoad.setFont(new Font("dialog", 0, 12));
        btLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (table.getCellEditor() != null)
                    table.getCellEditor().stopCellEditing();

                Train loadingTrain = doLoadTrain();
                if (loadingTrain != null) {
                    if (loadingTrain.color == null) {
                        Color c = ((TrainTableModel) table.getModel()).myTrain.color;
                        loadingTrain.color = c;
                    }

                    ((TrainTableModel) table.getModel()).myTrain = loadingTrain;
                    tfName.setText(loadingTrain.getTrainName());
                    tfNameD.setText(loadingTrain.trainNameDown);
                    tfNameU.setText(loadingTrain.trainNameUp);
                }
            }
        });

        JButton btSave = new JButton(_("Save"));
        btSave.setFont(new Font("dialog", 0, 12));
        btSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (table.getCellEditor() != null)
                    table.getCellEditor().stopCellEditing();

                Train savingTrain = ((TrainTableModel) table.getModel()).myTrain;
                savingTrain.trainNameDown = tfNameD.getText().trim();
                savingTrain.trainNameUp = tfNameU.getText().trim();
                savingTrain.trainNameFull = tfName.getText().trim();

                doSaveTrain(savingTrain);
            }
        });

        JButton btOK = new JButton(_("OK"));
        btOK.setFont(new Font("dialog", 0, 12));
        btOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (table.getCellEditor() != null)
                    table.getCellEditor().stopCellEditing();

                Train train = ((TrainTableModel) table.getModel()).myTrain;
                train.trainNameDown = tfNameD.getText().trim();
                train.trainNameUp = tfNameU.getText().trim();
                train.trainNameFull = tfName.getText().trim();

                isCanceled = false;
//				TrainDialog.this.setVisible(false);
                TrainDialog.this.dispose();
            }
        });

        JButton btCancel = new JButton(_("Cancel"));
        btCancel.setFont(new Font("dialog", 0, 12));
        btCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isCanceled = true;
//				TrainDialog.this.setVisible(false);
                TrainDialog.this.dispose();
            }
        });

        JButton btWeb = new JButton(_("Get From Web"));
        btWeb.setFont(new Font("dialog", 0, 12));
        btWeb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (tfName.getText().trim().equals("")) {
                    new MessageBox(_("Must input train number before trying to get data from web.")).showMessage();
                    return;
                }
                if (table.getCellEditor() != null)
                    table.getCellEditor().stopCellEditing();


                String proxyAddress = mainFrame.prop.getProperty(MainFrame.Prop_HTTP_Proxy_Server);
                int proxyPort = 0;
                try {
                    proxyPort = Integer.parseInt(mainFrame.prop.getProperty(MainFrame.Prop_HTTP_Proxy_Port));
                } catch (NumberFormatException ex) {
                    proxyPort = 0;
                }
                Train loadingTrain = doLoadTrainFromWeb(tfName.getText().trim(), proxyAddress, proxyPort);
                if (loadingTrain != null) {
                    if (loadingTrain.color == null) {
                        Color c = ((TrainTableModel) table.getModel()).myTrain.color;
                        loadingTrain.color = c;
                    }

                    ((TrainTableModel) table.getModel()).myTrain = loadingTrain;
                    tfName.setText(loadingTrain.getTrainName());
                    tfNameD.setText(loadingTrain.trainNameDown);
                    tfNameU.setText(loadingTrain.trainNameUp);

                    table.revalidate();
                    table.updateUI();
                } else {
                    new MessageBox(_("Unable to get train information from web.")).showMessage();
                }

            }
        });


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btColor);
        buttonPanel.add(btLoad);
        buttonPanel.add(btSave);
        buttonPanel.add(btOK);
        buttonPanel.add(btCancel);
        //buttonPanel.add(btWeb);// joe commented out on 2015/06/10 due to now 12306 interface can't work!

        JPanel rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout());
        rootPanel.add(buildTrainPanel(), BorderLayout.CENTER);
        rootPanel.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().add(rootPanel);
    }

    private JPanel buildTrainPanel() {
        JButton btDel = new JButton(_("Delete"));
        btDel.setFont(new Font("dialog", 0, 12));
        btDel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (table.getCellEditor() != null)
                    table.getCellEditor().stopCellEditing();

                ((TrainTableModel) table.getModel()).myTrain.delStop(table.getSelectedRow());

                table.revalidate();
            }
        });

        JButton btAdd = new JButton(_("Add(Before)"));
        btAdd.setFont(new Font("dialog", 0, 12));
        btAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //        table.getCellEditor().stopCellEditing();
                String name = _("Station");
                String arrive = "00:00";
                String leave = "00:00";
                ((TrainTableModel) table.getModel()).myTrain.insertStop(new Stop(name, arrive, leave, false, "0"),
                        table.getSelectedRow());

                table.revalidate();
            }
        });

        JButton btApp = new JButton(_("Add(After)"));
        btApp.setFont(new Font("dialog", 0, 12));
        btApp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //        table.getCellEditor().stopCellEditing();
                int curIndex = table.getSelectedRow();
                if (curIndex < 0)
                    return;

                String name = _("Station");
                String arrive = "00:00";
                String leave = "00:00";
                ((TrainTableModel) table.getModel()).myTrain.insertStop(new Stop(name, arrive, leave, false, "0"), curIndex + 1);

                table.revalidate();
            }
        });

        JButton btFil = new JButton(_("Interpolate Timetable"));
        //JButton btFil = new JButton(_("推算时刻"));
        btFil.setFont(new Font("dialog", 0, 12));
        btFil.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //如果没有下面两行，则补全时刻被单击前选中的单元格不会被更新！
                if (table.getCellEditor() != null)
                    table.getCellEditor().stopCellEditing();
                int curIndex = table.getSelectedRow();

                if (curIndex < 1) {
                    System.out.println("当前行为首行，无法计算！");
                    return;
                }


                int t1 = Train.trainTimeToInt(((TrainTableModel) table.getModel()).myTrain.stops[curIndex - 1].leave);
                int t2 = Train.trainTimeToInt(((TrainTableModel) table.getModel()).myTrain.stops[curIndex + 1].arrive);

                int d1 = Integer.parseInt(((TrainTableModel) table.getModel()).myTrain.stops[curIndex - 1].licheng);
                int d2 = Integer.parseInt(((TrainTableModel) table.getModel()).myTrain.stops[curIndex + 1].licheng);

                if (d1 >= d2) {
                    System.out.println("错误：出发站里程大于等于到达站里程");
                    return;
                }
                if (t2 < t1) {
                    t2 = t2 + 24 * 60;
                }
                double mySpeed = ((double) (d2 - d1) / (double) (t2 - t1));
                System.out.println(d2 + " " + d1 + " " + t2 + " " + t1 + " " + mySpeed + "km/min");
                //如果里程仍然为0（包括输入未确认），则提示需要更新里程
                String aa = ((TrainTableModel) table.getModel()).myTrain.stops[curIndex].licheng;
                int d;
                int t;
                if (aa.equalsIgnoreCase("0")) {
                    System.out.println("错误：通过站里程为0");
                    return;
                }

                //如果里程含有#号，则为相对里程，用于计算
                if (aa.indexOf("#") >= 0) {
                    aa = aa.replaceAll("#", "");
                    d = Integer.parseInt(aa);

                    if (d >= (d2 - d1)) {
                        System.out.println("错误：通过站相对里程大于出发站到达站区间里程");
                        return;
                    }
                    if (d <= 0) {
                        System.out.println("错误：通过站相对里程为非正数");
                        return;
                    }

                    t = (int) ((d - 0) / mySpeed + t1);
                } else
                //里程为绝对里程，需要计算
                {
                    aa = aa;
                    d = Integer.parseInt(aa);

                    if (d <= d1) {
                        System.out.println("错误：通过站里程小于等于出发站里程");
                        return;
                    }
                    if (d >= d2) {
                        System.out.println("错误：通过站里程大于等于到达站里程");
                        return;
                    }

                    t = (int) ((d - d1) / mySpeed + t1);

                }
                System.out.println(d);
                System.out.println(t);


                if (t > (24 * 60)) {
                    t = t - 24 * 60;
                }
                System.out.println(d2 + " " + d1 + " " + t2 + " " + t1 + " " + mySpeed + "km/min, pass time:" + t);
                String oo = Train.intToTrainTime(t);
                System.out.println(oo);
                //String oo = "13:00";
                ((TrainTableModel) table.getModel()).myTrain.stops[curIndex].arrive = oo;
                //model.myTrain.stops[curIndex].leave = model.myTrain.stops[curIndex].arrive;
                //model.fireTableDataChanged();		
                ((TrainTableModel) table.getModel()).fireTableDataChanged();
                ((TrainTableModel) table.getModel()).myTrain.stops[curIndex].leave = oo;
                ((TrainTableModel) table.getModel()).fireTableDataChanged();
                //table.revalidate();
                //如果没有updateui，则只有单击别的行后当前被推算时刻行的时刻才会显示更新后的时刻。
                table.updateUI();
                //table.setModel(model);
                //curIndex = table.getSelectedRow();
                //table.getSelectionModel().setSelectionInterval(curIndex-1, curIndex);

            }
        });


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btAdd);
        buttonPanel.add(btApp);
        buttonPanel.add(btDel);
        buttonPanel.add(btFil);

        JLabel lbNameU = new JLabel(_("Up-going:"));
        lbNameU.setFont(new Font("dialog", 0, 12));
        JLabel lbNameD = new JLabel(_("Down-going"));
        lbNameD.setFont(new Font("dialog", 0, 12));
        JLabel lbName = new JLabel(_("Train number:"));
        lbName.setFont(new Font("dialog", 0, 12));

        tfNameU = new JTextField(4);
        tfNameU.setFont(new Font("dialog", 0, 12));
        tfNameU.setText(((TrainTableModel) table.getModel()).myTrain.trainNameUp);
        tfNameD = new JTextField(4);
        tfNameD.setFont(new Font("dialog", 0, 12));
        tfNameD.setText(((TrainTableModel) table.getModel()).myTrain.trainNameDown);
        tfName = new JTextField(12);
        tfName.setFont(new Font("dialog", 0, 12));
        tfName.setText(((TrainTableModel) table.getModel()).myTrain.getTrainName());

        JPanel namePanel = new JPanel();
        namePanel.setBorder(new EmptyBorder(1, 1, 1, 1));
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

        chooser.setDialogTitle(_("Save Train"));
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setMultiSelectionEnabled(false);
        chooser.addChoosableFileFilter(new CSVFilter());
        chooser.addChoosableFileFilter(new TRFFilter());
        chooser.setFont(new java.awt.Font(_("FONT_NAME"), 0, 12));
        chooser.setApproveButtonText(_("Save "));

        String savingName = savingTrain.getTrainName().replace('/', '_');
        chooser.setSelectedFile(new File(savingName));

        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String f = chooser.getSelectedFile().getAbsolutePath();
            if (!f.endsWith(".trf"))
                f += ".trf";

            try {
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
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

        chooser.setDialogTitle(_("Load Train Information"));
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setMultiSelectionEnabled(false);
        chooser.addChoosableFileFilter(new CSVFilter());
        chooser.addChoosableFileFilter(new TRFFilter());
        chooser.setFont(new java.awt.Font(_("FONT_NAME"), 0, 12));

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

    public static Train doLoadTrainFromWeb(String code, String proxyAddress, int proxyPort) {
        Train train = new Train();
        train.trainNameFull = "";
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String date = format.format(now);

        if (code.indexOf("/") != -1)
            code = code.split("/")[0];

        String getData = "cxlx=cc&date=" + date + "&trainCode=" + code;
        try {
            Proxy proxy = null;
            if (proxyAddress.equals("") || proxyPort == 0)
                proxy = Proxy.NO_PROXY;
            else
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyAddress, proxyPort));
            URL url = new URL("http://dynamic.12306.cn/TrainQuery/skbcx.jsp?" + getData);

            URLConnection conn = url.openConnection(proxy);
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            conn.setRequestProperty("Referer", "http://dynamic.12306.cn/TrainQuery/trainInfoByStation.jsp");

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            // wr.write(postData);
            wr.flush();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            conn.getInputStream(), "UTF-8"));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                if ((inputLine.indexOf("//") == -1) && (inputLine.indexOf("mygrid.addRow") != -1)) {
                    String[] items = inputLine.split(",");
                    // if (items[4].indexOf("----") != -1) items[4] = items[5];
                    // if (items[5].indexOf("----") != -1) items[5] = items[4];
                    Stop stop = new Stop(items[2].split("\\^")[0].trim(), items[4].trim(), items[5].trim(), true, "0");
                    train.appendStop(stop);
                    String c = items[3].trim();
                    if ((c.charAt(c.length() - 1) - '0') % 2 == 0) {
                        if (train.trainNameUp.equals(""))
                            train.trainNameUp = c;
                    } else {
                        if (train.trainNameDown.equals(""))
                            train.trainNameDown = c;
                    }
                    if (train.trainNameFull.indexOf(c) == -1) {
                        if (train.trainNameFull.equals("")) {
                            train.trainNameFull = c;
                        } else {
                            train.trainNameFull += ("/" + c);
                        }
                    }
                }
            }
            in.close();

            String[] names = train.trainNameFull.split("/");
            for (int i = 0; i < names.length; ++i) {
                if ((names[i].charAt(names[i].length() - 1) - '0') % 2 == 0) {
                    train.trainNameUp = names[i];
                } else {
                    train.trainNameDown = names[i];
                }
            }

            // Fix wrong start/end time
            train.stops[0].arrive = train.stops[0].leave;
            train.stops[train.stopNum - 1].leave = train.stops[train.stopNum - 1].arrive;

            return train;
        } catch (Exception e) {
            return null;
        }
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
                _("Select the color for the line"), true, // modal
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
            return 5;
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
         * @param rowIndex    int
         * @param columnIndex int
         * @return boolean
         */
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return (columnIndex == 0) || (columnIndex == 1)
                    || (columnIndex == 2) || (columnIndex == 3) || (columnIndex == 4);
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
                    return Boolean.class;
                case 4:
                    return String.class;
                default:
                    return null;
            }
        }

        /**
         * getValueAt
         *
         * @param rowIndex    int
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
                case 4:
                    return myTrain.stops[rowIndex].licheng;
                //return "1xx";
                default:
                    return null;
            }
        }

        /**
         * setValueAt
         *
         * @param aValue      Object
         * @param rowIndex    int
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
                case 4:
                    myTrain.stops[rowIndex].licheng = (String) aValue;
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
                    return _("Station");
                case 1:
                    return _("Arrival");
                case 2:
                    return _("Leave");
                case 3:
                    return _("Passenger");
                case 4:
                    return _("Distance");
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

    // 2015-09-17 注释掉下面的TrainTable class定义，采用import org.paradise.etrc.wizard.addtrain.TrainTable;可以实现表格内容居中对齐！
    //与添加车次向导中内置时刻表查询所得表格样式一致！
    // public class TrainTable extends JTable {
    // /**
    // *
    // */
    // private static final long serialVersionUID = 2242015871442153005L;

    // public TrainTable() {
    // setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    // }

    // public Dimension getPreferredScrollableViewportSize() {
    // int h = this.getRowHeight() * 12;
    // int w = super.getPreferredScrollableViewportSize().width;
    // return new Dimension(w, h);
    // }

    // public boolean isRowSelected(int row) {
    //	    // return chart.trains[row].equals(chart.getActiveTrain());
    // return super.isRowSelected(row);
    // }
    // }
}
