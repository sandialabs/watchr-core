package gov.sandia.watchr.file;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import gov.sandia.watchr.config.FileConfig;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.log.ILogger;

public class WatchrCoreAppFileManifest {

    ////////////
    // FIELDS //
    ////////////

    private static final String CLASSNAME = WatchrCoreAppFileManifest.class.getSimpleName();
    private final IDatabase db;
    private ILogger logger;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public WatchrCoreAppFileManifest(IDatabase db, ILogger logger) {
        this.db = db;
        this.logger = logger;
    }

    /////////////
    // GETTERS //
    /////////////

    public List<String> readReports(FileConfig fileConfig) {
        List<String> reportsToReturn = new ArrayList<>();
        List<String> reportsToRead = new ArrayList<>();

        if(fileConfig != null) {
            String startFile = fileConfig.getStartFile();
            IFileReader fileReader = fileConfig.getFileReader();
            if(fileReader.isDirectory(startFile)) {
                logger.logDebug("Loading subdirectory " + startFile, CLASSNAME);
                reportsToRead = getReports(startFile, fileConfig);
            } else if(fileReader.isFile(startFile)) {
                logger.logDebug("Loading file " + startFile, CLASSNAME);
                reportsToRead.add(startFile);
            } else {
                throw new IllegalStateException("File \"" + startFile + "\" could not be identified as a folder or a file!");
            }

            logger.logDebug("Number of reports to read: " + reportsToRead.size(), CLASSNAME);
            for(String report : reportsToRead) {
                logger.logDebug("Reading report " + report, CLASSNAME);
                if(!db.hasSeenFile(report) || !fileConfig.shouldIgnoreOldFiles()) {
                    logger.logInfo("Reading new report " + report);
                    reportsToReturn.add(report);
                } else {
                    logger.logDebug("Report " + report + " has already been parsed.", CLASSNAME);
                }
            }
        }       
        return reportsToReturn;
    }

    /////////////
    // PRIVATE //
    /////////////

    private List<String> getReports(String startDirAbsolutePath, FileConfig fileConfig) {
        List<String> reports = new ArrayList<>();

        if(fileConfig != null) {
            IFileReader fileReader = fileConfig.getFileReader();
            String extension = fileConfig.getFileExtension();
            String fileNamePattern = fileConfig.getFileNamePatternAsRegex();
            boolean recurseSubdirectories = fileConfig.shouldRecurseDirectories();

            logger.logDebug("Getting contents from " + startDirAbsolutePath + "...", CLASSNAME);
            List<String> childPaths = fileReader.getFolderContents(startDirAbsolutePath);

            for(String childFile : childPaths) {
                if(fileReader.isFile(childFile)) {
                    logger.logDebug("Loading file " + childFile, CLASSNAME);
                    logger.logDebug("File name (no extension) must match pattern " + fileNamePattern, CLASSNAME);
                    logger.logDebug("Extension must match pattern " + extension, CLASSNAME);

                    if(FilenameUtils.getBaseName(fileReader.getName(childFile)).matches(fileNamePattern) &&
                       FilenameUtils.getExtension(fileReader.getName(childFile)).equals(extension)) {
                        logger.logDebug("Adding file " + childFile, CLASSNAME);
                        reports.add(childFile);
                    }
                } else if(fileReader.isDirectory(childFile) && recurseSubdirectories) {
                    logger.logDebug("Loading subdirectory " + childFile, CLASSNAME);
                    List<String> subdirectoryReports = getReports(childFile, fileConfig);
                    reports.addAll(subdirectoryReports);
                } else {
                    logger.logDebug(childFile + " is not a file or a subdirectory.", CLASSNAME);
                }
            }
        }

        logger.logDebug("Returning reports of size " + reports.size(), CLASSNAME);
        return reports;
    }
}
