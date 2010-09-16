package org.paradise.etrc;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;

import org.paradise.etrc.data.*;
import org.paradise.etrc.data.skb.ETRCLCB;
import org.paradise.etrc.data.skb.ETRCSKB;
import org.paradise.etrc.dialog.*;
import org.paradise.etrc.filter.CSVFilter;
import org.paradise.etrc.filter.GIFFilter;
import org.paradise.etrc.filter.TRCFilter;
import org.paradise.etrc.filter.TRFFilter;
import org.paradise.etrc.view.chart.ChartView;
import org.paradise.etrc.view.dynamic.DynamicView;
import org.paradise.etrc.view.sheet.SheetView;

import static org.paradise.etrc.ETRC._;

/**
 * @author lguo@sina.com
 * @version 1.0
 */

public class MainFrame extends JFrame implements ActionListener, Printable {
	private static final long serialVersionUID = 1L;
	
	public JPanel mainPanel;
	public JSplitPane splitPane;
	public ChartView chartView;
	public DynamicView runView;
	public SheetView sheetView;
	
	private boolean isShowRun = true;
	
	public JLabel statusBarMain = new JLabel();
	public JLabel statusBarRight = new JLabel();

	private Properties defaultProp;
	public Properties prop;
	public static String Prop_Working_Chart = "Working_Chart";
	public static String Prop_Show_UP = "Show_UP";
	public static String Prop_Show_Down = "Show_Down";
	public static String Prop_Show_Run = "Show_Run";
	public static String Prop_HTTP_Proxy_Server = "HTTP_Proxy_Server";
	public static String Prop_HTTP_Proxy_Port = "HTTP_Proxy_Port";
	
	private static String Sample_Chart_File = "sample.trc";
	private static String Properties_File = "htrc.prop";
	
	public boolean isNewCircuit = false;
	public Chart chart;

	private static final int MAX_TRAIN_SELECT_HISTORY_RECORD = 12;
	public Vector<String> trainSelectHistory;
	public JComboBox cbTrainSelectHistory;
	
	//Construct the frame
	public MainFrame() {
		defaultProp = new Properties();
		defaultProp.setProperty(Prop_Working_Chart, Sample_Chart_File);
		defaultProp.setProperty(Prop_Show_UP, "Y");
		defaultProp.setProperty(Prop_Show_Down, "Y");
		defaultProp.setProperty(Prop_Show_Run, "Y");
		defaultProp.setProperty(Prop_HTTP_Proxy_Server, "");
		defaultProp.setProperty(Prop_HTTP_Proxy_Port, "");
		
		prop = new Properties(defaultProp);
		
		trainSelectHistory = new Vector<String>();
		cbTrainSelectHistory = new JComboBox(new DefaultComboBoxModel(trainSelectHistory));
		
		try {
			prop.load(new FileInputStream(Properties_File));
		} catch (IOException e) {
			System.out.println("Unable to load prop file. Use default value.");
		}
				
		initChart();
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doExit() {
		if(chart != null)
			if(new YesNoBox(this, _("The train graph is has changed.\nDo you want to save the changes?")).askForYes())
				doSaveChart(); 
		
		try {
			prop.store(new FileOutputStream(Properties_File), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	//Component initialization
	private void jbInit() throws Exception {
		chartView = new ChartView(this);
		runView = new DynamicView(this);
		sheetView = new SheetView(this);
		
//		tbPane = new JTabbedPane();
//		tbPane.setFont(new Font("Dialog", Font.PLAIN, 12));
//		tbPane.add("点单1", sheetView);
//		tbPane.add("动态图", runView);
//		tbPane.setMinimumSize(tbPane.getPreferredSize());
//		
//		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, tbPane, chartView);
//		splitPane.setDividerLocation(tbPane.getPreferredSize().height);
//		splitPane.setDividerSize(5);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		mainPanel.add(runView, BorderLayout.NORTH);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, chartView, sheetView);
	    int dividerPos = Toolkit.getDefaultToolkit().getScreenSize().width - 253;
		splitPane.setDividerLocation(dividerPos);
		splitPane.setDividerSize(6);
		splitPane.setOneTouchExpandable(true);
		
		mainPanel.add(splitPane, BorderLayout.CENTER);

		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		this.setFont(new java.awt.Font(_("FONT_NAME"), 0, 10));
		this.setLocale(java.util.Locale.getDefault());
		this.setResizable(true);
		this.setState(Frame.NORMAL);
		this.setIconImage(new ImageIcon(org.paradise.etrc.MainFrame.class
				.getResource("/pic/icon.gif")).getImage());
		
		JPanel contentPane;
		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.setBorder(null);
		contentPane.setDebugGraphicsOptions(0);

		this.setJMenuBar(loadMenu());

		statusBarMain = loadStatusBar();
		statusBarRight = loadStatusBar();
		JPanel statusPanel = new JPanel();
		statusPanel.setLayout(new BorderLayout());
		statusPanel.add(statusBarMain, BorderLayout.CENTER);
		statusPanel.add(statusBarRight, BorderLayout.EAST);

		contentPane.add(statusPanel, BorderLayout.SOUTH);
		contentPane.add(loadToolBar(), BorderLayout.NORTH);
		contentPane.add(mainPanel, BorderLayout.CENTER);

		this.setTitle();
	}
/*
	private void jbInit() throws Exception {
		chartView = new ChartView(this);
		runView = new DynamicView(this);
		sheetView = new SheetView(this);
		
		JTabbedPane tbPane = new JTabbedPane();
		tbPane.setFont(new Font("Dialog", Font.PLAIN, 12));
		tbPane.add("点单", sheetView);
		tbPane.add("运行图", chartView);
		
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, runView, tbPane);
		splitPane.setDividerLocation(runView.getPreferredSize().height);
		splitPane.setDividerSize(3);
		runView.setMinimumSize(runView.getPreferredSize());

		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		this.setFont(new java.awt.Font(_("FONT_NAME"), 0, 10));
		this.setLocale(java.util.Locale.getDefault());
		this.setResizable(true);
		this.setState(Frame.NORMAL);
		this.setIconImage(new ImageIcon(org.paradise.etrc.MainFrame.class
				.getResource("/pic/icon.gif")).getImage());
		
		JPanel contentPane;
		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.setBorder(null);
		contentPane.setDebugGraphicsOptions(0);

		this.setJMenuBar(loadMenu());

		statusBarMain = loadStatusBar();
		statusBarRight = loadStatusBar();
		JPanel statusPanel = new JPanel();
		statusPanel.setLayout(new BorderLayout());
		statusPanel.add(statusBarMain, BorderLayout.CENTER);
		statusPanel.add(statusBarRight, BorderLayout.EAST);

		contentPane.add(statusPanel, BorderLayout.SOUTH);
		contentPane.add(loadToolBar(), BorderLayout.NORTH);
		contentPane.add(splitPane, BorderLayout.CENTER);

		this.setTitle();
	}
*/
    private static final String titlePrefix = _("LGuo's Electronic Train Graph");

	private String activeTrainName = "";

	public void setActiceTrainName(String _name) {
		if (_name.equalsIgnoreCase(""))
			activeTrainName = "";
		else
			activeTrainName = " (" + _name + ") ";
		
		//_name不为空的时候，加入hitory
		if (!_name.equalsIgnoreCase("")) {
			//如果已经存在于history中则删除之，以保证新加入的位于第一个
			if(trainSelectHistory.contains(_name))
				trainSelectHistory.remove(_name);
			
			trainSelectHistory.add(0, _name);
			
			//超过最大历史记录数，则删除最老的
			if(trainSelectHistory.size() > MAX_TRAIN_SELECT_HISTORY_RECORD)
				for(int i=12; i<trainSelectHistory.size(); i++)
					trainSelectHistory.remove(i);
			
			cbTrainSelectHistory.setModel(new DefaultComboBoxModel(trainSelectHistory));
		}

		setTitle();
	}

	public String getCircuitName() {
		String circuitName = "";
		if (chart.circuit.name.equalsIgnoreCase(""))
			circuitName = "";
		else
			circuitName = " -- [" + chart.circuit.name + "] ";

		return circuitName;
	}

	public void setTitle() {
		setTitle(titlePrefix + getCircuitName() + activeTrainName);
	}

	private JLabel loadStatusBar() {
		JLabel statusBar = new JLabel();
		Border border = BorderFactory.createLoweredBevelBorder();

		statusBar.setBorder(border);
		statusBar.setFont(new java.awt.Font(_("FONT_NAME"), 0, 12));
		statusBar.setText(_("Status: Normal"));
		return statusBar;
	}

	public JToggleButton jtButtonDown;
	public JToggleButton jtButtonShowRun;
	public JToggleButton jtButtonUp;

	private JToolBar loadToolBar() {
		JToolBar jToolBar = new JToolBar();
		
		//文件操作
		JButton jbOpenFile = createTBButton("openFile", _("Open a Chart"), File_Load_Chart);
		JButton jbSaveFile = createTBButton("saveFile", _("Save Chart"), File_Save_Chart);
		JButton jbSaveAs   = createTBButton("saveAs", _("Save Chart As"), File_Save_Chart_As);
		jToolBar.add(jbOpenFile);
		jToolBar.add(jbSaveFile);
		jToolBar.add(jbSaveAs);
		
		//车次、线路编辑
		JButton jbCircuitEdit = createTBButton("circuit", _("Edit Circuit"), Edit_Circuit);
		JButton jbTrainsEdit  = createTBButton("trains", _("Edit Train Information"), Edit_Trains);
		jToolBar.addSeparator();
		jToolBar.add(jbCircuitEdit);
		jToolBar.add(jbTrainsEdit);
		
		//查找车次
		JButton jbFindTrain = createTBButton("findTrain", _("Find a Train"), Edit_FindTrain);
		jToolBar.addSeparator();
		jToolBar.add(jbFindTrain);

		//坐标设置
		JButton jbSetupH = createTBButton("setupH", _("Timeline Settings"), Setup_Time);
		JButton jbSetupV = createTBButton("setupV", _("Distance Bar Settings"), Setup_Dist);
		jToolBar.addSeparator();
		jToolBar.add(jbSetupH);
		jToolBar.add(jbSetupV);
		
		//动态图是否开启
		ImageIcon imageRun = new ImageIcon(this.getClass().getResource("/pic/show_run.png"));
		jtButtonShowRun = new JToggleButton(imageRun);
		jtButtonShowRun.setToolTipText(_("Show Dynamic Chart"));
		jtButtonShowRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainFrame.this.changeShowRunState();
			}
		});
		
		//读配置文件设定是否显示动态图
		if(prop.getProperty(Prop_Show_Run).equalsIgnoreCase("Y")) {
			isShowRun = true;
		}
		else {
			isShowRun = false;
		}
		updateShowRunState();
		
		jToolBar.addSeparator();
		jToolBar.add(jtButtonShowRun);
		
		//上下行显示选择
		ImageIcon imageDown = new ImageIcon(this.getClass().getResource("/pic/down.png"));
		ImageIcon imageUp = new ImageIcon(this.getClass().getResource("/pic/up.png"));
		jtButtonDown = new JToggleButton(imageDown);
		jtButtonUp = new JToggleButton(imageUp);
		jtButtonUp.setToolTipText(_("Display Up-going Trains"));
		jtButtonDown.setToolTipText(_("Display Down-going Trains"));
		jtButtonDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chartView.changeShowDown();
			}
		});
		jtButtonUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chartView.changShownUp();
			}
		});
		
		//读配置文件设置上下行状态按钮
		chartView.showUpDownState = ChartView.SHOW_NONE;
		if(prop.getProperty(Prop_Show_Down).equalsIgnoreCase("Y")) {
			jtButtonDown.setSelected(true);
			chartView.showUpDownState ^= ChartView.SHOW_DOWN;
		}
		else
			jtButtonDown.setSelected(false);
		if(prop.getProperty(Prop_Show_UP).equalsIgnoreCase("Y")) {
			jtButtonUp.setSelected(true);
			chartView.showUpDownState ^= ChartView.SHOW_UP;
		}
		else
			jtButtonUp.setSelected(false);
		//让ChartView右下角的上下行状态显示图标显示正确的内容
		chartView.updateUpDownDisplay();

		jToolBar.addSeparator();
		jToolBar.add(jtButtonDown);
		jToolBar.add(jtButtonUp);
		
		//历史记录
		cbTrainSelectHistory.setFont(new Font("Dialog", Font.PLAIN, 12));
		cbTrainSelectHistory.setMinimumSize(new Dimension(64, 20));
		cbTrainSelectHistory.setMaximumSize(new Dimension(64, 20));
		cbTrainSelectHistory.setEditable(true);
		cbTrainSelectHistory.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				//当用键盘输入的时候会触发两次Action
				//一次是comboBoxChanged，另一次是comboBoxEdited
				//我们只处理与下拉选择一样的那一次：comboBoxChanged
				if(!ae.getActionCommand().equalsIgnoreCase("comboBoxChanged"))
					return;
				
				String trainToFind = (String) cbTrainSelectHistory.getSelectedItem();

				if(trainToFind == null)
					return;

				if(trainToFind.trim().equalsIgnoreCase(""))
					return;
				
				if(!chartView.findAndMoveToTrain(trainToFind))
					new MessageBox(MainFrame.this, String.format(_("Cannot find train information: %s"), trainToFind)).showMessage();
			}
		});
		jToolBar.addSeparator();
		jToolBar.add(cbTrainSelectHistory);

		return jToolBar;
	}
	
	private JButton createTBButton(String imgName, String toolTipText, String Command) {
		JButton jbOnToolBar = new JButton();

		ImageIcon  imageOpenFile = new ImageIcon(org.paradise.etrc.MainFrame.class
				.getResource("/pic/" + imgName + ".png"));

		jbOnToolBar.setIcon(imageOpenFile);
		jbOnToolBar.setToolTipText(toolTipText);
		jbOnToolBar.addActionListener(this);
		jbOnToolBar.setActionCommand(Command);
		
		return jbOnToolBar;
	}

	private final String File_Load_Chart = "File_Load_Chart";
	private final String File_Save_Chart = "File_Save_Chart";
	private final String File_Save_Chart_As = "File_Save_Chart_As";
	private final String File_Clear_Chart = "File_Clear_Chart";
//	private final String File_Circuit = "File_Circuit";
	private final String File_Train = "File_Train";
	private final String File_Export = "File_Export";
	private final String File_Exit = "File_Exit";

	private final String Edit_FindTrain = "Edit_FindTrain";
	private final String Edit_Circuit = "Edit_Circuit";
	private final String Edit_Trains = "Edit_Trains";

	private final String Setup_Margin = "Setup_MarginSet";
	private final String Setup_Time = "Setup_TimeSet";
	private final String Setup_Dist = "Setup_DistSet";

	private final String Tools_Circuit = "Tools_Circuit";
	private final String Tools_Train = "Tools_Train";
	
	private final String Help_About = "Help_About";

	private JMenuBar loadMenu() {
		JMenuBar jMenuBar = new JMenuBar();

		JMenu menuFile = createMenu(_("File"));
		menuFile.setMnemonic(KeyEvent.VK_F);
		menuFile.add(createMenuItem(_("New"), File_Clear_Chart)).setMnemonic(KeyEvent.VK_N);
		menuFile.add(createMenuItem(_("Open..."), File_Load_Chart)).setMnemonic(KeyEvent.VK_O);
		menuFile.addSeparator();
		menuFile.add(createMenuItem(_("SaveMenu"), File_Save_Chart)).setMnemonic(KeyEvent.VK_S);
		menuFile.add(createMenuItem(_("Save As..."), File_Save_Chart_As)).setMnemonic(KeyEvent.VK_A);
		menuFile.addSeparator();
//		menuFile.add(createMenuItem("更改线路...", File_Circuit)); //Bug:更改线路后没有清空车次
		menuFile.add(createMenuItem(_("Load Train..."), File_Train)).setMnemonic(KeyEvent.VK_L);
		menuFile.addSeparator();
		menuFile.add(createMenuItem(_("Export..."), File_Export)).setMnemonic(KeyEvent.VK_P);
		menuFile.addSeparator();
		menuFile.add(createMenuItem(_("Exit"), File_Exit)).setMnemonic(KeyEvent.VK_X);

		JMenu menuSetup = createMenu(_("Settings"));
		menuSetup.setMnemonic(KeyEvent.VK_S);
		menuSetup.add(createMenuItem(_("Margin..."), Setup_Margin)).setMnemonic(KeyEvent.VK_M);
		menuSetup.addSeparator();
		menuSetup.add(createMenuItem(_("Timeline..."), Setup_Time)).setMnemonic(KeyEvent.VK_T);
		menuSetup.add(createMenuItem(_("Distance Bar..."), Setup_Dist)).setMnemonic(KeyEvent.VK_D);

        JMenu menuEdit = createMenu(_("EditMenu"));
		menuEdit.setMnemonic(KeyEvent.VK_E);
        menuEdit.add(createMenuItem(_("Circuit..."), Edit_Circuit)).setMnemonic(KeyEvent.VK_C);
        menuEdit.add(createMenuItem(_("Train..."), Edit_Trains)).setMnemonic(KeyEvent.VK_R);
		menuEdit.addSeparator();
//		menuEdit.add(createMenuItem("车次录入...", Edit_NewTrain));
        menuEdit.add(createMenuItem(_("Find Train..."), Edit_FindTrain)).setMnemonic(KeyEvent.VK_F);
//		menuEdit.addSeparator();
//		menuEdit.add(createMenuItem("颜色设定...", Edit_Color));
		
        JMenu menuTools = createMenu(_("Tool"));
		menuTools.setMnemonic(KeyEvent.VK_T);
        menuTools.add(createMenuItem(_("Import Circuit..."), Tools_Circuit)).setMnemonic(KeyEvent.VK_C);
        menuTools.add(createMenuItem(_("Import Train..."), Tools_Train)).setMnemonic(KeyEvent.VK_R);

        JMenu menuHelp = createMenu(_("Help"));
		menuHelp.setMnemonic(KeyEvent.VK_H);
        menuHelp.add(createMenuItem(_("About..."), Help_About)).setMnemonic(KeyEvent.VK_A);

		jMenuBar.add(menuFile);
		jMenuBar.add(menuEdit);
		jMenuBar.add(menuSetup);
		jMenuBar.add(menuTools);
		jMenuBar.add(menuHelp);

		return jMenuBar;
	}

	private JMenu createMenu(String name) {
		JMenu menu = new JMenu(name);
		menu.setFont(new java.awt.Font(_("FONT_NAME"), 0, 12));

		return menu;
	}

	private JMenuItem createMenuItem(String name, String actionCommand) {
		JMenuItem menuItem = new JMenuItem(name);
		menuItem.setFont(new java.awt.Font(_("FONT_NAME"), 0, 12));

		menuItem.setActionCommand(actionCommand);
		menuItem.addActionListener(this);

		return menuItem;
	}

	private void initChart() {
		File chartFile = new File(prop.getProperty(Prop_Working_Chart));
		try {
			chart = new Chart(chartFile);
		} catch (IOException e) {
			System.out.println("Load old graph from last session failed, try to load the default graph.");
			try {
				chartFile = new File(Sample_Chart_File);
				chart = new Chart(chartFile);
			} catch (IOException e1) {
				new MessageBox(String.format(_("Load train graph failed, please check the %s file."), Sample_Chart_File)).showMessage();
				doExit();
			}
		}
		finally {
			prop.setProperty(Prop_Working_Chart, chartFile.getAbsolutePath());
		}
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if (command.equalsIgnoreCase(File_Exit)) {
			this.doExit();
		} else if (command.equalsIgnoreCase(Tools_Circuit)) {
			this.doCircuitTools();
		} else if (command.equalsIgnoreCase(Tools_Train)) {
			this.doTrainTools();
		} else if (command.equalsIgnoreCase(Setup_Margin)) {
			this.doMarginSet();
		} else if (command.equalsIgnoreCase(Setup_Time)) {
			this.doTimeSet();
		} else if (command.equalsIgnoreCase(Setup_Dist)) {
			this.doDistSet();
		} else if (command.equalsIgnoreCase(Edit_Circuit)) {
			this.doEditCircuit();
		} else if (command.equalsIgnoreCase(Edit_Trains)) {
			this.doEditTrains();
//		} else if (command.equalsIgnoreCase(Edit_NewTrain)) {
//			this.doNewTrain();
		} else if (command.equalsIgnoreCase(Edit_FindTrain)) {
			this.doFindTrain();
//		} else if (command.equalsIgnoreCase(Edit_Color)) {
//			this.doColorSet();
		} else if (command.equalsIgnoreCase(File_Train)) {
			this.doLoadTrain();
//		} else if (command.equalsIgnoreCase(File_Circuit)) {
//			this.doLoadCircuit();
		} else if (command.equalsIgnoreCase(File_Clear_Chart)) {
			this.doClearChart();
		} else if (command.equalsIgnoreCase(File_Save_Chart)) {
			this.doSaveChart();
		} else if (command.equalsIgnoreCase(File_Save_Chart_As)) {
			this.doSaveChartAs();
		} else if (command.equalsIgnoreCase(File_Load_Chart)) {
			this.doLoadChart();
		} else if (command.equalsIgnoreCase(File_Export)) {
			this.doExportChart();
		} else if (command.equalsIgnoreCase(Help_About)) {
			this.doHelpAbout();
		}
	}

	private void doTrainTools() {
		//new MessageBox(this, "todo：从网络获取数据生成车次描述文件(.trf文件)。").showMessage();
		if(new YesNoBox(this, _("This operation will delete all the train information on the graph, then import the train information from the default time table for this circuit. Continue?")).askForYes()) {
			FindTrainsDialog waitingBox = new FindTrainsDialog(this);
			waitingBox.findTrains();
		}
	}

	private void doCircuitTools() {
		//new MessageBox(this, "todo：从里程表获取数据生成线路描述文件(.cir文件)。").showMessage();
		String xianlu = new XianluSelectDialog(this).getXianlu();
		if(xianlu == null)
			return;
		
		Circuit circuit = new CircuitMakeDialog(this, xianlu).getCircuit();
		if(circuit == null)
			return;
		
		System.out.println(circuit);
		new CircuitEditDialog(this, circuit).showDialog();
		
		this.setTitle();
		chartView.repaint();
		runView.refresh();
		
		this.isNewCircuit = true;
	}

	private void doEditTrains() {
		TrainsDialog dlg = new TrainsDialog(this);
		Dimension dlgSize = dlg.getPreferredSize();
		Dimension frmSize = getSize();
		Point loc = getLocation();
		dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
				(frmSize.height - dlgSize.height) / 2 + loc.y);
		dlg.setModal(true);
		dlg.pack();
		dlg.setVisible(true);

		chartView.repaint();
		sheetView.updateData();
		runView.refresh();
	}

	private void doEditCircuit() {
		new CircuitEditDialog(this, this.chart.circuit.copy()).showDialog();
		
		this.setTitle();
		chartView.repaint();
		runView.refresh();
	}

	/**
	 * doFindTrain
	 */
	private void doFindTrain() {
		FindTrainDialog dlg = new FindTrainDialog(this);
		Dimension dlgSize = dlg.getPreferredSize();
		Dimension frmSize = getSize();
		Point loc = getLocation();
		dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
				(frmSize.height - dlgSize.height) / 2 + loc.y);
		dlg.setModal(false);
		dlg.pack();
		dlg.setVisible(true);
	}

	/**
	 * doHelpAbout
	 */
	private void doHelpAbout() {
		AboutBox dlg = new AboutBox(this);
		Dimension dlgSize = dlg.getPreferredSize();
		Dimension frmSize = getSize();
		Point loc = getLocation();
		dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
				(frmSize.height - dlgSize.height) / 2 + loc.y);
		dlg.setModal(true);
		dlg.pack();
		dlg.setVisible(true);
	}

	/**
	 * doChartEdit
	 */
//	private void doColorSet() {
//		TrainsDialog dlg = new TrainsDialog(this);
//		Dimension dlgSize = dlg.getPreferredSize();
//		Dimension frmSize = getSize();
//		Point loc = getLocation();
//		dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
//				(frmSize.height - dlgSize.height) / 2 + loc.y);
//		dlg.setModal(false);
//		dlg.pack();
//		dlg.setVisible(true);
//	}

	/**
	 * doClearChart
	 */
	private void doClearChart() {
		chart.clearTrains();
		chartView.repaint();
		sheetView.updateData();
		runView.refresh();
	}

	/**
	 * doExportChart
	 */
	public void doExportChart() {
		JFileChooser chooser = new JFileChooser();
		ETRC.setFont(chooser);

		chooser.setDialogTitle(_("Export Train Graph"));
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileFilter(new GIFFilter());
		chooser.setFont(new java.awt.Font(_("FONT_NAME"), 0, 12));
		
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String fileName = chart.circuit.name + df.format(new Date());
		chooser.setSelectedFile(new File(fileName));

		int returnVal = chooser.showOpenDialog(this); 
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			if (!f.getAbsolutePath().endsWith(".gif"))
				f = new File(f.getAbsolutePath() + ".gif");
			try {
				BufferedImage image = chartView.getBufferedImage();
				ImageIO.write(image, "gif", f);
			}
			catch(Exception ioe) {
				ioe.printStackTrace();
				this.statusBarMain.setText(_("Unable to export the graph."));
			}
		}
		

//		//获取默认打印作业
//		PrinterJob myPrtJob = PrinterJob.getPrinterJob();
//
//		//获取默认打印页面格式
//		//Paper paper = new Paper();
//		//paper.setImageableArea(80,80,1600,1000);
//		PageFormat pageFormat = myPrtJob.defaultPage();
//		pageFormat.setOrientation(PageFormat.REVERSE_LANDSCAPE);
//		//pageFormat.setPaper();
//
//		//设置打印工作
//		myPrtJob.setPrintable(this, pageFormat);
//
//		//显示打印对话框
//		if (myPrtJob.printDialog()) {
//			try {
//				//进行每一页的具体打印操作
//				//设置打印分辨率
//				//PrintRequestAttributeSet attrib = new HashPrintRequestAttributeSet();
//				//attrib.add(new PrinterResolution(290, 290, PrinterResolution.DPI));
//				myPrtJob.print();
//			}
//
//			catch (PrinterException pe) {
//				pe.printStackTrace();
//			}
//		}
	}

	/**
	 * print
	 *
	 * @param graphics Graphics
	 * @param pageFormat PageFormat
	 * @param pageIndex int
	 * @return int
	 */
	public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
		Graphics2D g2D = (Graphics2D) g;
		g2D.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
		Rectangle2D.Double rec1 = new Rectangle2D.Double(0.0, 0.0, pageFormat
				.getImageableWidth(), pageFormat.getImageableHeight());
		Rectangle2D.Double rec2 = new Rectangle2D.Double(0.0, 0.0, pageFormat
				.getImageableWidth() - 1.5,
				pageFormat.getImageableHeight() - 0.5);
		g2D.draw(rec1);
		g2D.draw(rec2);
		/*
		 Rectangle clip = g.getClipBounds();
		 g.drawRect(clip.x,clip.y,clip.width,clip.height);
		 g.drawLine(0,0,clip.width,clip.height);
		 g.drawLine(0,clip.height,clip.width,0);
		 g.drawString("W="+clip.width+" H="+clip.height,clip.x+20,clip.y+20);
		 System.out.println("W="+clip.width+" H="+clip.height);
		 return 0;
		 */
		return Printable.PAGE_EXISTS;
	}

	public void doSaveChart() {
		File savingFile = new File(prop.getProperty(Prop_Working_Chart));
		
		//如果是在Sample_Chart_File上操作的，则改调“另存为”
		if(savingFile.getName().equalsIgnoreCase(Sample_Chart_File)) {
			doSaveChartAs();
		}
		//如果已经做过载入过线路等操作，则改调“另存为”
		else if(isNewCircuit) {
			doSaveChartAs();
		}
		else {
			try {
				chart.saveToFile(savingFile);
			} catch (IOException ex) {
				System.err.println("Err:" + ex.getMessage());
				this.statusBarMain.setText(_("Unable to save the graph."));
			}
		}
	}

	/**
	 * doSaveChartAs
	 */
	public void doSaveChartAs() {
		JFileChooser chooser = new JFileChooser();
		ETRC.setFont(chooser);

		chooser.setDialogTitle(_("Save As"));
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileFilter(new TRCFilter());
		chooser.setFont(new java.awt.Font(_("FONT_NAME"), 0, 12));
		
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String fileName = chart.circuit.name + df.format(new Date());
		chooser.setSelectedFile(new File(fileName));

		int returnVal = chooser.showOpenDialog(this); 
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			if (!f.getAbsolutePath().endsWith(".trc"))
				f = new File(f.getAbsolutePath() + ".trc");
			//System.out.println(f);

			try {
				chart.saveToFile(f);
				prop.setProperty(Prop_Working_Chart, f.getAbsolutePath());
			} catch (IOException ex) {
				System.err.println("Err:" + ex.getMessage());
				this.statusBarMain.setText(_("Unable to save the graph."));
			}
		}
	}

	/**
	 * doLoadChart
	 */
	public void doLoadChart() {
		JFileChooser chooser = new JFileChooser();
		ETRC.setFont(chooser);

		chooser.setDialogTitle(_("Open"));
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileFilter(new TRCFilter());
		chooser.setFont(new java.awt.Font(_("FONT_NAME"), 0, 12));

		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			System.out.println(f);

			try {
				chart.loadFromFile(f);
				chartView.updateData();
				chartView.resetSize();

				sheetView.updateData();
				sheetView.refresh();
				
				runView.refresh();
				
				setTitle();
				prop.setProperty(Prop_Working_Chart, f.getAbsolutePath());
			} catch (IOException ex) {
				System.err.println("Err:" + ex.getMessage());
				statusBarMain.setText(_("Unable to load the graph."));
			}
		}
	}

	/**
	 * doLoadTrain
	 */
	public void doLoadTrain() {
		if(!(new YesNoBox(this, _("Load train information file and overwrite the existing information. Continue?")).askForYes()))
			return;

		JFileChooser chooser = new JFileChooser();
		ETRC.setFont(chooser);

		chooser.setDialogTitle(_("Load Train Information"));
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setMultiSelectionEnabled(true);
		chooser.addChoosableFileFilter(new CSVFilter());
		chooser.addChoosableFileFilter(new TRFFilter());
		chooser.setFont(new java.awt.Font(_("FONT_NAME"), 0, 12));

		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f[] = chooser.getSelectedFiles();
			if (f.length > 0)
				System.out.println(f[0]);
			else
				System.out.println("No File selected!");

			Train loadingTrain = null;
			for (int j = 0; j < f.length; j++) {
				loadingTrain = new Train();
				try {
					loadingTrain.loadFromFile(f[j].getAbsolutePath());
				} catch (IOException ex) {
					System.err.println("Error: " + ex.getMessage());
				}

//				System.out.println(loadingTrain.getTrainName() + "次列车从"
//						+ loadingTrain.startStation + "到"
//						+ loadingTrain.terminalStation + "，共经停"
//						+ loadingTrain.stopNum + "个车站");
//				for (int i = 0; i < loadingTrain.stopNum; i++)
//					System.out.println(loadingTrain.stops[i].stationName + "站 "
//							+ Train.toTrainFormat(loadingTrain.stops[i].arrive)
//							+ " 到 "
//							+ Train.toTrainFormat(loadingTrain.stops[i].leave)
//							+ " 发");

				if(loadingTrain.isDownTrain(chart.circuit, false) > 0)
					chart.addTrain(loadingTrain);
			}

			//System.out.println("1.Move to: "+loadingTrain.getTrainName());
			//mainView.buildTrainDrawings();
			chartView.repaint();
			sheetView.updateData();
			chartView.findAndMoveToTrain(loadingTrain.getTrainName(chart.circuit));
			runView.refresh();
			//panelChart.panelLines.repaint();
		}

	}

	/**
	 * doDistSet
	 */
	private void doDistSet() {
		DistSetDialog dlg = new DistSetDialog(this, chart);
		Dimension dlgSize = dlg.getPreferredSize();
		Dimension frmSize = getSize();
		Point loc = getLocation();
		dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
				(frmSize.height - dlgSize.height) / 2 + loc.y);
		dlg.setModal(false);
		dlg.pack();
		dlg.setVisible(true);
	}

	/**
	 * doTimeSet
	 */
	private void doTimeSet() {
		TimeSetDialog dlg = new TimeSetDialog(this, chart);
		Dimension dlgSize = dlg.getPreferredSize();
		Dimension frmSize = getSize();
		Point loc = getLocation();
		dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
				(frmSize.height - dlgSize.height) / 2 + loc.y);
		dlg.setModal(false);
		dlg.pack();
		dlg.setVisible(true);
	}

	private void doMarginSet() {
		MarginSetDialog dlg = new MarginSetDialog(this);
		Dimension dlgSize = dlg.getPreferredSize();
		Dimension frmSize = getSize();
		Point loc = getLocation();
		dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
				(frmSize.height - dlgSize.height) / 2 + loc.y);
		dlg.setModal(false);
		dlg.pack();
		dlg.setVisible(true);
	}
	
	private ETRCSKB skb = null;
	public ETRCSKB getSKB() {
		if(skb == null)
			try {
				skb = new ETRCSKB("eda/");
			} catch (IOException e) {
				new MessageBox(this, _("Unable to open time table.")).showMessage();
				e.printStackTrace();
			}
			
		return skb;
	}
	
	private ETRCLCB lcb = null;
	public ETRCLCB getLCB() {
		if(lcb == null)
			try {
				lcb = new ETRCLCB("eda/");
			} catch (IOException e) {
				new MessageBox(this, _("Unable to open circuit table.")).showMessage();
				e.printStackTrace();
			}
		
		return lcb;
	}
	
	private void changeShowRunState() {
		isShowRun = isShowRun ? false : true;
		updateShowRunState();
	}

	private void updateShowRunState() {
		jtButtonShowRun.setSelected(isShowRun);
		runView.setRunState(isShowRun);
		runView.setVisible(isShowRun);
		prop.setProperty(Prop_Show_Run, isShowRun ? "Y" : "N");
	}
	
	//Overridden so we can exit when window is closed
	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			this.doExit();
			super.processWindowEvent(e);
		} else if (e.getID() == WindowEvent.WINDOW_ACTIVATED) {
			super.processWindowEvent(e);
			chartView.requestFocus();
		}
	}
}
