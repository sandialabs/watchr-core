package gov.sandia.watchr.cmd.impl;

import java.io.File;
import java.io.IOException;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.cmd.WatchrCommand;
import gov.sandia.watchr.strategy.WatchrRunStrategySerializer;

public class WatchrStopCommand extends WatchrCommand {

    @Override
    public void execute(Object parameter, WatchrCoreApp appReference) throws IOException {
        WatchrRunStrategySerializer watchrRunSerializer = new WatchrRunStrategySerializer();
        watchrRunSerializer.delete(new File(WatchrCoreApp.CACHED_RUN_DIRECTORY), WatchrCoreApp.CACHED_RUN_NAME);
    }
    
}
