/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.db;

import java.io.File;
import java.util.List;
import java.util.Set;

import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.config.WatchrConfig;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;

public interface IDatabase {   
    
    public Set<PlotWindowModel> getAllPlots();
    public Set<String> getCategories();
    public Set<PlotWindowModel> getChildren(String parentPlotName, String parentCategory);
    public Set<PlotWindowModel> getChildren(PlotWindowModel parentPlot, String category);
    public Set<String> getFilenameCache();
    public GraphDisplayConfig getGraphDisplayConfig();
    public WatchrConfig getLastConfig();
    public PlotWindowModel getParent(String plotName, String category);
    public PlotWindowModel getPlot(String plotName, String category);
    public Set<PlotWindowModel> getPlots(String plotName, String category);
    public PlotWindowModel getRootPlot();
    public boolean hasSeenFile(File file);
    
    public void loadState();
    public void saveState();
    
    public void addChildPlots(PlotWindowModel parent, List<PlotWindowModel> childPlots);
    public void addFileToCache(File file);
    public void addPlot(PlotWindowModel newPlot);
    public void deletePlot(PlotWindowModel newPlot);
    public void setWatchrConfig(WatchrConfig watchrConfig);
    
}
