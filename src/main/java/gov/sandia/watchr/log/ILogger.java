/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.log;

import gov.sandia.watchr.config.WatchrConfigError;

public interface ILogger {

    public void log(WatchrConfigError errorObj);
    
    public void logInfo(String message);

    public void logWarning(String message);

    public void logError(String error);

    public void logError(String error, Throwable t);
}
