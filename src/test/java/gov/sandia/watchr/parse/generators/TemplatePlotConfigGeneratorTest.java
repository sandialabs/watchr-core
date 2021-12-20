package gov.sandia.watchr.parse.generators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.DataLine;
import gov.sandia.watchr.config.FileConfig;
import gov.sandia.watchr.config.HierarchicalExtractor;
import gov.sandia.watchr.config.PlotConfig;
import gov.sandia.watchr.config.RuleConfig;
import gov.sandia.watchr.config.file.DefaultFileReader;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.log.StringOutputLogger;

public class TemplatePlotConfigGeneratorTest {
    
    private FileConfig fileConfig;
    private List<PlotConfig> allPlotConfigs;
    private PlotConfig templatePlotConfig;
    private PlotConfig inheritPlotConfig;

    private TemplatePlotConfigGenerator templateGenerator;

    private StringOutputLogger testLogger;
    private IFileReader fileReader;

    @Before
    public void setup() {
        testLogger = new StringOutputLogger();
        fileReader = new DefaultFileReader(testLogger);
        fileConfig = new FileConfig("", testLogger, fileReader);

        this.allPlotConfigs = new ArrayList<>();

        templatePlotConfig = new PlotConfig("", testLogger);
        templatePlotConfig.setTemplateName("template");
        templatePlotConfig.setCategory("category");

        inheritPlotConfig = new PlotConfig("", testLogger);
        inheritPlotConfig.setInheritTemplate("template");
        inheritPlotConfig.setCategory("category");

        allPlotConfigs.add(templatePlotConfig);
        allPlotConfigs.add(inheritPlotConfig);

        templateGenerator = new TemplatePlotConfigGenerator(allPlotConfigs, testLogger);
    }

    @Test
    public void testApplyChildExtractor() {
        HierarchicalExtractor xExtractor = new HierarchicalExtractor(fileConfig, "");
        HierarchicalExtractor yExtractor = new HierarchicalExtractor(fileConfig, "");
        xExtractor.setProperty("getPath", "{0}");
        xExtractor.setProperty("getKey", "time");
        yExtractor.setProperty("getPath", "{1}/{2}");
        yExtractor.setProperty("getKey", "key");

        DataLine dataLine = new DataLine(fileConfig, "");
        dataLine.setXExtractor(xExtractor);
        dataLine.setYExtractor(yExtractor);
        templatePlotConfig.getDataLines().add(dataLine);

        HierarchicalExtractor childYExtractor = new HierarchicalExtractor(fileConfig, "");
        childYExtractor.setProperty("getKey", "otherKey");
        DataLine dataLine2 = new DataLine(fileConfig, "");
        dataLine2.setYExtractor(childYExtractor);
        inheritPlotConfig.getDataLines().add(dataLine2);

        PlotConfig resultPlotConfig = templateGenerator.handlePlotGenerationForTemplate(inheritPlotConfig);
        assertEquals(2, resultPlotConfig.getDataLines().size());
        DataLine dataLine1 = resultPlotConfig.getDataLines().get(0);
        assertEquals("{0}", dataLine1.getXExtractor().getProperty("getPath"));
        assertEquals("time", dataLine1.getXExtractor().getProperty("getKey"));
        assertEquals("{1}/{2}", dataLine1.getYExtractor().getProperty("getPath"));
        assertEquals("key", dataLine1.getYExtractor().getProperty("getKey"));

        // Note that dataLine2 inherits none of the properties of dataLine1, but
        // is an entirely distinct data line.  This is because dataLine1 is not
        // a template, and dataLine2 does not explicitly inherit from it.
        DataLine retrievedDataLine2 = resultPlotConfig.getDataLines().get(1);
        assertNull(retrievedDataLine2.getXExtractor().getProperty("getPath"));
        assertNull(retrievedDataLine2.getXExtractor().getProperty("getKey"));
        assertEquals("otherKey", retrievedDataLine2.getYExtractor().getProperty("getKey"));
    }

    @Test
    public void testApplyChildRules() {
        RuleConfig rule1 = new RuleConfig("", testLogger);
        rule1.setCondition("dataLine < average");
        rule1.setAction("fail");
        RuleConfig rule2 = new RuleConfig("", testLogger);
        rule2.setCondition("dataLine > standardDeviationOffset");
        rule2.setAction("warn");
        templatePlotConfig.getPlotRules().add(rule1);
        templatePlotConfig.getPlotRules().add(rule2);

        RuleConfig rule3 = new RuleConfig("", testLogger);
        rule3.setCondition("dataLine < average");
        rule3.setAction("warn");
        inheritPlotConfig.getPlotRules().add(rule3);

        PlotConfig resultPlotConfig = templateGenerator.handlePlotGenerationForTemplate(inheritPlotConfig);
        assertEquals(2, resultPlotConfig.getPlotRules().size());
        RuleConfig resultRule1 = resultPlotConfig.getPlotRules().get(0);
        assertEquals("dataLine < average", resultRule1.getCondition());
        assertEquals("warn", resultRule1.getAction());
        RuleConfig resultRule2 = resultPlotConfig.getPlotRules().get(1);
        assertEquals("dataLine > standardDeviationOffset", resultRule2.getCondition());
        assertEquals("warn", resultRule2.getAction());
    }

    @Test
    public void testOneTemplateAndOneInherit() {
        // 1 template plot with 1 inherit plot, no data lines
        templatePlotConfig.setType(PlotType.AREA_PLOT);
        inheritPlotConfig.setName("MyDistinctPlotName");

        PlotConfig resultPlotConfig = templateGenerator.handlePlotGenerationForTemplate(inheritPlotConfig);
        assertEquals("MyDistinctPlotName", resultPlotConfig.getName());
        assertEquals(PlotType.AREA_PLOT, resultPlotConfig.getType());
    }

    @Test
    public void testOneTemplateAndTwoInherits() {
        // 1 template plot with 2 inherit plots, no data lines
        inheritPlotConfig.setName("MyDistinctPlotName");

        PlotConfig inheritPlotConfig2 = new PlotConfig("", testLogger);
        inheritPlotConfig2.setInheritTemplate("template");
        inheritPlotConfig2.setName("MyDistinctPlotName2");

        allPlotConfigs.add(inheritPlotConfig2);

        PlotConfig resultPlotConfig2 = templateGenerator.handlePlotGenerationForTemplate(inheritPlotConfig2);
        assertEquals("MyDistinctPlotName2", resultPlotConfig2.getName());
        assertEquals("category", resultPlotConfig2.getCategory());
    }

    @Test
    public void testOneTemplateAndOneInheritWithOneDataLine() {
        // 1 template plot with 1 inherit plot, 1 new data line
        DataLine dataLine = new DataLine(fileConfig, "");
        dataLine.setName("ChildDataLine");
        inheritPlotConfig.getDataLines().add(dataLine);

        PlotConfig resultPlotConfig = templateGenerator.handlePlotGenerationForTemplate(inheritPlotConfig);
        assertEquals(1, resultPlotConfig.getDataLines().size());
        assertEquals("ChildDataLine", resultPlotConfig.getDataLines().get(0).getName());
    }

    @Test
    public void testOneTemplateAndOneInheritWithOneInheritedDataLine() {
        // 1 template plot with 1 inherit plot, 1 inherited data line
        DataLine dataLine = new DataLine(fileConfig, "");
        dataLine.setName("DataLine");
        dataLine.setTemplateName("DataLineTemplate");
        templatePlotConfig.getDataLines().add(dataLine);

        DataLine inheritDataLine = new DataLine(fileConfig, "");
        inheritDataLine.setInheritTemplate("DataLineTemplate");
        inheritPlotConfig.getDataLines().add(inheritDataLine);

        PlotConfig resultPlotConfig = templateGenerator.handlePlotGenerationForTemplate(inheritPlotConfig);
        assertEquals(1, resultPlotConfig.getDataLines().size());
        assertEquals("DataLine", resultPlotConfig.getDataLines().get(0).getName());
    }

    @Test
    public void testOneTemplateAndOneInheritWithTwoDataLines() {
        // 1 template plot with 1 inherit plot, 1 new and 1 inherited data line
        DataLine dataLine = new DataLine(fileConfig, "");
        dataLine.setName("ParentDataLine");
        dataLine.setTemplateName("DataLineTemplate");
        templatePlotConfig.getDataLines().add(dataLine);

        DataLine childDataLine = new DataLine(fileConfig, "");
        childDataLine.setName("ChildDataLine");
        templatePlotConfig.getDataLines().add(childDataLine);

        DataLine inheritDataLine = new DataLine(fileConfig, "");
        inheritDataLine.setInheritTemplate("DataLineTemplate");
        inheritPlotConfig.getDataLines().add(inheritDataLine);

        PlotConfig resultPlotConfig = templateGenerator.handlePlotGenerationForTemplate(inheritPlotConfig);
        assertEquals(2, resultPlotConfig.getDataLines().size());
        assertEquals("ChildDataLine", resultPlotConfig.getDataLines().get(0).getName());
        assertEquals("ParentDataLine", resultPlotConfig.getDataLines().get(1).getName());
    }

    @Test
    public void testOneNonTemplatePlotWithTemplateAndInheritDataLines() {
        // 1 non-template plot with 1 template data line and 1 inherit data line
        DataLine dataLine = new DataLine(fileConfig, "");
        dataLine.setName("ParentDataLine");
        dataLine.setTemplateName("DataLineTemplate");
        templatePlotConfig.getDataLines().add(dataLine);

        DataLine inheritDataLine = new DataLine(fileConfig, "");
        inheritDataLine.setInheritTemplate("DataLineTemplate");
        templatePlotConfig.getDataLines().add(inheritDataLine);

        PlotConfig resultPlotConfig = templateGenerator.handlePlotGenerationForTemplate(inheritPlotConfig);
        assertEquals(2, resultPlotConfig.getDataLines().size());
        assertEquals("ParentDataLine", resultPlotConfig.getDataLines().get(0).getName());
        assertEquals("ParentDataLine", resultPlotConfig.getDataLines().get(1).getName());        
    }
}
