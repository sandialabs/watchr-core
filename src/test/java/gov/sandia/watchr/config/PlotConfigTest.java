package gov.sandia.watchr.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.TestLogger;
import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.graph.chartreuse.model.PlotTracePoint;
import gov.sandia.watchr.util.RGB;

public class PlotConfigTest {

    private TestLogger testLogger;

    @Before
    public void setup() {
        testLogger = new TestLogger();
        WatchrCoreApp.getInstance().setLogger(testLogger);
    }    

    @Test
    public void testValidate_HappyPath() {
        FileConfig fileConfig = new FileConfig("");

        PlotConfig plotConfig = new PlotConfig("");
        plotConfig.setName("myPlot");
        plotConfig.getDataLines().add(new DataLine(fileConfig, ""));
        plotConfig.getPlotRules().add(new RuleConfig(""));

        plotConfig.validate();
        List<WatchrConfigError> errors = testLogger.getErrors();
        assertEquals(0, errors.size());
    }

    @Test
    public void testCopyAndEquals() {
        PlotConfig plotConfig = new PlotConfig("");
        FileConfig fileConfig = new FileConfig("");
        NameConfig nameConfig = new NameConfig("");

        plotConfig.setCategory("category");
        plotConfig.setUseLegend(true);
        plotConfig.setNameConfig(nameConfig);
        plotConfig.getDataLines().add(new DataLine(fileConfig, ""));
        plotConfig.getPlotRules().add(new RuleConfig(""));
        plotConfig.setPointFilterConfig(new FilterConfig(""));
        
        PlotConfig copy = new PlotConfig(plotConfig);
        assertEquals(plotConfig, copy);
    }

    @Test
    public void testCopyAndNotEquals() {
        PlotConfig plotConfig = new PlotConfig("");
        FileConfig fileConfig = new FileConfig("");
        NameConfig nameConfig = new NameConfig("");

        plotConfig.setCategory("category");
        plotConfig.setUseLegend(true);
        plotConfig.setNameConfig(nameConfig);
        plotConfig.getDataLines().add(new DataLine(fileConfig, ""));
        plotConfig.getPlotRules().add(new RuleConfig(""));
        plotConfig.setPointFilterConfig(new FilterConfig(""));
        
        PlotConfig copy = new PlotConfig(plotConfig);
        copy.setCategory("category2");
        assertNotEquals(plotConfig, copy);
    }    

    @Test
    public void testCopyAndHashCode() {
        PlotConfig plotConfig = new PlotConfig("");
        FileConfig fileConfig = new FileConfig("");
        NameConfig nameConfig = new NameConfig("");

        plotConfig.setCategory("category");
        plotConfig.setUseLegend(true);
        plotConfig.setNameConfig(nameConfig);
        plotConfig.getDataLines().add(new DataLine(fileConfig, ""));
        plotConfig.getPlotRules().add(new RuleConfig(""));
        plotConfig.setPointFilterConfig(new FilterConfig(""));
        
        PlotConfig copy = new PlotConfig(plotConfig);
        assertEquals(plotConfig.hashCode(), copy.hashCode());
    }

    @Test
    public void testDiffs() {
        PlotConfig plotConfig = new PlotConfig("/my/path/prefix");
        FileConfig fileConfig = new FileConfig("/my/path/prefix");
        NameConfig nameConfig = new NameConfig("/my/path/prefix");
        FilterConfig pointFilterConfig = new FilterConfig("/my/path/prefix");
        FileFilterConfig fileFilterConfig = new FileFilterConfig("/my/path/prefix");

        plotConfig.setCategory("category");
        plotConfig.setUseLegend(true);
        plotConfig.setNameConfig(nameConfig);
        plotConfig.getDataLines().add(new DataLine(fileConfig, "/my/path/prefix"));
        plotConfig.getPlotRules().add(new RuleConfig("/my/path/prefix"));
        plotConfig.setPointFilterConfig(pointFilterConfig);
        plotConfig.setFileFilterConfig(fileFilterConfig);
        
        PlotConfig plotConfig2 = new PlotConfig(plotConfig);
        plotConfig2.setCategory("category2");
        plotConfig2.setUseLegend(false);
        NameConfig nameConfig2 = plotConfig2.getNameConfig();
        nameConfig2.setNameFormatRemovePrefix("test");
        FilterConfig filterConfig2 = plotConfig2.getPointFilterConfig();
        filterConfig2.getFilterPoints().add(new PlotTracePoint(1,1,1));
        DataLine dataLine2 = plotConfig2.getDataLines().get(0);
        dataLine2.setColor(new RGB(255,255,255));
        RuleConfig rule2 = plotConfig2.getPlotRules().get(0);
        rule2.setCondition("test");
        FileFilterConfig fileFilterConfig2 = plotConfig2.getFileFilterConfig();
        fileFilterConfig2.setNamePattern("blah");

        plotConfig2.setInheritTemplate("inheritTemplate");
        plotConfig2.setTemplateName("templateName");
        
        List<WatchrDiff<?>> diffs = plotConfig.diff(plotConfig2);
        assertEquals(9, diffs.size());

        WatchrDiff<?> diff1 = diffs.get(0);
        assertEquals(DiffCategory.CATEGORY, diff1.getProperty());
        assertEquals("/my/path/prefix/plotConfig", diff1.getPath());
        assertEquals("category", diff1.getBeforeValue());
        assertEquals("category2", diff1.getNowValue());

        WatchrDiff<?> diff2 = diffs.get(1);
        assertEquals(DiffCategory.USE_LEGEND, diff2.getProperty());
        assertEquals("/my/path/prefix/plotConfig", diff2.getPath());
        assertTrue((Boolean) diff2.getBeforeValue());
        assertFalse((Boolean) diff2.getNowValue());

        WatchrDiff<?> diff3 = diffs.get(2);
        assertEquals(DiffCategory.TEMPLATE_NAME, diff3.getProperty());
        assertEquals("/my/path/prefix/plotConfig", diff3.getPath());
        assertEquals("", diff3.getBeforeValue());
        assertEquals("templateName", diff3.getNowValue());

        WatchrDiff<?> diff4 = diffs.get(3);
        assertEquals(DiffCategory.INHERIT_TEMPLATE, diff4.getProperty());
        assertEquals("/my/path/prefix/plotConfig", diff4.getPath());
        assertEquals("", diff4.getBeforeValue());
        assertEquals("inheritTemplate", diff4.getNowValue());

        WatchrDiff<?> diff5 = diffs.get(4);
        assertEquals(DiffCategory.NAME_FORMAT_REMOVE_PREFIX, diff5.getProperty());
        assertEquals("/my/path/prefix/nameConfig", diff5.getPath());
        assertEquals("", diff5.getBeforeValue());
        assertEquals("test", diff5.getNowValue());

        WatchrDiff<?> diff6 = diffs.get(5);
        assertEquals(DiffCategory.FILTER_POINTS, diff6.getProperty());
        assertEquals("/my/path/prefix/filterConfig", diff6.getPath());
        assertTrue(((List<?>)diff6.getBeforeValue()).isEmpty());
        assertFalse(((List<?>)diff6.getNowValue()).isEmpty());
        assertEquals(new PlotTracePoint("1.0", "1.0", "1.0"), ((List<?>)diff6.getNowValue()).get(0));

        WatchrDiff<?> diff7 = diffs.get(6);
        assertEquals(DiffCategory.FILENAME_PATTERN, diff7.getProperty());
        assertEquals("/my/path/prefix/fileFilterConfig", diff7.getPath());
        assertEquals("", diff7.getBeforeValue());
        assertEquals("blah", diff7.getNowValue());

        WatchrDiff<?> diff8 = diffs.get(7);
        assertEquals(DiffCategory.LINE_COLOR, diff8.getProperty());
        assertEquals("/my/path/prefix/dataLine", diff8.getPath());
        assertNull(diff8.getBeforeValue());
        assertEquals(new RGB(255, 255, 255), diff8.getNowValue());

        WatchrDiff<?> diff9 = diffs.get(8);
        assertEquals(DiffCategory.CONDITION, diff9.getProperty());
        assertEquals("/my/path/prefix/ruleConfig", diff9.getPath());
        assertEquals("", diff9.getBeforeValue());
        assertEquals("test", diff9.getNowValue());
    }

    @Test
    public void testDiffs_Null() {
        PlotConfig plotConfig = new PlotConfig("/my/path/prefix");
        FileConfig fileConfig = new FileConfig("/my/path/prefix");
        NameConfig nameConfig = new NameConfig("/my/path/prefix");
        FilterConfig pointFilterConfig = new FilterConfig("/my/path/prefix");
        FileFilterConfig fileFilterConfig = new FileFilterConfig("/my/path/prefix");

        plotConfig.setCategory("category");
        plotConfig.setUseLegend(true);
        plotConfig.setNameConfig(nameConfig);
        plotConfig.getDataLines().add(new DataLine(fileConfig, "/my/path/prefix"));
        plotConfig.getPlotRules().add(new RuleConfig("/my/path/prefix"));
        plotConfig.setPointFilterConfig(pointFilterConfig);
        plotConfig.setFileFilterConfig(fileFilterConfig);
        
        PlotConfig plotConfig2 = new PlotConfig(plotConfig);
        plotConfig2.setCategory(null);
        plotConfig2.setNameConfig(null);
        plotConfig2.setPointFilterConfig(null);
        plotConfig2.setFileFilterConfig(null);
        plotConfig2.setInheritTemplate(null);
        plotConfig2.setTemplateName(null);
        
        List<WatchrDiff<?>> diffs = plotConfig.diff(plotConfig2);
        assertEquals(6, diffs.size());
        
        WatchrDiff<?> diff1 = diffs.get(0);
        assertEquals(DiffCategory.CATEGORY, diff1.getProperty());
        assertEquals("/my/path/prefix/plotConfig", diff1.getPath());
        assertEquals("category", diff1.getBeforeValue());
        assertNull(diff1.getNowValue());

        WatchrDiff<?> diff2 = diffs.get(1);
        assertEquals(DiffCategory.TEMPLATE_NAME, diff2.getProperty());
        assertEquals("/my/path/prefix/plotConfig", diff2.getPath());
        assertEquals("", diff2.getBeforeValue());
        assertNull(diff2.getNowValue());

        WatchrDiff<?> diff3 = diffs.get(2);
        assertEquals(DiffCategory.INHERIT_TEMPLATE, diff3.getProperty());
        assertEquals("/my/path/prefix/plotConfig", diff3.getPath());
        assertEquals("", diff3.getBeforeValue());
        assertNull(diff3.getNowValue());

        WatchrDiff<?> diff4 = diffs.get(3);
        assertEquals(DiffCategory.NAME_CONFIG, diff4.getProperty());
        assertEquals("/my/path/prefix/plotConfig", diff4.getPath());
        assertEquals(nameConfig, diff4.getBeforeValue());
        assertNull(diff4.getNowValue());

        WatchrDiff<?> diff5 = diffs.get(4);
        assertEquals(DiffCategory.POINT_FILTER_CONFIG, diff5.getProperty());
        assertEquals("/my/path/prefix/plotConfig", diff5.getPath());
        assertEquals(pointFilterConfig, diff5.getBeforeValue());
        assertNull(diff5.getNowValue());

        WatchrDiff<?> diff6 = diffs.get(5);
        assertEquals(DiffCategory.FILE_FILTER_CONFIG, diff6.getProperty());
        assertEquals("/my/path/prefix/plotConfig", diff6.getPath());
        assertEquals(fileFilterConfig, diff6.getBeforeValue());
        assertNull(diff6.getNowValue());
    }    
}
