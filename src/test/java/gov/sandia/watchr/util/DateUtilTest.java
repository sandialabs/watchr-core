package gov.sandia.watchr.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DateUtilTest {

    @Test
    public void testEpochTimeToTimestamp() {
        String date = DateUtil.epochTimeToTimestamp(1615501362450L);
        assertEquals("2021-03-11T15:22:42", date);
    }
}
