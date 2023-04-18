package gov.sandia.watchr.cmd.impl;

import java.io.IOException;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.cmd.WatchrCommand;
import gov.sandia.watchr.strategy.WatchrRunStrategy;

public class WatchrRunCommand extends WatchrCommand {

    @Override
    public void execute(Object parameter, WatchrCoreApp app) throws IOException {
        WatchrRunStrategy run = loadCachedRun();
        run.run(app);
        saveCachedRun(run);
    }
}
