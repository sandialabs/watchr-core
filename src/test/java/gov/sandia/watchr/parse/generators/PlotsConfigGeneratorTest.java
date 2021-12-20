package gov.sandia.watchr.parse.generators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.FileConfig;
import gov.sandia.watchr.config.FileFilterConfig;
import gov.sandia.watchr.config.PlotConfig;
import gov.sandia.watchr.config.RuleConfig;
import gov.sandia.watchr.config.derivative.DerivativeLineType;
import gov.sandia.watchr.config.file.DefaultFileReader;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.db.impl.FileBasedDatabase;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotModelUtil;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTracePoint;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.log.StringOutputLogger;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.util.RGB;

public class PlotsConfigGeneratorTest {

    private StringOutputLogger testLogger;

    @Before
    public void setup() {
        testLogger = new StringOutputLogger();
    }

    @Test
    public void testDoesFilePassNameFilter() {
        FileConfig fileConfig = new FileConfig("", testLogger, new DefaultFileReader(testLogger));
        PlotsConfigGenerator generator = new PlotsConfigGenerator(null, fileConfig);
        FileFilterConfig fileFilterConfig = new FileFilterConfig("", testLogger);

        fileFilterConfig.setNamePattern("*");
        assertTrue(generator.doesFilePassNameFilter("MyFile.json", fileFilterConfig));

        fileFilterConfig.setNamePattern("*blah*");
        assertFalse(generator.doesFilePassNameFilter("MyFile.json", fileFilterConfig));
        assertTrue(generator.doesFilePassNameFilter("MyFile-blah.json", fileFilterConfig));

        fileFilterConfig.setNamePattern("blah");
        assertFalse(generator.doesFilePassNameFilter("MyFile-blah.json", fileFilterConfig));
        assertTrue(generator.doesFilePassNameFilter("blah", fileFilterConfig));
    }

    /**
     * This plot seeks to test whether plot rules can be recursively applied to child plots.
     */
    @Test
    public void testApplySettingsToNewPlots_ChildPlotRulesWithFailure() {
        try {
            List<PlotTracePoint> mainValues = new ArrayList<>();
            mainValues.add(new PlotTracePoint("1.0", "1.0"));
            mainValues.add(new PlotTracePoint("2.0", "2.0"));
            mainValues.add(new PlotTracePoint("3.0", "3.0"));
            List<PlotTracePoint> avgValues = new ArrayList<>();
            avgValues.add(new PlotTracePoint("1.0", "0.0"));
            avgValues.add(new PlotTracePoint("2.0", "1.0"));
            avgValues.add(new PlotTracePoint("3.0", "4.0"));

            List<RuleConfig> rules = new ArrayList<>();
            RuleConfig rule1 = new RuleConfig("", testLogger);
            rule1.setCondition("average > dataLine");
            rule1.setAction("fail");

            rules.add(rule1);

            PlotTraceModel traceModel = new PlotTraceModel()
                .setPoints(mainValues);
            PlotCanvasModel canvasModel = PlotModelUtil.newPlotCanvas(traceModel, "", "", "");
            PlotWindowModel windowModel = new PlotWindowModel(canvasModel)
                .setName("Parent");
            PlotTraceModel avgTraceModel = new PlotTraceModel()
                .setPoints(avgValues)
                .setDerivativeLineType(DerivativeLineType.AVERAGE);
            canvasModel.addTraceModel(avgTraceModel);

            PlotTraceModel childTraceModel = new PlotTraceModel()
                .setPoints(mainValues);
            PlotCanvasModel childCanvasModel = PlotModelUtil.newPlotCanvas(childTraceModel, "", "", "");
            PlotWindowModel childWindowModel = new PlotWindowModel(childCanvasModel)
                .setName("Child");
            PlotTraceModel childAvgTraceModel = new PlotTraceModel()
                .setPoints(avgValues)
                .setDerivativeLineType(DerivativeLineType.AVERAGE);
            childCanvasModel.addTraceModel(childAvgTraceModel);

            List<PlotWindowModel> children = new ArrayList<>();
            children.add(childWindowModel);

            List<PlotWindowModel> plots = new ArrayList<>();
            plots.add(windowModel);

            File rootDir = Files.createTempDirectory(null).toFile();
            ILogger logger = new StringOutputLogger();
            IFileReader reader = new DefaultFileReader(logger);
            IDatabase db = new FileBasedDatabase(rootDir, logger, reader);
            db.addPlot(childWindowModel);
            db.addPlot(windowModel);
            db.setPlotsAsChildren(windowModel, children);

            PlotConfig plotConfig = new PlotConfig("", logger);
            plotConfig.getPlotRules().addAll(rules);

            PlotsConfigGenerator plotsConfigGenerator = new PlotsConfigGenerator(db, new FileConfig("", logger, reader));
            plotsConfigGenerator.applySettingsToNewPlots(plots, plotConfig);

            assertEquals(new RGB(235, 156, 156), windowModel.getBackgroundColor());
            assertEquals(new RGB(235, 156, 156), childWindowModel.getBackgroundColor());
        } catch(IOException | WatchrParseException e) {
            fail(e.getMessage());
        }
    }       
}
