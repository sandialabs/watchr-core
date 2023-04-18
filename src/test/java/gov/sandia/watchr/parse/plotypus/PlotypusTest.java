package gov.sandia.watchr.parse.plotypus;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import gov.sandia.watchr.config.WatchrConfig;
import gov.sandia.watchr.log.StringOutputLogger;

public class PlotypusTest {
    
    @Test
    public void testGetProblemStatus() {
        Plotypus<WatchrConfig> plotypus = new Plotypus<>(10, new StringOutputLogger());
        assertEquals("", plotypus.getProblemStatus());
    }

    @Test
    public void testInterruptTentacles() {
        Plotypus<WatchrConfig> plotypus = new Plotypus<>(10, new StringOutputLogger());
        assertEquals("", plotypus.interruptTentacles());
    }
}
