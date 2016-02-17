package org.paradise.etrc.tools;

import org.paradise.etrc.data.skb.ETRCData;
import org.paradise.etrc.data.skb.LCBStation;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * 从achen1的里程表导入数据
 */
public class LCBReader {
    private String path;
    private Vector xianluList;
    private Vector stationList;
    private Hashtable xianluTable;

    public LCBReader(String _path) throws IOException {
        path = _path;

        xianluList = new Vector();
        stationList = new Vector();
        xianluTable = new Hashtable();

        loadLCB();
    }

    private String buildLCB(LCBStation station) {
//		if(station.name.endsWith("沪杭线"))
//			System.out.println(station);
        return ((String) xianluTable.get(station.xianlu)).substring(0, 2)
                + ETRCData.encode1(station.level)
                + ETRCData.encode2(station.dist)
                + station.name;
    }

    private void dumpToETRC(String path) throws IOException {
        File exlFile = new File(path + "exl.eda");
        File elcFile = new File(path + "elc.eda");

        PrintStream exlOut = new PrintStream(new FileOutputStream(exlFile));
        PrintStream elcOut = new PrintStream(new FileOutputStream(elcFile));

        for (int i = 0; i < xianluList.size(); i++) {
            exlOut.println(xianluTable.get(xianluList.get(i)));
        }

        for (int i = 0; i < stationList.size(); i++) {
//			System.out.println( (LCBStation) stationList.get(i) );
//			System.out.println(buildLCB((LCBStation) stationList.get(i)));
            elcOut.println(buildLCB((LCBStation) stationList.get(i)));
        }

        exlOut.close();
        elcOut.close();
    }

    private void loadLCB() throws IOException {
        File f = new File(path + "里程表.csv");

        BufferedReader in = new BufferedReader(new FileReader(f));

        String line = in.readLine();
        while (line != null) {
            String stationDist = "0";
            String xianluIndex = "*";
            String xianluName = "";
            String stationName = "";
            String stationLevel = "六等站";

            String sta_line[] = line.split(",");
            if (sta_line.length >= 5) {
                xianluIndex = sta_line[0];
                xianluName = sta_line[1];
                stationName = sta_line[2];
                stationDist = sta_line[3];
                stationLevel = sta_line[4];
            } else if (sta_line.length >= 4) {
                xianluIndex = sta_line[0];
                xianluName = sta_line[1];
                stationName = sta_line[2];
                stationDist = sta_line[3];
            } else if (sta_line.length >= 2) {
                xianluIndex = sta_line[0];
                xianluName = sta_line[1];
            }

            int dist = 0;
            try {
                dist = Integer.parseInt(stationDist);
            } catch (NumberFormatException nfe) {
            }

            int level = 6;
            if (stationLevel.equalsIgnoreCase("特等站"))
                level = 0;
            else if (stationLevel.equalsIgnoreCase("一等站"))
                level = 1;
            else if (stationLevel.equalsIgnoreCase("二等站"))
                level = 2;
            else if (stationLevel.equalsIgnoreCase("三等站"))
                level = 3;
            else if (stationLevel.equalsIgnoreCase("四等站"))
                level = 4;
            else if (stationLevel.equalsIgnoreCase("五等站"))
                level = 5;

            String xianlu = xianluIndex + xianluName;
            if (!xianluList.contains(xianlu)) {
                xianluList.add(xianlu);

                //System.out.println(xianluIndex + ":" + xianluName);
            }

            if (!stationName.trim().equalsIgnoreCase("")) {
                LCBStation station = new LCBStation(stationName, dist, level, false);
                station.xianlu = xianlu;
                stationList.add(station);
            }

            line = in.readLine();
        }

        buildeXianluTable();
    }

    private void buildeXianluTable() {
        Enumeration en = xianluList.elements();
        while (en.hasMoreElements()) {
            String xianlu1ID = (String) en.nextElement();
            String xianlu0ID = xianlu1ID.substring(1, xianlu1ID.length());
            String ID3 = getXLIndex(xianlu1ID.substring(0, 1));

            xianluTable.put(xianlu1ID, ID3 + xianlu0ID);
        }
    }

    int[] id = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private String getXLIndex(String xianluIndex) {
        int nextID = id[xianluIndex.charAt(0) - 'A']++;
        return xianluIndex + ETRCData.encode1(nextID);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            LCBReader lcb = new LCBReader("C:\\trains\\");

//			Enumeration enu = lcb.xianluList.elements();
//			while(enu.hasMoreElements()) {
//				String xianlu = (String)enu.nextElement();
//				System.out.println(xianlu);
//			}
//			System.out.println(lcb.xianluList.size());

            Enumeration en = lcb.xianluTable.keys();
            while (en.hasMoreElements()) {
                String id = (String) en.nextElement();
                String name = (String) lcb.xianluTable.get(id);

                System.out.println(id + " : " + name);
            }

            System.out.println("xl = " + lcb.xianluTable.size());
            System.out.println("lc = " + lcb.stationList.size());

            lcb.dumpToETRC("C:\\trains\\");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
