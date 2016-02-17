package org.paradise.etrc;

import org.paradise.etrc.data.Chart;
import org.paradise.etrc.data.Circuit;
import org.paradise.etrc.data.Train;
import org.paradise.etrc.data.skb.ETRCLCB;
import org.paradise.etrc.data.skb.ETRCSKB;
import org.paradise.etrc.dialog.AboutBox;
import org.paradise.etrc.dialog.ChartSetDialog;
import org.paradise.etrc.dialog.CircuitEditDialog;
import org.paradise.etrc.dialog.CircuitMakeDialog;
import org.paradise.etrc.dialog.DistSetDialog;
import org.paradise.etrc.dialog.FindTrainDialog;
import org.paradise.etrc.dialog.FindTrainsDialog;
import org.paradise.etrc.dialog.MarginSetDialog;
import org.paradise.etrc.dialog.MessageBox;
import org.paradise.etrc.dialog.TimeSetDialog;
import org.paradise.etrc.dialog.TrainsDialog;
import org.paradise.etrc.dialog.XianluSelectDialog;
import org.paradise.etrc.dialog.YesNoBox;
import org.paradise.etrc.filter.CSVFilter;
import org.paradise.etrc.filter.GIFFilter;
import org.paradise.etrc.filter.TRCFilter;
import org.paradise.etrc.filter.TRFFilter;
import org.paradise.etrc.view.chart.ChartView;
import org.paradise.etrc.view.dynamic.DynamicView;
import org.paradise.etrc.view.sheet.SheetView;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;

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
    public int type_case;
    public String my_regexp;
    public JLabel statusBarMain = new JLabel();
    public JLabel statusBarRight = new JLabel();
    //OrderedProperties 为自定义可排序属性，在根目录下
    private OrderedProperties defaultProp;
    public OrderedProperties prop;
    public static String Prop_Working_Chart = "Working_Chart";
    public static String Prop_Show_UP = "Show_UP";
    public static String Prop_Show_Down = "Show_Down";
    public static String Prop_Show_Run = "Show_Run";
    public static String Prop_HTTP_Proxy_Server = "HTTP_Proxy_Server";
    public static String Prop_HTTP_Proxy_Port = "HTTP_Proxy_Port";
    //2014-07-10 Joe add below setting to be supported in htrc.proc
    //定义一个新属性，需要3步，  第65 行： 定义属性字符串，第102行绑定属性， 第300+行 设定属性具体行为
    public static String Prop_underDrawingColor = "underDrawingColor";
    public static String Prop_isDrawNormalPoint = "isDrawNormalPoint";
    public static String Prop_lineWidth = "lineWidth";
    public static String Prop_G_color = "G_color";
    public static String Prop_D_color = "D_color";
    public static String Prop_C_color = "C_color";
    public static String Prop_Z_color = "Z_color";
    public static String Prop_T_color = "T_color";
    public static String Prop_K_color = "K_color";
    public static String Prop_Y_color = "Y_color";
    public static String Prop_L_color = "L_color";
    public static String Prop_default_color = "default_color";
    public static String Prop_grid_color = "grid_color";
    public static String Prop_halfHourDashGrid = "halfHourDashGrid";
    //2015-07-13 Joe增加属性
    public static String Prop_dashsiding = "dashsiding";
    public static String Prop_showdistance = "showdistance";
    public static String Prop_nightmode = "nightmode";
    public static String Prop_showtrainstartend = "showtrainstartend";


    private static String Sample_Chart_File = "sample.trc";
    private static String Properties_File = "htrc.prop";

    public boolean isNewCircuit = false;
    public Chart chart;

    private static final int MAX_TRAIN_SELECT_HISTORY_RECORD = 12;
    public Vector<String> trainSelectHistory;
    public JComboBox cbTrainSelectHistory;

    //Construct the frame
    public MainFrame() {
        defaultProp = new OrderedProperties();
        defaultProp.setProperty(Prop_Working_Chart, Sample_Chart_File);
        defaultProp.setProperty(Prop_Show_UP, "Y");
        defaultProp.setProperty(Prop_Show_Down, "Y");
        defaultProp.setProperty(Prop_Show_Run, "Y");
        defaultProp.setProperty(Prop_HTTP_Proxy_Server, "");
        defaultProp.setProperty(Prop_HTTP_Proxy_Port, "");
        defaultProp.setProperty(Prop_underDrawingColor, "N");
        defaultProp.setProperty(Prop_isDrawNormalPoint, "N");
        defaultProp.setProperty(Prop_lineWidth, "3");
        defaultProp.setProperty(Prop_G_color, "FF00BE"); //Color(255, 0, 190), pink
        defaultProp.setProperty(Prop_D_color, "800080"); //Color(128, 0, 128) purple
        defaultProp.setProperty(Prop_C_color, "800080"); //Color(128, 0, 128) purple
        defaultProp.setProperty(Prop_T_color, "0000FF");//Color(0, 0, 255); blue
        defaultProp.setProperty(Prop_Z_color, "804000"); //Color(128, 64, 0); brown
        defaultProp.setProperty(Prop_K_color, "FF0000"); //Color(255, 0, 0); red
        defaultProp.setProperty(Prop_Y_color, "FF0000"); //Color(255, 0, 0); red
        defaultProp.setProperty(Prop_L_color, "008000"); //Color(0, 128, 0); dark green
        defaultProp.setProperty(Prop_default_color, "008000"); //Color(0, 128, 0); dark green
        defaultProp.setProperty(Prop_grid_color, "008000"); //Color(0,128,0);dark green
        defaultProp.setProperty(Prop_halfHourDashGrid, "Y"); // enable dash dot line for half hour clock grid

        defaultProp.setProperty(Prop_dashsiding, "Y"); // enable dash  line for the siding stations
        defaultProp.setProperty(Prop_showdistance, "Y"); // show current train distance in dynamic views
        defaultProp.setProperty(Prop_nightmode, "Y"); // nigh modes for dynamic views
        defaultProp.setProperty(Prop_showtrainstartend, "Y"); // show train start end sation name for the train name textbox


        //prop = new Properties(defaultProp); //prop改成OrderedProperties之后必须注释掉本行，否则出错
        prop = defaultProp;
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
        if (chart != null)
            if (new YesNoBox(this, _("The graph preference will be auto-saved.\r Besides, do you want to save the graph change?\r")).askForYes())
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
            if (trainSelectHistory.contains(_name))
                trainSelectHistory.remove(_name);

            trainSelectHistory.add(0, _name);

            //超过最大历史记录数，则删除最老的
            if (trainSelectHistory.size() > MAX_TRAIN_SELECT_HISTORY_RECORD)
                for (int i = 12; i < trainSelectHistory.size(); i++)
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
        JButton jbSaveAs = createTBButton("saveAs", _("Save Chart As"), File_Save_Chart_As);
        jToolBar.add(jbOpenFile);
        jToolBar.add(jbSaveFile);
        jToolBar.add(jbSaveAs);

        //车次、线路编辑
        JButton jbCircuitEdit = createTBButton("circuit", _("Edit Circuit"), Edit_Circuit);
        JButton jbTrainsEdit = createTBButton("trains", _("Edit Train Information"), Edit_Trains);
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
        //jToolBar.addSeparator();
        //JButton jbSetupVV = createTBButton("setupVV", _("Overall Chart Settings"), Setup_Chart);
        //jToolBar.add(jbSetupVV);


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
        if (prop.getProperty(Prop_Show_Run).equalsIgnoreCase("Y")) {
            isShowRun = true;
        } else {
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
        if (prop.getProperty(Prop_Show_Down).equalsIgnoreCase("Y")) {
            jtButtonDown.setSelected(true);
            chartView.showUpDownState ^= ChartView.SHOW_DOWN;
        } else
            jtButtonDown.setSelected(false);
        if (prop.getProperty(Prop_Show_UP).equalsIgnoreCase("Y")) {
            jtButtonUp.setSelected(true);
            chartView.showUpDownState ^= ChartView.SHOW_UP;
        } else
            jtButtonUp.setSelected(false);
        //2014-07-10 Joe add below setting to be supported in htrc.proc
        if (prop.getProperty(Prop_underDrawingColor).equalsIgnoreCase("N")) {
            chartView.DEFAULT_UNDER_COLOR = new Color(220, 220, 220);
            chartView.underDrawingColor = null;//当水印色为null的时候不画反向车次
            prop.setProperty(Prop_underDrawingColor, "N");
        } else
        //2015-06-18 read in the underdrawing color setting from the htrc.prop file
        {
            chartView.DEFAULT_UNDER_COLOR = new Color(Integer.parseInt(prop.getProperty(Prop_underDrawingColor), 16));
            chartView.underDrawingColor = chartView.DEFAULT_UNDER_COLOR;
            prop.setProperty(Prop_underDrawingColor, prop.getProperty(Prop_underDrawingColor));
        }
        if (prop.getProperty(Prop_isDrawNormalPoint).equalsIgnoreCase("N")) {
            chartView.isDrawNormalPoint = false;
            prop.setProperty(Prop_isDrawNormalPoint, "N");
        } else {
            chartView.isDrawNormalPoint = true;
            prop.setProperty(Prop_isDrawNormalPoint, "Y");
        }

        // the width of train drawing lines,  0.5 * the width number
        if (prop.getProperty(Prop_lineWidth).equalsIgnoreCase("1")) {
            chartView.lineWidth = 0.5f;
            prop.setProperty(Prop_lineWidth, "1");
        } else if (prop.getProperty(Prop_lineWidth).equalsIgnoreCase("2")) {
            chartView.lineWidth = 1.0f;
            prop.setProperty(Prop_lineWidth, "2");
        } else if (prop.getProperty(Prop_lineWidth).equalsIgnoreCase("3")) {
            chartView.lineWidth = 1.5f;
            prop.setProperty(Prop_lineWidth, "3");
        } else if (prop.getProperty(Prop_lineWidth).equalsIgnoreCase("4")) {
            chartView.lineWidth = 2.0f;
            prop.setProperty(Prop_lineWidth, "4");
        } else
        //default setting the same as previous version
        {
            chartView.lineWidth = 0.5f;
            prop.setProperty(Prop_lineWidth, "1");
        }

        if (prop.getProperty(Prop_halfHourDashGrid).equalsIgnoreCase("Y")) {
            chartView.halfHourDashGrid = true;
            prop.setProperty(Prop_halfHourDashGrid, "Y");
        } else {
            chartView.halfHourDashGrid = false;
            prop.setProperty(Prop_halfHourDashGrid, "N");
        }


        if (prop.getProperty(Prop_dashsiding).equalsIgnoreCase("Y")) {
            chartView.DASHSIDING = true;
            prop.setProperty(Prop_dashsiding, "Y");
        } else {
            chartView.DASHSIDING = false;
            prop.setProperty(Prop_dashsiding, "N");
        }


        if (prop.getProperty(Prop_showdistance).equalsIgnoreCase("Y")) {
            chartView.SHOWDISTANCE = true;
            prop.setProperty(Prop_showdistance, "Y");
        } else {
            chartView.SHOWDISTANCE = false;
            prop.setProperty(Prop_showdistance, "N");
        }

        if (prop.getProperty(Prop_nightmode).equalsIgnoreCase("Y")) {
            chartView.NIGHTMODE = true;
            prop.setProperty(Prop_nightmode, "Y");
        } else {
            chartView.NIGHTMODE = false;
            prop.setProperty(Prop_nightmode, "N");
        }

        if (prop.getProperty(Prop_showtrainstartend).equalsIgnoreCase("Y")) {
            chartView.SHOWTRAIN_StartEnd = true;
            prop.setProperty(Prop_showtrainstartend, "Y");
        } else {
            chartView.SHOWTRAIN_StartEnd = false;
            prop.setProperty(Prop_showtrainstartend, "N");
        }


        String c1 = prop.getProperty(Prop_G_color);
        String c2 = prop.getProperty(Prop_D_color);
        String c3 = prop.getProperty(Prop_C_color);
        String c4 = prop.getProperty(Prop_Z_color);
        String c5 = prop.getProperty(Prop_T_color);
        String c6 = prop.getProperty(Prop_K_color);
        String c7 = prop.getProperty(Prop_L_color);
        String c8 = prop.getProperty(Prop_Y_color);
        String c9 = prop.getProperty(Prop_default_color);
        String c10 = prop.getProperty(Prop_grid_color);
// use 	Integer.parseInt(c1, 16) to cnvert the hex "string" to the color	
        chartView.G_color_chartview = new Color(Integer.parseInt(c1, 16));
        chartView.D_color_chartview = new Color(Integer.parseInt(c2, 16));
        chartView.C_color_chartview = new Color(Integer.parseInt(c3, 16));
        chartView.Z_color_chartview = new Color(Integer.parseInt(c4, 16));
        chartView.T_color_chartview = new Color(Integer.parseInt(c5, 16));
        chartView.K_color_chartview = new Color(Integer.parseInt(c6, 16));
        chartView.L_color_chartview = new Color(Integer.parseInt(c7, 16));
        chartView.Y_color_chartview = new Color(Integer.parseInt(c8, 16));
        chartView.default_color_chartview = new Color(Integer.parseInt(c9, 16));
        chartView.gridColor = new Color(Integer.parseInt(c10, 16));
        //chartView.gridColor = Color.GRAY;//808080, the color setting of previous version
        prop.setProperty(Prop_G_color, c1);
        prop.setProperty(Prop_D_color, c2);
        prop.setProperty(Prop_C_color, c3);
        prop.setProperty(Prop_Z_color, c4);
        prop.setProperty(Prop_T_color, c5);
        prop.setProperty(Prop_K_color, c6);
        prop.setProperty(Prop_L_color, c7);
        prop.setProperty(Prop_Y_color, c8);
        prop.setProperty(Prop_default_color, c9);
        prop.setProperty(Prop_grid_color, c10);
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
        cbTrainSelectHistory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                //当用键盘输入的时候会触发两次Action
                //一次是comboBoxChanged，另一次是comboBoxEdited
                //我们只处理与下拉选择一样的那一次：comboBoxChanged
                if (!ae.getActionCommand().equalsIgnoreCase("comboBoxChanged"))
                    return;

                String trainToFind = (String) cbTrainSelectHistory.getSelectedItem();

                if (trainToFind == null)
                    return;

                if (trainToFind.trim().equalsIgnoreCase(""))
                    return;

                if (!chartView.findAndMoveToTrain(trainToFind))
                    new MessageBox(MainFrame.this, String.format(_("Cannot find train information: %s"), trainToFind)).showMessage();
            }
        });
        jToolBar.addSeparator();
        jToolBar.add(cbTrainSelectHistory);

        return jToolBar;
    }

    private JButton createTBButton(String imgName, String toolTipText, String Command) {
        JButton jbOnToolBar = new JButton();

        ImageIcon imageOpenFile = new ImageIcon(org.paradise.etrc.MainFrame.class
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
    private final String Setup_Chart = "Setup_ChartSet";
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
        //new added
        menuSetup.addSeparator();
        menuSetup.add(createMenuItem(_("ChartSet..."), Setup_Chart)).setMnemonic(KeyEvent.VK_W);


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
        } finally {
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
        } else if (command.equalsIgnoreCase(Setup_Chart)) {
            this.doChartSet();
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


        if (new YesNoBox(this, _("This operation will delete all the train information on the graph, then import the train information from the default time table for this circuit. Continue?")).askForYes()) {
            //以下设置影响FindTrainsDialog.java
            // if ( new YesNoBox(this, _("only common trains? 是否排除动车组（G/C/D)车次即只铺画普通机辆车次（Z/T/K/L/Y/xxxx)？")).askForYes()) {
            // //仅铺画普通列车
            // type_case  = 1;
            // }
            // else
            // {
            // if ( new YesNoBox(this, _("only CRH? 是否排除普通机辆（Z/T/K/L/Y/xxxx)车次即只铺画动车组？")).askForYes()) {
            // //仅铺画动车组
            // type_case = 2;
            // }
            // else
            // {//铺画全部列车
            // type_case = 3;
            // }

            // }
            //以下设置影响FindTrainsDialog.java
            my_regexp = JOptionPane.showInputDialog(null, "Please input the regular expression to define the trains which you want to chart\r请输入用来描述你要铺画的列车车次的简单正则表达式:", "Z.*|T.*|L.*|K.*|Y.*|S.*|G.*|D.*|C.*|^\\d.*");
            //my_regexp = JOptionPane.showInputDialog(null,"Please input the regular expression to define the trains which you want to chart:Z.*|T.*|L.*|K.*|Y.*|S.*|G.*|D.*|C.*|^\\d.*","Z.*|T.*|L.*|K.*|Y.*|S.*|G.*|D.*|C.*|^\\d.*");
            //下面是一个不完整的正则表达式用例
            //System.out.println(inputValue.matches(".*joe.*"));
            FindTrainsDialog waitingBox = new FindTrainsDialog(this);
            waitingBox.findTrains();
        }
    }

    private void doCircuitTools() {
        //new MessageBox(this, "todo：从里程表获取数据生成线路描述文件(.cir文件)。").showMessage();
        String xianlu = new XianluSelectDialog(this).getXianlu();
        if (xianlu == null)
            return;

        Circuit circuit = new CircuitMakeDialog(this, xianlu).getCircuit();
        if (circuit == null)
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
            } catch (Exception ioe) {
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
     * @param graphics   Graphics
     * @param pageFormat PageFormat
     * @param pageIndex  int
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
        if (savingFile.getName().equalsIgnoreCase(Sample_Chart_File)) {
            doSaveChartAs();
        }
        //如果已经做过载入过线路等操作，则改调“另存为”
        else if (isNewCircuit) {
            doSaveChartAs();
        } else {
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
        File chartFile = new File(prop.getProperty(Prop_Working_Chart));
        JFileChooser chooser = new JFileChooser(chartFile);
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
        File chartFile = new File(prop.getProperty(Prop_Working_Chart));
        JFileChooser chooser = new JFileChooser(chartFile);
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
        if (!(new YesNoBox(this, _("Load train information file and overwrite the existing information. Continue?")).askForYes()))
            return;
        File chartFile = new File(prop.getProperty(Prop_Working_Chart));
        JFileChooser chooser = new JFileChooser(chartFile);
        //change the default directly when filechooser dialog show to the working_chart dir in htrc props.
        //JFileChooser chooser = new JFileChooser();
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

                if (loadingTrain.isDownTrain(chart.circuit, false) > 0)
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
     * doChartSet
     */
    private void doChartSet() {
        ChartSetDialog dlg = new ChartSetDialog(this);
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
        if (skb == null)
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
        if (lcb == null)
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




