/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
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
        DEBUG,
        INFO,
        WARNING,
        ERROR;
    }
    private final long time;
    private final ErrorLevel level;
    private final String message;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public WatchrConfigError(ErrorLevel level, String message) {
        this.time = System.currentTimeMillis();
        this.level = level;
        this.message = message;
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
}