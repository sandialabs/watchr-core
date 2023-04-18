/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.db.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.unix4j.Unix4j;
import org.unix4j.line.Line;

import gov.sandia.watchr.config.DataFilterConfig;
import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.config.WatchrConfig;
import gov.sandia.watchr.config.derivative.DerivativeLine;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.db.DatabaseMetadata;
import gov.sandia.watchr.db.impl.bc.DerivativeLineMarshaller;
import gov.sandia.watchr.db.impl.bc.FileReaderMarshaller;
import gov.sandia.watchr.db.impl.bc.GraphDisplayConfigMarshaller;
import gov.sandia.watchr.db.impl.bc.LoggerMarshaller;
import gov.sandia.watchr.db.impl.bc.PlotCanvasModelMarshaller;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotRelationshipManager;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.util.StringUtil;

public class FileBasedDatabase extends AbstractDatabase {

    ////////////
    // FIELDS //
    ////////////

    private static final String CLASSNAME = FileBasedDatabase.class.getSimpleName();

    private static final String FILE_LAST_CONFIG = "lastConfig.json";
    private static final String FILE_METADATA = "metadata.json";

    protected final File rootDir;
    protected Object fileWriteMonitor = new Object();

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FileBasedDatabase(File rootDir, ILogger logger, IFileReader fileReader) {
        super(logger, fileReader);
        this.rootDir = rootDir;
    }
    
    public FileBasedDatabase(String rootDirAbsPath, ILogger logger, IFileReader fileReader) {
        super(logger, fileReader);
        this.rootDir = new File(rootDirAbsPath);
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public List<PlotWindowModel> getAllPlots() {
        if(rootDir != null && rootDir.exists()) {
            File[] fileList = rootDir.listFiles();
            for(File file : fileList) {
                if(file.getName().startsWith("plot_")) {
                    String baseName = FilenameUtils.getBaseName(file.getName());
                    String uuid = baseName.split("_")[1];
                    PlotWindowModel plot = getPlotByUUID(uuid);
                    if(plot != null) {
                        addPlot(plot);
                    }
                }
            }
        }
        return super.getAllPlots();
    }

    @Override
    public PlotWindowModel getRootPlot() {
        PlotWindowModel root = super.getRootPlot();
        if(root == null) {
            root = loadRootPlot();
        }
        return root;
    }   

    @Override
    protected PlotWindowModel getPlotByUUID(String uuid) {
        PlotWindowModel plot = super.getPlotByUUID(uuid);
        if(plot == null) {
            plot = loadPlotUsingUUID(uuid);
        }
        return plot;
    }

    @Override
    public void loadState() {
        logger.logDebug("rootDir: " + rootDir, CLASSNAME);
        logger.logDebug("Looking for root plot...", CLASSNAME);
        loadRootPlot();

        logger.logDebug("Reading parent-child plot relationships...", CLASSNAME);
        readParentChildPlotRelationships();

        logger.logDebug("Reading configuration information...", CLASSNAME);
        readLastConfiguration();
        
        logger.logDebug("Reading file cache...", CLASSNAME);
        readFileCache();

        logger.logDebug("Reading database metadata...", CLASSNAME);
        readMetadata();
    }

    @Override
    public PlotWindowModel loadPlotUsingUUID(String uuid) {
        String expectedName = "plot_" + uuid + ".json";
        if(rootDir != null && rootDir.exists()) {
            File loadFile = new File(rootDir, expectedName);
            if(loadFile.exists()) {
                PlotWindowModel plot = readPlot(loadFile);
                addPlot(plot);
                rebuildPlotRelationships(plot);
                return plot;
            } else {
                logger.logWarning("Could not find file " + expectedName);
            }
        }
        
        return null;
    }

    @Override
    public PlotWindowModel loadPlotUsingInnerFields(String name, String category) {
        String fullJsonNameField     = StringUtil.escapeRegexCharacters("\"name\":\"" + name + "\",");
        String fullJsonCategoryField = StringUtil.escapeRegexCharacters("\"category\":\"" + category + "\",");
        boolean useCategory = StringUtils.isNotBlank(category);

        if(rootDir != null && rootDir.exists()) {
            for(File file : rootDir.listFiles()) {
                if(file.getName().startsWith("plot_")) {
                    List<Line> nameLines     = Unix4j.grep(fullJsonNameField, file).toLineList(); 
                    List<Line> categoryLines = Unix4j.grep(fullJsonCategoryField, file).toLineList(); 

                    if(nameLines.size() == 1 && (categoryLines.size() == 1 || !useCategory)) {
                        PlotWindowModel plot = readPlot(file);
                        addPlot(plot);
                        rebuildPlotRelationships(plot);
                        return plot;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public PlotWindowModel loadRootPlot() {
        if(rootDir != null && rootDir.exists()) {
            for(File file : rootDir.listFiles()) {
                try {
                    String fileContents = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                    if(fileContents.contains("\"isRoot\":true")) {
                        PlotWindowModel newRootPlot = readPlot(file);
                
                        synchronized(plotMonitor) {
                            boolean alreadyContains = false;
                            for(int i = 0; i < plots.size(); i++) {
                                PlotWindowModel checkPlot = plots.get(i);
                                if(checkPlot.equals(newRootPlot)) {
                                    alreadyContains = true;
                                    break;
                                }
                            }
                
                            if(!alreadyContains) {
                                plots.add(newRootPlot);
                                dirtyPlotUUIDs.add(newRootPlot.getUUID().toString());
                                rebuildPlotRelationships(newRootPlot);
                            }
                        }

                        return newRootPlot;
                    }
                } catch(IOException e) {
                    logger.logError("An error occurred deserializing " + file.getName(), e);
                }
            }
        }
        return null;
    }

    @Override
    public void deletePlot(String plotUUID) {
        super.deletePlot(plotUUID);

        String deleteFileName = "plot_" + plotUUID + ".json";
        File deleteFile = new File(rootDir, deleteFileName);
        if(deleteFile.exists()) {
            try {
                Files.delete(Paths.get(deleteFile.toURI()));
                logger.logInfo("Successfully deleted " + deleteFile.toURI().toString());
            } catch(IOException e) {
                logger.logError("Could not delete file " + deleteFileName, e);
            }
        } else {
            logger.logInfo(deleteFileName + " is missing or was previously deleted.");
        }
    }

    @Override
    protected void deleteChildPlots(String parentPlotUUID) {
        logger.logDebug("First, delete any child plots...", CLASSNAME);

        List<String> childPlotUUIDs =
            new ArrayList<>(parentChildPlots.getOrDefault(parentPlotUUID, new HashSet<>()));
        for(String childPlotUUID : childPlotUUIDs) {
            PlotWindowModel childPlot = getPlotByUUID(childPlotUUID);
            if(childPlot != null) {
                logger.logDebug("Deleting child plot " + childPlot.getName(), CLASSNAME);
                deletePlot(childPlotUUID);
            } else {
                logger.logWarning("Couldn't find child plot by UUID " + childPlotUUID);
            }
        }
    }

    @Override
    public void saveState() {
        synchronized(fileWriteMonitor) {
            writePlots();
            writeParentChildPlotRelationships();
            writeLastConfiguration();
            writeFileCache();
            writeMetadata();
        }
    }

    ///////////////
    // PROTECTED //
    ///////////////

    public void readPlots() {
        if(rootDir != null && rootDir.exists()) {
            for(File file : rootDir.listFiles()) {
                if(file.getName().startsWith("plot_")) {
                    PlotWindowModel deszPlot = readPlot(file);
                    if(deszPlot != null) {
                        addPlot(deszPlot);
                        rebuildPlotRelationships(deszPlot);
                        setListeners(deszPlot);
                    }
                }
            }
        }
    }

    public PlotWindowModel readPlot(File plotFile) {
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(plotFile))) {
            Gson gson =
                new GsonBuilder()
                    .registerTypeAdapter(PlotCanvasModel.class, new PlotCanvasModelMarshaller())
                    .create();
            return gson.fromJson(bufferedReader, PlotWindowModel.class); 
        } catch(IOException e) {
            logger.logError("An error occurred deserializing " + plotFile.getName(), e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected void readParentChildPlotRelationships() {
        File file = new File(rootDir, "parentChildPlots.json");
        if(file.exists()) {
            GsonBuilder builder = new GsonBuilder(); 
            Gson gson = builder.create(); 
            try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                parentChildPlots.clear();
                Map<String, Set<String>> deszMap = gson.fromJson(bufferedReader, Map.class); 
                parentChildPlots.putAll(deszMap);
            } catch(IOException e) {
                logger.logError("An error occurred deserializing parentChildPlots.json", e);
            }
        }
    }

    protected void readLastConfiguration() {
        File file = new File(rootDir, FILE_LAST_CONFIG);

        if(file.exists()) {
            Gson gson = null;
            try {
                gson = new GsonBuilder()
                    .registerTypeAdapter(DerivativeLine.class, new DerivativeLineMarshaller())
                    .registerTypeAdapter(ILogger.class, new LoggerMarshaller())
                    .registerTypeAdapter(IFileReader.class, new FileReaderMarshaller())
                    .registerTypeAdapter(GraphDisplayConfig.class, new GraphDisplayConfigMarshaller(logger))
                    .create();
            } catch(JsonParseException e) {
                logger.logError("An error occurred deserializing categories.json", e);
            }

            if(gson != null) {
                try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                    config = gson.fromJson(bufferedReader, WatchrConfig.class);
                } catch(IOException e) {
                    logger.logError("An error occurred deserializing categories.json", e);
                }
            }

            if(config != null && config.getFilterConfig() == null) {
                config.setFilterConfig(new DataFilterConfig(WatchrConfig.START_PATH, logger));
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void readFileCache() {
        File file = new File(rootDir, "fileCache.json");
        if(file.exists()) {
            GsonBuilder builder = new GsonBuilder(); 
            Gson gson = builder.create(); 
            try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                filenameCache.clear();
                filenameCache.addAll(gson.fromJson(bufferedReader, Set.class)); 
            } catch(IOException e) {
                logger.logError("An error occurred deserializing fileCache.json", e);
            }
        }
    }

    protected void readMetadata() {
        File file = new File(rootDir, FILE_METADATA);
        if(file.exists()) {
            GsonBuilder builder = new GsonBuilder(); 
            Gson gson = builder.create(); 
            try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                metadata = gson.fromJson(bufferedReader, DatabaseMetadata.class); 
            } catch(IOException e) {
                logger.logError("An error occurred deserializing metadata.json", e);
            }
        }
    }

    protected void rebuildPlotRelationships(PlotWindowModel windowModel) {
        PlotRelationshipManager.addWindowModel(windowModel);
        for(PlotCanvasModel canvasModel : windowModel.getCanvasModels()) {
            if(canvasModel != null) {
                PlotRelationshipManager.addCanvasModel(canvasModel);
            }
        }
    }

    protected void writePlots() {
        for(String dirtyPlotUUID : dirtyPlotUUIDs) {
            PlotWindowModel plot = getPlotByUUID(dirtyPlotUUID);
            if(plot != null) {
                writePlotWindowModel(plot);
            }
        }
    }

    protected void writePlotWindowModel(PlotWindowModel plotWindowModel) {
        GsonBuilder builder = new GsonBuilder(); 
        Gson gson = builder.create(); 
        String destinationFileName = "plot_" + plotWindowModel.getUUID() + ".json";
        File destinationFile = new File(rootDir, destinationFileName);
        logger.logDebug("Writing " + destinationFileName + " to disk...", CLASSNAME);

        try(FileWriter writer = new FileWriter(destinationFile)) {
            writer.write(gson.toJson(plotWindowModel));   
        } catch(IOException e) {
            logger.logError("An error occurred serializing " + destinationFileName, e);
        }
    }

    protected void writeParentChildPlotRelationships() {
        GsonBuilder builder = new GsonBuilder(); 
        Gson gson = builder.create(); 
        String parentChildPlotsFilename = "parentChildPlots.json";
        File parentChildPlotsFile = new File(rootDir, parentChildPlotsFilename);
        try(FileWriter writer = new FileWriter(parentChildPlotsFile)) {
            writer.write(gson.toJson(parentChildPlots));   
        } catch(IOException e) {
            logger.logError("An error occurred serializing " + parentChildPlotsFilename, e);
        }
    }

    protected void writeLastConfiguration() {
        GsonBuilder builder = new GsonBuilder(); 
        Gson gson = builder.create(); 
        File configFile = new File(rootDir, FILE_LAST_CONFIG);
        try(FileWriter writer = new FileWriter(configFile)) {
            writer.write(gson.toJson(config));   
        } catch(IOException e) {
            logger.logError("An error occurred serializing " + FILE_LAST_CONFIG, e);
        }
    }

    protected void writeMetadata() {
        GsonBuilder builder = new GsonBuilder(); 
        Gson gson = builder.create(); 
        File metadataFile = new File(rootDir, FILE_METADATA);
        try(FileWriter writer = new FileWriter(metadataFile)) {
            writer.write(gson.toJson(metadata));   
        } catch(IOException e) {
            logger.logError("An error occurred serializing " + FILE_METADATA, e);
        }
    }     

    protected void writeFileCache() {
        GsonBuilder builder = new GsonBuilder(); 
        Gson gson = builder.create();
        String filenameCacheFilename = "fileCache.json";
        File filenameCacheFile = new File(rootDir, filenameCacheFilename);
        try(FileWriter writer = new FileWriter(filenameCacheFile)) {
            writer.write(gson.toJson(filenameCache));   
        } catch(IOException e) {
            logger.logError("An error occurred serializing " + filenameCacheFilename, e);
        }
    }
}
