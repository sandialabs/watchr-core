/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse;

public class WatchrParseException extends Exception {

    ////////////
    // FIELDS //
    ////////////
    
    private static final long serialVersionUID = -1139888594035481113L;
    private final Exception originalException;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public WatchrParseException(Exception originalException) {
        this.originalException = originalException;
    }

    /////////////
    // GETTERS //
    /////////////

    public Exception getOriginalException() {
        return originalException;
    }
}
