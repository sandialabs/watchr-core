package gov.sandia.watchr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;

public class WatchrCoreAppTest_YamlConfigTests {
    
    @Test
    public void testUnitExample_YamlConfig_HelloWorld() {
        try {            
            File config    = TestFileUtils.loadTestFile("unit_tests/json/HelloWorld/config.yaml");
            File dataFile  = TestFileUtils.loadTestFile("unit_tests/json/HelloWorld/performance_day_1.json");
            File dbDir     = Files.createTempDirectory("testUnitExample_Json_HelloWorld").toFile();
            File exportDir = Files.createTempDirectory("testUnitExample_Json_HelloWorld").toFile();

            WatchrCoreApp.main(new String[] { "start" });
            WatchrCoreApp.main(new String[] { "config", config.getAbsolutePath() });
            WatchrCoreApp.main(new String[] { "put", "db", dbDir.getAbsolutePath() });
            WatchrCoreApp.main(new String[] { "put", "plots", exportDir.getAbsolutePath() });
            WatchrCoreApp.main(new String[] { "add", dataFile.getAbsolutePath() });
            WatchrCoreApp.main(new String[] { "run" });

            assertEquals(1, exportDir.listFiles().length);
            String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_X, "x: ['2021-04-05T22:21:21'],");
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [1.0],");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }         
    }

    @After
    public void teardown() {
        try {
            File watchrRunDirectory = new File(System.getProperty("user.dir") + File.separator + "watchrRun");
            File graphDirectory = new File(System.getProperty("user.dir") + File.separator + "graph");

            FileUtils.deleteDirectory(watchrRunDirectory);
            FileUtils.deleteDirectory(graphDirectory);
        } catch(IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
