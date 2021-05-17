/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.util;

/**
 *
 * @author Elliott Ridgway
 */
public class FileUtil {

    private FileUtil() {}

    /**
	 * Given an arbitrary String, scrub it from characters that are illegal
	 * for filenames.
	 * 
	 * @param originalName The original String.
	 * @return The new String, appropriate for using as a filename.
	 */
	public static String removeIllegalCharactersFromFilename(String originalName) {
		return originalName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_"); //$NON-NLS-1$
	}
}
