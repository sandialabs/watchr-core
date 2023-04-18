/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.db;

public class DatabaseMetadata {
    
    ////////////
    // FIELDS //
    ////////////

    public static final int HEALTH_MAX = 10;

    private int plotCount = 0;
    private int failedPlotCount = 0;
    private int newPlotCount = 0;
    private int health = HEALTH_MAX;

    /////////////
    // GETTERS //
    /////////////

    public int getPlotCount() {
        return plotCount;
    }

    public int getFailedPlotCount() {
        return failedPlotCount;
    }

    public int getNewPlotCount() {
        return newPlotCount;
    }

    public int getHealth() {
        return health;
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

    public void setNewPlotCount(int newPlotCount) {
        this.newPlotCount = newPlotCount;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
