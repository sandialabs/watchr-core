/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author Elliott Ridgway
 */
public class FileUtil {

    private FileUtil() {}

    /**
	 * Given an arbitrary String, scrub it from characters that are illegal
	 * for filenames.
	 * 
	 * @param originalName The original String.
	 * @return The new String, appropriate for using as a filename.
	 */
	public static String removeIllegalCharactersFromFilename(String originalName) {
		return originalName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_"); //$NON-NLS-1$
	}

    /**
     * Make a file path relative around another "anchoring" file path.
     * 
     * @param anchorFilePath     The path to use as an anchor.
     * @param pathToMakeRelative The file path to make relative to the anchor path.
     * @param onePathIsDirectory Declares whether the path to make relative ends in
     *                           a directory, or the anchor path ends in a
     *                           directory, or both. If any of these conditions is
     *                           true, the method requires slightly different
     *                           path-building logic.
     * @return The relative file path. If the two path arguments share nothing in
     *         common, the original, unmodified pathToMakeRelative string is
     *         returned.
     */
	public static String createRelativeFilePath(String anchorFilePath, String pathToMakeRelative, boolean onePathIsDirectory) {
		String quoteFileSeparator = Pattern.quote(File.separator);
		
		List<String> anchorFilePathPieces = Arrays.asList(anchorFilePath.split(quoteFileSeparator));
		List<String> pathToMakeRelativePieces = Arrays.asList(pathToMakeRelative.split(quoteFileSeparator));
		
		int upDirectories = 0;
		int i = 0;
		for(i = 0; i < anchorFilePathPieces.size(); i++) {
			String nextAnchorPiece = anchorFilePathPieces.get(i);
			if(i < pathToMakeRelativePieces.size()) {
				String nextPathToMakeRelativePiece = pathToMakeRelativePieces.get(i);
				
				if(!nextAnchorPiece.equals(nextPathToMakeRelativePiece)) {
					if(onePathIsDirectory) {
						upDirectories = anchorFilePathPieces.size() - i;
					} else {
						upDirectories = anchorFilePathPieces.size() - i - 1;
					}
					break;
				}
			} else {
				upDirectories = pathToMakeRelativePieces.size() - anchorFilePathPieces.size();
			}
		}
		
		if(i == 0) {
			// This means the two paths have nothing in common.  In this case, we should just
			// return the target path the user is interested in.
			return pathToMakeRelative;
		}
		
		StringBuilder relativePath = new StringBuilder();
		for(int j = 0; j < upDirectories; j++) {
			relativePath.append("..").append(File.separator); //$NON-NLS-1$
		}
		
		for(; i < pathToMakeRelativePieces.size(); i++) {
			relativePath.append(pathToMakeRelativePieces.get(i));
			if(i < pathToMakeRelativePieces.size() - 1) {
				relativePath.append(File.separator);
			}
		}
		return relativePath.toString();
	}
	
    /**
     * Make a file path relative around another "anchoring" file path. This method
     * assumes that the anchor path points to a file, not a directory. If the anchor
     * path is for a directory, use
     * {@link FileUtil#createRelativeDirectoryPath(String, String)} instead.
     * 
     * @param anchorFilePath     The path to use as an anchor. Must point to a file
     *                           for this method.
     * @param pathToMakeRelative The file path to make relative to the anchor path.
     *                           Can be a directory or a file.
     * @return The relative file path. If the two path arguments share nothing in
     *         common, the original, unmodified pathToMakeRelative string is
     *         returned.
     */
	public static String createRelativeFilePath(String anchorFilePath, String pathToMakeRelative) {
		return createRelativeFilePath(anchorFilePath, pathToMakeRelative, false);
	}
	
    /**
     * Make a file path relative around another "anchoring" file path. This method
     * assumes that the anchor path or the relative path (or both) points to a
     * directory, not a file. If both paths terminate in a file, use
     * {@link FileUtil#createRelativeFilePath(String, String)} instead.
     * 
     * @param anchorDirPath      The path to use as an anchor. Must point to a
     *                           directory for this method.
     * @param pathToMakeRelative The file path to make relative to the anchor path.
     *                           Can be a directory or a file.
     * @return The relative file path. If the two path arguments share nothing in
     *         common, the original, unmodified pathToMakeRelative string is
     *         returned.
     */
	public static String createRelativeDirectoryPath(String anchorDirPath, String pathToMakeRelative) {
		return createRelativeFilePath(anchorDirPath, pathToMakeRelative, true);
	}
	
    /**
     * Given an absolute path (the "anchor") and a relative path, convert the
     * relative path into an absolute path based on the provided anchor path.
     * 
     * @param absolutePathAnchor The anchoring absolute path to start from.
     * @param relativePath       The relative path to convert to an absolute path.
     * @return The original relative path converted to an absolute path.
     * @throws IOException Thrown if the canonical path to targetAbsolutePath cannot
     *                     be resolved.
     */
	public static String createAbsoluteFilePath(String absolutePathAnchor, String relativePath) throws IOException {
		File anchorPath = new File(absolutePathAnchor);
		if(!anchorPath.isDirectory()) {
			anchorPath = new File(anchorPath.getParent());
		}
		File targetAbsolutePath = new File(anchorPath, relativePath);
		return targetAbsolutePath.getCanonicalPath();
	}	
}
