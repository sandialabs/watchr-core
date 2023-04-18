package gov.sandia.watchr.config.file;

import java.util.List;

import gov.sandia.watchr.log.ILogger;

/**
 * Watchr is required to be flexible enough to retrieve files and folders
 * through interfaces apart from the standard Java File class. This interface
 * abstracts specific filesystem mechanics away from Watchr.<br>
 * <br>
 * It is recommended that filepaths provided to this interface be absolute, but
 * filepaths being absolute or relative is ultimately up to the discretion of
 * the implementation.
 * 
 * @author Elliott Ridgway
 */
public interface IFileReader {

    /**
     * Get the contents of a particular file as a String.
     * 
     * @param path The path to the file.
     * @return The contents of the file.
     */
    public String readFromFile(String path);

    /**
     * Write a String to a file.
     * 
     * @param path The path to the file.
     * @param fileContents The contents to write to the file.
     */
    public void writeToFile(String path, String fileContents);

    /**
     * Get the child files/folders for the given folder.
     * 
     * @param path The path to the folder.
     * @return The child contents of the folder.
     */
    public List<String> getFolderContents(String path);

    /**
     * 
     * @param path The path to the file or folder.
     * @return Whether it exists on the filesystem.
     */
    public boolean exists(String path);

    /**
     * 
     * @param path The path to the file or folder.
     * @return Whether the path points to a non-directory file.
     */
    public boolean isFile(String path);

    /**
     * 
     * @param path The path to the file or folder.
     * @return Whether the path points to a directory.
     */
    public boolean isDirectory(String path);

    /**
     * 
     * @param path The path to the file or folder.
     * @return The name of the file or folder, independent of its path.
     */
    public String getName(String path);

    /**
     * 
     * @param logger The logger to use.
     */
    public void setLogger(ILogger logger);
}
