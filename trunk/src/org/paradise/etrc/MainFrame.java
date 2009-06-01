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
import org.paradise.etrc.filter.TRCFilter;
import org.paradise.etrc.filter.TRFFilter;
import org.paradise.etrc.view.chart.ChartView;
import org.paradise.etrc.view.dynamic.DynamicView;
import org.paradise.etrc.view.sheet.SheetView;

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
	
	private static String Sample_Chart_File = "sample.trc";
	private static String Properties_File = "htrc.prop";
	
	public boolean isNewCircuit = false;
	public Chart chart;

	private static final int MAX_TRAIN_SELECT_HISTORY_RECORD = 12;
	public Vector trainSelectHistory;
	public JComboBox cbTrainSelectHistory;
	
	//Construct the frame
	public MainFrame() {
		defaultProp = new Properties();
		defaultProp.setProperty(Prop_Working_Chart, Sample_Chart_File);
		defaultProp.setProperty(Prop_Show_UP, "Y");
		defaultProp.setProperty(Prop_Show_Down, "N");
		defaultProp.setProperty(Prop_Show_Run, "Y");
		
		prop = new Properties(defaultProp);
		
		trainSelectHistory = new Vector();
		cbTrainSelectHistory = new JComboBox(new DefaultComboBoxModel(trainSelectHistory));
		
		try {
			prop.load(new FileInputStream(Properties_File));
		} catch (IOException e) {
			e.printStackTrace();
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
			if(new YesNoBox(this, "是否在退出前保存运行图？").askYesNo())
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
		this.setFont(new java.awt.Font("宋体", 0, 10));
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
		this.setFont(new java.awt.Font("宋体", 0, 10));
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
	private static final String titlePrefix = "<[ LGuo的电子运行图 ]>";

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
		statusBar.setFont(new java.awt.Font("宋体", 0, 12));
		statusBar.setText("状态：正常 <lguo@sina.com>");
		return statusBar;
	}

	public JToggleButton jtButtonDown;
	public JToggleButton jtButtonShowRun;
	public JToggleButton jtButtonUp;

	private JToolBar loadToolBar() {
		JToolBar jToolBar = new JToolBar();
		
		//文件操作
		JButton jbOpenFile = createTBButton("openFile", "Open A Chart", File_Load_Chart);
		JButton jbSaveFile = createTBButton("saveFile", "Save The Chart", File_Save_Chart);
		JButton jbSaveAs   = createTBButton("saveAs", "Save The Chart As ...", File_Save_Chart_As);
		jToolBar.add(jbOpenFile);
		jToolBar.add(jbSaveFile);
		jToolBar.add(jbSaveAs);
		
		//车次、线路编辑
		JButton jbCircuitEdit = createTBButton("circuit", "Edit The Circuit", Edit_Circuit);
		JButton jbTrainsEdit  = createTBButton("trains", "Edit The Trains", Edit_Trains);
		jToolBar.addSeparator();
		jToolBar.add(jbCircuitEdit);
		jToolBar.add(jbTrainsEdit);
		
		//查找车次
		JButton jbFindTrain = createTBButton("findTrain", "Find A Train", Edit_FindTrain);
		jToolBar.addSeparator();
		jToolBar.add(jbFindTrain);

		//坐标设置
		JButton jbSetupH = createTBButton("setupH", "Setup Time Coordinate", Setup_Time);
		JButton jbSetupV = createTBButton("setupV", "Setup Distance Coordinate", Setup_Dist);
		jToolBar.addSeparator();
		jToolBar.add(jbSetupH);
		jToolBar.add(jbSetupV);
		
		//动态图是否开启
		ImageIcon imageRun = new ImageIcon(this.getClass().getResource("/pic/show_run.png"));
		jtButtonShowRun = new JToggleButton(imageRun);
		jtButtonShowRun.setToolTipText("Show Dynamic Chart");
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
		jtButtonUp.setToolTipText("Display Up-Going Trains");
		jtButtonDown.setToolTipText("Display Down-Going Trains");
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
					new MessageBox(MainFrame.this, "没有找到" + trainToFind + "次列车！").showMessage();
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
	private final String File_Print = "File_Print";
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

		JMenu menuFile = createMenu("文件");
		menuFile.add(createMenuItem("清空运行图", File_Clear_Chart));
		menuFile.add(createMenuItem("打开运行图...", File_Load_Chart));
		menuFile.addSeparator();
		menuFile.add(createMenuItem("保存运行图", File_Save_Chart));
		menuFile.add(createMenuItem("运行图另存为...", File_Save_Chart_As));
		menuFile.addSeparator();
//		menuFile.add(createMenuItem("更改线路...", File_Circuit)); //Bug:更改线路后没有清空车次
		menuFile.add(createMenuItem("载入车次...", File_Train));
		menuFile.addSeparator();
		menuFile.add(createMenuItem("打印运行图...", File_Print));
		menuFile.addSeparator();
		menuFile.add(createMenuItem("退出", File_Exit));

		JMenu menuSetup = createMenu("设置");
		menuSetup.add(createMenuItem("边距设定...", Setup_Margin));
		menuSetup.addSeparator();
		menuSetup.add(createMenuItem("时间轴设定...", Setup_Time));
		menuSetup.add(createMenuItem("距离轴设定...", Setup_Dist));

		JMenu menuEdit = createMenu("编辑");
		menuEdit.add(createMenuItem("线路编辑...", Edit_Circuit));
		menuEdit.add(createMenuItem("车次编辑...", Edit_Trains));
		menuEdit.addSeparator();
//		menuEdit.add(createMenuItem("车次录入...", Edit_NewTrain));
		menuEdit.add(createMenuItem("车次查找...", Edit_FindTrain));
//		menuEdit.addSeparator();
//		menuEdit.add(createMenuItem("颜色设定...", Edit_Color));
		
		JMenu menuTools = createMenu("工具");
		menuTools.add(createMenuItem("线路导入...", Tools_Circuit));
		menuTools.add(createMenuItem("车次导入...", Tools_Train));

		JMenu menuHelp = createMenu("帮助");
		menuHelp.add(createMenuItem("关于...", Help_About));

		jMenuBar.add(menuFile);
		jMenuBar.add(menuEdit);
		jMenuBar.add(menuSetup);
		jMenuBar.add(menuTools);
		jMenuBar.add(menuHelp);

		return jMenuBar;
	}

	private JMenu createMenu(String name) {
		JMenu menu = new JMenu(name);
		menu.setFont(new java.awt.Font("宋体", 0, 12));

		return menu;
	}

	private JMenuItem createMenuItem(String name, String actionCommand) {
		JMenuItem menuItem = new JMenuItem(name);
		menuItem.setFont(new java.awt.Font("宋体", 0, 12));

		menuItem.setActionCommand(actionCommand);
		menuItem.addActionListener(this);

		return menuItem;
	}

	private void initChart() {
		File chartFile = new File(prop.getProperty(Prop_Working_Chart));
		try {
			chart = new Chart(chartFile);
		} catch (IOException e) {
			System.out.println("上次工作的运行图文件载入错误，尝试载入默认的运行图");
			try {
				chartFile = new File(Sample_Chart_File);
				chart = new Chart(chartFile);
			} catch (IOException e1) {
				new MessageBox("运行图文件载入失败，请在检查"+Sample_Chart_File+"文件").showMessage();
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
		} else if (command.equalsIgnoreCase(File_Print)) {
			this.doPrintChart();
		} else if (command.equalsIgnoreCase(Help_About)) {
			this.doHelpAbout();
		}
	}

	private void doTrainTools() {
		//new MessageBox(this, "todo：从网络获取数据生成车次描述文件(.trf文件)。").showMessage();
		if(new YesNoBox(this, "删除所有车次，从本系统自带的时刻表中自动导入经过本线路（停2站以上）的车次，是否继续？").askYesNo()) {
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
		dlg.setModal(false);
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
	 * doPrint
	 */
	private void doPrintChart() {
		new MessageBox(this, "JAVA的打印能力太弱，不想做这个功能了。拟改图片输出，请查看 C:\\ETRC.gif。 ").showMessage();
		
		try {
			BufferedImage image = chartView.getBufferedImage();

			ImageIO.write(image, "gif", new File("c:\\ETRC.gif"));
		}
		catch(Exception ioe) {
			ioe.printStackTrace();
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

	private void doSaveChart() {
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
				this.statusBarMain.setText("保存运行图出错！");
			}
		}
	}

	/**
	 * doSaveChartAs
	 */
	private void doSaveChartAs() {
		JFileChooser chooser = new JFileChooser();
		ETRC.setFont(chooser);

		chooser.setDialogTitle("保存运行图");
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileFilter(new TRCFilter());
		chooser.setFont(new java.awt.Font("宋体", 0, 12));
		
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
				this.statusBarMain.setText("保存运行图出错！");
			}
		}
	}

	/**
	 * doLoadChart
	 */
	private void doLoadChart() {
		JFileChooser chooser = new JFileChooser();
		ETRC.setFont(chooser);

		chooser.setDialogTitle("载入运行图");
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileFilter(new TRCFilter());
		chooser.setFont(new java.awt.Font("宋体", 0, 12));

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
				statusBarMain.setText("载入运行图出错！");
			}
		}
	}

	/**
	 * doLoadTrain
	 */
	public void doLoadTrain() {
		if(!(new YesNoBox(this, "（批量）读入车次文件，覆盖已经存在的车次，是否继续？").askYesNo()))
			return;

		JFileChooser chooser = new JFileChooser();
		ETRC.setFont(chooser);

		chooser.setDialogTitle("载入车次");
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setMultiSelectionEnabled(true);
		chooser.addChoosableFileFilter(new CSVFilter());
		chooser.addChoosableFileFilter(new TRFFilter());
		chooser.setFont(new java.awt.Font("宋体", 0, 12));

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

				if(loadingTrain.isDownTrain(chart.circuit) > 0)
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
				new MessageBox(this, "打开时刻表文件失败！").showMessage();
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
				new MessageBox(this, "打开里程表文件失败！").showMessage();
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
