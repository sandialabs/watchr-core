/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.chartreuse.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.graph.chartreuse.ChartreuseException;
import gov.sandia.watchr.parse.generators.rule.actors.RulePlotTraceModelFailActor;
import gov.sandia.watchr.util.CommonConstants;
import gov.sandia.watchr.util.RGB;

import java.util.TreeMap;
import java.util.UUID;

/**
 * A plot window model is the top-level container for a graphical visualization of Chartreuse plot data.
 * A plot window model can contain one or more {@link PlotCanvasModel}s.
 * 
 * @author Elliott Ridgway
 *
 */
public class PlotWindowModel {
	
	////////////
	// FIELDS //
	////////////
	
	private final UUID uuid;
	private static final String VERSION = "3.0"; // Used for backwards compatibility. //$NON-NLS-1$
	
	private String name;
	private String font;
	private String nickname;
	private int viewHeight = -1;
	private int viewWidth = -1;
	private boolean legendVisible;
	private RGB backgroundColor;
	
	private String divName;
	private String category;
	private final boolean isRoot;
	
	////////////////////
	// CHILD ELEMENTS //
	////////////////////
	
	private List<PlotCanvasModel> canvasModels;
	
	/////////////////
	// CONSTRUCTOR //
	/////////////////
	
	public PlotWindowModel(String name) {
		uuid = UUID.randomUUID();
		PlotRelationshipManager.addWindowModel(this);

		this.name = name;
		if(name.equals(CommonConstants.ROOT_PATH_ALIAS)) {
			isRoot = true;
		} else {
			isRoot = false;
		}

		divName = "plotDiv";
		canvasModels = new ArrayList<>();
		category = "";
		nickname = "";
		backgroundColor = new RGB(255, 255, 255);
	}
	
	public PlotWindowModel(PlotWindowModel copy) throws ChartreuseException {
	    this.uuid = UUID.randomUUID();
		PlotRelationshipManager.addWindowModel(this);
		
        name = copy.getName();
        font = copy.getFont();
        viewHeight = copy.getViewHeight();
        viewWidth = copy.getViewWidth();
		canvasModels = new ArrayList<>();
		nickname = copy.getNickname();
		List<PlotCanvasModel> copyCanvasModels = new ArrayList<>(copy.getCanvasModels());
        for(PlotCanvasModel canvasModel : copyCanvasModels) {
            new PlotCanvasModel(uuid, canvasModel);
		}
		backgroundColor = copy.getBackgroundColor();
		
		legendVisible = copy.getLegendVisible();
		category = copy.getCategory();
		divName = copy.getDivName();
		isRoot = copy.isRoot();

	}

	/**
	 * Attaches an existing {@link PlotCanvasModel} to a new parent
	 * {@link PlotWindowModel} that has default settings configured
	 * automatically.  The canvas will be placed at (0, 0).
	 * 
	 * @param canvasModel The PlotCanvasModel to give a new parent
	 * PlotWindowModel.
	 * @return The new PlotWindowModel.
	 */
	public PlotWindowModel(PlotCanvasModel canvasModel) {
		uuid = UUID.randomUUID();
		PlotRelationshipManager.addWindowModel(this);

		divName = "plotDiv";
		canvasModels = new ArrayList<>();
		category = "";
		nickname = "";
		backgroundColor = new RGB(255, 255, 255);

		this.setName(canvasModel.getName())
			.setLegendVisible(canvasModel.getTraceModels().size() > 1)
			.setFont("Segoe UI");
			
		this.isRoot = false;
		
		canvasModel
			.setRowPosition(0)
			.setColPosition(0);
		this.addCanvasModel(canvasModel);
	}
	
	/////////////
	// GETTERS //
	/////////////
	
	public UUID getUUID() {
		return uuid;
	}
	
	public String getVersion() {
		return VERSION;
	}
	
	public String getName() {
		return name;
	}
	
	public String getFont() {
		return font;
	}

	public int getViewHeight() {
		return viewHeight;
	}

	public int getViewWidth() {
		return viewWidth;
	}
	
	public List<PlotCanvasModel> getCanvasModels() {
		if(canvasModels == null) {
			canvasModels = new ArrayList<>();
		}
		
		// Unmodifiable because we want to force users to call addCanvasModel,
		// which auto-sets parent information.
		return Collections.unmodifiableList(canvasModels);
	}
	
	public PlotCanvasModel getCanvasModel(int row, int col, boolean getOverlaid) {
		for(PlotCanvasModel canvasModel : canvasModels) {
			if((canvasModel.getRowPosition() == row && canvasModel.getColPosition() == col) &&
			   (canvasModel.isOverlaid() && getOverlaid) || (!canvasModel.isOverlaid() && !getOverlaid)) {
				return canvasModel;
			}
		}
		return null;
	}
	
	public boolean getLegendVisible() {
		return legendVisible;
	}

	public String getDivName() {
		return divName;
	}

	public String getCategory() {
		return category;
	}

	public boolean isRoot() {
		return isRoot;
	}

	public RGB getBackgroundColor() {
		return backgroundColor;
	}

	public String getNickname() {
		return nickname;
	}
	
	public String getNameOrNickname() {
		if(this.hasNickname()){
			return getNickname();
		}
		else{
			return getName();
		}
	}

	////////////////////////
	// GETTERS (COMPUTED) //
	////////////////////////
	
	public int getRowCount() {
		return getChildCanvasesAsTable().size();
	}
	
	public int getColCount() {
		if(getChildCanvasesAsTable().isEmpty()) {
			return 0;
		}
		return getChildCanvasesAsTable().get(0).size();
	}
	
	public List<List<PlotCanvasModel>> getChildCanvasesAsTable() {
		/////////////////////////////////////////////
		// Outer list is row, inner list is column //
		/////////////////////////////////////////////
		
		Map<Integer, List<PlotCanvasModel>> temporaryMap = new TreeMap<>(); // Sorted by key
		for(PlotCanvasModel canvasModel : getCanvasModels()) {
			if(canvasModel.getBaseCanvasModelIfOverlaid() != null) {
				// Don't consider overlaid canvases when constructing as a table.
				// (We can always get to overlaid canvases through the getOverlaidCanvasModels()
				// method in PlotCanvasModel)
				continue;
			}
			
			int index = canvasModel.getRowPosition();
			List<PlotCanvasModel> columnList = temporaryMap.getOrDefault(index, new ArrayList<>());
			columnList.add(canvasModel);
			temporaryMap.put(index, columnList);
		}
		
		List<List<PlotCanvasModel>> finalTable = new ArrayList<>();
		for(Entry<Integer, List<PlotCanvasModel>> entry : temporaryMap.entrySet()) {
			List<PlotCanvasModel> columnList = entry.getValue();
			finalTable.add(columnList);
		}
		return finalTable;
	}

	public int getNextCanvasRow(int preferredRowSize) {
		int canvasCount = getCanvasModels().size();
		if(canvasCount < preferredRowSize) {
			return 0;
		} else {
			return (canvasCount / preferredRowSize);
		}
	}

	public int getNextCanvasColumn(int preferredRowSize) {
		int canvasCount = getCanvasModels().size();
		return canvasCount % preferredRowSize;
	}

	public boolean isFailing() {
		return getBackgroundColor().equals(RulePlotTraceModelFailActor.FAIL_COLOR);
	}

	/////////////
	// SETTERS //
	/////////////

	public PlotWindowModel setName(String name) {
		this.name = name;
		return this;
	}
	
	public PlotWindowModel setFont(String font) {
		this.font = font;
		return this;
	}
	
	public PlotWindowModel setViewHeight(int viewHeight) {
		this.viewHeight = viewHeight;
		return this;
	}
	
	public PlotWindowModel setViewWidth(int viewWidth) {
		this.viewWidth = viewWidth;
		return this;
	}
	
	public PlotWindowModel setLegendVisible(boolean legendVisible) {
		this.legendVisible = legendVisible;
		return this;
	}
	
	public void addCanvasModel(PlotCanvasModel canvasModel) {
		this.canvasModels.add(canvasModel);
		canvasModel.setParent(uuid);
	}
	
	public boolean removeCanvasModel(PlotCanvasModel canvasModel) {
		return canvasModels.remove(canvasModel);
	}
	
	public boolean removeAllCanvasModels(List<PlotCanvasModel> canvasModels) {
		return this.canvasModels.removeAll(canvasModels);
	}
	
	public void clearCanvasModels() {
		canvasModels.clear();
	}

	public void setDivName(String divName) {
		this.divName = divName;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public PlotWindowModel setBackgroundColor(RGB backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	public void setNickname(String nickname){
		this.nickname = nickname;
	}
	
	/////////////
	// UTILITY //
	/////////////
	
	public boolean isSingleCanvas() {
		return 
			   getChildCanvasesAsTable().size() == 1 &&
			   getChildCanvasesAsTable().get(0).size() == 1;
	}

	public boolean hasNickname() {
		return StringUtils.isNotBlank(nickname);
	}
	
	/**
	 * Determine if a {@link PlotWindowModel} contains a canvas that needs 3D
	 * rendering.<br><br>
	 * Note that this method will short-circuit to returning {@code false} if the provided
	 * PlotWindowModel has multiple canvases, because multi-canvas
	 * plot windows do not currently support displaying rendered 3D data.
	 * 
	 * @return True if it contains rendered 3D data.
	 */
	public boolean is3DWindowModel() {
		boolean isThreeDimensional = true;
		if(getCanvasModels().size() == 1) {
			isThreeDimensional = getCanvasModels().get(0).is3DRenderedCanvasModel();			
		} else {
			isThreeDimensional = false;
		}
		return isThreeDimensional;
	}

	public boolean isEmpty2D() {
		boolean empty = StringUtils.isBlank(name);
		empty = empty || canvasModels.isEmpty();

		if(!empty) {
			for(int i = 0; i < canvasModels.size(); i++) {
				PlotCanvasModel canvasModel = canvasModels.get(i);
				empty = empty || canvasModels.isEmpty();
				if(!empty) {
					for(int j = 0; j < canvasModel.getTraceModels().size(); j++) {
						PlotTraceModel traceModel = canvasModel.getTraceModels().get(j);
						empty = empty || traceModel.isEmpty2D();
					}
				}
			}
		}
		return empty;
	}
	
	////////////////
	// HEURISTICS //
	////////////////
	
	/**
	 * This method attempts to determine a reasonable default width
	 * for a {@link PlotWindowModel} heuristically, based on 1) the
	 * maximum point density on a given {@link PlotTraceModel}, and
	 * 2) the number of {@link PlotCanvasModel} rows.
	 *  
	 * @param windowModel The PlotWindowModel to examine.
	 * @return A heuristically recommended width.
	 */
	public int getHeuristicWidth() {
		int defaultPointDensity = 300;
		int defaultWidth = 1024;
		
		int colSize = getColCount();
		int maxTracePoints = 0;
		
		for(PlotCanvasModel canvasModel : getCanvasModels()) {
			for(PlotTraceModel traceModel : canvasModel.getTraceModels()) {
				if(traceModel.getPoints().size() > maxTracePoints) {
					maxTracePoints = traceModel.getPoints().size();
				}
			}
		}
		if(colSize > 0 && maxTracePoints > 0) {
			int traceDensity = maxTracePoints;
			if(traceDensity < defaultPointDensity) {
				traceDensity = defaultPointDensity;
			}
			
			int proposedWidth = traceDensity * colSize;
			if(proposedWidth > defaultWidth) {
				return proposedWidth;
			}
		}
		return defaultWidth;
	}
	
	/**
	 * This method attempts to determine a reasonable default height
	 * for a {@link PlotWindowModel} heuristically, based on 1) the
	 * maximum point density on a given {@link PlotTraceModel}, and
	 * 2) the number of {@link PlotCanvasModel} columns.
	 *  
	 * @param windowModel The PlotWindowModel to examine.
	 * @return A heuristically recommended height.
	 */
	public int getHeuristicHeight() {
		int defaultPointDensity = 300;
		int defaultHeight = 768;
		
		int rowSize = getRowCount();
		int maxTracePoints = 0;
		
		for(PlotCanvasModel canvasModel : getCanvasModels()) {
			for(PlotTraceModel traceModel : canvasModel.getTraceModels()) {
				if(traceModel.getPoints().size() > maxTracePoints) {
					maxTracePoints = traceModel.getPoints().size();
				}
			}
		}
		if(rowSize > 0 && maxTracePoints > 0) {
			int traceDensity = maxTracePoints;
			if(traceDensity < defaultPointDensity) {
				traceDensity = defaultPointDensity;
			}
			
			int proposedHeight = traceDensity * rowSize;
			if(proposedHeight > defaultHeight) {
				return proposedHeight;
			}
		}
		return defaultHeight;
	}
	
	public boolean effectiveEquals(Object other) {
		boolean equals = false;
		if(other instanceof PlotWindowModel) {
			PlotWindowModel otherModel  = (PlotWindowModel) other;
			
			if(otherModel.getFont() != null && getFont() != null) {
				equals = otherModel.getFont().equals(getFont());
			} else {
				equals = otherModel.getFont() == null && getFont() == null;
			}
			
			equals = equals && otherModel.getLegendVisible() == getLegendVisible();
			equals = equals && otherModel.getName().equals(getName());
			equals = equals && otherModel.getVersion().equals(getVersion());
			equals = equals && otherModel.getViewWidth() == getViewWidth();
			equals = equals && otherModel.getViewHeight() == getViewHeight();
			
			equals = equals && otherModel.getCanvasModels().equals(getCanvasModels());
		}
		return equals;
	}
	
	////////////////
	// OVERRIDDEN //
	////////////////
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof PlotWindowModel) {
			PlotWindowModel otherModel  = (PlotWindowModel) other;
			return otherModel.getUUID().equals(uuid);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return uuid.hashCode();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PlotWindowModel: [").append(name);
		sb.append("]");
		return sb.toString();
	}
	
}
