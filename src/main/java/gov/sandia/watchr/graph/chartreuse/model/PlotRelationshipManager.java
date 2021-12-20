/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.chartreuse.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlotRelationshipManager {
    
    private static final List<PlotWindowModel> windowModels;
    private static final List<PlotCanvasModel> canvasModels;

    static {
        windowModels = new ArrayList<>();
        canvasModels = new ArrayList<>();
    }

    private PlotRelationshipManager() {}

    public static PlotWindowModel getWindowModel(UUID windowModelUUID) {
        for(int i = 0; i < windowModels.size(); i++) {
            PlotWindowModel windowModel = windowModels.get(i);
            if(windowModel.getUUID().equals(windowModelUUID)) {
                return windowModel;
            }
        }
        return null;
    }

    public static void addWindowModel(PlotWindowModel windowModel) {
        windowModels.add(windowModel);
    }     

    public static PlotCanvasModel getCanvasModel(UUID canvasModelUUID) {
        for(PlotCanvasModel canvasModel : canvasModels) {
            if(canvasModel.getUUID().equals(canvasModelUUID)) {
                return canvasModel;
            }
        }
        return null;
    }

    public static void addCanvasModel(PlotCanvasModel canvasModel) {
        canvasModels.add(canvasModel);
    }
}
