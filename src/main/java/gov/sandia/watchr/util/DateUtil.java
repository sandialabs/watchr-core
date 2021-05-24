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
import java.util.TimeZone;

/**
 *
 * @author Elliott Ridgway
 */

public class DateUtil {

    private DateUtil() {}

    public static String epochTimeToTimestamp(long epochTime) {
        return epochTimeToTimestamp(epochTime, TimeZone.getDefault());
    }

    /**
     * Convert millisecond epoch time to a timestamp.
     * @param epochTime
     * @return
     */
    public static String epochTimeToTimestamp(long epochTime, TimeZone zone) {
        Date date = new Date(epochTime);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        format.setTimeZone(zone);
        return format.format(date);
    }

    public static String epochTimeSecondsToTimestamp(long epochTime) {
        return epochTimeSecondsToTimestamp(epochTime, TimeZone.getDefault());
    }

    /**
     * Convert second epoch time to a timestamp.
     * @param epochTime
     * @return
     */
    public static String epochTimeSecondsToTimestamp(long epochTime, TimeZone zone) {
        long epochTimeMs = epochTime * 1000;
        Date date = new Date(epochTimeMs);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        format.setTimeZone(zone);
        return format.format(date);
    }
}
