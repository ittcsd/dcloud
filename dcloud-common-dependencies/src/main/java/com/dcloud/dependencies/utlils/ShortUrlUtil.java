package com.dcloud.dependencies.utlils;

import java.util.Random;


public class ShortUrlUtil {

	private ShortUrlUtil(){}

	/**
	 * 短链的生成
	 * 
	 * @return shortUrl
	 */
	public static String generateShortUrl() {

		String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
				"g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q",
				"r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1",
				"2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C",
				"D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
				"O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y",
				"Z"

		};
		Random random = new Random();
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < 6; i++) {
			int number = random.nextInt(62);
			buffer.append(chars[number]);
		}
		
		// 获得短链
		return buffer.toString();
	}

	public static void main(String[] args) {
		String s = generateShortUrl();
		System.out.println("s = " + s);
	}
}
