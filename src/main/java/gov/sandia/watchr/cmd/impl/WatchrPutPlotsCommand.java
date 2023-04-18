package gov.sandia.watchr.cmd.impl;

import java.io.File;
import java.io.IOException;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.cmd.WatchrCommand;
import gov.sandia.watchr.strategy.WatchrRunStrategy;

public class WatchrPutPlotsCommand extends WatchrCommand {

    @Override
    public void execute(Object parameter, WatchrCoreApp appReference) throws IOException {
        WatchrRunStrategy run = loadCachedRun();
        if(parameter instanceof String) {
            String graphDirStr = (String) parameter;
            File graphDir = new File(graphDirStr);
            run.setExportDir(graphDir);
            saveCachedRun(run);
        } else {
            System.err.println("Unknown parameter type passed for Watchr graph export location.");
        }
    }
    
}
