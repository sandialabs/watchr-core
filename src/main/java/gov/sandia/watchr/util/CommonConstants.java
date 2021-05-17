/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.util;

/**
 * Common constants used throughout Watchr.
 * 
 * @author Elliott Ridgway
 */
public class CommonConstants {

    // Alias used to represent the root of the folder tree.
    public static final String ROOT_PATH_ALIAS = "root";

    public static final String UNUSUAL_COMMA = "###COMMA###";
        
    public static final String MAX_DATE = "z"; // "z" is like an upper range for date strings.
    public static final String MIN_DATE = "0"; // "0" is like a lower range for date strings.

    private CommonConstants() {}
}
