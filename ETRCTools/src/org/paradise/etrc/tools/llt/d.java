package org.paradise.etrc.tools.llt;

import java.io.*;
import java.util.Vector;
import javax.microedition.lcdui.*;

//Referenced classes of package Lulutong:
//         Lulutong, b, c, e, 
//         g, i

public final class d
{

 public d()
 {
 }

 public static void a(Display display)
 {
//     d_Lulutong = lulutong;
     b_display = display;
     i_Vector = new Vector();
 }

 public static void a(String s, String s1, String s2, Displayable displayable)
 {
     e_String_staName1 = s;
     f_String_staName2 = s1;
     c_displayable = displayable;
 }

 public final void a()
 {
     if(a_int_1 > 0)
     {
//         d_Lulutong.e.a();
//         b_display.setCurrent(d_Lulutong.e);
     }
//     c c1;
//     (c1 = new c(this)).start();
 }

 private void b()
 {
     switch(a_int_1)
     {
     case 0: // '\0'
         e();
         break;

     case 1: // '\001'
         c();
         break;

     case 2: // '\002'
         formatTrainInfo();
         break;

     case 3: // '\003'
         f();
         break;
     }
//     d_Lulutong.e.b();
 }

 private static byte[] readFromFileBuffer(byte fileBuffer[], int index)
 {
     if(fileBuffer == null || index < 0)
         return null;
     
     int j1 = 0;
     boolean flag = false;
     boolean flag1 = false;
     
     for(int i2 = fileBuffer.length; j1 < i2; j1++)
     {
         int k1 = fileBuffer[j1++] * 128 + fileBuffer[j1++];
         int l1 = j1;
         
         for(; fileBuffer[j1] >= 0 && j1 < i2; j1++);
         if(k1 == index)
         {
             byte abyte1[] = new byte[j1 - l1];
             for(j1 = l1; fileBuffer[j1] >= 0 && j1 < i2; j1++)
                 abyte1[j1 - l1] = fileBuffer[j1];

             return abyte1;
         }
     }

     return null;
 }

 /**
   * @param i1
  * @return
  */
 private byte[] a_read_data_station(int i1)
 {
     i1++;
     byte abyte0[] = null;
     String s = String.valueOf(i1 % 10);
     i1--;
     String s1 = new String();
     s1 = s1 + "/Data/S" + s + ".dat";
     InputStream inputstream = getClass().getResourceAsStream(s1);
     int j1;
     byte abyte1[] = new byte[j1 = k_int15000];
     try
     {
         inputstream.read(abyte1);
         inputstream.close();
     }
     catch(IOException ioexception)
     {
         Alert alert = new Alert("读取车站库出错:" + ioexception.getMessage());
         b_display.setCurrent(alert);
         return null;
     }
     return abyte0 = readFromFileBuffer(abyte1, i1);
 }

 private static void alert(String s)
 {
     Alert alert;
     (alert = new Alert("警告")).setString(s);
     alert.setTimeout(-2);
     b_display.setCurrent(alert, c_displayable);
 }

 private void c()
 {
     if(e_String_staName1 == null || f_String_staName2 == null)
     {
         alert("请输入信息");
         return;
     }
//     Lulutong.e.a(1, e_String_staName1);
//     Lulutong.e.a(2, f_String_staName2);
     l_int_1_staIndex1 = c_int_String_findStationIdx(e_String_staName1);
     m_int_1_staIndex2 = c_int_String_findStationIdx(f_String_staName2);
     if(l_int_1_staIndex1 < 0 || m_int_1_staIndex2 < 0)
     {
         String s = new String();
         if(l_int_1_staIndex1 < 0)
             s = s + e_String_staName1;
         if(m_int_1_staIndex2 < 0)
             s = s + f_String_staName2;
         alert(s = s + "车站没有找到:(");
         return;
     }
     
     byte staTrain1[] = null;
     byte staTrain2[] = null;
     staTrain1 = a_read_data_station(l_int_1_staIndex1);
     staTrain2 = a_read_data_station(m_int_1_staIndex2);
     if(staTrain1 == null || staTrain2 == null)
     {
         String s1 = new String();
         if(staTrain1 == null)
             s1 = s1 + e_String_staName1;
         if(staTrain2 == null)
             s1 = s1 + f_String_staName2;
         alert(s1 = s1 + "没有查到相关车次信息");
         return;
     }
     i_Vector.removeAllElements();
     int i1 = staTrain1.length;
     int j1 = staTrain2.length;
     for(int k1 = 0; k1 < i1; k1 += 2)
     {
         for(int i2 = 0; i2 < j1; i2 += 2)
             if(staTrain1[k1] == staTrain2[i2] && staTrain1[k1 + 1] == staTrain2[i2 + 1])
             {
                 int j2 = staTrain1[k1] * 128 + staTrain1[k1 + 1];
                 i_Vector.addElement(String.valueOf(j2));
             }

     }

//     if(d_Lulutong.c == null)
//     {
//         d_Lulutong.c = new List("\u7ED3\u679C", 3);
//         d_Lulutong.c.addCommand(d_Lulutong.a);
//         d_Lulutong.c.addCommand(d_Lulutong.b);
//         d_Lulutong.c.setCommandListener(d_Lulutong);
//         d_Lulutong.c.setFitPolicy(1);
//     }
//     i l1;
//     (l1 = new i(this)).start();
 }

 private byte[] b_read_data_train(int index)
 {
     String fileName = String.valueOf((index + 1) % 20);
     byte result[] = null;
     int j1 = k_int15000;
     InputStream inputstream = getClass().getResourceAsStream("/Data/T" + fileName + ".dat");
     byte fileBuffer[] = new byte[j1];
     try
     {
         inputstream.read(fileBuffer);
         inputstream.close();
     }
     catch(IOException _ex)
     {
         alert("加载库信息出错！无法查询 ");
         return null;
     }
     if((result = readFromFileBuffer(fileBuffer, index)) == null)
         alert("信息不全！无法查询");
     return result;
 }

 private static int b(String s)
 {
     s = s.toUpperCase();
     s = s + "/";
     int i1 = h_Vector_Train.size();
     int j1 = 0;
     boolean flag = false;
     while(i1 - j1 > 5) 
     {
         int k1;
         k1 = (k1 = i1 + j1) >> 1;
         String s1 = h_Vector_Train.elementAt(k1).toString();
         if((s1 = s1 + "/").compareTo(s) > 0)
             i1 = k1;
         else
             j1 = k1;
     }
     if((i1 += 5) >= h_Vector_Train.size())
         i1 = h_Vector_Train.size() - 1;
     for(int l1 = j1; l1 <= i1; l1++)
     {
         String s2 = h_Vector_Train.elementAt(l1).toString();
         if((s2 = s2 + "/").indexOf(s) != -1)
             return l1;
     }

     for(int i2 = 0; i2 < h_Vector_Train.size(); i2++)
     {
         String s3 = h_Vector_Train.elementAt(i2).toString();
         if((s3 = s3 + "/").indexOf(s) != -1)
             return i2;
     }

     return -1;
 }

 private static int c_int_String_findStationIdx(String s)
 {
     return g_Vector_stations.indexOf(s);
 }

 private void formatTrainInfo()
 {
     byte trainInfo[];
     int j1;
     int k1;
     byte byte3;
     if(e_String_staName1 == null)
     {
         alert("请输入车次信息!");
         return;
     }
//     Lulutong.e.a(3, e_String_staName1);
     int i1;
     if((i1 = b(e_String_staName1)) < 0)
     {
         String s;
         (s = new String()).concat(e_String_staName1);
         s.concat("没有找到该车次！无法查询");
         alert(s);
         return;
     }
     e_String_staName1 = h_Vector_Train.elementAt(i1).toString();
     if((trainInfo = b_read_data_train(i1)) == null)
     {
         String s1 = new String();
         s1 = s1 + e_String_staName1;
         alert(s1 = s1 + "没有详细信息!");
         return;
     }
     j1 = trainInfo.length;
     k1 = 0;
//     if(d.d == null)
//     {
//         d.d = new List(e_String_staName1 + "次", 3);
//         d.d.addCommand(d.a);
//         d.d.addCommand(d.b);
//         d.d.setCommandListener(d);
//         d.d.setFitPolicy(1);
//     }
//     for(; d.d.size() > 0; d.d.delete(0));
     k1++;
     byte3 = trainInfo[0];
     k1++;

//_L5:
     String s3;
//     if(k1 >= j1)
//         break; /* Loop/switch isn't completed */
     int l1 = trainInfo[k1++] * 128 + trainInfo[k1++];
     
     s3 = g_Vector_stations.elementAt(l1).toString();
     s3 = s3 + " ";
     
     byte bHour;
     if((bHour = trainInfo[k1++]) < 10)
         s3 = s3 + "0";
     s3 = s3 + bHour;
     s3 = s3 + ":";
     byte bMin;
     if((bMin = trainInfo[k1++]) < 10)
         s3 = s3 + "0";
     s3 = s3 + bMin;
     
//     if(d.f[1] != 1) goto _L2; else goto _L1
//_L1:
//     (new StringBuffer()).append(s3);
//     "到\n\停";
//       goto _L3
//_L2:
//     (new StringBuffer()).append(s3);
//     "到停";
//_L3:
//     append();
//     toString();
//     s3;

     byte byte2 = trainInfo[k1++];
     s3 = s3 + byte2;
     s3 = s3 + "分";
     s3 = s3 + (trainInfo[k1++] * 128 + trainInfo[k1++]);
     s3 = s3 + "公里";
//     d.d.append(s3, null);
//     if(true) goto _L5; else goto _L4
//_L4:
     String s2;
     int i2;
     int j2;
     s2 = new String();
     i2 = byte3 & 0xf0;
     j2 = byte3 & 0xf;
     if(i2 == 0 || i2 == 1)
         s2 = s2 + "新";
//     if(i2 != 0 && i2 != 2) goto _L7; else goto _L6
//_L6:
//     (new StringBuffer()).append(s2);
//     "空调";
//       goto _L8
//_L7:
//     (new StringBuffer()).append(s2);
//     "";
//_L8:
//     append();
//     toString();
//     s2;
//     if(j2 != 1) goto _L10; else goto _L9
//_L9:
//     (new StringBuffer()).append(s2);
//     "特快";
//       goto _L11
//_L10:
//     if(j2 != 0) goto _L13; else goto _L12
//_L12:
//     (new StringBuffer()).append(s2);
//     "普快";
//       goto _L11
//_L13:
//     (new StringBuffer()).append(s2);
//     "普客";
//_L11:
//     append();
//     toString();
//     s2;
//     d.d.setTitle(null);
//     d.d.setTicker(new Ticker(new String(e + ":" + s2 + " " + d.d.size())));
//     b.setCurrent(d.d);
     return;
 }

 private void e()
 {
     InputStream inStaIDX;
     StringBuffer stringbuffer;
     byte trainInfo[];
     if(null != g_Vector_stations && null != h_Vector_Train)
     {
//         b.setCurrent(d.a());
//         return;
     }
     h_Vector_Train = null;
     g_Vector_stations = null;
     g_Vector_stations = new Vector();
     h_Vector_Train = new Vector();
     new String("");
     String staIDXFileName = null;
     staIDXFileName = "/Data/station.idx";
     inStaIDX = getClass().getResourceAsStream(staIDXFileName);
     
     stringbuffer = new StringBuffer();
     trainInfo = new byte[j_int34000 + 2];
     int i1_34000 = j_int34000;
     
 	try {

 	 int len = inStaIDX.read(trainInfo, 2, i1_34000);
     inStaIDX.close();
     
     trainInfo[0] = (byte)((len & 0xff00) >> 8);
     trainInfo[1] = (byte)(len & 0xff);
     
     DataInputStream datainputstream;
     String stationNames = (datainputstream = new DataInputStream(new ByteArrayInputStream(trainInfo, 0, len + 2))).readUTF();
     datainputstream.close();
    
     for(int i2 = stationNames.charAt(0) != '\uFEFF' ? 0 : 1; i2 < stationNames.length(); stringbuffer.delete(0, stringbuffer.length()))
     {
         for(; i2 < stationNames.length() && stationNames.charAt(i2) != '\r'; i2++)
             stringbuffer.append(stationNames.charAt(i2));

         i2 += 2;
         g_Vector_stations.addElement(stringbuffer.toString());
     }

 	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}

//       goto _L1
//     Object obj;
//     obj;
     (new StringBuffer()).append("错误：不支持的编码格式!Error:");
//     ((UnsupportedEncodingException) (obj)).getMessage();
//       goto _L2
//     obj;
     (new StringBuffer()).append("加载车站信息出错!Error:");
//     ((IOException) (obj)).getMessage();
//_L2:
//     append();
//     toString();
     a();
//_L1:
     
     
     String s1_TrainIDX_FileName = "/Data/train.idx";
     InputStream in_TrainIDX = getClass().getResourceAsStream(s1_TrainIDX_FileName);
     try
     {
         int j1 = in_TrainIDX.read(trainInfo);
         in_TrainIDX.close();
         for(int l1 = 0; l1 < j1;)
         {
             for(; l1 < j1 && trainInfo[l1] != 13; l1++)
                 stringbuffer.append((char)trainInfo[l1]);

             l1 += 2;
             h_Vector_Train.addElement(stringbuffer.toString());
             stringbuffer.delete(0, stringbuffer.length());
         }

     }
     catch(IOException ioexception)
     {
         alert("加载车次信息出错!Error:" + ioexception.getMessage());
     }
//     b.setCurrent(d.a());
     return;
 }

 private void f()
 {
     if(e_String_staName1 == null)
     {
         alert("请输入车站信息!");
         return;
     }
//     Lulutong.e.a(4, e_String_staName1);
     l_int_1_staIndex1 = c_int_String_findStationIdx(e_String_staName1);
     if(l_int_1_staIndex1 < 0)
     {
         String s = new String();
         s = s + e_String_staName1;
         alert(s = s + "没有找到该车站！无法查询");
         return;
     }
     
     m_int_1_staIndex2 = l_int_1_staIndex1;
     i_Vector.removeAllElements();
     byte staTrainInfo[];
     if((staTrainInfo = a_read_data_station(l_int_1_staIndex1)) == null)
     {
         String s1 = new String();
         if(staTrainInfo == null)
             s1 = s1 + e_String_staName1;
         alert(s1 = s1 + "没有查到相关车次信息");
         return;
     }
     int i1 = staTrainInfo.length;
     for(int j1 = 0; j1 < i1;)
     {
         int k1 = staTrainInfo[j1++] * 128 + staTrainInfo[j1++];
         i_Vector.addElement(String.valueOf(k1));
     }

//     if(d_Lulutong.c == null)
//     {
//         d_Lulutong.c = new List("\u7ED3\u679C", 3);
//         d_Lulutong.c.addCommand(d_Lulutong.a);
//         d_Lulutong.c.addCommand(d_Lulutong.b);
//         d_Lulutong.c.setCommandListener(d_Lulutong);
//         d_Lulutong.c.setFitPolicy(1);
//     }
//     g g1;
//     (g1 = new g(this)).start();
 }

 private void a(boolean flag)
 {
//     for(; d.c.size() > 0; d.c.delete(0));
     if(flag) {
//	     d.c;
	     (new StringBuffer()).append("结果:").append(i_Vector.size());
     }
     else {
//	     d.c;
	     (new StringBuffer()).append("结果:").append(i_Vector.size() / 2);
     }
     
//     append();
//     toString();
//     setTitle();
     int i1 = 0;

//_L9:
//     if(i1 >= i_Vector.size()) //goto _L5; else goto _L4
//_L4:
     byte trainInfo[];
     String s1;
     if(a_int_1 == -1)
     {
         System.out.println("取消了");
         return;
     }
     String s;
     int j1 = Integer.parseInt(s = i_Vector.elementAt(i1).toString());
     trainInfo = getTrain(j1);

     s1 = h_Vector_Train.elementAt(j1).toString();
     if(trainInfo == null)
//         continue; /* Loop/switch isn't completed */
     if(/*d.f[0]*/1 == 1) //goto _L7; else goto _L6
//_L6:
     {
     (new StringBuffer()).append(s1).append(" 次\n时间[");
     }
     else {
//_L7:
     (new StringBuffer()).append(s1).append(" [");
     }

     if(trainInfo[0] < 10)
         s1 = s1 + "0";
     s1 = s1 + trainInfo[0];
     s1 = s1 + ":";
     if(trainInfo[1] < 10)
         s1 = s1 + "0";
     s1 = s1 + trainInfo[1];
     s1 = s1 + "~";
     if(trainInfo[2] < 10)
         s1 = s1 + "0";
     s1 = s1 + trainInfo[2];
     s1 = s1 + ":";
     if(trainInfo[3] < 10)
         s1 = s1 + "0";
     s1 = s1 + trainInfo[3];
     s1 = s1 + "]";
//     d.c.append(s1, null);
////     if(d.c.size() == 1)
////         b.setCurrent(d.c);
////     i1++;
////       goto _L9
//_L5:
//     if(!flag)
//         d.c.setTitle("结果:" + d.c.size());
//     if(d.c.size() == 0)
//         b.setCurrent(d.c);
     return;
 }

 private byte[] getTrain(int i1)
 {
     byte trainByte[] = null;
     String s = g_Vector_stations.elementAt(l_int_1_staIndex1).toString();
     String s1 = g_Vector_stations.elementAt(m_int_1_staIndex2).toString();
     int ai[] = new int[10];
     int ai1[] = new int[10];
     ai[0] = ai1[0] = 0;
     for(int j1 = 1; j1 < 10; j1++)
     {
         ai[j1] = 20;
         ai1[j1] = 20;
     }

     int k1 = 1;
     int l1 = 1;
     for(; l_int_1_staIndex1 + k1 < g_Vector_stations.size() && k1 < 10; k1++)
     {
         String s2;
         if((s2 = g_Vector_stations.elementAt(l_int_1_staIndex1 + k1).toString()).equals(s + "东") || s2.equals(s + "南") || s2.equals(s + "西") || s2.equals(s + "北"))
             ai[l1++] = k1;
     }

     int i2 = 1;
     l1 = 1;
     for(; m_int_1_staIndex2 + i2 < g_Vector_stations.size() && i2 < 10; i2++)
     {
         String s3;
         if((s3 = g_Vector_stations.elementAt(m_int_1_staIndex2 + i2).toString()).equals(s1 + "东") || s3.equals(s1 + "南") || s3.equals(s1 + "西") || s3.equals(s1 + "北"))
             ai1[l1++] = i2;
     }

     String s4 = g_Vector_stations.elementAt(l_int_1_staIndex1).toString();
     String s5;
     boolean flag = (s5 = g_Vector_stations.elementAt(m_int_1_staIndex2).toString()).equals(s4 + "东") || s5.equals(s4 + "南") || s5.equals(s4 + "西") || s5.equals(s4 + "北") 
                      || s4.equals(s5 + "东") || s4.equals(s5 + "南") || s4.equals(s5 + "西") || s4.equals(s5 + "北");
     byte trainInfo[] = b_read_data_train(i1);
     int j2 = l_int_1_staIndex1;
     int k2 = m_int_1_staIndex2;
     int l2 = 0;
     do
     {
         if(l2 >= 10 || ai[l2] > 10)
             break;
         int i3 = 0;
         do
         {
             if(i3 >= 10 || ai1[i3] > 10 || flag && i3 > 1)
                 break;
             l_int_1_staIndex1 = j2 + ai[l2];
             m_int_1_staIndex2 = k2 + ai1[i3];
             if((trainByte = decode_trainData(trainInfo)) != null)
                 break;
             i3++;
         } while(true);
         if(trainByte != null || flag)
             break;
         l2++;
     } while(true);
     l_int_1_staIndex1 = j2;
     m_int_1_staIndex2 = k2;
     return trainByte;
 }

 private static byte[] decode_trainData(byte trainInfo[])
 {
     int index;
     byte byte5;
     byte body[];
     int j1;
     byte head[];
     if(trainInfo == null)
     {
         System.out.println("train is null");
         return null;
     }
     index = 0;
     byte5 = 0;
     body = new byte[10];
     j1 = trainInfo.length;
     index++;//0
     body[4] = trainInfo[0];
     index++;//1
     body[5] = trainInfo[1];
     head = new byte[4];
//_L10:
//     if(i1 >= j1) goto _L2; else goto _L1
//_L1:
     byte byte0;
     byte byte1;
     byte byte2;
     byte byte3;
     byte byte4;
     int k1;
     k1 = trainInfo[index++] * 128 + trainInfo[index++];//2,3
     byte0 = trainInfo[index++];//4
     byte1 = trainInfo[index++];//5
     byte2 = trainInfo[index++];//6
     byte3 = trainInfo[index++];//7
     byte4 = trainInfo[index++];//8
//     if(l != k1) goto _L4; else goto _L3
//_L3:
//     if(l != m) goto _L6; else goto _L5
//_L5:
     body[0] = byte0;
     body[1] = byte1;
     if((byte1 += byte2) >= 60)
     {
         byte0++;
         byte1 -= 60;
         if(byte0 >= 24)
             byte0 -= 24;
     }
     body[2] = byte0;
     body[3] = byte1;
     byte5 = 2;
//     abyte2;
//     0;
     head[1] = head[2] = head[3] = 0;
//       goto _L7
//_L6:
     if((byte1 += byte2) >= 60)
     {
         byte0++;
         byte1 -= 60;
         if(byte0 >= 24)
             byte0 -= 24;
     }
     body[0] = byte0;
     body[1] = byte1;
     byte5 = 1;
     head[0] = byte3;
//     abyte2;
//     1;
//       goto _L8
//_L4:
//     if(m != k1) goto _L10; else goto _L9
//_L9:
//     if(byte5 == 0)
//         return null;
     body[2] = byte0;
     body[3] = byte1;
     byte5 = 2;
     head[2] = byte3;
//     abyte2;
//     3;
//_L8:
//     byte4;
//_L7:
//     JVM INSTR bastore ;
//       goto _L10
//_L2:
     if(byte5 != 2)
     {
         return null;
     } else
     {
         body[6] = head[0];
         body[7] = head[1];
         body[8] = head[2];
         body[9] = head[3];
         return body;
     }
 }

 static void a(d d1)
 {
     d1.b();
 }

 static void a(d d1, boolean flag)
 {
     d1.a(flag);
 }

 public static int a_int_1 = -1;
 private static Display b_display;
 private static Displayable c_displayable;
// private static Lulutong d_Lulutong = null;
 private static String e_String_staName1 = null;
 private static String f_String_staName2 = null;
 private static Vector g_Vector_stations = null;
 private static Vector h_Vector_Train = null;
 private static Vector i_Vector = null;
 private static int j_int34000 = 34000;
 private static int k_int15000 = 15000;
 private static int l_int_1_staIndex1 = -1;
 private static int m_int_1_staIndex2 = -1;


 // Unreferenced inner class Lulutong/c
 class c extends Thread
 {

     public final void run()
     {
//         d_Lulutong.a(a);
     }

     private final d a;

         c()
         {
             a = d.this;
         }
 }


 // Unreferenced inner class Lulutong/i
 class i extends Thread
 {

     public final void run()
     {
//         d_Lulutong.a(a, false);
     }

     private final d a;

         i()
         {
             a = d.this;
         }
 }


 // Unreferenced inner class Lulutong/g
 class g extends Thread
 {

     public final void run()
     {
//         d_Lulutong.a(a, true);
     }

     private final d a;

         g()
         {
             a = d.this;
         }
 }

}