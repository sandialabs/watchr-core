package gov.sandia.watchr.cmd.impl;

import java.io.IOException;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.cmd.WatchrCommand;
import gov.sandia.watchr.strategy.WatchrRunStrategy;

public class WatchrStartCommand extends WatchrCommand {

    @Override
    public void execute(Object parameter, WatchrCoreApp appReference) throws IOException {
        WatchrRunStrategy run = new WatchrRunStrategy();
        saveCachedRun(run);
    }
    
}
