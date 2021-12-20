/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;

public class Log4jLogger implements ILogger {

    ////////////
    // FIELDS //
    ////////////

    private static Logger log = LogManager.getRootLogger();
    private ErrorLevel loggingLevel = ErrorLevel.INFO;

    /////////
    // LOG //
    /////////

    @Override
    public void logDebug(String message) {
        if(loggingLevel.ordinal() <= ErrorLevel.DEBUG.ordinal()) {
            log.debug(message);
        }
    }

    @Override
    public void logInfo(String message) {
        if(loggingLevel.ordinal() <= ErrorLevel.INFO.ordinal()) {
            log.info(message);
        }
    }

    @Override
    public void logWarning(String message) {
        if(loggingLevel.ordinal() <= ErrorLevel.WARNING.ordinal()) {
            log.warn(message);
        }
    }

    @Override
    public void logError(String error) {
        if(loggingLevel.ordinal() <= ErrorLevel.ERROR.ordinal()) {
            log.error(error);
        }
    }

    @Override
    public void logError(String error, Throwable t) {
        if(loggingLevel.ordinal() <= ErrorLevel.ERROR.ordinal()) {
            log.error(error, t);
        }
    }

    @Override
    public void log(WatchrConfigError errorObj) {
        ErrorLevel level = errorObj.getLevel();
        String prefix = errorObj.getTime() + " [" + level.toString() + "] ";
        String message = errorObj.getMessage();
        int loggingOrdinal = loggingLevel.ordinal();

        if(level == ErrorLevel.DEBUG && loggingOrdinal <= ErrorLevel.DEBUG.ordinal()) {
            logDebug(prefix + ": " + message);
        } else if(level == ErrorLevel.INFO && loggingOrdinal <= ErrorLevel.INFO.ordinal()) {
            logInfo(prefix + ": " + message);
        } else if(level == ErrorLevel.WARNING && loggingOrdinal <= ErrorLevel.WARNING.ordinal()) {
            logWarning(prefix + ": " + message);
        } else if(level == ErrorLevel.ERROR && loggingOrdinal <= ErrorLevel.ERROR.ordinal()) {
            logError(prefix + ": " + message);
        }
    }

    /////////////
    // SETTERS //
    /////////////

    @Override
    public void setLoggingLevel(ErrorLevel loggingLevel) {
        this.loggingLevel = loggingLevel;
    }

    /////////////
    // GETTERS //
    /////////////

    @Override
    public ErrorLevel getLoggingLevel() {
        return loggingLevel;
    }
}
