package gov.sandia.watchr.config.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.log.ILogger;

/**
 * Default implementation of {@link IFileReader} that uses
 * the standard Java {@link File} cass.  Paths provided
 * should be absolute system filepaths.
 * 
 * @author Elliott Ridgway
 */
public class DefaultFileReader implements IFileReader {

    private ILogger logger;

    public DefaultFileReader(ILogger logger) {
        this.logger = logger;
    }

    @Override
    public String readFromFile(String absolutePath) {
        try {
            File file = new File(absolutePath);
            return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch(IOException e) {
            logger.logError("Could not read file at " + absolutePath, e);
        }
        return "";
    }

    @Override
    public void writeToFile(String destinationFileAbsPath, String fileContents) {
        try {
            File destinationFile = new File(destinationFileAbsPath);
            FileUtils.write(destinationFile, fileContents, StandardCharsets.UTF_8);
        } catch(IOException e) {
            logger.logError("Could not write to file at " + destinationFileAbsPath, e);
        }
    }

    @Override
    public List<String> getFolderContents(String absolutePath) {
        List<String> folderContents = new ArrayList<>();
        File folder = new File(absolutePath);
        if(folder.isDirectory()) {
            for(File child : folder.listFiles()) {
                folderContents.add(child.getAbsolutePath());
            }
        }
        return folderContents;
    }

    @Override
    public boolean isFile(String absolutePath) {
        if(StringUtils.isNotBlank(absolutePath)) {
            File file = new File(absolutePath);
            if(file.exists()) {
                return file.isFile();
            }
        }
        return false;
    }

    @Override
    public boolean isDirectory(String absolutePath) {
        if(StringUtils.isNotBlank(absolutePath)) {
            File file = new File(absolutePath);
            if(file.exists()) {
                return file.isDirectory();
            }
        }
        return false;
    }

    @Override
    public String getName(String absolutePath) {
        File file = new File(absolutePath);
        return file.getName();
    }

    @Override
    public boolean exists(String absolutePath) {
        File file = new File(absolutePath);
        return file.exists();
    }

    @Override
    public void setLogger(ILogger logger) {
        this.logger = logger;
    }
}
