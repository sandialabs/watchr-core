/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.db.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.config.WatchrConfig;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotRelationshipManager;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.log.ILogger;

public class FileBasedDatabase extends AbstractDatabase {

    private static final String FILE_LAST_CONFIG = "lastConfig.json";
    protected final File rootDir;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FileBasedDatabase(File rootDir) {
        super();
        this.rootDir = rootDir;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void saveState() {
        writePlots();
        writeParentChildPlotRelationships();
        writeLastConfiguration();
        writeFileCache();
    }

    @Override
    public void loadState() {
        ILogger logger = WatchrCoreApp.getInstance().getLogger();

        logger.logInfo("rootDir: " + rootDir);
        logger.logInfo("Reading plots...");
        readPlots();

        logger.logInfo("Reading parent-child plot relationships...");
        readParentChildPlotRelationships();

        logger.logInfo("Reading configuration information...");
        readLastConfiguration();
        
        logger.logInfo("Reading file cache...");
        readFileCache();
    }

    @Override
    public void deletePlot(PlotWindowModel plotToDelete) {
        super.deletePlot(plotToDelete);
        ILogger logger = WatchrCoreApp.getInstance().getLogger();

        String deleteFileName = "plot_" + plotToDelete.getUUID() + ".json";
        File deleteFile = new File(rootDir, deleteFileName);
        if(deleteFile.exists()) {
            try {
                Files.delete(Paths.get(deleteFile.toURI()));
                logger.logInfo("Successfully deleted " + deleteFile.toURI().toString());
            } catch(IOException e) {
                logger.logError("Could not delete file " + deleteFileName, e);
            }
        } else {
            logger.logWarning("Could not find file " + deleteFileName);
        }
    }

    /////////////
    // PACKAGE //
    /////////////

    /*package*/ void writePlots() {
        for(String dirtyPlotUUID : dirtyPlotUUIDs) {
            PlotWindowModel plot = getPlotByUUID(dirtyPlotUUID);
            writePlotWindowModel(plot);
        }
    }

    /*package*/ void writePlotWindowModel(PlotWindowModel plotWindowModel) {
        GsonBuilder builder = new GsonBuilder(); 
        Gson gson = builder.create(); 
        String destinationFileName = "plot_" + plotWindowModel.getUUID() + ".json";
        File destinationFile = new File(rootDir, destinationFileName);
        try(FileWriter writer = new FileWriter(destinationFile)) {
            writer.write(gson.toJson(plotWindowModel));   
        } catch(IOException e) {
            ILogger logger = WatchrCoreApp.getInstance().getLogger();
            logger.logError("An error occurred serializing " + destinationFileName, e);
        }
    }

    /*package*/ void writeParentChildPlotRelationships() {
        GsonBuilder builder = new GsonBuilder(); 
        Gson gson = builder.create(); 
        String parentChildPlotsFilename = "parentChildPlots.json";
        File parentChildPlotsFile = new File(rootDir, parentChildPlotsFilename);
        try(FileWriter writer = new FileWriter(parentChildPlotsFile)) {
            writer.write(gson.toJson(parentChildPlots));   
        } catch(IOException e) {
            ILogger logger = WatchrCoreApp.getInstance().getLogger();
            logger.logError("An error occurred serializing " + parentChildPlotsFilename, e);
        }
    }

    /*package*/ void writeLastConfiguration() {
        GsonBuilder builder = new GsonBuilder(); 
        Gson gson = builder.create(); 
        String configFileName = "lastConfig.json";
        File configFile = new File(rootDir, configFileName);
        try(FileWriter writer = new FileWriter(configFile)) {
            writer.write(gson.toJson(config));   
        } catch(IOException e) {
            ILogger logger = WatchrCoreApp.getInstance().getLogger();
            logger.logError("An error occurred serializing " + configFileName, e);
        }
    }   

    /*package*/ void writeFileCache() {
        GsonBuilder builder = new GsonBuilder(); 
        Gson gson = builder.create();
        String filenameCacheFilename = "fileCache.json";
        File filenameCacheFile = new File(rootDir, filenameCacheFilename);
        try(FileWriter writer = new FileWriter(filenameCacheFile)) {
            writer.write(gson.toJson(filenameCache));   
        } catch(IOException e) {
            ILogger logger = WatchrCoreApp.getInstance().getLogger();
            logger.logError("An error occurred serializing " + filenameCacheFilename, e);
        }
    }

    /*package*/ void readPlots() {
        if(rootDir != null && rootDir.exists()) {
            for(File file : rootDir.listFiles()) {
                if(file.getName().startsWith("plot_")) {
                    PlotWindowModel deszPlot = readPlot(file);
                    if(deszPlot != null) {
                        plots.add(deszPlot);
                        setListeners(deszPlot);
                    }
                }
            }

            rebuildPlotRelationships(plots);
        }
    }

    /*package*/ PlotWindowModel readPlot(File plotFile) {
        GsonBuilder builder = new GsonBuilder(); 
        Gson gson = builder.create(); 
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(plotFile))) {
            return gson.fromJson(bufferedReader, PlotWindowModel.class); 
        } catch(IOException e) {
            ILogger logger = WatchrCoreApp.getInstance().getLogger();
            logger.logError("An error occurred deserializing " + plotFile.getName(), e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    /*package*/ void readParentChildPlotRelationships() {
        File file = new File(rootDir, "parentChildPlots.json");
        if(file.exists()) {
            GsonBuilder builder = new GsonBuilder(); 
            Gson gson = builder.create(); 
            try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                parentChildPlots.clear();
                Map<String, Set<String>> deszMap = gson.fromJson(bufferedReader, Map.class); 
                parentChildPlots.putAll(deszMap);
            } catch(IOException e) {
                ILogger logger = WatchrCoreApp.getInstance().getLogger();
                logger.logError("An error occurred deserializing parentChildPlots.json", e);
            }
        }
    }

    /*package*/ void readLastConfiguration() {
        File file = new File(rootDir, FILE_LAST_CONFIG);
        if(file.exists()) {
            GsonBuilder builder = new GsonBuilder(); 
            Gson gson = builder.create(); 
            try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                config = gson.fromJson(bufferedReader, WatchrConfig.class);
            } catch(IOException e) {
                ILogger logger = WatchrCoreApp.getInstance().getLogger();
                logger.logError("An error occurred deserializing categories.json", e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    /*package*/ void readFileCache() {
        File file = new File(rootDir, "fileCache.json");
        if(file.exists()) {
            GsonBuilder builder = new GsonBuilder(); 
            Gson gson = builder.create(); 
            try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                filenameCache.clear();
                filenameCache.addAll(gson.fromJson(bufferedReader, Set.class)); 
            } catch(IOException e) {
                ILogger logger = WatchrCoreApp.getInstance().getLogger();
                logger.logError("An error occurred deserializing fileCache.json", e);
            }
        }
    }

    /*package*/ void rebuildPlotRelationships(Set<PlotWindowModel> windowModels) {
        for(PlotWindowModel windowModel : windowModels) {
            PlotRelationshipManager.addWindowModel(windowModel);
            for(PlotCanvasModel canvasModel : windowModel.getCanvasModels()) {
                if(canvasModel != null) {
                    PlotRelationshipManager.addCanvasModel(canvasModel);
                }
            }
        }
    }
}
