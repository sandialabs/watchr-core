/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr;

import java.io.File;
import java.util.List;
import java.util.Set;

import gov.sandia.watchr.cmd.CommandParser;
import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.config.PlotypusConfig;
import gov.sandia.watchr.config.WatchrConfig;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.config.file.DefaultFileReader;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.config.reader.WatchrConfigReader;
import gov.sandia.watchr.config.rule.RuleConfig;
import gov.sandia.watchr.config.rule.RuleConfig.RuleWhen;
import gov.sandia.watchr.db.DatabaseMetadata;
import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.db.PlotDatabaseSearchCriteria;
import gov.sandia.watchr.db.impl.AbstractDatabase;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.graph.library.GraphOperationResult;
import gov.sandia.watchr.graph.library.IHtmlGraphRenderer;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.log.Log4jLogger;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.parse.generators.rule.RuleGenerator;
import gov.sandia.watchr.parse.plotypus.Plotypus;

/**
 * The top-level public API for Watchr. Because only one instance of Watchr
 * should theoretically be available at any given time, care should be taken
 * with initializing multiple Watchr apps.
 * 
 * @author Elliott Ridgway
 */
public class WatchrCoreApp {

    ////////////
    // FIELDS //
    ////////////

    private static final String VERSION = "1.15.2";

    private static final String CLASSNAME = WatchrCoreApp.class.getSimpleName();
    public static final String CACHED_RUN_DIRECTORY = System.getProperty("user.dir") + File.separator + "watchrRun";
    public static final String CACHED_RUN_NAME = "lastRun";
    
    protected final WatchrCoreAppDatabaseSubsystem dbSubsystem;
    protected final WatchrCoreAppGraphSubsystem graphSubsystem;

    private IFileReader fileReader;
    private ILogger logger;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public WatchrCoreApp() {
        logger = new Log4jLogger();
        fileReader = new DefaultFileReader(logger);
        dbSubsystem = new WatchrCoreAppDatabaseSubsystem(logger, fileReader);
        graphSubsystem = new WatchrCoreAppGraphSubsystem(this, logger, fileReader);
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
        fileReader.setLogger(logger);
        dbSubsystem.setLogger(logger);
        graphSubsystem.setLogger(logger);
    }

    public void setFileReader(IFileReader fileReader) {
        this.fileReader = fileReader;
        dbSubsystem.setFileReader(fileReader);
        graphSubsystem.setLogger(logger);
    }


    //////////////
    // PLOTYPUS //
    //////////////

    /**
     * Create a new {@link Plotypus} based on the provided {@link WatchrConfig}, if
     * any. If no configuration exists, the plotypus is initialized with default
     * settings.
     * 
     * @return The created plotypus.
     */
    public Plotypus<WatchrConfig> createPlotypus(WatchrConfig config) {
        Plotypus<WatchrConfig> plotypus;
        PlotypusConfig plotypusConfig = config.getPlotsConfig().getPlotypusConfig();
        if(plotypusConfig != null) {
            plotypus = createPlotypus(plotypusConfig.getNumberOfTentacles());
            plotypus.setPayloadTimeout(plotypusConfig.getPayloadTimeout());
            plotypus.setPayloadTimeWarning(plotypusConfig.getPayloadTimeWarning());
        } else {
            plotypus = createPlotypus(10);
        }
        return plotypus;
    }

    /**
     * Create a new {@link Plotypus}.
     * 
     * @param tentacleCount The number of tentacles to give the plotypus.

     */
    public Plotypus<WatchrConfig> createPlotypus(int tentacleCount) {
        return new Plotypus<>(tentacleCount, logger);
    }


    /////////////////
    // CONFIG FILE //
    /////////////////

    /**
     * Create a new {@link WatchrConfigReader} object.
     * 
     * @param startFileAbsPath The absolute file path to the "start file" (either a
     *                         file or directory).
     * @return The new WatchrConfigJsonReader object.
     */
    public WatchrConfigReader createWatchrConfigReader(String startFileAbsPath) {
        return new WatchrConfigReader(startFileAbsPath, logger, fileReader);
    }

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
     * @param extension          The config file extension.
     */
    public void validateCompileTimeErrors(String startFileAbsPath, String configFileContents) {
        WatchrConfigReader reader = new WatchrConfigReader(startFileAbsPath, logger, fileReader);
        WatchrConfig watchrConfig = reader.deserialize(configFileContents);
        watchrConfig.validate();
    }

    public void applyBeforeRunRules(
            WatchrConfig config, AbstractDatabase db, List<WatchrDiff<?>> diffs) throws WatchrParseException {
        logger.logInfo("Applying database rules before updating plots...");
        List<RuleConfig> ruleConfigs = config.getRuleConfigs(RuleWhen.BEFORE);
        RuleGenerator ruleGenerator = new RuleGenerator(db, logger);
        ruleGenerator.generate(ruleConfigs, diffs);
    }

    public void applyAfterRunRules(
            WatchrConfig config, AbstractDatabase db, List<WatchrDiff<?>> diffs) throws WatchrParseException {
        logger.logInfo("Applying database rules after updating plots...");
        List<RuleConfig> ruleConfigs = config.getRuleConfigs(RuleWhen.AFTER);
        RuleGenerator ruleGenerator = new RuleGenerator(db, logger);
        ruleGenerator.generate(ruleConfigs, diffs);
    }
    
    public List<WatchrDiff<?>> loadDiffs(WatchrConfig watchrConfig, WatchrConfigReader reader,
            AbstractDatabase db) {
        List<WatchrDiff<?>> diffs = reader.getDiffs(db.getLastConfig(), watchrConfig);
        if (!diffs.isEmpty()) {
            logger.logInfo("Watchr config file has diffed since the last run.");
            logger.logDebug(diffs.toString(), CLASSNAME);
        }
        return diffs;
    }


    ////////////////////////
    // DATABASE SUBSYSTEM //
    ////////////////////////

    public void addToDatabase(
            Plotypus<WatchrConfig> plotypus, WatchrConfig watchrConfig, 
            AbstractDatabase db, List<WatchrDiff<?>> diffs, String startFileAbsPath) {
        dbSubsystem.addToDatabase(plotypus, watchrConfig, db, diffs, startFileAbsPath);
    }

    public void connectDatabase(String dbName, Class<? extends IDatabase> dbClassType, Object[] args) {
        dbSubsystem.connectDatabase(dbName, dbClassType, args);
    }

    public void clearNewPlotCountFromDatabase(String dbName) {
        dbSubsystem.clearNewPlotCount(dbName);
    }

    public boolean deletePlotFromDatabase(String databaseName, String plotName, String category) {
        return dbSubsystem.deletePlotFromDatabase(databaseName, plotName, category);
    }

    public boolean setNicknameInDatabase(String databaseName, String plotName, String category, String nickname){
        return dbSubsystem.setNicknameInDatabase(databaseName, plotName, category, nickname);
    }

    public AbstractDatabase getDatabaseAndAttachLogger(String dbName) {
        return dbSubsystem.getDatabaseAndAttachLogger(dbName);
    }

    public Set<String> getDatabaseCategories(String databaseName) {
        return dbSubsystem.getDatabaseCategories(databaseName);
    }

    public Set<String> getDatabaseFilenameCache(String databaseName) {
        return dbSubsystem.getDatabaseFilenameCache(databaseName);
    }

    public DatabaseMetadata getDatabaseMetadata(String databaseName) {
        return dbSubsystem.getDatabaseMetadata(databaseName);
    }    

    public GraphDisplayConfig getDatabaseGraphDisplayConfiguration(String databaseName) {
        return dbSubsystem.getDatabaseGraphDisplayConfiguration(databaseName);
    }

    public Set<PlotWindowModel> getDatabaseChildPlots(String databaseName, PlotDatabaseSearchCriteria search) {
        return dbSubsystem.getChildPlots(databaseName, search);
    }

    public PlotWindowModel getDatabaseParentPlot(String databaseName, PlotDatabaseSearchCriteria search) {
        return dbSubsystem.getParentPlot(databaseName, search);
    }

    public PlotWindowModel getDatabasePlot(String databaseName, PlotDatabaseSearchCriteria search) {
        return dbSubsystem.searchPlot(databaseName, search);
    }
    
    public int getFailedPlotsSizeFromDatabase(String databaseName) {
        return dbSubsystem.getFailedPlotsSize(databaseName);
    }    

    public int getPlotsSizeFromDatabase(String databaseName) {
        return dbSubsystem.getPlotsSize(databaseName);
    }    

    public void saveDatabase(String databaseName) {
        dbSubsystem.saveDatabase(databaseName);
    }    

    public void saveDatabase(Plotypus<WatchrConfig> plotypus, String dbName) {
        dbSubsystem.saveDatabase(plotypus, dbName);
    }    


    //////////////
    // GRAPHING //
    //////////////

    public void exportAllGraphHtml(
            String dbName, GraphDisplayConfig plotConfiguration, String destDirAbsPath) {
        graphSubsystem.exportAllGraphHtml(dbName, plotConfiguration, destDirAbsPath);
    }

    public GraphOperationResult getGraphHtml(
            String dbName, GraphDisplayConfig plotConfiguration, boolean standalone) {
        return graphSubsystem.getGraphHtml(dbName, plotConfiguration, standalone);
    }

    public IHtmlGraphRenderer getGraphRenderer(
            Class<? extends IHtmlGraphRenderer> graphLibraryType, String dbName) {
        return graphSubsystem.getGraphRenderer(graphLibraryType, dbName);
    }


    //////////
    // MAIN //
    //////////

    public static void main(String[] args) throws Exception {
        WatchrCoreApp app = new WatchrCoreApp();
        CommandParser parser = new CommandParser(app);
        if(args.length == 0) {
            parser.printHelp();
        } else {  
            parser.parse(args);
        }
    }

    public static void cmd(String command) throws Exception {
        main(new String[] { command });
    }

    public static void cmd(String command, String argValue) throws Exception {
        main(new String[] { command, argValue });
    }

    public static void cmd(String command1, String command2, String argValue) throws Exception {
        main(new String[] { command1, command2, argValue });
    }
}
