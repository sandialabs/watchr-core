/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * 
 * @author Derek Trumbo, Elliott Ridgway
 */
public class NumUtil {

	/**
	 * Normalizes a number value based on a provided minimum and maximum.
	 * 
	 * @param myVal The value to normalize.
	 * @param minVal The minimum value.
	 * @param maxVal The maximum value.
	 * @return The normalized value.
	 */
	public static double normalize(double myVal, double minVal, double maxVal) {
		return (myVal - minVal) / (maxVal - minVal);
	}
	
	/**
	 * Converts a number to its ordinal representation.<br><br>
	 * Code:  https://stackoverflow.com/questions/6810336/is-there-a-way-in-java-to-convert-an-integer-to-its-ordinal
	 * 
	 * @param i The number.
	 * @return Its ordinal representation.
	 */
	public static String ordinal(int i) {
	    String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
	    switch (i % 100) {
	    case 11:
	    case 12:
	    case 13:
	        return i + "th"; //$NON-NLS-1$
	    default:
	        return i + sufixes[i % 10];

	    }
	}
	
	/**
	 * Checks whether a {@link String} {@code s}
	 * is an integer.
	 * @param s The original String to check.
	 * @return True if it is an integer.
	 */
	public static boolean isInteger(String s) {
		boolean isInteger = StringUtils.isNotBlank(s);
		isInteger = isInteger && s.matches(KeyUtil.INT_REGEX);
		
		if(NumberUtils.isCreatable(s)) {
			Double intAsDouble = Double.parseDouble(s);
			isInteger = isInteger && intAsDouble < Integer.MAX_VALUE;
			isInteger = isInteger && intAsDouble > Integer.MIN_VALUE;
		}
		
		return isInteger;
	}
	
	/**
	 * Checks whether a {@link String} {@code s}
	 * can be formatted as an integer.  For instance,
	 * {@code s} may be written in scientific notation,
	 * but when written normally, it has no decimal
	 * component.
	 * @param s The original String to check.
	 * @return True if it can be formatted as an integer.
	 */
	public static boolean isFormattableAsInteger(String s) {
		if(NumberUtils.isCreatable(s)) {
			Double d = Double.parseDouble(s);
			return (d % 1 == 0);
		}
		return false;
	}
	
	/**
	 * Parses a {@link String} into a {@link Double}.  Also
	 * checks for case-insensitive variations of the String "NaN".
	 * @param dataValue The original String value.
	 * @return The Double value is the String is a creatable number, or Double.NaN if the String
	 * case-insensitively matches "NaN", or null if neither of the first two cases are true.
	 */
	public static Double toDoubleOrNaN(String dataValue) {
		if(NumberUtils.isCreatable(dataValue)) {
			Double number = Double.parseDouble(dataValue);
			return number;
		} else if(Double.toString(Double.NaN).toLowerCase().equals(dataValue.toString().toLowerCase())) {
			return Double.NaN;
		}
		return null;
	}
	
	/**
	 * Truncates a decimal number to a specific number of decimal points.
	 * @param originalDouble The original double value.
	 * @param precision The number of decimal points to preserve.
	 * @return The double value with decimal precision applied.
	 */
	public static Double applyDecimalPrecision(double originalDouble, int precision) {
		StringBuilder sb = new StringBuilder("#."); //$NON-NLS-1$
		for(int i = 0; i < precision; i++) {
			sb.append("#"); //$NON-NLS-1$
		}
		String doubleFormat = sb.toString();
		DecimalFormat formatter = new DecimalFormat(doubleFormat, DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		
		return Double.parseDouble(formatter.format(originalDouble));
	}
}
