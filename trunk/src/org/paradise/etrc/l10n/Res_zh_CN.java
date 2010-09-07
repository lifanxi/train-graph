package org.paradise.etrc.l10n;

import java.util.ListResourceBundle;

public class Res_zh_CN extends ListResourceBundle {
    static final Object[][] contents = new String[][]{
        { "The train graph is has changed.\r\nDo you want to save the changes?", "运行图己经被更改。\r\n要保存改动吗？" },
        { "FONT_NAME", "宋体" },
        { "LGuo's Electronic Train Graph", "<[ LGuo的电子运行图 ]>" },
        { "Status: Normal", "状态：正常" },

        // Menu
        { "File", "文件(F)" },
        { "New", "新建(N)" },
        { "Open...", "打开(O)" },
        { "Save", "保存(S)" },
        { "Save As...", "另存为(A)..." },
        { "Load Train...", "载入车次(L)..." },
        { "Export...", "导出运行图(P)..." },
        { "Exit", "退出(X)" },
        { "Settings", "设置(S)" },
        { "Margin...", "边距(M)..." },
        { "Timeline...", "时间轴(T)..." },
        { "Distance Bar...", "距离轴(D)..." },
        { "Edit", "编辑(E)" },
        { "Circuit...", "线路编辑(C)..." }, 
        { "Train...", "车次编辑(R)..." },
        { "Find Train...", "车次查找(F)..." },
        { "Tool", "工具(T)" },
        { "Import Circuit...", "线路导入(C)..." },
        { "Import Train...", "车次导入(R)..." },
        { "Help", "帮助(H)" },
        { "About...", "关于(A)..." },

        // Toolbar
        { "Open a Chart", "打开运行图" },
        { "Save Chart", "保存运行图" },
        { "Save Chart As", "运行图另存为" },
        { "Edit Circuit", "线路编辑" },
        { "Edit Train Information", "车次编辑" },
        { "Find a Train", "车次查找" },
        { "Timeline Settings", "时间轴设置" },
        { "Distance Bar Settings", "距离轴设置" },
        { "Show Dynamic Chart", "显示动态图" },
        { "Display Up-going Trains", "显示上行车次" },
        { "Display Down-going Trains", "显示下行车次" },
        
        { "Cannot find train information: %s", "没有找到%s次列车！" },
        { "Load train graph failed, please check the %s file.", "运行图文件载入失败，请检查%s文件" },

        { "This operation will delete all the train information on the graph, then import the train information from the default time table for this circuit. Continue?", "删除所有车次，从本系统自带的时刻表中自动导入经过本线路（停2站以上）的车次，是否继续？" },

        { "Export Train Graph", "导出运行图" },
        { "Unable to export the graph.", "导出运行图出错！" },
        { "Unable to save the graph.", "保存运行图出错！" },
        
        { "Save As", "运行图另存为" },
        { "Open", "载入运行图" },
        { "Unable to load the graph.", "载入运行图出错！" },
        { "Load train information file and overwrite the existing information. Continue?", "（批量）读入车次文件，覆盖已经存在的车次，是否继续？" },
        { "Load Train Information", "载入车次" },

        { "Unable to open time table.", "打开时刻表文件失败！" },
        { "Unable to open circuit table.", "打开里程表文件失败！" },

        { "Unable to read chart settings.", "运行图设置读取错误" }, 
        { "Error reading color.", "颜色读取错" },
        { "Error reading color settings for the train %s.", "%s次颜色读取错误" },

        { "Error reading circuit name.", "线路名读取错" },
        { "Error in circuit length format.", "线路总长数据格式错" },
        { "Error reading circuit length.", "线路总长读取错" },

        { "Near %s Station", "%s站附近" },
        { "%s Station", "%s站" }, 

        { "Invalid data for station %d", "第%d站数据有误" },
        { "Invalid distance data for station %s", "%s站公里数数据格式错" },
        { "Invalid level data fro station %s",  "%s站等级数据格式错" },
        { "Invalid hidden type data for station %s", "%s站是否隐藏数据格式错" },
        
        { "Error reading train number.", "车次读取错" },
        { "Error reading departure station.", "始发站读取错" },
        { "Error reading terminal station.", "终到读取错" },
        { "Data incomplete in:", "车次不完整:" },

        { "Station %d data error in line %s", "%s第%d站数据有误" },
        
        // Filter
        { "Line Description File (*.cir)", "线路描述文件 (*.cir)" },
        { "Excel CSV File (*.csv)", "Excel CSV格式文件 (*.csv)" },
        { "GIF File (*.gif)", "GIF图片文件 (*.gif)" },
        { "Train Graph File (*.trc)", "列车运行图文件 (*.trc)" },
        { "Train Description File (*.trf)", "车次描述文件 (*.trf)" },

        // Dialog
        { "About", "关于" },
        { "LGuo's Train Graph", "LGuo的电子运行图" },
        { "Author: Guo Lei (HASEA ID:lguo)", "郭磊（海子ID: lguo）出品" },
        { "Thanks to: achen1 on HASEA", "鸣谢：海子网 achen1 等网友" },
        { "OK", "确定" },
        { "Cancel", "取消" },

        { "Settings for Train Graph", "设定运行图参数" },
        { "Pixels per kilometer:", "每公里像素数：" },
        { "Lowest display station level:",  "最低显示车站等级：" },
        { "Highest station level to show blod line:", "最低粗线坐标：" },
        { " The highest station level is 0", " 特等站等级为0" },
        { "Distance bar", "距离轴" },
        { "Timeline", "时间轴" },
        
        { "Message Content", "信息内容" },
        { "Message", "消息" },
        { "Question", "问题" },
        { "Yes", "是" },
        { "No", "否" },
        
        { "Input the train you want to find", "请输入要查找的车次" },
        { "Train number to find:", "查找的车次：" },
        { "Clear", "清空" },
        { "Input the train you want to find", "请输入要查找的车次" },
        { "Not found", "没有找到" },
        
        { "Slice Output", "切片输出窗口（临时）" },
        
        { "Finding Train Information", "查找车次" },
        { "Find", "查找" },
        { "Removing existing train data, please wait...", "删除现有车次，请稍等..." },
        { "Please wait while imporing train information...", "正在查找车次，请稍等..." },
        { "Importing train information %s", "找到车次信息：%s" },
        { "Margin Settings", "边距设置" },
        
        { "Left:", "左" },
        { "Right:", "右" },
        { "Top:", "上" },
        { "Bottom:", "下" },

        { "Default", "默认" },
        { "Set", "设定" },
        { "Test", "测试"},
        { "Graph Margin", "图表边距" },
        { "Set Graph Margin", "设定图表边距" },
        { "Input data out of range (%d-%d).", "输入数据超出范围(%d-%d)！" },
        { "Input data out of range.", "输入数据超出范围！" },
        { "Invalid input", "输入数据格式错！" },
        
        { "Circuit Selection", "选择线路" }, 
        { "Train Information", "车次信息" },
        { "Display opposite direction train using watermark", "水印显示反向车次" },
        { "Add", "添加" },
        { "Add(Before)", "添加(前)" },
        { "Add(After)", "添加(后)" },
        { "Load", "载入" },
        { "Edit", "编辑" },
        { "Delete", "删除" },    
        
        { "Number", "车次" },
        { "Departure", "起始站" },
        { "Termial", "终点站" },
        { "Color", "颜色" },
        { "Select the color for the line", "请选择行车线颜色" },
        { "%s is already in the graph. Overwrite?", "%已经在运行图中，是否覆盖？"},
        { "Middle", "中间站" },
        { "Station", "站名" },
      
        { " Must be a divider of 60", " 必须是60的约数" },
        { "Time for 0 pos:", "零坐标时刻：" },
        { "Pixel per min：", "每分钟像素：" },
        { "Y-axis gap (min):", "纵坐标间隔：" },
        { "min", "分钟" },
        
        { "Add New Train", "添加新车" },
        { "Export Graph...", "导出运行图" },
        { "Train Slice", "车次切片" },
        { "Change Color", "更改颜色" }, 
        { "Edit Time Table", "编辑点单" },
        
        { "Train %s is already in the graph, overwrite?", "%s次已经在图中，是否覆盖？" },
        
        { "Load Circuit", "载入线路" },
        { "Save Circuit", "保存线路" },
        { "Distance", "距离" },
        { "Level", "等级" },
        { "Hidden", "隐藏" },
        { "Circuit Name:", "线路名：" },
        
        { "From:", "起点：" },
        { "To:", "终点：" },
        { " Section", "段" },
        { "Please confirm the down-going direction for circuit %s is from %s to %s.", "请确认%s %s 至 %s 为下行方向！" },
        
        { "Always highlight termials", "始终突出画到发点" },
        { "Enable watermark display", "开启反向水印显示" },
        
        { "Set timeline settings", "设定时间轴参数" },
        
        { "Distance Bar Setting", "距离轴设置" },
        { "Set distance bar setting", "设定距离轴参数" },
        
        { "Up-going:", "上行车次：" },
        { "Down-going:", "下行车次：" },
        { "Train number:", "车次：" },
        { "Save Train", "保存车次" },
        { "Save ", "保存" },
        
        { "Arrival", "到点" },
        { "Leave", "发点" },
        { "Passenger", "办客" },
        
        // Wizard
        { "Step %d: %s", "第%d步：%s" }, 
        { "Finish", "完成" },
        { "Next >", "下一步 >" },
        { "< Previous", "< 上一步" },
        
        { "Add New Train", "添加新车" },
        { "Input Train Number", "输入车次" },
        { "Edit Time Table", "编辑点单" },
        { "Select Start Station", "确定入图（始发）点" },
        { "Select Terminal Station", "确定出图（终到）点" },
        { "Automatically import train informtaion from build-in time table?", "是否查询内置时刻表获取点单数据？" },
        { "Unable to find information for the train %s, please input manually.", "没有找到%s次列车的时刻表，请手工输入。" },
        { "Find %d related information for the train %s, use the first record.", "找到%d条有关%s次列车的时刻表数据，选用第一条数据。" },
        { "  The separator between the hour and minute can be space, colon, comma or period. 3 or 4 digital number can also be accepted. Click on the next text box will complete the current input. ", "    时分间隔可以是空格、冒号、分号或者句点。也可以直接输入3位或者4位纯数字，如：凌晨1点23分可以直接输入123，用鼠标点击下一输入框结束输入。" },

        { "Search", "查找" },
        { "Station name:", "站名" },
        { "Input Station Name", "输入车站名" },
        
        { "Departure/Start Time:", "入图/始发时间：" }, 
        { "Termial/End Time:", "出图/终到时间：" },
        { "  Input full train number, the separator for the train number can be slash, comma or period, <Tab> to finish input.",
          "    请输入全车次，复车次的间隔符可以是左右斜杠，顿号，逗号，句号（点），后续车次可以只输入最后一位或者两位，Tab键结束输入。" },  
        { "  Select the down-going train number for this circuit, use Mouse or Up/Down arrow key, <Tab> to finish selection.",
          "    请选择在本线路所使用的下行车次，用鼠标 或者，上、下键选择，Tab键结束选择。"
        },
        { "  Select the up-going train number for this circuit, use Mouse or Up/Down arrow key, <Tab> to finish selection.",
          "    请选择在本线路所使用的上行车次，用鼠标 或者，上、下键选择，Tab键结束选择。"
        },
        { "  Cancel adding train.", "    放弃添加车次。" },
        { "  Next Step: Input or read train information for the train %s. (Press Enter or Spacebar)",
          "    下一步：从时刻表读入或者手工录入 %s次 列车的点单。（按回车或者空格键）"
        },
        { " %s down-going direction %s km from %s station: %s station",
          "%s下行%s公里(距离%s站): %s站"
        },
        { "  Train %s departures from %s station in this section, no need to set start point",
          "  %次列车由本区间内的%s站始发，不需要设置入图点"
        },
        { "  The first stop of train %s in this section is %s, change the station and time if this is not correct.",
          "  %s次 在本区间内的第一个停站是 %s站 如果该站非实际入图点，请另行选择，若系统推算入图时间有误请自行更正"
        },
        { "  The train %s passes this section, set the start point manually.",
          "%s次 跨越本区间（在本区间内无图定办客停点），无法推算入图点，请选择入图车站，并手工设定入图时间"
        },
        { "  Train %s terminates at %s station in this section, no need to set end point",
          "  %次列车由本区间内的%s站终到，不需要设置出图点"
        },   
        { "  The last stop of train %s in this section is %s, change the station and time if this is not correct.",
          "  %s次 在本区间内的最后一个停站是 %s站 如果该站非实际出图点，请另行选择，若系统推算出图时间有误请自行更正"
        },
        { "  The train %s passes this section, set the ends point manually.",
          "%s次 跨越本区间（在本区间内无图定办客停点），无法推算入图点，请选择入图车站，并手工设定入图时间"
        },     
    };
    
	@Override
	protected Object[][] getContents() {
		return contents;
	}

}
