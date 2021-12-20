/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config.reader;

import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import gov.sandia.watchr.config.FileConfig;
import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
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
    public FileConfig handle(JsonElement jsonElement, IConfig parent) {
        FileConfig fileConfig = new FileConfig(startDirectoryAbsolutePath, parent.getConfigPath(), logger, fileReader);

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
        for(Entry<String, JsonElement> entry : entrySet) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if(key.equals(Keywords.FILENAME)) {
                seenKeywords.add(Keywords.FILENAME);
                fileConfig.setFileNamePattern(value.getAsString());
            } else if(key.equals(Keywords.TYPE)) {
                seenKeywords.add(Keywords.TYPE);
                fileConfig.setFileExtension(value.getAsString());
            } else if(key.equals(Keywords.IGNORE_OLD_FILES)) {
                seenKeywords.add(Keywords.IGNORE_OLD_FILES);
                fileConfig.setIgnoreOldFiles(value.getAsBoolean());
            } else if(key.equals(Keywords.RECURSE_DIRECTORIES)) {
                seenKeywords.add(Keywords.RECURSE_DIRECTORIES);
                fileConfig.setRecurseDirectories(value.getAsBoolean());
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
