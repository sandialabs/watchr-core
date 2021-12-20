/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.generators;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import gov.sandia.watchr.config.FileConfig;
import gov.sandia.watchr.config.file.IFileReader;

public abstract class FileAwareGenerator<E> extends AbstractGenerator<E> {
    
    ////////////
    // FIELDS //
    ////////////

    protected final FileConfig fileConfig;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    protected FileAwareGenerator(FileConfig fileConfig) {
        super(fileConfig.getLogger());
        this.fileConfig = fileConfig;
    }

    /////////////
    // GETTERS //
    /////////////

    protected List<String> getReports(String startDirAbsolutePath) {
        List<String> reports = new ArrayList<>();

        if(fileConfig != null) {
            IFileReader fileReader = fileConfig.getFileReader();
            String extension = fileConfig.getFileExtension();
            String fileNamePattern = fileConfig.getFileNamePatternAsRegex();
            boolean recurseSubdirectories = fileConfig.shouldRecurseDirectories();

            logger.logDebug("Getting contents from " + startDirAbsolutePath + "...");
            List<String> childPaths = fileReader.getFolderContents(startDirAbsolutePath);

            for(String childFile : childPaths) {
                if(fileReader.isFile(childFile)) {
                    logger.logDebug("Loading file " + childFile);
                    if(FilenameUtils.getBaseName(fileReader.getName(childFile)).matches(fileNamePattern) &&
                       FilenameUtils.getExtension(fileReader.getName(childFile)).equals(extension)) {
                        logger.logDebug("Adding file " + childFile);
                        reports.add(childFile);
                    }
                } else if(fileReader.isDirectory(childFile) && recurseSubdirectories) {
                    logger.logDebug("Loading subdirectory " + childFile);
                    List<String> subdirectoryReports = getReports(childFile);
                    reports.addAll(subdirectoryReports);
                } else {
                    logger.logDebug(childFile + " is not a file or a subdirectory.");
                }
            }
        }

        logger.logDebug("Returning reports of size " + reports.size());
        return reports;
    }
}
