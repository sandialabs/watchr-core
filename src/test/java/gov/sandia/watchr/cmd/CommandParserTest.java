package gov.sandia.watchr.cmd;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.util.OsUtil;

public class CommandParserTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    
    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }
    
    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }    
    
    @Test
    public void testPrintHelp() throws Exception {
        WatchrCoreApp.main(new String[] { "help" });

        final String LINE_1  = "Watchr v" + WatchrCoreApp.getVersion() + "";
        final String LINE_2  = "=============";
        final String LINE_3  = "";
        final String LINE_4  = "Commands:";
        final String LINE_5  = "  help    - Print Watchr's command-line commands.";
        final String LINE_6  = "  start   - Start a new Watchr session.";
        final String LINE_7  = "  restart - Clear previous Watchr session information.";
        final String LINE_8  = "";
        final String LINE_9  = "  config <file>         - Provide the configuration file.";
        final String LINE_10 = "  add <file>            - Add a data file that Watchr should read.";
        final String LINE_11 = "  add <directory>       - Add a directory containing data files that Watchr should read.";
        final String LINE_12 = "  put db <directory>    - Provide the path to the directory where Watchr should put its database.";
        final String LINE_13 = "  put plots <directory> - Provide the path to the directory where Watchr should export graphs.";
        final String LINE_14 = "";
        final String LINE_15 = "  run - Run Watchr according to configuration set up by previous commands.";

        String outResult = outContent.toString();
        String[] outTokens = outResult.split(OsUtil.getOSLineBreak());
        assertEquals(LINE_1, outTokens[0]);
        assertEquals(LINE_2, outTokens[1]);
        assertEquals(LINE_3, outTokens[2]);
        assertEquals(LINE_4, outTokens[3]);
        assertEquals(LINE_5, outTokens[4]);
        assertEquals(LINE_6, outTokens[5]);
        assertEquals(LINE_7, outTokens[6]);
        assertEquals(LINE_8, outTokens[7]);
        assertEquals(LINE_9, outTokens[8]);
        assertEquals(LINE_10, outTokens[9]);
        assertEquals(LINE_11, outTokens[10]);
        assertEquals(LINE_12, outTokens[11]);
        assertEquals(LINE_13, outTokens[12]);
        assertEquals(LINE_14, outTokens[13]);
        assertEquals(LINE_15, outTokens[14]);
    }
}
