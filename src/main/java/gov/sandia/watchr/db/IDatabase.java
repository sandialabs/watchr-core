/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.db;

import java.util.List;
import java.util.Set;

import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.config.WatchrConfig;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.log.ILogger;

public interface IDatabase {

    /////////////
    // GETTERS //
    /////////////
    
    public List<PlotWindowModel> getAllPlots();
    public List<String> getAllPlotUUIDs();
    public Set<String> getCategories();
    public Set<PlotWindowModel> getChildren(PlotDatabaseSearchCriteria searchCriteria);
    public Set<PlotWindowModel> getChildren(PlotWindowModel parentPlot, String category);
    public int getChildrenCount(PlotDatabaseSearchCriteria searchCriteria);

    public DatabaseMetadata getMetadata();
    public Set<String> getFilenameCache();
    public GraphDisplayConfig getGraphDisplayConfig();
    public WatchrConfig getLastConfig();
    public PlotWindowModel getParent(PlotDatabaseSearchCriteria searchCriteria);
    public PlotWindowModel getRootPlot();
    public boolean hasSeenFile(String fileAbsPath);

    public PlotWindowModel searchPlot(PlotDatabaseSearchCriteria searchCriteria);
    public PlotWindowModel searchAndMakeNewIfMissing(PlotDatabaseSearchCriteria searchCriteria);

    public ILogger getLogger();
    public IFileReader getFileReader();
    public void setLogger(ILogger logger);
    public void setFileReader(IFileReader fileReader);

    /////////////
    // SETTERS //
    /////////////
    
    public void addFileToCache(String fileAbsPath);
    public void removeFileFromCache(String fileAbsPath);

    public PlotWindowModel createRootPlotIfMissing();
    public void addPlot(PlotWindowModel newPlot);
    public void updatePlot(PlotWindowModel plot, boolean replace);
    public void clearPlotCache();
    public void deletePlot(String plotUUID);
    public void setNickname(String plotUUID, String nickname); 
    public void deleteAll();
    public void setPlotsAsChildren(PlotWindowModel parent, List<PlotWindowModel> childPlots);
    public void setWatchrConfig(WatchrConfig watchrConfig);
    public void updateMetadata();
    
    ////////////////////
    // DISK LOAD/SAVE //
    ////////////////////

    public void loadState();
    public PlotWindowModel loadPlotUsingUUID(String uuid);
    public PlotWindowModel loadPlotUsingInnerFields(String name, String category);
    public PlotWindowModel loadRootPlot();
    public void saveState();
}
