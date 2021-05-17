/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.util;

/**
 * Utility methods related to {@link RGB} objects.
 * 
 * @author Elliott Ridgway
 *
 */
public class RgbUtil {

	private RgbUtil() {}
	
	/**
	 * @return The color black, represented as an {@link RGB} object set to (0, 0, 0).
	 */
	public static RGB blackRGB() {
		return new RGB(0, 0, 0);
	}

	public static RGB parseColor(String color) {
        String[] rgbs = color.split(",");
        if(rgbs.length == 3) {
            return new RGB(
                Integer.parseInt(rgbs[0]),
                Integer.parseInt(rgbs[1]),
                Integer.parseInt(rgbs[2])
            );
        }
        return null;
    }
}
