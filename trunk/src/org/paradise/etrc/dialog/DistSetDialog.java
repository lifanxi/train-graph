package org.paradise.etrc.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

//import com.borland.jbcl.layout.*;

import org.paradise.etrc.*;
import org.paradise.etrc.data.Chart;

/**
 * @author lguo@sina.com
 * @version 1.0
 */

public class DistSetDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 5479809489457404370L;

MainFrame mainFrame = null;

  JPanel panel = new JPanel();
  VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
  JPanel jPanel2 = new JPanel();
  JLabel lbDisplay = new JLabel();
  JTextField tfDistScale = new JTextField();
  JLabel lbDistScale = new JLabel();
  JTextField tfDisplay = new JTextField();
  JButton btOK = new JButton();
  JButton btDefault = new JButton();
  TitledBorder titledBorder1;
  JLabel statusBar = new JLabel();
  JPanel jPanel3 = new JPanel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();

  String defaultStatus = "距离轴设置";
  JLabel jLabel2 = new JLabel();
  JLabel lbBold = new JLabel();
  JTextField tfBold = new JTextField();
  JLabel jLabel3 = new JLabel();

  Chart chart;
  public DistSetDialog(Frame frame, Chart _chart) {
    super(frame, "距离轴设置", false);

    if(frame instanceof MainFrame) {
      mainFrame = (MainFrame) frame;
    }
    
    chart = _chart;

    try {
      jbInit();
      pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    titledBorder1 = new TitledBorder("");
    panel.setLayout(verticalFlowLayout1);
    this.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
    this.setFont(new java.awt.Font("宋体", 0, 12));
    panel.setFont(new java.awt.Font("Dialog", 0, 12));
    panel.setBorder(BorderFactory.createRaisedBevelBorder());

    lbDisplay.setText("每公里像素数：");
    lbDisplay.setFont(new java.awt.Font("宋体", 0, 12));
    lbDisplay.setPreferredSize(new Dimension(8, 15));
    lbDisplay.setToolTipText("");
    lbDisplay.setHorizontalAlignment(SwingConstants.RIGHT);

    tfDistScale.setMinimumSize(new Dimension(12, 22));
    tfDistScale.setPreferredSize(new Dimension(20, 22));
    tfDistScale.setText(chart.distScale+"");
    tfDistScale.setColumns(0);

    lbDistScale.setText("最低显示等级：");
    lbDistScale.setFont(new java.awt.Font("宋体", 0, 12));
    lbDistScale.setPreferredSize(new Dimension(32, 15));
    lbDistScale.setRequestFocusEnabled(true);
    lbDistScale.setToolTipText("");
    lbDistScale.setHorizontalAlignment(SwingConstants.RIGHT);

    tfDisplay.setMinimumSize(new Dimension(24, 22));
    tfDisplay.setPreferredSize(new Dimension(20, 22));
    tfDisplay.setText(chart.displayLevel+"");

    btOK.setFont(new java.awt.Font("宋体", 0, 12));
    btOK.setText("设定");
    btOK.setActionCommand("Button_OK");
    btOK.addActionListener(this);

    btDefault.setFont(new java.awt.Font("宋体", 0, 12));
    btDefault.setText("默认");
    btDefault.setActionCommand("Button_Default");
    btDefault.addActionListener(this);





    titledBorder1.setTitlePosition(2);
    titledBorder1.setTitleFont(new java.awt.Font("宋体", 0, 12));
    titledBorder1.setTitle("距离轴参数");

    statusBar.setFont(new java.awt.Font("宋体", 0, 12));
    statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
    statusBar.setText("设定距离轴参数");
    verticalFlowLayout1.setHgap(2);
    verticalFlowLayout1.setVgap(2);

    jPanel3.setBorder(titledBorder1);
    jPanel3.setInputVerifier(null);
    jPanel3.setLayout(gridBagLayout2);
    jLabel1.setFont(new java.awt.Font("宋体", 0, 12));
    jLabel1.setHorizontalAlignment(SwingConstants.LEFT);
    jLabel1.setText("等站");
    jLabel2.setText("特等站等级为0");
    jLabel2.setFont(new java.awt.Font("宋体", 0, 12));
    jLabel2.setToolTipText("");
    jLabel2.setHorizontalAlignment(SwingConstants.LEFT);
    jLabel2.setHorizontalTextPosition(SwingConstants.LEFT);
    lbBold.setHorizontalAlignment(SwingConstants.RIGHT);
    lbBold.setToolTipText("");
    lbBold.setRequestFocusEnabled(true);
    lbBold.setPreferredSize(new Dimension(32, 15));
    lbBold.setFont(new java.awt.Font("宋体", 0, 12));
    lbBold.setText("最低粗线坐标：");

    tfBold.setText(chart.boldLevel+"");
    tfBold.setPreferredSize(new Dimension(20, 22));
    tfBold.setMinimumSize(new Dimension(24, 22));

    jLabel3.setText("等站");
    jLabel3.setHorizontalAlignment(SwingConstants.LEFT);
    jLabel3.setFont(new java.awt.Font("宋体", 0, 12));
    getContentPane().add(panel);

    panel.add(jPanel3, null);
    jPanel3.add(tfDisplay,                        new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 75, 0));
    jPanel3.add(lbDisplay,                   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 87, 7));
    jPanel3.add(tfDistScale,                      new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 75, 0));
    jPanel3.add(lbDistScale,                   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 63, 7));

    panel.add(jPanel2, null);
    jPanel2.add(btDefault, null);
    jPanel2.add(btOK, null);
    panel.add(statusBar, null);
    jPanel3.add(jLabel1,                    new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 1, 0, 10), 0, 0));
    jPanel3.add(jLabel2,                          new GridBagConstraints(1, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    jPanel3.add(lbBold,             new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jPanel3.add(tfBold,             new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
    jPanel3.add(jLabel3,       new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 1, 0, 10), 0, 0));
  }

  /**
   * actionPerformed
   *
   * @param e ActionEvent
   */
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if(command.equalsIgnoreCase("Button_OK")) {
      doOK();
    }
    else if(command.equalsIgnoreCase("Button_Default")) {
      doDefault();
    }
    else if(command.equalsIgnoreCase("Button_Test")) {
      doTest();
    }
  }

  /**
   * doTest
   */
  private void doTest() {
    //this.mainFrame.remove(mainFrame.panelChart);
    //this.mainFrame.getContentPane().add(mainFrame.panelChart, BorderLayout.CENTER);
    //this.mainFrame.panelChart.spLines.getRootPane().setSize(400,800);
    mainFrame.repaint();
  }

  /**
   * doDefault
   */
  private void doDefault() {
    this.tfDistScale.setText("3");
    this.tfDisplay.setText("4");
    this.tfBold.setText("2");

    this.statusBar.setText(defaultStatus);
  }

  /**
   * doOK
   */
  private void doOK() {
    String stDistScale = tfDistScale.getText();
    String stDisplay = tfDisplay.getText();
    String stBold = tfBold.getText();

    int distScale = 3;
    int display = 4;
    int bold = 2;
    try{
      distScale = Integer.parseInt(stDistScale);
      display = Integer.parseInt(stDisplay);
      bold = Integer.parseInt(stBold);

      if(!((distScale >= 1 && distScale <= 10)
           &&(display >= 0 && display <= 6)
           &&(bold >=0 && bold <= 6)
           &&(bold <= display))){

        this.statusBar.setText("输入数据超出范围！");
      }
      else{
        if(this.mainFrame != null) {
          chart.distScale = distScale;
          chart.displayLevel = display;
          chart.boldLevel = bold;

          mainFrame.validate();
          mainFrame.chartView.resetSize();
          mainFrame.runView.refresh();
        }
        this.statusBar.setText(defaultStatus);
      }
    }
    catch(NumberFormatException e) {
      this.statusBar.setText("输入数据格式错！");
    }
  }
}
