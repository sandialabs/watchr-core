package gov.sandia.watchr;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import gov.sandia.watchr.config.FileConfig;
import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.config.PlotsConfig;
import gov.sandia.watchr.config.WatchrConfig;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.db.DatabaseMetadata;
import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.db.PlotDatabaseSearchCriteria;
import gov.sandia.watchr.db.impl.AbstractDatabase;
import gov.sandia.watchr.db.impl.FileBasedDatabase;
import gov.sandia.watchr.file.WatchrCoreAppFileManifest;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.generators.WatchrConfigGenerator;
import gov.sandia.watchr.parse.plotypus.Plotypus;
import gov.sandia.watchr.parse.plotypus.TentaclePayload;

/**
 * @author Elliott Ridgway
 */
public class WatchrCoreAppDatabaseSubsystem {

    ////////////
    // FIELDS //
    ////////////
 
    private static final String CLASSNAME = WatchrCoreAppDatabaseSubsystem.class.getSimpleName();
    public static final String ALLCATEGORIES = "all-categories";

    private IFileReader fileReader;
    private ILogger logger;
    private final Map<String, IDatabase> dbCacheMap;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public WatchrCoreAppDatabaseSubsystem(ILogger logger, IFileReader fileReader) {
        this.logger = logger;
        this.fileReader = fileReader;
        dbCacheMap = new HashMap<>();
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


    //////////////////////
    // CONNECT DATABASE //
    //////////////////////

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
    public void connectDatabase(
            String databaseName, Class<? extends IDatabase> databaseType, Object[] args) {
                
        logger.logInfo("Attempting database connection (watchr-core)");
        if (dbCacheMap.containsKey(databaseName) && dbCacheMap.get(databaseName) != null) {
            logger.logInfo("Database with name " + databaseName + " has been previously initialized.");
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
                logger.logDebug("Successful DB creation? " + (success ? "Yes" : "No"), CLASSNAME);
            } else {
                logger.logDebug("Folder already exists", CLASSNAME);
            }

            IDatabase db = new FileBasedDatabase(rootDir, logger, fileReader);
            dbCacheMap.put(databaseName, db);
            db.loadState();

            logger.logDebug("FolderBasedDatabase was initialized with rootDir " + rootDir, CLASSNAME);
        } catch (Exception e) {
            logger.logError("An error occurred initializing the database", e);
        }
    }    


    /////////////
    // GETTERS //
    /////////////

    public IDatabase getDatabase(String databaseName) {
        return dbCacheMap.get(databaseName);
    }

    /**
     * Get a cached {@link AbstractDatabase} object, and attach the app's current
     * logger to it before returning.
     * 
     * @param databaseName The name of the database to look for in the cache.
     * @return The database, or null if it's not in the cache.
     */
    public AbstractDatabase getDatabaseAndAttachLogger(String databaseName) {
        AbstractDatabase db = (AbstractDatabase) dbCacheMap.get(databaseName);
        if (db != null) {
            db.setLogger(logger);
            return db;
        } else {
            logger.logWarning(databaseName + " database does not exist (watchr-core)");
        }
        return null;
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
            db.getCategories().add(ALLCATEGORIES);
            return db.getCategories();
        } else {
            logger.logWarning(databaseName + " is null (watchr-core)");
        }
        return new HashSet<>();
    }

    public Set<String> getDatabaseFilenameCache(String databaseName) {
        IDatabase db = dbCacheMap.get(databaseName);
        if (db != null) {
            return db.getFilenameCache();
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

    public DatabaseMetadata getDatabaseMetadata(String databaseName) {
        IDatabase db = dbCacheMap.get(databaseName);
        if (db != null) {
            return db.getMetadata();
        } else {
            logger.logWarning(databaseName + " is null (watchr-core)");
        }
        return null;
    }    

    /**
     * Return the number of plots in the database.
     * 
     * @param databaseName The name of the database.
     * @return The number of plots in the database.
     */
    public int getPlotsSize(String databaseName) {
        IDatabase db = dbCacheMap.get(databaseName);
        if (db != null) {
            return db.getMetadata().getPlotCount();
        }
        return 0;
    }

    /**
     * Return the number of plots in the database that are marked as being in a
     * "failure" state. See {@link RulePlotTraceModelFailActor} for more
     * information.
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

    public PlotWindowModel searchPlot(String dbName, PlotDatabaseSearchCriteria search) {
        IDatabase db = dbCacheMap.get(dbName);
        return db.searchPlot(search);
    }

    public PlotWindowModel getParentPlot(String dbName, PlotDatabaseSearchCriteria search) {
        IDatabase db = dbCacheMap.get(dbName);
        return db.getParent(search);
    }

    public Set<PlotWindowModel> getChildPlots(String dbName, PlotDatabaseSearchCriteria search) {
        IDatabase db = dbCacheMap.get(dbName);
        return db.getChildren(search);
    }


    /////////////////////
    // MODIFY DATABASE //
    /////////////////////

    /**
     * Add more data to the database. This is a multi-step method that does the
     * following:<br>
     * 1. Read the file config section from the provided {@link WatchrConfig}
     * argument.<br>
     * 2. Using the {@code startFileAbsPath} argument, read in a list of report data
     * files located at that path.<br>
     * 3. Calculate diffs between the current WatchrConfig argument and the
     * {@link AbstractDatabase}'s last-loaded configuration. 4. Use the provided
     * {@link Plotypus} to process the report files in a multi-threaded way.
     * 
     * @param plotypus         The plotypus provided to process the report files.
     * @param watchrConfig     The overall configuration for interpreting the report
     *                         files.
     * @param reader           The reader to use for finding diffs in the
     *                         configuration file.
     * @param db               The database
     * @param diffs            Diffs in the config file.
     * @param startFileAbsPath The start path that points Watchr to the report data
     *                         that needs to be processed.
     */
    public void addToDatabase(
            Plotypus<WatchrConfig> plotypus, WatchrConfig watchrConfig,
            AbstractDatabase db, List<WatchrDiff<?>> diffs, String startFileAbsPath) {

        if(isStartFileValid(watchrConfig, startFileAbsPath)) {
            FileConfig fileConfig = new FileConfig(watchrConfig.getPlotsConfig().getFileConfig());
            WatchrCoreAppFileManifest fileManifest = new WatchrCoreAppFileManifest(db, logger);
            
            List<String> reportsToRead = fileManifest.readReports(fileConfig);

            if(fileConfig.shouldRandomizeFileOrder()) {
                Collections.shuffle(reportsToRead);
            }
            sendPayloadsToPlotypus(plotypus, reportsToRead, watchrConfig, db, diffs);
        } else {
            logger.logWarning("startFileAbsPath is not valid (watchr-core)");
        }
    }

    boolean isStartFileValid(WatchrConfig config, String startFileAbsPath) {
        PlotsConfig plotsConfig = config.getPlotsConfig();
        FileConfig fileConfig = plotsConfig.getFileConfig();

        boolean proceed = fileReader.exists(startFileAbsPath);
        if (proceed) {
            logger.logDebug(startFileAbsPath + " exists (watchr-core)", CLASSNAME);
            if (!fileReader.isDirectory(startFileAbsPath)) {
                logger.logInfo(startFileAbsPath + " is not a directory (watchr-core)");
                proceed = FilenameUtils.getExtension(startFileAbsPath).equals(fileConfig.getFileExtension());
                if (!proceed) {
                    logger.logWarning(startFileAbsPath + " does not have the correct extension (watchr-core)");
                } else {
                    proceed = FilenameUtils.getBaseName(startFileAbsPath)
                            .matches(fileConfig.getFileNamePatternAsRegex());
                    if (!proceed) {
                        logger.logWarning(startFileAbsPath
                                + " does not match the provided regex pattern for filenames (watchr-core)");
                    }
                }
            }
        } else {
            logger.logWarning(startFileAbsPath + " does not exist (watchr-core)");
        }
        return proceed;
    }

    private void sendPayloadsToPlotypus(Plotypus<WatchrConfig> plotypus, List<String> reportsToRead,
            WatchrConfig watchrConfig, AbstractDatabase db, List<WatchrDiff<?>> diffs) {

        for(String report : reportsToRead) {
            List<String> singleReportList = new ArrayList<>();
            singleReportList.add(report);
            WatchrConfig configCopy = new WatchrConfig(watchrConfig);
            WatchrConfigGenerator watchrConfigGenerator = new WatchrConfigGenerator(db, singleReportList);

            TentaclePayload<WatchrConfig> payload = new TentaclePayload<>(watchrConfigGenerator, configCopy, diffs);
            logger.logDebug("Adding payload for report " + report, CLASSNAME);
            plotypus.addPayload(payload);
        }
    }    

    public boolean setNicknameInDatabase(String databaseName, String plotName, String category, String nickname){
        logger.logInfo("Attempting to set nickname for plot " + plotName + " in category " + category + " with nickname " + nickname + "...");
        IDatabase db = dbCacheMap.get(databaseName);
        if (db != null) {
            db.setLogger(logger);
            logger.logDebug("Found database " + databaseName, CLASSNAME);
            PlotWindowModel windowModel = db.searchPlot(new PlotDatabaseSearchCriteria(plotName, category));
            if (windowModel != null) {
                logger.logDebug("Found plot.  Setting nickname...", CLASSNAME);
                String plotUUID = windowModel.getUUID().toString();
                db.setNickname(plotUUID, nickname);
                logger.logInfo("Setting nickname was successful");
                logger.logDebug("Saving database state to disk", CLASSNAME);
                db.saveState();
                return true;
            } else {
                logger.logWarning("Could not find plot " + plotName + " in category " + category);
            }
        }
        logger.logWarning("Could not find database " + databaseName);
        return false;
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
            logger.logDebug("Found database " + databaseName, CLASSNAME);
            PlotWindowModel windowModel = db.searchPlot(new PlotDatabaseSearchCriteria(plotName, category));
            if (windowModel != null) {
                logger.logDebug("Found plot.  Deleting...", CLASSNAME);
                String plotUUID = windowModel.getUUID().toString();
                db.deletePlot(plotUUID);
                logger.logInfo("Delete was successful");
                logger.logDebug("Saving database state to disk", CLASSNAME);
                db.saveState();
                return true;
            } else {
                logger.logWarning("Could not find plot " + plotName + " in category " + category);
            }
        }
        logger.logWarning("Could not find database " + databaseName);
        return false;
    }

    /**
     * Clear a database's record of new plots found. This is typically done at the
     * beginning of a new use of Watchr to process a new dataset.
     * 
     * @param databaseName The name of the database to clear the plot count for.
     */
    public void clearNewPlotCount(String databaseName) {
        IDatabase db = dbCacheMap.get(databaseName);
        if (db != null) {
            db.getMetadata().setNewPlotCount(0);
        }
    }    

    /**
     * Save the database. This method saves regardless of whether any associated
     * {@link Plotypus} objects are still working.
     * 
     * @param databaseName The name of the database to save.
     */
    public void saveDatabase(String databaseName) {
        saveDatabase(null, databaseName);
    }

    /**
     * Save the database, but wait on the provided {@link Plotypus} to finish
     * working before saving.
     * 
     * @param plotypus     The plotypus to wait on.
     * @param databaseName The name of the database to save to.
     */
    public void saveDatabase(Plotypus<WatchrConfig> plotypus, String databaseName) {
        try {
            IDatabase db = dbCacheMap.get(databaseName);

            if(plotypus != null) {
                if(plotypus.isAlive() && plotypus.isWorking()) {
                    plotypus.waitToFinish();
                }
                if(db != null) {
                    removeFailedReportsFromDatabase(plotypus, db);
                }
            }

            if(db != null) {
                db.saveState();
            }
        } catch (InterruptedException e) {
            logger.logError("Interrupted exception!", e);
            Thread.currentThread().interrupt();
        }
    }

    private void removeFailedReportsFromDatabase(Plotypus<WatchrConfig> plotypus, IDatabase db) {
        List<TentaclePayload<WatchrConfig>> payloads = plotypus.getFailedPayloads();
        for(TentaclePayload<WatchrConfig> payload : payloads) {
            WatchrConfigGenerator generator = (WatchrConfigGenerator) payload.getGenerator();
            List<String> reportAbsPaths = generator.getReportAbsPaths();
            for(String report : reportAbsPaths) {
                db.removeFileFromCache(report);
                logger.logWarning("Watchr failed to process report " + report + ", but can try again on the next run.");
            }
        }
    }
}
