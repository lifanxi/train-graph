package org.paradise.etrc.test;

public class TKTable {
/*	
 public int c;
 public int d;
 public int e;
 public int f;
 public int g;
 public int h;
 public int i;
 public int j;
 public boolean k;
 public int l;
 public int m;
 public int n;
 //public a u;
 public int A;

 public TKTable(Display display, Displayable displayable, String s1, int i1)
 {
     c = 0;
     d = 0;
     g = 0;
     h = 55;
     k = false;
//     r = new Command("返回", 3, 7);
//     s = new Command("发送结果", 8, 7);
     setTitle(s1);
     A = i1;
     if(i1 == 1)
     {
//         q = new Command("停靠站信息", 8, 2);
//         p = new Command("详情..", 8, 1);
//         addCommand(q);
//         addCommand(p);
//         setTitle(s1 + "\n共计:" + list.size() + "次");
     }
     if(i1 == 2)
     {
//         p = new Command("详细...", 8, 1);
//         addCommand(p);
     }
//     addCommand(r);
//     setCommandListener(this);
//     t = display;
//     v = displayable;
 }

 public final void paint(Graphics g1)
 {
//     d = g1.getFont().getHeight();
//     g = d * 3;
//     a = g1.getClipHeight();
//     b = g1.getClipWidth();
     if(!k)
     {
         a(g1, o, 0, A);
         drawGrid(0, e, b, d, g1);
         j = e;
         k = true;
     } else
     {
         switch(i)
         {
         default:
             break;

         case 1: // '\001'
             if(j + d >= a)
             {
                 if(l < o.size())
                     a(g1, o, m + 1, A);
                 else
                     return;
             } else
             {
                 a(g1, o, m, A);
                 j += d;
             }
             drawGrid(0, j, b, d, g1);
             break;

         case 2: // '\002'
             if(j <= e)
             {
                 if(m > 0)
                     a(g1, o, m - 1, A);
                 else
                     return;
             } else
             {
                 a(g1, o, m, A);
                 j -= d;
             }
             drawGrid(0, j, b, d, g1);
             break;

         case 3: // '\003'
             a(g1, o, m, A);
             drawGrid(0, j, b, d, g1);
             break;
         }
     }
     i = 3;
 }

 public final void keyPressed(int i1)
 {
     int j1 = getGameAction(i1);
     k = true;
     if(j1 == 6)
     {
         i = 1;
         if(n < f - 1)
             n++;
         repaint();
     }
     if(j1 == 1)
     {
         i = 2;
         if(n > 0)
             n--;
         repaint();
     }
     if(j1 == 8 || j1 == 5)
         a(A);
 }

 private void a(Graphics g1, List list, int i1, int j1)
 {
     c = 0;
     g1.setColor(255, 255, 255);
     g1.fillRect(0, 0, a, b);
     f = a / d;
     e = (a - f * d) + d;
     int k1 = i1;
     int l1 = 1;
     switch(j1)
     {
     case 1: // '\001'
         g1.setColor(119, 149, 167);
         g1.fillRect(0, 0, b, e);
         g1.setColor(255, 255, 255);
         c += d;
         g1.drawString("车次", 3, 3, 0);
         g1.drawString("开点", g + 3, 3, 0);
         int i2 = (b - g) / 3;
         g1.drawString("到点", g + i2 * 1 + 3, 3, 0);
         g1.drawString("硬座", g + i2 * 2 + 3, 3, 0);
         c = e;
         m = i1;
         f = (a - e) / d;
         for(int j2 = 1; j2 <= f; j2++)
         {
             g1.setColor(255, 255, 255);
             g1.fillRect(g, c, b, d);
             g1.setColor(0, 0, 0);
             g1.drawLine(0, c, b, c);
             if(k1 < list.size() && l1 <= f)
             {
                 String s1 = list.getString(k1);
                 String as[] = getTK(s1, 1);
                 drawGrid(as, i2, g1);
                 k1++;
                 l1++;
             }
             c += d;
             g1.setColor(221, 221, 221);
             g1.fillRect(0, c, b, d);
             g1.setColor(0, 0, 0);
             g1.drawLine(0, c, b, c);
             if(k1 < list.size() && l1 <= f)
             {
                 String s2 = list.getString(k1);
                 String as1[] = getTK(s2, 1);
                 drawGrid(as1, i2, g1);
                 k1++;
                 l1++;
             }
             c += d;
         }

         l = k1;
         g1.setColor(0, 0, 0);
         g1.drawLine(g, 0, g, a);
         g1.drawLine(g + i2 * 1, 0, g + i2 * 1, a);
         g1.drawLine(g + i2 * 2, 0, g + i2 * 2, a);
         return;

     case 2: // '\002'
         g1.setColor(102, 0, 102);
         g1.fillRect(0, 0, b, e);
         g = 16;
         g1.setColor(255, 255, 255);
         c += d;
         int k2 = (b - g - h) / 3;
         g1.drawString("站名", g + 3, 3, 0);
         g1.drawString("到点", g + h + 3, 3, 0);
         g1.drawString("开点", g + h + k2 * 1 + 3, 3, 0);
         g1.drawString("公里", g + h + k2 * 2 + 3, 3, 0);
         g1.fillRect(0, e, g, a);
         c = e;
         m = i1;
         f = (a - e) / d;
         for(int l2 = 1; l2 <= f; l2++)
         {
             g1.setColor(255, 255, 255);
             g1.fillRect(g, c, b, d);
             g1.setColor(0, 0, 0);
             g1.drawLine(0, c, b, c);
             if(k1 < list.size() && l1 <= f)
             {
                 String s3 = list.getString(k1);
                 String as2[] = getTK(s3, 2);
                 b(as2, k2, g1);
                 k1++;
                 l1++;
             }
             c += d;
             g1.setColor(239, 237, 250);
             g1.fillRect(0, c, b, d);
             g1.setColor(0, 0, 0);
             g1.drawLine(0, c, b, c);
             if(k1 < list.size() && l1 <= f)
             {
                 String s4 = list.getString(k1);
                 String as3[] = getTK(s4, 2);
                 b(as3, k2, g1);
                 k1++;
                 l1++;
             }
             c += d;
         }

         l = k1;
         g1.setColor(0, 0, 0);
         g1.drawLine(g, 0, g, a);
         g1.drawLine(g + h, 0, g + h, a);
         g1.drawLine(g + h + k2 * 1, 0, g + h + k2 * 1, a);
         g1.drawLine(g + h + k2 * 2, 0, g + h + k2 * 2, a);
         break;
     }
 }

 private static void drawGrid(int i1, int j1, int k1, int l1, Graphics g1)
 {
     g1.setColor(255, 0, 0);
     g1.drawRect(i1, j1, k1, l1);
     g1.drawRect(i1 + 1, j1 + 1, k1 - 2, l1 - 2);
 }

 private String[] getTK(String s1, int i1)
 {
     String as[] = (String[])null;
     switch(i1)
     {
     case 1: // '\001'
         as = new String[4];
         String as1[] = a(s1, 11, "\n");
         as[0] = as1[0].substring(3);
         if(as[0].length() > 4)
             as[0] = as[0].substring(0, 4) + "*";
         as[1] = as1[3].substring(5);
         as[2] = as1[4].substring(as1[4].indexOf(":") + 1);
         as[3] = as1[6].substring(3);
         break;

     case 2: // '\002'
         as = a(s1, 5, ",");
         break;
     }
     return as;
 }

 public final void commandAction(Command command, Displayable displayable)
 {
     if(displayable == this)
     {
         if(command == p)
             a(A);
         if(command == r)
             t.setCurrent(v);
         if(command == q)
         {
             if(m + n >= o.size())
                 return;
             String s1;
             int i1 = (s1 = o.getString(m + n)).indexOf(":") + 1;
             if((s1 = s1.substring(i1, (i1 + s1.indexOf("\n")) - 2).trim()).indexOf("/") >= 0)
                 s1 = s1.substring(0, s1.indexOf("/"));
             j j1 = new j(s1, this, t, 1);
             (new Thread(j1)).start();
         }
     }
     if(displayable == u)
     {
         if(command == r)
         {
             i = 3;
             t.setCurrent(this);
             u = null;
             repaint();
         }
         if(command == s)
         {
             w = new Form("发送查询结果");
             x = new TextField("收件人1", "", 12, 2);
             y = new TextField("收件人2(可选)", "", 12, 2);
             z = new TextField("收件人3(可选)", "", 12, 2);
             w.append(x);
             w.append(y);
             w.append(z);
             s = new Command("发送", 4, 1);
             w.addCommand(s);
             w.addCommand(r);
             w.setCommandListener(this);
             t.setCurrent(w);
         }
     }
     if(displayable == w)
     {
         if(command == s)
         {
             String s2 = o.getString(m + n);
             c c1;
             if(x.getString().length() == 11)
                 (c1 = new c(x.getString(), s2, t)).start();
             if(y.getString().length() == 11)
                 (c1 = new c(y.getString(), s2, t)).start();
             if(z.getString().length() == 11)
                 (c1 = new c(z.getString(), s2, t)).start();
         }
         if(command == r)
             t.setCurrent(u);
     }
 }

 private void a(int i1)
 {
     if(m + n >= o.size())
         return;
     i = 0;
     u = new a(o.getString(m + n), t, this, i1);
     if(i1 == 1)
         u.addCommand(s);
     u.addCommand(r);
     u.setCommandListener(this);
     t.setCurrent(u);
 }

 private static String[] a(String s1, int i1, String s2)
 {
     int j1 = 0;
     String as[] = new String[i1];
     for(int k1 = 1; k1 <= i1; k1++)
     {
         int l1;
         if((l1 = s1.indexOf(s2, j1)) == -1)
         {
             as[i1 - 1] = s1.substring(j1 + 1, s1.length());
             break;
         }
         as[k1 - 1] = s1.substring(j1, l1);
         j1 = l1 + 1;
     }

     return as;
 }

 private void a(String as[], int i1, Graphics g1)
 {
     g1.setColor(0, 0, 0);
     g1.drawString(as[0], 3, c + 3, 0);
     g1.drawString(as[1], g + 3, c + 3, 0);
     g1.drawString(as[2], g + i1 * 1 + 3, c + 3, 0);
     g1.drawString(as[3], g + i1 * 2 + 3, c + 3, 0);
 }

 private void b(String as[], int i1, Graphics g1)
 {
     int j1 = g + h;
     g1.setColor(0, 0, 255);
     g1.drawString(as[0], 3, c + 3, 0);
     g1.setColor(0, 0, 0);
     if(as[1].length() > 3)
         as[1] = as[1].substring(0, 2) + "*";
     g1.drawString(as[1], g + 3, c + 3, 0);
     if(!as[2].equals("B"))
     {
         g1.drawString(as[2], j1 + 3, c + 3, 0);
     } else
     {
         g1.setColor(0, 255, 0);
         g1.drawString("始发", j1 + 3, c + 3, 0);
         g1.setColor(0, 0, 0);
     }
     if(!as[3].equals("E"))
     {
         g1.drawString(as[3], j1 + i1 * 1 + 3, c + 3, 0);
     } else
     {
         g1.setColor(255, 0, 0);
         g1.drawString("终点", j1 + i1 * 1 + 3, c + 3, 0);
         g1.setColor(0, 0, 0);
     }
     g1.drawString(as[4], j1 + i1 * 2 + 3, c + 3, 0);
 }

 public static long a(long l1)
 {
     long l2;
     return l2 = Togo.drawGrid(l2 = l1 + 7L);
 }
 
*/
}
