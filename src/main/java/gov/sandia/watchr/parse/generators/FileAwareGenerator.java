/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.generators;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import gov.sandia.watchr.config.FileConfig;

public abstract class FileAwareGenerator<E> extends AbstractGenerator<E> {
    
    ////////////
    // FIELDS //
    ////////////

    protected final FileConfig fileConfig;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    protected FileAwareGenerator(FileConfig fileConfig) {
        this.fileConfig = fileConfig;
    }

    /////////////
    // GETTERS //
    /////////////

    protected File getReportLocation() {
        if(fileConfig != null) {
            return fileConfig.getStartDir();
        }
        return null;
    }

    protected List<File> getReports(File startDir) {
        List<File> reports = new ArrayList<>();

        if(fileConfig != null) {
            String extension = fileConfig.getFileExtension();
            String fileNamePattern = fileConfig.getFileNamePattern();
            boolean recurseSubdirectories = fileConfig.shouldRecurseDirectories();

            for(File file : startDir.listFiles()) {
                if(file.isFile()) {
                    if(FilenameUtils.getBaseName(file.getName()).matches(fileNamePattern) &&
                       FilenameUtils.getExtension(file.getName()).equals(extension)) {
                        reports.add(file);
                    }
                } else if(file.isDirectory() && recurseSubdirectories) {
                    List<File> subdirectoryReports = getReports(file);
                    reports.addAll(subdirectoryReports);
                }
            }
        }

        return reports;
    }
}
