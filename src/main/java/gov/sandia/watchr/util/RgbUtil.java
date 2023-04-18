/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.util;

import java.util.Random;

/**
 * Utility methods related to {@link RGB} objects.
 * 
 * @author Elliott Ridgway
 *
 */
public class RgbUtil {

    private static final String RANDOM = "random";
    private static final Random rand = new Random();

	private RgbUtil() {}
	
	/**
	 * @return The color black, represented as an {@link RGB} object set to (0, 0, 0).
	 */
	public static RGB blackRGB() {
		return new RGB(0, 0, 0);
	}

	public static RGB parseColor(String color) {
        if(color.equals(RANDOM)) {
            return new RGB(rand.nextInt(200), rand.nextInt(200), rand.nextInt(200));
        } else {
            String[] rgbs = color.split(",");
            if(rgbs.length == 3) {
                return new RGB(
                    Integer.parseInt(rgbs[0].trim()),
                    Integer.parseInt(rgbs[1].trim()),
                    Integer.parseInt(rgbs[2].trim())
                );
            } else if(rgbs.length == 4) {
                return new RGBA(
                    Integer.parseInt(rgbs[0].trim()),
                    Integer.parseInt(rgbs[1].trim()),
                    Integer.parseInt(rgbs[2].trim()),
                    Double.parseDouble(rgbs[3].trim())
                );
            }
        }
        return null;
    }

    public static RGB copyColor(RGB color) {
        if(color instanceof RGBA) {
            return new RGBA((RGBA)color);
        } else {
            return new RGB(color);
        }
    }    
}
