package org.paradise.util;

import java.io.UnsupportedEncodingException;

public class Tools {
	public static String byteToString(byte[] bs) {
		String rt = "";
		for (int i = 0; i < bs.length; i++)
			rt += toHex16(bs[i]);
		return rt;
	}
	
	public static void printBytesOneLine(byte[] bs) {
		for (int i = 0; i < bs.length; i++)
			System.out.print(toHex16(bs[i]) + ":");
		System.out.println();
	}

	public static void printBytes(byte[] bs) {
		for (int i = 0; i < bs.length; i++)
			System.out.println(i + ":" + toHex16(bs[i]) + ":" + bs[i]);
	}

	public void printInts(int[] is) {
		for (int i = 0; i < is.length; i++)
			System.out.println(i + ":" + toHex32(is[i]) + ":" + is[i]);
	}

	static String toHex32(int i) {
		return toHex8((i >> 12) & 0x000F) + toHex8((i >> 8) & 0x000F)
				+ toHex8((i >> 4) & 0x000F) + toHex8(i & 0x000F);
	}

	public static String toHex16(int b) {
		int i = b < 0 ? b + 256 : b;
		if (i > 255)
			return "??";

		String hex = toHex8(i / 16) + toHex8(i % 16);
		return hex;
	}

	static String toHex8(int b) {
		switch (b) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
			return "" + b;
		case 10:
			return "A";
		case 11:
			return "B";
		case 12:
			return "C";
		case 13:
			return "D";
		case 14:
			return "E";
		case 15:
			return "F";
		default:
			return "?";
		}
	}
	
	public static byte Hex8ToInt(char hex8) {
		switch(hex8) {
		case '0': return 0;
		case '1': return 1;
		case '2': return 2;
		case '3': return 3;
		case '4': return 4;
		case '5': return 5;
		case '6': return 6;
		case '7': return 7;
		case '8': return 8;
		case '9': return 9;
		case 'A': return 10;
		case 'B': return 11;
		case 'C': return 12;
		case 'D': return 13;
		case 'E': return 14;
		case 'F': return 15;
		default: return (byte) 0;
		}
	}
	
	public static byte Hex16ToInt(String hex16) {
		return (byte) (Hex8ToInt(hex16.charAt(0)) * 16 + Hex8ToInt(hex16.charAt(1)));
	}
	
	public static byte[] HexToBytes(String hex) throws UnsupportedEncodingException {
		hex = hex.toUpperCase();
		byte bt[] = new byte[hex.length()/2];
		int j=0;
		for(int i=0; i<=hex.length()-2; i+=2) {
			String str = hex.substring(i, i+2);
			byte ch = Hex16ToInt(str);
			//System.out.println(str+":"+ch);
			bt[j++] = ch;
		}
		
		return bt;
	}
	
	public static void main(String argv[]) {
		try {
			String str1 = new String(HexToBytes("6170706C69636174696F6E2F766E642E7761702E636F6E6E65637469766974792D7762786D6C3B5345433D313B4D41433D3933423337374137383541323630433932383945333645304633313545303531394232454436334600"));
			System.out.println(str1 + ":::" + "str2");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
