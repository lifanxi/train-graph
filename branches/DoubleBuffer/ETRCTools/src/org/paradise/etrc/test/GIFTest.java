package org.paradise.etrc.test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;
import org.paradise.etrc.test.gif.*;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class GIFTest {
	public static void test1() {
		try {
			// 读取模板图片内容
			BufferedImage image = ImageIO.read(new FileInputStream("c:\\1.jpg"));
			Graphics2D g = image.createGraphics();// 得到图形上下文
			g.setColor(Color.BLACK); // 设置画笔颜色
			// 设置字体
			g.setFont(new Font(ETRC.getString("FONT_NAME"), Font.LAYOUT_LEFT_TO_RIGHT, 15));// 写入签名
			g.drawString("很好吃诶，要不要也来一口？好吧! ", 43, image.getHeight() - 10);
			g.dispose();
			FileOutputStream out = new FileOutputStream("c:\\3.jpg");
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			encoder.encode(image);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void test2() {
		try {
			// 指定Frame的文件
			String imgFileName[] = new String[] { "c:\\1.jpg", "c:\\2.jpg",
					"c:\\3.jpg" };
			String outputFileName = "c:\\test.gif";
			AnimatedGifEncoder e = new AnimatedGifEncoder();
			e.start(outputFileName);// 开始处理
			e.setDelay(500); // 设置延迟时间
			for (int i = 0; i < imgFileName.length; i++) {
				e.addFrame(ImageIO.read(new FileInputStream(imgFileName[i])));// 加入Frame
			}
			e.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		test2();
	}
}