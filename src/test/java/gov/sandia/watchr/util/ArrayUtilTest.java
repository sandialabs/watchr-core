package gov.sandia.watchr.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;

public class ArrayUtilTest {

	////////////
	// FIELDS //
	////////////
	
	private Random randomSeed1;
	private Random randomSeed2;
	
	////////////
	// BEFORE //
	////////////
	
	@Before
	public void setup() {
		// We use fixed random seeds to ensure consistent behavior on repeated unit test execution.
		randomSeed1 = new Random(1337);
		randomSeed2 = new Random(481516);
	}
	
	///////////
	// TESTS //
	///////////
	
	@Test
	public void testAsDoubleArr() {
		String[] originalArr = new String[] {"1.0", "2.2", "3.3"};
		double[] newArr = ArrayUtil.asDoubleArr(originalArr);
		assertEquals(1.0, newArr[0], 1.0e-4);
		assertEquals(2.2, newArr[1], 1.0e-4);
		assertEquals(3.3, newArr[2], 1.0e-4);
	}
	
	@Test
	public void testAsDoubleObjArr() {
		String[] originalArr = new String[] {"1.0", "2.2", "3.3"};
		Double[] newArr = ArrayUtil.asDoubleObjArr(originalArr);
		assertEquals(1.0, newArr[0], 1.0e-4);
		assertEquals(2.2, newArr[1], 1.0e-4);
		assertEquals(3.3, newArr[2], 1.0e-4);
	}
	
	@Test
    public void testAsDoubleObjArrFromDoubleArr() {
        double[] originalArr = new double[] {1.0, 2.2, 3.3};
        Double[] newArr = ArrayUtil.asDoubleObjArrFromDoubleArr(originalArr);
        assertEquals(1.0, newArr[0], 1.0e-4);
        assertEquals(2.2, newArr[1], 1.0e-4);
        assertEquals(3.3, newArr[2], 1.0e-4);
    }
	
	@Test
    public void testAsDoubleArrFromDoubleObjArr() {
        Double[] originalArr = new Double[] {1.0, 2.2, 3.3};
        double[] newArr = ArrayUtil.asDoubleArrFromDoubleObjArr(originalArr);
        assertEquals(1.0, newArr[0], 1.0e-4);
        assertEquals(2.2, newArr[1], 1.0e-4);
        assertEquals(3.3, newArr[2], 1.0e-4);
    }
	
	@Test
	public void testAsDoubleArr_WithBadData() {
		String[] originalArr = new String[] {"1.0", "2.2", "A"};
		double[] newArr = ArrayUtil.asDoubleArr(originalArr);
		assertNull(newArr);
	}
	
	@Test
	public void testAsDoubleArr_WithNull() {
		double[] newArr = ArrayUtil.asDoubleArr(null);
		assertNull(newArr);
	}
	
	@Test
	public void testAsDoubleArrFromStringList() {
		List<String> originalList = new ArrayList<>();
		originalList.add("1.0");
		originalList.add("2.2");
		originalList.add("3.3");
		
		double[] newArr = ArrayUtil.asDoubleArrFromStringList(originalList);
		assertEquals(1.0, newArr[0], 1.0e-4);
		assertEquals(2.2, newArr[1], 1.0e-4);
		assertEquals(3.3, newArr[2], 1.0e-4);
	}
	
	@Test
	public void testAsDoubleObjArrFromStringList() {
		List<String> originalList = new ArrayList<>();
		originalList.add("1.0");
		originalList.add("2.2");
		originalList.add("3.3");
		
		Double[] newArr = ArrayUtil.asDoubleObjArrFromStringList(originalList);
		assertEquals(1.0, newArr[0], 1.0e-4);
		assertEquals(2.2, newArr[1], 1.0e-4);
		assertEquals(3.3, newArr[2], 1.0e-4);
	}
	
	@Test
	public void testAsDoubleArrFromDoubleList() {
		List<Double> originalList = new ArrayList<>();
		originalList.add(1.0);
		originalList.add(2.2);
		originalList.add(3.3);
		
		double[] newArr = ArrayUtil.asDoubleArrFromDoubleList(originalList);
		assertEquals(1.0, newArr[0], 1.0e-4);
		assertEquals(2.2, newArr[1], 1.0e-4);
		assertEquals(3.3, newArr[2], 1.0e-4);
	}
	
	@Test
	public void testAsDoubleArrFromDoubleList_WithNull() {
		double[] newArr = ArrayUtil.asDoubleArrFromDoubleList(null);
		assertNull(newArr);
	}
	
	@Test
	public void testAsDoubleListFromDoubleArr() {
		double[] originalArr = new double[] {1.0, 2.2, 3.3};
		List<Double> newList = ArrayUtil.asDoubleListFromDoubleArr(originalArr);
		assertEquals(1.0, newList.get(0), 1.0e-4);
		assertEquals(2.2, newList.get(1), 1.0e-4);
		assertEquals(3.3, newList.get(2), 1.0e-4);
	}
	
	@Test
	public void testAsDoubleListFromDoubleArr_WithNull() {
		List<Double> newList = ArrayUtil.asDoubleListFromDoubleArr(null);
		assertNull(newList);
	}
	
	@Test
	public void testAsDoubleListFromStringList() {
		List<String> originalList = new ArrayList<>();
		originalList.add("1.0");
		originalList.add("2.2");
		originalList.add("3.3");
		List<Double> newList = ArrayUtil.asDoubleListFromStringList(originalList);
		assertEquals(1.0, newList.get(0), 1.0e-4);
		assertEquals(2.2, newList.get(1), 1.0e-4);
		assertEquals(3.3, newList.get(2), 1.0e-4);
	}
	
	@Test
	public void testAsDoubleListFromStringList_WithBadData() {
		List<String> originalList = new ArrayList<>();
		originalList.add("1.0");
		originalList.add("2.2");
		originalList.add("A");
		List<Double> newList = ArrayUtil.asDoubleListFromStringList(originalList);
		assertNull(newList);
	}
	
	@Test
	public void testAsDoubleListFromStringList_WithNull() {
		List<Double> newList = ArrayUtil.asDoubleListFromStringList(null);
		assertNull(newList);
	}
	
	@Test
	public void testAsIntArr() {
		String[] originalArr = new String[] {"1", "2", "3"};
		int[] newArr = ArrayUtil.asIntArr(originalArr);
		assertEquals(1, newArr[0]);
		assertEquals(2, newArr[1]);
		assertEquals(3, newArr[2]);
	}
	
	@Test
	public void testAsIntArr_WithBadData() {
		String[] originalArr = new String[] {"1", "2", "A"};
		int[] newArr = ArrayUtil.asIntArr(originalArr);
		assertNull(newArr);
	}
	
	@Test
	public void testAsIntArr_WithNull() {
		int[] newArr = ArrayUtil.asIntArr(null);
		assertNull(newArr);
	}
	
	@Test
	public void testAsIntArrFromStringList() {
		List<String> originalList = new ArrayList<>();
		originalList.add("1");
		originalList.add("2");
		originalList.add("3");
		
		int[] newArr = ArrayUtil.asIntArrFromStringList(originalList);
		assertEquals(1, newArr[0]);
		assertEquals(2, newArr[1]);
		assertEquals(3, newArr[2]);
	}
	
	@Test
	public void testAsIntegerListFromIntArr() {
		int[] originalArr = new int[] {1, 2, 3};
		List<Integer> newList = ArrayUtil.asIntegerListFromIntArr(originalArr);
		assertEquals(Integer.valueOf(1), newList.get(0));
		assertEquals(Integer.valueOf(2), newList.get(1));
		assertEquals(Integer.valueOf(3), newList.get(2));
	}
	
	@Test
	public void testAsIntegerListFromIntArr_WithNull() {
		List<Integer> newList = ArrayUtil.asIntegerListFromIntArr(null);
		assertNull(newList);
	}
	
	@Test
	public void testAsIntegerListFromStringList() {
		List<String> originalList = new ArrayList<>();
		originalList.add("1");
		originalList.add("2");
		originalList.add("3");
		List<Integer> newList = ArrayUtil.asIntegerListFromStringList(originalList);
		assertEquals(Integer.valueOf(1), newList.get(0));
		assertEquals(Integer.valueOf(2), newList.get(1));
		assertEquals(Integer.valueOf(3), newList.get(2));
	}
	
	@Test
	public void testAsIntegerListFromStringList_WithBadData() {
		List<String> originalList = new ArrayList<>();
		originalList.add("1");
		originalList.add("2");
		originalList.add("A");
		List<Integer> newList = ArrayUtil.asIntegerListFromStringList(originalList);
		assertNull(newList);
	}
	
	@Test
	public void testAsIntegerListFromStringList_WithNull() {
		List<Integer> newList = ArrayUtil.asIntegerListFromStringList(null);
		assertNull(newList);
	}
	
	@Test
	public void testAsStringListFromIntArr() {
		int[] originalArr = new int[] {1,2,3};
		List<String> newList = ArrayUtil.asStringList(originalArr);
		assertEquals("1", newList.get(0));
		assertEquals("2", newList.get(1));
		assertEquals("3", newList.get(2));
	}
	
	@Test
	public void testAsStringList_FromDoublePrimitiveArr() {
		double[] originalArr = new double[] { 1.0, 2.0, 3.0 };
		List<String> newList = ArrayUtil.asStringList(originalArr);
		assertEquals("1.0", newList.get(0));
		assertEquals("2.0", newList.get(1));
		assertEquals("3.0", newList.get(2));
	}
	
	@Test
	public void testAsStringList_FromDoubleObjectArr() {
		Double[] originalArr = new Double[] { 1.0, 2.0, 3.0 };
		List<String> newList = ArrayUtil.asStringList(originalArr);
		assertEquals("1.0", newList.get(0));
		assertEquals("2.0", newList.get(1));
		assertEquals("3.0", newList.get(2));
	}
	
	@Test
	public void testAsStringList_FromDoubleObjectArrWithNulls() {
		Double[] originalArr = new Double[] { 1.0, null, 3.0 };
		List<String> newList = ArrayUtil.asStringList(originalArr);
		assertEquals("1.0", newList.get(0));
		assertEquals("", newList.get(1));
		assertEquals("3.0", newList.get(2));
	}
	
	@Test
	public void testAsStringListWithQuotes() {
		double[] originalArr = new double[] { 1.0, 2.0, 3.0 };
		List<String> newList = ArrayUtil.asStringListWithQuotes(originalArr, '\'');
		assertEquals("'1.0'", newList.get(0));
		assertEquals("'2.0'", newList.get(1));
		assertEquals("'3.0'", newList.get(2));
	}
	
	@Test
	public void testAsStringListWithQuotes_WithBadChar() {
		try {
			double[] originalArr = new double[] { 1.0, 2.0, 3.0 };
			ArrayUtil.asStringListWithQuotes(originalArr, 'A');
			fail("Test should have thrown an UnsupportedOperationException.");
		} catch(UnsupportedOperationException e) {
			assertEquals("Unrecognized quote character.", e.getMessage());
		}
	}
	
	@Test
	public void testAsStringListWithQuotes_FromStringList() {
		List<String> originalList = new ArrayList<>();
		originalList.add("1.0");
		originalList.add("2.0");
		originalList.add("3.0");
		
		List<String> newList = ArrayUtil.asStringListWithQuotes(originalList, '\'');
		assertEquals("'1.0'", newList.get(0));
		assertEquals("'2.0'", newList.get(1));
		assertEquals("'3.0'", newList.get(2));
	}
	
	@Test
	public void testAsStringListWithQuotes_FromStringListWithBadChar() {
		try {
			List<String> originalList = new ArrayList<>();
			originalList.add("1.0");
			originalList.add("2.0");
			originalList.add("3.0");
			
			ArrayUtil.asStringListWithQuotes(originalList, 'A');
			fail("Test should have thrown an UnsupportedOperationException.");
		} catch(UnsupportedOperationException e) {
			assertEquals("Unrecognized quote character.", e.getMessage());
		}
	}
	
	@Test
	public void testIsDoubleList() {
		List<String> originalList = new ArrayList<>();
		originalList.add("1.0");
		originalList.add("2.0");
		originalList.add("3.0");
		assertTrue(ArrayUtil.isDoubleList(originalList));
		
		originalList.clear();
		originalList.add("1.0");
		originalList.add("2.0");
		originalList.add("A");
		assertFalse(ArrayUtil.isDoubleList(originalList));
	}
	
	@Test
	public void testIsDoubleListWithNaNs() {
		List<String> originalList = new ArrayList<>();
		originalList.add("1.0");
		originalList.add("NaN");
		originalList.add("3.0");
		assertTrue(ArrayUtil.isDoubleList(originalList));
	}
	
	@Test
	public void testIsIntList() {
		List<String> originalList = new ArrayList<>();
		originalList.add("1");
		originalList.add("2");
		originalList.add("3");
		assertTrue(ArrayUtil.isIntList(originalList));
		
		originalList.clear();
		originalList.add("1");
		originalList.add("2");
		originalList.add("A");
		assertFalse(ArrayUtil.isIntList(originalList));
	}
	
	@Test
	public void testNormalizeArray() {
		double[] originalArray           = new double[] { 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0 };
		double[] actualNormalizedArray   = ArrayUtil.normalizeArray(originalArray);
		double[] expectedNormalizedArray = new double[] { 0.0, 0.1111, 0.2222, 0.3333, 0.4444, 0.5555, 0.66666, 0.7777, 0.8888, 1.0 };
		
		assertArrayEquals(expectedNormalizedArray, actualNormalizedArray, 1.0e-4);
	}
	
	@Test
	public void testGetMin() {
		double[] array = new double[] { 2.0, 3.0, 4.0, 5.0, 6.0, 1.0, 7.0, 8.0, 9.0, 10.0 };
		assertEquals(1.0, ArrayUtil.getMin(array), 1.0e-4);
	}
	
	@Test
	public void testGetMax() {
		double[] array = new double[] { 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 10.0, 7.0, 8.0, 9.0 };
		assertEquals(10.0, ArrayUtil.getMax(array), 1.0e-4);
	}
	
	@Test
	public void testGetMinFromStringList() {
		List<String> strList = new ArrayList<>();
		strList.add("3.0");
		strList.add("2.0");
		strList.add("4.0");
		strList.add("1.0");
		strList.add("5.0");
		assertEquals(1.0, ArrayUtil.getMinFromStringList(strList), 1.0e-4);
	}
	
	@Test
	public void testGetMaxFromStringList() {
		List<String> strList = new ArrayList<>();
		strList.add("6.0");
		strList.add("7.0");
		strList.add("10.0");
		strList.add("8.0");
		strList.add("9.0");
		assertEquals(10.0, ArrayUtil.getMaxFromStringList(strList), 1.0e-4);
	}
	
	@Test
	public void testGetMaxFromEmptyStringList() {
		List<String> strList = new ArrayList<>();
		assertEquals(Double.NaN, ArrayUtil.getMaxFromStringList(strList), 1.0e-4);
	}
	
	@Test
	public void testGetMinFromEmptyStringList() {
		List<String> strList = new ArrayList<>();
		assertEquals(Double.NaN, ArrayUtil.getMinFromStringList(strList), 1.0e-4);
	}
	
	@Test
	public void testSortNumerically() {
		List<String> originalStrList = new ArrayList<>();
		originalStrList.add("8.0");
		originalStrList.add("7.0");
		originalStrList.add("10.0");
		originalStrList.add("9.0");
		originalStrList.add("6.0");
		
		List<String> expectedStrList = new ArrayList<>();
		expectedStrList.add("6.0");
		expectedStrList.add("7.0");
		expectedStrList.add("8.0");
		expectedStrList.add("9.0");
		expectedStrList.add("10.0");
		
		assertEquals(expectedStrList, ArrayUtil.sortNumerically(originalStrList));
	}
	
	@Test
	public void testSortIntelligent_ForNumbers() {
		List<String> originalStrList = new ArrayList<>();
		originalStrList.add("8.0");
		originalStrList.add("7.0");
		originalStrList.add("10.0");
		originalStrList.add("9.0");
		originalStrList.add("6.0");
		
		List<String> expectedStrList = new ArrayList<>();
		expectedStrList.add("6.0");
		expectedStrList.add("7.0");
		expectedStrList.add("8.0");
		expectedStrList.add("9.0");
		expectedStrList.add("10.0");
		
		assertEquals(expectedStrList, ArrayUtil.sortIntelligent(originalStrList));
	}
	
	@Test
	public void testSortIntelligent_ForNonNumbers() {
		List<String> originalStrList = new ArrayList<>();
		originalStrList.add("10.0");
		originalStrList.add("11.0");
		originalStrList.add("1.0");
		originalStrList.add("A");
		originalStrList.add("$");
		
		List<String> expectedStrList = new ArrayList<>();
		expectedStrList.add("$");
		expectedStrList.add("1.0");
		expectedStrList.add("10.0");
		expectedStrList.add("11.0");
		expectedStrList.add("A");
		
		assertEquals(expectedStrList, ArrayUtil.sortIntelligent(originalStrList));
	}
	
	@Test
	public void testApplyDecimalPrecision_ForDoubleArr() {
		double[] originalDoubleArr = new double[] { 1.2156343443, 2.243543435, 3.343543435345, 4.67234756 };
		double[] expectedDoubleArr = new double[] { 1.216, 2.244, 3.344, 4.672 };
		double[] actualDoubleArr = ArrayUtil.applyDecimalPrecision(originalDoubleArr, 3);
		assertArrayEquals(expectedDoubleArr, actualDoubleArr, 1e-4);
	}
	
	@Test
	public void testApplyDecimalPrecision_ForStringList() {
		List<String> originalStringList = new ArrayList<>();
		originalStringList.add("1.2156343443");
		originalStringList.add("2.243543435");
		originalStringList.add("3.343543435345");
		originalStringList.add("4.67234756");
		
		List<String> expectedStringList = new ArrayList<>();
		expectedStringList.add("1.216");
		expectedStringList.add("2.244");
		expectedStringList.add("3.344");
		expectedStringList.add("4.672");
		
		List<String> actualStringList = ArrayUtil.applyDecimalPrecision(originalStringList, 3);
		assertEquals(expectedStringList, actualStringList);
	}
	
	@Test
	public void testApplyDecimalPrecision_ForStringListAndHandleBadValues() {
		List<String> originalStringList = new ArrayList<>();
		originalStringList.add("1.2156343443");
		originalStringList.add("2.243543435");
		originalStringList.add("A");
		originalStringList.add("3.343543435345");
		originalStringList.add("4.67234756");
		
		List<String> expectedStringList = new ArrayList<>();
		expectedStringList.add("1.216");
		expectedStringList.add("2.244");
		expectedStringList.add("A");
		expectedStringList.add("3.344");
		expectedStringList.add("4.672");
		
		List<String> actualStringList = ArrayUtil.applyDecimalPrecision(originalStringList, 3);
		assertEquals(expectedStringList, actualStringList);
	}
	
	@Test
	public void testMonotonicNumbers() {
		double[] expectedZeroArray = new double[] { 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0 }; 	
		double[] actualZeroArray = ArrayUtil.monotonicNumbers(10, true);
		assertArrayEquals(expectedZeroArray, actualZeroArray, 1.e-4);
		
		double[] expectedOneArray = new double[] { 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0 }; 	
		double[] actualOneArray = ArrayUtil.monotonicNumbers(10, false);
		assertArrayEquals(expectedOneArray, actualOneArray, 1.e-4);
	}
	
	@Test
	public void testGetLinearRegression() {
		double[] xData = doubleArrayOfSize(10, randomSeed1);
		double[] yData = doubleArrayOfSize(10, randomSeed2);
		
		Pair<Double, Double> linearRegression = ArrayUtil.getLinearRegression(xData, yData);
		assertEquals(0.006553062587625312, linearRegression.getLeft(), 1.0e-4);
		assertEquals(0.4599503597461731, linearRegression.getRight(), 1.0e-4);
	}
	
	@Test
	public void testGetLinearRegression_UnequalLength() {
		double[] xData = doubleArrayOfSize(10, randomSeed1);
		double[] yData = doubleArrayOfSize(15, randomSeed2);
	
		// The result should be the same as the previous unit test, because extra
		// data in the longer Y array should get thrown away and not be considered
		// for the linear regression calculation.
		Pair<Double, Double> linearRegression = ArrayUtil.getLinearRegression(xData, yData);
		assertEquals(0.006553062587625312, linearRegression.getLeft(), 1.0e-4);
		assertEquals(0.4599503597461731, linearRegression.getRight(), 1.0e-4);
    }
    
	private double[] doubleArrayOfSize(int size, Random rand) {
		double[] array = new double[size];
		for(int i = 0; i < size; i++) {
			array[i] = rand.nextDouble();
		}
		return array;
	}
}

