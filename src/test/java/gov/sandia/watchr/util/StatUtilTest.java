package gov.sandia.watchr.util;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Elliott Ridgway
 */
public class StatUtilTest {
    
    private final double[] dataset = {10, 2, 38, 23, 38, 23, 21};
    private final double[] verySmallDataset = {
        5.26929e-05, 6.38035e-05, 5.10579e-05, 6.42592e-05, 5.40156e-05,
        5.58784e-05, 5.91227e-05, 5.35163e-05, 7.55462e-05, 5.83424e-05,
        5.66851e-05, 7.30123e-05, 5.00549e-05, 5.14673e-05, 5.34931e-05,
        6.38379e-05, 5.42619e-05, 7.29381e-05, 5.87058e-05, 5.67107e-05
    };
    
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

    @Test
    public void testAvg_VerySmallNumbers() {
        Assert.assertEquals(5.897011E-5, StatUtil.avg(verySmallDataset), 1.0e-7);
    }
}
