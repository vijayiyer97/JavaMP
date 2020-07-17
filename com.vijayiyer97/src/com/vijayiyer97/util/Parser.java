package com.vijayiyer97.util;

interface Parser {
	
	boolean hasNextBoolean();
	boolean hasNextByte();
	boolean hasNextChar();
	boolean hasNextShort();
	boolean hasNextInt();
	boolean hasNextFloat();
	boolean hasNextLong();
	boolean hasNextDouble();
	
	boolean nextBoolean();
	byte nextByte();
	char nextChar();
	short nextShort();
	int nextInt();
	float nextFloat();
	long nextLong();
	double nextDouble();
}
