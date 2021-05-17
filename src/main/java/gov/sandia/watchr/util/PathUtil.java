/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.util;

import java.util.regex.Pattern;

/**
 *
 * @author Elliott Ridgway
 */
public class PathUtil {
    
    /**
     * Removes a number of leading segments from a file path.  This is useful for shortening an
     * absolute path to a path that starts at some root directory.
     * 
     * @param path The full path to shorten.
     * @param delimiter The delimiter to use for splitting the path.
     * @param segmentsToRemove The number of leading segments to remove.
     * 
     * @return The shortened path.
     */
    public static String removeLeadingSegments(String path, String delimiter, int segmentsToRemove) {
        String[] segments = path.split(Pattern.quote(delimiter));
        StringBuilder finalPathSB = new StringBuilder();
        if(segmentsToRemove <= segments.length) {
            for(int i = segmentsToRemove; i < segments.length; i++) {
                finalPathSB.append(segments[i]);
                if(i < segments.length - 1) {
                    finalPathSB.append(delimiter);
                }
            }
        }
        
        return finalPathSB.toString();
    }
}
