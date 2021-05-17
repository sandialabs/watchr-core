/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 * Various array utility methods.
 * @author Elliott Ridgway
 *
 */
public class ArrayUtil {
	
	/////////////////////
	// TO DOUBLE ARRAY //
	/////////////////////
	
	/**
	 * Converts an array of {@link String}s (that are parseable
	 * into {@link Double}s) to a primitive double array.
	 * 
	 * @param originalArr The original array of Strings.
	 * @return The primitive double array, or null if a {@link NumberFormatException}
	 * or {@link NullPointerException} was encountered.
	 * 
	 */
	public static double[] asDoubleArr(String[] originalArr) {
		try {
			double[] xArr = new double[originalArr.length];
			for(int i = 0; i < xArr.length; i++) {
				xArr[i] = Double.parseDouble(originalArr[i]);
			}
			return xArr;
		} catch(NumberFormatException | NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * Converts an array of {@link String}s (that are parseable
	 * into {@link Double}s) to a Double object array.
	 * 
	 * @param originalArr The original array of Strings.
	 * @return The Double object array, or null if a {@link NumberFormatException}
	 * or {@link NullPointerException} was encountered.
	 * 
	 */
	public static Double[] asDoubleObjArr(String[] originalArr) {
		try {
			Double[] xArr = new Double[originalArr.length];
			for(int i = 0; i < xArr.length; i++) {
				xArr[i] = Double.parseDouble(originalArr[i]);
			}
			return xArr;
		} catch(NumberFormatException | NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * Converts a {@link List} of {@link String}s (that are parseable
	 * into {@link Double}s) to a primitive double array.
	 * 
	 * @param originalList The original List of Strings.
	 * @return The primitive double array, or null if a {@link NumberFormatException}
	 * or {@link NullPointerException} was encountered.
	 * 
	 */
	public static double[] asDoubleArrFromStringList(List<String> originalList) {
		String[] originalArr = originalList.toArray(new String[originalList.size()]);
		return asDoubleArr(originalArr);
	}

	/**
	 * Converts a {@link List} of {@link String}s (that are parseable
	 * into {@link Double}s) to a Double object array.
	 * 
	 * @param originalList The original List of Strings.
	 * @return The Double object array, or null if a {@link NumberFormatException}
	 * or {@link NullPointerException} was encountered.
	 * 
	 */
	public static Double[] asDoubleObjArrFromStringList(List<String> originalList) {
		String[] originalArr = originalList.toArray(new String[originalList.size()]);
		return asDoubleObjArr(originalArr);
	}
	
	/**
	 * 
	 * @param originalArr
	 * @return
	 */
    public static Double[] asDoubleObjArrFromDoubleArr(double[] originalArr) {
        Double[] xArr = new Double[originalArr.length];
        for(int i = 0; i < xArr.length; i++) {
            xArr[i] = originalArr[i];
        }
        return xArr;
    }
    
    /**
     * 
     * @param originalArr
     * @return
     */
    public static double[] asDoubleArrFromDoubleObjArr(Double[] originalArr) {
        double[] xArr = new double[originalArr.length];
        for(int i = 0; i < xArr.length; i++) {
            xArr[i] = originalArr[i];
        }
        return xArr;
    }
	
	/**
	 * Converts a {@link List} of {@link Double}s to a primitive double array.
	 * 
	 * @param originalList The original List of Doubles.
	 * @return The primitive double array, or null if a {@link NullPointerException}
	 * was encountered.
	 * 
	 */
	public static double[] asDoubleArrFromDoubleList(List<Double> originalList) {
		try {
			double[] xArr = new double[originalList.size()];
			for(int i = 0; i < xArr.length; i++) {
				xArr[i] = originalList.get(i);
			}
			return xArr;
		} catch(NullPointerException e) {
			return null;
		}
	}
	
	////////////////////
	// TO DOUBLE LIST //
	////////////////////
	
	/**
	 * Converts an array of doubles to a {@link List} of {@link Double}s.
	 * 
	 * @param originalArr The original double array.
	 * @return The List of Doubles, or null if a {@link NullPointerException}
	 * was encountered.
	 */
	public static List<Double> asDoubleListFromDoubleArr(double[] originalArr) {
		try {
			List<Double> xList = new ArrayList<>();
			for(int i = 0; i < originalArr.length; i++) {
				xList.add(originalArr[i]);
			}
			return xList;
		} catch(NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * Converts a {@link List} of {@link String}s (parseable into {@link Double}
	 * objects) to a List of {@link Double}s.
	 * 
	 * @param originalList The original List of Strings.
	 * @return The List of Doubles, or null if the List could not be created.
	 */
	public static List<Double> asDoubleListFromStringList(List<String> originalList) {
        if(originalList == null) {
            return null;
        }
        
        List<Double> xList = new ArrayList<>();
        for(int i = 0; i < originalList.size(); i++) {
            if(NumberUtils.isCreatable(originalList.get(i)) ||
                    originalList.get(i).equals(Double.toString(Double.NaN))) {
                xList.add(Double.parseDouble(originalList.get(i)));
            } else {
                return null;
            }
        }
        return xList;
    }
	
	//////////////////
	// TO INT ARRAY //
	//////////////////
	
	/**
	 * Converts an array of {@link String}s (that are parseable
	 * into {@link Integer}s) to a primitive int array.
	 * 
	 * @param originalArr The original array of Strings.
	 * @return The primitive int array, or null if a {@link NumberFormatException}
	 * or {@link NullPointerException} was encountered.
	 * 
	 */
	public static int[] asIntArr(String[] originalArr) {
		try {
			int[] xArr = new int[originalArr.length];
			for(int i = 0; i < xArr.length; i++) {
				xArr[i] = Integer.parseInt(originalArr[i]);
			}
			return xArr;
		} catch(NumberFormatException | NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * Converts a {@link List} of {@link String}s (that are parseable
	 * into {@link Integer}s) to a primitive int array.
	 * 
	 * @param originalList The original List of Strings.
	 * @return The primitive int array, or null if a {@link NumberFormatException}
	 * or {@link NullPointerException} was encountered.
	 * 
	 */
	public static int[] asIntArrFromStringList(List<String> originalList) {
		String[] originalArr = originalList.toArray(new String[originalList.size()]);
		return asIntArr(originalArr);
	}
	
	/////////////////////
	// TO INTEGER LIST //
	/////////////////////
	
	/**
	 * Converts an array of ints to a {@link List} of {@link Integer}s.
	 * 
	 * @param originalArr The original array of ints.
	 * @return The List of Integers, or null if a {@link NullPointerException}
	 * was encountered.
	 * 
	 */
	public static List<Integer> asIntegerListFromIntArr(int[] originalArr) {
		try {
			List<Integer> xList = new ArrayList<>();
			for(int i = 0; i < originalArr.length; i++) {
				xList.add(originalArr[i]);
			}
			return xList;
		} catch(NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * Converts a {@link List} of {@link String}s (parseable as {@link Integer}s)
	 * into a List of Integers.
	 * 
	 * @param originalList The original List of Strings.
	 * @return The List of Integers, or null if the List could not be created.
	 * 
	 */
	public static List<Integer> asIntegerListFromStringList(List<String> originalList) {
        if(originalList == null) {
            return null;
        }
        
        List<Integer> xList = new ArrayList<>();
        for(int i = 0; i < originalList.size(); i++) {
            if(NumUtil.isFormattableAsInteger(originalList.get(i))) {
                xList.add(Integer.parseInt(originalList.get(i)));
            } else {
                return null;
            }
        }
        return xList;
    }
	
	////////////////////
	// TO STRING LIST //
	////////////////////
	
	/**
	 * Converts an int array into a {@link List} of {@link String}s.
	 * 
	 * @param origArr The int array.
	 * @return The List of Strings.
	 */
	public static List<String> asStringList(int[] origArr) {
		List<String> newList = new ArrayList<>();
		for(int i = 0; i < origArr.length; i++) {
			newList.add(Integer.toString(origArr[i]));
		}
		return newList;
	}
	
	/**
	 * Converts a double array into a {@link List} of {@link String}s.
	 * 
	 * @param origArr The double array.
	 * @return The List of Strings.
	 */
	public static List<String> asStringList(double[] origArr) {
		List<String> newList = new ArrayList<>();
		for(int i = 0; i < origArr.length; i++) {
			newList.add(Double.toString(origArr[i]));
		}
		return newList;
	}
	
	/**
	 * Converts a {@link Double} object array into a {@link List} of {@link String}s.
	 * Null values are converted to empty Strings.
	 * 
	 * @param origArr The Double array.
	 * @return The List of Strings.
	 */
	public static List<String> asStringList(Double[] origArr) {
		List<String> newList = new ArrayList<>();
		for(int i = 0; i < origArr.length; i++) {
			Double value = origArr[i];
			if(value != null) {
				newList.add(Double.toString(value));
			} else {
				newList.add("");
			}
		}
		return newList;
	}
	
	/**
	 * Converts a double array to a {@link List} of {@link String}s, where each
	 * element is wrapped in a set of quotes provided by the caller.
	 * 
	 * @param origArr The original array of doubles.
	 * @param quote The quote character to use.  Only ' and " are allowed.
	 * @return The List of quote-wrapped doubles, as Strings.
	 * @throws UnsupportedOperationException Thrown if an unrecognized character is used for the quote.
	 */
	public static List<String> asStringListWithQuotes(double[] origArr, char quote) throws UnsupportedOperationException {
		if(quote != '\'' && quote != '"') {
			throw new UnsupportedOperationException("Unrecognized quote character.");
		}
		
		List<String> newList = new ArrayList<>();
		for(int i = 0; i < origArr.length; i++) {
			newList.add(Character.toString(quote) + origArr[i] + Character.toString(quote));
		}
		return newList;
	}
	
	/**
	 * Converts a {@link List} of {@link String}s to a new List, where each
	 * element is wrapped in a set of quotes provided by the caller.
	 * 
	 * @param origList The original array of Strings.
	 * @param quote The quote character to use.  Only ' and " are allowed.
	 * @return The List of quote-wrapped Strings.
	 * @throws UnsupportedOperationException Thrown if an unrecognized character is used for the quote.
	 */
	public static List<String> asStringListWithQuotes(List<String> origList, char quote) throws UnsupportedOperationException {
		if(quote != '\'' && quote != '"') {
			throw new UnsupportedOperationException("Unrecognized quote character.");
		}
		
		List<String> newList = new ArrayList<>();
		for(int i = 0; i < origList.size(); i++) {
			newList.add(Character.toString(quote) + origList.get(i) + Character.toString(quote));
		}
		return newList;
	}
	
	///////////
	// CHECK //
	///////////
	
	/**
	 * Verifies that each element in a {@link List} of {@link String}s is
	 * parseable to a {@link Double}.
	 * 
	 * @param originalList The original List of Strings to inspect.
	 * @return Whether the List only contains parseably doubles.
	 */
	public static boolean isDoubleList(List<String> originalList) {
		return ArrayUtil.asDoubleObjArrFromStringList(originalList) != null;
	}
	
	/**
	 * Verifies that each element in a {@link List} of {@link String}s is
	 * parseable to an {@link Integer}.
	 * 
	 * @param originalList The original List of Strings to inspect.
	 * @return Whether the List only contains parseable Integers.
	 */
	public static boolean isIntList(List<String> originalList) {
		return ArrayUtil.asIntArrFromStringList(originalList) != null;
	}
	
	//////////////////
	// PROCESS DATA //
	//////////////////
	
	/**
	 * Normalizes the data in a double array, using the smallest and
	 * largest values in the array as the bounds to normalize against.
	 * 
	 * @param originalArr The double array to normalize.
	 * @return A new array with normalized double values.
	 */
	public static double[] normalizeArray(double[] originalArr) {
		double[] finalArr = new double[originalArr.length];
		for(int i = 0; i < originalArr.length; i++) {
			finalArr[i] = NumUtil.normalize(originalArr[i], getMin(originalArr), getMax(originalArr));
		}
		return finalArr;
	}
	
	/**
	 * Gets the linear regression given two arrays of X and Y data.  If the arrays
	 * are of unequal size, the shorter array length is used and any data leftover
	 * in the longer array is not used.
	 * 
	 * @param xData The X data.
	 * @param yData The Y data.
	 * @return A {@link Pair} representing slope and intercept.
	 */ 
	public static Pair<Double, Double> getLinearRegression(double[] xData, double[] yData) {
		SimpleRegression simpleRegression = new SimpleRegression(true);
		
		for(int i = 0; i < xData.length && i < yData.length; i++) {
			simpleRegression.addData(xData[i], yData[i]);
		}
		
		return new ImmutablePair<Double, Double>(simpleRegression.getSlope(), simpleRegression.getIntercept());
	}
	
	/////////////
	// MIN/MAX //
	/////////////
	
	/**
	 * Finds the minimum value in a double array.  This is primarily a convenience
	 * method that performs the necessary conversions to leverage {@link Collections#min}.
	 * @param sourceArray The original array.
	 * @return The minimum double value in the array, or negative of the Double.MAX_VALUE if the array
	 * is empty.
	 */
	public static double getMin(double[] sourceArray) {
		if(sourceArray.length == 0) {
			return Double.MAX_VALUE * -1;
		}
		return Collections.min(Arrays.asList(ArrayUtils.toObject(sourceArray)));
	}
	
	/**
	 * Finds the maximum value in a double array.  This is primarily a convenience
	 * method that performs the necessary conversions to leverage {@link Collections#max}.
	 * @param sourceArray The original array.
	 * @return The maximum double value in the array, or Double.MAX_VALUE if the array
	 * is empty.
	 */
	public static double getMax(double[] sourceArray) {
		if(sourceArray.length == 0) {
			return Double.MAX_VALUE;
		}
		return Collections.max(Arrays.asList(ArrayUtils.toObject(sourceArray)));
	}
	
	/**
	 * Finds the minimum value in a {@link Collection} of {@link String}s, assuming that each
	 * String in the List is a creatable number - see {@link NumberUtils#isCreatable}..
	 * "Uncreatable" numbers are not considered.
	 * 
	 * @param sourceList The original Collection.
	 * @return The minimum double value from the List, or NaN if the list is invalid (null or empty).
	 */
	public static double getMinFromStringList(Collection<String> sourceList) {
		List<Double> doubleValues = new ArrayList<>();
		for(String number : sourceList) {
			if(NumberUtils.isCreatable(number)) {
				doubleValues.add(Double.parseDouble(number));
			}
		}
		if(!doubleValues.isEmpty()) {
			return Collections.min(doubleValues);
		}
		return Double.NaN;
	}
	
	/**
	 * Finds the maximum value in a {@link Collection} of {@link String}s, assuming that each
	 * String in the List is a creatable number - see {@link NumberUtils#isCreatable}.
	 * "Uncreatable" numbers are not considered.
	 * 
	 * @param sourceList The original Collection.
	 * @return The maximum double value from the List, or NaN if the list is invalid (null or empty).
	 */
	public static double getMaxFromStringList(Collection<String> sourceList) {
		List<Double> doubleValues = new ArrayList<>();
		for(String number : sourceList) {
			if(NumberUtils.isCreatable(number)) {
				doubleValues.add(Double.parseDouble(number));
			}
		}
		if(!doubleValues.isEmpty()) {
			return Collections.max(doubleValues);
		}
		return Double.NaN;
	}
	
	//////////
	// SORT //
	//////////
	
	/**
	 * Sorts a {@link List} of {@link String}s numerically.  This
	 * method presumes that the String elements in the List are
	 * parseable into {@link Double}s.
	 * 
	 * @param originalList The original String List to sort.
	 * @return A numerically-sorted String List, or null if an
	 * element in the List was not parseable into a Double.
	 */
	public static List<String> sortNumerically(List<String> originalList) {
		try {
			List<Double> numericalList = new ArrayList<>();
			for(String item : originalList) {
				numericalList.add(Double.parseDouble(item));
			}
			Collections.sort(numericalList);
			List<String> finalList = new ArrayList<>();
			for(Double item : numericalList) {
				finalList.add(Double.toString(item));
			}
			return finalList;
		} catch(NullPointerException | NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Sorts a {@link List} of {@link String}s, but also makes a determination
	 * as to whether the list should be sorted alphabetically or numerically.
	 * This is done by inspecting each element of the list to determine if each
	 * element is a parseable {@link Double}.  If not, the list is sorted
	 * as normal using {@link Collections#sort}.
	 * 
	 * @param originalList The original List to sort.
	 * @return The List of Strings, either sorted alphabetically or numerically
	 * depending on the contents.
	 */
	public static List<String> sortIntelligent(List<String> originalList) {
		List<String> finalList = new ArrayList<>();
		if(isDoubleList(originalList)) {
			finalList.addAll(sortNumerically(originalList));
		} else {
			finalList.addAll(originalList);
			Collections.sort(finalList);
		}
		return finalList;
	}
	
	///////////////
	// PRECISION //
	///////////////
	
	/**
	 * Given a {@link List} of {@link String}s, and assuming that this List's String values are
	 * parseable into {@link Double}s, this method will apply decimal precision to each double
	 * value in the list.
	 * 
	 * @param originalList The list of values to apply decimal precision to.
	 * @param precision The number of decimal points to preserve.
	 * @return A new List of Strings, where each element has been truncated.
	 */
	public static List<String> applyDecimalPrecision(List<String> originalList, int precision) {
		List<String> finalList = new ArrayList<>();
		for(String item : originalList) {
			try {
				double number = Double.parseDouble(item);
				finalList.add(Double.toString(NumUtil.applyDecimalPrecision(number, precision)));
			} catch(NumberFormatException nfe) {
				finalList.add(item);
			}
		}
		return finalList; 
	}
	
	/**
	 * Given an array of doubles, this method will apply decimal precision to each double
	 * value in the array.
	 * 
	 * @param originalArr The array of values to apply decimal precision to.
	 * @param precision The number of decimal points to preserve.
	 * @return A new {@link List} of {@link Double}s, where each element in the List is
	 * a double value that has had decimal precision applied to it.
	 */
	public static double[] applyDecimalPrecision(double[] originalArr, int precision) {
		double[] formattedArr = new double[originalArr.length];
		for(int i = 0; i < originalArr.length; i++) {
			formattedArr[i] = NumUtil.applyDecimalPrecision(originalArr[i], precision);
		}
		return formattedArr;
	}
	
	/**
	 * Creates an array with monotonically increasing double values.
	 * @param size The size of the array.
	 * @param zeroBased If true, the first entry in the array is 0.  Otherwise, it's 1.
	 * @return The generated array.
	 */
	public static double[] monotonicNumbers(int size, boolean zeroBased) {
		double[] arr = new double[size];
		for(int i = 0; i < size; i++) {
			arr[i] = zeroBased ? i : i+1;
		}
		return arr;
	}
}