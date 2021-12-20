/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.db.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.config.CategoryConfiguration;
import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.config.WatchrConfig;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.db.DatabaseMetadata;
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
    protected DatabaseMetadata metadata;

    protected Set<String> filenameCache;
    protected Map<String, Set<String>> parentChildPlots;

    protected List<PlotWindowModel> plots;
    protected Set<String> dirtyPlotUUIDs;
    protected Object plotMonitor = new Object();

    protected ILogger logger;
    protected IFileReader fileReader;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    protected AbstractDatabase() {
        this(null, null);
    }

    protected AbstractDatabase(ILogger logger, IFileReader fileReader) {
        this.config = new WatchrConfig(logger, fileReader);
        this.metadata = new DatabaseMetadata();

        this.plots = new ArrayList<>();
        this.dirtyPlotUUIDs = new HashSet<>();
        this.parentChildPlots = new HashMap<>();
        this.filenameCache = new HashSet<>();

        setLogger(logger);
        setFileReader(fileReader);
    }

    /////////////
    // GETTERS //
    /////////////

    @Override
    public PlotWindowModel getRootPlot() {
        synchronized(plotMonitor) {
            for(int i = 0; i < plots.size(); i++) {
                PlotWindowModel plot = plots.get(i);
                if(plot.isRoot()) {
                    return plot;
                }
            }
        }
        return null;
    }   

    @Override
    public List<PlotWindowModel> getAllPlots() {
        List<PlotWindowModel> plotsCopy = new ArrayList<>();
        synchronized(plotMonitor) {
            for(int i = 0; i < plots.size(); i++) {
                PlotWindowModel plot = plots.get(i);
                plotsCopy.add(plot);
            }
        }
        return plotsCopy;
    }

    @Override
    public Set<PlotWindowModel> getChildren(PlotWindowModel parentPlot, String category) {
        UUID uuid = parentPlot.getUUID();
        String uuidStr = uuid.toString();

        Set<String> childUUIDs = new HashSet<>(parentChildPlots.getOrDefault(uuidStr, new HashSet<>()));
        Set<PlotWindowModel> childPlots = new HashSet<>();
        for(String childUUID : childUUIDs) {
            PlotWindowModel childPlot = getPlotByUUID(childUUID);
            if(childPlot == null) {
                childPlot = loadPlotUsingUUID(childUUID);
            }

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

        PlotWindowModel parentWindowModel = searchPlot(parentPlotName, category);
        if(parentWindowModel != null) {
            return getChildren(parentWindowModel, category);
        }
        return new HashSet<>();
    }

    @Override
    public PlotWindowModel getParent(String plotName, String category) {
        PlotWindowModel childPlot = searchPlot(plotName, category);
        if(childPlot != null) {
            for(Entry<String, Set<String>> entry : parentChildPlots.entrySet()) {
                String parentUUID = entry.getKey();
                Set<String> children = new HashSet<>();
                children.addAll(entry.getValue());

                if(children.contains(childPlot.getUUID().toString())) {
                    for(int i = 0; i < plots.size(); i++) {
                        PlotWindowModel plot = plots.get(i);
                        if(plot.getUUID().toString().equals(parentUUID)) {
                            return plot;
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
        return Collections.unmodifiableSet(filenameCache);
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
    public boolean hasSeenFile(String fileAbsPath) {
        return filenameCache.contains(fileReader.getName(fileAbsPath));
    }

    @Override
    public DatabaseMetadata getMetadata() {
        return metadata;
    }

    @Override
    public ILogger getLogger() {
        return logger;
    }

    @Override
    public IFileReader getFileReader() {
        return fileReader;
    }

    ////////////
    // SEARCH //
    ////////////

    @Override
    public PlotWindowModel searchPlot(String plotName, String category) {
        logger.logDebug("AbstractDatabase.searchPlot()");
        logger.logDebug("Name = \"" + plotName + "\", Category = \"" + category + "\".");
        PlotWindowModel returnedWindowModel = null;
        if(StringUtils.isNotBlank(plotName)) {
            logger.logDebug("Look in loaded plot cache first...");
            synchronized(plotMonitor) {
                for(int i = 0; i < plots.size(); i++) {
                    PlotWindowModel plot = plots.get(i);
                    boolean categoryMatch =
                        (StringUtils.isBlank(category) ||
                        StringUtils.isBlank(plot.getCategory()) ||
                        plot.getCategory().toLowerCase().matches(category.toLowerCase())); // Case-insensitive matching

                    if(plot.getName().equals(plotName) && categoryMatch) {
                        logger.logDebug("Found plot with name " + plotName + ", and category matched.");
                        returnedWindowModel = plot;
                        break;
                    }
                }
            }
        }
        if(returnedWindowModel == null) {
            logger.logDebug("Could not find plot in cache, so load it from disk...");
            returnedWindowModel = loadPlotUsingInnerFields(plotName, category);
        }

        return returnedWindowModel;
    }

    /////////////
    // SETTERS //
    /////////////

    @Override
    public void addPlot(PlotWindowModel newPlot) {
        synchronized(plotMonitor) {
            boolean alreadyContains = false;
            for(int i = 0; i < plots.size(); i++) {
                PlotWindowModel checkPlot = plots.get(i);
                if(checkPlot.equals(newPlot) || (newPlot.isRoot() && getRootPlot() != null)) {
                    alreadyContains = true;
                    break;
                }
            }

            if(!alreadyContains) {
                plots.add(newPlot);
            }
        }
        dirtyPlotUUIDs.add(newPlot.getUUID().toString());
    }    

    @Override
    public void setPlotsAsChildren(PlotWindowModel parent, List<PlotWindowModel> newChildPlots) {
        Set<String> childPlotUUIDs = new HashSet<>(parentChildPlots.getOrDefault(parent.getUUID().toString(), new HashSet<>()));
        for(PlotWindowModel childPlot : newChildPlots) {
            if(childPlot != null) {
                childPlotUUIDs.add(childPlot.getUUID().toString());
            }
        }
        parentChildPlots.put(parent.getUUID().toString(), childPlotUUIDs);
    }

    @Override
    public void updatePlot(PlotWindowModel changedPlot) {
        synchronized(plotMonitor) {
            PlotWindowModel foundOriginalPlot = null;
            for(int i = 0; i < plots.size(); i++) {
                PlotWindowModel plot = plots.get(i);
                if(plot.getUUID().equals(changedPlot.getUUID())) {
                    foundOriginalPlot = plot;
                    break;
                }
            }

            if(foundOriginalPlot != null) {
                plots.remove(foundOriginalPlot);
                plots.add(changedPlot);
            } else {
                logger.logError(
                    "Tried to update plot in database (UUID " + changedPlot.getUUID() +
                    ", name \"" + changedPlot.getName() + "\") but could not find original copy of plot.");
            }
        }
    }

    @Override
    public void deletePlot(PlotWindowModel plotToDelete) {
        logger.logDebug("Preparing to delete plot " + plotToDelete.getName());
        logger.logDebug("First, delete any child plots...");
        String plotToDeleteUUIDStr = plotToDelete.getUUID().toString();

        Collection<String> childPlotUUIDs = parentChildPlots.getOrDefault(plotToDeleteUUIDStr, new HashSet<>());
        for(String childPlotUUID : childPlotUUIDs) {
            PlotWindowModel childPlot = getPlotByUUID(childPlotUUID);
            if(childPlot != null) {
                logger.logDebug("Deleting plot " + childPlot.getName());
                deletePlot(childPlot);
            } else {
                logger.logWarning("Couldn't find plot by UUID " + childPlotUUID);
            }
        }
        logger.logDebug("Removing plot " + plotToDelete.getName() + " from database lookup files...");

        parentChildPlots.remove(plotToDeleteUUIDStr);
        dirtyPlotUUIDs.remove(plotToDeleteUUIDStr);
        String rootPlotUUIDStr = getRootPlot().getUUID().toString();
        Collection<String> result = parentChildPlots.get(rootPlotUUIDStr);
        if(result != null) {
            Set<String> rootPlots = new HashSet<>(result);
            if(rootPlots.contains(plotToDeleteUUIDStr)) {
                logger.logDebug("Also removing plot from set of root plots...");
                rootPlots.remove(plotToDeleteUUIDStr);
                parentChildPlots.put(rootPlotUUIDStr, rootPlots);
            }
        }

        synchronized(plotMonitor) {
            plots.remove(plotToDelete);
        }
    } 

    @Override
    public void addFileToCache(String fileAbsPath) {
        filenameCache.add(fileReader.getName(fileAbsPath));
    }    

    @Override
    public void setWatchrConfig(WatchrConfig config) {
        this.config = config;
    }

    @Override
    public void updateMetadata() {
        synchronized(plotMonitor) {
            int failedCount = 0;
            int plotCount = plots.size() - 1; // Don't count root plot

            for(int i = 0; i < plots.size(); i++) {
                PlotWindowModel plot = plots.get(i);
                if(plot.isFailing()) {
                    failedCount++;
                }
            }
            metadata.setPlotCount(plotCount);
            metadata.setFailedPlotCount(failedCount);
        }
    }

    @Override
    public void setLogger(ILogger logger) {
        this.logger = logger;
    }

    @Override
    public void setFileReader(IFileReader fileReader) {
        this.fileReader = fileReader;
    }

    ///////////////
    // PROTECTED //
    ///////////////

    protected PlotWindowModel getPlotByUUID(String uuid) {
        synchronized(plotMonitor) {
            for(int i = 0; i < plots.size(); i++) {
                PlotWindowModel plot = plots.get(i);
                if(plot.getUUID().toString().equals(uuid)) {
                    return plot;
                }
            }
        }
        return null;
    }

    public void clearPlotCache() {
        synchronized(plotMonitor) {
            plots.clear();
        }
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
