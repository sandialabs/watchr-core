package gov.sandia.watchr.cmd.impl;

import java.io.File;
import java.io.IOException;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.cmd.WatchrCommand;
import gov.sandia.watchr.strategy.WatchrRunStrategy;

public class WatchrPutDatabaseCommand extends WatchrCommand {

    @Override
    public void execute(Object parameter, WatchrCoreApp appReference) throws IOException {
        WatchrRunStrategy run = loadCachedRun();
        if(parameter instanceof String) {
            String dbDirStr = (String) parameter;
            File dbDir = new File(dbDirStr);
            run.setDatabaseDir(dbDir);
            saveCachedRun(run);
        } else {
            System.err.println("Unknown parameter type passed for Watchr database location.");
        }
    }
    
}
