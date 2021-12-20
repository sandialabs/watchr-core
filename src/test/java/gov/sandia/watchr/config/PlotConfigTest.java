package gov.sandia.watchr.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.PlotConfig.CanvasLayout;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.config.file.DefaultFileReader;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.graph.chartreuse.model.PlotTracePoint;
import gov.sandia.watchr.log.StringOutputLogger;
import gov.sandia.watchr.util.RGB;

public class PlotConfigTest {

    private StringOutputLogger testLogger;
    private IFileReader fileReader;

    @Before
    public void setup() {
        testLogger = new StringOutputLogger();
        fileReader = new DefaultFileReader(testLogger);
    }    

    @Test
    public void testValidate_HappyPath() {
        FileConfig fileConfig = new FileConfig("", testLogger, fileReader);

        PlotConfig plotConfig = new PlotConfig("", testLogger);
        plotConfig.setName("myPlot");
        plotConfig.getDataLines().add(new DataLine(fileConfig, ""));
        plotConfig.getPlotRules().add(new RuleConfig("", testLogger));

        plotConfig.validate();
        List<String> errors = testLogger.getLog();
        assertEquals(0, errors.size());
    }

    @Test
    public void testCopyAndEquals() {
        PlotConfig plotConfig = new PlotConfig("", testLogger);
        FileConfig fileConfig = new FileConfig("", testLogger, fileReader);
        NameConfig nameConfig = new NameConfig(fileConfig, "");

        plotConfig.setCategory("category");
        plotConfig.setUseLegend(true);
        plotConfig.setNameConfig(nameConfig);
        plotConfig.getDataLines().add(new DataLine(fileConfig, ""));
        plotConfig.getPlotRules().add(new RuleConfig("", testLogger));
        plotConfig.setPointFilterConfig(new FilterConfig("", testLogger));
        
        PlotConfig copy = new PlotConfig(plotConfig);
        assertEquals(plotConfig, copy);
    }

    @Test
    public void testCopyAndNotEquals() {
        PlotConfig plotConfig = new PlotConfig("", testLogger);
        FileConfig fileConfig = new FileConfig("", testLogger, fileReader);
        NameConfig nameConfig = new NameConfig(fileConfig, "");

        plotConfig.setCategory("category");
        plotConfig.setUseLegend(true);
        plotConfig.setNameConfig(nameConfig);
        plotConfig.getDataLines().add(new DataLine(fileConfig, ""));
        plotConfig.getPlotRules().add(new RuleConfig("", testLogger));
        plotConfig.setPointFilterConfig(new FilterConfig("", testLogger));
        
        PlotConfig copy = new PlotConfig(plotConfig);
        copy.setCategory("category2");
        assertNotEquals(plotConfig, copy);
    }    

    @Test
    public void testCopyAndHashCode() {
        PlotConfig plotConfig = new PlotConfig("", testLogger);
        FileConfig fileConfig = new FileConfig("", testLogger, fileReader);
        NameConfig nameConfig = new NameConfig(fileConfig, "");

        plotConfig.setCategory("category");
        plotConfig.setUseLegend(true);
        plotConfig.setNameConfig(nameConfig);
        plotConfig.getDataLines().add(new DataLine(fileConfig, ""));
        plotConfig.getPlotRules().add(new RuleConfig("", testLogger));
        plotConfig.setPointFilterConfig(new FilterConfig("", testLogger));
        
        PlotConfig copy = new PlotConfig(plotConfig);
        assertEquals(plotConfig.hashCode(), copy.hashCode());
    }

    @Test
    public void testDiffs() {
        PlotConfig plotConfig = new PlotConfig("/my/path/prefix", testLogger);
        FileConfig fileConfig = new FileConfig("/my/path/prefix", testLogger, fileReader);
        NameConfig nameConfig = new NameConfig(fileConfig, "/my/path/prefix");
        FilterConfig pointFilterConfig = new FilterConfig("/my/path/prefix", testLogger);
        FileFilterConfig fileFilterConfig = new FileFilterConfig("/my/path/prefix", testLogger);

        plotConfig.setCategory("category");
        plotConfig.setUseLegend(true);
        plotConfig.setNameConfig(nameConfig);
        plotConfig.getDataLines().add(new DataLine(fileConfig, "/my/path/prefix"));
        plotConfig.getPlotRules().add(new RuleConfig("/my/path/prefix", testLogger));
        plotConfig.setPointFilterConfig(pointFilterConfig);
        plotConfig.setFileFilterConfig(fileFilterConfig);
        
        PlotConfig plotConfig2 = new PlotConfig(plotConfig);
        plotConfig2.setCategory("category2");
        plotConfig2.setUseLegend(false);
        plotConfig2.setCanvasLayout(CanvasLayout.INDEPENDENT);
        plotConfig2.setCanvasPerRow(3);
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

        plotConfig2.setType(PlotType.TREE_MAP);
        
        List<WatchrDiff<?>> diffs = plotConfig.diff(plotConfig2);
        assertEquals(12, diffs.size());

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
        assertEquals(DiffCategory.TYPE, diff3.getProperty());
        assertEquals("/my/path/prefix/plotConfig", diff3.getPath());
        assertEquals(PlotType.SCATTER_PLOT, diff3.getBeforeValue());
        assertEquals(PlotType.TREE_MAP, diff3.getNowValue());

        WatchrDiff<?> diff4 = diffs.get(3);
        assertEquals(DiffCategory.TEMPLATE_NAME, diff4.getProperty());
        assertEquals("/my/path/prefix/plotConfig", diff4.getPath());
        assertEquals("", diff4.getBeforeValue());
        assertEquals("templateName", diff4.getNowValue());

        WatchrDiff<?> diff5 = diffs.get(4);
        assertEquals(DiffCategory.INHERIT_TEMPLATE, diff5.getProperty());
        assertEquals("/my/path/prefix/plotConfig", diff5.getPath());
        assertEquals("", diff5.getBeforeValue());
        assertEquals("inheritTemplate", diff5.getNowValue());

        WatchrDiff<?> diff6 = diffs.get(5);
        assertEquals(DiffCategory.NAME_FORMAT_REMOVE_PREFIX, diff6.getProperty());
        assertEquals("/my/path/prefix/nameConfig", diff6.getPath());
        assertEquals("", diff6.getBeforeValue());
        assertEquals("test", diff6.getNowValue());

        WatchrDiff<?> diff7 = diffs.get(6);
        assertEquals(DiffCategory.FILTER_POINTS, diff7.getProperty());
        assertEquals("/my/path/prefix/filterConfig", diff7.getPath());
        assertTrue(((List<?>)diff7.getBeforeValue()).isEmpty());
        assertFalse(((List<?>)diff7.getNowValue()).isEmpty());
        assertEquals(new PlotTracePoint("1.0", "1.0", "1.0"), ((List<?>)diff7.getNowValue()).get(0));

        WatchrDiff<?> diff8 = diffs.get(7);
        assertEquals(DiffCategory.FILENAME_PATTERN, diff8.getProperty());
        assertEquals("/my/path/prefix/fileFilterConfig", diff8.getPath());
        assertEquals("", diff8.getBeforeValue());
        assertEquals("blah", diff8.getNowValue());

        WatchrDiff<?> diff9 = diffs.get(8);
        assertEquals(DiffCategory.LINE_COLOR, diff9.getProperty());
        assertEquals("/my/path/prefix/dataLine", diff9.getPath());
        assertNull(diff9.getBeforeValue());
        assertEquals(new RGB(255, 255, 255), diff9.getNowValue());

        WatchrDiff<?> diff10 = diffs.get(9);
        assertEquals(DiffCategory.CONDITION, diff10.getProperty());
        assertEquals("/my/path/prefix/ruleConfig", diff10.getPath());
        assertEquals("", diff10.getBeforeValue());
        assertEquals("test", diff10.getNowValue());

        WatchrDiff<?> diff11 = diffs.get(10);
        assertEquals(DiffCategory.CANVAS_LAYOUT, diff11.getProperty());
        assertEquals("/my/path/prefix/plotConfig", diff11.getPath());
        assertEquals(CanvasLayout.SHARED, diff11.getBeforeValue());
        assertEquals(CanvasLayout.INDEPENDENT, diff11.getNowValue());

        WatchrDiff<?> diff12 = diffs.get(11);
        assertEquals(DiffCategory.CANVAS_PER_ROW, diff12.getProperty());
        assertEquals("/my/path/prefix/plotConfig", diff12.getPath());
        assertEquals(1, diff12.getBeforeValue());
        assertEquals(3, diff12.getNowValue());
    }

    @Test
    public void testDiffs_Null() {
        PlotConfig plotConfig = new PlotConfig("/my/path/prefix", testLogger);
        FileConfig fileConfig = new FileConfig("/my/path/prefix", testLogger, fileReader);
        NameConfig nameConfig = new NameConfig(fileConfig, "/my/path/prefix");
        FilterConfig pointFilterConfig = new FilterConfig("/my/path/prefix", testLogger);
        FileFilterConfig fileFilterConfig = new FileFilterConfig("/my/path/prefix", testLogger);

        plotConfig.setCategory("category");
        plotConfig.setUseLegend(true);
        plotConfig.setNameConfig(nameConfig);
        plotConfig.getDataLines().add(new DataLine(fileConfig, "/my/path/prefix"));
        plotConfig.getPlotRules().add(new RuleConfig("/my/path/prefix", testLogger));
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
