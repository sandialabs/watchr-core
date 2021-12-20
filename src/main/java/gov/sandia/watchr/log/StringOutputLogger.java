package gov.sandia.watchr.log;

import java.util.ArrayList;
import java.util.List;

import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.util.OsUtil;

public class StringOutputLogger implements ILogger {
    
    ////////////
    // FIELDS //
    ////////////

    private List<String> logContents = new ArrayList<>();
    private ErrorLevel loggingLevel = ErrorLevel.INFO;

    /////////////
    // GETTERS //
    /////////////

    public String getLogAsString() {
        StringBuilder sb = new StringBuilder();
        for(String logMessage : logContents) {
            sb.append(logMessage).append(OsUtil.getOSLineBreak());
        }
        return sb.toString();
    }

    public List<String> getLog() {
        return logContents;
    }

    @Override
    public ErrorLevel getLoggingLevel() {
        return loggingLevel;
    }

    /////////
    // LOG //
    /////////

    @Override
    public void log(WatchrConfigError e) {
        ErrorLevel level = e.getLevel();
        String prefix = e.getTime() + " [" + level.toString() + "] ";
        String message = e.getMessage();
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

    @Override
    public void logError(String message) {
        if(loggingLevel.ordinal() <= ErrorLevel.ERROR.ordinal()) {
            logContents.add(message);
        }
    }

    @Override
    public void logError(String message, Throwable t) {
        if(loggingLevel.ordinal() <= ErrorLevel.ERROR.ordinal()) {
            StringBuilder sb = new StringBuilder();
            sb.append(message).append(OsUtil.getOSLineBreak());
            sb.append(t.getMessage());
            logContents.add(sb.toString());
        }
    }

    @Override
    public void logInfo(String message) {
        if(loggingLevel.ordinal() <= ErrorLevel.INFO.ordinal()) {
            logContents.add(message);
        }
    }

    @Override
    public void logWarning(String message) {
        if(loggingLevel.ordinal() <= ErrorLevel.WARNING.ordinal()) {
            logContents.add(message);
        }
    }

    @Override
    public void logDebug(String message) {
        if(loggingLevel.ordinal() <= ErrorLevel.DEBUG.ordinal()) {
            logContents.add(message);
        }
    }

    /////////////
    // SETTERS //
    /////////////

    @Override
    public void setLoggingLevel(ErrorLevel loggingLevel) {
        this.loggingLevel = loggingLevel;
    }
}
