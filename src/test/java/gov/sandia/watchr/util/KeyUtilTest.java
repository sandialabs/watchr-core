package gov.sandia.watchr.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class KeyUtilTest {
	@Test
	public void testVerifyTextInsertion_ForDoubles() {
		String doubleRegex = KeyUtil.DOUBLE_REGEX;
		
		assertTrue(KeyUtil.verifyTextInsertion(doubleRegex, "", "1", KeyUtil.NULL_CHAR, 0));
		assertTrue(KeyUtil.verifyTextInsertion(doubleRegex, "1", "2", KeyUtil.NULL_CHAR, 0));
		assertTrue(KeyUtil.verifyTextInsertion(doubleRegex, "12", ".", KeyUtil.NULL_CHAR, 0));
		assertTrue(KeyUtil.verifyTextInsertion(doubleRegex, "12.", "4", KeyUtil.NULL_CHAR, 0));
		assertTrue(KeyUtil.verifyTextInsertion(doubleRegex, "12.4", "", KeyUtil.BACKSPACE, 0));
		assertTrue(KeyUtil.verifyTextInsertion(doubleRegex, "12.4", "-", KeyUtil.NULL_CHAR, 0));
		
		assertFalse(KeyUtil.verifyTextInsertion(doubleRegex, "-124E", "E", KeyUtil.NULL_CHAR, 0));
		assertFalse(KeyUtil.verifyTextInsertion(doubleRegex, "-124E", "-", KeyUtil.NULL_CHAR, 0));
		
		assertFalse(KeyUtil.verifyTextInsertion(doubleRegex, "-124E", ".", KeyUtil.NULL_CHAR, 0));
		assertTrue(KeyUtil.verifyTextInsertion(doubleRegex, "-124E", ".", KeyUtil.NULL_CHAR, 1));
		assertFalse(KeyUtil.verifyTextInsertion(doubleRegex, "-124.E", ".", KeyUtil.NULL_CHAR, 0));
		assertFalse(KeyUtil.verifyTextInsertion(doubleRegex, "-124.E.", ".", KeyUtil.NULL_CHAR, 0));
		
		assertTrue(KeyUtil.verifyTextInsertion(doubleRegex, "10", "-", KeyUtil.NULL_CHAR, 0));
		assertFalse(KeyUtil.verifyTextInsertion(doubleRegex, "10", "-", KeyUtil.NULL_CHAR, 1));
		assertTrue(KeyUtil.verifyTextInsertion(doubleRegex, "10E", "-", KeyUtil.NULL_CHAR, 3));
		
		assertFalse(KeyUtil.verifyTextInsertion(doubleRegex, "-10", "-", KeyUtil.NULL_CHAR, 1));

		assertFalse(KeyUtil.verifyTextInsertion(doubleRegex, "10.0", "-", '-', 3));
	}
	
	@Test
	public void testVerifyTextInsertion_GracefulFailureForOutOfBoundsKeyLocation() {
		assertFalse(KeyUtil.verifyTextInsertion(KeyUtil.INT_REGEX, "1", "1", KeyUtil.NULL_CHAR, -1));
		assertFalse(KeyUtil.verifyTextInsertion(KeyUtil.INT_REGEX, "1", "1", KeyUtil.NULL_CHAR, 3));
	}
}

