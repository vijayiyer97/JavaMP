package com.vijayiyer97.util;

import java.io.Serializable;

/**
 * A dynamically mutable bit array wrapper class. Stores {@code n} bits where {@code n} is one greater
 * than the significand length of the bit set. It abstracts methods for bitwise  operations and
 * binary arithmetic.
 * 
 * @author Vijay Iyer
 *
 */
public abstract class BitSet implements Comparable<BitSet>, Cloneable, Serializable {
	
	private static final byte[] binOne = { 1 };
	
	/**
	 * {@code BitSet} representation of zero.
	 */
	public static final BitSet ZERO = new BitSetLE();
	
	/**
	 * {@code BitSet} representation of one.
	 */
	public static final BitSet ONE = new BitSetLE(1, binOne);
	
	// STANDARD EXCEPTIONS
	static UnsupportedOperationException ILLEGAL_ACCESS = new 
			UnsupportedOperationException("attempted access of undefined bit.");
	static UnsupportedOperationException ILLEGAL_OPERATION = new
			UnsupportedOperationException("illegal operation.");
	static NumberFormatException ILLEGAL_VALUE = new 
			NumberFormatException("bit is neither zero nor one.");

	private static final long serialVersionUID = 3561524646151130347L;
	
	/**
	 * A generic bit array. Inherited classes must implement either little-endian or
	 * big-endian context for this array.
	 */
	protected byte[] bits;
	
	// the cached size of the bit array.
	protected int size;
	
	/**
	 * The length of the significand portion of the bit array.
	 */
	protected int len = 0;
	
	/**
	 * The signature of the bit set. Determines whether the form is in two's compliment.
	 */
	protected byte signum = 0;
	
	/**
	 * Flag for complement of state the bit set. Used in determining output for {@code get()}
	 * method.
	 */
	protected byte complement = 0;
	
	/// INITIALIZERS
	
	
	/**
	 * Initializes an mutable unsigned empty bit set, whose length is {@code 0} and bits are null.
	 */
	public BitSet() { 
		bits = new byte[1];
		size = 0;
	}
	
	/**
	 * Initializes a mutable empty bit set, whose length is {@code 0} and bits are null.
	 * 
	 * @param signum {@code BitSet} signature. Must be 1 or -1.
	 */
	public BitSet(int signum) {
		this();
		
		if (signum != 1 && signum != -1) {
			throw new NumberFormatException(signum + " is neither 1 nor -1");
		}
			
		this.signum = (byte) signum;
	}
	
	/**
	 * Initializes an empty bit set, whose length is {@code size} and bits are null.
	 * @param size Capacity of the new {@code BitSet}.
	 */
	public BitSet(int signum, int nbits) {
		this(signum);
		
		size = nbits;
		bits = new byte[size];
	}
	
	/**
	 * Initializes a {@code BitSet} instance from another {@code BitSet} instance.
	 * 
	 * @param other {@code BitSet} instance.
	 */
	BitSet(BitSet other) {
		this.signum = other.signum;
		this.complement = other.complement;
		this.size = other.size;
		this.len = other.len;
		this.bits = other.bits.clone();
	}
	
	/**
	 * Initializes a {@code BitSet} instance from a {@code DecimalSet} instance.
	 * @param decimal {@code DecimalSet} instance.
	 */
	BitSet(DecimalSet decimal) {
		this.signum = (byte) decimal.signum;
		this.complement = 0;
		
		convertToBin(decimal);
	}
	
	/**
	 * Initializes a {@code BitSet} instance from a {@code char} array.
	 * @param decimal {@code char} array storing the decimal representation of the {@code BitSet}.
	 */
	BitSet(char[] decimal) {
		this(new DecimalSet(decimal));
	}
	
	protected abstract void convertToBin(DecimalSet decimal);
	
	@Override
	public abstract String toString();
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
 			return true;
		}
		
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof BitSet)) {
			return false;
		}
		
		BitSet other = (BitSet) obj;		
		if (this.signum != other.signum || this.len != other.len) {
			return false;
		}
		
		if (this.compareSignificands(other) != 0) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Compares the bits of two {@code BitSet} instances from the most to least significant bit.
	 * Implementation required for using {@code compareTo()}.
	 * 
	 * @param o {@code BitSet} instance to compare.
	 * @return A positive integer if this instance is greater, a negative integer if this instance is 
	 * smaller, or zero if both instances are identical.
	 */
	protected abstract int compareSignificands(BitSet o);
	
	@Override
	public int compareTo(BitSet o) {
		if (this.equals(o)) { 
			return 0;
		}

		if (this.complement == o.complement) {
			if (this.signum == o.signum) {
				return this.compareSignificands(o);
			}
			return this.signum - o.signum;
		}

		BitSet other = o.clone();

		other.twosComplement();

		return compareTo(other);
	}

	public int compareMagnitudes(BitSet o) {
		if (this.equals(o)) { 
			return 0;
		}

		if (this.complement == o.complement) {
			return this.compareSignificands(o);
		}

		BitSet other = o.clone();
		
		other.twosComplement();
		
		return compareMagnitudes(other);
	}
	
	@Override
	public abstract BitSet clone();
	
	/**
	 * Converts this {@code BitSet} instance into a {@code BitSetLE} instance.
	 * 
	 * @return A new {@code BitSetLE} instance.
	 */
	public abstract BitSetLE toLittleEndian();
	
	/**
	 * Converts this {@code BitSet} instance into a {@code BitSetBE} instance.
	 * 
	 * @return A new {@code BitSetBE} instance.
	 */
	public abstract BitSetBE toBigEndian();
	
	/// GETTERS
	
	
	/**
	 * Getter method for a single bit in the set. Return the bit if within bounds, or the complement
	 * value if out of bounds. Throws {@code UnsupportedException} if method attempts to access a bit
	 * with an index less 0.
	 * 
	 * @param index Index of bit.
	 * 
	 * @return Bit at the given index.
	 */
	public abstract int get(int index);
	
	/**
	 * Value is 1 if signature is negative, 0 otherwise.
	 * 
	 * @return Signum bit
	 */
	public int sign() {
		return signum == -1 ? 1 : 0;
	}
	
	/**
	 * Getter method for length of significand bits. If the most significant bit is one, then this 
	 * returns one less than the full size of the bit set.
	 * 
	 * @return Length of the bit array
	 */
	public int length() {
		int length = 0;
		
		for (int i = len - 1; i >= 0; i--) {
			if (get(i) == (complement ^ 1)) {
				length = i + 1;
				break;
			}
		}
		
		return length;
	}
	
	
	/**
	 * Bit array getter method.
	 * 
	 * @return Bit array for this BitSet instance.
	 */
	public byte[] toByteArray() {
		return bits.clone();
	}
	
	/**
	 * Checks for an empty bit array.
	 * 
	 * @return {@code true} if bit array is empty, {@code false} otherwise.
	 */
	public boolean isEmpty() {
		return len == 0;
	}
	
	/**
	 * Gets the capacity of the bit array for this BitSet instance
	 * @return The current capacity in bytes.
	 */
	public int capacity() {
		return size;
	}
	
	
	/// BIT MANIPULATION
	
	
	/**
	 * Sets the bits in range [fromIndex, toIndex) to the specified value: either {@code 1} if 
	 * {@code true} or {@code 0} if {@code false}.
	 * 
	 * @param fromIndex Start index.
	 * @param toIndex End index.
	 * @param value Bit value.
	 */
	protected abstract void set(int fromIndex, int toIndex, boolean value);
	
	/**
	 * Sets the bits in range [fromIndex, toIndex) to {@code 1}. Note that the index is based on the
	 * reading convention of the {@code BitSet} implementation
	 * 
	 * @param fromIndex Start index.
	 * @param toIndex End index.
	 */
	public void set(int fromIndex, int toIndex) {
		set(fromIndex, toIndex, true);
	}
	
	/**
	 * Sets the bit at a given index to {@code 1}. Note that the index is based on the
	 * reading convention of the {@code BitSet} implementation
	 * 
	 * @param index Index of bit.
	 */
	public void set(int index) {
		set(index, index + 1, true);
	}
	
	/**
	 * Sets the bits in range [fromIndex, toIndex) to {@code 0}. Note that the index is based on the
	 * reading convention of the {@code BitSet} implementation
	 * 
	 * @param fromIndex Start index.
	 * @param toIndex End index.
	 */
	public void clear(int fromIndex, int toIndex) {
		set(fromIndex, toIndex, false);
	}
	
	/**
	 * Sets the bit at a given index to {@code 0}. Note that the index is based on the
	 * reading convention of the {@code BitSet} implementation
	 * 
	 * @param index Index of bit.
	 */
	public void clear(int index) {
		set(index, index + 1, false);
	}
	
	/**
	 * Clears the entire bit array by reinitializing it with current size. NOTE: other properties are
	 * unmodified.
	 */
	protected void clear() {
		bits = new byte[size];
	}
	
	/**
	 * Resets the {@code BitSet} instance to an empty mutable state of the same signature.
	 */
	public void reset() {
		complement = 0;
		size = 0;
		len = 0;
		clear();
	}
	
	/**
	 * Appends a bit to the most significant bit of the set. Conserves leading zeros.
	 * Throws a {@code NumberFormatException} if bit is neither zero nor one.
	 * 
	 * @param bit Bit to append
	 */
	public abstract void append(int bit);
	
	/**
	 * Prepends a bit to the least significant bit of the set. Conserves leading zeros.
	 * Throws a {@code NumberFormatException} if bit is neither zero nor one.
	 * 
	 * @param bit
	 */
	public abstract void prepend(int bit);
	
	/**
	 * Pops the bit at the last index in the set. Guaranteed to return {@code 1}.
	 * 
	 * @return The removed bit.
	 */
	public abstract int pop();
	
	/**
	 * Pops the bit at the given index in the set.
	 * Throws a {@code UnsupportedOperationException} if index is less than zero.
	 * 
	 * @param index Index of bit to pop.
	 * @return The removed bit.
	 */
	public abstract int pop(int index);
	
	/**
	 * Inverts the bit at the given index.
	 * 
	 * @param index Index of the inverted bit.
	 */
	public void flip(int index) {
		if (index < 0) {
			throw BitSet.ILLEGAL_ACCESS;
		} else if (index < size) {
			bits[index] ^= 1;
			
			if (index > len - 1) {
				len = index;
			} else if (index == len - 1) {
				for (int i = len - 1; i >= 0; i--) {
					if (bits[i] == 1) {
						len = i + 1;
						break;
					}
				}
			}
			
		} else if (complement == 0) {
			set(index);
		} else {
			clear(index);
		}
	}
	
	/**
	 * Inverts the bits in the range [fromIndex, toIndex).
	 * 
	 * @param fromIndex Start index.
	 * @param toIndex End index.
	 */
	public abstract void flip(int fromIndex, int toIndex);	
	/**
	 * Inverts the signature of the {@code BitSet}
	 */
	public void flipSign() {
		if (signum == -1) {
			signum = 1;
		} else if (signum == 1) {
			signum = -1;
		} else {
			throw new UnsupportedOperationException("cannot invert null signature.");
		}
	}
	
	/**
	 * Transforms the {@code BitSet} to its one's complement with bit length {@code nbit + 1}.
	 * 
	 * @param nbit Most significant bit for the operation.
	 */
	public abstract void onesComplement();
	
	/**
	 * Transforms the {@code BitSet} to its two's complement.
	 * 
	 * @param nbit Most significant bit for the operation.
	 */
	public abstract void twosComplement();
	
	/**
	 * Reverses the bits in this bit set.
	 * 
	 * @return Byte array containing the reversed bit set.
	 */
	protected byte[] reverseBits() {
		byte[] result = new byte[len];
		
		for (int i = 0; i < len; i++) {
			result[len - i - 1] = bits[i];
		}
		
		return result;
	}
	
	
	/// BITWISE OPERATIONS
	
	/**
	 * Performs the bitwise inverse ({@code ~}) operation.
	 * 
	 * @return This {@code BitSet} instance.
	 */
	public BitSet inverse() {
		flip(0, len);
		return this;
	}
	
	/**
	 * Performs the left bit shift ({@code <<}) operation.
	 * 
	 * @param index Number of bits to shift by. 
	 * @return This {@code BitSet} instance.
	 */
	public abstract BitSet lshift(int index);

	/**
	 * Performs the right bit shift ({@code >>}) operation.
	 * 
	 * @param index Number of bits to shift by. 
	 * @return This {@code BitSet} instance.
	 */
	public abstract BitSet rshift(int index);
	
	/**
	 * Performs the bitwise AND ({@code &}) operation.
	 * 
	 * @param bitSet {@code BitSet} to mask with.
	 * @return This {@code BitSet} instance.
	 */
	public BitSet and(BitSet bitSet) {
		int nbit = this.len > bitSet.len ? this.len : bitSet.len;
		
		for (int i = 0; i < nbit; i++) {
			byte bit = (byte) (this.get(i) & bitSet.get(i));
			
			if (bit == 1) {
				set(i);
			} else {
				clear(i);
			}
		}
		
		return this;
	}
	
	/**
	 * Performs the bitwise OR ({@code |}) operation.
	 * 
	 * @param bitSet {@code BitSet} to mask with.
	 * @return This {@code BitSet} instance.
	 */
	public BitSet or(BitSet bitSet) {
		int nbit = this.len > bitSet.len ? this.len : bitSet.len;
		
		for (int i = 0; i < nbit; i++) {
			byte bit = (byte) (this.get(i) | bitSet.get(i));
			
			if (bit == 1) {
				set(i);
			} else {
				clear(i);
			}
		}
		
		return this;
	}
	
	/**
	 * Performs the bitwise XOR ({@code ^}) operation.
	 * 
	 * @param bitSet {@code BitSet} to mask with.
	 * @return This {@code BitSet} instance. 
	 */
	public BitSet xor(BitSet bitSet) {
		int nbit = this.len > bitSet.len ? this.len : bitSet.len;
		
		for (int i = 0; i < nbit; i++) {
			byte bit = (byte) (this.get(i) ^ bitSet.get(i));
			
			if (bit == 1) {
				set(i);
			} else {
				clear(i);
			}
		}
		
		return this;
	}
	
	
	/// BINARY ARITHMETIC
	
	
	/**
	 * Adds a given {@code BitSet} instance to this instance.
	 * 
	 * @param bitSet {@code BitSet} instance to add.
	 * @return A new {@code BitSet} instance.
	 */
	public abstract BitSet add(BitSet bitSet);
	
	/**
	 * Subtracts a given {@code BitSet} instance from this instance.
	 * 
	 * @param bitSet {@code BitSet} instance to subtract.
	 * @return A new {@code BitSet} instance.
	 */
	public abstract BitSet subtract(BitSet bitSet);
	
	/**
	 * Multiply a given {@code BitSet} instance to this instance.
	 * 
	 * @param bitSet {@code BitSet} instance to multiply.
	 * @return A new {@code BitSet} instance.
	 */
	public abstract BitSet multiply(BitSet bitSet);

	/**
	 * Divides a given {@code BitSet} instance from this instance.
	 * 
	 * @param bitSet {@code BitSet} instance to divide.
	 * @return A new {@code BitSet} instance.
	 */
	public abstract BitSet divide(BitSet bitSet);
	
}
