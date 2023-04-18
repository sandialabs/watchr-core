package gov.sandia.watchr.cmd.impl;

import java.io.File;
import java.io.IOException;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.cmd.WatchrCommand;
import gov.sandia.watchr.strategy.WatchrRunStrategy;

public class WatchrAddCommand extends WatchrCommand {

    @Override
    public void execute(Object parameter, WatchrCoreApp appReference) throws IOException {
        WatchrRunStrategy run = loadCachedRun();
        if(parameter instanceof String) {
            String dataFileStr = (String) parameter;
            File dataFile = new File(dataFileStr);
            if(!dataFile.exists()) {
                System.err.println("Data file/folder " + dataFileStr + " does not exist.");
            } else {
                run.setDataFile(dataFile);
                saveCachedRun(run);
            }
        } else {
            System.err.println("Unknown parameter type passed for data file location.");
        }
    }
    
}
