package com.vijayiyer97.util;

import java.util.Arrays;

final class DecimalSet {
	public char[] digits;
	public int signum;


	DecimalSet() {
		digits = new char[1];
		digits[0] = '0';
	}

	DecimalSet(char[] chars) {
		if (chars[0] < 48 || chars[0] > 57) {
			if (chars[0] == '-') {
				signum = -1;
				digits = Arrays.copyOfRange(chars, 1, chars.length);
			} else if (chars[0] == '+') {
				signum = 1;
			} else {
				throw new  NumberFormatException("ENTER MESSAGE HERE");
			}
			
			digits = Arrays.copyOfRange(chars, 1, chars.length);
		} else {
			int start = -1;
			for (int i = 0; i < chars.length; i++) {
				if (chars[i] != '0') {
					start = i;
				}
			}
			
			if (start == -1) {
				digits = new char[1];
				digits[0] = '0';
			} else {
				digits = Arrays.copyOfRange(chars, start, chars.length);
			}
		}

	}
}
