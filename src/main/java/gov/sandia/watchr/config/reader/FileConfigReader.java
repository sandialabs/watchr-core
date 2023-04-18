/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config.reader;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import gov.sandia.watchr.config.FileConfig;
import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.element.ConfigConverter;
import gov.sandia.watchr.config.element.ConfigElement;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.log.ILogger;

public class FileConfigReader extends AbstractConfigReader<FileConfig> {

    ////////////
    // FIELDS //
    ////////////

    private final String startDirectoryAbsolutePath;
    private final IFileReader fileReader;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FileConfigReader(String startDirectoryAbsolutePath, ILogger logger, IFileReader fileReader) {
        super(logger);
        this.fileReader = fileReader;
        this.startDirectoryAbsolutePath = startDirectoryAbsolutePath;
    }

    //////////////
    // OVERRIDE //
    //////////////
    
    @Override
    public FileConfig handle(ConfigElement element, IConfig parent) {
        FileConfig fileConfig = new FileConfig(startDirectoryAbsolutePath, parent.getConfigPath(), logger, fileReader);

        ConfigConverter converter = element.getConverter();
        Map<String, Object> map = element.getValueAsMap();
        for(Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if(key.equals(Keywords.FILENAME)) {
                seenKeywords.add(Keywords.FILENAME);
                fileConfig.setFileNamePattern(converter.asString(value));
            } else if(key.equals(Keywords.TYPE)) {
                seenKeywords.add(Keywords.TYPE);
                fileConfig.setFileExtension(converter.asString(value));
            } else if(key.equals(Keywords.IGNORE_OLD_FILES)) {
                seenKeywords.add(Keywords.IGNORE_OLD_FILES);
                fileConfig.setIgnoreOldFiles(converter.asBoolean(value));
            } else if(key.equals(Keywords.RECURSE_DIRECTORIES)) {
                seenKeywords.add(Keywords.RECURSE_DIRECTORIES);
                fileConfig.setRecurseDirectories(converter.asBoolean(value));
            } else if(key.equals(Keywords.RANDOMIZE_FILE_ORDER)) {
                seenKeywords.add(Keywords.RANDOMIZE_FILE_ORDER);
                fileConfig.setShouldRandomizeFileOrder(converter.asBoolean(value));
            } else {
                logger.log(new WatchrConfigError(ErrorLevel.WARNING, "handleAsFileConfig: Unrecognized element `" + key + "`."));
            }
        }

        validateMissingKeywords();
        return fileConfig;
    }

    @Override
    public Set<String> getRequiredKeywords() {
        Set<String> requiredKeywords = new HashSet<>();
        requiredKeywords.add(Keywords.FILENAME);
        requiredKeywords.add(Keywords.TYPE);
        return requiredKeywords;
    }
}
