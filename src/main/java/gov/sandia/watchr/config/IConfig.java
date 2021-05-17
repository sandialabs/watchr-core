/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config;

import java.util.List;

import gov.sandia.watchr.config.diff.WatchrDiff;

public interface IConfig {
    
    public void validate();

    public String getConfigPath();

    public List<WatchrDiff<?>> diff(IConfig other);
}
