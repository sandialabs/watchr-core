/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.log;

import org.apache.log4j.Logger;

import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;

public class Log4jLogger implements ILogger {

    private static Logger log = Logger.getLogger(Log4jLogger.class.getName());

    @Override
    public void logInfo(String message) {
        log.info(message);
    }

    @Override
    public void logWarning(String message) {
        log.warn(message);
    }

    @Override
    public void logError(String error) {
        log.error(error);
    }

    @Override
    public void logError(String error, Throwable t) {
        log.error(error, t);
    }

    @Override
    public void log(WatchrConfigError errorObj) {
        ErrorLevel level = errorObj.getLevel();
        String time = errorObj.getTime();
        String message = errorObj.getMessage();

        if(level == ErrorLevel.INFO) {
            logInfo(time + ": " + message);
        } else if(level == ErrorLevel.WARNING) {
            logWarning(time + ": " + message);
        } else if(level == ErrorLevel.ERROR) {
            logError(time + ": " + message);
        }
    }
}
