/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.db.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.config.CategoryConfiguration;
import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.config.WatchrConfig;
import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.graph.chartreuse.PlotToken;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceChangeListener;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.util.CommonConstants;

public abstract class AbstractDatabase implements IDatabase {

    ////////////
    // FIELDS //
    ////////////

    protected WatchrConfig config;

    protected Set<String> filenameCache;
    protected Map<String, Set<String>> parentChildPlots;

    protected Set<PlotWindowModel> plots;
    protected Set<String> dirtyPlotUUIDs;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    protected AbstractDatabase() {
        this.config = new WatchrConfig();

        this.plots = new HashSet<>();
        this.dirtyPlotUUIDs = new HashSet<>();
        this.parentChildPlots = new HashMap<>();
        this.filenameCache = new HashSet<>();
    }

    /////////////
    // GETTERS //
    /////////////

    @Override
    public PlotWindowModel getPlot(String plotName, String category) {
        if(StringUtils.isNotBlank(plotName)) {
            for(PlotWindowModel windowModel : plots) {
                boolean categoryMatch =
                    (StringUtils.isBlank(category) ||
                    StringUtils.isBlank(windowModel.getCategory()) ||
                    windowModel.getCategory().toLowerCase().matches(category.toLowerCase())); // Case-insensitive matching
                if(windowModel.getName().matches(plotName) && categoryMatch) {
                    return windowModel;
                }
            }
        }
        return null;
    }    

    @Override
    public Set<PlotWindowModel> getPlots(String plotName, String category) {
        plotName = plotName.replace("*", ".*");
        category = category.replace("*", ".*");

        Set<PlotWindowModel> searchResults = new HashSet<>();
        if(StringUtils.isNotBlank(plotName)) {
            for(PlotWindowModel windowModel : plots) {
                if(windowModel.getName().matches(plotName) &&
                   windowModel.getCategory().toLowerCase().matches(category.toLowerCase())) {
                    searchResults.add(windowModel);
                }
            }
        } else {
            PlotWindowModel rootPlot = getRootPlot();
            if(rootPlot != null) {
                searchResults.add(rootPlot);
            }
        }
        return searchResults;
    }

    @Override
    public Set<PlotWindowModel> getAllPlots() {
        return plots;
    }

    @Override
    public PlotWindowModel getRootPlot() {
        List<PlotWindowModel> plotsList = new ArrayList<>(plots);
        for(PlotWindowModel plot : plotsList) {
            if(plot.isRoot()) {
                return plot;
            }
        }
        return null;
    }    

    @Override
    public Set<PlotWindowModel> getChildren(PlotWindowModel parentPlot, String category) {
        UUID uuid = parentPlot.getUUID();
        String uuidStr = uuid.toString();

        Set<String> childUUIDs = new HashSet<>(parentChildPlots.getOrDefault(uuidStr, new HashSet<>()));
        Set<PlotWindowModel> childPlots = new HashSet<>();
        for(String childUUID : childUUIDs) {
            PlotWindowModel childPlot = getPlotByUUID(childUUID);
            if(childPlot != null) {
                boolean categoryMatch =
                    (StringUtils.isBlank(category) ||
                     StringUtils.isBlank(childPlot.getCategory()) ||
                     childPlot.getCategory().toLowerCase().matches(category.toLowerCase())); // Case-insensitive matching
                if(categoryMatch) {
                    if(childPlot.isEmpty2D()) {
                        childPlots.addAll(getChildren(childPlot, category));
                    } else {
                        childPlots.add(childPlot);
                    }
                }
            }
        }
        return childPlots;
    }

    @Override
    public Set<PlotWindowModel> getChildren(String parentPlotName, String category) {
        if(parentPlotName.equals(CommonConstants.ROOT_PATH_ALIAS) || parentPlotName.isEmpty()) {
            PlotWindowModel rootPlot = getRootPlot();
            if(rootPlot != null) {
                return getChildren(rootPlot, category);
            }
        }

        PlotWindowModel parentWindowModel = getPlot(parentPlotName, category);
        if(parentWindowModel != null) {
            return getChildren(parentWindowModel, category);
        }
        return new HashSet<>();
    }

    @Override
    public PlotWindowModel getParent(String plotName, String category) {
        PlotWindowModel childPlot = getPlot(plotName, category);
        if(childPlot != null) {
            for(Entry<String, Set<String>> entry : parentChildPlots.entrySet()) {
                String parentUUID = entry.getKey();
                Set<String> children = new HashSet<>();
                children.addAll(entry.getValue());

                if(children.contains(childPlot.getUUID().toString())) {
                    for(PlotWindowModel parentWindowModel : plots) {
                        if(parentWindowModel.getUUID().toString().equals(parentUUID)) {
                            return parentWindowModel;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Set<String> getCategories() {
        CategoryConfiguration categoryConfiguration = config.getPlotsConfig().getCategoryConfig();
        return categoryConfiguration.getCategories();
    }

    @Override
    public Set<String> getFilenameCache() {
        return filenameCache;
    }

    @Override
    public GraphDisplayConfig getGraphDisplayConfig() {
        return config.getGraphDisplayConfig();
    }

    @Override
    public WatchrConfig getLastConfig() {
        return config;
    }    

    @Override
    public boolean hasSeenFile(File file) {
        return filenameCache.contains(file.getName());
    }    

    /////////////
    // SETTERS //
    /////////////

    @Override
    public void addPlot(PlotWindowModel newPlot) {
        if(newPlot.isRoot() && getRootPlot() != null) {
            throw new IllegalStateException("Database cannot contain two root plots.");
        }

        plots.add(newPlot);
        dirtyPlotUUIDs.add(newPlot.getUUID().toString());
    }    

    @Override
    public void addChildPlots(PlotWindowModel parent, List<PlotWindowModel> newChildPlots) {
        Set<String> childPlotUUIDs = new HashSet<>(parentChildPlots.getOrDefault(parent.getUUID().toString(), new HashSet<>()));
        for(PlotWindowModel childPlot : newChildPlots) {
            if(childPlot != null) {
                childPlotUUIDs.add(childPlot.getUUID().toString());
            }
        }
        parentChildPlots.put(parent.getUUID().toString(), childPlotUUIDs);
    }    

    @Override
    public void deletePlot(PlotWindowModel plotToDelete) {
        ILogger logger = WatchrCoreApp.getInstance().getLogger();
        logger.logInfo("Preparing to delete plot " + plotToDelete.getName());
        logger.logInfo("First, delete any child plots...");
        String plotToDeleteUUIDStr = plotToDelete.getUUID().toString();

        Set<String> childPlotUUIDs = new HashSet<>(parentChildPlots.getOrDefault(plotToDeleteUUIDStr, new HashSet<>()));
        for(String childPlotUUID : childPlotUUIDs) {
            PlotWindowModel childPlot = getPlotByUUID(childPlotUUID);
            if(childPlot != null) {
                logger.logInfo("Deleting plot " + childPlot.getName());
                deletePlot(childPlot);
            } else {
                logger.logWarning("Couldn't find plot by UUID " + childPlotUUID);
            }
        }
        logger.logInfo("Removing plot " + plotToDelete.getName() + " from database lookup files...");

        parentChildPlots.remove(plotToDeleteUUIDStr);
        String rootPlotUUIDStr = getRootPlot().getUUID().toString();
        Set<String> rootPlots = parentChildPlots.get(rootPlotUUIDStr);
        if(rootPlots != null && rootPlots.contains(plotToDeleteUUIDStr)) {
            logger.logInfo("Also removing plot from set of root plots...");
            rootPlots.remove(plotToDeleteUUIDStr);
            parentChildPlots.put(rootPlotUUIDStr, rootPlots);
        }

        plots.remove(plotToDelete);
    } 

    @Override
    public void addFileToCache(File file) {
        filenameCache.add(file.getName());
    }    

    @Override
    public void setWatchrConfig(WatchrConfig config) {
        this.config = config;
    }

    ///////////////
    // PROTECTED //
    ///////////////

    protected PlotWindowModel getPlotByUUID(String uuid) {
        List<PlotWindowModel> plotsList = Arrays.asList(plots.toArray(new PlotWindowModel[plots.size()]));
        for(int i = 0; i < plotsList.size(); i++) {
            PlotWindowModel plot = plotsList.get(i);
            if(plot.getUUID().toString().equals(uuid)) {
                return plot;
            }
        }
        return null;
    }

    protected void setListeners(PlotWindowModel windowModel) {
        for(PlotCanvasModel canvasModel : windowModel.getCanvasModels()) {
            for(PlotTraceModel traceModel : canvasModel.getTraceModels()) {
                traceModel.addListener(new PlotTraceChangeListener(){
                    @Override
                    public void changed() {
                        dirtyPlotUUIDs.add(windowModel.getUUID().toString());
                    }

                    @Override
                    public void propertyChanged(PlotToken property) {
                        dirtyPlotUUIDs.add(windowModel.getUUID().toString());
                    }
                });
            }
        }
    }
}
