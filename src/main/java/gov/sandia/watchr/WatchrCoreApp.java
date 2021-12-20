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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.config.CategoryConfiguration;
import gov.sandia.watchr.config.FileConfig;
import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.config.PlotsConfig;
import gov.sandia.watchr.config.WatchrConfig;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.config.file.DefaultFileReader;
import gov.sandia.watchr.config.file.IFileReader;
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
import gov.sandia.watchr.rule.actors.RuleFailActor;

/**
 * The top-level public API for Watchr. Because only one instance of Watchr
 * should theoretically be available at any given time, this class follows the
 * singleton pattern.
 * 
 * @author Elliott Ridgway
 */
public class WatchrCoreApp {

    ////////////
    // FIELDS //
    ////////////

    private static final String VERSION = "1.8.0";

    private final Map<String, IDatabase> dbCacheMap;
    private final Map<String, IHtmlGraphRenderer> graphRendererMap;

    private IFileReader fileReader;
    private ILogger logger;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public WatchrCoreApp() {
        logger = new Log4jLogger();
        fileReader = new DefaultFileReader(logger);
        dbCacheMap = new HashMap<>();
        graphRendererMap = new HashMap<>();
    }

    /////////////
    // GETTERS //
    /////////////

    public static String getVersion() {
        return VERSION;
    }

    public ILogger getLogger() {
        return logger;
    }

    public IFileReader getFileReader() {
        return fileReader;
    }

    /////////////
    // SETTERS //
    /////////////

    public void setLogger(ILogger logger) {
        this.logger = logger;
    }

    public void setFileReader(IFileReader fileReader) {
        this.fileReader = fileReader;
    }

    /////////////////
    // CONFIG FILE //
    /////////////////

    /**
     * Validate "compile-time" errors for Watchr. These are errors that can be
     * identified in the config file itself, before running Watchr.
     * 
     * @param startFile  The data file or directory containing data that Watchr will
     *                   read.
     * @param configFile Watchr's config file.
     */
    public void validateCompileTimeErrors(File startFile, File configFile) {
        String startFileAbsPath = startFile.getAbsolutePath();
        String configFileContents = fileReader.readFromFile(configFile.getAbsolutePath());

        validateCompileTimeErrors(startFileAbsPath, configFileContents);
    }

    /**
     * Validate "compile-time" errors for Watchr. These are errors that can be
     * identified in the config file itself, before running Watchr.
     * 
     * @param startFileAbsPath   The absolute path to the file or directory
     *                           containing data that Watchr will read.
     * @param configFileContents Watchr's config information, as a String.
     */
    public void validateCompileTimeErrors(String startFileAbsPath, String configFileContents) {
        WatchrConfigJsonReader reader = new WatchrConfigJsonReader(startFileAbsPath, logger, fileReader);
        WatchrConfig watchrConfig = reader.deserialize(configFileContents);
        watchrConfig.validate();
    }

    //////////////
    // DATABASE //
    //////////////

    /**
     * 
     * @return A List of {@link IDatabase} implementations supported by Watchr.
     */
    public List<Class<? extends IDatabase>> getKnownDatabaseImplementations() {
        List<Class<? extends IDatabase>> dbs = new ArrayList<>();
        dbs.add(FileBasedDatabase.class);
        return dbs;
    }

    /**
     * Connect to a Watchr database. If no such database exists, a new one will be
     * instantiated. This method uses a unique name lookup to prevent multiple of
     * the same database from being instantiated.
     * 
     * @param databaseName The name of the database to connect to.
     * @param databaseType The type of database. See implementations of
     *                     {@link IDatabase}.
     * @param args         An arbitary list of arguments for the database connection
     *                     operation. Certain database types may require arguments
     *                     at the time of initialization.
     */
    public void connectDatabase(String databaseName, Class<? extends IDatabase> databaseType, Object[] args) {
        logger.logInfo("Attempting database connection (watchr-core)");
        if (dbCacheMap.containsKey(databaseName) && dbCacheMap.get(databaseName) != null) {
            logger.logInfo("Database with name " + databaseName + " has already been initialized!");
        } else {
            if (databaseType == FileBasedDatabase.class && args.length > 0) {
                Object arg = args[0];
                if (arg instanceof File) {
                    File rootDir = (File) arg;
                    connectFileBasedDatabase(databaseName, rootDir);
                } else {
                    logger.logWarning("Expected File in arg list, but was " + arg.getClass().toGenericString());
                }
            }
        }
    }

    private void connectFileBasedDatabase(String databaseName, File rootDir) {
        try {
            if (!rootDir.exists()) {
                boolean success = rootDir.mkdir();
                logger.logDebug("Successful DB creation? " + (success ? "Yes" : "No"));
            } else {
                logger.logDebug("Folder already exists");
            }

            IDatabase db = new FileBasedDatabase(rootDir, logger, fileReader);
            dbCacheMap.put(databaseName, db);
            db.loadState();

            logger.logDebug("FolderBasedDatabase was initialized with rootDir " + rootDir);
        } catch (Exception e) {
            logger.logError("An error occurred initializing the database", e);
        }
    }

    /**
     * 
     */
    public IDatabase getDatabase(String databaseName) {
        return dbCacheMap.get(databaseName);
    }

    /**
     * 
     * @param databaseName The name of the database.
     * @return The database's defined list of categories. See
     *         {@link CategoryConfiguration} for more information.
     */
    public Set<String> getDatabaseCategories(String databaseName) {
        IDatabase db = dbCacheMap.get(databaseName);
        if (db != null) {
            return db.getCategories();
        } else {
            logger.logWarning(databaseName + " is null (watchr-core)");
        }
        return new HashSet<>();
    }

    /**
     * 
     * @param databaseName The name of the database.
     * @return The database's defined {@link GraphDisplayConfig}.
     */
    public GraphDisplayConfig getDatabaseGraphDisplayConfiguration(String databaseName) {
        IDatabase db = dbCacheMap.get(databaseName);
        if (db != null) {
            return db.getGraphDisplayConfig();
        }
        return null;
    }

    /**
     * Process one of more data files according to the provided Watchr configuration
     * file.
     * 
     * @param databaseName     The name of the database to write to.
     * @param startFileAbsPath The absolute path to the start file (or directory)
     *                         that Watchr will read.
     * @param configFileStr    Watchr's config information, as a String.
     * @throws WatchrParseException Thrown if an error occurs while processing the
     *                              data.
     */
    public void addToDatabase(
            String databaseName, String startFileAbsPath, String configFileStr) throws WatchrParseException {

        WatchrConfigJsonReader reader = new WatchrConfigJsonReader(startFileAbsPath, logger, fileReader);
        logger.logDebug("startFileAbsPath is " + startFileAbsPath + " (watchr-core)");
        WatchrConfig watchrConfig = reader.deserialize(configFileStr);
        if(isStartFileValid(watchrConfig, startFileAbsPath)) {
            IDatabase db = dbCacheMap.get(databaseName);
            if(db != null) {
                db.setLogger(logger);
                List<WatchrDiff<?>> diffs = reader.getDiffs(db.getLastConfig(), watchrConfig);
                if (!diffs.isEmpty()) {
                    logger.logInfo("Watchr config file has diffed since the last run.");
                    logger.logDebug(diffs.toString());
                }

                WatchrConfigGenerator watchrConfigGenerator = new WatchrConfigGenerator(db);
                watchrConfigGenerator.generate(watchrConfig, diffs);
            } else {
                logger.logWarning(databaseName + " database does not exist (watchr-core)");
            }
        } else {
            logger.logWarning("startFileAbsPath is not valid (watchr-core)");
        }
    }    

    /**
     * Delete a single plot from the database. This is an irreversible operation.
     * 
     * @param databaseName The name of the database to delete from.
     * @param plotName     The name of the plot to delete.
     * @param category     The category that the plot belongs to.
     * @return Whether the delete operation was successful.
     */
    public boolean deletePlotFromDatabase(String databaseName, String plotName, String category) {
        logger.logInfo("Attempting to delete plot " + plotName + " in category " + category + "...");
        IDatabase db = dbCacheMap.get(databaseName);
        if (db != null) {
            db.setLogger(logger);
            logger.logDebug("Found database " + databaseName);
            PlotWindowModel windowModel = db.searchPlot(plotName, category);
            if (windowModel != null) {
                logger.logDebug("Found plot.  Deleting...");
                db.deletePlot(windowModel);
                logger.logInfo("Delete was successful");
                logger.logDebug("Saving database state to disk");
                return true;
            } else {
                logger.logWarning("Could not find plot " + plotName + " in category " + category);
            }
        }
        logger.logWarning("Could not find database " + databaseName);
        return false;
    }

    public void saveDatabase(String databaseName) {
        IDatabase db = dbCacheMap.get(databaseName);
        if (db != null) {
            db.saveState();
        }
    }

    /**
     * Return the number of plots in the database.
     * 
     * @param databaseName The name of the database.
     * @return The number of plots in the database.
     */
    public int getPlotsSize(String databaseName) {
        IDatabase db = dbCacheMap.get(databaseName);
        if(db != null) {
            return db.getMetadata().getPlotCount();
        }
        return 0;
    }

    /**
     * Return the number of plots in the database that are marked as being in a
     * "failure" state. See {@link RuleFailActor} for more information.
     * 
     * @param databaseName The name of the database.
     * @return The number of failed plots in the database.
     */
    public int getFailedPlotsSize(String databaseName) {
        IDatabase db = dbCacheMap.get(databaseName);
        if (db != null) {
            return db.getMetadata().getFailedPlotCount();
        }
        return 0;
    }

    //////////////
    // GRAPHING //
    //////////////

    /**
     * 
     * @return The List of {@link IHtmlGraphRenderer} implementations known to
     *         Watchr.
     */
    public List<Class<? extends IHtmlGraphRenderer>> getKnownGraphRenderers() {
        List<Class<? extends IHtmlGraphRenderer>> graphRenderers = new ArrayList<>();
        graphRenderers.add(PlotlyGraphRenderer.class);
        return graphRenderers;
    }

    /**
     * Given a database, retrieve its graph renderer. If none has been set, this
     * method's first argument will allow you to instantiate a graph renderer for
     * the database.
     * 
     * @param graphLibraryType The type of graph renderer to instantiate if none has
     *                         been.
     * @param databaseName     The name of the database.
     * @return The {@link IHtmlGraphRenderer} implementation for the given database.
     */
    public IHtmlGraphRenderer getGraphRenderer(Class<? extends IHtmlGraphRenderer> graphLibraryType,
            String databaseName) {
        IHtmlGraphRenderer graphRenderer = graphRendererMap.get(databaseName);
        if (graphRenderer == null) {
            IDatabase db = dbCacheMap.get(databaseName);
            if (db != null && graphLibraryType == PlotlyGraphRenderer.class) {
                graphRenderer = new PlotlyGraphRenderer(db, logger, fileReader);
                graphRendererMap.put(databaseName, graphRenderer);
            }
        }
        return graphRenderer;
    }

    /**
     * Return specific HTML renders of plots from a given database.
     * 
     * @param databaseName      The name of the database to render plots from.
     * @param plotConfiguration The {@link GraphDisplayConfiguration} that governs
     *                          the layout of the rendered plots.
     * @param standalone        If true, plots will be viewable without any
     *                          surrounding HTML code. Otherwise, the HTML files
     *                          will assume they will be embedded as child divs
     *                          inside existing HTML pages.
     * @return The {@link GraphOperationResult} that contains the rendered HTML
     *         plots, as well as associated metadata about the transaction.
     */
    public GraphOperationResult getGraphHtml(
            String databaseName, GraphDisplayConfig plotConfiguration, boolean standalone) {
        IHtmlGraphRenderer graphRenderer = getGraphRenderer(PlotlyGraphRenderer.class, databaseName);
        if (graphRenderer != null) {
            return graphRenderer.getGraphHtml(plotConfiguration, standalone);
        } else {
            String logMessage = "No graphRenderer has been configured!";
            logger.logWarning(logMessage);

            GraphOperationResult result = new GraphOperationResult();
            result.setLog(logMessage);
            return result;
        }
    }

    /**
     * Given a database, export all of its plots as HTML graphs.
     * 
     * @param databaseName      The name of the database to render plots from.
     * @param plotConfiguration The {@link GraphDisplayConfiguration} that governs
     *                          the layout of the rendered plots.
     * @param destDirAbsPath    The absolute path to the destination directory where
     *                          rendered plots should be written to.
     */
    public void exportAllGraphHtml(
            String databaseName, GraphDisplayConfig plotConfiguration, String destDirAbsPath) {
        IHtmlGraphRenderer graphRenderer = getGraphRenderer(PlotlyGraphRenderer.class, databaseName);
        if(graphRenderer != null) {
            List<String> categories = new ArrayList<>(getDatabaseCategories(databaseName));
            graphRenderer.exportGraphHtml(plotConfiguration, categories, destDirAbsPath);
        }
    }

    /**
     * 
     * @param dbName
     * @param dataDir
     * @param configFile
     * @param exportDir
     * @throws IOException
     */
    public void exportAllGraphHtml(String dbName, File dataDir, File configFile, File exportDir) throws IOException {
        if(dataDir.exists()) {
            WatchrConfigJsonReader reader = new WatchrConfigJsonReader(dataDir.getAbsolutePath(), logger, fileReader);
            if(configFile.exists()) {
                String configFileContents = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
                WatchrConfig watchrConfig = reader.deserialize(configFileContents);
                if(exportDir.exists()) {
                    exportAllGraphHtml(dbName, watchrConfig.getGraphDisplayConfig(), exportDir.getAbsolutePath());
                }
            }
        }
    }    

    /**
     * Clears the cache of database names associated with specific graph renderers.
     */
    public void clearAllGraphingLibraries() {
        graphRendererMap.clear();
    }

    //////////
    // MAIN //
    //////////

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("watchr-core " + VERSION);
            System.out.println(
                    "Usage:  watchr-core [config file] [data file/folder] [optional: database location] [optional: export location]");
        } else if (args.length >= 2) {
            String configFileStr = args[0];
            String dataFileStr = args[1];

            String dbDirStr = "";
            if (args.length >= 3) {
                dbDirStr = args[2];
            }
            if (StringUtils.isBlank(dbDirStr)) {
                dbDirStr = new File("db").getAbsolutePath();
            }

            String exportDirStr = "";
            if (args.length >= 4) {
                exportDirStr = args[3];
            }
            if (StringUtils.isBlank(exportDirStr)) {
                exportDirStr = new File("graph").getAbsolutePath();
            }

            File configFile = new File(configFileStr);
            File dataFile = new File(dataFileStr);
            if (configFile.exists() && dataFile.exists()) {
                String configFileContents = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
                String dbName = "db_" + UUID.randomUUID().toString();
                Path dbDirPath = Files.createDirectories(Paths.get(dbDirStr));
                File dbDir = new File(dbDirPath.toString());

                WatchrCoreApp app = new WatchrCoreApp();
                app.connectDatabase(dbName, FileBasedDatabase.class, new Object[] { dbDir });

                File dataDir;
                if (dataFile.isDirectory()) {
                    dataDir = dataFile;
                    app.addToDatabase(dbName, dataDir.getAbsolutePath(), configFileContents);
                } else {
                    dataDir = dataFile.getParentFile();
                    app.addToDatabase(dbName, dataFile.getAbsolutePath(), configFileContents);
                }
                app.saveDatabase(dbName);

                Path exportDirPath = Files.createDirectories(Paths.get(exportDirStr));
                File exportDir = new File(exportDirPath.toString());
                app.exportAllGraphHtml(dbName, dataDir, configFile, exportDir);
            } else {
                if (!configFile.exists()) {
                    System.err.println("Config file " + configFileStr + " does not exist.");
                } else {
                    System.err.println("Data file/folder " + dataFileStr + " does not exist.");
                }
            }
        }
    }

    /**
     * Instantiates a new WatchrCoreApp.  This method should not be used
     * in practice - it is required by unit tests that need to start multiple
     * Watchr processes.
     * 
     * @param dbName
     * @param dbDir
     * @return
     */
    public static WatchrCoreApp initWatchrApp(String dbName, File dbDir) {
        WatchrCoreApp app = new WatchrCoreApp();
        app.connectDatabase(dbName, FileBasedDatabase.class, new Object[] { dbDir });
        return app;
    }

    /////////////////////
    // PRIVATE UTILITY //
    /////////////////////


    /////////////
    // UTILITY //
    /////////////

    protected boolean isStartFileValid(WatchrConfig config, String startFileAbsPath) {
        PlotsConfig plotsConfig = config.getPlotsConfig();
        FileConfig fileConfig = plotsConfig.getFileConfig();

        boolean proceed = fileReader.exists(startFileAbsPath);
        if(proceed) {
            logger.logInfo(startFileAbsPath + " exists (watchr-core)");
            if(!fileReader.isDirectory(startFileAbsPath)) {
                logger.logWarning(startFileAbsPath + " is not a directory (watchr-core)");
                proceed = FilenameUtils.getExtension(startFileAbsPath).equals(fileConfig.getFileExtension());
                if(!proceed) {
                    logger.logWarning(startFileAbsPath + " does not have the correct extension (watchr-core)");
                } else {
                    proceed = FilenameUtils.getBaseName(startFileAbsPath).matches(fileConfig.getFileNamePatternAsRegex());
                    if(!proceed) {
                        logger.logWarning(startFileAbsPath + " does not match the provided regex pattern for filenames (watchr-core)");
                    }
                }
            }
        } else {
            logger.logWarning(startFileAbsPath + " does not exist (watchr-core)");
        }
        return proceed;
    }    
}
