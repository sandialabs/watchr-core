package gov.sandia.watchr.util;

import static org.junit.Assert.assertEquals;

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
}
