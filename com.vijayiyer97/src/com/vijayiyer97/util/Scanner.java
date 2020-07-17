package com.vijayiyer97.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Scanner extends Tokenizer implements Parser, Iterator<String>, Closeable {
	
	private static final String DELIMITER = "\\p{javaWhitespace}+";
	private static final String LINE_SEPARATOR = System.lineSeparator();
	
	private final Reader reader;
	
	private Pattern delimiter = Pattern.compile(DELIMITER);
	private Pattern lineSeparator = Pattern.compile(LINE_SEPARATOR);
	private Matcher matcher = null;
	private String buffer = null;
	
	private boolean waitForEOT = true;
	
	private int radix = 10;
	
	public Scanner(String source) {
		reader = null;
		
		buffer = source;
	}
	
	public Scanner(InputStream source) {
		if (source == System.in) {
			waitForEOT = false;
		}
		reader = (Reader) new InputStreamReader(source);
	}
	
	public Scanner(File source) throws FileNotFoundException {
		FileInputStream istream = new FileInputStream(source);
		reader = (Reader) new InputStreamReader(istream);
	}
	
	public Scanner(Path source) throws FileNotFoundException {
		File file = new File(source.toString());
		FileInputStream istream = new FileInputStream(file);
		reader = (Reader) new InputStreamReader(istream);
	}
	
	public Scanner(Readable source) {
		reader = (Reader) source;
	}
	
	public Pattern delimiter() {
		return delimiter;
	}
	
	public int radix() {
		return radix;
	}
	
	public Scanner useDelimiter(Pattern delimiter) {
		this.delimiter = delimiter;
		return this;
	}
	
	public Scanner useDelimiter(String delimiter) {
		Pattern pattern = Pattern.compile(delimiter);
		return useDelimiter(pattern);
	}
	
	public Scanner useRadix(int radix) {
		this.radix = radix;
		return this;
	}
	
	public boolean hasNext(Pattern pattern) {
		String token = read(false, delimiter, pattern);
		return token != null;
	}
	
	public boolean hasNext(String pattern) {
		Pattern ptrn = Pattern.compile(pattern);
		return hasNext(ptrn);
	}
	
	public boolean hasNextByte(int radix) {
		try {
			String token = read(false, delimiter, null);
			Byte.parseByte(token, radix);
		} catch (NoSuchElementException except) {
			return false;
		} catch (NumberFormatException except) {
			return false;
		}
		return true;
	}
	
	public boolean hasNextShort(int radix) {
		try {
			String token = read(false, delimiter, null);
			Short.parseShort(token, radix);
		} catch (NoSuchElementException except) {
			return false;
		} catch (NumberFormatException except) {
			return false;
		}
		return true;
	}
	
	public boolean hasNextInt(int radix) {
		try {
			String token = read(false, delimiter, null);
			Integer.parseInt(token, radix);
		} catch (NoSuchElementException except) {
			return false;
		} catch (NumberFormatException except) {
			return false;
		}
		return true;
	}
	
	public boolean hasNextLong(int radix) {
		try {
			String token = read(false, delimiter, null);
			Long.parseLong(token, radix);
		} catch (NoSuchElementException except) {
			return false;
		} catch (NumberFormatException except) {
			return false;
		}
		return true;
	}
	
	public String next(Pattern pattern) {
		return read(true, delimiter, pattern);
	}
	
	public String next(String pattern) {
		Pattern ptrn = Pattern.compile(pattern);
		return next(ptrn);
	}
	
	public boolean hasNextLine() {
		String token = read(false, lineSeparator, null);
		return token != null;
	}
	
	public String nextLine() {
		return read(true, lineSeparator, null);
	}
	
	public byte nextByte(int radix) {
		try {
			String token = read(true, delimiter, null);
			return Byte.parseByte(token, radix);
		} catch (NumberFormatException except) {
			throw new InputMismatchException();
		}
	}
	
	public short nextShort(int radix) {
		try {
			String token = read(true, delimiter, null);
			return Short.parseShort(token, radix);
		} catch (NumberFormatException except) {
			throw new InputMismatchException();
		}
	}
	
	public int nextInt(int radix) {
		try {
			String token = read(true, delimiter, null);
			return Integer.parseInt(token, radix);
		} catch (NumberFormatException except) {
			throw new InputMismatchException();
		}
	}
	
	public long nextLong(int radix) {
		try {
			String token = read(true, delimiter, null);
			return Long.parseLong(token, radix);
		} catch (NumberFormatException except) {
			throw new InputMismatchException();
		}
	}
	
	private String read(boolean consume, Pattern delimiter, Pattern pattern) {
		while (buffer == null) {
			if (EOT) {
				throw new  NoSuchElementException();
			}
			Bytes bytes = readBytes();
			buffer = bytes.toString(delimiter);
		}
		
		if (pattern == null) {
			return scanner(consume, delimiter);
		}
		
		return match(consume, pattern);
	}
	
	private Bytes readBytes() {
		Bytes bytes = null;		
		try {			
			bytes = new Bytes();
			
			do {
				int b = reader.read();
				if (!waitForEOT && (b == 10 || b == 13)) {
					break;
				}
				bytes.append(b);
				if (b == -1) {
					EOT = true; // emit end-of-transmission signal
					break;
				}
			} while (true);
		} catch (IOException except) {
			throw new IllegalStateException();
		}
		
		return bytes;
	}
	
	private String match(boolean consume, Pattern pattern) {
		String match = null;
		matcher = pattern.matcher(this.buffer);
		
		if (matcher.find()) {
			match = matcher.group();
			if (consume) {
				this.buffer = buffer.substring(matcher.end(), buffer.length() - 1);
			}
		}
		
		return match;
	}
	
	private String scanner(boolean consume, Pattern pattern) {
		String token = null;
		String buffer = this.buffer;
		
		do {
			this.matcher = pattern.matcher(buffer);
			if (this.matcher.find()) {
				token = buffer.substring(0, this.matcher.start());
				buffer = buffer.substring(this.matcher.end(), buffer.length());
				if (buffer.isEmpty()) {
					buffer = null;
					break;
				}
			} else {
				token = buffer;
				buffer = null;
				break;
			}
		} while (token.isEmpty());
		
		if (consume) {
			this.buffer = buffer;
		}
		
		return token;
	}
	
	private char parseChar(String s) throws NoSuchElementException {
		if (s.length() != 1) {
			throw new NumberFormatException();
		}
		return s.charAt(0);
	}
	
	@Override
	public boolean hasNext() {
		String token = read(false, delimiter, null);
		return token != null;
	}

	@Override
	public String next() {
		return read(true, delimiter, null);
	}
	
	@Override
	public boolean hasNextBoolean() {
		try {
			String token = read(false, delimiter, null);
			Boolean.parseBoolean(token);
		} catch (NoSuchElementException except) {
			return false;
		} catch (NumberFormatException except) {
			return false;
		}
		return true;
	}

	@Override
	public boolean hasNextByte() {
		return hasNextByte(radix);
	}

	@Override
	public boolean hasNextChar() {
		try {
			String token = read(false, delimiter, null);
			parseChar(token);
		} catch (NoSuchElementException except) {
			return false;
		} catch (NumberFormatException except) {
			return false;
		}
		return true;
	}

	@Override
	public boolean hasNextShort() {
		return hasNextShort(radix);
	}

	@Override
	public boolean hasNextInt() {
		return hasNextInt(radix);
	}

	@Override
	public boolean hasNextFloat() {
		try {
			String token = read(false, delimiter, null);
			Float.parseFloat(token);
		} catch (NoSuchElementException except) {
			return false;
		} catch (NumberFormatException except) {
			return false;
		}
		return true;
	}

	@Override
	public boolean hasNextLong() {
		return hasNextLong(radix);
	}

	@Override
	public boolean hasNextDouble() {
		try {
			String token = read(false, delimiter, null);
			Double.parseDouble(token);
		} catch (NoSuchElementException except) {
			return false;
		} catch (NumberFormatException except) {
			return false;
		}
		return true;
	}

	@Override
	public boolean nextBoolean() {
		try {
			String token = read(true, delimiter, null);
			return Boolean.parseBoolean(token);
		} catch (NumberFormatException except) {
			throw new InputMismatchException();
		}
	}

	@Override
	public byte nextByte() {
		return nextByte(radix);
	}

	@Override
	public char nextChar() {
		try {
			String token = read(true, delimiter, null);
			return parseChar(token);
		} catch (NumberFormatException except) {
			throw new InputMismatchException();
		}
	}

	@Override
	public short nextShort() {
		return nextShort(radix);
	}

	@Override
	public int nextInt() {
		return nextInt(radix);
	}

	@Override
	public float nextFloat() {
		try {
			String token = read(true, delimiter, null);
			return Float.parseFloat(token);
		} catch (NumberFormatException except) {
			throw new InputMismatchException();
		}
	}

	@Override
	public long nextLong() {
		return nextLong(radix);
	}

	@Override
	public double nextDouble() {
		try {
			String token = read(true, delimiter, null);
			return Double.parseDouble(token);
		} catch (NumberFormatException except) {
			throw new InputMismatchException();
		}
	}
	
	@Override
	public void close() {
		try {
			reader.close();
		} catch (IOException except) {
			throw new IllegalStateException();
		}
	}
	
	private final class Bytes {
		private final static int SIZE = 1024;
		
		private int[] data;
		private int internalSize;
		public int size;
		
				
		Bytes() {
			data = new int[SIZE];
			internalSize = SIZE;
			size = 0;
		}
		
		public void append(int value) {
			if (size < internalSize) {
				data[size++] = value;
			} else {
				int[] temp = new int[size + 1];
				
				for (int i = 0; i < size; i++) {
					temp[i] = data[i];
				}
				
				temp[size++] = value;
				
				
				internalSize = size;
								
				data = new int[internalSize];
				
				for (int i = 0; i < internalSize; i++) {
					data[i] = temp[i];
				}
			}
		}
		
		@Override
		public String toString() {
			return toString(delimiter);
		}
		
		public String toString(Pattern delimiter) {
			char[] chars = new char[size];
			
			for (int i = 0; i < size; i++) {
				int b = data[i];
				if (b == -1) {
					chars = new char[i];
					
					for (int j = 0; j < i; j++) {
						chars[j] = (char) data[j];
					}
					
					break;
				} else if (!waitForEOT && (b == 10 || b == 13)) {
					break;
				} else {
					chars[i] = (char) b;
				}
			}
			
			String str = new String(chars);
			
			if (str.isEmpty() || Pattern.matches(delimiter.pattern(), str)) {
				return null;
			}
			
			return str;
		}
	}

}
