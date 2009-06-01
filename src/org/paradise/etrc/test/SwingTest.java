package org.paradise.etrc.test;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;

/** 
 * Swing 组件测试程序
 * 测试Swing所有组件及其相应的事件
 * @author 天翼.李 2003.4.17 晚23:14
 * @link http://www.robochina.org
 * @link robococde@etang.com
 */
public class SwingTest extends JFrame
{
    /**
     * 主模块，初始化所有子模块，并设置主框架的相关属性
     */
    public SwingTest()
    {
        // 初始化所有模块
        MenuTest menuTest = new MenuTest();
        LeftPanel leftPanel = new LeftPanel();
        RightPanel rightPanel = new RightPanel();
        BottomPanel bottomPanel = new BottomPanel();
        CenterPanel centerPanel = new CenterPanel();
        
        // 设置主框架的布局
        Container c = this.getContentPane();
        // c.setLayout(new BorderLayout())
        this.setJMenuBar(menuTest);
        
        c.add(leftPanel,BorderLayout.WEST);
        c.add(rightPanel,BorderLayout.EAST);
        c.add(centerPanel,BorderLayout.CENTER);
        c.add(bottomPanel,BorderLayout.SOUTH);
        
        // 利用无名内隐类，增加窗口事件
        this.addWindowListener(new WindowAdapter()
            {
                public void WindowClosing(WindowEvent e)
                {   
                    // 释放资源，退出程序
                    dispose();
                    System.exit(0);
                }
            });
            
        
        
        setSize(700,500);
        setTitle("Swing 组件大全简体版");
        // 隐藏frame的标题栏,此功暂时关闭，以方便使用window事件
        // setUndecorated(true);
        setLocation(200,150);
        show();        
    }

    ////////////////////////////////////////////////////////////////////////////
    /**
     * 菜单栏处理模块
     * JMenuBar --+
     *            --JMenu--+
     *                       --JMenuItem  --ActionListener 
     *              
     */
    class MenuTest extends JMenuBar
    {
        private JDialog aboutDialog;
            
        /**
         * 菜单初始化操作
         */    
        public MenuTest()
        {
            JMenu fileMenu = new JMenu("文件");
            JMenuItem exitMenuItem = new JMenuItem("退出",KeyEvent.VK_E);
            JMenuItem aboutMenuItem = new JMenuItem("关于...",KeyEvent.VK_A);            
                                                
            fileMenu.add(exitMenuItem);
            fileMenu.add(aboutMenuItem);
            
            this.add(fileMenu);        
            
                    
            aboutDialog = new JDialog();
            initAboutDialog();
                        
            // 菜单事件
            exitMenuItem.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    dispose();
                    System.exit(0);
                }
            });
            
            aboutMenuItem.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    // "关于"对话框的处理
                    aboutDialog.show();
                }
            });            
                        
        }
        
        /**
         * 返回关于对话框
         */
        public JDialog getAboutDialog()
        {
            return aboutDialog;
        }
        
        /**
         * 设置"关于"对话框的外观及响应事件,操作和JFrame一样都是在内容
         * 框架上进行的
         */
        public void initAboutDialog()
        {
            aboutDialog.setTitle("关于");
            
            Container con =aboutDialog.getContentPane();
             
            // Swing 中使用html语句
            Icon icon = new ImageIcon("smile.gif");
            JLabel aboutLabel = new JLabel("<html><b><font size=5>"+
            "<center>Swing 组件大全简体版！"+"<br>天翼.李",icon,JLabel.CENTER);
                        
            //JLabel aboutLabel = new JLabel("Swing 组件大全简体版！",icon,JLabel.CENTER);
            con.add(aboutLabel,BorderLayout.CENTER);
            
            aboutDialog.setSize(450,225);
            aboutDialog.setLocation(300,300);                        
            aboutDialog.addWindowListener(new WindowAdapter()
            {
                public void WindowClosing(WindowEvent e)
                {
                    dispose();
                }                    
            });            
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /**
     * 最左边模块，继承JPanel,初始化内容为JTree
     * JPanel--+
     *         --JTree
     */
    class LeftPanel extends JPanel
    {
        private int i = 0;
        public LeftPanel()
        {
            
            DefaultMutableTreeNode    root = new DefaultMutableTreeNode("Root");
            DefaultMutableTreeNode child = new DefaultMutableTreeNode("Child");
            DefaultMutableTreeNode select = new DefaultMutableTreeNode("select");
            
            DefaultMutableTreeNode child1 = new DefaultMutableTreeNode(""+i);
            
            root.add(child);        
            root.add(select);
            child.add(child1);    
            
            JTree tree = new JTree(root);
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
            
            // 每个节点的行高
            tree.setRowHeight(20);            
            tree.addTreeSelectionListener(new TreeSelectionListener ()
            {
                public void valueChanged(TreeSelectionEvent e)
                {
                    // 内隐类不能直接引用外部类tree，1.外部变量可申明为final 2.新建外部类的对象
                    JTree tree =(JTree)e.getSource();
                    DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
                    i++;
                    selectNode.add(new DefaultMutableTreeNode(""+i));
                }
            });            
            
            tree.setPreferredSize(new Dimension(100,300));
        //    tree.setEnabled(true);
            JScrollPane scrollPane = new JScrollPane(tree);
            //scrollPane.setSize(100,350);
            this.add(scrollPane);
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /**
     * 最下面层模块，继承JPanel,初始化内容为进度条，并由定时器控制
     * JPanel--+
     *         --JProcessBar  --Timer
     */
    class BottomPanel extends JPanel
    {
        private JProgressBar pb;
        ////////////////////////////////////////
        //public class 
        //////////////////////////////
        public BottomPanel()
        {
            pb = new JProgressBar();
            pb.setPreferredSize(new Dimension(680,20));
            
            // 设置定时器，用来控制进度条的处理
            Timer time = new Timer(1,new ActionListener()
            { 
                int counter = 0;
                public void actionPerformed(ActionEvent e)
                {
                    counter++;
                    pb.setValue(counter);
                    Timer t = (Timer)e.getSource();
                    
                    // 如果进度条达到最大值重新开发计数
                    if (counter == pb.getMaximum())
                    {
                        t.stop();
                        counter =0;
                        t.start();
                    }                    
                }
            });
            time.start();
            
            pb.setStringPainted(true);
            pb.setMinimum(0);
            pb.setMaximum(1000);
            pb.setBackground(Color.white);
            pb.setForeground(Color.red);
                        
            this.add(pb);                
        }
        
        /**
         * 设置进度条的数据模型
         */
        public void setProcessBar(BoundedRangeModel rangeModel)
        {
            pb.setModel(rangeModel);
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /**
     * 最右边模块，继承JPanel,初始化各种按钮
     * JPanel--+
     *         --JButton  --JToggleButton -- JList -- JCombox --JCheckBox ....
     */
    class RightPanel extends JPanel
    {
        public RightPanel()
        {
            this.setLayout(new GridLayout(8,1));        
            
            
            // 初始化各种按钮
            JCheckBox checkBox = new JCheckBox("复选按钮");            
            JButton button = new JButton("打开文件");
            button.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    JFileChooser file = new JFileChooser();
                    int result = file.showOpenDialog(new JPanel());
                    if (result ==file.APPROVE_OPTION) 
                    {
                        String fileName = file.getSelectedFile().getName();                    
                        String dir = file.getCurrentDirectory().toString();
                          JOptionPane.showConfirmDialog(null,dir+"\\"+fileName,"选择的文件",JOptionPane.YES_OPTION);
                     }
                }
            });
            ////////////////////////////////////////
            //public
            //////////////////////////////////////////
                    
            JToggleButton toggleButton = new JToggleButton("双态按钮");
            
            ButtonGroup    buttonGroup = new ButtonGroup();
            JRadioButton radioButton1 = new JRadioButton("单选按钮1",false);
            JRadioButton radioButton2 = new JRadioButton("单选按钮2",false);
            
            // 组合框的处理
            JComboBox comboBox = new JComboBox();
            comboBox.setToolTipText("点击下拉列表增加选项");
            comboBox.addActionListener(new ActionListener() 
            {
                public void actionPerformed(ActionEvent e)
                {
                    JComboBox comboBox =(JComboBox)e.getSource();
                    comboBox.addItem("程序员");
                    comboBox.addItem("分析员");
                }
            });
            
            // 列表框的处理
            DefaultListModel litem = new DefaultListModel();
            litem.addElement("香蕉");
            litem.addElement("水果");
            JList list = new JList(litem);
            
            
            list.addListSelectionListener(new ListSelectionListener ()
            {
                public void valueChanged(ListSelectionEvent e)
                {
                    JList l = (JList)e.getSource();
                    Object s= l.getSelectedValue();
                    JOptionPane.showMessageDialog(null,s,"消息框",JOptionPane.YES_OPTION);
                }
            });
            
            // 增加按钮组
            buttonGroup.add(radioButton1);
            buttonGroup.add(radioButton2);
            
            // 增加各种按钮到JPanel中显示
            add(button);
            add(toggleButton);
            add(checkBox);
            add(radioButton1);            
            add(radioButton2);
            add(comboBox);
            
            add(list);
            
            this.setBorder(new EtchedBorder(EtchedBorder.LOWERED,Color.LIGHT_GRAY,Color.blue));
        }        
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /**
     * 中间层模块，继承JPanel,初始化页签,并在页签中设置文本区，表格,
     * 文本区上下用分隔条分隔
     * JPanel--+
     *         -JTabbedPane--+
     *                          --Draw    --JTable  -JTextAreas -JText --JPopupMenu
     */
    class CenterPanel extends JPanel
    {
        public CenterPanel()
        {
            JTabbedPane tab = new JTabbedPane(JTabbedPane.TOP);
            
            JTextField textField = new JTextField("文本域,点击打开<文件按钮>可选择文件");
            textField.setActionCommand("textField");
            
            JTextPane textPane = new JTextPane();
            textPane.setCursor(new Cursor(Cursor.TEXT_CURSOR));
            textPane.setText("编辑器,试着点击文本区，试着拉动分隔条。");
                        
            textPane.addMouseListener(new MouseAdapter () 
            {
                public void mousePressed (MouseEvent e)
                {
                    JTextPane textPane = (JTextPane)e.getSource();
                    textPane.setText("编辑器点击命令成功");
                //    textField.setText(""+textPane.getText());
                }
            });
            
            /*
            UpperCaseDocument doc = new Document(); 
            textField.setDocumentsetDocument(doc);
            doc.addDocumentListener(new DocumentListener()
            {
                public void changedUpdate(DocumentEvent e){}
                public void removeUpdate(DocumentEvent e){}
                public void insertUpdate(DocumentEvent e)
                {
                    Document text = (Document)e.getDocument();
                    text.setText("复制成功");
                }                
            });
            */
            
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,textField,textPane);
            
                
            JTable table = new JTable(10,10);
            //table.showHorizontalLines(true);
            //table.showVerticalLines(true);
            //table.gridColor(Color.blue);
            
            JPanel pane  = new JPanel();
            pane.add(table.getTableHeader(),BorderLayout.NORTH);
            pane.add(table);
                        
            tab.addTab("文本演示",splitPane);
            //tab.addTab(table.getTableHeader());
            tab.addTab("表格演示",pane);
            tab.setPreferredSize(new Dimension(500,600));
            this.add(tab);
            this.setEnabled(true);            
        }
    }
    
    
    public static void main(String args[])
    {
        // 设置主框架属性,此处没有使用，可打开看看效果
        //try
        //{
        //    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        //}
        //catch  (Exception e){}
        new SwingTest();                        
    }
}
