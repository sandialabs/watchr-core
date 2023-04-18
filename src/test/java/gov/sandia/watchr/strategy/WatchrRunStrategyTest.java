package gov.sandia.watchr.strategy;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;

import gov.sandia.watchr.TestFileUtils;
import gov.sandia.watchr.TestableWatchrCoreApp;
import gov.sandia.watchr.WatchrCoreApp;

public class WatchrRunStrategyTest {
    
    @Test(expected=IllegalStateException.class)
    public void givenNoDataSources_WhenNoneAreProvided_ThenIllegalStateExceptionShouldBeThrown() throws IOException {

        //ARRANGE
        File config = TestFileUtils.loadTestFile("unit_tests/xml/ExportByPlot/config.json");
        File dbDir  = Files.createTempDirectory(
            "givenNoDataSources_WhenNoneAreProvided_ThenIllegalStateExceptionShouldBeThrown").toFile();
        WatchrCoreApp app = TestableWatchrCoreApp.initWatchrAppForTests();
        WatchrRunStrategy run = new WatchrRunStrategy();
        run.setConfigFile(config)
           .setDatabaseDir(dbDir);

        //ACT
        run.run(app);
    }

    @Test
    public void givenTwoDataSources_WhenBothAreProvided_ThenWatchrShouldRememberBoth() throws IOException {

        //ARRANGE
        File config    = TestFileUtils.loadTestFile("unit_tests/xml/ExportByPlot/config.json");
        File dataFile1 = TestFileUtils.loadTestFile("unit_tests/xml/ExportByPlot/performance_day_1.xml");
        File dataFile2 = TestFileUtils.loadTestFile("unit_tests/xml/ExportByPlot/performance_day_2.xml");
        File dbDir     = Files.createTempDirectory(
            "givenTwoDataSources_WhenBothAreProvided_ThenWatchrShouldRememberBoth").toFile();
        WatchrCoreApp app = TestableWatchrCoreApp.initWatchrAppForTests();
        WatchrRunStrategy run = new WatchrRunStrategy();
        run.setConfigFile(config)
           .setDataFile(dataFile1)
           .setDataFile(dataFile2)
           .setDatabaseDir(dbDir);

        //ACT
        run.run(app);

        //ASSERT
        assertEquals(2, app.getDatabaseFilenameCache(dbDir.getName()).size());
    }
}
