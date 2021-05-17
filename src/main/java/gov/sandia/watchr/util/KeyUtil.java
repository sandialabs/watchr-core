/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.util;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Utility methods related to keyboard entry data.
 * 
 * @author Elliott Ridgway
 *
 */
public class KeyUtil {
	
	////////////
	// FIELDS //
	////////////
	
	public static final String INT_REGEX = "-?\\d*"; //$NON-NLS-1$
	public static final String INT_SCIENTIFIC_NOTATION_REGEX = "-?\\d*((e|E)(\\+|\\-)*(\\d*))??";
	public static final String DOUBLE_REGEX = "\\-{0,1}\\d*\\.{0,1}\\d*(e(\\-|\\+){0,1}|E(\\-|\\+){0,1}){0,1}\\d*\\.{0,1}\\d*"; //$NON-NLS-1$
	public static final String STRING_REGEX = ".*"; //$NON-NLS-1$
	public static final String STRING_REGEX_NO_SPACES = "[^\\s]*"; //$NON-NLS-1$
	
	public static final Character NULL_CHAR = '\0';
	public static final Character BACKSPACE = '\u0008';
	public static final Character DELETE    = '\u007F';

	/////////////////
	// CONSTRUCTOR //
	/////////////////

	private KeyUtil() {}
	
	/////////////
	// UTILITY //
	/////////////
	
	/**
	 * Verifies whether a piece of text still adheres to a regular expression after new text
	 * (typically coming from a keyboard button press event) has been inserted.
	 * 
	 * @param regex The regular expression to check the new text against.
	 * @param fullText The full text before insertion.
	 * @param eText Depending on the event, the new text that will be inserted.
	 * @param eCharacter Depending on the event, the character represented by the key that was typed.
	 * @param keyLocation The location in the fullText to insert the eText string.
	 * @return True if the text insertion did not violate the regular expression.
	 */
	public static boolean verifyTextInsertion(String regex, String fullText, String eText, Character eCharacter, int keyLocation) {
		boolean proceed = true;
		
		if(keyLocation > fullText.length() || keyLocation < 0) {
			proceed = false;
		} else if(!eCharacter.equals(BACKSPACE) && !eCharacter.equals(DELETE)) {
			StringBuilder sb = new StringBuilder();
			sb.append(fullText.substring(0, keyLocation));
			if(StringUtils.isNotBlank(eText)) {
				sb.append(eText);
			} else {
				sb.append(eCharacter);
			}
			sb.append(fullText.substring(keyLocation, fullText.length()));
			
			proceed = Pattern.matches(regex, sb.toString());
		}
		
		return proceed;
	}
}
