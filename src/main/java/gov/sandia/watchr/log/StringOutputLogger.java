package gov.sandia.watchr.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.util.OsUtil;

public class StringOutputLogger implements ILogger {
    
    ////////////
    // FIELDS //
    ////////////

    private boolean stdOutEcho = false;
    private List<String> logContents = new ArrayList<>();
    private ErrorLevel loggingLevel = ErrorLevel.INFO;
    private List<String> loggableDebugClasses = new ArrayList<>();

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public StringOutputLogger() {
        loggableDebugClasses.add(WatchrCoreApp.class.getSimpleName());
        loggableDebugClasses.add(StringOutputLogger.class.getSimpleName());
    }

    /////////////
    // SETTERS //
    /////////////

    public void setStdOutEcho(boolean stdOutEcho) {
        this.stdOutEcho = stdOutEcho;
    }

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
        String loggingClass = e.getLoggingClass();
        int loggingOrdinal = loggingLevel.ordinal();

        if(level == ErrorLevel.DEBUG && loggingOrdinal <= ErrorLevel.DEBUG.ordinal()) {
            logDebug(prefix + ": " + message, loggingClass);
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
            if(stdOutEcho) {
                System.out.println(message);
            }
        }
    }

    @Override
    public void logError(String message, Throwable t) {
        if(loggingLevel.ordinal() <= ErrorLevel.ERROR.ordinal()) {
            StringBuilder sb = new StringBuilder();
            sb.append(message).append(OsUtil.getOSLineBreak());
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            sb.append(sw.toString());

            logContents.add(sb.toString());

            if(stdOutEcho) {
                System.out.println(message);
                t.printStackTrace();
            }
        }
    }

    @Override
    public void logInfo(String message) {
        if(loggingLevel.ordinal() <= ErrorLevel.INFO.ordinal()) {
            logContents.add(message);
            if(stdOutEcho) {
                System.out.println(message);
            }
        }
    }

    @Override
    public void logWarning(String message) {
        if(loggingLevel.ordinal() <= ErrorLevel.WARNING.ordinal()) {
            logContents.add(message);
            if(stdOutEcho) {
                System.out.println(message);
            }
        }
    }

    @Override
    public void logDebug(String message, String loggingClassName) {
        if(loggingLevel.ordinal() <= ErrorLevel.DEBUG.ordinal() &&
           loggableDebugClasses.contains(loggingClassName)) {

            logContents.add(message);
            if(stdOutEcho) {
                System.out.println(message);
            }
        }
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void setLoggingLevel(ErrorLevel loggingLevel) {
        this.loggingLevel = loggingLevel;
    }

    @Override
    public List<String> getLoggableDebugClasses() {
        return loggableDebugClasses;
    }
}
