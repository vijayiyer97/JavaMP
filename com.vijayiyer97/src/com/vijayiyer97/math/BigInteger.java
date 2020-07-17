package com.vijayiyer97.math;

import java.util.Arrays;

import com.vijayiyer97.util.BitSet;
import com.vijayiyer97.util.BitSetBE;

public final class BigInteger extends Number implements Comparable<BigInteger> {

	/**
	 * Allows object serialization.
	 */
	private static final long serialVersionUID = -591565568046932669L;

	/**
	 * 	Stores the binary representation of {@code BigInteger} in big-endian format.
	 */
	private BitSet bits;

	/**
	 * Initializes a {@code BigInteger} representation of zero.
	 */
	BigInteger() {
		bits = BitSet.ZERO;
	}

	/**
	 * Initializes a {@code BigInteger} instance from bits represented in a big-endian two's compelement notation.
	 * 
	 * @param val Bits in big-endian two's complement format.
	 */
	public BigInteger(byte[] val) {
		bits = new BitSetBE(val);
	}

	/**
	 * Initializes a {@code BigInteger} instance from a signature and magnitude represented in a big-endian 
	 * standard notation.
	 * 
	 * @param signum Signature of {@code BigInteger}.
	 * @param magnitude Magnitude of {@code BigInteger}.
	 */
	public BigInteger(int signum, byte[] magnitude) {
		bits = new BitSetBE(signum, magnitude);
	}

	/**
	 * Initializes a {@code BigInteger} instance from a {@code String} decimal value.
	 * 
	 * @param val {@code String} representation of decimal value.
	 */
	public BigInteger(String val) {
		this();

		char[] chars = val.toCharArray(); // digit cache

		for (char c: chars) {
			if (c < '0' || c > '9') {
				throw new NumberFormatException(val + " is not a valid integer");
			}
		}
		
		int start = 0;
		for (char c: chars) {
			if (c != '0') {
				break;
			}

			start++;
		}
		
		chars = Arrays.copyOfRange(chars, start, chars.length);
		
		
	}
	

	@Override
	public String toString() {
		String str = "";
		int value = 0;
		byte[] bitArray = bits.toByteArray();

		for (int i = 0; i < bitArray.length; i++) {
			value += bitArray[i] * (1 << i);			
		}

		str += value;

		return str;
	}

	@Override
	public int compareTo(BigInteger o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int intValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long longValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float floatValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double doubleValue() {
		// TODO Auto-generated method stub
		return 0;
	}

}
