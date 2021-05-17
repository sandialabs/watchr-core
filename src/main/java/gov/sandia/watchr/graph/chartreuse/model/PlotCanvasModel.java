/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.chartreuse.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.config.DerivativeLine.DerivativeLineType;
import gov.sandia.watchr.graph.chartreuse.Dimension;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.util.ArrayUtil;
import gov.sandia.watchr.util.RGB;

/**
 * A plot canvas model can be thought of a single set of axes.  A plot canvas can show one or more
 * {@link PlotTraceModel}s by grouping them onto the same set of axes.
 * 
 * @author Elliott Ridgway
 *
 */
public class PlotCanvasModel {
	
	////////////
	// FIELDS //
	////////////
	
	private static final String VERSION = "3.0"; // Used for backwards compatibility. //$NON-NLS-1$
	
	public static final int DEFAULT_CANVAS_DECIMAL_PRECISION = 3;
	
	private final UUID uuid;

	private String name;
	private int rowPosition;
	private int colPosition;
	private int axisPrecision;
	private String xAxisLabel;
	private String yAxisLabel;
	private String zAxisLabel;
	private RGB xAxisRGB;
	private RGB yAxisRGB;
	private RGB zAxisRGB;
	private Double xAxisRangeStart;
	private Double xAxisRangeEnd;
	private Double yAxisRangeStart;
	private Double yAxisRangeEnd;
	private Double zAxisRangeStart;
	private Double zAxisRangeEnd;
	private boolean autoscale;
	private boolean xLogScale;
	private boolean yLogScale;
	private boolean zLogScale;
	private boolean drawGridLines;
	private boolean drawAxisLines;
		
	////////////////////
	// PARENT ELEMENT //
	////////////////////
	
	private UUID parentWindowModelUUID;
	
	////////////////////
	// CHILD ELEMENTS //
	////////////////////
	
	private List<PlotCanvasModel> overlaidCanvasModels;
	private List<PlotTraceModel> traceModels;
	
	/////////////////
	// CONSTRUCTOR //
	/////////////////
	
	public PlotCanvasModel(UUID parentWindowModelUUID) {
		this.parentWindowModelUUID = parentWindowModelUUID;

		this.uuid = UUID.randomUUID();
		PlotRelationshipManager.addCanvasModel(this);

		if(parentWindowModelUUID != null) {
			PlotWindowModel parent = getParent();
			parent.addCanvasModel(this);
		}
		
		autoscale = true;
		overlaidCanvasModels = new ArrayList<>();
		traceModels = new ArrayList<>();
		
		xAxisRangeStart = null;
		yAxisRangeStart = null;
		zAxisRangeStart = null;
		xAxisRangeEnd = null;
		yAxisRangeEnd = null;
		zAxisRangeEnd = null;

		axisPrecision = DEFAULT_CANVAS_DECIMAL_PRECISION;
	}
	
	public PlotCanvasModel(UUID parentWindowModelUUID, PlotCanvasModel copy) {
	    this(parentWindowModelUUID);

        this.setName(copy.getName())
            .setRowPosition(copy.getRowPosition())
            .setColPosition(copy.getColPosition())
            .setXAxisLabel(copy.getXAxisLabel())
            .setYAxisLabel(copy.getYAxisLabel())
            .setZAxisLabel(copy.getZAxisLabel())
            .setXAxisRGB(copy.getXAxisRGB())
            .setYAxisRGB(copy.getYAxisRGB())
            .setZAxisRGB(copy.getZAxisRGB())
            .setAxisPrecision(copy.getAxisPrecision())
            .setAutoscale(copy.getAutoscale())
            .setXLogScale(copy.getXLogScale())
            .setYLogScale(copy.getYLogScale())
            .setZLogScale(copy.getZLogScale())
            .setDrawAxisLines(copy.getDrawAxisLines())
            .setDrawGridLines(copy.getDrawGridLines());

		if(copy.getXAxisRangeStart() != Double.NEGATIVE_INFINITY) {
			setXAxisRangeStart(copy.getXAxisRangeStart());
		}
		if(copy.getYAxisRangeStart() != Double.NEGATIVE_INFINITY) {
            setYAxisRangeStart(copy.getYAxisRangeStart());
		}
		if(copy.getZAxisRangeStart() != Double.NEGATIVE_INFINITY) { 
            setZAxisRangeStart(copy.getZAxisRangeStart());
		}
		if(copy.getXAxisRangeEnd() != Double.POSITIVE_INFINITY) {
            setXAxisRangeEnd(copy.getXAxisRangeEnd());
		}
		if(copy.getYAxisRangeEnd() != Double.POSITIVE_INFINITY) {
            setYAxisRangeEnd(copy.getYAxisRangeEnd());
		}
		if(copy.getZAxisRangeEnd() != Double.POSITIVE_INFINITY) {
            setZAxisRangeEnd(copy.getZAxisRangeEnd());
		}
	        
		List<PlotTraceModel> copyTraceModels = new ArrayList<>(copy.getTraceModels());
        for(PlotTraceModel trace : copyTraceModels) {
            new PlotTraceModel(uuid, trace);
        }
        
        for(PlotCanvasModel overlayCanvas : copy.getOverlaidCanvasModels()) {
            addOverlaidCanvasModel(new PlotCanvasModel(parentWindowModelUUID, overlayCanvas));
        }
	}
	
	/////////////
	// GETTERS //
	/////////////
	
	public PlotWindowModel getParent() {
		return PlotRelationshipManager.getWindowModel(parentWindowModelUUID);
	}

	public UUID getParentWindowModelUUID() {
		return parentWindowModelUUID;
	}

	public UUID getUUID() {
		return uuid;
	}	
	
	public String getVersion() {
		return VERSION;
	}
	
	public int getRowPosition() {
		return rowPosition;
	}
	
	public int getColPosition() {
		return colPosition;
	}
	
	public int getAxisPrecision() {
		return axisPrecision;
	}

	public String getName() {
		return name == null ? getDescriptivePositionName() : name;
	}
	
	public String getXAxisLabel() {
		return xAxisLabel == null ? "" : xAxisLabel; //$NON-NLS-1$
	}
	
	public String getYAxisLabel() {
		return yAxisLabel == null ? "" : yAxisLabel; //$NON-NLS-1$
	}
	
	public String getZAxisLabel() {
		return zAxisLabel == null ? "" : zAxisLabel; //$NON-NLS-1$
	}
	
	public RGB getXAxisRGB() {
		return xAxisRGB;
	}
	
	public RGB getYAxisRGB() {
		return yAxisRGB;
	}
	
	public RGB getZAxisRGB() {
		return zAxisRGB;
	}
	
	public double getXAxisRangeStart() {
		return xAxisRangeStart != null ? xAxisRangeStart : Double.NEGATIVE_INFINITY;
	}
	
	public double getXAxisRangeEnd() {
		return xAxisRangeEnd != null ? xAxisRangeEnd : Double.POSITIVE_INFINITY;
	}
	
	public double getYAxisRangeStart() {
		return yAxisRangeStart != null ? yAxisRangeStart : Double.NEGATIVE_INFINITY;
	}
	
	public double getYAxisRangeEnd() {
		return yAxisRangeEnd != null ? yAxisRangeEnd : Double.POSITIVE_INFINITY;
	}
	
	public double getZAxisRangeStart() {
		return zAxisRangeStart != null ? zAxisRangeStart : Double.NEGATIVE_INFINITY;
	}
	
	public double getZAxisRangeEnd() {
		return zAxisRangeEnd != null ? zAxisRangeEnd : Double.POSITIVE_INFINITY;
	}
	
	public boolean getAutoscale() {
		return autoscale;
	}
	
	public boolean getXLogScale() {
		return xLogScale;
	}
	
	public boolean getYLogScale() {
		return yLogScale;
	}
	
	public boolean getZLogScale() {
		return zLogScale;
	}
	
	public boolean getDrawGridLines() {
		return drawGridLines;
	}
	
	public boolean getDrawAxisLines() {
		return drawAxisLines;
	}
	
	public List<PlotCanvasModel> getOverlaidCanvasModels() {
		if(overlaidCanvasModels == null) {
			overlaidCanvasModels = new ArrayList<>();
		}
		
		// Unmodifiable because we want to force users to call addOverlaidCanvasModel,
		// which auto-sets associated overlay information.
		return Collections.unmodifiableList(overlaidCanvasModels);
	}
	
	public List<PlotTraceModel> getTraceModels() {
		if(traceModels == null) {
			traceModels = new ArrayList<>();
		}
		
		// Unmodifiable because we want to force users to call addTraceModel,
		// which auto-sets parent information.
		return Collections.unmodifiableList(traceModels);
	}

	public List<PlotTraceModel> getNonDerivativeTraceModels() {
		List<PlotTraceModel> baseTraceModels = new ArrayList<>();
		for(PlotTraceModel traceModel : traceModels) {
			if(traceModel.getDerivativeLineType() == null) {
				baseTraceModels.add(traceModel);
			}
		}
		return Collections.unmodifiableList(baseTraceModels);
	}
	
	////////////////////////
	// GETTERS (COMPUTED) //
	////////////////////////
	
	public String getDescriptivePositionName() {
		return "(" + (colPosition) + ", " + (rowPosition) + ")";
	}
	
	public String getDescriptivePositionNameOffsetOne() {
		return "(" + (colPosition+1) + ", " + (rowPosition+1) + ")";
	}
	
	public String getDescriptiveAxesName() {
		if(StringUtils.isBlank(xAxisLabel) && StringUtils.isBlank(yAxisLabel) && StringUtils.isBlank(zAxisLabel)) {
			return "<No axis text>";			
		} else {
			StringBuilder sb = new StringBuilder();
			if(!StringUtils.isBlank(xAxisLabel)) {
				sb.append(xAxisLabel);
			}
			if(!StringUtils.isBlank(xAxisLabel) && !StringUtils.isBlank(yAxisLabel)) {
				sb.append("/");
			}
			if(!StringUtils.isBlank(yAxisLabel)) {
				sb.append(yAxisLabel);
			}
			
			if(is3DCanvasModel()) {
				if(!StringUtils.isBlank(zAxisLabel) && !StringUtils.isBlank(xAxisLabel) || !StringUtils.isBlank(yAxisLabel)) {
					sb.append("/");
				}
				if(!StringUtils.isBlank(zAxisLabel)) {
					sb.append(zAxisLabel);
				}
			}
			return sb.toString();
		}
	}
	
	public int getInvertedRowPosition() {
		PlotWindowModel thisParent = getParent();
		if(thisParent != null) {
			int rowCount = thisParent.getRowCount() - 1;
			return rowCount - getRowPosition();
		}
		return -1;
	}
	
	public double getMinimumTraceValue(Dimension dim) {
		double minimum = Double.MAX_VALUE;
		for(PlotTraceModel trace : traceModels) {
			double newMinimum = ArrayUtil.getMinFromStringList(trace.getDimensionValues(dim));
			if(newMinimum <= minimum) {
				minimum = newMinimum;
			}
		}
		return minimum;
	}
	
	public double getMaximumTraceValue(Dimension dim) {
		double maximum = Double.MAX_VALUE * -1;
		for(PlotTraceModel trace : traceModels) {
			double newMaximum = ArrayUtil.getMaxFromStringList(trace.getDimensionValues(dim));
			if(newMaximum >= maximum) {
				maximum = newMaximum;
			}
		}
		return maximum;
	}
	
	public double getLocalMinimumTraceValue(Dimension dim) {
		double minimum = Double.MAX_VALUE;
		Set<PlotCanvasModel> canvases = new LinkedHashSet<>();
		canvases.add(this);
		if(isOverlaid()) {
			PlotCanvasModel baseCanvasModel = getBaseCanvasModelIfOverlaid();
			canvases.addAll(baseCanvasModel.getOverlaidCanvasModels());
		} else {
			canvases.addAll(getOverlaidCanvasModels());
		}
		
		for(PlotCanvasModel canvas : canvases) {
			double newMinimum = canvas.getMinimumTraceValue(dim);
			if(newMinimum < minimum) {
				minimum = newMinimum;
			}
		}
		return minimum;
	}
	
	public double getLocalMaximumTraceValue(Dimension dim) {
		double maximum = Double.MAX_VALUE * -1;
		Set<PlotCanvasModel> canvases = new LinkedHashSet<>();
		canvases.add(this);
		if(isOverlaid()) {
			PlotCanvasModel baseCanvasModel = getBaseCanvasModelIfOverlaid();
			canvases.addAll(baseCanvasModel.getOverlaidCanvasModels());
		} else {
			canvases.addAll(getOverlaidCanvasModels());
		}
		
		for(PlotCanvasModel canvas : canvases) {
			double newMaximum = canvas.getMaximumTraceValue(dim);
			if(newMaximum > maximum) {
				maximum = newMaximum;
			}
		}
		return maximum;
	}
	
	/////////////
	// SETTERS //
	/////////////
	
	protected void setParent(UUID parentWindowModelUUID) {
		this.parentWindowModelUUID = parentWindowModelUUID;
	} 

	public PlotCanvasModel setName(String name) {
		this.name = name;
		return this;
	}
	
	public PlotCanvasModel setRowPosition(int rowPosition) {
		this.rowPosition = rowPosition;
		for(PlotCanvasModel overlaidCanvasModel : overlaidCanvasModels) {
			overlaidCanvasModel.setRowPosition(rowPosition);
		}
		return this;
	}
	
	public PlotCanvasModel setColPosition(int colPosition) {
		this.colPosition = colPosition;
		for(PlotCanvasModel overlaidCanvasModel : overlaidCanvasModels) {
			overlaidCanvasModel.setColPosition(colPosition);
		}
		return this;
	}
	
	public PlotCanvasModel setAxisPrecision(int axisPrecision) {
		this.axisPrecision = axisPrecision;
		return this;
	}
	
	
	public PlotCanvasModel setXAxisLabel(String xAxisLabel) {
		this.xAxisLabel = xAxisLabel;
		return this;
	}
	
	public PlotCanvasModel setYAxisLabel(String yAxisLabel) {
		this.yAxisLabel = yAxisLabel;
		return this;
	}	
	
	public PlotCanvasModel setZAxisLabel(String zAxisLabel) {
		this.zAxisLabel = zAxisLabel;
		return this;
	}	
	
	public PlotCanvasModel setXLogScale(boolean xLogScale) {
		this.xLogScale = xLogScale;
		return this;
	}
	
	public PlotCanvasModel setYLogScale(boolean yLogScale) {
		this.yLogScale = yLogScale;
		return this;
	}
	
	public PlotCanvasModel setZLogScale(boolean zLogScale) {
		this.zLogScale = zLogScale;
		return this;
	}
	
	public PlotCanvasModel setXAxisRGB(RGB xAxisRGB) {
		this.xAxisRGB = xAxisRGB;
		return this;
	}
	
	public PlotCanvasModel setYAxisRGB(RGB yAxisRGB) {
		this.yAxisRGB = yAxisRGB;
		return this;
	}
	
	public PlotCanvasModel setZAxisRGB(RGB zAxisRGB) {
		this.zAxisRGB = zAxisRGB;
		return this;
	}
	
	public PlotCanvasModel setXAxisRangeStart(double xAxisRangeStart) {
		this.xAxisRangeStart = xAxisRangeStart;
		return this;
	}
	
	public PlotCanvasModel setXAxisRangeEnd(double xAxisRangeEnd) {
		this.xAxisRangeEnd = xAxisRangeEnd;
		return this;
	}
	
	public PlotCanvasModel setYAxisRangeStart(double yAxisRangeStart) {
		this.yAxisRangeStart = yAxisRangeStart;
		return this;
	}
	
	public PlotCanvasModel setYAxisRangeEnd(double yAxisRangeEnd) {
		this.yAxisRangeEnd = yAxisRangeEnd;
		return this;
	}
	
	public PlotCanvasModel setZAxisRangeStart(double zAxisRangeStart) {
		this.zAxisRangeStart = zAxisRangeStart;
		return this;
	}
	
	public PlotCanvasModel setZAxisRangeEnd(double zAxisRangeEnd) {
		this.zAxisRangeEnd = zAxisRangeEnd;
		return this;
	}
	
	public PlotCanvasModel setAutoscale(boolean autoscale) {
		this.autoscale = autoscale;
		return this;
	}
	
	public PlotCanvasModel setDrawGridLines(boolean showGridLines) {
		this.drawGridLines = showGridLines;
		return this;
	}
	
	public PlotCanvasModel setDrawAxisLines(boolean showAxisLines) {
		this.drawAxisLines = showAxisLines;
		return this;
	}
	
	public void addTraceModel(PlotTraceModel traceModel) {
		this.traceModels.add(traceModel);
		traceModel.setParent(uuid);
	}
	
	public boolean removeTraceModel(PlotTraceModel traceModel) {
		return traceModels.remove(traceModel);
	}
	
	public boolean removeAllTraceModels(List<PlotTraceModel> traceModels) {
		return this.traceModels.removeAll(traceModels);
	}
	
	public void clearTraceModels() {
		traceModels.clear();
	}
	
	public void addOverlaidCanvasModel(PlotCanvasModel canvasModel) {
		canvasModel.setRowPosition(rowPosition);
		canvasModel.setColPosition(colPosition);
		this.overlaidCanvasModels.add(canvasModel);
	}
	
	/////////////
	// UTILITY //
	/////////////
	
	public PlotCanvasModel getBaseCanvasModelIfOverlaid() {
		if(parentWindowModelUUID != null) {
			PlotWindowModel parent = getParent();
			for(PlotCanvasModel siblingModel : parent.getCanvasModels()) {
				if(siblingModel.getOverlaidCanvasModels().contains(this)) {
					return siblingModel;
				}
			}
		}
		return null;
	}
	
	public boolean isOverlaid() {
		return getBaseCanvasModelIfOverlaid() != null;
	}
	
	public boolean isSingleTrace() {
		return traceModels.size() == 1;
	}
	
	/**
	 * Determine if a {@link PlotCanvasModel} contains 3D data.
	 * @return True if it contains 3D data.
	 */
	public boolean is3DCanvasModel() {
		boolean isThreeDimensional = true;
		if(getTraceModels().isEmpty()) {
			isThreeDimensional = false;				
		}
		for(PlotTraceModel traceModel : getTraceModels()) {
			isThreeDimensional = isThreeDimensional && traceModel.isThreeDimensional();					
		}
		return isThreeDimensional;
	}
	
	/**
	 * Distinct from {@link PlotCanvasModel#is3DCanvasModel()}, this
	 * method returns true if the three-dimensional data needs to be
	 * rendered in a 3D axis space to be properly displayed.  Not all
	 * three-dimensional datasets need to be rendered this way to be
	 * displayed (for instance, heatmaps).
	 * @return True if the plot will need to be rendered as 3D.
	 */
	public boolean is3DRenderedCanvasModel() {
		boolean isThreeDimensional = true;
		if(getTraceModels().isEmpty()) {
			isThreeDimensional = false;				
		}
		for(PlotTraceModel traceModel : getTraceModels()) {
			isThreeDimensional = isThreeDimensional && traceModel.isThreeDimensionalRendered();					
		}
		return isThreeDimensional;
	}
	
	/**
	 * @param type The {@link PlotType} to look for.
	 * @return Whether or not the canvas only contains the user-specified PlotType.
	 */
	public boolean canvasContainsOnlyOneType(PlotType type) {
		boolean found = false;
		for(PlotTraceModel traceModel : getTraceModels()) {
			if(traceModel.getPointType() != type) {
				found = true;
				break;
			}
		}
		return !found;
	}

	public PlotTraceModel findDerivativeLine(DerivativeLineType type) {
		for(PlotTraceModel traceModel : getTraceModels()) {
			if(traceModel.getDerivativeLineType() == type) {
				return traceModel;
			}
		}
		return null;
	}

	public PlotTraceModel findDerivativeLine(String mainReferenceTraceName, DerivativeLineType type) {
		for(PlotTraceModel traceModel : getTraceModels()) {
			if(traceModel.getName().equals(mainReferenceTraceName) &&
			   traceModel.getDerivativeLineType() == type) {
				return traceModel;
			}
		}
		return null;
	}	
	
	//////////////
	// OVERRIDE //
	//////////////
	
	@Override
	public boolean equals(Object other) {
		boolean equals = false;
		if(other instanceof PlotCanvasModel) {
			equals = true;
			PlotCanvasModel otherModel = (PlotCanvasModel) other;

			equals = equals && otherModel.getName().equals(getName());
			equals = equals && otherModel.getRowPosition() == getRowPosition();
			equals = equals && otherModel.getColPosition() == getColPosition();
			equals = equals && otherModel.getXAxisLabel().equals(getXAxisLabel());
			equals = equals && otherModel.getYAxisLabel().equals(getYAxisLabel());
			equals = equals && otherModel.getZAxisLabel().equals(getZAxisLabel());
			
			if(otherModel.getXAxisRGB() != null && getXAxisRGB() != null) {
				equals = equals && otherModel.getXAxisRGB().equals(getXAxisRGB());
			} else if(otherModel.getXAxisRGB() != null ^ getXAxisRGB() != null) {
				equals = false;
			}
			
			if(otherModel.getYAxisRGB() != null && getYAxisRGB() != null) {
				equals = equals && otherModel.getYAxisRGB().equals(getYAxisRGB());
			} else if(otherModel.getYAxisRGB() != null ^ getYAxisRGB() != null) {
				equals = false;
			}
			
			if(otherModel.getZAxisRGB() != null && getZAxisRGB() != null) {
				equals = equals && otherModel.getZAxisRGB().equals(getZAxisRGB());
			} else if(otherModel.getZAxisRGB() != null ^ getZAxisRGB() != null) {
				equals = false;
			}
			
			equals = equals && otherModel.getXAxisRangeStart() == getXAxisRangeStart();
			equals = equals && otherModel.getYAxisRangeStart() == getYAxisRangeStart();
			equals = equals && otherModel.getZAxisRangeStart() == getZAxisRangeStart();
			equals = equals && otherModel.getXAxisRangeEnd() == getXAxisRangeEnd();
			equals = equals && otherModel.getYAxisRangeEnd() == getYAxisRangeEnd();
			equals = equals && otherModel.getZAxisRangeEnd() == getZAxisRangeEnd();
			equals = equals && otherModel.getXLogScale() == getXLogScale();
			equals = equals && otherModel.getYLogScale() == getYLogScale();
			equals = equals && otherModel.getZLogScale() == getZLogScale();
			
			equals = equals && otherModel.getAxisPrecision() == getAxisPrecision();
			equals = equals && otherModel.getDrawAxisLines() == getDrawAxisLines();
			equals = equals && otherModel.getDrawGridLines() == getDrawGridLines();

			equals = equals && otherModel.getTraceModels().equals(getTraceModels());
		}
		return equals;
	}
	
	@Override
	public int hashCode() {
		int hashCode = 1;
		
		hashCode = hashCode + getName().hashCode();
		hashCode = hashCode + rowPosition;
		hashCode = hashCode + colPosition;
		if(StringUtils.isNotBlank(xAxisLabel)) {
			hashCode = hashCode + xAxisLabel.hashCode();
		}
		if(StringUtils.isNotBlank(yAxisLabel)) {
			hashCode = hashCode + yAxisLabel.hashCode();
		}
		if(StringUtils.isNotBlank(zAxisLabel)) {
			hashCode = hashCode + zAxisLabel.hashCode();
		}
		if(xAxisRGB != null) {
			hashCode = hashCode + xAxisRGB.hashCode();
		}
		if(yAxisRGB != null) {
			hashCode = hashCode + yAxisRGB.hashCode();
		}
		if(zAxisRGB != null) {
			hashCode = hashCode + zAxisRGB.hashCode();
		}
		hashCode = hashCode + (xAxisRangeStart == null ? 1 : xAxisRangeStart.hashCode());
		hashCode = hashCode + (xAxisRangeEnd   == null ? 2 : xAxisRangeEnd.hashCode());
		hashCode = hashCode + (yAxisRangeStart == null ? 3 : yAxisRangeStart.hashCode());
		hashCode = hashCode + (yAxisRangeEnd   == null ? 4 : yAxisRangeEnd.hashCode());
		hashCode = hashCode + (zAxisRangeStart == null ? 5 : zAxisRangeStart.hashCode());
		hashCode = hashCode + (zAxisRangeEnd   == null ? 6 : zAxisRangeEnd.hashCode());
		hashCode = hashCode + Boolean.valueOf(autoscale).hashCode();
		hashCode = hashCode + Boolean.valueOf(xLogScale).hashCode();
		hashCode = hashCode + Boolean.valueOf(yLogScale).hashCode();
		hashCode = hashCode + Boolean.valueOf(zLogScale).hashCode();
		hashCode = hashCode + Integer.valueOf(axisPrecision).hashCode();
		hashCode = hashCode + Boolean.valueOf(drawGridLines).hashCode();
		hashCode = hashCode + Boolean.valueOf(drawAxisLines).hashCode();
		
		for(PlotTraceModel traceModel : traceModels) {
			int index = traceModels.indexOf(traceModel);
			hashCode = hashCode + ((index + 1) * traceModel.hashCode());
		}
		
		return hashCode;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PlotCanvasModel: [").append(getDescriptiveAxesName());
		sb.append("]");
		return sb.toString();
	}
}

