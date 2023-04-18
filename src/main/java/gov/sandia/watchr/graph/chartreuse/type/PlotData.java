/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.chartreuse.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

/**
 * A PlotData object is a general structure for storing data to be graphically
 * plotted. The inner data structure that stores the actual plot values is a
 * two-dimensional array of {@link Double} values. A PlotData object can also
 * contain up to two arrays of header labels, stored in {@link Pair} objects of
 * type {@link NodeType}, {@link String}. <br>
 * <br>
 * A PlotData object is said to be "one-dimensional" if it only contains one
 * array of header labels. Even though the underlying data structure is a 2D
 * grid of double values, those values can only be referenced using a
 * one-dimensional list of header values; another way to think of it is that the
 * data that can be accessed in the grid using one header value will be a
 * one-dimensional list of double values. Also, keep in mind that even though
 * the PlotData object is said to be "one-dimensional," this has no correlation
 * on the dimensionality of the data being stored in the 2D grid. You can easily
 * represent N-dimensional data in a 2D grid, if each dimension corresponds to
 * one row of the grid. <br>
 * <br>
 * A PlotData object is said to be "two-dimensional" if both arrays of header
 * labels are filled out. A data point in the 2D grid of double values can be
 * accessed using the indices of any two of these header values. <br>
 * <br>
 * PlotData objects are immutable by design. All data must be prepared to insert
 * at the time of object creation.
 * 
 * @author Elliott Ridgway
 *
 */
public class PlotData {
	
	////////////
	// FIELDS //
	////////////
	
	private final String name;
	private final String description;
	private final Double[][] data;
	private final Pair<NodeType, String>[] xHeaders;
	private final Pair<NodeType, String>[] yHeaders;
	
	/////////////////
	// CONSTRUCTOR //
	/////////////////
	
	@SuppressWarnings("unchecked")
	public PlotData(String name, String description, Double[][] data, Pair<NodeType, String>[] xHeaders, Pair<NodeType, String>[] yHeaders) {
		this.name = name;
		this.description = description;
		
		this.data     = data != null     ? Arrays.copyOf(data, data.length)         : new Double[0][0];
		this.xHeaders = xHeaders != null ? Arrays.copyOf(xHeaders, xHeaders.length) : new Pair[0];
		this.yHeaders = yHeaders != null ? Arrays.copyOf(yHeaders, yHeaders.length) : new Pair[0];
	}
	
	/////////////
	// GETTERS //
	/////////////
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Double[][] getData() {
		return Arrays.copyOf(data, data.length);
	}
	
	public Pair<NodeType, String>[] getXHeaders() {
		return Arrays.copyOf(xHeaders, xHeaders.length);
	}
	
	public Pair<NodeType, String>[] getYHeaders() {
		return Arrays.copyOf(yHeaders, yHeaders.length);
	}
	
	////////////////////////
	// GETTERS (COMPUTED) //
	////////////////////////
	
	public List<Pair<NodeType, String>> getXVariables() {
		List<Pair<NodeType, String>> variables = new ArrayList<>();
		for(int i = 0; i < xHeaders.length; i++) {
			if(xHeaders[i].getLeft() == NodeType.VARIABLE) {
				variables.add(xHeaders[i]);
			}
		}
		return variables;
	}
	
	public List<String> getXVariableNames() {
		List<String> variables = new ArrayList<>();
		for(int i = 0; i < xHeaders.length; i++) {
			if(xHeaders[i].getLeft() == NodeType.VARIABLE) {
				variables.add(xHeaders[i].getRight());
			}
		}
		return variables;
	}
	
	public List<Pair<NodeType, String>> getXResponses() {
		List<Pair<NodeType, String>> responses = new ArrayList<>();
		for(int i = 0; i < xHeaders.length; i++) {
			if(xHeaders[i].getLeft() == NodeType.RESPONSE) {
				responses.add(xHeaders[i]);
			}
		}
		return responses;
	}
	
	public List<String> getXResponseNames() {
		List<String> responses = new ArrayList<>();
		for(int i = 0; i < xHeaders.length; i++) {
			if(xHeaders[i].getLeft() == NodeType.RESPONSE) {
				responses.add(xHeaders[i].getRight());
			}
		}
		return responses;
	}
	
	public List<Pair<NodeType, String>> getYVariables() {
		List<Pair<NodeType, String>> variables = new ArrayList<>();
		for(int i = 0; i < yHeaders.length; i++) {
			if(yHeaders[i].getLeft() == NodeType.VARIABLE) {
				variables.add(yHeaders[i]);
			}
		}
		return variables;
	}
	
	public List<String> getXHeaderNames() {
		List<String> xHeaderNames = new ArrayList<>();
		for(int i = 0; i < xHeaders.length; i++) {
			xHeaderNames.add(xHeaders[i].getRight());
		}
		return xHeaderNames;
	}
	
	public List<String> getYVariableNames() {
		List<String> variables = new ArrayList<>();
		for(int i = 0; i < yHeaders.length; i++) {
			if(yHeaders[i].getLeft() == NodeType.VARIABLE) {
				variables.add(yHeaders[i].getRight());
			}
		}
		return variables;
	}
	
	public List<Pair<NodeType, String>> getYResponses() {
		List<Pair<NodeType, String>> responses = new ArrayList<>();
		for(int i = 0; i < yHeaders.length; i++) {
			if(yHeaders[i].getLeft() == NodeType.RESPONSE) {
				responses.add(yHeaders[i]);
			}
		}
		return responses;
	}
	
	public List<String> getYResponseNames() {
		List<String> responses = new ArrayList<>();
		for(int i = 0; i < yHeaders.length; i++) {
			if(yHeaders[i].getLeft() == NodeType.RESPONSE) {
				responses.add(yHeaders[i].getRight());
			}
		}
		return responses;
	}
	
	public List<String> getYHeaderNames() {
		List<String> yHeaderNames = new ArrayList<>();
		for(int i = 0; i < yHeaders.length; i++) {
			yHeaderNames.add(yHeaders[i].getRight());
		}
		return yHeaderNames;
	}
	
	public LinkedHashSet<String> getVariableNames() {
		LinkedHashSet<String> variables = new LinkedHashSet<>();
		variables.addAll(getXVariableNames());
		variables.addAll(getYVariableNames());
		return variables;
	}
	
	public LinkedHashSet<Pair<NodeType, String>> getVariables() {
		LinkedHashSet<Pair<NodeType, String>> variables = new LinkedHashSet<>();
		variables.addAll(getXVariables());
		variables.addAll(getYVariables());
		return variables;
	}
	
	public LinkedHashSet<String> getResponseNames() {
		LinkedHashSet<String> responses = new LinkedHashSet<>();
		responses.addAll(getXResponseNames());
		responses.addAll(getYResponseNames());
		return responses;
	}
	
	public LinkedHashSet<Pair<NodeType, String>> getResponses() {
		LinkedHashSet<Pair<NodeType, String>> responses = new LinkedHashSet<>();
		responses.addAll(getXResponses());
		responses.addAll(getYResponses());
		return responses;
	}
	
	public List<Pair<NodeType, String>> getAllHeadersList() {
		List<Pair<NodeType, String>> headers = new ArrayList<>();
		headers.addAll(Arrays.asList(getXHeaders()));
		headers.addAll(Arrays.asList(getYHeaders()));
		return headers;
	}
	
	public Set<Pair<NodeType, String>> getAllHeadersSet() {
		// Use this method to eliminate duplicate headers.
		Set<Pair<NodeType, String>> headers = new LinkedHashSet<>();
		headers.addAll(Arrays.asList(getXHeaders()));
		headers.addAll(Arrays.asList(getYHeaders()));
		return headers;
	}
	
	public int getIndexOfXHeader(String header) {
		for(int i = 0; i < xHeaders.length; i++) {
			if(xHeaders[i].getRight().equals(header)) {
				return i;
			}
		}
		return -1;
	}
	
	public int getIndexOfYHeader(String header) {
		for(int i = 0; i < yHeaders.length; i++) {
			if(yHeaders[i].getRight().equals(header)) {
				return i;
			}
		}
		return -1;
	}
	
	public Double[] getColumn(int colIndex) {
		Double[] col = new Double[data.length];
		for(int row = 0; row < data.length; row ++) {
			Double dataValue = data[row][colIndex];
			col[row] = dataValue;
		}
		return col;
	}
	
	public LinkedHashMap<Pair<NodeType, String>, Double> getColumnWithHeaders(int rowIndex) {
		LinkedHashMap<Pair<NodeType, String>, Double> columnWithHeaders = new LinkedHashMap<>();
		for(int row = 0; row < data.length; row ++) {
			Pair<NodeType, String> header = getXHeaders()[row];
			Double dataValue = data[row][rowIndex];
			columnWithHeaders.put(header, dataValue);
		}
		return columnWithHeaders;
	}
	
	public Double[] getRow(int index) {
		return data[index];
	}
	
	public Double getDataAt(String xHeader, String yHeader) {
		int xHeaderIndex = getIndexOfXHeader(xHeader);
		int yHeaderIndex = getIndexOfYHeader(yHeader);
		return data[xHeaderIndex][yHeaderIndex];
	}
	
	/////////////
	// UTILITY //
	/////////////
	
	public NodeType checkVariableDataType(String label) {
		for(String variableName : getVariableNames()) {
			if(variableName.equals(label)) {
				return NodeType.VARIABLE;
			}
		}
		for(String responseName : getResponseNames()) {
			if(responseName.equals(label)) {
				return NodeType.RESPONSE;
			}
		}
		return null;
	}
}

