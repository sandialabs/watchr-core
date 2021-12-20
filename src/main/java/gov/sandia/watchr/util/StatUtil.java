/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.util;

/**
 * @author Elliott Ridgway
 */

public class StatUtil {

    private StatUtil() {}

    public static double avg(double[] arr) {
        double ret = 0.0;
        if(arr.length == 0) {
            return ret;
        }
        
        for(double value : arr) {
            ret += value;
        }
        ret = ret / (arr.length * 1.0);
        return ret;
    }

    public static double stdDev(double[] arr) {
        double mean = avg(arr);
        
        double standardDeviation = 0.0;
        for(double num : arr) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.sqrt(standardDeviation / arr.length);
    }
}
