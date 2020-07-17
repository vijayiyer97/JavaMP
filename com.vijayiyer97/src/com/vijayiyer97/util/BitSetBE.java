package com.vijayiyer97.util;

/**
 * {@code BitSetBE} is a {@code BitSet} extension which adds big-endian context to the bit set. 
 * It also implements bitwise operators and binary arithmetic using big-endian context. One useful
 * feature of {@code BitSetBE} is that the left and right bit shift operations ({@code <<} and 
 * {@code >>}) are perfect inverses of each other, since the bits are conserved.
 * 
 * @author Vijay Iyer
 *
 */
public final class BitSetBE extends BitSet {

	private static final byte[] binTwo = { 1, 0 };

	/**
	 * {@code BitSetBE} representation of two.
	 */
	public static final BitSet TWO = new BitSetBE(1, binTwo);

	/**
	 * Allows serialization of this object.
	 */
	private static final long serialVersionUID = -6784819151477331819L;

	/**
	 * Initializes an immutable empty {@code BitSet} instance.
	 */
	BitSetBE() {
		super();
	}

	/**
	 * Initializes a mutable empty {@code BitSet} instance, with a given signature.
	 * 
	 * @param signum Signature of instance.
	 */
	public BitSetBE(int signum) {
		super(signum);
	}

	/**
	 * Initializes a mutable empty {@code BitSet} instance, with given signature and size.
	 * 
	 * @param signum Signature of instance.
	 * @param nbits Size of instance.
	 */
	public BitSetBE(int signum, int nbits) {
		super(signum, nbits);
	}

	/**
	 * Initializes a bit set from a given signed big-endian bit array. Throws 
	 * {@code NumberFormatException} if any element in the bit array is neither zero nor one.
	 * 
	 * @param bits Bits of instance.
	 */
	public BitSetBE(int signum, byte[] bits) {
		if (signum != 1 && signum != -1) {
			throw new NumberFormatException(signum + " is neither 1 nor -1.");
		}

		for (byte bit: bits) {
			if (bit != 1 && bit != 0) {
				throw BitSet.ILLEGAL_VALUE;
			}
		}

		this.bits = new byte[bits.length];
		size = bits.length;
		this.signum = (byte) signum;

		for (int i = 0; i < bits.length; i++) {
			this.bits[i] = bits[i];

			if (bits[i] == 1) {
				len =  i + 1;
			}
		}
	}

	/**
	 * Initializes a {@code BitSetBE} instance from a big-endian two's complement bit array.
	 * @param bits Bits in two's complement notation.
	 */
	public BitSetBE(byte[] val) {
		this.complement = val[0];
		byte[] bits = new byte[val.length - 1];

		for (int i = 1; i < val.length; i++) {
			bits[i -1] = val[i];
		}

		this.bits = bits;
		this.size = bits.length;
		this.signum = (byte) (this.complement * -2 + 1);
		this.len = 0;

		for (int i = 0; i < this.size; i++) {
			if (bits[i] == (complement ^ 1)) {
				this.len = i + 1;
				break;
			}
		}

		if (this.len == 0) {
			this.signum = 0;
		}
	}

	/**
	 * Initializes a {@code BitSetBE} instance from another {@code BitSetBE} instance.
	 * 
	 * @param other {@code BitSetBE} instance.
	 */
	BitSetBE(BitSetBE other) {
		super(other);
	}

	/**
	 * Converts a {@code BitSetLE} instance to a {@code BitSetBE} instance.
	 * 
	 * @param other {@code BitSetLE} instance
	 */
	BitSetBE(BitSetLE other) {
		this.signum = other.signum;
		this.complement = other.complement;
		this.size = other.size;
		this.len = other.len;
		this.bits = other.reverseBits();
	}

	@Override
	protected void convertToBin(DecimalSet decimal) {
		this.size = (int) (decimal.digits.length/Math.log10(2)) + 1;
		this.bits = new byte[this.size];

		char[] digits = decimal.digits.clone();
		
		int qSize = (int) (log10(digits) - Math.log10(2)) + 1;
		char[] quotient = new char[qSize];
		
		for (int i = 0; i < digits.length; i++) {
			int bit = (digits[digits.length - 1] - 48) % 2;
			bits[this.size - 1] = (byte) bit;
			
			int D = digits[0] - 48;
		}

	}
	
	/**
	 * Approximates the base ten logarithm of a number {@code X} of arbitrary length using the formula
	 * {@code log10(X) = log10(x) + N - 1}, where {@code x} is the significand {@code X} in scientific notation and
	 * {@code N} is the number of digits in {@code X}.
	 * @param X {@code String} representation of the integer argument for the base ten logarithm.
	 * @return The result of the approximation.
	 */
	private double log10(char[] X) {
		String x = X[0] + ".";

		for (int i = 1; i < X.length; i++) {
			x += X[i];
		}

		double A = Double.parseDouble(x);

		double logA = Math.log10(A) + X.length - 1;

		return logA;
	}

	@Override
	public String toString() { 
		String str = "";
		if (signum == -1) {
			str = "-";
		} else if (signum == 0 || len == 0) {
			return "0";
		}
		for (int i = 0; i < len; i++) {
			str += Byte.toString(bits[i]);
		}
		return str;
	}

	@Override
	protected int compareSignificands(BitSet o) {
		BitSet other = o.toLittleEndian();

		int aLen = this.length();
		int bLen = other.length();

		int nbit = aLen > bLen ? aLen : bLen;
		int result = 0;

		for (int i = nbit - 1; i >= 0; i--) {
			int dif = this.get(i) - other.get(i);

			if (dif != 0) {
				if (complement == 1) {
					dif *= -1;
				}
				result = dif * (i + 1);
				break;
			}
		}

		return result;
	}

	@Override
	public BitSet clone() {
		return new BitSetBE(this);
	}

	@Override
	public BitSetLE toLittleEndian() {
		BitSetLE littleEndian = new BitSetLE(this);
		return littleEndian;
	}

	@Override
	public BitSetBE toBigEndian() {
		return new BitSetBE(this);
	}

	@Override
	public int get(int index) {
		if (index < 0) {
			throw BitSet.ILLEGAL_ACCESS;
		} else if (index < len) {
			return bits[len - index - 1];
		}

		return complement;
	}

	@Override
	protected void set(int fromIndex, int toIndex, boolean value) {
		if (signum == 0) {
			if (complement == 0) {
				signum = 1;
			} else {
				signum = -1;
			}
		} if (fromIndex < 0) {
			throw BitSet.ILLEGAL_ACCESS;
		} else if (toIndex < fromIndex) {
			throw BitSet.ILLEGAL_OPERATION;
		}

		if (value) {
			if (toIndex > size) {
				byte[] temp = bits.clone();
				bits = new byte[toIndex];

				for (int i = size - 1; i >= 0; i--) {
					bits[toIndex + i - size] = temp[i];
				}

				size = toIndex;
				len = size;
			} else if (toIndex > len) {
				byte[] temp = bits.clone();

				for (int i = len - 1; i >= 0; i--) {
					bits[toIndex + i - len] = temp[i];
				}

				len = toIndex;
			}

			for (int i = len - fromIndex - 1 ; i >= len - toIndex; i--) {
				bits[i] = 1;
			}
		} else {
			if(fromIndex < len && toIndex >= len) {
				for (int i = len - fromIndex; i < len; i++) {
					bits[i - len + fromIndex] = bits[i];
				}

				len = fromIndex;
			} else if (fromIndex < len) {
				for (int i = len - fromIndex - 1; i >= len - toIndex; i--) {
					bits[i] = 0;
				}
			}
		}
	}

	@Override
	public void append(int bit) {
		if (signum == 0) {
			if (complement == 0) {
				signum = 1;
			} else {
				signum = -1;
			}
		}

		if (bit == 1 || bit == 0) {
			byte[] temp = bits.clone();

			if (len == size) {
				bits =  new byte[++size];
			}

			for (int i = ++len - 1; i > 0; i--) {
				bits[i] = temp[i - 1];
			}

			bits[0] = (byte) bit;

		} else {
			throw BitSet.ILLEGAL_VALUE;
		}
	}

	@Override
	public void prepend(int bit) {
		if (signum == 0) {
			if (complement == 0) {
				signum = 1;
			} else {
				signum = -1;
			}		}

		if (bit == 1 || bit == 0) {
			if (len == size) {
				byte[] temp = bits.clone();
				bits = new byte[++size];

				for (int i = 0; i < len; i++) {
					bits[i] = temp[i];
				}
			}

			bits[len++] = (byte) bit;
		} else {
			throw BitSet.ILLEGAL_VALUE;
		}
	}

	@Override
	public int pop() {
		int bit = bits[0];
		len--;

		for (int i = 0; i < len; i++) {
			bits[i] = bits[i + 1];
		}

		bits[len] = 0;

		return bit;
	}

	@Override
	public int pop(int index) {
		return 0;
	}

	@Override
	public void flip(int fromIndex, int toIndex) {
		if (fromIndex < 0) {
			throw BitSet.ILLEGAL_ACCESS;
		} else if (toIndex < fromIndex) {
			throw BitSet.ILLEGAL_OPERATION;
		} 

		if (fromIndex >= len) {
			for (int i = fromIndex; i < toIndex; i++) {
				if (complement == 0) {
					set(i);
				} else {
					clear(i);
				}
			}
		} else if (toIndex <= len) {
			for (int i = len - toIndex; i < len - fromIndex; i++) {
				bits[i] ^= 1;
			}
		} else {
			for (int i = fromIndex; i < toIndex; i++) {
				byte bit = (byte) (get(i) ^ 1);

				if (bit == 1) {
					set(i);
				} else {
					clear(i);
				}
			}

		}
	}

	@Override
	public void onesComplement() {		
		for (int i = len - 1; i >= 0; i--) {
			bits[i] ^= 1;
		}

		flipSign();
		complement ^= 1;
	}

	@Override
	public void twosComplement() {
		int index = 0;
		for (int i = len - 1; i >= 0; i--) {
			if (bits[i] == 1) {
				index = i;
				break;
			}
		}

		for (int i = 0; i < index; i++) {
			bits[i] ^= 1;
		}

		flipSign();
		complement ^= 1;
	}

	@Override
	public BitSet lshift(int index) {
		if (index > 0) {
			if (len + index > size) {				
				byte[] temp = bits.clone();
				bits = new byte[len + index];

				for (int i = 0; i < size; i++) {
					bits[i] = temp[i];
				}

				size = len + index;
			}

			len += index;
		} else if (index < 0) {
			throw new UnsupportedOperationException("bit shift does not support negative parameters.");
		}

		return this;
	}

	@Override
	public BitSet rshift(int index) {
		if (index > 0) {
			if (len > index) {
				len -= index;
			} else {
				len = 0;
			}
		} else if (index < 0) {
			throw new UnsupportedOperationException("bit shift does not support negative parameters.");
		}

		return this;
	}

	/**
	 * Adds two bit sets.
	 * 
	 * @param A {@code BitSet} instance.
	 * @param B {@code BitSet} instance.
	 * @return A new {@code BitSet} instance.
	 */
	private BitSet addition(BitSet A, BitSet B) {
		int nbit = A.len > B.len ? A.len : B.len; // to allow proper addition
		nbit++;

		BitSet result = new BitSetBE(1, nbit);

		boolean recomplement = false;

		if (A.signum == B.signum) {
			result.signum = A.signum;
		} else {
			if (A.signum == -1) {
				if (A.compareMagnitudes(B) > 0) {
					recomplement = true;
				}

				A.twosComplement();
			} else {
				if (B.compareMagnitudes(A) > 0) {
					recomplement = true;
				}

				B.twosComplement();
			}
		}

		int carry = 0;

		for (int i = 0; i <= nbit; i++) {
			int a = A.get(i);
			int b = B.get(i);
			int c = carry + a + b;
			int sum = c & 1;
			carry = c >> 1;

		result.append(sum);
		}

		result.pop();

		if (recomplement) {
			result.complement = 1;
			result.twosComplement();
		}

		return result;
	}

	@Override
	public BitSet add(BitSet bitSet) {
		BitSet A = this.clone();
		BitSet B = bitSet.toBigEndian();

		if (A.complement == 1) {
			A.twosComplement();
		} if (B.complement == 1) {
			B.twosComplement();
		}

		if (A.equals(B)) {
			return A.lshift(1);
		} else if (A.equals(BitSet.ZERO)) {
			return B;
		} else if (B.equals(BitSet.ZERO)) {
			return A;
		} else if (A.signum == B.signum) { 
			if (A.equals(BitSet.ONE)) {
				for (int i = 0; i <= B.len; i++) {
					if (B.get(i) == 0) {
						B.flip(0, i);
						break;
					}
				}

				return B;
			} else if (B.equals(BitSet.ONE)) {
				for (int i = 0; i <= A.len; i++) {
					if (A.get(i) == 0) {
						A.flip(0, i + 1);
						break;
					}
				}

				return A;
			}
		}

		return addition(A, B);
	}

	@Override
	public BitSet subtract(BitSet bitSet) {
		BitSet A = this.clone();
		BitSet B = bitSet.toBigEndian();

		if (A.complement == 1) {
			A.twosComplement();
		} if (B.complement == 1) {
			B.twosComplement();
		}

		if (A.equals(B)) {
			return BitSet.ZERO;
		} else if (A.equals(BitSet.ZERO)) {
			B.flipSign();
			return B;
		} else if (B.equals(BitSet.ZERO)) {
			return A;
		} else if (B.equals(BitSet.ONE)) {
			for (int i = 0; i <= A.len; i++) {
				if (A.get(i) == 1) {
					A.flip(0, i + 1);
					break;
				}
			}

			return A;
		}


		B.flipSign();
		return addition(A, B);
	}

	/**
	 * Multiplies two bit sets.
	 * 
	 * @param A {@code BitSet} instance.
	 * @param B {@code BitSet} instance.
	 * @return A new {@code BitSet} instance.
	 */
	private BitSet multiplication(BitSet A, BitSet B) {
		BitSet result = BitSet.ZERO;

		for (int i = 0; i < B.len; i++) {
			BitSet temp = new BitSetBE(1, A.len);

			int b = B.bits[B.len - i - 1];

			for (int j = 0; j < A.len; j++) {
				int a = A.bits[A.len - j - 1];
				int bit = a * b;

				temp.append(bit);
			}

			result = temp.lshift(i).add(result);
		}

		if (A.signum != B.signum) {
			result.flipSign();
		}

		return result;
	}

	@Override
	public BitSet multiply(BitSet bitSet) {
		BitSet A = this.clone();
		BitSet B = bitSet.toBigEndian();

		if (A.complement == 1) {
			A.twosComplement();
		} if (B.complement == 1) {
			B.twosComplement();
		}

		if (A.equals(B)) {
			return multiplication(A, B);
		} else if (A.equals(BitSet.ZERO) || B.equals(BitSet.ZERO)) {
			return BitSet.ZERO;
		} else if (A.equals(BitSet.ONE)) {
			return B;
		} else if (A.equals(BitSetBE.TWO)) {
			return B.lshift(1);
		} else if (B.equals(BitSet.ONE)) {
			return A;
		} else if (B.equals(BitSetBE.TWO)) {
			return A.lshift(1);
		}

		return multiplication(A, B);
	}

	/**
	 * Divides two bit sets.
	 * 
	 * @param A {@code BitSet} instance.
	 * @param B {@code BitSet} instance.
	 * @return A new {@code BitSet} instance.
	 */
	private BitSet division(BitSet A, BitSet B) {
		BitSet Q = new BitSetBE(1, A.len);
		BitSet R = new BitSetBE(1, A.len + 1);

		for (int i = 0; i < A.len; i++) {
			R.prepend(A.bits[i]);

			if (R.compareTo(B) >= 0) {
				R = R.subtract(B);
				Q.prepend(1);
			} else {
				Q.prepend(0);
			}
		}

		if (A.signum != B.signum) {
			Q.flipSign();
		}

		return Q;
	}

	@Override
	public BitSet divide(BitSet bitSet) {
		BitSet A = this.clone();
		BitSet B = bitSet.toBigEndian();

		if (A.complement == 1) {
			A.twosComplement();
		} if (B.complement == 1) {
			B.twosComplement();
		}

		if (A.equals(B)) {
			if (A.equals(BitSet.ZERO)) {
				throw new ArithmeticException("indeterminate operation");
			}
			return BitSet.ONE;
		} else if (A.compareTo(B) < 0 || A.equals(BitSet.ZERO)) {
			return BitSet.ZERO;
		} else if (B.equals(BitSet.ZERO)) {
			throw new ArithmeticException("division by zero");
		} else if (B.equals(BitSet.ONE)) {
			return A;
		} else if (B.equals(BitSetBE.TWO)) {
			return A.rshift(1);
		}

		return division(A, B);
	}
}
