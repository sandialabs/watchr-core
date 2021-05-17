/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.log.ILogger;

public class FileConfig implements IConfig {

    ////////////
    // FIELDS //
    ////////////

    private final File startDir;

    private String fileNamePattern = "";
    private String fileExtension = "";
    private boolean ignoreOldFiles = false;
    private boolean recurseDirectories = false;

    private final String configPath;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public FileConfig(String configPathPrefix) {
        this.startDir = null;
        this.configPath = configPathPrefix + "/fileConfig";
    }

    public FileConfig(File startDir, String configPathPrefix) {
        this.startDir = startDir;
        this.configPath = configPathPrefix + "/fileConfig";
    }

    public FileConfig(FileConfig copy) {
        this.startDir = (copy.getStartDir() != null) ? new File(copy.getStartDir().getAbsolutePath()) : null;
        this.fileNamePattern = copy.getFileNamePattern();
        this.fileExtension = copy.getFileExtension();
        this.ignoreOldFiles = copy.shouldIgnoreOldFiles();
        this.recurseDirectories = copy.shouldRecurseDirectories();
        this.configPath = copy.getConfigPath();
    }

    /////////////
    // GETTERS //
    /////////////

    public File getStartDir() {
        return startDir;
    }

    public boolean shouldIgnoreOldFiles() {
        return ignoreOldFiles;
    }

    public String getFileNamePattern() {
        return fileNamePattern;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public boolean shouldRecurseDirectories() {
        return recurseDirectories;
    }

    @Override
    public String getConfigPath() {
        return configPath;
    }

    /////////////
    // SETTERS //
    /////////////

    public void setIgnoreOldFiles(boolean ignoreOldFiles) {
        this.ignoreOldFiles = ignoreOldFiles;
    }

    public void setFileNamePattern(String fileNamePattern) {
        // Note: Must convert general wildcard syntax to Java regex syntax.
        this.fileNamePattern = fileNamePattern.replace("*", ".*");
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public void setRecurseDirectories(boolean recurseDirectories) {
        this.recurseDirectories = recurseDirectories;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void validate() {
        ILogger logger = WatchrCoreApp.getInstance().getLogger();

        if(startDir == null) {
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, "A directory for parseable reports was not provided."));
        } else if(!startDir.exists()) {
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, startDir + " does not exist."));
        }
        if(StringUtils.isBlank(fileNamePattern)) {
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, "Pattern for finding report files cannot be blank!"));
        }
        if(StringUtils.isBlank(fileExtension)) {
            String message =
                "No file type extension was provided.  It is strongly recommend that at least one file extension " +
                "is specified so Watchr knows how to parse your report files.";
                logger.log(new WatchrConfigError(ErrorLevel.WARNING, message));
        }
    }

    @Override
    public List<WatchrDiff<?>> diff(IConfig other) {
        List<WatchrDiff<?>> diffList = new ArrayList<>();
        if(other instanceof FileConfig) {
            FileConfig otherFileConfig = (FileConfig) other;

            boolean startDirsXorNull = startDir == null ^ otherFileConfig.startDir == null;
            if(startDirsXorNull || (startDir != null && !(startDir.equals(otherFileConfig.startDir)))) {
                WatchrDiff<File> diff = new WatchrDiff<>(configPath, DiffCategory.START_DIR);
                diff.setBeforeValue(startDir);
                diff.setNowValue(otherFileConfig.startDir);
                diffList.add(diff);
            }
            if(!(fileNamePattern.equals(otherFileConfig.fileNamePattern))) {
                WatchrDiff<String> diff = new WatchrDiff<>(configPath, DiffCategory.FILENAME_PATTERN);
                diff.setBeforeValue(fileNamePattern);
                diff.setNowValue(otherFileConfig.fileNamePattern);
                diffList.add(diff);
            }
            if(!(fileExtension.equals(otherFileConfig.fileExtension))) {
                WatchrDiff<String> diff = new WatchrDiff<>(configPath, DiffCategory.FILE_EXTENSION);
                diff.setBeforeValue(fileExtension);
                diff.setNowValue(otherFileConfig.fileExtension);
                diffList.add(diff);
            }
            if(ignoreOldFiles != otherFileConfig.ignoreOldFiles) {
                WatchrDiff<Boolean> diff = new WatchrDiff<>(configPath, DiffCategory.IGNORE_OLD_FILES);
                diff.setBeforeValue(ignoreOldFiles);
                diff.setNowValue(otherFileConfig.ignoreOldFiles);
                diffList.add(diff);
            }
            if(recurseDirectories != otherFileConfig.recurseDirectories) {
                WatchrDiff<Boolean> diff = new WatchrDiff<>(configPath, DiffCategory.RECURSE_DIRECTORIES);
                diff.setBeforeValue(recurseDirectories);
                diff.setNowValue(otherFileConfig.recurseDirectories);
                diffList.add(diff);
            }
        }

        return diffList;
    }

    @Override
    public boolean equals(Object other) {
        boolean equals = false;
		if(other == null) {
            return false;
        } else if(other == this) {
            return true;
        } else if(getClass() != other.getClass()) {
            return false;
        } else {
			FileConfig otherFileConfig = (FileConfig) other;

            if(startDir != null && otherFileConfig.startDir != null) {
                equals = startDir.equals(otherFileConfig.startDir);
            } else if(startDir == null && otherFileConfig.startDir == null) {
                equals = true;
            }
            equals = equals && fileNamePattern.equals(otherFileConfig.fileNamePattern);
            equals = equals && fileExtension.equals(otherFileConfig.fileExtension);
            equals = equals && ignoreOldFiles == otherFileConfig.ignoreOldFiles;
            equals = equals && recurseDirectories == otherFileConfig.recurseDirectories;
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        if(startDir != null) {
            hash = 31 * (hash + startDir.hashCode());
        }
        hash = 31 * (hash + fileNamePattern.hashCode());
        hash = 31 * (hash + fileExtension.hashCode());
        hash = 31 * (hash + Boolean.hashCode(ignoreOldFiles));
        hash = 31 * (hash + Boolean.hashCode(recurseDirectories));
        return hash;
    }  
}
