package gov.sandia.watchr;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;

import gov.sandia.watchr.graph.chartreuse.model.PlotTracePoint;
import gov.sandia.watchr.util.OsUtil;

public class TestFileUtils {

    private static final String LOG_FILE = "logFile.txt";

    /**
     * Utility method for reading a test file that is local to the src/test/resources
     * directory to a String object.
     * 
     * @param governingClass Used to orient a {@link ClassLoader} for loading from the
     * resources directory.
     * @param relativePath The path to the test resource file, starting from "src/test/resources".
     * @return The contents of the file as a String.
     * @throws IOException Thrown if there was an error finding the file.
     * @throws URISyntaxException Thrown if there was a URL error in the provided file path.
     */
    public static String readTestFileToString(Class<?> governingClass, String relativePath)
            throws IOException, URISyntaxException {

        ClassLoader classLoader = governingClass.getClassLoader();
        URL expectedFileURL = classLoader.getResource(relativePath);
        if(expectedFileURL != null) {                
            File expectedFile = new File(expectedFileURL.toURI());
            if(expectedFile.exists()) {
                return FileUtils.readFileToString(expectedFile, StandardCharsets.UTF_8);
            } else {
                throw new FileNotFoundException("File does not exist at path " + expectedFile.getPath());
            }
        } else {
            throw new FileNotFoundException("Could not find test file.  URL: " + expectedFileURL);
        }
    }

    /**
     * Utility method for getting a test file that is local to the src/test/resources
     * directory to a String object.
     * 
     * @param governingClass Used to orient a {@link ClassLoader} for loading from the
     * resources directory.
     * @param relativePath The path to the test resource file, starting from "src/test/resources".
     * @return The contents of the file as a String.
     * @throws IOException Thrown if there was an error finding the file.
     * @throws URISyntaxException Thrown if there was a URL error in the provided file path.
     */
    public static File getTestFile(Class<?> governingClass, String relativePath)
            throws IOException, URISyntaxException {

        ClassLoader classLoader = governingClass.getClassLoader();
        URL expectedFileURL = classLoader.getResource(relativePath);
        if(expectedFileURL != null) {                
            File expectedFile = new File(expectedFileURL.toURI());
            if(expectedFile.exists()) {
                return expectedFile;
            } else {
                throw new FileNotFoundException("File does not exist at path " + expectedFile.getPath());
            }
        } else {
            throw new FileNotFoundException("Could not find test file.  URL: " + expectedFileURL);
        }
    }

    public static File initializeTestLogFile(Class<?> governingClass) {
        ClassLoader classLoader = governingClass.getClassLoader();
        try {
            URL logFileURL = classLoader.getResource(LOG_FILE);
            File logFile = new File(logFileURL.toURI());
            if(!logFile.exists()) {
                logFile.createNewFile();
            }
            return logFile;
        } catch(IOException | URISyntaxException e) {
            Assert.fail(e.getMessage());
        }
        return null;
    }

	public static void compareStringToTestFile(Class<?> contextClass, String actualStr, String expectedFilePath) {
		try {
            InputStream expectedInputStream = contextClass.getClassLoader().getResourceAsStream(expectedFilePath);
			String expectedStr = IOUtils.toString(expectedInputStream, StandardCharsets.UTF_8);
			actualStr   = OsUtil.convertToOsLineEndings(actualStr);
			expectedStr = OsUtil.convertToOsLineEndings(expectedStr);
			
			actualStr = actualStr.trim();
			expectedStr = expectedStr.trim();
			
			assertEquals(expectedStr, actualStr);
		} catch(Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());			
		}
	}

    public static List<PlotTracePoint> formatAsPoints(Double[] x, Double[] y) {
        int range = Math.min(x.length, y.length);

        List<PlotTracePoint> points = new ArrayList<>();
        for(int i = 0; i < range; i++) {
            points.add(new PlotTracePoint(x[i], y[i]));
        }
        return points;
    }

    public static List<PlotTracePoint> formatAsPoints(Double[] x, Double[] y, Double[] z) {
        int range = Math.min(x.length, y.length);
        range = Math.min(range, z.length);

        List<PlotTracePoint> points = new ArrayList<>();
        for(int i = 0; i < range; i++) {
            points.add(new PlotTracePoint(x[i], y[i], z[i]));
        }
        return points;
    }
}