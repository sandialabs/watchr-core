/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.db;

public class DatabaseMetadata {
    
    ////////////
    // FIELDS //
    ////////////

    private int plotCount = 0;
    private int failedPlotCount = 0;

    /////////////
    // GETTERS //
    /////////////

    public int getPlotCount() {
        return plotCount;
    }

    public int getFailedPlotCount() {
        return failedPlotCount;
    }

    /////////////
    // SETTERS //
    /////////////

    public void setPlotCount(int plotCount) {
        this.plotCount = plotCount;
    }

    public void setFailedPlotCount(int failedPlotCount) {
        this.failedPlotCount = failedPlotCount;
    }
}
