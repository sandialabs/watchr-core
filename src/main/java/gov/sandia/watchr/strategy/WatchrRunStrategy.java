package gov.sandia.watchr.strategy;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.config.WatchrConfig;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.config.reader.WatchrConfigReader;
import gov.sandia.watchr.db.impl.AbstractDatabase;
import gov.sandia.watchr.db.impl.FileBasedDatabase;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.parse.plotypus.Plotypus;

public class WatchrRunStrategy implements Serializable {
    
    ////////////
    // FIELDS //
    ////////////

    private String dbDirStr     = new File(System.getProperty("user.dir") + File.separator + "db").getAbsolutePath();
    private String exportDirStr = new File(System.getProperty("user.dir") + File.separator + "graph").getAbsolutePath();
    private List<String> dataFilePaths = new ArrayList<>();
    private String configFileStr;
    private Class<? extends ILogger> loggerClass;

    private transient Plotypus<WatchrConfig> plotypus;

    /////////////
    // SETTERS //
    /////////////

    public WatchrRunStrategy setDatabaseDir(File dbDir) {
        this.dbDirStr = dbDir.getAbsolutePath();
        return this;
    }

    public WatchrRunStrategy setDataFile(File dataFile) {
        this.dataFilePaths.add(dataFile.getAbsolutePath());
        return this;
    }

    public WatchrRunStrategy setConfigFile(File configFile) {
        this.configFileStr = configFile.getAbsolutePath();
        return this;
    }

    public WatchrRunStrategy setExportDir(File exportDir) {
        this.exportDirStr = exportDir.getAbsolutePath();
        return this;
    }

    public WatchrRunStrategy setLogger(ILogger logger) {
        this.loggerClass = logger.getClass();
        return this;
    }

    /////////
    // RUN //
    /////////

    public void run(WatchrCoreApp app) {
        try {
            File dbDir = new File(dbDirStr);
            String dbName = dbDir.getName();
            app.connectDatabase(dbName, FileBasedDatabase.class, new Object[] { dbDir });
            if(loggerClass != null) {
                app.setLogger(loggerClass.newInstance());
            }
            
            Iterator<String> dataFileIter = dataFilePaths.iterator();
            String dataPath = "";
            if(!dataFileIter.hasNext()) {
                throw new IllegalStateException("No data paths were provided for Watchr!");
            } else {
                dataPath = dataFileIter.next();
            }

            WatchrConfigReader reader = app.createWatchrConfigReader(dataPath);
            
            app.clearNewPlotCountFromDatabase(dbName);
            
            String configFileContents = FileUtils.readFileToString(new File(configFileStr), StandardCharsets.UTF_8);
            String extension = FilenameUtils.getExtension(configFileStr);
            WatchrConfig watchrConfig = reader.deserialize(configFileContents, extension);
            AbstractDatabase db = app.getDatabaseAndAttachLogger(dbName);
            List<WatchrDiff<?>> diffs = app.loadDiffs(watchrConfig, reader, db);
            
            app.applyBeforeRunRules(watchrConfig, db, diffs);
            
            plotypus = app.createPlotypus(watchrConfig);

            while(dataPath != null) {
                watchrConfig.setStartFileAbsPath(dataPath);
                app.addToDatabase(plotypus, watchrConfig, db, diffs, dataPath);
                if(dataFileIter.hasNext()) {
                    dataPath = dataFileIter.next();
                } else {
                    dataPath = null;
                }
            }
            plotypus.begin();
            plotypus.waitToFinish();

            app.applyAfterRunRules(watchrConfig, db, diffs);
            app.saveDatabase(plotypus, dbName);

            GraphDisplayConfig graphConfig = watchrConfig.getGraphDisplayConfig();
            app.exportAllGraphHtml(dbName, graphConfig, exportDirStr);

        } catch(WatchrParseException e) {
            app.getLogger().logError("An error occurred executing this Watchr strategy.", e);
            app.getLogger().logError("Original exception: ", e.getOriginalException());
        } catch(IllegalAccessException | InstantiationException | IOException e) {
            app.getLogger().logError("An error occurred executing this Watchr strategy.", e);
        } catch(InterruptedException e) {
            app.getLogger().logError("An interruption error occurred while executing this Watchr strategy.", e);
            Thread.currentThread().interrupt();
        } finally {
            if(plotypus != null) plotypus.kill();
        }
    }
}
