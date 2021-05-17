package gov.sandia.watchr.parse.generators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import gov.sandia.watchr.config.RuleConfig;
import gov.sandia.watchr.config.DerivativeLine.DerivativeLineType;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotModelUtil;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTracePoint;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.util.RGB;

public class RuleGeneratorTest {
    
    @Test
    public void testRulesWithSuccess() {
        try {
            List<PlotTracePoint> mainValues = new ArrayList<>();
            mainValues.add(new PlotTracePoint("1.0", "1.0"));
            mainValues.add(new PlotTracePoint("2.0", "2.0"));
            mainValues.add(new PlotTracePoint("3.0", "3.0"));
            List<PlotTracePoint> avgValues = new ArrayList<>();
            avgValues.add(new PlotTracePoint("1.0", "0.0"));
            avgValues.add(new PlotTracePoint("2.0", "1.0"));
            avgValues.add(new PlotTracePoint("3.0", "2.0"));

            List<RuleConfig> rules = new ArrayList<>();
            RuleConfig rule1 = new RuleConfig("");
            rule1.setCondition("average > dataLine");
            rule1.setAction("fail");

            rules.add(rule1);

            PlotTraceModel traceModel = new PlotTraceModel()
                .setPoints(mainValues);
            PlotCanvasModel canvasModel = PlotModelUtil.newPlotCanvas(traceModel, "", "", "");
            PlotWindowModel windowModel = new PlotWindowModel(canvasModel);
            PlotTraceModel avgTraceModel = new PlotTraceModel()
                .setPoints(avgValues);
            avgTraceModel.setDerivativeLineType(DerivativeLineType.AVERAGE);
            canvasModel.addTraceModel(avgTraceModel);

            RuleGenerator ruleGenerator = new RuleGenerator(traceModel);
            List<WatchrDiff<?>> diffs = new ArrayList<>();
            ruleGenerator.generate(rules, diffs);
    
            assertEquals(windowModel.getBackgroundColor(), new RGB(255, 255, 255));
        } catch(WatchrParseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testRulesWithFailure_IfAvgGreaterThan() {
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
            RuleConfig rule1 = new RuleConfig("");
            rule1.setCondition("average > dataLine");
            rule1.setAction("fail");

            rules.add(rule1);

            PlotTraceModel traceModel = new PlotTraceModel()
                .setPoints(mainValues);
            PlotCanvasModel canvasModel = PlotModelUtil.newPlotCanvas(traceModel, "", "", "");
            PlotWindowModel windowModel = new PlotWindowModel(canvasModel);
            PlotTraceModel avgTraceModel = new PlotTraceModel()
                .setPoints(avgValues)
                .setDerivativeLineType(DerivativeLineType.AVERAGE);
            canvasModel.addTraceModel(avgTraceModel);

            RuleGenerator ruleGenerator = new RuleGenerator(traceModel);
            List<WatchrDiff<?>> diffs = new ArrayList<>();
            ruleGenerator.generate(rules, diffs);
            assertEquals(windowModel.getBackgroundColor(), new RGB(235, 156, 156));
        } catch(WatchrParseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testRulesWithFailure_IfStdDevEqualTo() {
        try {
            List<PlotTracePoint> mainValues = new ArrayList<>();
            mainValues.add(new PlotTracePoint("1.0", "1.0"));
            mainValues.add(new PlotTracePoint("2.0", "2.0"));
            mainValues.add(new PlotTracePoint("3.0", "3.0"));
            List<PlotTracePoint> stdDevValues = new ArrayList<>();
            stdDevValues.add(new PlotTracePoint("1.0", "0.0"));
            stdDevValues.add(new PlotTracePoint("2.0", "1.0"));
            stdDevValues.add(new PlotTracePoint("3.0", "3.0"));

            List<RuleConfig> rules = new ArrayList<>();
            RuleConfig rule1 = new RuleConfig("");
            rule1.setCondition("dataLine == standardDeviation");
            rule1.setAction("warn");

            rules.add(rule1);

            PlotTraceModel traceModel = new PlotTraceModel()
                .setPoints(mainValues);
            PlotCanvasModel canvasModel = PlotModelUtil.newPlotCanvas(traceModel, "", "", "");
            PlotWindowModel windowModel = new PlotWindowModel(canvasModel);
            PlotTraceModel stdDevTraceModel = new PlotTraceModel()
                .setPoints(stdDevValues)
                .setDerivativeLineType(DerivativeLineType.STANDARD_DEVIATION);
            canvasModel.addTraceModel(stdDevTraceModel);

            RuleGenerator ruleGenerator = new RuleGenerator(traceModel);
            List<WatchrDiff<?>> diffs = new ArrayList<>();
            ruleGenerator.generate(rules, diffs);
            assertEquals(windowModel.getBackgroundColor(), new RGB(239, 228, 176));
        } catch(WatchrParseException e) {
            fail(e.getMessage());
        }
    } 

    @Test
    public void testRulesWithFailure_IfStdDevOffsetLessThan() {
        try {
            List<PlotTracePoint> mainValues = new ArrayList<>();
            mainValues.add(new PlotTracePoint("1.0", "1.0"));
            mainValues.add(new PlotTracePoint("2.0", "2.0"));
            mainValues.add(new PlotTracePoint("3.0", "3.0"));
            List<PlotTracePoint> stdDevValues = new ArrayList<>();
            stdDevValues.add(new PlotTracePoint("1.0", "0.0"));
            stdDevValues.add(new PlotTracePoint("2.0", "1.0"));
            stdDevValues.add(new PlotTracePoint("3.0", "2.0"));

            List<RuleConfig> rules = new ArrayList<>();
            RuleConfig rule1 = new RuleConfig("");
            rule1.setCondition("standardDeviationOffset < dataLine");
            rule1.setAction("warn");

            rules.add(rule1);

            PlotTraceModel traceModel = new PlotTraceModel()
                .setPoints(mainValues);
            PlotCanvasModel canvasModel = PlotModelUtil.newPlotCanvas(traceModel, "", "", "");
            PlotWindowModel windowModel = new PlotWindowModel(canvasModel);
            PlotTraceModel stdDevTraceModel = new PlotTraceModel()
                .setPoints(stdDevValues)
                .setDerivativeLineType(DerivativeLineType.STANDARD_DEVIATION_OFFSET);
            canvasModel.addTraceModel(stdDevTraceModel);

            RuleGenerator ruleGenerator = new RuleGenerator(traceModel);
            List<WatchrDiff<?>> diffs = new ArrayList<>();
            ruleGenerator.generate(rules, diffs);
            assertEquals(windowModel.getBackgroundColor(), new RGB(239, 228, 176));
        } catch(WatchrParseException e) {
            fail(e.getMessage());
        }
    }
}
