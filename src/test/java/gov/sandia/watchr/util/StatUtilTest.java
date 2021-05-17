package gov.sandia.watchr.util;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Elliott Ridgway
 */
public class StatUtilTest {
    
    private final double[] dataset = {10, 2, 38, 23, 38, 23, 21};
    
    @Test
    public void testAvg() {
        Assert.assertEquals(22.142857, StatUtil.avg(dataset), 1.0e-4);
    }

    @Test
    public void testAvgZeroLength() {
        Assert.assertEquals(0.0, StatUtil.avg(new double[0]), 1.0e-4);
    }    
    
    @Test
    public void testStdDev() {
        Assert.assertEquals(12.298996, StatUtil.stdDev(dataset), 1.0e-4);
    }
}
