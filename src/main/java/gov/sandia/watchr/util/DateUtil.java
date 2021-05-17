/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Elliott Ridgway
 */

public class DateUtil {

    private DateUtil() {}

    /**
     * 
     * @param epochTime
     * @return
     */
    public static String epochTimeToTimestamp(long epochTime) {
        Date date = new Date(epochTime);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return format.format(date);
    }
}
