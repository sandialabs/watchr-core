/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.db.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.config.CategoryConfiguration;
import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.config.NameConfig;
import gov.sandia.watchr.config.WatchrConfig;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.db.DatabaseMetadata;
import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.db.NewPlotDatabaseSearchCriteria;
import gov.sandia.watchr.db.PlotDatabaseSearchCriteria;
import gov.sandia.watchr.graph.chartreuse.PlotToken;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceChangeListener;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.generators.line.extractors.ExtractionResult;
import gov.sandia.watchr.parse.generators.line.extractors.ExtractionResultNameResolver;
import gov.sandia.watchr.parse.generators.rule.RuleApplyable;
import gov.sandia.watchr.parse.generators.rule.RuleTarget;
import gov.sandia.watchr.util.CommonConstants;
import gov.sandia.watchr.WatchrCoreAppDatabaseSubsystem;

public abstract class AbstractDatabase implements IDatabase, RuleApplyable {

    ////////////
    // FIELDS //
    ////////////

    private static final String CLASSNAME = AbstractDatabase.class.getSimpleName();

    protected WatchrConfig config;
    protected DatabaseMetadata metadata;

    protected Set<String> filenameCache;
    protected Map<String, Set<String>> parentChildPlots;

    protected List<PlotWindowModel> plots;
    protected Set<String> dirtyPlotUUIDs;

    protected Object plotMonitor = new Object();
    protected Object rootPlotMonitor = new Object();

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
                if(!plot.isRoot()) {
                    plotsCopy.add(plot);
                }
            }
        }
        return plotsCopy;
    }

    @Override
    public List<String> getAllPlotUUIDs() {
        List<String> plotsUUIDList = new ArrayList<>();
        synchronized(plotMonitor) {
            for(int i = 0; i < plots.size(); i++) {
                PlotWindowModel plot = plots.get(i);
                if(!plot.isRoot()) {
                    plotsUUIDList.add(plot.getUUID().toString());
                }
            }
        }
        return plotsUUIDList;
    }

    @Override
    public Set<PlotWindowModel> getChildren(PlotWindowModel parentPlot, String category) {
        logger.logDebug("getChildren(PlotWindowModel, String)", CLASSNAME);
        logger.logDebug("Parent Plot:" + parentPlot.getName(), CLASSNAME);
        UUID uuid = parentPlot.getUUID();
        String uuidStr = uuid.toString();
        logger.logDebug("Parent Plot UUID:" + parentPlot.getUUID().toString(), CLASSNAME);

        Set<String> childUUIDsSet = new LinkedHashSet<>();
        if(parentChildPlots.get(uuidStr) != null) {
            childUUIDsSet.addAll(parentChildPlots.get(uuidStr));
        }
        List<String> childUUIDs = new ArrayList<>();
        childUUIDs.addAll(childUUIDsSet);

        logger.logDebug("childUUIDsSet size:" + childUUIDs.size(), CLASSNAME);
        Set<PlotWindowModel> childPlots = new HashSet<>();
        for(int i = 0; i < childUUIDs.size(); i++) {
            String childUUID = childUUIDs.get(i);
            PlotWindowModel childPlot = getPlotByUUID(childUUID);
            if(childPlot != null) {
                logger.logDebug("Loaded child plot: " + childPlot.getName() + ", " + childPlot.getCategory(), CLASSNAME);
                boolean categoryMatch =
                    (StringUtils.isBlank(category) ||
                     StringUtils.isBlank(childPlot.getCategory()) ||
                     category.equalsIgnoreCase(WatchrCoreAppDatabaseSubsystem.ALLCATEGORIES) || // all categories handler
                     childPlot.getCategory().toLowerCase().matches(WatchrCoreAppDatabaseSubsystem.ALLCATEGORIES) || // all categories handler
                     childPlot.getCategory().toLowerCase().matches(category.toLowerCase())); // Case-insensitive matching
                if(categoryMatch) {
                    logger.logDebug("Category match", CLASSNAME);
                    
                    if(childPlot.isEmpty2D()) {
                        logger.logDebug("Loading children instead", CLASSNAME);
                        List<PlotWindowModel> returnedChildren =
                            new ArrayList<>(getChildren(childPlot, category));
                        childPlots.addAll(returnedChildren);
                    } else {
                        logger.logDebug("Adding child " + childPlot.getName() + " to set.", CLASSNAME);
                        childPlots.add(childPlot);
                    }
                }
            } else {
                logger.logDebug("Child plot was ultimately null!", CLASSNAME);
            }
        }
        logger.logDebug("Found:" + childPlots.size(), CLASSNAME);
        return childPlots;
    }

    @Override
    public Set<PlotWindowModel> getChildren(PlotDatabaseSearchCriteria searchCriteria) {
        String plotName = searchCriteria.getName();
        String category = searchCriteria.getCategory();

        if(plotName.equals(CommonConstants.ROOT_PATH_ALIAS) || plotName.isEmpty()) {
            PlotWindowModel rootPlot = getRootPlot();
            if(rootPlot != null) {
                return getChildren(rootPlot, category);
            }
        }
        PlotWindowModel parentWindowModel = searchPlot(searchCriteria, false);
        if(parentWindowModel != null) {
            return getChildren(parentWindowModel, category);
        }
        return new HashSet<>();
    }

    @Override
    public int getChildrenCount(PlotDatabaseSearchCriteria searchCriteria) {
        String plotName = searchCriteria.getName();

        PlotWindowModel parentWindowModel = null;
        if(plotName.equals(CommonConstants.ROOT_PATH_ALIAS) || plotName.isEmpty()) {
            parentWindowModel = getRootPlot();
        } else {
            parentWindowModel = searchPlot(searchCriteria, false);
        }

        if(parentWindowModel != null) {
            String uuidStr = parentWindowModel.getUUID().toString();
            Set<String> childUUIDs = new HashSet<>(parentChildPlots.getOrDefault(uuidStr, new HashSet<>()));
            return childUUIDs.size();
        }
        return 0;
    }

    @Override
    public PlotWindowModel getParent(PlotDatabaseSearchCriteria searchCriteria) {
        PlotWindowModel childPlot = searchPlot(searchCriteria, false);
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
    public PlotWindowModel searchPlot(PlotDatabaseSearchCriteria searchCriteria) {
        return searchPlot(searchCriteria, false);
    }

    @Override
    public PlotWindowModel searchAndMakeNewIfMissing(PlotDatabaseSearchCriteria searchCriteria) {
        return searchPlot(searchCriteria, true);
    }

    /////////////
    // SETTERS //    
    /////////////

    @Override
    public PlotWindowModel createRootPlotIfMissing() {
        logger.logDebug("createRootPlotIfMissing()", CLASSNAME);
        synchronized(rootPlotMonitor) {
            PlotWindowModel rootPlot = getRootPlot();
            if(rootPlot == null) {
                logger.logDebug("Root plot is null, adding a new root.", CLASSNAME);
                rootPlot = new PlotWindowModel(CommonConstants.ROOT_PATH_ALIAS);
                addPlot(rootPlot);
            }
            return rootPlot;
        }
    }

    @Override
    public void addPlot(PlotWindowModel newPlot) {
        logger.logDebug("addPlot()", CLASSNAME);
        logger.logDebug("Adding new plot " + newPlot.getName(), CLASSNAME);
        synchronized(plotMonitor) {
            boolean alreadyContains = false;
            for(int i = 0; i < plots.size(); i++) {
                PlotWindowModel checkPlot = plots.get(i);
                if((checkPlot != null && checkPlot.equals(newPlot)) || (newPlot.isRoot() && getRootPlot() != null)) {
                    alreadyContains = true;
                    break;
                }
            }

            if(!alreadyContains) {
                plots.add(newPlot);
                if(!dirtyPlotUUIDs.contains(newPlot.getUUID().toString())) {
                    dirtyPlotUUIDs.add(newPlot.getUUID().toString());
                }
            }
        }
    }    

    @Override
    public void setPlotsAsChildren(PlotWindowModel parent, List<PlotWindowModel> newChildPlots) {
        logger.logDebug("setPlotsAsChildren(PlotWindowModel, List<PlotWindowModel>)", CLASSNAME);
        List<PlotWindowModel> childPlotsCopyList = new ArrayList<>(newChildPlots);

        // This method guarantees that child plots are sorted first by category and then by name.
        Collections.sort(childPlotsCopyList, (p1, p2) -> {
            if(p1 != null && p2 != null) {
                if(p1.getCategory().equals(p2.getCategory())) {
                    return p1.getName().compareTo(p2.getName());
                } else {
                    return p1.getCategory().compareTo(p2.getCategory());
                }
            } else {
                return 0;
            }
        });

        synchronized(plotMonitor) {
            Set<String> finalChildPlotUUIDsSet = new LinkedHashSet<>();
            finalChildPlotUUIDsSet.addAll(
                parentChildPlots.getOrDefault(parent.getUUID().toString(), new HashSet<>()));

            for(PlotWindowModel childPlot : childPlotsCopyList) {
                if(childPlot != null) {
                    finalChildPlotUUIDsSet.add(childPlot.getUUID().toString());
                }
            }
            logger.logDebug("Adding " + parent.getName() + " (UUID " + parent.getUUID().toString() + ") to parentChildPlots", CLASSNAME);
            logger.logDebug("Adding " + finalChildPlotUUIDsSet.size() + " children to this UUID.", CLASSNAME);
            parentChildPlots.put(parent.getUUID().toString(), finalChildPlotUUIDsSet);
        }
    }

    @Override
    public void updatePlot(PlotWindowModel plot, boolean replace) {
        dirtyPlotUUIDs.add(plot.getUUID().toString());

        if(replace) {
            PlotWindowModel foundOriginalPlot = getPlotByUUID(plot.getUUID().toString());
            if(foundOriginalPlot != null && foundOriginalPlot != plot) {
                synchronized(plotMonitor) {
                    plots.remove(foundOriginalPlot);
                    plots.add(plot);                    
                }
            } else {
                logger.logError(
                    "Tried to update plot in database (UUID " + plot.getUUID() +
                    ", name \"" + plot.getName() + "\") but could not find original copy of plot.");
            }
        }
    }

    @Override
    public void deletePlot(String plotUUID) {
        PlotWindowModel plotToDelete = getPlotByUUID(plotUUID);
        if(plotToDelete == null) {
            logger.logDebug("Cannot delete plot with UUID " + plotUUID +
                            ", since it does not exist (most likely, it's already been deleted).", CLASSNAME);
            return;
        }

        logger.logDebug("Preparing to delete plot " + plotToDelete.getName(), CLASSNAME);
        deleteChildPlots(plotUUID);
        deletePlotFromDatabaseMetadata(plotUUID);
        deletePlotFromParentChildRelationships(plotUUID);
        deletePlotFromRoot(plotUUID);       

        synchronized(plotMonitor) {
            plots.remove(plotToDelete);
        }

        updateMetadata();
    }

    @Override 
    public void setNickname(String plotUUID, String nickname){
        PlotWindowModel plotToSetNickname = getPlotByUUID(plotUUID);
        if(plotToSetNickname == null) {
            logger.logDebug("Cannot set nickname for plot with UUID " + plotUUID, CLASSNAME);
            return;
        }

        logger.logDebug("Preparing to set nickname for plot " + plotToSetNickname.getName() + " with the nickname " + nickname, CLASSNAME);
        plotToSetNickname.setNickname(nickname);

        updateMetadata();
    }

    protected void deleteChildPlots(String parentPlotUUID) {
        logger.logDebug("First, delete any child plots...", CLASSNAME);

        List<String> childPlotUUIDs =
            new ArrayList<>(parentChildPlots.getOrDefault(parentPlotUUID, new HashSet<>()));
        for(String childPlotUUID : childPlotUUIDs) {
            PlotWindowModel childPlot = getPlotByUUID(childPlotUUID);
            if(childPlot != null) {
                logger.logDebug("Deleting plot " + childPlot.getName(), CLASSNAME);
                deletePlot(childPlotUUID);
            } else {
                logger.logWarning("Couldn't find child plot by UUID " + childPlotUUID);
            }
        }
    }

    protected void deletePlotFromDatabaseMetadata(String plotUUID) {
        logger.logDebug("Removing plot from database lookup files...", CLASSNAME);
        dirtyPlotUUIDs.remove(plotUUID);
    }

    protected void deletePlotFromParentChildRelationships(String plotUUID) {
        logger.logDebug("Removing plot from parent-child relationships...", CLASSNAME);
        parentChildPlots.remove(plotUUID);
        for(Entry<String, Set<String>> entry : parentChildPlots.entrySet()) {
            String parentUUID = entry.getKey();
            Collection<String> childUUIDs = entry.getValue();
            if(childUUIDs.contains(plotUUID)) {
                childUUIDs.remove(plotUUID);
                parentChildPlots.put(parentUUID, new HashSet<>(childUUIDs));
            }
        }
    }

    protected void deletePlotFromRoot(String plotUUID) {
        synchronized(rootPlotMonitor) {
            PlotWindowModel rootPlot = getRootPlot();
            if(rootPlot != null) {
                String rootPlotUUIDStr = rootPlot.getUUID().toString();
                Collection<String> result = parentChildPlots.get(rootPlotUUIDStr);
                if(result != null) {
                    Set<String> rootPlots = new HashSet<>(result);
                    if(rootPlots.contains(plotUUID)) {
                        logger.logDebug("Also removing plot from set of root plots...", CLASSNAME);
                        rootPlots.remove(plotUUID);
                        setPlotsAsChildren(rootPlotUUIDStr, rootPlots);
                    }
                }
            }
        }
    }

    @Override
    public void deleteAll() {
        List<String> plotUUIDsToDelete = getAllPlotUUIDs();
        for(String plotUUID : plotUUIDsToDelete) {
            deletePlot(plotUUID);
        }

        synchronized(plotMonitor) {
            plots.clear();
            parentChildPlots.clear();
            dirtyPlotUUIDs.clear();
        }
        saveState();
    }

    @Override
    public void addFileToCache(String fileAbsPath) {
        filenameCache.add(fileReader.getName(fileAbsPath));
    }    

    @Override
    public void removeFileFromCache(String fileAbsPath) {
        filenameCache.remove(fileReader.getName(fileAbsPath));
    }  

    @Override
    public void setWatchrConfig(WatchrConfig config) {
        this.config = config;
    }

    @Override
    public void updateMetadata() {
        logger.logDebug("Updating database metadata...", CLASSNAME);
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

    private void setPlotsAsChildren(String parentUUID, Collection<String> childUUIDs) {
        PlotWindowModel parent = getPlotByUUID(parentUUID);
        List<PlotWindowModel> children = new ArrayList<>();
        for(String uuid : childUUIDs) {
            children.add(getPlotByUUID(uuid));
        }
        setPlotsAsChildren(parent, children);
    }

    private PlotWindowModel searchPlot(
            PlotDatabaseSearchCriteria searchCriteria, boolean makeNewIfAbsent) {

        PlotWindowModel returnedWindowModel = null;
        
        String plotName = searchCriteria.getName();
        String category = searchCriteria.getCategory();
        if(StringUtils.isNotBlank(plotName)) {
            logger.logDebug("AbstractDatabase.searchPlot()", CLASSNAME);
            logger.logDebug("Name = \"" + plotName + "\", Category = \"" + category + "\".", CLASSNAME);
            logger.logDebug("Look in loaded plot cache first...", CLASSNAME);
            logger.logDebug("Load plot with name \"" + plotName + "\" in category \"" + category + "\".", CLASSNAME);

            synchronized(plotMonitor) {
                returnedWindowModel = loadPlotFromCache(plotName, category);
                if(returnedWindowModel == null) {
                    logger.logDebug("Could not find plot in cache, so load it from disk...", CLASSNAME);
                    returnedWindowModel = loadPlotUsingInnerFields(plotName, category);
                }

                if(returnedWindowModel == null && makeNewIfAbsent) {
                    logger.logDebug("Could not find plot with name \"" + plotName + "\" in category \"" + category + "\".  Creating a new one...", CLASSNAME);
                    returnedWindowModel = newPlotFromSearchCriteria(searchCriteria);
                    if(returnedWindowModel != null) {
                        addPlot(returnedWindowModel);
                    }
                }
            }
        }
        return returnedWindowModel;
    }

    private PlotWindowModel loadPlotFromCache(String name, String category) {
        logger.logDebug("Looking for plot with name " + name + ", and category " + category + ".", CLASSNAME);
        for(int i = 0; i < plots.size(); i++) {
            PlotWindowModel plot = plots.get(i);
            boolean categoryMatch =
                (StringUtils.isBlank(category) && StringUtils.isBlank(plot.getCategory())) ||
                plot.getCategory().toLowerCase().matches(category.toLowerCase()); // Case-insensitive matching

            if(plot.getName().equals(name) && categoryMatch) {
                logger.logDebug("Found plot with name " + name + ", and category matched.", CLASSNAME);
                return plot;
            }
        }
        return null;
    }

    private PlotWindowModel newPlotFromSearchCriteria(PlotDatabaseSearchCriteria searchCriteria) {
        String plotName = searchCriteria.getName();        
        if(StringUtils.isBlank(plotName) && searchCriteria instanceof NewPlotDatabaseSearchCriteria) {
            NewPlotDatabaseSearchCriteria newPlotSearchCriteria = (NewPlotDatabaseSearchCriteria) searchCriteria;
            NameConfig nameConfig = newPlotSearchCriteria.getNameConfig();
            ExtractionResult xResult = newPlotSearchCriteria.getXResult();
            ExtractionResult yResult = newPlotSearchCriteria.getYResult();
            int resultIndex = newPlotSearchCriteria.getResultIndex();
            ExtractionResultNameResolver nameResolver = new ExtractionResultNameResolver(nameConfig, logger);
            plotName = nameResolver.getName(xResult, yResult, resultIndex);
        }
        if(plotName != null) {
            PlotWindowModel newPlot = new PlotWindowModel(plotName);
            newPlot.setCategory(searchCriteria.getCategory());
            return newPlot;
        }
        return null;
    }

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
                        if(!dirtyPlotUUIDs.contains(windowModel.getUUID().toString())) {
                            dirtyPlotUUIDs.add(windowModel.getUUID().toString());
                        }
                    }

                    @Override
                    public void propertyChanged(PlotToken property) {
                        if(!dirtyPlotUUIDs.contains(windowModel.getUUID().toString())) {
                            dirtyPlotUUIDs.add(windowModel.getUUID().toString());
                        }
                    }
                });
            }
        }
    }

    ///////////
    // RULES //
    ///////////

    @Override
    public Double getValue(RuleTarget target) {
        if(target == RuleTarget.NUMBER_OF_NEW_DATASETS && metadata != null) {
            return Double.valueOf(metadata.getNewPlotCount());
        }
        return null;
    } 
}