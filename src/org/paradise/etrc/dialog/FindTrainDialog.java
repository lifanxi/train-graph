package org.paradise.etrc.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

//import com.borland.jbcl.layout.*;

import org.paradise.etrc.*;
import static org.paradise.etrc.ETRC._;

/**
 * @author lguo@sina.com
 * @version 1.0
 */

public class FindTrainDialog extends JDialog implements ActionListener {
 	private static final long serialVersionUID = -2094251491864017010L;

MainFrame mainFrame = null;

  JPanel panel = new JPanel();
  VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
  JPanel jPanel2 = new JPanel();
  JLabel lbTrainName = new JLabel();
  JTextField tfTrainName = new JTextField();
  JButton btFind = new JButton();
  JButton btClear = new JButton();
  JLabel statusBar = new JLabel();
  JPanel jPanel3 = new JPanel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();

  String defaultStatus = _("Input the train you want to find");

  public FindTrainDialog(Frame frame) {
    super(frame, _("Find a Train"), false);

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
    panel.setLayout(verticalFlowLayout1);
    this.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
    this.setFont(new java.awt.Font(_("FONT_NAME"), 0, 12));
    panel.setFont(new java.awt.Font("Dialog", 0, 12));
    panel.setBorder(BorderFactory.createRaisedBevelBorder());

    lbTrainName.setText(_("Train number to find:"));
    lbTrainName.setFont(new java.awt.Font(_("FONT_NAME"), 0, 12));
    lbTrainName.setPreferredSize(new Dimension(8, 15));
    lbTrainName.setToolTipText("");
    lbTrainName.setHorizontalAlignment(SwingConstants.RIGHT);

    tfTrainName.setMinimumSize(new Dimension(12, 22));
    tfTrainName.setPreferredSize(new Dimension(20, 22));
    tfTrainName.setColumns(0);
    //tfTrainName.addKeyListener(this);
    tfTrainName.setActionCommand("Button_Find");
    tfTrainName.addActionListener(this);

    btFind.setFont(new java.awt.Font(_("FONT_NAME"), 0, 12));
    btFind.setText(_("Find"));
    btFind.setActionCommand("Button_Find");
    btFind.addActionListener(this);
    btFind.requestFocus(false);

    btClear.setFont(new java.awt.Font(_("FONT_NAME"), 0, 12));
    btClear.setText(_("Clear"));
    btClear.setActionCommand("Button_Clear");
    btClear.addActionListener(this);

    statusBar.setFont(new java.awt.Font(_("FONT_NAME"), 0, 12));
    statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
    statusBar.setText(_("Input the train you want to find"));
    verticalFlowLayout1.setHgap(2);
    verticalFlowLayout1.setVgap(2);

    jPanel3.setBorder(null);
    jPanel3.setInputVerifier(null);
    jPanel3.setLayout(gridBagLayout2);

    getContentPane().add(panel);

    panel.add(jPanel3, null);
    jPanel3.add(lbTrainName,                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 87, 7));
    jPanel3.add(tfTrainName,                       new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 75, 0));

    panel.add(jPanel2, null);
    jPanel2.add(btClear, null);
    jPanel2.add(btFind, null);
    panel.add(statusBar, null);
  }

  /**
   * actionPerformed
   *
   * @param e ActionEvent
   */
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if(command.equalsIgnoreCase("Button_Find")) {
      doFind();
    }
    else if(command.equalsIgnoreCase("Button_Default")) {
      doClear();
    }
  }

  private void doClear() {
    this.tfTrainName.setText("");

    this.statusBar.setText(defaultStatus);
  }

  /**
   * doOK
   */
  private void doFind() {
    String trainName = tfTrainName.getText();

    if(trainName.trim().equalsIgnoreCase(""))
    	return;
    
    if(mainFrame.chartView.findAndMoveToTrain(trainName))
      this.statusBar.setText(trainName);
    else {
      this.statusBar.setText(_("Not found"));
      //doClear();
    }
  }
}
