/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
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

    private static final String TIMESTAMP_FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss";

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
        DateFormat format = new SimpleDateFormat(TIMESTAMP_FORMAT_ISO_8601);
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
        DateFormat format = new SimpleDateFormat(TIMESTAMP_FORMAT_ISO_8601);
        format.setTimeZone(zone);
        return format.format(date);
    }

    /**
     * 
     * @param value The value to examine.
     * @return Whether the given value is a timestamp conforming to
     *         "yyyy-MM-dd'T'HH:mm:ss".
     */
    public static boolean isTimestamp(String value) { 
        final String timestampRegex = "\\d{4}\\-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}";
        return value.matches(timestampRegex);
    }

    /**
     * Tests whether a String is a valid timestamp.
     * @param timestamp The String timestamp to test.
     * @return True if valid, false if not.
     */
    public static boolean isTimestampValid(String timestamp) { 
        boolean success = true;
        String[] dateTimeSplit = timestamp.split("T");
        if(dateTimeSplit.length == 2) {
            String date = dateTimeSplit[0];
            String[] dateSplit = date.split("-");
            if(dateSplit.length == 3) {
                String year = dateSplit[0];
                String month = dateSplit[1];
                String day = dateSplit[2];
                success = isYearValid(year);
                success = success && isMonthValid(month);
                success = success && isDayValid(day);
            } else {
                return false;
            }

            String time = dateTimeSplit[1];
            String[] timeSplit = time.split(":");
            if(timeSplit.length == 3) {
                String hour = timeSplit[0];
                String minute = timeSplit[1];
                String second = timeSplit[2];
                success = success && isHourValid(hour);
                success = success && isSecondOrMinuteValid(minute);
                success = success && isSecondOrMinuteValid(second);
            } else {
                return false;
            }
        } else {
            return false;
        }

        return success;
    }

    private static boolean isYearValid(String time) {
        boolean success = true;
        if(NumUtil.isInteger(time)) {
            int timeInt = Integer.parseInt(time);
            success = time.length() == 4;
            success = success && timeInt > 0;
        } else {
            return false;
        }
        return success;
    }    

    private static boolean isMonthValid(String time) {
        boolean success = true;
        if(NumUtil.isInteger(time)) {
            int timeInt = Integer.parseInt(time);
            success = time.length() <= 2;
            success = success && timeInt > 0;
            success = success && timeInt <= 12;
        } else {
            return false;
        }
        return success;
    }

    private static boolean isDayValid(String time) {
        boolean success = true;
        if(NumUtil.isInteger(time)) {
            int timeInt = Integer.parseInt(time);
            success = time.length() <= 2;
            success = success && timeInt > 0;
            success = success && timeInt <= 31;
        } else {
            return false;
        }
        return success;
    }

    private static boolean isHourValid(String time) {
        boolean success = true;
        if(NumUtil.isInteger(time)) {
            int timeInt = Integer.parseInt(time);
            success = time.length() <= 2;
            success = success && timeInt >= 0;
            success = success && timeInt < 24;
        } else {
            return false;
        }
        return success;
    }    

    private static boolean isSecondOrMinuteValid(String time) {
        boolean success = true;
        if(NumUtil.isInteger(time)) {
            int timeInt = Integer.parseInt(time);
            success = time.length() <= 2;
            success = success && timeInt >= 0;
            success = success && timeInt < 60;
        } else {
            return false;
        }
        return success;
    }
}
