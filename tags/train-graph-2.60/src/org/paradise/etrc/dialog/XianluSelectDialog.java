package org.paradise.etrc.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.paradise.etrc.MainFrame;
import org.paradise.etrc.data.skb.ETRCLCB;

import static org.paradise.etrc.ETRC._;

public class XianluSelectDialog extends JDialog {
	private static final long serialVersionUID = 1389706992939223725L;
	
	private ETRCLCB lcb;
	
	private JButton[] btChar;
	private JButton btSearch;
	
	private JList xlList;
	
//	private Vector curXianlu;
	private String xianlu;
	
	private MainFrame mainFrame;
	
	public boolean isCanceled = false;
	
	public XianluSelectDialog(MainFrame _mainFrame){
		super(_mainFrame, _("Circuit Selection"), true);
		
		mainFrame = _mainFrame;
		lcb = mainFrame.getLCB();
		
		try {
			jbInit();
			pack();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void jbInit() throws Exception {
		this.setTitle(_("Circuit Selection"));
		btChar = new JButton[27];
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(4, 7));
		for(int i=0; i<26; i++) {
			btChar[i] = buildCharButton((char) ('A' + i)); 
			buttonPanel.add(btChar[i]);
		}

		btChar[26] = buildCharButton('*'); 
		buttonPanel.setBorder(new EmptyBorder(5,2,5,2));
		buttonPanel.add(btChar[26]);
		
		btSearch = new JButton(_("Search"));
		btSearch.setFont(new Font(_("FONT_NAME"), 0, 12));
		btSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				StationInputDialog dlg = new StationInputDialog(mainFrame);
				Dimension dlgSize = dlg.getPreferredSize();
				Dimension frmSize = getSize();
				Point loc = getLocation();
				dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
						(frmSize.height - dlgSize.height) / 2 + loc.y);
				dlg.setModal(true);
				dlg.pack();
				dlg.setVisible(true);
				if (dlg.stationName != null)
				{
					XianluSelectDialog.this.xlList.setListData(lcb.findCircuitsByStation(dlg.stationName));
				}
			}
		});
		buttonPanel.add(btSearch);
		
		xlList = buildXianluList();
		JScrollPane jsp = new JScrollPane(xlList);

		JButton btOK = new JButton(_("OK"));
		btOK.setFont(new Font("dialog", 0, 12));
		btOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				xianlu = (String) xlList.getSelectedValue();
				XianluSelectDialog.this.setVisible(false);
			}
		});

		JButton btCancel = new JButton(_("Cancel"));
		btCancel.setFont(new Font("dialog", 0, 12));
		btCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				xianlu = null;
				XianluSelectDialog.this.setVisible(false);
			}
		});
		
		JPanel okCancelPanel = new JPanel();
		okCancelPanel.add(btOK);
		okCancelPanel.add(btCancel);
		
		JPanel rootPanel = new JPanel();
		rootPanel.setLayout(new BorderLayout());
		rootPanel.add(buttonPanel, BorderLayout.NORTH);
		rootPanel.add(jsp, BorderLayout.CENTER);
		rootPanel.add(okCancelPanel, BorderLayout.SOUTH);

		getContentPane().add(rootPanel);
	}
	
	private JList buildXianluList() {
		xlList = new JList();
		xlList.setListData(lcb.findXianlu('J'));
		xlList.setFont(new Font("dialog", 0, 12));
//		xlList.addListSelectionListener(new ListSelectionListener() {
//			public void valueChanged(ListSelectionEvent lse) {
//				String xianlu = (String) ((JList) (lse.getSource())).getSelectedValue();
//				XianluSelectDialog.this.xianlu = xianlu;
//			}
//		});
		
		return xlList;
	}
	
	private JButton buildCharButton(char ch) {
		JButton jb = new JButton("" + ch);
		
		jb.setActionCommand("" + ch);
		
		jb.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				char head = ae.getActionCommand().charAt(0);
				XianluSelectDialog.this.xlList.setListData(lcb.findXianlu(head));
			}
			
		});
		
		return jb;
	}
	
	public String getXianlu() {
		Dimension dlgSize = this.getPreferredSize();
		
		if(mainFrame != null) {
			Dimension frmSize = mainFrame.getSize();
			Point loc = mainFrame.getLocation();
			this.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
					(frmSize.height - dlgSize.height) / 2 + loc.y);
		}
		
		this.setModal(true);
		this.setVisible(true);
		
		return xianlu;
	}

//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		try {
//			System.out.println(new XianluSelectDialog(null, "").getXianlu());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
}
