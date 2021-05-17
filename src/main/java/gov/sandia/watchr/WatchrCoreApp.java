/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.config.WatchrConfig;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.config.reader.WatchrConfigJsonReader;
import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.db.impl.FileBasedDatabase;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.graph.library.GraphOperationResult;
import gov.sandia.watchr.graph.library.IHtmlGraphRenderer;
import gov.sandia.watchr.graph.library.impl.PlotlyGraphRenderer;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.log.Log4jLogger;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.parse.generators.WatchrConfigGenerator;
import gov.sandia.watchr.util.CommonConstants;

/**
 * The top-level public API for Watchr.  Because only one instance of Watchr should
 * theoretically be available at any given time, this class follows the singleton
 * pattern.
 * 
 * @author Elliott Ridgway
 */
public class WatchrCoreApp {

    ////////////
    // FIELDS //
    ////////////
    
    private static final WatchrCoreApp singleton;
    private static final String VERSION = "1.0.0";

    private final Map<String, IDatabase> dbCacheMap;
    private final Map<String, IHtmlGraphRenderer> graphRendererMap;

    private ILogger logger;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    private WatchrCoreApp() {
        logger = new Log4jLogger();
        dbCacheMap = new HashMap<>();
        graphRendererMap = new HashMap<>();
    }

    //////////
    // INIT //
    //////////

    static {
        singleton = new WatchrCoreApp();
    }

    /////////////
    // GETTERS //
    /////////////

    public static WatchrCoreApp getInstance() {
        return singleton;
    }

    public ILogger getLogger() {
        return logger;
    }

    public String getVersion() {
        return VERSION;
    }

    /////////////
    // SETTERS //
    /////////////

    public void setLogger(ILogger logger) {
        this.logger = logger;
    }


    /////////////////
    // CONFIG FILE //
    /////////////////

    public void validateCompileTimeErrors(File directory, File configFile) {
        String configFileContents;
        try {
            configFileContents = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
            validateCompileTimeErrors(directory, configFileContents);
        } catch (IOException e) {
            logger.logError("Error occurred reading config file.", e);
        }
    }

    public void validateCompileTimeErrors(File directory, String configFileStr) {
        WatchrConfigJsonReader reader = new WatchrConfigJsonReader(directory);
        WatchrConfig watchrConfig = reader.deserialize(configFileStr);
        watchrConfig.validate();
    }

    public void processConfigFile(
            String databaseName, File directory, String configFileStr) throws WatchrParseException {
        WatchrConfigJsonReader reader = new WatchrConfigJsonReader(directory);
        WatchrConfig watchrConfig = reader.deserialize(configFileStr);
        
        IDatabase db = dbCacheMap.get(databaseName);

        List<WatchrDiff<?>> diffs = reader.getDiffs(db.getLastConfig(), watchrConfig);
        if(!diffs.isEmpty()) {
            getLogger().logInfo("Diffs:");
            getLogger().logInfo(diffs.toString());
        }

        WatchrConfigGenerator watchrConfigGenerator = new WatchrConfigGenerator(db);
        watchrConfigGenerator.generate(watchrConfig, diffs);

        db.saveState();
    }    

    //////////////
    // DATABASE //
    //////////////

    public List<Class<? extends IDatabase>> getKnownDatabaseImplementations() {
        List<Class<? extends IDatabase>> dbs = new ArrayList<>();
        dbs.add(FileBasedDatabase.class);
        return dbs;
    }

    public void connectDatabase(String databaseName, Class<? extends IDatabase> databaseType, Object[] args) {
        logger.logInfo("Attempting database connection (watchr-core)");
        if(dbCacheMap.containsKey(databaseName)) {
            logger.logInfo("Database with name " + databaseName + " has already been initialized!");
        } else {
            if (databaseType == FileBasedDatabase.class && args.length > 0) {
                try {
                    Object arg = args[0];
                    if (arg instanceof File) {
                        File rootDir = (File) arg;
                        if(!rootDir.exists()) {
                            boolean success = rootDir.mkdir();
                            logger.logInfo("Successful DB creation? " + (success ? "Yes" : "No"));
                        } else {
                            logger.logInfo("Folder already exists");
                        }

                        IDatabase db = new FileBasedDatabase(rootDir);
                        dbCacheMap.put(databaseName, db);
                        db.loadState();

                        logger.logInfo("FolderBasedDatabase was initialized with rootDir " + rootDir);
                    } else {
                        logger.logWarning("Expected File in arg list, but was " + arg.getClass().toGenericString());
                    }
                } catch(Exception e) {
                    logger.logError("An error occurred initializing the database", e);
                }
            }
        }
    }

    public Set<String> getDatabaseCategories(String databaseName) {
        IDatabase db = dbCacheMap.get(databaseName);
        if(db != null) {
            return db.getCategories();
        }
        return new HashSet<>();
    }

    public GraphDisplayConfig getDatabaseGraphDisplayConfiguration(String databaseName) {
        IDatabase db = dbCacheMap.get(databaseName);
        if(db != null) {
            return db.getGraphDisplayConfig();
        }
        return null;
    }

    public boolean deletePlotFromDatabase(String databaseName, String plotName, String category) {
        logger.logInfo("Attempting to delete plot " + plotName + " in category " + category + "...");
        IDatabase db = dbCacheMap.get(databaseName);
        if(db != null) {
            logger.logInfo("Found database " + databaseName);
            PlotWindowModel windowModel = db.getPlot(plotName, category);
            if(windowModel != null) {
                logger.logInfo("Found plot.  Deleting...");
                db.deletePlot(windowModel);
                logger.logInfo("Delete was successful");
                logger.logInfo("Saving database state to disk");
                db.saveState();
                return true;
            } else {
                logger.logWarning("Could not find plot " + plotName + " in category " + category);
            }
        }
        logger.logWarning("Could not find database " + databaseName);
        return false;
    }

    public int getPlotsSize(String databaseName) {
        IDatabase db = dbCacheMap.get(databaseName);
        if(db != null) {
            int size = db.getAllPlots().size();
            size--; // Don't count the root plot.
            return size;
        }
        return 0;
    }

    public int getFailedPlotsSize(String databaseName) {
        IDatabase db = dbCacheMap.get(databaseName);
        if(db != null) {
            int failCount = 0;
            List<PlotWindowModel> plots = new ArrayList<>(db.getAllPlots());
            for(PlotWindowModel plot : plots) {
                if(plot.isFailing() && !plot.isRoot()) {
                    failCount++;
                }
            }
            return failCount;
        }
        return 0;
    }

    //////////////
    // GRAPHING //
    //////////////

    public List<Class<? extends IHtmlGraphRenderer>> getKnownGraphRenderers() {
        List<Class<? extends IHtmlGraphRenderer>> graphRenderers = new ArrayList<>();
        graphRenderers.add(PlotlyGraphRenderer.class);
        return graphRenderers;
    }    

    public IHtmlGraphRenderer getGraphRenderer(Class<? extends IHtmlGraphRenderer> graphLibraryType, String databaseName) {
        IHtmlGraphRenderer graphRenderer = graphRendererMap.get(databaseName);
        if(graphRenderer == null) {
            IDatabase db = dbCacheMap.get(databaseName);
            if(db != null && graphLibraryType == PlotlyGraphRenderer.class) {    
                graphRenderer = new PlotlyGraphRenderer(db);
                graphRendererMap.put(databaseName, graphRenderer);
            }
        }
        return graphRenderer;
    }

    public GraphDisplayConfig getDefaultGraphDisplayConfiguration() {
        GraphDisplayConfig plotConfiguration = new GraphDisplayConfig("");
        plotConfiguration.setNextPlotDbLocation(CommonConstants.ROOT_PATH_ALIAS);
        plotConfiguration.setGraphWidth(500);
        plotConfiguration.setGraphHeight(500);
        plotConfiguration.setGraphsPerRow(3);
        return plotConfiguration;
    }

    public GraphOperationResult getGraphHtml(String databaseName, GraphDisplayConfig plotConfiguration, boolean standalone) {
        IHtmlGraphRenderer graphRenderer = getGraphRenderer(PlotlyGraphRenderer.class, databaseName);
        if(graphRenderer != null) {
            return graphRenderer.getGraphHtml(plotConfiguration, standalone);
        } else {
            return new GraphOperationResult();
        }
    }

    public void exportAllGraphHtml(String databaseName, GraphDisplayConfig plotConfiguration, File destDir) {
        IHtmlGraphRenderer graphRenderer = getGraphRenderer(PlotlyGraphRenderer.class, databaseName);
        if(graphRenderer != null) {
            try {
                List<String> categories = new ArrayList<>(getDatabaseCategories(databaseName));
                graphRenderer.exportAllGraphHtml(plotConfiguration, categories, destDir, true);
            } catch(IOException e) {
                logger.logError("An error occurred exporting HTML graphs.", e);
            }
        }
    }

    public void clearAllGraphingLibraries() {
        graphRendererMap.clear();
    }

    //////////
    // MAIN //
    //////////

    public static void main( String[] args ) throws Exception {
        if(args.length == 0) {
            System.out.println("watchr-core " + VERSION);
            System.out.println("Usage:  watchr-core [config file] [data file/folder] [optional: database location] [optional: export location]");
        } else if(args.length >= 2) {
            String configFileStr = args[0];
            String dataFileStr = args[1];

            String dbDirStr = "";
            if(args.length >= 3) {
                dbDirStr = args[2];
            }
            if(StringUtils.isBlank(dbDirStr)) {
                dbDirStr = new File("db").getAbsolutePath();
            }

            String exportDirStr = "";
            if(args.length >= 4) {
                exportDirStr = args[3];
            }
            if(StringUtils.isBlank(exportDirStr)) {
                exportDirStr = Files.createTempDirectory(null).toFile().getAbsolutePath();
            }

            File configFile = new File(configFileStr);
            File dataFile = new File(dataFileStr);
            if(configFile.exists() && dataFile.exists()) {
                String configFileContents = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
                String dbName = "db_" + UUID.randomUUID().toString();
                File dbDir = new File(dbDirStr);
                File dataDir;
                if(dataFile.isDirectory()) {
                    dataDir = dataFile;
                } else {
                    dataDir = dataFile.getParentFile();
                }

                WatchrCoreApp app = new WatchrCoreApp();
                app.connectDatabase(dbName, FileBasedDatabase.class, new Object[]{ dbDir });
                app.processConfigFile(dbName, dataDir, configFileContents);

                if(StringUtils.isNotBlank(exportDirStr)) {
                    File exportDir = new File(exportDirStr);
                    app.exportAllGraphHtml(dbName, app.getDefaultGraphDisplayConfiguration(), exportDir);
                }
            } else {
                if(!configFile.exists()) {
                    System.err.println("Config file " + configFileStr + " does not exist.");
                } else {
                    System.err.println("Data file/folder " + dataFileStr + " does not exist.");
                }
            }
        }
    }
}
