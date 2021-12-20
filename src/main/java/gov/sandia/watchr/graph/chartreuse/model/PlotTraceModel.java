/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.chartreuse.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import gov.sandia.watchr.config.derivative.DerivativeLineType;
import gov.sandia.watchr.graph.chartreuse.CommonPlotTerms;
import gov.sandia.watchr.graph.chartreuse.Dimension;
import gov.sandia.watchr.graph.chartreuse.PlotToken;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.util.ListUtil;
import gov.sandia.watchr.util.RGB;
import gov.sandia.watchr.util.RgbUtil;
import gov.sandia.watchr.util.StringUtil;

/**
 * A plot trace model is the most basic grouping of information that can be visualized in Chartreuse.
 * A trace represents a single "variable" (either parameter or response) visually rendered in some way.<br><br>
 * To give a basic example, a trace could be a series of points plotted on a Cartesian plane (i.e. a scatter plot).
 * A trace can also be represented in other ways that we don�t naturally think of as "tracing" (such as a histogram).
 * 
 * @author Elliott Ridgway
 *
 */
public class PlotTraceModel {
	
	////////////
	// FIELDS //
	////////////
		
	private static final String VERSION = "3.0"; // Used for backwards compatibility. //$NON-NLS-1$

	private String name;
	private DerivativeLineType derivativeLineType = null;

	protected Set<PlotTracePoint> points;
	private Map<PlotToken, String> properties;
	
	private List<RGB> rgbs;
	private List<Double> colorScaleAnchors;
	private String colorScaleType;
	
	private Dimension trimNoDelta = Dimension.NONE;
	private Dimension relativeAxis = Dimension.NONE;

	private UUID uuid;

	private final List<PlotTraceChangeListener> listeners;

	////////////
	// PARENT //
	////////////
	
	private UUID parentCanvasModelUUID;

	////////////////////////////
	// WATCHR-SPECIFIC FIELDS //
	////////////////////////////

	protected Set<PlotTracePoint> filterValues;
	
	/////////////////
	// CONSTRUCTOR //
	/////////////////	

	public PlotTraceModel() {
		this(null);
	}

	public PlotTraceModel(UUID parentCanvasModelUUID) {
		this(parentCanvasModelUUID, true);
	}
	
	public PlotTraceModel(UUID parentCanvasModelUUID, boolean shouldSetUUID) {
		this.parentCanvasModelUUID = parentCanvasModelUUID;
		if(shouldSetUUID) {
			this.uuid = UUID.randomUUID();
		}

		if(parentCanvasModelUUID != null) {
			PlotCanvasModel parent = getParent();
			parent.addTraceModel(this);
		}
		
		properties = new HashMap<>();
		properties.put(PlotToken.TRACE_BOUND_LOWER, Integer.toString(Integer.MIN_VALUE));
		properties.put(PlotToken.TRACE_BOUND_UPPER, Integer.toString(Integer.MAX_VALUE));
		properties.put(PlotToken.TRACE_PRECISION, Integer.toString(-1));
		properties.put(PlotToken.TRACE_DRAW_NUMBER_LABELS, Boolean.TRUE.toString());
		properties.put(PlotToken.TRACE_POINT_MODE, "Circle");
		properties.put(PlotToken.TRACE_ORIENTATION, CommonPlotTerms.ORIENTATION_HORIZONTAL.getLabel());
		
		points = new LinkedHashSet<>();
		filterValues = new LinkedHashSet<>();
		
		rgbs = new ArrayList<>();
		colorScaleAnchors = new ArrayList<>();
		setPrimaryRGB(RgbUtil.blackRGB());
		
		colorScaleType = CommonPlotTerms.SCALE_CONTINUOUS.getLabel();

		this.listeners = new ArrayList<>();
	}

	public PlotTraceModel(UUID parentCanvasModelUUID, PlotTraceModel copy) {
		this(parentCanvasModelUUID, copy.getUUID() != null);
		
		this.setName(copy.getName());
        this.setTrimNoDelta(copy.getTrimNoDelta());
        this.setRelativeAxis(copy.getRelativeAxis());
        
		for(PlotTracePoint point : copy.getPoints()) {
        	this.points.add(new PlotTracePoint(point));
		}

		this.rgbs.clear();
		for(RGB rgb : copy.getRGBs()) {
        	this.rgbs.add(new RGB(rgb));
		}

		this.colorScaleAnchors.clear();
		for(Double colorScaleAnchor : copy.getColorScaleAnchors()) {
        	this.colorScaleAnchors.add(colorScaleAnchor);
		}
		this.setColorScaleType(copy.getColorScaleType());
		
		getProperties().clear();
        getProperties().putAll(copy.getProperties());

        this.filterValues.addAll(copy.getFilterValues());
    }

	/////////////
	// GETTERS //
	/////////////
	
	public PlotCanvasModel getParent() {
		return PlotRelationshipManager.getCanvasModel(parentCanvasModelUUID);
	}

	public UUID getParentUUID() {
		return parentCanvasModelUUID;
	}	
	
	public String getVersion() {
		return VERSION;
	}

	public String getName() {
		return StringUtils.isBlank(name) ? "" : name; //$NON-NLS-1$
	}

	public UUID getUUID() {
		return uuid;
	}
	
	public String get(PlotToken property) {
		return properties.get(property);
	}

	public List<PlotTracePoint> getPoints() {
		return getPoints(null);
	}

	public Set<PlotTracePoint> getFilterValues() {
		return Collections.unmodifiableSet(filterValues);
	}
	
	public List<RGB> getRGBs() {
		return rgbs;
	}
	
	public List<Double> getColorScaleAnchors() {
		return colorScaleAnchors;
	}
	
	public String getColorScaleType() {
		return colorScaleType;
	}
	
	public Dimension getTrimNoDelta() {
		return trimNoDelta;
	}
	
	public Dimension getRelativeAxis() {
		return relativeAxis;
	}
	
	public Map<PlotToken, String> getProperties() {
		return properties;
	}

	public DerivativeLineType getDerivativeLineType() {
		return derivativeLineType;
	}

	////////////////////////
	// GETTERS (COMPUTED) //
	////////////////////////

	@SuppressWarnings("unchecked")
	public List<PlotTracePoint> getPoints(PlotTraceOptions options) {
		List<PlotTracePoint> returnPoints = new ArrayList<>(points);
		if(options != null && options.sortAlongDimension != null) {
			Dimension dim = options.sortAlongDimension;
			if(dim == Dimension.X) {
				returnPoints.sort((PlotTracePoint p1, PlotTracePoint p2) -> {
					boolean numberComparison = NumberUtils.isCreatable(p1.x) && NumberUtils.isCreatable(p2.x);
					if(numberComparison) {
						Double n1 = Double.parseDouble(p1.x);
						Double n2 = Double.parseDouble(p2.x);
						return n1.compareTo(n2);
					} else {
						return p1.x.compareTo(p2.x);
					}
				});
			} else if(dim == Dimension.Y) {
				returnPoints.sort((PlotTracePoint p1, PlotTracePoint p2) -> {
					boolean numberComparison = NumberUtils.isCreatable(p1.y) && NumberUtils.isCreatable(p2.y);
					if(numberComparison) {
						Double n1 = Double.parseDouble(p1.y);
						Double n2 = Double.parseDouble(p2.y);
						return n1.compareTo(n2);
					} else {
						return p1.y.compareTo(p2.y);
					}
				});
			} else if(dim == Dimension.Z) {
				returnPoints.sort((PlotTracePoint p1, PlotTracePoint p2) -> {
					boolean numberComparison = NumberUtils.isCreatable(p1.z) && NumberUtils.isCreatable(p2.z);
					if(numberComparison) {
						Double n1 = Double.parseDouble(p1.z);
						Double n2 = Double.parseDouble(p2.z);
						return n1.compareTo(n2);
					} else {
						return p1.z.compareTo(p2.z);
					}
				});
			}
		}

		List<PlotTracePoint> filteredReturnPoints = new ArrayList<>();
		if(options != null && options.filterPoints) {
			for(PlotTracePoint point : returnPoints) {
				if(!isPointFiltered(point)) {
					filteredReturnPoints.add(point);
				}
			}
		} else {
			filteredReturnPoints.addAll(returnPoints);
		}

		List<PlotTracePoint> rangeReturnPoints = new ArrayList<>();
		if(options != null && options.displayRange > 0) {
			int start = filteredReturnPoints.size() - options.displayRange;
			int end = filteredReturnPoints.size();
			if(start < 0) {
				start = 0;
			}
			rangeReturnPoints = (List<PlotTracePoint>) ListUtil.truncate(filteredReturnPoints, start, end);
		} else {
			rangeReturnPoints.addAll(filteredReturnPoints);
		}

		return rangeReturnPoints;
	}

	public Set<String> getDimensionValues(Dimension dim) {
		Set<String> values = new HashSet<>();
		for(PlotTracePoint point : points) {
			if(dim == Dimension.X) values.add(point.x);
			else if(dim == Dimension.Y) values.add(point.y);
			else if(dim == Dimension.Z) values.add(point.z);
		}
		return values;
	}

	public PlotTracePoint findPoint(String xMatch, String yMatch) {
		for(PlotTracePoint point : points) {
			if(point.x.matches(xMatch) && point.y.matches(yMatch)) {
				return point;
			}
		}
		return null;
	}

	public boolean isPointFiltered(PlotTracePoint point) {
		for(PlotTracePoint filterValue : filterValues) {
			boolean notAllBlank = StringUtils.isNotBlank(filterValue.x);
			notAllBlank = notAllBlank || StringUtils.isNotBlank(filterValue.y);
			notAllBlank = notAllBlank || StringUtils.isNotBlank(filterValue.z);

			String filterValueXRegex = StringUtil.convertToRegex(filterValue.x);
			String filterValueYRegex = StringUtil.convertToRegex(filterValue.y);
			String filterValueZRegex = StringUtil.convertToRegex(filterValue.z);;

			if((point.x.matches(filterValueXRegex) || StringUtils.isBlank(filterValueXRegex)) &&
			   (point.y.matches(filterValueYRegex) || StringUtils.isBlank(filterValueYRegex)) &&
			   (point.z.matches(filterValueZRegex) || StringUtils.isBlank(filterValueZRegex)) &&
			   notAllBlank) {
				return true;
			}
		}
		return false;
	}
	
	public int getPropertyAsInt(PlotToken property) {
		return Integer.parseInt(properties.get(property));
	}
	
	public boolean getPropertyAsBoolean(PlotToken property) {
		return Boolean.parseBoolean(properties.get(property));
	}
	
	public PlotType getPointType() {
		String pointType = properties.get(PlotToken.TRACE_POINT_TYPE);
		if(StringUtils.isNotBlank(pointType)) {
			return PlotType.valueOf(pointType);
		}
		return null;
	}
	
	public RGB getPrimaryColor() {
		return !rgbs.isEmpty() ? rgbs.get(0) : null;
	}

	public boolean containsPoint(String xValue, String yValue) {
		for(PlotTracePoint point : points) {
			if(point.x.equals(xValue) && point.y.equals(yValue)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasMetadata() {
		for(PlotTracePoint point : points) {
			if(!point.metadata.isEmpty()) {
				return true;
			}
		}
		return false;
	}
	
	/////////////
	// SETTERS //
	/////////////
	
	protected void setParent(UUID parentCanvasModelUUID) {
		this.parentCanvasModelUUID = parentCanvasModelUUID;
	}

	public PlotTraceModel setName(String name) {
		this.name = name;
		return this;
	}
	
	public PlotTraceModel set(PlotToken property, String value) {
		properties.put(property, value);
		return this;
	}
	
	public PlotTraceModel set(PlotToken property, int value) {
		properties.put(property, Integer.toString(value));
		return this;
	}
	
	public PlotTraceModel set(PlotToken property, boolean value) {
		properties.put(property, Boolean.toString(value));
		return this;
	}
	
	public PlotTraceModel set(PlotToken property, PlotType value) {
		properties.put(property, value.toString());
		return this;
	}
	
	public PlotTraceModel setColors(List<RGB> newColors) {
		rgbs.clear();
		rgbs.addAll(newColors);
		return this;
	}
	
	public PlotTraceModel setColorScaleAnchors(List<Double> newColorScaleAnchors) {
		colorScaleAnchors.clear();
		colorScaleAnchors.addAll(newColorScaleAnchors);
		return this;
	}
	
	public PlotTraceModel setColorScaleType(String colorScaleType) {
		this.colorScaleType = colorScaleType;
		return this;
	}

	public PlotTraceModel setTrimNoDelta(Dimension trimNoDelta) {
		this.trimNoDelta = trimNoDelta;
		return this;
	}
	
	public PlotTraceModel setRelativeAxis(Dimension relativeAxis) {
		this.relativeAxis = relativeAxis;
		return this;
	}

	public PlotTraceModel setDerivativeLineType(DerivativeLineType derivativeLineType) {
		this.derivativeLineType = derivativeLineType;
		return this;
	}
	
	public PlotTraceModel setPoints(Collection<PlotTracePoint> points) {
		this.points.clear();
		this.points.addAll(points);
		return this;
	}

	public PlotTraceModel addFilterValue(PlotTracePoint point) {
		this.filterValues.add(point);
		return this;
	}	

	public PlotTraceModel setFilterValues(List<PlotTracePoint> points) {
		this.filterValues.clear();
		this.filterValues.addAll(points);
		return this;
	}

	public PlotTraceModel applyFilterValues(List<PlotTracePoint> points) {
		this.filterValues.addAll(points);
		return this;
	}
	
	public PlotTraceModel setPrimaryRGB(RGB rgb) {
		if(rgbs.isEmpty()) {
			rgbs.add(rgb);
			colorScaleAnchors.add(0.0);
		} else {
			rgbs.set(0, rgb);
			colorScaleAnchors.add(0, 0.0);
		}
		return this;
	}

	public void add(PlotTracePoint point) {
		points.add(point);
	}

	public void addAll(List<PlotTracePoint> points) {
		this.points.addAll(points);
	}

	public void clear() {
		points.clear();
	}
	
	/////////////
	// UTILITY //
	/////////////
	
	/**
	 * @return True if there is data stored in the X, Y, and Z
	 * dimensions.
	 */
	public boolean isThreeDimensional() {
		for(PlotTracePoint point : points) {
			if(StringUtils.isNotBlank(point.z)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Distinct from {@link PlotTraceModel#isThreeDimensional()}, this
	 * method returns true if the three-dimensional data needs to be
	 * rendered in a 3D axis space to be properly displayed.  Not all
	 * three-dimensional datasets need to be rendered this way to be
	 * displayed (for instance, heatmaps).
	 * @return True if the plot will need to be rendered as 3D.
	 */
	public boolean isThreeDimensionalRendered() {
		boolean is3D = false;
		PlotType pointType = getPointType();
		
		is3D = pointType == PlotType.SCATTER_3D_PLOT;
		is3D = is3D || pointType == PlotType.SURFACE_3D_PLOT;
		return is3D;
	}
	
	/**
	 * @return True if the list of color scale anchors are not sorted from
	 * smallest to largest.
	 */
	public boolean areColorAnchorValuesOutOfOrder() {
		for(int i = 1; i < colorScaleAnchors.size(); i++) {
			Double upperNumber = colorScaleAnchors.get(i);
			Double lowerNumber = colorScaleAnchors.get(i-1);
			if(upperNumber < lowerNumber) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param lowerRange The lower range to check for.
	 * @param upperRange The upper range to check for.
	 * @return True if any values in the color scale anchor list are outside the
	 * specified range.
	 */
	public boolean areColorAnchorValuesOutsideRange(double lowerRange, double upperRange) {
		boolean outsideRange = false;
		
		for(int i = 0; i < colorScaleAnchors.size(); i++) {
			Double colorScaleAnchor = colorScaleAnchors.get(i);
			outsideRange = colorScaleAnchor > upperRange;
			outsideRange = outsideRange || colorScaleAnchor < lowerRange;
			if(outsideRange) {
				break;
			}
		}
		return outsideRange;
	}
	
	/**
	 * 
	 * @return True if the color scale anchor list contains one value.
	 */
	public boolean isOnlyOneColorAnchorValue() {
		return colorScaleAnchors.size() == 1;
	}

	public boolean isEmpty2D() {
		boolean empty = points.isEmpty();
		boolean allZeroes = true;
		for(PlotTracePoint point : points) {
			allZeroes = allZeroes && StringUtils.isBlank(point.y);
		}
		empty = empty || allZeroes;
		return empty;
	}

	///////////////
	// LISTENERS //
	///////////////

	public void addListener(PlotTraceChangeListener listener) {
		listeners.add(listener);
	}

	public void fireChangeListeners() {
		for(PlotTraceChangeListener listener : listeners) {
			listener.changed();
		}
	}

	public void firePropertyChangeListeners(PlotToken property) {
		for(PlotTraceChangeListener listener : listeners) {
			listener.propertyChanged(property);
		}
	}
	
	//////////////
	// OVERRIDE //
	//////////////
	
	@Override
	public boolean equals(Object other) {
		boolean equals = false;
		if(other == null) {
            return false;
        } else if(other == this) {
            return true;
        } else if(getClass() != other.getClass()) {
            return false;
        } else {
			PlotTraceModel otherModel = (PlotTraceModel) other;
			equals = otherModel.getProperties().keySet().size() == getProperties().keySet().size();
			equals = equals && otherModel.getProperties().keySet().containsAll(getProperties().keySet());
			equals = equals && otherModel.getProperties().values().containsAll(getProperties().values());
			equals = equals && otherModel.getName().equals(getName());
			equals = equals && otherModel.getPoints().equals(getPoints());
			equals = equals && otherModel.getFilterValues().equals(getFilterValues());
			equals = equals && otherModel.getPointType() == getPointType();
			equals = equals && otherModel.getTrimNoDelta().equals(getTrimNoDelta());
			equals = equals && otherModel.getRelativeAxis().equals(getRelativeAxis());
			equals = equals && otherModel.getRGBs().equals(getRGBs());
			equals = equals && otherModel.getColorScaleAnchors().equals(getColorScaleAnchors());
			equals = equals && otherModel.getColorScaleType().equals(getColorScaleType());
		}
		return equals;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PlotTraceModel: [");
		sb.append(getName());
		sb.append("]");
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		int hashCode = 1;
		
		if(StringUtils.isNotBlank(name)) {
			hashCode = hashCode + name.hashCode();
		}
		
		hashCode = hashCode + properties.hashCode();
		
		hashCode = hashCode + points.hashCode();
		
		hashCode = hashCode + rgbs.hashCode();
		hashCode = hashCode + colorScaleAnchors.hashCode();
		hashCode = hashCode + colorScaleType.hashCode();
		
		hashCode = hashCode + trimNoDelta.hashCode();
		hashCode = hashCode + relativeAxis.hashCode();
		
		return hashCode;
	}
}

