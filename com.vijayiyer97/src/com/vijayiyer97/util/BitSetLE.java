package com.vijayiyer97.util;

/**
 * {@code BitSetLE} is a {@code BitSet} extension which adds little-endian context to the bit set. 
 * It also implements bitwise operators and binary arithmetic using little-endian context.
 * 
 * @author vijay
 *
 */
public final class BitSetLE extends BitSet {

	private static final byte[] binTwo = { 0, 1 };

	/**
	 * {@code BitSetLE} representation of two.
	 */
	public static final BitSet TWO = new BitSetLE(1, binTwo);


	// INITIALIZERS

	/**
	 * Allows serialization of this object.
	 */
	private static final long serialVersionUID = -5050264069058579975L;

	/**
	 * Initializes an unsigned mutable empty {@code BitSetLE} instance.
	 */
	BitSetLE() {
		super();
	}

	/**
	 * Initializes a mutable empty {@code BitSetLE} instance, with a given signature.
	 * 
	 * @param signum Signature of instance.
	 */
	public BitSetLE(int signum) {
		super(signum);
	}

	/**
	 * Initializes a mutable empty {@code BitSetLE} instance, with given signature and size.
	 * 
	 * @param signum Signature of instance.
	 * @param nbits Size of instance.
	 */
	public BitSetLE(int signum, int nbits) {
		super(signum, nbits);
	}

	/**
	 * Initializes a {@code BitSetLE} instance from a given signed little-endian bit array. Throws 
	 * {@code NumberFormatException} if any element in the bit array is neither zero nor one.
	 * 
	 * @param bits Bits of instance.
	 */
	public BitSetLE(int signum, byte[] bits) {
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
	 * Initializes a {@code BitSetLE} instance from a little-endian two's complement bit array.
	 * @param bits Bits in two's complement notation.
	 */
	BitSetLE(byte[] val) {
		this.complement = val[val.length - 1];
		byte[] bits = new byte[val.length - 1];
		
		for (int i = val.length - 1; i >= 1; i--) {
			bits[i -1] = val[i];
		}
		
		this.bits = bits;
		this.size = bits.length;
		this.signum = (byte) (this.complement * -2 + 1);
		this.len = 0;
		
		for (int i = this.size - 1; i >= 0; i--) {
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
	 * Initializes a {@code BitSetLE} instance from another {@code BitSetLE} instance.
	 * 
	 * @param other {@code BitSetLE} instance.
	 */
	public BitSetLE(BitSetLE other) {
		super(other);
	}

	/**
	 * Converts a {@code BitSetBE} instance to a {@code BitSetLE} instance.
	 * 
	 * @param other {@code BitSetBE} instance
	 */
	BitSetLE(BitSetBE other) {
		this.signum = other.signum;
		this.complement = other.complement;
		this.size = other.size;
		this.len = other.len;
		this.bits = other.reverseBits();
	}
	
	@Override
	protected void convertToBin(DecimalSet decimal) {
		
	}

	@Override
	public String toString() {
		String str = "";
		if (signum == -1) {
			str = "-";
		} else if (signum == 0 || len == 0) {
			return "0";
		}
		for (int i = len - 1; i >= 0; i--) {
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
		return new BitSetLE(this);
	}

	@Override
	public BitSetLE toLittleEndian() {
		return new BitSetLE(this);
	}

	@Override
	public BitSetBE toBigEndian() {
		BitSetBE bigEndian = new BitSetBE(this);
		return bigEndian;
	}

	@Override
	public int get(int index) {
		if (index < 0) {
			throw BitSet.ILLEGAL_ACCESS;
		} else if (index < len) {
			return bits[index];
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

		if (toIndex > size) {
			byte[] temp = bits.clone();
			bits = new byte[toIndex];

			for (int i = 0; i < size; i++) {
				bits[i] = temp[i];
			}

			size = toIndex;
		}

		if (value) {
			for (int i = fromIndex; i < toIndex; i++) {
				bits[i] = 1;
			}

			if (toIndex > len) {
				len = toIndex;
			}
		} else {
			for (int i = fromIndex; i < toIndex; i++) {
				if (i < len) {
					bits[i] = 0;
				} else {
					bits[i] = complement;
				}
			}			
			if (toIndex == len) {
				for (int i = fromIndex - 1; i >= 0; i--) {
					if (bits[i] == (complement ^ 1)) {
						len = i + 1;
						return;
					}
				}

				size = 1;
				len = 0;
				bits = new byte[size];
				signum = 0;
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
	public void prepend(int bit) {
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
	public int pop() {
		int bit = bits[--len];

		bits[len] = complement;

		return bit;
	}

	@Override
	public int pop(int index) {
		int bit = get(index);
		// TODO: FIX METHOD
		return bit;
	}

	@Override
	public void flip(int fromIndex, int toIndex) {
		if (fromIndex < 0) {
			throw BitSet.ILLEGAL_ACCESS;
		} else if (toIndex < fromIndex) {
			throw new UnsupportedOperationException("illegal operation.");
		} else if (fromIndex >= size) {
			for (int i = toIndex - 1; i >= fromIndex; i--) {
				if (complement == 0) {
					set(i);
				} else {
					clear(i);
				}
			}
		} else if (toIndex <= size) {
			for (int i = fromIndex; i < toIndex; i++) {
				bits[i] ^= 1;
			}

			if (fromIndex > len - 1 | toIndex > len) {
				len = toIndex;
			} else {
				for (int i = len - 1; i >= 0; i--) {
					if (bits[i] == 1) {
						len = i + 1;
						break;
					}
				}
			}
		} else {
			for (int i = fromIndex; i < toIndex; i++) {
				if (i < len) {
					bits[i] ^= 1;
				} else {
					append(complement ^ 1);
				}
			}

		}
	}

	@Override
	public void onesComplement() {		
		for (int i = 0; i < len; i++) {
			bits[i] ^= 1;
		}

		flipSign();
		complement ^= 1;
	}

	@Override
	public void twosComplement() {
		int index = len;
		for (int i = 0; i < len; i++) {
			if (bits[i] == 1) {
				index = i + 1;
				break;
			}
		}

		for (int i = index; i < len; i++) {
			bits[i] ^= 1;
		}

		flipSign();
		complement ^= 1;
	}

	@Override
	public BitSet lshift(int index) {
		if (index > 0) {
			for (int i = this.len + index - 1; i >= index; i--) {
				int bit = this.get(i - index);

				if (bit == 1) {
					this.set(i);
				} else {
					this.clear(i);
				}
			}

			this.clear(0, index);
		} else if (index < 0) {
			throw new UnsupportedOperationException("bit shift does not support negative parameters.");
		}

		return this;
	}

	@Override
	public BitSet rshift(int index) {
		if (index > 0) {
			for (int i = index; i < this.len; i++) {
				int bit = this.get(i);

				if (bit == 1) {
					this.set(i - index);
				} else {
					this.clear(i - index);
				}
			}

			this.clear(len - index, len);
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

		BitSet result = new BitSetLE(1, nbit);

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

			for (int i = result.len - 1; i >=0; i--) {
				if (result.bits[i] == 1) {
					result.len = i + 1;
					break;
				}
			}
		}

		return result;
	}

	@Override
	public BitSet add(BitSet bitSet) {
		BitSet A = this.clone();
		BitSet B = bitSet.toLittleEndian();

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
		BitSet B = bitSet.toLittleEndian();

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
			BitSetLE temp = new BitSetLE(1, A.len);

			byte b = B.bits[i];

			for (int j = 0; j < A.len; j++) {
				byte a = A.bits[j];
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
		BitSet B = bitSet.toLittleEndian();

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
		} else if (A.equals(BitSetLE.TWO)) {
			return B.lshift(1);
		} else if (B.equals(BitSet.ONE)) {
			return A;
		} else if (B.equals(BitSetLE.TWO)) {
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
		BitSet Q = new BitSetLE(1, A.len);
		BitSet R = new BitSetLE(1, A.len + 1);

		for (int i = A.len - 1; i >= 0; i--) {
			R.lshift(1);
			if (A.bits[i] == 1) {
				R.set(0);
			} 

			if (R.compareTo(B) >= 0) {
				R = R.subtract(B);
				Q.set(i);
			}
		}

		if (A.signum != B.signum) {
			Q.signum = -1;
		}

		return Q;
	}

	@Override
	public BitSet divide(BitSet bitSet) {
		BitSet A = this.clone();
		BitSet B = bitSet.toLittleEndian();

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
		} else if (A.compareMagnitudes(B) < 0 || A.equals(BitSet.ZERO)) {
			return BitSet.ZERO;
		} else if (B.equals(BitSet.ZERO)) {
			throw new ArithmeticException("division by zero");
		} else if (B.equals(BitSet.ONE)) {
			return A;
		} else if (B.equals(BitSetLE.TWO)) {
			return A.rshift(1);
		}

		return division(A, B);
	}
}
