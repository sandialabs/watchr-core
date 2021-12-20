/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.generators.line;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.PlotConfig;
import gov.sandia.watchr.config.PlotConfig.CanvasLayout;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.log.StringOutputLogger;
import gov.sandia.watchr.parse.generators.line.impl.ScatterPlotDataLineGenerator;

public class DataLineGeneratorTest {
    
    private DataLineGenerator dataLineGenerator;
    private StringOutputLogger testLogger;

    @Before
    public void setup() {
        testLogger = new StringOutputLogger();
        PlotConfig plotConfig = new PlotConfig("", testLogger);
        dataLineGenerator = new ScatterPlotDataLineGenerator(plotConfig, null, null);
    }

    @Test
    public void testNewCanvas_Grid() {
        PlotWindowModel windowModel = new PlotWindowModel("");
        int gridRowSize = 3;
        PlotCanvasModel canvas1 = dataLineGenerator.newCanvasModel(windowModel, CanvasLayout.GRID, gridRowSize);
        PlotCanvasModel canvas2 = dataLineGenerator.newCanvasModel(windowModel, CanvasLayout.GRID, gridRowSize);
        PlotCanvasModel canvas3 = dataLineGenerator.newCanvasModel(windowModel, CanvasLayout.GRID, gridRowSize);
        PlotCanvasModel canvas4 = dataLineGenerator.newCanvasModel(windowModel, CanvasLayout.GRID, gridRowSize);
        PlotCanvasModel canvas5 = dataLineGenerator.newCanvasModel(windowModel, CanvasLayout.GRID, gridRowSize);
        PlotCanvasModel canvas6 = dataLineGenerator.newCanvasModel(windowModel, CanvasLayout.GRID, gridRowSize);

        assertEquals(6, windowModel.getCanvasModels().size());
        assertEquals(0, windowModel.getCanvasModels().get(0).getRowPosition());
        assertEquals(0, windowModel.getCanvasModels().get(0).getColPosition());
        assertEquals(0, windowModel.getCanvasModels().get(1).getRowPosition());
        assertEquals(1, windowModel.getCanvasModels().get(1).getColPosition());
        assertEquals(0, windowModel.getCanvasModels().get(2).getRowPosition());
        assertEquals(2, windowModel.getCanvasModels().get(2).getColPosition());
        assertEquals(1, windowModel.getCanvasModels().get(3).getRowPosition());
        assertEquals(0, windowModel.getCanvasModels().get(3).getColPosition());
        assertEquals(1, windowModel.getCanvasModels().get(4).getRowPosition());
        assertEquals(1, windowModel.getCanvasModels().get(4).getColPosition());
        assertEquals(1, windowModel.getCanvasModels().get(5).getRowPosition());
        assertEquals(2, windowModel.getCanvasModels().get(5).getColPosition());

        assertEquals(canvas1, windowModel.getCanvasModels().get(0));
        assertEquals(canvas2, windowModel.getCanvasModels().get(1));
        assertEquals(canvas3, windowModel.getCanvasModels().get(2));
        assertEquals(canvas4, windowModel.getCanvasModels().get(3));
        assertEquals(canvas5, windowModel.getCanvasModels().get(4));
        assertEquals(canvas6, windowModel.getCanvasModels().get(5));
    }

    @Test
    public void testNewCanvas_Independent() {
        PlotWindowModel windowModel = new PlotWindowModel("");
        PlotCanvasModel canvas1 = dataLineGenerator.newCanvasModel(windowModel, CanvasLayout.INDEPENDENT);
        canvas1.setName("Canvas1");
        PlotCanvasModel canvas2 = dataLineGenerator.newCanvasModel(windowModel, CanvasLayout.INDEPENDENT);
        canvas1.setName("Canvas2");
        PlotCanvasModel canvas3 = dataLineGenerator.newCanvasModel(windowModel, CanvasLayout.INDEPENDENT);
        canvas1.setName("Canvas3");

        assertEquals(3, windowModel.getCanvasModels().size());
        assertFalse(canvas1.isOverlaid());
        assertTrue(canvas2.isOverlaid());
        assertTrue(canvas3.isOverlaid());
        assertEquals(canvas1, canvas2.getBaseCanvasModelIfOverlaid());
        assertEquals(canvas1, canvas3.getBaseCanvasModelIfOverlaid());
    }

    @Test
    public void testNewCanvas_Shared() {
        PlotWindowModel windowModel = new PlotWindowModel("");
        PlotCanvasModel canvas1 = dataLineGenerator.newCanvasModel(windowModel);
        PlotCanvasModel canvas2 = dataLineGenerator.newCanvasModel(windowModel);

        assertEquals(1, windowModel.getCanvasModels().size());
        assertSame(canvas1, canvas2);
    }

    @Test
    public void testNewCanvas_StackX() {
        PlotWindowModel windowModel = new PlotWindowModel("");
        PlotCanvasModel canvas1 = dataLineGenerator.newCanvasModel(windowModel, CanvasLayout.STACKX);
        PlotCanvasModel canvas2 = dataLineGenerator.newCanvasModel(windowModel, CanvasLayout.STACKX);
        PlotCanvasModel canvas3 = dataLineGenerator.newCanvasModel(windowModel, CanvasLayout.STACKX);

        assertEquals(3, windowModel.getCanvasModels().size());
        assertEquals(0, windowModel.getCanvasModels().get(0).getRowPosition());
        assertEquals(0, windowModel.getCanvasModels().get(0).getColPosition());
        assertEquals(0, windowModel.getCanvasModels().get(1).getRowPosition());
        assertEquals(1, windowModel.getCanvasModels().get(1).getColPosition());
        assertEquals(0, windowModel.getCanvasModels().get(2).getRowPosition());
        assertEquals(2, windowModel.getCanvasModels().get(2).getColPosition());

        assertEquals(canvas1, windowModel.getCanvasModels().get(0));
        assertEquals(canvas2, windowModel.getCanvasModels().get(1));
        assertEquals(canvas3, windowModel.getCanvasModels().get(2));
    }

    @Test
    public void testNewCanvas_StackY() {
        PlotWindowModel windowModel = new PlotWindowModel("");
        PlotCanvasModel canvas1 = dataLineGenerator.newCanvasModel(windowModel, CanvasLayout.STACKY);
        PlotCanvasModel canvas2 = dataLineGenerator.newCanvasModel(windowModel, CanvasLayout.STACKY);
        PlotCanvasModel canvas3 = dataLineGenerator.newCanvasModel(windowModel, CanvasLayout.STACKY);

        assertEquals(3, windowModel.getCanvasModels().size());
        assertEquals(0, windowModel.getCanvasModels().get(0).getRowPosition());
        assertEquals(0, windowModel.getCanvasModels().get(0).getColPosition());
        assertEquals(1, windowModel.getCanvasModels().get(1).getRowPosition());
        assertEquals(0, windowModel.getCanvasModels().get(1).getColPosition());
        assertEquals(2, windowModel.getCanvasModels().get(2).getRowPosition());
        assertEquals(0, windowModel.getCanvasModels().get(2).getColPosition());

        assertEquals(canvas1, windowModel.getCanvasModels().get(0));
        assertEquals(canvas2, windowModel.getCanvasModels().get(1));
        assertEquals(canvas3, windowModel.getCanvasModels().get(2));
    }    
}
