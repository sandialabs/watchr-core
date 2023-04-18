/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config;

import gov.sandia.watchr.util.DateUtil;

public class WatchrConfigError {
    
    ////////////
    // FIELDS //
    ////////////

    public enum ErrorLevel {
        DEBUG,               // The finest level of granularity for debugging. Users must also separately
                             // specify which Java classes they want to debug, since this option produces
                             // so much output.
        INFO,                // Information for the user. Information does not have a negative connotation.
        WARNING,             // Warnings are non-critical problems that Watchr can easily continue from.   
        ERROR;               // Watchr errors and Java stacktraces. Watchr will do its best to recover from an error.
    }
    private final long time;
    private final ErrorLevel level;
    private final String message;
    private String loggingClass;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public WatchrConfigError(ErrorLevel level, String message, String loggingClass) {
        this.time = System.currentTimeMillis();
        this.level = level;
        this.message = message;
        this.loggingClass = loggingClass;
    }

    public WatchrConfigError(ErrorLevel level, String message) {
        this(level, message, "");
    }

    /////////////
    // GETTERS //
    /////////////

    public String getTime() {
        return DateUtil.epochTimeToTimestamp(time);
    }

    public ErrorLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public String getLoggingClass() {
        return loggingClass;
    }
}