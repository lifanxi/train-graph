package org.paradise.etrc.data.skb;

public class ETRCData {
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
	
	public static int decode(char c) {
		for(int i=0; i<codeTable.length; i++)
			if(codeTable[i] == c)
				return i;

		return 0;
	}
	
	public static String encode1(int num) {
		return "" + codeTable[num];
	}
	
	public static String encode2(int num) {
		if(num >= codeTable.length*codeTable.length)
			return "##";
		
		int num1 = num / codeTable.length;
		int num2 = num % codeTable.length;
		
		return "" + codeTable[num1] + codeTable[num2];
	}
	
}
