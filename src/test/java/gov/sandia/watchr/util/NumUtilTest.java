package gov.sandia.watchr.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NumUtilTest {

	@Test
	public void testNormalize() {
		double normalizedValue = NumUtil.normalize(25, 10, 50);
		assertEquals(0.375, normalizedValue, 1.0e-4);
	}
	
	@Test
	public void testOrdinal() {
		assertEquals("1st", NumUtil.ordinal(1));
		assertEquals("2nd", NumUtil.ordinal(2));
		assertEquals("3rd", NumUtil.ordinal(3));
		assertEquals("4th", NumUtil.ordinal(4));
		assertEquals("5th", NumUtil.ordinal(5));
		assertEquals("6th", NumUtil.ordinal(6));
		assertEquals("7th", NumUtil.ordinal(7));
		assertEquals("8th", NumUtil.ordinal(8));
		assertEquals("9th", NumUtil.ordinal(9));
		assertEquals("10th", NumUtil.ordinal(10));
		assertEquals("11th", NumUtil.ordinal(11));
		assertEquals("12th", NumUtil.ordinal(12));
		assertEquals("13th", NumUtil.ordinal(13));
		assertEquals("14th", NumUtil.ordinal(14));
		assertEquals("21st", NumUtil.ordinal(21));
		assertEquals("22nd", NumUtil.ordinal(22));
		assertEquals("23rd", NumUtil.ordinal(23));
	}
	
	@Test
	public void testIsInteger() {
		assertTrue(NumUtil.isInteger("1"));
		assertTrue(NumUtil.isInteger("-1"));
		assertFalse(NumUtil.isInteger("1.0"));
		assertFalse(NumUtil.isInteger(""));
		assertFalse(NumUtil.isInteger("2147483649")); // too big to be an int
		assertFalse(NumUtil.isInteger("-2147483648")); // too small to be an int
	}
	
	@Test
	public void testIsFormattableAsInteger() {
		assertTrue(NumUtil.isFormattableAsInteger("1"));
		assertTrue(NumUtil.isFormattableAsInteger("2.0"));
		assertTrue(NumUtil.isFormattableAsInteger("3E10"));
		assertFalse(NumUtil.isFormattableAsInteger("1.001E2"));
		assertFalse(NumUtil.isFormattableAsInteger("2.2"));
	}
	
	@Test
	public void testToDoubleOrNan() {
		assertEquals(1.0, NumUtil.toDoubleOrNaN("1.0"), 1.0e-4);
		assertEquals(Double.NaN, NumUtil.toDoubleOrNaN("NaN"), 1.0e-4);
		assertEquals(Double.NaN, NumUtil.toDoubleOrNaN("NAN"), 1.0e-4);
		assertEquals(Double.NaN, NumUtil.toDoubleOrNaN("nan"), 1.0e-4);
	}
	
	@Test
	public void testApplyDecimalPrecision() {
		double originalDouble = 1.2156343443;
		double expectedDouble = 1.216;
		double actualDouble = NumUtil.applyDecimalPrecision(originalDouble, 3);
		assertEquals(expectedDouble, actualDouble, 1e-4);
	}    
}
