package gov.sandia.watchr.graph.chartreuse.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PlotTracePointTest {
    
    @Test
    public void testCopyConstructor() {
        PlotTracePoint point = new PlotTracePoint("1", "2", "3");
        point.metadata.put("A", "B");

        PlotTracePoint point2 = new PlotTracePoint(point);
        assertEquals("1", point2.x);
        assertEquals("2", point2.y);
        assertEquals("3", point2.z);
        assertEquals("B", point2.metadata.get("A"));
        assertEquals(1, point2.metadata.size());
    }

    @Test
    public void testToString() {
        PlotTracePoint point = new PlotTracePoint("1", "2", "3");
        point.metadata.put("A", "B");

        assertEquals("[1, 2, 3]", point.toString());
    }
}
