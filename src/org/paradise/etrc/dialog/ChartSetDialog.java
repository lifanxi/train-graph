package org.paradise.etrc.dialog;

import org.paradise.etrc.MainFrame;
import org.paradise.etrc.data.Chart;
import org.paradise.etrc.view.chart.ChartView;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import static org.paradise.etrc.ETRC._;

public class ChartSetDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private MainFrame mainFrame;
    private JTabbedPane tbPane;
    private JLabel statusBar;

    private JTextField d0;
    private JTextField d1;
    private JTextField d2;

    private JTextField t0;
    private JTextField t1;
    private JTextField t2;

    private static String defaultStatus = _("Settings for Train Graph");

    public ChartSetDialog(MainFrame _mainFrame) {
        super(_mainFrame, _("Settings for Train Graph"), false);
        mainFrame = _mainFrame;

        init();
    }

    private void init() {
        tbPane = new JTabbedPane();

        Chart chart = mainFrame.chart;

        d0 = createJTextField("" + chart.distScale);
        d1 = createJTextField("" + chart.displayLevel);
        d2 = createJTextField("" + chart.boldLevel);

        t0 = createJTextField("" + chart.startHour);
        t1 = createJTextField("" + chart.minuteScale);
        t2 = createJTextField("" + chart.timeInterval);

        JPanel distPanel = creatJPanel(
                createJLabelL(_("Pixels per km:")), d0, createJLabelR(" "),
                createJLabelL(_("Display level:")), d1, createJLabelR(" "),
                createJLabelL(_("Bold line level:")), d2, createJLabelR(" "),
                createJLabelM(_(" The highest station level is 0")));

        JPanel timePanel = creatJPanel(
                createJLabelL(_("Time for 0 pos:")), t0, createJLabelR(" "),
                createJLabelL(_("Pixel per min:")), t1, createJLabelR(" "),
                createJLabelL(_("Y-axis gap:")), t2, createJLabelR("min"),
                createJLabelM(_(" Must be a divider of 60")));


        tbPane.add(_("Distance bar"), distPanel);
        tbPane.add(_("Timeline"), timePanel);

        statusBar = new JLabel(defaultStatus);
        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());

        this.getRootPane().setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        this.setResizable(false);

        this.setLayout(new BorderLayout());
        this.add(tbPane, BorderLayout.NORTH);
        this.add(createDownPanel(), BorderLayout.CENTER);
        this.add(statusBar, BorderLayout.SOUTH);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();

        JPanel panelBT = new JPanel();
        JButton btDefault = createJButton(_("Default"));
        JButton btOK = createJButton(_("Set"));

        panelBT.add(btDefault);
        panelBT.add(btOK);

        panel.setLayout(new BorderLayout());
        panel.add(panelBT, BorderLayout.EAST);

        return panel;
    }

    private JPanel createDownPanel() {
        JPanel panel = new JPanel();

        JCheckBox cbDrawPoint;
        cbDrawPoint = new JCheckBox();
        cbDrawPoint.setFont(new java.awt.Font("Dialog", 0, 12));
        cbDrawPoint.setText(_("Always highlight terminals"));
        //cbDrawPoint.setText(_("始终突出画到发点"));
        cbDrawPoint.setSelected(mainFrame.chartView.isDrawNormalPoint);
        cbDrawPoint.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (((JCheckBox) e.getSource()).isSelected())
                    mainFrame.chartView.isDrawNormalPoint = true;
                else
                    mainFrame.chartView.isDrawNormalPoint = false;

                mainFrame.chartView.repaint();
            }
        });

        JCheckBox cbUnderColor;
        JCheckBox ShowDistance;
        JCheckBox DashSiding;
        JCheckBox NightMode;
        JCheckBox StartEnd;
        JCheckBox DetailTime;
        JCheckBox halfHourDashGrid;
        //用水印颜色显示反向运行线
        cbUnderColor = new JCheckBox();
        cbUnderColor.setFont(new java.awt.Font("Dialog", 0, 12));
        cbUnderColor.setText(_("Enable watermark display Opposite Trains"));
        //cbUnderColor.setText(_("水印显示反向车次"));
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


        //动态图显示里程信息
        ShowDistance = new JCheckBox();
        ShowDistance.setFont(new java.awt.Font("Dialog", 0, 12));
        //ShowDistance.setText(_("动态图中显示当前车次里程信息"));
        ShowDistance.setText(_("Show Distance Info in Dynamic View"));
        ShowDistance.setSelected(mainFrame.chartView.SHOWDISTANCE == true);
        ShowDistance.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (((JCheckBox) e.getSource()).isSelected())
                    mainFrame.chartView.SHOWDISTANCE = true;
                else
                    mainFrame.chartView.SHOWDISTANCE = false;

                mainFrame.chartView.repaint();
            }
        });
        //虚线显示侧线车站运行线
        DashSiding = new JCheckBox();
        DashSiding.setFont(new java.awt.Font("Dialog", 0, 12));
        //DashSiding.setText(_("虚线显示侧线车站运行线"));
        DashSiding.setText(_("Use DashLine for trains lines to siding station"));
        DashSiding.setSelected(mainFrame.chartView.DASHSIDING == true);
        DashSiding.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (((JCheckBox) e.getSource()).isSelected())
                    mainFrame.chartView.DASHSIDING = true;
                else
                    mainFrame.chartView.DASHSIDING = false;

                mainFrame.chartView.repaint();
            }
        });

        //设置动态运行图是否为夜间模式
        NightMode = new JCheckBox();
        NightMode.setFont(new java.awt.Font("Dialog", 0, 12));
        NightMode.setText(_("Enable Night Mode in Dynamic View"));
        //NightMode.setText(_("设置动态运行图为夜间模式"));
        NightMode.setSelected(mainFrame.chartView.NIGHTMODE == true);
        NightMode.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (((JCheckBox) e.getSource()).isSelected())
                    mainFrame.chartView.NIGHTMODE = true;
                else
                    mainFrame.chartView.NIGHTMODE = false;

                mainFrame.chartView.repaint();
            }
        });


        //设置运行图运行图中车次起始框，终止框显示起点站终点站
        StartEnd = new JCheckBox();
        StartEnd.setFont(new java.awt.Font("Dialog", 0, 12));
        StartEnd.setText(_("*Show Start-End Station"));
        //StartEnd.setText(_("*车次框中显示起止车站"));
        StartEnd.setSelected(mainFrame.chartView.SHOWTRAIN_StartEnd == true);
        StartEnd.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (((JCheckBox) e.getSource()).isSelected())
                    mainFrame.chartView.SHOWTRAIN_StartEnd = true;
                else
                    mainFrame.chartView.SHOWTRAIN_StartEnd = false;

                mainFrame.chartView.repaint();
            }
        });

        //设置运行图中是否显示通过文本框实现的详细停点
        DetailTime = new JCheckBox();
        DetailTime.setFont(new java.awt.Font("Dialog", 0, 12));
        DetailTime.setText(_("*Show Detail Stop Time"));
        //DetailTime.setText(_("*显示详细停点"));
        DetailTime.setSelected(mainFrame.chartView.SHOWTRAIN_DetailTime == true);
        DetailTime.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (((JCheckBox) e.getSource()).isSelected())
                    mainFrame.chartView.SHOWTRAIN_DetailTime = true;
                else
                    mainFrame.chartView.SHOWTRAIN_DetailTime = false;

                mainFrame.chartView.repaint();
            }
        });


        //设置运行图中是否采用虚线显示半小时线
        halfHourDashGrid = new JCheckBox();
        halfHourDashGrid.setFont(new java.awt.Font("Dialog", 0, 12));
        halfHourDashGrid.setText(_("Display Half-Hour clock using dashline"));
        //halfHourDashGrid.setText(_("虚线显示半小时时间线"));
        halfHourDashGrid.setSelected(mainFrame.chartView.halfHourDashGrid == true);
        halfHourDashGrid.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (((JCheckBox) e.getSource()).isSelected())
                    mainFrame.chartView.halfHourDashGrid = true;
                else
                    mainFrame.chartView.halfHourDashGrid = false;

                mainFrame.chartView.repaint();
            }
        });

        JPanel panelCB = new JPanel();
        panelCB.setLayout(new GridLayout(5, 1));
        panelCB.add(cbUnderColor);
        panelCB.add(cbDrawPoint);

        panelCB.add(halfHourDashGrid);
        panelCB.add(DashSiding);

        panelCB.add(DetailTime);
        panelCB.add(StartEnd);

        panelCB.add(ShowDistance);
        panelCB.add(NightMode);


        panel.setLayout(new BorderLayout());
        panel.add(panelCB, BorderLayout.CENTER);
        panel.add(createButtonPanel(), BorderLayout.SOUTH);

        return panel;
    }

    private JButton createJButton(String name) {
        JButton bt = new JButton(name);
        bt.setActionCommand(name);

        bt.setPreferredSize(new Dimension(62, 24));
        bt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (ae.getActionCommand().equals(_("Default")))
                    setDefault();
                else
                    setValues();
            }
        });

        return bt;
    }

    public void editSettings() {
        Dimension dlgSize = getPreferredSize();
        Dimension frmSize = mainFrame.getSize();
        Point loc = mainFrame.getLocation();
        setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
                (frmSize.height - dlgSize.height) / 2 + loc.y);

        pack();
        this.setSize(dlgSize);
        setVisible(true);
    }

    public Dimension getPreferredSize() {
        //change the dialog window width to show all texts
        int w = 570;
        int h = tbPane.getPreferredSize().height + 308;
        return new Dimension(w, h);
    }

    private JPanel creatJPanel(JLabel lbL0, JTextField tfM0, JLabel lbR0,
                               JLabel lbL1, JTextField tfM1, JLabel lbR1,
                               JLabel lbL2, JTextField tfM2, JLabel lbR2,
                               JLabel lbM3) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        panel.add(lbL0, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(10, 0, 0, 0), 75, 7));
        panel.add(tfM0, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(2, 2, 2, 2), 75, 0));
        panel.add(lbR0, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 1, 0, 10), 0, 0));

        panel.add(lbL1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 75, 7));
        panel.add(tfM1, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(2, 2, 2, 2), 75, 0));
        panel.add(lbR1, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 1, 0, 10), 0, 0));

        panel.add(lbL2, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 75, 7));
        panel.add(tfM2, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(2, 2, 2, 2), 75, 0));
        panel.add(lbR2, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 1, 0, 10), 0, 0));

        panel.add(lbM3, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 5, 0), 0, 0));

        return panel;
    }

    private JLabel createJLabelL(String name) {
        JLabel lb = new JLabel(name);
        lb.setFont(new Font("Dialog", Font.PLAIN, 12));
        lb.setPreferredSize(new Dimension(32, 15));
        lb.setHorizontalAlignment(SwingConstants.RIGHT);

        return lb;
    }

    private JLabel createJLabelR(String name) {
        JLabel lb = new JLabel(name);
        lb.setFont(new Font("Dialog", Font.PLAIN, 12));
        lb.setPreferredSize(new Dimension(32, 15));
        lb.setHorizontalAlignment(SwingConstants.LEFT);

        return lb;
    }

    private JLabel createJLabelM(String name) {
        JLabel lb = new JLabel(name);
        lb.setFont(new Font("Dialog", Font.PLAIN, 12));
        lb.setHorizontalAlignment(SwingConstants.LEFT);
        lb.setHorizontalTextPosition(SwingConstants.LEFT);

        return lb;
    }

    private JTextField createJTextField(String value) {
        JTextField tf = new JTextField();
        tf.setPreferredSize(new Dimension(12, 22));
        tf.setText(value);
        tf.setColumns(0);

        return tf;
    }

    private void setDefault() {
        d0.setText("3");
        d1.setText("4");
        d2.setText("2");

        t0.setText("18");
        t1.setText("2");
        t2.setText("10");
    }

    private void setValues() {
        Chart chart = mainFrame.chart;

        String stDistScale = d0.getText();
        String stDisplay = d1.getText();
        String stBold = d2.getText();

        String stStart = t0.getText();
        String stMinScale = t1.getText();
        String stInterval = t2.getText();

        int distScale = 3;
        int display = 4;
        int bold = 2;

        int start = 0;
        int minScale = 2;
        int interval = 10;
        try {
            distScale = Integer.parseInt(stDistScale);
            display = Integer.parseInt(stDisplay);
            bold = Integer.parseInt(stBold);

            start = Integer.parseInt(stStart);
            minScale = Integer.parseInt(stMinScale);
            interval = Integer.parseInt(stInterval);

            if (
                    (!((distScale >= 1 && distScale <= 10)
                            && (display >= 0 && display <= 6)
                            && (bold >= 0 && bold <= 6)
                            && (bold <= display)))

                            ||

                            (!((start >= 0 && start <= 23)
                                    && (minScale >= 1 && minScale <= 10)
                                    && ((interval == 1)
                                    || (interval == 2)
                                    || (interval == 3)
                                    || (interval == 4)
                                    || (interval == 5)
                                    || (interval == 6)
                                    || (interval == 10)
                                    || (interval == 12)
                                    || (interval == 15)
                                    || (interval == 20)
                                    || (interval == 30)
                                    || (interval == 60))))
                    ) {

                this.statusBar.setText(_("Input data out of range."));
            } else {
                chart.distScale = distScale;
                chart.displayLevel = display;
                chart.boldLevel = bold;

                chart.startHour = start;
                chart.minuteScale = minScale;
                chart.timeInterval = interval;

                mainFrame.chartView.resetSize();
                mainFrame.runView.refresh();

                this.statusBar.setText(defaultStatus);
            }
        } catch (NumberFormatException e) {
            this.statusBar.setText(_("Invalid input"));
        }
    }
}
