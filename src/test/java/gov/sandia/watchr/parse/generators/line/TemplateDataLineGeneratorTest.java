package gov.sandia.watchr.parse.generators.line;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import gov.sandia.watchr.config.DataLine;
import gov.sandia.watchr.config.FileConfig;
import gov.sandia.watchr.config.HierarchicalExtractor;
import gov.sandia.watchr.config.file.DefaultFileReader;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.log.StringOutputLogger;
import gov.sandia.watchr.util.OsUtil;
import gov.sandia.watchr.util.RGB;

public class TemplateDataLineGeneratorTest {
    
    private List<DataLine> allDataLines;
    private TemplateDataLineGenerator dataLineGenerator;

    @Test
    public void testCascadingInheritAndTemplateDataLines() {
        ILogger logger = new StringOutputLogger();
        FileConfig fileConfig = new FileConfig("", logger, new DefaultFileReader(logger));

        HierarchicalExtractor xExtractor = new HierarchicalExtractor(fileConfig, "");
        xExtractor.setKey("A");
        HierarchicalExtractor yExtractor = new HierarchicalExtractor(fileConfig, "");
        yExtractor.setKey("B");

        DataLine dataLine1 = new DataLine(fileConfig, "");
        dataLine1.setName("Level1");
        dataLine1.setTemplateName("baseTemplate");
        dataLine1.setColor(new RGB(0, 100, 255));
        dataLine1.setXExtractor(xExtractor);

        DataLine dataLine2 = new DataLine(fileConfig, "");
        dataLine2.setName("Level2");
        dataLine2.setInheritTemplate("baseTemplate");
        dataLine2.setTemplateName("middleTemplate");
        dataLine2.setColor(new RGB(100, 0, 255));
        dataLine2.setYExtractor(yExtractor);

        DataLine dataLine3 = new DataLine(fileConfig, "");
        dataLine3.setName("Level3");
        dataLine3.setInheritTemplate("middleTemplate");
        dataLine3.setColor(new RGB(0, 0, 0));

        // The lines are deliberately added in reverse order in order to ensure that
        // inherit/template relationships can be resolved out of order.
        allDataLines = new ArrayList<>();
        allDataLines.add(dataLine3);
        allDataLines.add(dataLine2);
        allDataLines.add(dataLine1);

        dataLineGenerator = new TemplateDataLineGenerator(allDataLines, logger);
        DataLine appliedDataLine3 = dataLineGenerator.handleDataLineGenerationForTemplate(dataLine3);
        DataLine appliedDataLine2 = dataLineGenerator.handleDataLineGenerationForTemplate(dataLine2);

        assertEquals(new RGB(100, 0, 255), appliedDataLine2.getColor());
        assertEquals(xExtractor, appliedDataLine2.getXExtractor());

        assertEquals(new RGB(0,0,0), appliedDataLine3.getColor());
        assertEquals(xExtractor, appliedDataLine3.getXExtractor());
        assertEquals(yExtractor, appliedDataLine3.getYExtractor());
    }

    @Test
    public void testMissingTemplateDataLine() {
        StringOutputLogger logger = new StringOutputLogger();
        FileConfig fileConfig = new FileConfig("", logger, new DefaultFileReader(logger));

        DataLine dataLine2 = new DataLine(fileConfig, "");
        dataLine2.setName("Level2");
        dataLine2.setInheritTemplate("baseTemplate");
        dataLine2.setTemplateName("middleTemplate");

        // The lines are deliberately added in reverse order in order to ensure that
        // inherit/template relationships can be resolved out of order.
        allDataLines = new ArrayList<>();
        allDataLines.add(dataLine2);

        dataLineGenerator = new TemplateDataLineGenerator(allDataLines, logger);
        dataLineGenerator.handleDataLineGenerationForTemplate(dataLine2);

        assertEquals(
            "Data line depends on template baseTemplate, but this template does not exist in the configuration." + OsUtil.getOSLineBreak(),
            logger.getLogAsString());
    }
}
