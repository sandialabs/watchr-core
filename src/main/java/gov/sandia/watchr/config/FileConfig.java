/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.util.StringUtil;

public class FileConfig implements IConfig {

    ////////////
    // FIELDS //
    ////////////

    private final String startFileAbsPath;

    private String fileNamePattern = "";
    private String fileExtension = "";
    private boolean ignoreOldFiles = false;
    private boolean recurseDirectories = false;

    private final String configPath;
    private final ILogger logger;
    private final IFileReader fileReader;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public FileConfig(String configPathPrefix, ILogger logger, IFileReader fileReader) {
        this.logger = logger;
        this.startFileAbsPath = null;
        this.configPath = configPathPrefix + "/fileConfig";
        this.fileReader = fileReader;
    }

    public FileConfig(String startFileAbsPath, String configPathPrefix, ILogger logger, IFileReader fileReader) {
        this.logger = logger;
        this.startFileAbsPath = startFileAbsPath;
        this.configPath = configPathPrefix + "/fileConfig";
        this.fileReader = fileReader;
    }

    public FileConfig(FileConfig copy) {
        this.logger = copy.logger;
        this.startFileAbsPath = (copy.getStartFile() != null) ? copy.getStartFile() : null;
        this.fileNamePattern = copy.getFileNamePattern();
        this.fileExtension = copy.getFileExtension();
        this.ignoreOldFiles = copy.shouldIgnoreOldFiles();
        this.recurseDirectories = copy.shouldRecurseDirectories();
        this.configPath = copy.getConfigPath();
        this.fileReader = copy.getFileReader();
    }

    /////////////
    // GETTERS //
    /////////////

    public String getStartFile() {
        return startFileAbsPath;
    }

    public boolean shouldIgnoreOldFiles() {
        return ignoreOldFiles;
    }

    public String getFileNamePattern() {
        return fileNamePattern;
    }

    public String getFileNamePatternAsRegex() {
        return StringUtil.convertToRegex(fileNamePattern);
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

    @Override
    public ILogger getLogger() {
        return logger;
    }

    public IFileReader getFileReader() {
        return fileReader;
    }

    /////////////
    // SETTERS //
    /////////////

    public void setIgnoreOldFiles(boolean ignoreOldFiles) {
        this.ignoreOldFiles = ignoreOldFiles;
    }

    public void setFileNamePattern(String fileNamePattern) {
        this.fileNamePattern = fileNamePattern;
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
        if(startFileAbsPath == null) {
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, "A directory for parseable reports was not provided."));
        } else if(!fileReader.exists(startFileAbsPath)) {
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, "Directory \"" + startFileAbsPath + "\" does not exist."));
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

            boolean startDirsXorNull = startFileAbsPath == null ^ otherFileConfig.startFileAbsPath == null;
            if(startDirsXorNull || (startFileAbsPath != null && !(startFileAbsPath.equals(otherFileConfig.startFileAbsPath)))) {
                WatchrDiff<String> diff = new WatchrDiff<>(configPath, DiffCategory.START_DIR);
                diff.setBeforeValue(startFileAbsPath);
                diff.setNowValue(otherFileConfig.startFileAbsPath);
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

            if(startFileAbsPath != null && otherFileConfig.startFileAbsPath != null) {
                equals = startFileAbsPath.equals(otherFileConfig.startFileAbsPath);
            } else if(startFileAbsPath == null && otherFileConfig.startFileAbsPath == null) {
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
        if(startFileAbsPath != null) {
            hash = 31 * (hash + startFileAbsPath.hashCode());
        }
        hash = 31 * (hash + fileNamePattern.hashCode());
        hash = 31 * (hash + fileExtension.hashCode());
        hash = 31 * (hash + Boolean.hashCode(ignoreOldFiles));
        hash = 31 * (hash + Boolean.hashCode(recurseDirectories));
        return hash;
    }  
}
