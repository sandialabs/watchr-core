/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.util;

import org.apache.commons.lang3.SystemUtils;

/**
 * A collection of utility methods related to determining the current operating system.
 * 
 * @author Elliott Ridgway
 * @see https://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
 *
 */
public class OsUtil {
    
    private OsUtil() {}

	/////////////
	// UTILITY //
	/////////////
	
	/**
	 * @return A Windows-style CRLF line ending if the current OS is Windows.  Otherwise,
	 * return an LF line ending.
	 */
	public static String getOSLineBreak() {
		return SystemUtils.IS_OS_WINDOWS ? "\r\n" : "\n"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * Given a file path string, convert the file separators to the correct slash direction
	 * for the current operating system.
	 * 
	 * @param str A file path string.
	 * @return The same file path string with file separator slashes proper for the
	 * current operating system.
	 */
	public static String convertToOsFileSeparators(String str) {
		if(SystemUtils.IS_OS_WINDOWS) {
			return str.replace("/", "\\"); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			return str.replace("\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Given a string, convert any line ending characters to the correct characters
	 * for the current operating system.
	 * 
	 * @param str The string to convert.
	 * @return The same string with line endings converted for the current
	 * operating system.
	 */
	public static String convertToOsLineEndings(String str) {
		if(SystemUtils.IS_OS_WINDOWS) {
			str = str.replaceAll("(?<!\r)\n", "\r\n");
		} else {
			str = str.replace("\r\n", "\n");
		}
		return str;
	}
}
