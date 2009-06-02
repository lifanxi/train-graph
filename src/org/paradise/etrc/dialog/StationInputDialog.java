package org.paradise.etrc.dialog;

import java.awt.*;
import javax.swing.*;

/**
 * @author lguo@sina.com
 * @version 1.0
 */

public class StationInputDialog extends JDialog {
	private static final long serialVersionUID = 9065758632068382095L;
JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JTable jTable1 = new JTable();
  JPanel jPanel1 = new JPanel();
  JLabel jLabel1 = new JLabel();
  JTextField jTextField1 = new JTextField();

  public StationInputDialog(Frame frame, String title, boolean modal) {
    super(frame, title, modal);
    try {
      jbInit();
      pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public StationInputDialog() {
    this(null, "", false);
  }

  private void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    jLabel1.setText("jLabel1");
    jTextField1.setText("jTextField1");
    jTable1.setTableHeader(null);
    getContentPane().add(panel1);
    panel1.add(jTable1,  BorderLayout.CENTER);
    panel1.add(jPanel1, BorderLayout.NORTH);
    jPanel1.add(jLabel1, null);
    jPanel1.add(jTextField1, null);
  }
}
