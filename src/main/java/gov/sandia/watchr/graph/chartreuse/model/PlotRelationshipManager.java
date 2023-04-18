/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
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
        synchronized(windowModels) {
            for(int i = 0; i < windowModels.size(); i++) {
                PlotWindowModel windowModel = windowModels.get(i);
                if(windowModel != null && windowModel.getUUID().equals(windowModelUUID)) {
                    return windowModel;
                }
            }
        }
        return null;
    }

    public static void addWindowModel(PlotWindowModel windowModel) {
        windowModels.add(windowModel);
    }     

    public static PlotCanvasModel getCanvasModel(UUID canvasModelUUID) {
        synchronized(canvasModels) {
            for(int i = 0; i < canvasModels.size(); i++) {
                PlotCanvasModel canvasModel = canvasModels.get(i);
                if(canvasModel != null && canvasModel.getUUID().equals(canvasModelUUID)) {
                    return canvasModel;
                }
            }
        }
        return null;
    }

    public static void addCanvasModel(PlotCanvasModel canvasModel) {
        synchronized(canvasModels) {
            boolean foundExistingCanvas = false;
            for(int i = 0; i < canvasModels.size(); i++) {
                PlotCanvasModel foundCanvasModel = canvasModels.get(i);
                if(foundCanvasModel != null && foundCanvasModel.getUUID().equals(canvasModel.getUUID())) {
                    foundExistingCanvas = true;
                }
            }

            if(!foundExistingCanvas) {
                canvasModels.add(canvasModel);
            }
        }
    }
}
