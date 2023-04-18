package gov.sandia.watchr.cmd.impl;

import java.io.File;
import java.io.IOException;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.cmd.WatchrCommand;
import gov.sandia.watchr.strategy.WatchrRunStrategy;

public class WatchrConfigCommand extends WatchrCommand {

    @Override
    public void execute(Object parameter, WatchrCoreApp appReference) throws IOException {
        WatchrRunStrategy run = loadCachedRun();
        if(parameter instanceof String) {
            String configFileStr = (String) parameter;
            File configFile = new File(configFileStr);
            if(!configFile.exists()) {
                System.err.println("Config file " + configFileStr + " does not exist.");
            } else {
                run.setConfigFile(configFile);
                saveCachedRun(run);
            }
        } else {
            System.err.println("Unknown parameter type passed for Watchr config file.");
        }
    }
}
