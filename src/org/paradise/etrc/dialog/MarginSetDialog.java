package org.paradise.etrc.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import com.borland.jbcl.layout.*;

import org.paradise.etrc.*;
import org.paradise.etrc.view.MainView;

/**
 * @author lguo@sina.com
 * @version 1.0
 */

public class MarginSetDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 5918995210504867184L;

MainFrame mainFrame = null;

  JPanel panel = new JPanel();
  JPanel jPanel1 = new JPanel();
  VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
  JPanel jPanel2 = new JPanel();
  JLabel lbRight = new JLabel();
  JTextField tfRight = new JTextField();
  JLabel lbLeft = new JLabel();
  JTextField tfLeft = new JTextField();
  JLabel lbDown = new JLabel();
  JTextField tfDown = new JTextField();
  JLabel lbUp = new JLabel();
  JTextField tfUp = new JTextField();
  JButton btOK = new JButton();
  JButton btDefault = new JButton();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel lbCenter = new JLabel();
  TitledBorder titledBorder1;
  JLabel statusBar = new JLabel();
  JButton btTest = new JButton();

  public MarginSetDialog(Frame frame) {
    super(frame, "边距设置", false);

    if(frame instanceof MainFrame) {
      mainFrame = (MainFrame) frame;
    }

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

    lbRight.setFont(new java.awt.Font("宋体", 0, 12));
    lbRight.setPreferredSize(new Dimension(8, 15));
    lbRight.setHorizontalAlignment(SwingConstants.RIGHT);
    lbRight.setText("右：");

    tfRight.setMinimumSize(new Dimension(12, 22));
    tfRight.setPreferredSize(new Dimension(20, 22));
    tfRight.setText(mainFrame.mainView.rightMargin+"");

    lbLeft.setText("左：");
    lbLeft.setFont(new java.awt.Font("宋体", 0, 12));
    lbLeft.setPreferredSize(new Dimension(8, 15));
    lbLeft.setHorizontalAlignment(SwingConstants.RIGHT);

    tfLeft.setRequestFocusEnabled(true);
    tfLeft.setMinimumSize(new Dimension(12, 22));
    tfLeft.setPreferredSize(new Dimension(20, 22));
    tfLeft.setText(mainFrame.mainView.leftMargin+"");

    lbDown.setText("下：");
    lbDown.setFont(new java.awt.Font("宋体", 0, 12));
    lbDown.setPreferredSize(new Dimension(8, 15));
    lbDown.setHorizontalAlignment(SwingConstants.RIGHT);

    tfDown.setMinimumSize(new Dimension(12, 22));
    tfDown.setPreferredSize(new Dimension(20, 22));
    tfDown.setText(mainFrame.mainView.bottomMargin+"");

    lbUp.setText("上：");
    lbUp.setFont(new java.awt.Font("宋体", 0, 12));
    lbUp.setPreferredSize(new Dimension(8, 15));
    lbUp.setHorizontalAlignment(SwingConstants.RIGHT);

    tfUp.setMinimumSize(new Dimension(12, 22));
    tfUp.setPreferredSize(new Dimension(20, 22));
    tfUp.setText(mainFrame.mainView.topMargin+"");

    btOK.setFont(new java.awt.Font("宋体", 0, 12));
    btOK.setText("设定");
    btOK.setActionCommand("Button_OK");
    btOK.addActionListener(this);

    btDefault.setFont(new java.awt.Font("宋体", 0, 12));
    btDefault.setText("默认");
    btDefault.setActionCommand("Button_Default");
    btDefault.addActionListener(this);

    btTest.addActionListener(this);
    btTest.setActionCommand("Button_Test");
    btTest.setText("测试");
    btTest.setFont(new java.awt.Font("宋体", 0, 12));

    jPanel1.setLayout(gridBagLayout1);

    lbCenter.setBorder(null);
    lbCenter.setPreferredSize(new Dimension(64, 48));
    lbCenter.setText("");

    jPanel1.setFont(new java.awt.Font("宋体", 0, 12));
    jPanel1.setAlignmentY((float) 0.5);
    jPanel1.setBorder(titledBorder1);

    titledBorder1.setTitlePosition(2);
    titledBorder1.setTitleFont(new java.awt.Font("宋体", 0, 12));
    titledBorder1.setTitle("图表边距");

    statusBar.setFont(new java.awt.Font("宋体", 0, 12));
    statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
    statusBar.setText("设定图表边距");
    verticalFlowLayout1.setHgap(2);
    verticalFlowLayout1.setVgap(2);

    getContentPane().add(panel);

    panel.add(jPanel1, null);
    jPanel1.add(lbUp,     new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 36, 7));
    jPanel1.add(tfUp,   new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 18, 0));
    jPanel1.add(lbDown,   new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 36, 7));
    jPanel1.add(tfDown,   new GridBagConstraints(3, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 18, 0));
    jPanel1.add(lbLeft,   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 36, 7));
    jPanel1.add(tfLeft,   new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 18, 0));
    jPanel1.add(lbRight,   new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 36, 7));
    jPanel1.add(tfRight,   new GridBagConstraints(5, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 6), 18, 0));
    jPanel1.add(lbCenter,       new GridBagConstraints(2, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

    panel.add(jPanel2, null);
    jPanel2.add(btTest, null);
    jPanel2.add(btDefault, null);
    jPanel2.add(btOK, null);
    panel.add(statusBar, null);
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
    //mainFrame.repaint();
    this.statusBar.setText("测试用的，如果忘记去掉请告知 lguo@suteng.com");
  }

  /**
   * doDefault
   */
  private void doDefault() {
    this.tfUp.setText(MainView.DEFAULT_TOP_MARGIN+"");
    this.tfDown.setText(MainView.DEFAULT_BOTTOM_MARGIN+"");
    this.tfLeft.setText(MainView.DEFAULT_LEFT_MARGIN+"");
    this.tfRight.setText(MainView.DEFAULT_RIGHT_MARGIN+"");
    this.statusBar.setText("设定图表边距");
  }

  /**
   * doOK
   */
  private void doOK() {
    String stUp = tfUp.getText();
    String stDown = tfDown.getText();
    String stLeft = tfLeft.getText();
    String stRight = tfRight.getText();

    int up = 30;
    int down = 30;
    int left = 80;
    int right = 80;
    try{
      up = Integer.parseInt(stUp);
      down = Integer.parseInt(stDown);
      left = Integer.parseInt(stLeft);
      right = Integer.parseInt(stRight);

      if(!(  (up >= 0 && up <= 200)
          && (down >= 0 && down <= 200)
          && (left >= 0 && left <= 200)
          && (right >= 0 && right <= 200))) {
        this.statusBar.setText("输入数据超出范围(0-200)！");
      }
      else{
        if(this.mainFrame != null) {
          mainFrame.mainView.topMargin = up;
          mainFrame.mainView.bottomMargin = down;
          mainFrame.mainView.leftMargin = left;
          mainFrame.mainView.rightMargin = right;

          mainFrame.mainView.repaint();
          mainFrame.validate();
        }
        this.statusBar.setText("设定图表边距");
      }
    }
    catch(NumberFormatException e) {
      this.statusBar.setText("输入数据格式错！");
    }
  }
}
