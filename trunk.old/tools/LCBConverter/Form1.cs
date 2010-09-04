using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.IO;

namespace LCBConvertor
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }
                /// <summary>
        /// 取单个字符的拼音声母
        /// Code By MuseStudio@hotmail.com
        /// 2004-11-30
        /// </summary>
        /// <param name="c">要转换的单个汉字</param>
        /// <returns>拼音声母</returns>
        public string GetPYChar(string c)
        {
            if ((c == "醴") || (c=="漯")) 
                return "L";
            if (c == "濉")
                return "S";
            if (c == "甬")
                return "Y";
            if (c == "滠")
                return "S";

            byte[] array = new byte[2];
            array = System.Text.Encoding.Default.GetBytes(c);
            int i = (short)(array[0] - '\0') * 256 + ((short)(array[1] - '\0'));

            if (i < 0xB0A1) { MessageBox.Show("*"); return "*"; }
            if ( i < 0xB0C5) return "a";
            if ( i < 0xB2C1) return "b";
            if ( i < 0xB4EE) return "c";
            if ( i < 0xB6EA) return "d";
            if ( i < 0xB7A2) return "e";
            if ( i < 0xB8C1) return "f";
            if ( i < 0xB9FE) return "g";
            if ( i < 0xBBF7) return "h";
            if ( i < 0xBFA6) return "j";
            if ( i < 0xC0AC) return "k";
            if ( i < 0xC2E8) return "l";
            if ( i < 0xC4C3) return "m";
            if ( i < 0xC5B6) return "n";
            if ( i < 0xC5BE) return "o";
            if ( i < 0xC6DA) return "p";
            if ( i < 0xC8BB) return "q";
            if ( i < 0xC8F6) return "r";
            if ( i < 0xCBFA) return "s";
            if ( i < 0xCDDA) return "t";
            if ( i < 0xCEF4) return "w";
            if ( i < 0xD1B9) return "x";
            if ( i < 0xD4D1) return "y";
            if ( i < 0xD7FA) return "z";
            MessageBox.Show("*");
            return "*";
        } 
        private static int[] pyvalue = new int[]{-20319,-20317,-20304,-20295,-20292,-20283,-20265,-20257,-20242,-20230,-20051,-20036,-20032,-20026,  
     -20002,-19990,-19986,-19982,-19976,-19805,-19784,-19775,-19774,-19763,-19756,-19751,-19746,-19741,-19739,-19728,  
     -19725,-19715,-19540,-19531,-19525,-19515,-19500,-19484,-19479,-19467,-19289,-19288,-19281,-19275,-19270,-19263,  
     -19261,-19249,-19243,-19242,-19238,-19235,-19227,-19224,-19218,-19212,-19038,-19023,-19018,-19006,-19003,-18996,  
     -18977,-18961,-18952,-18783,-18774,-18773,-18763,-18756,-18741,-18735,-18731,-18722,-18710,-18697,-18696,-18526,  
     -18518,-18501,-18490,-18478,-18463,-18448,-18447,-18446,-18239,-18237,-18231,-18220,-18211,-18201,-18184,-18183,  
     -18181,-18012,-17997,-17988,-17970,-17964,-17961,-17950,-17947,-17931,-17928,-17922,-17759,-17752,-17733,-17730,  
     -17721,-17703,-17701,-17697,-17692,-17683,-17676,-17496,-17487,-17482,-17468,-17454,-17433,-17427,-17417,-17202,  
     -17185,-16983,-16970,-16942,-16915,-16733,-16708,-16706,-16689,-16664,-16657,-16647,-16474,-16470,-16465,-16459,  
     -16452,-16448,-16433,-16429,-16427,-16423,-16419,-16412,-16407,-16403,-16401,-16393,-16220,-16216,-16212,-16205,  
     -16202,-16187,-16180,-16171,-16169,-16158,-16155,-15959,-15958,-15944,-15933,-15920,-15915,-15903,-15889,-15878,  
     -15707,-15701,-15681,-15667,-15661,-15659,-15652,-15640,-15631,-15625,-15454,-15448,-15436,-15435,-15419,-15416,  
     -15408,-15394,-15385,-15377,-15375,-15369,-15363,-15362,-15183,-15180,-15165,-15158,-15153,-15150,-15149,-15144,  
     -15143,-15141,-15140,-15139,-15128,-15121,-15119,-15117,-15110,-15109,-14941,-14937,-14933,-14930,-14929,-14928,  
     -14926,-14922,-14921,-14914,-14908,-14902,-14894,-14889,-14882,-14873,-14871,-14857,-14678,-14674,-14670,-14668,  
     -14663,-14654,-14645,-14630,-14594,-14429,-14407,-14399,-14384,-14379,-14368,-14355,-14353,-14345,-14170,-14159,  
     -14151,-14149,-14145,-14140,-14137,-14135,-14125,-14123,-14122,-14112,-14109,-14099,-14097,-14094,-14092,-14090,  
     -14087,-14083,-13917,-13914,-13910,-13907,-13906,-13905,-13896,-13894,-13878,-13870,-13859,-13847,-13831,-13658,  
     -13611,-13601,-13406,-13404,-13400,-13398,-13395,-13391,-13387,-13383,-13367,-13359,-13356,-13343,-13340,-13329,  
     -13326,-13318,-13147,-13138,-13120,-13107,-13096,-13095,-13091,-13076,-13068,-13063,-13060,-12888,-12875,-12871,  
     -12860,-12858,-12852,-12849,-12838,-12831,-12829,-12812,-12802,-12607,-12597,-12594,-12585,-12556,-12359,-12346,  
     -12320,-12300,-12120,-12099,-12089,-12074,-12067,-12058,-12039,-11867,-11861,-11847,-11831,-11798,-11781,-11604,  
     -11589,-11536,-11358,-11340,-11339,-11324,-11303,-11097,-11077,-11067,-11055,-11052,-11045,-11041,-11038,-11024,  
     -11020,-11019,-11018,-11014,-10838,-10832,-10815,-10800,-10790,-10780,-10764,-10587,-10544,-10533,-10519,-10331,  
     -10329,-10328,-10322,-10315,-10309,-10307,-10296,-10281,-10274,-10270,-10262,-10260,-10256,-10254};  
         private static string[] pystr = new string[]{"a","ai","an","ang","ao","ba","bai","ban","bang","bao","bei","ben","beng","bi","bian","biao",  
     "bie","bin","bing","bo","bu","ca","cai","can","cang","cao","ce","ceng","cha","chai","chan","chang","chao","che","chen",  
     "cheng","chi","chong","chou","chu","chuai","chuan","chuang","chui","chun","chuo","ci","cong","cou","cu","cuan","cui",  
     "cun","cuo","da","dai","dan","dang","dao","de","deng","di","dian","diao","die","ding","diu","dong","dou","du","duan",  
     "dui","dun","duo","e","en","er","fa","fan","fang","fei","fen","feng","fo","fou","fu","ga","gai","gan","gang","gao",  
     "ge","gei","gen","geng","gong","gou","gu","gua","guai","guan","guang","gui","gun","guo","ha","hai","han","hang",  
     "hao","he","hei","hen","heng","hong","hou","hu","hua","huai","huan","huang","hui","hun","huo","ji","jia","jian",  
     "jiang","jiao","jie","jin","jing","jiong","jiu","ju","juan","jue","jun","ka","kai","kan","kang","kao","ke","ken",  
     "keng","kong","kou","ku","kua","kuai","kuan","kuang","kui","kun","kuo","la","lai","lan","lang","lao","le","lei",  
     "leng","li","lia","lian","liang","liao","lie","lin","ling","liu","long","lou","lu","lv","luan","lue","lun","luo",  
     "ma","mai","man","mang","mao","me","mei","men","meng","mi","mian","miao","mie","min","ming","miu","mo","mou","mu",  
     "na","nai","nan","nang","nao","ne","nei","nen","neng","ni","nian","niang","niao","nie","nin","ning","niu","nong",  
     "nu","nv","nuan","nue","nuo","o","ou","pa","pai","pan","pang","pao","pei","pen","peng","pi","pian","piao","pie",  
     "pin","ping","po","pu","qi","qia","qian","qiang","qiao","qie","qin","qing","qiong","qiu","qu","quan","que","qun",  
     "ran","rang","rao","re","ren","reng","ri","rong","rou","ru","ruan","rui","run","ruo","sa","sai","san","sang",  
     "sao","se","sen","seng","sha","shai","shan","shang","shao","she","shen","sheng","shi","shou","shu","shua",  
     "shuai","shuan","shuang","shui","shun","shuo","si","song","sou","su","suan","sui","sun","suo","ta","tai",  
     "tan","tang","tao","te","teng","ti","tian","tiao","tie","ting","tong","tou","tu","tuan","tui","tun","tuo",  
     "wa","wai","wan","wang","wei","wen","weng","wo","wu","xi","xia","xian","xiang","xiao","xie","xin","xing",  
     "xiong","xiu","xu","xuan","xue","xun","ya","yan","yang","yao","ye","yi","yin","ying","yo","yong","you",  
     "yu","yuan","yue","yun","za","zai","zan","zang","zao","ze","zei","zen","zeng","zha","zhai","zhan","zhang",  
     "zhao","zhe","zhen","zheng","zhi","zhong","zhou","zhu","zhua","zhuai","zhuan","zhuang","zhui","zhun","zhuo",  
     "zi","zong","zou","zu","zuan","zui","zun","zuo"};  
   
         public static string Convert(string chrstr)  
         {  
             string temp = "";  
             int c = chrstr.Length;  
             for (int i = 0; i < c; i++)  
             {  
                 if (chrstr[i] > 255)  
                     temp += _Convert(chrstr[i].ToString());  
                 else  
                     temp += chrstr[i].ToString();  
             }  
             return temp;  
         }  
         /// <summary>  
         /// 汉字转换为拼音  
         /// </summary>  
         /// <param name="chrstr"></param>  
         /// <returns></returns>  
         private static string _Convert(string chrstr)  
         {  
             byte[] array = new byte[2];  
             string returnstr = "";  
             int chrasc = 0;  
             int i1 = 0;  
             int i2 = 0;  
             char[] nowchar = chrstr.ToCharArray();  
             for (int j = 0; j < nowchar.Length; j++)  
             {  
                 array = System.Text.Encoding.Default.GetBytes(nowchar[j].ToString());  
                 i1 = (short)(array[0]);  
                 i2 = (short)(array[1]);  
   
                 chrasc = i1 * 256 + i2 - 65536;  
                 if (chrasc > 0 && chrasc < 160)  
                 {  
                     returnstr += nowchar[j];  
                 }  
                 else  
                 {  
                     for (int i = (pyvalue.Length - 1); i >= 0; i--)  
                     {  
                         if (pyvalue[i] <= chrasc)  
                         {  
                             returnstr += pystr[i];  
                             break;  
                         }  
                     }  
                 }  
             }  
             return returnstr;  
   
         }  
       
        private void button1_Click(object sender, EventArgs e)
        {
            stationDataSet ds = new stationDataSet();
            stationDataSetTableAdapters.站名TableAdapter zm = new LCBConvertor.stationDataSetTableAdapters.站名TableAdapter();
            zm.Fill(ds.站名);
            stationDataSetTableAdapters.线路名TableAdapter xlm = new LCBConvertor.stationDataSetTableAdapters.线路名TableAdapter();
            xlm.Fill(ds.线路名);
            stationDataSetTableAdapters.线路里程TableAdapter xllc = new LCBConvertor.stationDataSetTableAdapters.线路里程TableAdapter();
            xllc.Fill(ds.线路里程);
            stationDataSetTableAdapters.车站等级TableAdapter xl = new LCBConvertor.stationDataSetTableAdapters.车站等级TableAdapter();
            xl.Fill(ds.车站等级);


            System.IO.FileStream fs = new System.IO.FileStream("exl.eda", System.IO.FileMode.Create);
            System.IO.StreamWriter sw = new System.IO.StreamWriter(fs, Encoding.UTF8);
            char last = ' ';
            int i = 0;
            System.Collections.ArrayList a1 = new System.Collections.ArrayList();
            System.Collections.ArrayList a2 = new System.Collections.ArrayList();
            System.Collections.ArrayList a3 = new System.Collections.ArrayList();
            foreach (stationDataSet.线路名Row r in ds.线路名.Rows)
            {
                char c = GetPYChar(r.线路名.Substring(0, 1)).ToUpper()[0];
                if (c == last)
                {
                    ++i;
                }
                else
                {
                    last = c;
                    i = 0;
                }
                sw.WriteLine("{0}{1}{2}", c, encode1(i), r.线路名);
                a1.Add(c + encode1(i));
                a2.Add(r.线路名);
            }
            sw.Close();
            fs.Close();

            fs = new System.IO.FileStream("ezm.eda", System.IO.FileMode.Create);
            sw = new System.IO.StreamWriter(fs, Encoding.UTF8);
            SortedList<string, int> sl = new SortedList<string,int>();
            int ddd = 0;
            foreach (stationDataSet.站名Row r in ds.站名.Rows)
            {
                sw.WriteLine("{0}", r.站名);
                sl.Add(r.站名, ddd);
                ++ddd;
            }
            sw.Close();
            fs.Close();

            fs = new System.IO.FileStream("elc.eda", System.IO.FileMode.Create);
            sw = new System.IO.StreamWriter(fs, Encoding.UTF8);
            for ( i = 0; i < a1.Count; ++i)
            {
                stationDataSet.线路里程Row[] rs = (stationDataSet.线路里程Row[])ds.线路里程.Select(@"线路名='" + a2[i].ToString() + "'", "距起始站里程");
                foreach (stationDataSet.线路里程Row r in rs)
                {
                    stationDataSet.车站等级Row  [] row = (stationDataSet.车站等级Row[])ds.车站等级.Select(@"站名='" + r.站名 + "'", "等级");
                    int temp = 3;
                    if (row.Length != 0)
                    {
                        temp = row[0].等级;
                    }
                    sw.WriteLine("{0}{1}{2}{3}", a1[i], temp, encode2(r.距起始站里程).ToString(), r.站名);
                }

            }
            sw.Close();
            fs.Close();

            //
            fs = new System.IO.FileStream("ecc.eda", System.IO.FileMode.Create);
            sw = new System.IO.StreamWriter(fs, Encoding.UTF8);
            FileStream fs2 = new System.IO.FileStream("etrc.eda", System.IO.FileMode.Create);
            StreamWriter sw2 = new System.IO.StreamWriter(fs2, Encoding.UTF8);
            
            System.IO.DirectoryInfo di = new DirectoryInfo(@"C:\trf");
            FileInfo[] fi = di.GetFiles();
            int id = 0;
            foreach (FileInfo f in fi)
            {
                try
                {
                    StreamReader sr = new StreamReader(f.FullName, Encoding.Default);
                    int line = 0;
                    string t = "";
                    string t1 = "";
                    while (!sr.EndOfStream)
                    {
                        string data = sr.ReadLine();
                        string[] datum;
                        int stationid;
                        switch (line)
                        {
                            case 0:
                                datum = data.Split(new char[] { ',' });
                                System.Diagnostics.Debug.Assert(datum.Length == 4);
                                sw.WriteLine(datum[1]);
                                t1 = encode2(id);
                                break;
                            case 1:
                            case 2:
                                break;
                            default:
                                datum = data.Split(new char[] { ',' });
                                if (datum[0] == "木镇")
                                    datum[0] = "椑木镇";
                                else if (datum[0] == "黄")
                                    datum[0] = "黄磏";
                                else if (datum[0] == "滩")
                                    datum[0] = "鲜滩";
                                else if (datum[0] == "思")
                                    datum[0] = "思濛";
                                else if (datum[0] == "弯")
                                    datum[0] = "弯坵";
                                //else if (datum[0] == "汨罗东")
                                //    datum[0] = "汩罗东";

                                System.Diagnostics.Debug.Assert(datum.Length == 3);
                                try
                                {
                                    stationid = sl[datum[0]];
                                }
                                catch
                                {
                                    MessageBox.Show(datum[0]);
                                    throw;
                                }
                                t = encode2(stationid);
                                t += encode1(int.Parse(datum[1].Substring(0, 2)));
                                t += encode1(int.Parse(datum[1].Substring(3, 2)));
                                t += encode1(int.Parse(datum[2].Substring(0, 2)));
                                t += encode1(int.Parse(datum[2].Substring(3, 2)));
                                sw2.Write(t1 + t);
                                break;
                        }

                        ++line;
                    }
                    sr.Close();
                    ++id;
                }
                catch
                {
                    MessageBox.Show(f.Name);
                }
            }
            MessageBox.Show("success");
            sw2.Close();
            fs2.Close();
            sw.Close();
            fs.Close();
        }

        public static char[] codeTable = {
                '0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'a', 'b', 'c', 'd', 'e', 'f',
                'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
                'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
                'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D',
                'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
                'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                'U', 'V', 'W', 'X', 'Y', 'Z', '.', ' ',
                '!', '@', '#', '$', '%', '^', '&', '*',
                '(', ')', '-', '_', '=', '+', '[', ']',
        };

        //解码，字符到数值
        public static int decode(char c)
        {
            for (int i = 0; i < codeTable.Length; i++)
                if (codeTable[i] == c)
                    return i;

            return 0;
        }

        //一位编码（用于时、分、站序的数值）
        public static String encode1(int num)
        {
            return "" + codeTable[num].ToString();
        }

        //两位编码（用于编号、里程等数值）
        public static String encode2(int num)
        {
            if (num >= codeTable.Length * codeTable.Length)
            {
                System.Diagnostics.Debug.Assert(false);
                return "##";
            }

            int num1 = num / codeTable.Length;
            int num2 = num % codeTable.Length;

            return "" + codeTable[num1].ToString() + codeTable[num2].ToString();
        }
    }
}