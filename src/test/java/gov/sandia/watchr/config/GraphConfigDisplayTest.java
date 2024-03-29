package gov.sandia.watchr.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.GraphDisplayConfig.GraphDisplaySort;
import gov.sandia.watchr.config.GraphDisplayConfig.LeafNodeStrategy;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.log.StringOutputLogger;
import gov.sandia.watchr.util.OsUtil;

public class GraphConfigDisplayTest {

    private StringOutputLogger testLogger;

    @Before
    public void setup() {
        testLogger = new StringOutputLogger();
    }    
    
    @Test
    public void testToString() {
        GraphDisplayConfig displayConfig = new GraphDisplayConfig("", testLogger);
        displayConfig.setDisplayCategory("Category");
        displayConfig.setDisplayRange(100);
        displayConfig.setDisplayedDecimalPlaces(3);
        displayConfig.setGraphHeight(400);
        displayConfig.setGraphWidth(400);
        displayConfig.setGraphsPerPage(10);
        displayConfig.setGraphsPerRow(10);
        displayConfig.setLastPlotDbLocation("lastPlotDbLocation");
        displayConfig.setNextPlotDbLocation("nextPlotDbLocation");
        displayConfig.setLeafNodeStrategy(LeafNodeStrategy.TRAVEL_UP_TO_PARENT);
        displayConfig.setPage(10);

        String expectedStr = "nextPlotDbLocation: nextPlotDbLocation" + OsUtil.getOSLineBreak() +
                             "lastPlotDbLocation: lastPlotDbLocation" + OsUtil.getOSLineBreak() +
                             "page: 10" + OsUtil.getOSLineBreak() +
                             "searchQuery: /" + OsUtil.getOSLineBreak() +
                             "displayCategory: Category" + OsUtil.getOSLineBreak() +
                             "displayRange: 100" + OsUtil.getOSLineBreak() +
                             "graphWidth: 400" + OsUtil.getOSLineBreak() +
                             "graphHeight: 400" + OsUtil.getOSLineBreak() +
                             "graphsPerRow: 10" + OsUtil.getOSLineBreak() +
                             "graphsPerPage: 10" + OsUtil.getOSLineBreak() +
                             "displayedDecimalPlaces: 3" + OsUtil.getOSLineBreak() +
                             "leafNodeStrategy: TRAVEL_UP_TO_PARENT" + OsUtil.getOSLineBreak();
        String actualStr = displayConfig.toString();
        assertEquals(expectedStr, actualStr);
    }

    @Test
    public void testValidate_GoodBoi() {
        GraphDisplayConfig displayConfig = new GraphDisplayConfig("", testLogger);
        displayConfig.setDisplayCategory("Category");
        displayConfig.setDisplayRange(100);
        displayConfig.setDisplayedDecimalPlaces(3);
        displayConfig.setGraphHeight(400);
        displayConfig.setGraphWidth(400);
        displayConfig.setGraphsPerPage(10);
        displayConfig.setGraphsPerRow(10);
        displayConfig.setLastPlotDbLocation("lastPlotDbLocation");
        displayConfig.setNextPlotDbLocation("nextPlotDbLocation");
        displayConfig.setPage(10);
        
        displayConfig.validate();
        List<String> errors = testLogger.getLog();
        assertEquals(0, errors.size());
    }

    @Test
    public void testValidate_BadPage() {
        GraphDisplayConfig displayConfig = new GraphDisplayConfig("", testLogger);
        displayConfig.setDisplayCategory("Category");
        displayConfig.setDisplayRange(100);
        displayConfig.setDisplayedDecimalPlaces(3);
        displayConfig.setGraphHeight(400);
        displayConfig.setGraphWidth(400);
        displayConfig.setGraphsPerPage(10);
        displayConfig.setGraphsPerRow(10);
        displayConfig.setLastPlotDbLocation("lastPlotDbLocation");
        displayConfig.setNextPlotDbLocation("nextPlotDbLocation");

        displayConfig.setPage(0);
        
        displayConfig.validate();
        List<String> errors = testLogger.getLog();
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("ERROR"));
        assertTrue(errors.get(0).contains("Page must start at 1."));
    }

    @Test
    public void testValidate_BadDisplayRange() {
        GraphDisplayConfig displayConfig = new GraphDisplayConfig("", testLogger);
        displayConfig.setDisplayCategory("Category");
        displayConfig.setDisplayedDecimalPlaces(3);
        displayConfig.setGraphHeight(400);
        displayConfig.setGraphWidth(400);
        displayConfig.setGraphsPerPage(10);
        displayConfig.setGraphsPerRow(10);
        displayConfig.setLastPlotDbLocation("lastPlotDbLocation");
        displayConfig.setNextPlotDbLocation("nextPlotDbLocation");
        displayConfig.setPage(1);

        displayConfig.setDisplayRange(0);
        
        displayConfig.validate();
        List<String> errors = testLogger.getLog();
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("ERROR"));
        assertTrue(errors.get(0).contains("Display range must be at least 1."));
    }     

    @Test
    public void testValidate_BadGraphWidth() {
        GraphDisplayConfig displayConfig = new GraphDisplayConfig("", testLogger);
        displayConfig.setDisplayCategory("Category");
        displayConfig.setDisplayedDecimalPlaces(3);
        displayConfig.setGraphHeight(400);
        displayConfig.setGraphsPerPage(10);
        displayConfig.setGraphsPerRow(10);
        displayConfig.setLastPlotDbLocation("lastPlotDbLocation");
        displayConfig.setNextPlotDbLocation("nextPlotDbLocation");
        displayConfig.setPage(1);
        displayConfig.setDisplayRange(1);

        displayConfig.setGraphWidth(0);
        
        displayConfig.validate();
        List<String> errors = testLogger.getLog();
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("ERROR"));
        assertTrue(errors.get(0).contains("Graph width must be at least 1 pixel."));
    }

    @Test
    public void testValidate_BadGraphHeight() {
        GraphDisplayConfig displayConfig = new GraphDisplayConfig("", testLogger);
        displayConfig.setDisplayCategory("Category");
        displayConfig.setDisplayedDecimalPlaces(3);
        displayConfig.setGraphsPerPage(10);
        displayConfig.setGraphsPerRow(10);
        displayConfig.setLastPlotDbLocation("lastPlotDbLocation");
        displayConfig.setNextPlotDbLocation("nextPlotDbLocation");
        displayConfig.setPage(1);
        displayConfig.setDisplayRange(1);
        displayConfig.setGraphWidth(1);

        displayConfig.setGraphHeight(0);
        
        displayConfig.validate();
        List<String> errors = testLogger.getLog();
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("ERROR"));
        assertTrue(errors.get(0).contains("Graph height must be at least 1 pixel."));
    }

    @Test
    public void testValidate_BadGraphsPerRow() {
        GraphDisplayConfig displayConfig = new GraphDisplayConfig("", testLogger);
        displayConfig.setDisplayCategory("Category");
        displayConfig.setDisplayedDecimalPlaces(3);
        displayConfig.setGraphsPerPage(10);
        displayConfig.setLastPlotDbLocation("lastPlotDbLocation");
        displayConfig.setNextPlotDbLocation("nextPlotDbLocation");
        displayConfig.setPage(1);
        displayConfig.setDisplayRange(1);
        displayConfig.setGraphWidth(1);
        displayConfig.setGraphHeight(1);

        displayConfig.setGraphsPerRow(0);
        
        displayConfig.validate();
        List<String> errors = testLogger.getLog();
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("ERROR"));
        assertTrue(errors.get(0).contains("Must have at least 1 graph per row."));
    }

    @Test
    public void testValidate_BadGraphsPerPage() {
        GraphDisplayConfig displayConfig = new GraphDisplayConfig("", testLogger);
        displayConfig.setDisplayCategory("Category");
        displayConfig.setDisplayedDecimalPlaces(3);
        displayConfig.setLastPlotDbLocation("lastPlotDbLocation");
        displayConfig.setNextPlotDbLocation("nextPlotDbLocation");
        displayConfig.setDisplayRange(1);
        displayConfig.setPage(1);
        displayConfig.setGraphWidth(1);
        displayConfig.setGraphHeight(1);
        displayConfig.setGraphsPerRow(1);

        displayConfig.setGraphsPerPage(0);
        
        displayConfig.validate();
        List<String> errors = testLogger.getLog();
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("ERROR"));
        assertTrue(errors.get(0).contains("Must have at least 1 graph per page."));
    }
    
    @Test
    public void testValidate_BadDecimalPlaces() {
        GraphDisplayConfig displayConfig = new GraphDisplayConfig("", testLogger);
        displayConfig.setDisplayCategory("Category");
        displayConfig.setLastPlotDbLocation("lastPlotDbLocation");
        displayConfig.setNextPlotDbLocation("nextPlotDbLocation");
        displayConfig.setDisplayRange(1);
        displayConfig.setPage(1);
        displayConfig.setGraphWidth(1);
        displayConfig.setGraphHeight(1);
        displayConfig.setGraphsPerRow(1);
        displayConfig.setGraphsPerPage(1);
        
        displayConfig.setDisplayedDecimalPlaces(-1);
        
        displayConfig.validate();
        List<String> errors = testLogger.getLog();
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("ERROR"));
        assertTrue(errors.get(0).contains("Number of displayed decimal places cannot be a negative number."));
    }    

    @Test
    public void testCopyAndEquals() {
        GraphDisplayConfig graphConfig = new GraphDisplayConfig("", testLogger);
        graphConfig.setNextPlotDbLocation("nextPlotDbLocation");
        graphConfig.setLastPlotDbLocation("lastPlotDbLocation");
        graphConfig.setPage(2);
        graphConfig.setDisplayCategory("displayCategory");
        graphConfig.setDisplayRange(150);
        graphConfig.setGraphWidth(1000);
        graphConfig.setGraphHeight(750);
        graphConfig.setGraphsPerRow(4);
        graphConfig.setGraphsPerPage(20);
        graphConfig.setDisplayedDecimalPlaces(10);
        
        GraphDisplayConfig copy = new GraphDisplayConfig(graphConfig);
        assertEquals(graphConfig, copy);
    }

    @Test
    public void testCopyAndNotEquals() {
        GraphDisplayConfig graphConfig = new GraphDisplayConfig("", testLogger);
        graphConfig.setNextPlotDbLocation("nextPlotDbLocation");
        graphConfig.setLastPlotDbLocation("lastPlotDbLocation");
        graphConfig.setPage(2);
        graphConfig.setDisplayCategory("displayCategory");
        graphConfig.setDisplayRange(150);
        graphConfig.setGraphWidth(1000);
        graphConfig.setGraphHeight(750);
        graphConfig.setGraphsPerRow(4);
        graphConfig.setGraphsPerPage(20);
        graphConfig.setDisplayedDecimalPlaces(10);
        
        GraphDisplayConfig copy = new GraphDisplayConfig(graphConfig);
        copy.setNextPlotDbLocation("differentPlotDbLocation");
        assertNotEquals(graphConfig, copy);
    }    

    @Test
    public void testCopyAndHashCode() {
        GraphDisplayConfig graphConfig = new GraphDisplayConfig("", testLogger);
        graphConfig.setNextPlotDbLocation("nextPlotDbLocation");
        graphConfig.setLastPlotDbLocation("lastPlotDbLocation");
        graphConfig.setPage(2);
        graphConfig.setDisplayCategory("displayCategory");
        graphConfig.setDisplayRange(150);
        graphConfig.setGraphWidth(1000);
        graphConfig.setGraphHeight(750);
        graphConfig.setGraphsPerRow(4);
        graphConfig.setGraphsPerPage(20);
        graphConfig.setDisplayedDecimalPlaces(10);
        
        GraphDisplayConfig copy = new GraphDisplayConfig(graphConfig);
        assertEquals(graphConfig.hashCode(), copy.hashCode());
    }

    @Test
    public void testDiffs() {
        GraphDisplayConfig graphConfig = new GraphDisplayConfig("/my/path/prefix", testLogger);
        graphConfig.setNextPlotDbLocation("nextPlotDbLocation");
        graphConfig.setLastPlotDbLocation("lastPlotDbLocation");
        graphConfig.setPage(2);
        graphConfig.setDisplayCategory("displayCategory");
        graphConfig.setDisplayRange(150);
        graphConfig.setGraphWidth(1000);
        graphConfig.setGraphHeight(750);
        graphConfig.setGraphsPerRow(4);
        graphConfig.setGraphsPerPage(20);
        graphConfig.setDisplayedDecimalPlaces(10);

        GraphDisplayConfig graphConfig2 = new GraphDisplayConfig("/my/path/prefix", testLogger);
        graphConfig2.setDisplayCategory("displayCategory2");
        graphConfig2.setDisplayRange(151);
        graphConfig2.setGraphWidth(1001);
        graphConfig2.setGraphHeight(751);
        graphConfig2.setGraphsPerRow(5);
        graphConfig2.setGraphsPerPage(21);
        graphConfig2.setDisplayedDecimalPlaces(11);
        graphConfig2.setSort(GraphDisplaySort.DESCENDING);
        
        List<WatchrDiff<?>> diffs = graphConfig.diff(graphConfig2);
        assertEquals(8, diffs.size());

        WatchrDiff<?> diff1 = diffs.get(0);
        assertEquals(DiffCategory.DISPLAY_CATEGORY, diff1.getProperty());
        assertEquals("/my/path/prefix/graphDisplayConfig", diff1.getPath());
        assertEquals("displayCategory", diff1.getBeforeValue());
        assertEquals("displayCategory2", diff1.getNowValue());

        WatchrDiff<?> diff2 = diffs.get(1);
        assertEquals(DiffCategory.DISPLAY_RANGE, diff2.getProperty());
        assertEquals("/my/path/prefix/graphDisplayConfig", diff2.getPath());
        assertEquals(150, diff2.getBeforeValue());
        assertEquals(151, diff2.getNowValue());

        WatchrDiff<?> diff3 = diffs.get(2);
        assertEquals(DiffCategory.GRAPH_WIDTH, diff3.getProperty());
        assertEquals("/my/path/prefix/graphDisplayConfig", diff3.getPath());
        assertEquals(1000, diff3.getBeforeValue());
        assertEquals(1001, diff3.getNowValue());
        
        WatchrDiff<?> diff4 = diffs.get(3);
        assertEquals(DiffCategory.GRAPH_HEIGHT, diff4.getProperty());
        assertEquals("/my/path/prefix/graphDisplayConfig", diff4.getPath());
        assertEquals(750, diff4.getBeforeValue());
        assertEquals(751, diff4.getNowValue());
        
        WatchrDiff<?> diff5 = diffs.get(4);
        assertEquals(DiffCategory.GRAPHS_PER_ROW, diff5.getProperty());
        assertEquals("/my/path/prefix/graphDisplayConfig", diff5.getPath());
        assertEquals(4, diff5.getBeforeValue());
        assertEquals(5, diff5.getNowValue());

        WatchrDiff<?> diff6 = diffs.get(5);
        assertEquals(DiffCategory.GRAPHS_PER_PAGE, diff6.getProperty());
        assertEquals("/my/path/prefix/graphDisplayConfig", diff6.getPath());
        assertEquals(20, diff6.getBeforeValue());
        assertEquals(21, diff6.getNowValue());

        WatchrDiff<?> diff7 = diffs.get(6);
        assertEquals(DiffCategory.DISPLAYED_DECIMAL_PLACES, diff7.getProperty());
        assertEquals("/my/path/prefix/graphDisplayConfig", diff7.getPath());
        assertEquals(10, diff7.getBeforeValue());
        assertEquals(11, diff7.getNowValue()); 

        WatchrDiff<?> diff8 = diffs.get(7);
        assertEquals(DiffCategory.SORT, diff8.getProperty());
        assertEquals("/my/path/prefix/graphDisplayConfig", diff8.getPath());
        assertEquals(GraphDisplaySort.ASCENDING, diff8.getBeforeValue());
        assertEquals(GraphDisplaySort.DESCENDING, diff8.getNowValue()); 
    }     
}