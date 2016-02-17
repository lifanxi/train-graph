package org.paradise.etrc.tools.sm;

import java.util.Vector;

public class TrainNameList {
    public TrainNameList() {
    }

    public static int Togoc(String s1) {
        return Integer.parseInt(s1);
    }


    public final void run() throws IOException {
//        System.out.println("Istar:" + d + "Iend:" + e);
        int cc[] = null;
        int cc1[] = null;
//        ai = a(d);
//        ai1 = a(e);
        String s = "";
        if (cc[0] == 0)
            s = "\u8D77\u59CB\u7AD9";
        if (cc1[0] == 0)
            s = "\u7EC8\u70B9\u7AD9";
        if (cc[0] == 0 && cc1[0] == 0)
            s = "\u8D77\u59CB\u7AD9\u548C\u7EC8\u70B9\u7AD9";
        if (s.length() > 0) {
//            i.a();
//            a("\u4F60\u8F93\u5165\u7684" + s + "\u6709\u8BEF!", g);
            return;
        }
        Vector vector = readcctk(cc[0], cc[1]);
        Vector vector1 = readcctk(cc1[0], cc1[1]);
//        List f = new List("", 3);
        int j = vector1.size();
        for (int k = 0; k < vector.size(); k++) {
//            if(h)
//            {
//                f.deleteAll();
//                break;
//            }

            String as[] = pasertk((String) vector.elementAt(k));
            for (int l = 0; l < j; l++) {
                String as1[] = pasertk((String) vector1.elementAt(l));
                if (as[0].trim().equals(as1[0].trim()) && Togoc(as[1]) < Togoc(as1[1])) {
//                    int i1 = Togoc(as1[0]);
//                    String as2[] = readcc(i1);
//                    boolean flag = false;
//                    if(b.length() > 0)
//                    {
//                        String s1 = as2[0].substring(0, 1);
//                        if(b.indexOf(s1) >= 0)
//                        {
//                            System.out.println("\u5E94\u8BE5\u8FC7\u6EE4:" + as2[0]);
//                            flag = true;
//                        }
//                    }
//                    if(!flag)
//                    {
//                        if(c > 0)
//                        {
//                            int j1 = Integer.parseInt(as[3].substring(0, 2));
//                            if(c == 1 && j1 > 12)
//                                flag = true;
//                            if(c == 2 && j1 < 12)
//                                flag = true;
//                        }
//                        if(!flag)
//                        {
//                            int k1 = Integer.parseInt(as1[1]) - Integer.parseInt(as[1]);
//                            String _tmp = as2[1] + as2[5] + as2[2] + "1";
//                            String as3[] = new String[7];
//                            for(int l1 = 0; l1 < 7; l1++)
//                                as3[l1] = "-";
//
//                            if(as2[0].trim().indexOf("K") < 0)
//                            {
//                                as[2] = "\u53D7";
//                                as[3] = "\u53D7";
//                                as1[2] = "\u9650";
//                            }
//                            String s2 = "\u8F66\u6B21:" + as2[0].trim() + "\n" + "\u5230\u8FBE" + d + "\u65F6\u95F4:" + as[2] + "\n" + "\u53D1\u8F66\u65F6\u95F4:" + as[3] + "\n" + "\u5230\u8FBE" + e + "\u65F6\u95F4:" + as1[2] + "\n" + "\u91CC\u7A0B:" + k1 + "Km\n" + "\u786C\u5EA7:" + as3[0] + "\n" + "\u8F6F\u5EA7:" + as3[1] + "\n" + "\u786C\u5367\u4E0A:" + as3[2] + " " + "\u786C\u5367\u4E2D:" + as3[3] + "\n" + "\u786C\u5367\u4E0B:" + as3[4] + "\n" + "\u8F6F\u5367\u4E0A:" + as3[5] + " " + "\u8F6F\u5367\u4E0B:" + as3[6] + "\n\f";
//                            f.append(s2, null);
//                            vector1.setElementAt(vector1.elementAt(j - 1), l);
//                            j--;
//                        }
//                    }
                }
            }

        }
//
//        if(f.size() > 0)
//        {
//            d d1 = new d(f, a, g, d + " \u5230 " + e, 1);
//            a.setCurrent(d1);
//        } else
//        if(!h)
//            a("\u5BF9\u4E0D\u8D77,\u6CA1\u6709\u60A8\u67E5\u8BE2\u7684\u7ED3\u679C!", g);
//        f = null;
//        i.a();
//        i = null;
    }

    private Vector readcctksy(int begin, int end) throws IOException {
        byte buffer[] = new byte[16];
        Vector vector = new Vector();

        String path = "C:\\trains\\smdata\\data\\cctksy";

        int tk_file_id = begin / 3000;
        int record_num_in_tkfile = begin % 3000;

        int tk_file_id2 = end / 3000;

        if (tk_file_id == tk_file_id2) {
            InputStream in;
            File file = new File(path + tk_file_id + ".txt");
            in = new DataInputStream(new FileInputStream(file));
            in.skip(3);

            //boolean flag = false;

            in.skip(record_num_in_tkfile * 16);

            int num = (end - begin) + 1;
            for (int _i = 0; _i < num; _i++) {
                in.read(buffer);
                String s = new String(buffer, "UTF-8");
                vector.addElement(s);
            }

            in.close();

        } else {
            InputStream in1;
            File file1 = new File(path + tk_file_id + ".txt");
            in1 = new DataInputStream(new FileInputStream(file1));
            in1.skip(record_num_in_tkfile * 16);

            int num = (tk_file_id + 1) * 3000 - begin;
            for (int _i = 0; _i < num; _i++) {
                in1.read(buffer);
                String s1 = new String(buffer, "UTF-8");
                vector.addElement(s1);
            }

            in1.close();


            (in1 = getClass().getResourceAsStream("/data/cctk" + tk_file_id2 + ".txt")).skip(3L);
            num = (end - tk_file_id2 * 3000) + 1;
            for (int j2 = 0; j2 < num; j2++) {
                in1.read(buffer);
                String s2 = new String(buffer, "UTF-8");
                vector.addElement(s2);
            }

            in1.close();
        }

        return vector;
    }

//    private void a(int j, int k)
//    {
//        byte abyte0[];
//        Object obj = null;
//        Object obj1 = null;
////        d = new List("", 3);
//        abyte0 = new byte[16];
//        try
//        {
//            int l = 0;
//            int i1 = j / 3000;
//            int j1 = j % 3000;
//            int k1 = k / 3000;
//            if(i1 == k1)
//            {
//                InputStream inputstream = getClass().getResourceAsStream("/data/cctksy" + i1 + ".txt");
//                boolean flag = false;
//                inputstream.skip(j1 * 16 + 3);
//                l = (k - j) + 1;
//                for(int i2 = 0; i2 < l; i2++)
//                {
//                    inputstream.read(abyte0);
//                    String s;
//                    String as[];
//                    int l2 = Integer.parseInt((as = b(s = new String(abyte0, "UTF-8")))[0]);
//                    String s3 = Togo.I.elementAt(l2).toString();
//                    String s6;
//                    if(as[1].equals("9999"))
//                    {
//                        as[2] = "受";
//                        as[3] = "限";
//                        s6 = "制";
//                    } else
//                    {
//                        s6 = as[1];
//                    }
//                    String s9 = (i2 + 1) + "," + s3.trim() + "," + as[2] + "," + as[3] + "," + s6 + ",";
//                    d.append(s9, null);
//                }
//
//                inputstream.close();
//                return;
//            }
//            InputStream inputstream1;
//            (inputstream1 = getClass().getResourceAsStream("/data/cctksy" + i1 + ".txt")).skip(j1 * 16 + 3);
//            int l1 = (i1 + 1) * 3000 - j;
//            for(int j2 = 0; j2 < l1; j2++)
//            {
//                inputstream1.read(abyte0);
//                String s1;
//                String as1[];
//                int i3 = Integer.parseInt((as1 = b(s1 = new String(abyte0, "UTF-8")))[0]);
//                String s4 = Togo.I.elementAt(i3).toString();
//                String s7 = (j2 + 1) + "," + s4.trim() + "," + as1[2] + "," + as1[3] + "," + Togo.c(as1[1]) + ",";
//                d.append(s7, null);
//            }
//
//            inputstream1.close();
//            inputstream1 = getClass().getResourceAsStream("/data/cctksy" + (i1 + 1) + ".txt");
//            l = (k - k1 * 3000) + 1;
//            inputstream1.skip(3L);
//            for(int k2 = 0; k2 < l; k2++)
//            {
//                inputstream1.read(abyte0);
//                String s2;
//                String as2[];
//                int j3 = Integer.parseInt((as2 = b(s2 = new String(abyte0, "UTF-8")))[0]);
//                String s5 = Togo.I.elementAt(j3).toString();
//                String s8 = (l1 + k2 + 1) + "," + s5.trim() + "," + as2[2] + "," + as2[3] + "," + Togo.c(as2[1]) + ",";
//                d.append(s8, null);
//            }
//
//            inputstream1.close();
//            return;
//        }
//        catch(Exception exception)
//        {
//            System.out.println("readUTF Error:" + exception.toString());
//        }
//    }


    private String[] pasertk(String s) {
        String as[] = new String[4];
        as[0] = s.substring(0, 4);
        as[1] = s.substring(4, 8);
        as[2] = s.substring(8, 10) + ":" + s.substring(10, 12);
        as[3] = s.substring(12, 14) + ":" + s.substring(14, 16);

        System.out.println(
                s + " = " +
                        as[0] + " > " +
                        as[1] + " < " +
                        as[2] + " | " +
                        as[3] + " | "
        );
        return as;
    }

    private String[] pasertksy(String s) {
        String as[] = new String[4];
        as[0] = s.substring(0, 4);
        as[1] = s.substring(4, 8);
        as[2] = s.substring(8, 10) + ":" + s.substring(10, 12);
        as[3] = s.substring(12, 14) + ":" + s.substring(14, 16);

        System.out.println(
                s + " = " +
                        as[0] + " > " +
                        as[1] + " < " +
                        as[2] + " | " +
                        as[3] + " | "
        );
        return as;
    }


    private Vector readcctk(int begin, int end) throws IOException {
        byte buffer[] = new byte[16];
        Vector vector = new Vector();

        String path = "C:\\trains\\smdata\\data\\cctk";

        int tk_file_id = begin / 3000;
        int record_num_in_tkfile = begin % 3000;

        int tk_file_id2 = end / 3000;

        if (tk_file_id == tk_file_id2) {
            InputStream in;
            File file = new File(path + tk_file_id + ".txt");
            in = new DataInputStream(new FileInputStream(file));
            in.skip(3);

            //boolean flag = false;

            in.skip(record_num_in_tkfile * 16);

            int num = (end - begin) + 1;
            for (int _i = 0; _i < num; _i++) {
                in.read(buffer);
                String s = new String(buffer, "UTF-8");
                vector.addElement(s);
            }

            in.close();

        } else {
            InputStream in1;
            File file1 = new File(path + tk_file_id + ".txt");
            in1 = new DataInputStream(new FileInputStream(file1));
            in1.skip(record_num_in_tkfile * 16);

            int num = (tk_file_id + 1) * 3000 - begin;
            for (int _i = 0; _i < num; _i++) {
                in1.read(buffer);
                String s1 = new String(buffer, "UTF-8");
                vector.addElement(s1);
            }

            in1.close();


            (in1 = getClass().getResourceAsStream("/data/cctk" + tk_file_id2 + ".txt")).skip(3L);
            num = (end - tk_file_id2 * 3000) + 1;
            for (int j2 = 0; j2 < num; j2++) {
                in1.read(buffer);
                String s2 = new String(buffer, "UTF-8");
                vector.addElement(s2);
            }

            in1.close();
        }

        return vector;
    }


    public void readccsy_zmsy() throws FileNotFoundException {
        //File file = new File("C:\\trains\\smdata\\data\\ccsy.txt");
        File file = new File("C:\\trains\\smdata\\data\\zmsy.txt");
        InputStream in = new DataInputStream(new FileInputStream(file));

        try {
            in.skip(3);

            int len = 8;
            byte buffer[] = new byte[len];
            int num = (in.available() - 3) / len;

            for (int i = 0; i < num; i++) {
                in.read(buffer);
                String record = new String(buffer, "UTF-8");

                System.out.println(record + " = "
                        + " : " + record.substring(0, 4)
                        + " : " + record.subSequence(4, 8));
            }

            in.close();
        } catch (Exception exception) {
            System.out.println("readUTF Error:" + exception.toString());
        }
    }

    //读zm文件获取PY和
    public void readzm() throws FileNotFoundException {
        File file = new File("C:\\trains\\smdata\\data\\zm.txt");
        InputStream in = new DataInputStream(new FileInputStream(file));

        try {
            in.skip(8);

            int len = 9;
            byte buffer[] = new byte[len];
            int num = (in.available() - 3) / len;

            for (int i = 0; i < num; i++) {
                in.read(buffer);
                String record = new String(buffer, "UTF-8");

                String id = record.substring(0, 4);
                String py = record.substring(4).trim();

                System.out.println(id + " : " + py);
            }

            in.close();
        } catch (Exception exception) {
            System.out.println("readUTF Error:" + exception.toString());
        }
    }

    //读zmhzsy文件，获取车站中文名列表
    public void readzmhzsy() throws FileNotFoundException {
        File file = new File("C:\\trains\\smdata\\data\\zmhzsy.txt");
        InputStream in = new DataInputStream(new FileInputStream(file));

        try {
            in.skip(3);

            int len = 25;
            byte buffer[] = new byte[len];
            int num = (in.available() - 3) / len;

            for (int i = 0; i < num; i++) {
                in.read(buffer);
                String record = new String(buffer, "UTF-8");

                String name = record.substring(0, record.length() - 10).trim();
                String id = record.substring(record.length() - 10, record.length());

                System.out.println(i + ": " + id + " : " + name);
            }

            in.close();
        } catch (Exception exception) {
            System.out.println("readUTF Error:" + exception.toString());
        }
    }

    //读ccdz和ccdzzm文件获取单一车子在cc文件中的index
    public void readccdz() throws FileNotFoundException {
        File file = new File("C:\\trains\\smdata\\data\\ccdz.txt");
        //File file = new File("C:\\trains\\smdata\\data\\ccdzzm.txt");
        InputStream in = new DataInputStream(new FileInputStream(file));


        try {
            in.skip(3);

            byte buffer[] = new byte[8];
            int j = (in.available() - 3) / 8;

            for (int k = 0; k < j; k++) {
                in.read(buffer);
                String record = new String(buffer, "UTF-8");

                String trainName = record.substring(0, 4).trim();
                String index = record.substring(record.length() - 4).trim();

                System.out.println(index + " : " + trainName);
            }

            in.close();
        } catch (Exception exception) {
            System.out.println("readUTF Error:" + exception.toString());
        }
    }

    public String[] readcc(int index) throws IOException {
        File file = new File("C:\\trains\\smdata\\data\\cc.txt");
        DataInputStream in = new DataInputStream(new FileInputStream(file));

        int len = 33;
        byte[] buffer = new byte[len];

//		in.skip(22);
        int pos = 22 + len * index;

        in.readFully(buffer, pos, len);
        return paserTrainName(index, new String(buffer));
    }

    //读cc.txt文件获取带索引的车次全名列表
    public void readcc() throws IOException {
        File file = new File("C:\\trains\\smdata\\data\\cc.txt");
        DataInputStream in = new DataInputStream(new FileInputStream(file));

        int pos = 0;
        int len = 33;
        int index = 0;
        byte[] buffer = new byte[len];

        try {
            in.skip(3);
            while (true) {
                in.readFully(buffer, pos, len);
                paserTrainName(index, new String(buffer));
//				System.out.println(new String(buffer));
                index++;
            }
        } catch (EOFException eof) {
        }
    }

    private String[] paserTrainName(int _index, String line) {
        int index = _index + 1;
        String name = line.substring(0, line.length() - 14).trim();
        String id = line.substring(line.length() - 14);

        String train[] = new String[6];

        train[0] = name;
        train[1] = id.substring(0, 1);
        train[2] = id.substring(1, 3);
        train[3] = id.substring(3, 8);
        train[4] = id.substring(8, 13);
        train[5] = id.substring(13, 14);

//		if(name.startsWith("T70"))
//		if(index < 600)
        System.out.println(index + " | "
                //+ train[0] + " : "
                + train[1] + " : "
                + train[2] + " : "
                + train[3] + " : "
                + train[4] + " : "
                + train[5] + " => "
                + train[0]);

        return train;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            new TrainNameList().readcc();
//			new TrainNameList().readccdz();
//			new TrainNameList().readzmhzsy();
//			new TrainNameList().readzm();
//			new TrainNameList().readccsy_zmsy();

            TrainNameList sm = new TrainNameList();
            Vector vec = sm.readcctk(33587, 33588);

            Object str[] = vec.toArray();
            for (int i = 0; i < str.length; i++) {
                //System.out.println((i+32676) + " : " + (String)str[i]);
                sm.pasertk((String) str[i]);
            }

            System.out.println("==========sy==========");

            vec = sm.readcctksy(33587, 33588);
            str = vec.toArray();
            for (int i = 0; i < str.length; i++) {
                //System.out.println((i+32676) + " : " + (String)str[i]);
                sm.pasertksy((String) str[i]);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
