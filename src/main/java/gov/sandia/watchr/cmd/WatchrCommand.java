package gov.sandia.watchr.cmd;

import java.io.File;
import java.io.IOException;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.strategy.WatchrRunStrategy;
import gov.sandia.watchr.strategy.WatchrRunStrategySerializer;

public abstract class WatchrCommand {
    
    public WatchrRunStrategy loadCachedRun() throws IOException {
        WatchrRunStrategySerializer watchrRunSerializer = new WatchrRunStrategySerializer();
        return watchrRunSerializer.load(new File(WatchrCoreApp.CACHED_RUN_DIRECTORY), WatchrCoreApp.CACHED_RUN_NAME);
    }

    public void saveCachedRun(WatchrRunStrategy run) throws IOException {
        File destDir = new File(WatchrCoreApp.CACHED_RUN_DIRECTORY);
        if(!destDir.exists()) {
            destDir.mkdirs();
        }

        WatchrRunStrategySerializer watchrRunSerializer = new WatchrRunStrategySerializer();
        watchrRunSerializer.save(destDir, WatchrCoreApp.CACHED_RUN_NAME, run);
    }

    public abstract void execute(Object parameter, WatchrCoreApp appReference) throws IOException;
}
