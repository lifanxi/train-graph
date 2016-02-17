package org.paradise.etrc.tools.llt;

import org.paradise.util.Tools;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public class ReadLLT {

    private static final int k_int15000 = 15000;

    Vector trainNames;

    Vector stationNames;

    public ReadLLT() {
        trainNames = readTrainNames();
        System.out.println(trainNames.size() + " 趟列车信息");

        stationNames = readStationNames();
        System.out.println(stationNames.size() + " 个停靠站信息");
    }

    private static byte[] readFromFileBuffer(byte fileBuffer[], int index) {
        if (fileBuffer == null || index < 0)
            return null;

        int j1 = 0;
        boolean flag = false;
        boolean flag1 = false;

        for (int i2 = fileBuffer.length; j1 < i2; j1++) {
            int k1 = fileBuffer[j1++] * 128 + fileBuffer[j1++];
            int l1 = j1;

            for (; fileBuffer[j1] >= 0 && j1 < i2; j1++)
                ;
            if (k1 == index) {
                byte abyte1[] = new byte[j1 - l1];
                for (j1 = l1; fileBuffer[j1] >= 0 && j1 < i2; j1++)
                    abyte1[j1 - l1] = fileBuffer[j1];

                return abyte1;
            }
        }

        return null;
    }

    private byte[] a_read_data_s(int i1) {
        i1++;
        byte abyte0[] = null;
        String s = String.valueOf(i1 % 10);
        i1--;
        String s1 = new String();
        s1 = s1 + "/Data/S" + s + ".dat";
        InputStream inputstream = getClass().getResourceAsStream(s1);
        int j1;
        byte abyte1[] = new byte[j1 = k_int15000];
        try {
            inputstream.read(abyte1);
            inputstream.close();
        } catch (IOException ioexception) {
            //	         Alert alert = new Alert("读取车站库出错:" + ioexception.getMessage());
            //	         b_display.setCurrent(alert);
            return null;
        }
        return abyte0 = readFromFileBuffer(abyte1, i1);
    }

    private byte[] readTrainInfo(int index) {
        String fileName = String.valueOf((index + 1) % 20);
        byte result[] = null;
        int j1 = k_int15000;
        InputStream inputstream = getClass().getResourceAsStream(
                "/Data/T" + fileName + ".dat");
        byte fileBuffer[] = new byte[j1];
        try {
            inputstream.read(fileBuffer);
            inputstream.close();
        } catch (IOException _ex) {
            alert("加载库信息出错！无法查询 ");
            return null;
        }
        if ((result = readFromFileBuffer(fileBuffer, index)) == null)
            alert("信息不全！无法查询");
        return result;
    }

    private void alert(String string) {
        System.out.println(string);
    }

    private Vector readTrainNames() {
        Vector h_Vector_Train = new Vector();
        byte[] buffer = new byte[34000 + 2];
        StringBuffer stringbuffer = new StringBuffer();

        String s1_TrainIDX_FileName = "/Data/train.idx";
        InputStream in_TrainIDX = getClass().getResourceAsStream(
                s1_TrainIDX_FileName);
        try {
            int j1 = in_TrainIDX.read(buffer);
            in_TrainIDX.close();
            for (int l1 = 0; l1 < j1; ) {
                for (; l1 < j1 && buffer[l1] != 13; l1++)
                    stringbuffer.append((char) buffer[l1]);

                l1 += 2;
                h_Vector_Train.addElement(stringbuffer.toString());
                stringbuffer.delete(0, stringbuffer.length());
            }

        } catch (IOException ioexception) {
            alert("加载车次信息出错!Error:" + ioexception.getMessage());
        }

        return h_Vector_Train;
    }

    private Vector readStationNames() {
        Vector g_Vector_stations = new Vector();
        String staIDXFileName = null;
        staIDXFileName = "/Data/station.idx";
        InputStream inStaIDX = getClass().getResourceAsStream(staIDXFileName);

        StringBuffer stringbuffer = new StringBuffer();
        byte[] buffer = new byte[34000 + 2];
        int i1_34000 = 34000;

        try {

            int len = inStaIDX.read(buffer, 2, i1_34000);
            inStaIDX.close();

            buffer[0] = (byte) ((len & 0xff00) >> 8);
            buffer[1] = (byte) (len & 0xff);

            DataInputStream datainputstream;
            String stationNames = (datainputstream = new DataInputStream(
                    new ByteArrayInputStream(buffer, 0, len + 2))).readUTF();
            datainputstream.close();

            for (int i2 = stationNames.charAt(0) != '\uFEFF' ? 0 : 1; i2 < stationNames
                    .length(); stringbuffer.delete(0, stringbuffer.length())) {
                for (; i2 < stationNames.length() && stationNames.charAt(i2) != '\r'; i2++)
                    stringbuffer.append(stationNames.charAt(i2));

                i2 += 2;
                g_Vector_stations.addElement(stringbuffer.toString());
            }

        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return g_Vector_stations;
    }

    private void readTrains() {
        for (int i = 0; i < trainNames.size(); i++) {
            String name = (String) trainNames.get(i);
            byte[] trainData = readTrainInfo(i);

            decodeTrain(name, trainData);
        }
    }

    private void decodeTrain(String name, byte[] trainData) {
        if (trainData == null || trainData.length < 2) {
            System.out.println(name + "无数据！" + trainData.length);
            return;
        }

        //		 System.out.println(trainData[0] + ":" + trainData[1] + " " + name);
        for (int i = 2; i < trainData.length; i += 7) {
            int stationID = trainData[i] * 128 + trainData[i + 1];
            byte hour = trainData[i + 2];
            byte min = trainData[i + 3];
            byte stop = trainData[i + 4];
            int dist = trainData[i + 5] + trainData[i + 6];

            String station = (String) stationNames.get(stationID);
            //				System.out.println(station + "站 " + hour + ":" + min + "到 停" + stop + "分 计价里程" + dist);
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        ReadLLT rllt = new ReadLLT();

        rllt.readTrains();

        String train = "T98";
        int index = 2131;
        byte[] trainInfo = rllt.readTrainInfo(index);
        Tools.printBytesOneLine(trainInfo);

        int i = 2;
        int k1 = trainInfo[i++] * 128 + trainInfo[i++];
        System.out.println(train + "(" + index + ") " + k1);

        //		for(int ti=0; ti<2172; ti++) {
        //			byte[] trainData = rllt.readTrainInfo(ti);
        //			/**
        //			 * header 长度2个字节
        //			 *
        //			 * 每个停站7个字节
        //			 * b[0]*128+b[1] 站索引
        //			 * b[3]          到站的小时数
        //			 * b[4]          到站的分钟数
        //			 * b[5]          停靠时间
        //			 * b[6]          计价里程
        //			 */
        //			Tools.printBytesOneLine(trainData);
        //		}
        //

//		cal1(rllt); //1车
        cal2(rllt); //2车
//		cal99(rllt); //3－99车
//		cal100(rllt); //100以上
    }

    private static void cal1(ReadLLT rllt) {
        int num1 = 0;
        for (int staIdx = 0; staIdx < rllt.stationNames.size(); staIdx++) {
            byte[] info = rllt.a_read_data_s(staIdx);
            if (info.length == 2) {
//				System.out.print(staIdx + "=");
//				Tools.printBytesOneLine(info);
                int tr = info[0] * 128 + info[1];
                System.out.println(rllt.stationNames.get(staIdx) + " 仅有 " + rllt.trainNames.get(tr) + " 次停靠");
                num1++;
            }
        }
        System.out.println("共有" + num1 + "个站只有一趟车停靠");
    }

    private static void cal2(ReadLLT rllt) {
        int num2 = 0;
        for (int staIdx = 0; staIdx < rllt.stationNames.size(); staIdx++) {
            byte[] info = rllt.a_read_data_s(staIdx);
            if (info.length == 4) {
//				System.out.print(staIdx + "=");
//				Tools.printBytesOneLine(info);
                int tr1 = info[0] * 128 + info[1];
                int tr2 = info[2] * 128 + info[3];
                System.out.println(rllt.stationNames.get(staIdx) + " 仅有 "
                        + rllt.trainNames.get(tr1) + " 次 和 "
                        + rllt.trainNames.get(tr2)
                        + " 次 停靠");
                num2++;
            }
        }
        System.out.println("共有" + num2 + "个站只有两趟车停靠");
    }

    private static void cal99(ReadLLT rllt) {
        for (int traNumAtSta = 6; traNumAtSta < 100; traNumAtSta += 2) {
            int num = 0;
            for (int staIdx = 0; staIdx < rllt.stationNames.size(); staIdx++) {
                byte[] info = rllt.a_read_data_s(staIdx);
                if (info.length == traNumAtSta) {
//					System.out.print(staIdx + "=");
//					Tools.printBytesOneLine(info);
                    int tr = info[0] * 128 + info[1];
//					System.out.println(rllt.stationNames.get(staIdx) + " 仅有 " + rllt.trainNames.get(tr) + " 次停靠");
                    num++;
                }
            }
            System.out.println("共有 " + num + " 个车站有 " + (traNumAtSta / 2) + " 趟车停靠");
        }

    }

    private static void cal100(ReadLLT rllt) {
        for (int staIdx = 0; staIdx < rllt.stationNames.size(); staIdx++) {
            byte[] info = rllt.a_read_data_s(staIdx);
            if (info.length >= 200) {
//				System.out.print(staIdx + "=");
//				Tools.printBytesOneLine(info);
                int tr = info[0] * 128 + info[1];
                System.out.println(rllt.stationNames.get(staIdx) + "站 有 " + info.length / 2 + " 趟列车停靠");
//				num++;
            }
        }
    }
}
