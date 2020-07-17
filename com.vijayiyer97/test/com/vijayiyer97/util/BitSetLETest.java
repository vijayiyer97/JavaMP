package com.vijayiyer97.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.vijayiyer97.util.BitSet;
import com.vijayiyer97.util.BitSetLE;


class BitSetLETest {

	static final int size = 31;

	static BitSetLE a = new BitSetLE(1, size);
	static BitSetLE b = new BitSetLE(1, size);

	Random random = new Random();
	
	void randomize(BitSet bitSet, int lim) {
		bitSet.reset();

		for (int i = 0; i < lim; i++) {
			bitSet.append(random.nextInt(2));
		}
	}

	@BeforeEach
	void setUp() throws Exception {
		randomize(a, size);
		randomize(b, size);
	}

	@Test
	void testToString() {
		int num = 0;
		for (int i = 0; i < size; i++) {
			num += a.get(i) * (1 << i);
		}

		if (a.sign() == 1) {
			num *= -1;
		}

		int test = Integer.parseInt(a.toString(), 2);

		assertEquals(num, test);
	}

	@Test
	void testClone() {
		BitSet test = a.clone();

		assertTrue(a != test && a.equals(test));
	}

	@Test
	void testToLittleEndian() {
		BitSet test = a.toLittleEndian();

		assertTrue(a != test && a.equals(test));
	}

	@RepeatedTest(10000)
	void testGet() {
		int i = -random.nextInt(size) - 1;
		int j = random.nextInt(a.len);
		int k = a.len + j;

		Exception test1 = assertThrows(UnsupportedOperationException.class, () -> a.get(i));
		int test2 = a.get(j);
		int test3 = a.get(k);

		assertAll(
				() -> assertAll("out of bounds test",
						() -> assertTrue(i < 0, "index less zero"),
						() -> assertEquals(BitSet.ILLEGAL_ACCESS, test1, "exception test")
						),
				() -> assertEquals(a.bits[j], test2, "in bounds test"),
				() -> assertEquals(a.complement, test3, "bounds extension test")
				);
	}

	@RepeatedTest(10000)
	void testSet() {
		int i = random.nextInt(size);
		int j = random.nextInt(size - i) + i + 1;
		boolean value = random.nextBoolean();

		int num = Integer.parseInt(a.toString(), 2);

		for (int k = i; k < j; k++) {
			if (value) {
				num |= 1 << k;
			} else {
				num &= ~(1 << k);
			}
		}

		a.set(i, j, value);

		Exception exception1 = assertThrows(UnsupportedOperationException.class,
				() -> a.set(-i - 1, j, value));
		Exception exception2 = assertThrows(UnsupportedOperationException.class, 
				() -> a.set(j, i, value));
		final int expected = num;

		assertAll(
				() -> assertEquals(exception1, BitSet.ILLEGAL_ACCESS),
				() -> assertEquals(exception2, BitSet.ILLEGAL_OPERATION),
				() -> assertEquals(expected, Integer.parseInt(a.toString(), 2))
				);
	}

	@RepeatedTest(10000)
	void testAppend() {
		int bit = random.nextInt(2);

		String expected = bit + a.toString();
		a.append(bit);

		assertEquals(expected, a.toString());
	}

	@RepeatedTest(10000)
	void testPrepend() {
		int bit = random.nextInt(2);

		String expected = a.toString() + bit;
		a.prepend(bit);

		assertEquals(expected, a.toString());
	}

	@RepeatedTest(10000)
	void testPop() {
		int expectedBit = a.bits[a.len - 1];

		String expectedStr = a.toString().substring(1);

		assertAll(
				() -> assertEquals(expectedBit, a.pop(), "unexpected pop value"),
				() -> assertEquals(expectedStr, a.toString(), "unexpected bit set")
				);
	}

	@Disabled("Until method is properly implemented")
	@RepeatedTest(10000)
	void testPopInt() {
		// TODO: implement test method
	}

	@RepeatedTest(10000)
	void testAdd() {
		randomize(a, 29);
		randomize(b, 29);

		int A = Integer.parseInt(a.toString(), 2);
		int B = Integer.parseInt(b.toString(), 2);

		int expected1 = A + B;
		int expected2 = A + 0;
		int expected3 = A + 1;
		int expected4 = A + A;


		BitSet c = a.add(b);
		BitSet d = a.add(BitSetLE.ZERO);
		BitSet e = a.add(BitSetLE.ONE);
		BitSet f = a.add(a);

		BitSet clone = a.clone();
		clone.twosComplement();

		BitSet g = clone.add(b);
		BitSet h = clone.add(BitSetLE.ZERO);
		BitSet i = clone.add(BitSetLE.ONE);
		BitSet j = clone.add(a);


		assertAll(
				() -> assertEquals(expected1, Integer.parseInt(c.toString(), 2), "expected1"),
				() -> assertEquals(expected2, Integer.parseInt(d.toString(), 2), "expected2"),
				() -> assertEquals(expected3, Integer.parseInt(e.toString(), 2), "expected3"),
				() -> assertEquals(expected4, Integer.parseInt(f.toString(), 2), "expected4"),
				() -> assertEquals(expected1, Integer.parseInt(g.toString(), 2), "expected5"),
				() -> assertEquals(expected2, Integer.parseInt(h.toString(), 2), "expected6"),
				() -> assertEquals(expected3, Integer.parseInt(i.toString(), 2), "expected7"),
				() -> assertEquals(expected4, Integer.parseInt(j.toString(), 2), "expected8")
				);

	}

	@RepeatedTest(10000)
	void testSubtract() {
		randomize(a, 30);
		randomize(b, 30);

		int A = Integer.parseInt(a.toString(), 2);
		int B = Integer.parseInt(b.toString(), 2);

		int expected1 = A - B;
		int expected2 = A - 0;
		int expected3 = A - 1;
		int expected4 = 0 - B;
		int expected5 = 1 - B;
		int expected6 = A - A;
		int expected7 = B - A;

		BitSet c = a.subtract(b);
		BitSet d = a.subtract(BitSetLE.ZERO);
		BitSet e = a.subtract(BitSetLE.ONE);
		BitSet f = BitSetLE.ZERO.subtract(b);
		BitSet g = BitSetLE.ONE.subtract(b);
		BitSet h = a.subtract(a);
		BitSet i = b.subtract(a);

		BitSet clone = a.clone();
		clone.twosComplement();

		BitSet j = clone.subtract(b);
		BitSet k = clone.subtract(BitSetLE.ZERO);
		BitSet l = clone.subtract(BitSetLE.ONE);
		BitSet m = BitSetLE.ZERO.subtract(b);
		BitSet n = BitSetLE.ONE.subtract(b);
		BitSet o = clone.subtract(a);
		BitSet p = b.subtract(clone);

		assertAll(
				() -> assertEquals(expected1, Integer.parseInt(c.toString(), 2), "expected1"),
				() -> assertEquals(expected2, Integer.parseInt(d.toString(), 2), "expected2"),
				() -> assertEquals(expected3, Integer.parseInt(e.toString(), 2), "expected3"),
				() -> assertEquals(expected4, Integer.parseInt(f.toString(), 2), "expected4"),
				() -> assertEquals(expected5, Integer.parseInt(g.toString(), 2), "expected5"),
				() -> assertEquals(expected6, Integer.parseInt(h.toString(), 2), "expected6"),
				() -> assertEquals(expected7, Integer.parseInt(i.toString(), 2), "expected7"),
				() -> assertEquals(expected1, Integer.parseInt(j.toString(), 2), "expected8"),
				() -> assertEquals(expected2, Integer.parseInt(k.toString(), 2), "expected9"),
				() -> assertEquals(expected3, Integer.parseInt(l.toString(), 2), "expected10"),
				() -> assertEquals(expected4, Integer.parseInt(m.toString(), 2), "expected11"),
				() -> assertEquals(expected5, Integer.parseInt(n.toString(), 2), "expected12"),
				() -> assertEquals(expected6, Integer.parseInt(o.toString(), 2), "expected13"),
				() -> assertEquals(expected7, Integer.parseInt(p.toString(), 2), "expected14")
				);

	}

	@RepeatedTest(10000)
	void testMutliply() {
		randomize(a, 15);
		randomize(b, 15);

		int A = Integer.parseInt(a.toString(), 2);
		int B = Integer.parseInt(b.toString(), 2);

		int expected1 = A * B;
		int expected2 = A * 0;
		int expected3 = A * 1;
		int expected4 = A * A;

		BitSet c = a.multiply(b);
		BitSet d = a.multiply(BitSetLE.ZERO);
		BitSet e = a.multiply(BitSetLE.ONE);
		BitSet f = a.multiply(a);

		BitSet clone = a.clone();
		clone.twosComplement();

		BitSet g = clone.multiply(b);
		BitSet h = clone.multiply(BitSetLE.ZERO);
		BitSet i = clone.multiply(BitSetLE.ONE);
		BitSet j = clone.multiply(a);

		assertAll(
				() -> assertEquals(expected1, Integer.parseInt(c.toString(), 2), "expected1"),
				() -> assertEquals(expected2, Integer.parseInt(d.toString(), 2), "expected2"),
				() -> assertEquals(expected3, Integer.parseInt(e.toString(), 2), "expected3"),
				() -> assertEquals(expected4, Integer.parseInt(f.toString(), 2), "expected4"),
				() -> assertEquals(expected1, Integer.parseInt(g.toString(), 2), "expected5"),
				() -> assertEquals(expected2, Integer.parseInt(h.toString(), 2), "expected6"),
				() -> assertEquals(expected3, Integer.parseInt(i.toString(), 2), "expected7"),
				() -> assertEquals(expected4, Integer.parseInt(j.toString(), 2), "expected8")
				);

	}

	@RepeatedTest(10000)
	void testDivide() {
		randomize(a, 30);
		randomize(b, 30);

		int A = Integer.parseInt(a.toString(), 2);
		int B = Integer.parseInt(b.toString(), 2);

		int expected1 = A / B;
		String expected2 = new ArithmeticException("division by zero").toString();
		int expected3 = A / 1;
		int expected4 = 0;
		int expected5 = 1 / B;
		String expected6 = new ArithmeticException("indeterminate operation").toString();
		int expected7 = B / A;
		int expected8 = 1;

		BitSet c = a.divide(b);
		Exception d = assertThrows(ArithmeticException.class,
				() -> a.divide(BitSetLE.ZERO));
		BitSet e = a.divide(BitSetLE.ONE);
		BitSet f = BitSetLE.ZERO.divide(b);
		BitSet g = BitSetLE.ONE.divide(b);
		Exception h = assertThrows(ArithmeticException.class,
				() -> BitSetLE.ZERO.divide(BitSetLE.ZERO));
		BitSet i = b.divide(a);

		BitSet cloneA = a.clone();
		BitSet cloneB = b.clone();
		cloneA.twosComplement();
		cloneB.twosComplement();

		BitSet j = cloneA.divide(cloneB);
		Exception k = assertThrows(ArithmeticException.class,
				() -> cloneA.divide(BitSetLE.ZERO));
		BitSet l = a.divide(BitSetLE.ONE);
		BitSet m = BitSetLE.ZERO.divide(cloneB);
		BitSet n = BitSetLE.ONE.divide(cloneB);
		Exception o = assertThrows(ArithmeticException.class,
				() -> BitSetLE.ZERO.divide(BitSetLE.ZERO));
		BitSet p = cloneB.divide(cloneA);

		BitSet q = a.divide(a);

		BitSet r = cloneA.divide(cloneA);
		BitSet s = cloneA.divide(b);
		BitSet t = b.divide(cloneA);


		assertAll(
				() -> assertEquals(expected1, Integer.parseInt(c.toString(), 2), "expected1"),
				() -> assertEquals(expected2, d.toString(), "expected2"),
				() -> assertEquals(expected3, Integer.parseInt(e.toString(), 2), "expected3"),
				() -> assertEquals(expected4, Integer.parseInt(f.toString(), 2), "expected4"),
				() -> assertEquals(expected5, Integer.parseInt(g.toString(), 2), "expected5"),
				() -> assertEquals(expected6, h.toString(), "expected6"),
				() -> assertEquals(expected7, Integer.parseInt(i.toString(), 2), "expected7"),
				() -> assertEquals(expected1, Integer.parseInt(j.toString(), 2), "expected8"),
				() -> assertEquals(expected2, k.toString(), "expected9"),
				() -> assertEquals(expected3, Integer.parseInt(l.toString(), 2), "expected10"),
				() -> assertEquals(expected4, Integer.parseInt(m.toString(), 2), "expected11"),
				() -> assertEquals(expected5, Integer.parseInt(n.toString(), 2), "expected12"),
				() -> assertEquals(expected6, o.toString(), "expected13"),
				() -> assertEquals(expected7, Integer.parseInt(p.toString(), 2), "expected14"),
				() -> assertEquals(expected8, Integer.parseInt(q.toString(), 2), "expected15"),
				() -> assertEquals(expected8, Integer.parseInt(r.toString(), 2), "expected16"),
				() -> assertEquals(expected1, Integer.parseInt(s.toString(), 2), "expected17"),
				() -> assertEquals(expected7, Integer.parseInt(t.toString(), 2), "expected18")

				);

	}

}
