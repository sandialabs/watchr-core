package gov.sandia.watchr.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.ZoneId;
import java.util.TimeZone;

import org.junit.Test;

public class DateUtilTest {

    @Test
    public void testEpochTimeToTimestamp() {
        String date = DateUtil.epochTimeToTimestamp(1615501362450L, TimeZone.getTimeZone(ZoneId.of("-07:00")));
        assertEquals("2021-03-11T15:22:42", date);
    }

    @Test
    public void testEpochTimeSecondsToTimestamp() {
        String date = DateUtil.epochTimeSecondsToTimestamp(1600304331, TimeZone.getTimeZone(ZoneId.of("-07:00")));
        assertEquals("2020-09-16T17:58:51", date);
    }

    @Test
    public void testIsTimestamp() {
        assertTrue(DateUtil.isTimestamp("2021-07-07T12:30:30"));
        assertFalse(DateUtil.isTimestamp("20210707T123030"));
        assertFalse(DateUtil.isTimestamp("202111-07-07T12:30:30"));
        assertTrue(DateUtil.isTimestamp("2019-04-19T19:47:60"));
    }

    @Test
    public void testIsTimestampValid() {
        assertTrue(DateUtil.isTimestampValid("2021-07-07T12:30:30"));
        assertFalse(DateUtil.isTimestampValid("2021-00-07T12:30:30"));
        assertFalse(DateUtil.isTimestampValid("2021-13-07T12:30:30"));
        assertFalse(DateUtil.isTimestampValid("2021-07-32T12:30:30"));
        assertFalse(DateUtil.isTimestampValid("2021-07-00T12:30:30"));
        assertFalse(DateUtil.isTimestampValid("2021-07-00T24:30:30"));
        assertFalse(DateUtil.isTimestampValid("2021-07-00T12:60:30"));
        assertFalse(DateUtil.isTimestampValid("2021-07-00T12:30:60"));
        assertFalse(DateUtil.isTimestampValid("2019-04-19T19:47:60"));
    }
}
