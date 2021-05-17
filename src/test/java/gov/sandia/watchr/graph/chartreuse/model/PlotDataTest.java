package gov.sandia.watchr.graph.chartreuse.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import gov.sandia.watchr.graph.chartreuse.ChartreuseTestsUtil;
import gov.sandia.watchr.graph.chartreuse.type.NodeType;
import gov.sandia.watchr.graph.chartreuse.type.PlotData;

public class PlotDataTest {

	@Test
	public void testConstructor_NullData() {
		PlotData pd = new PlotData(null, null, null, null, null);
		assertNotNull(pd);
		
		assertNull(pd.getName());
		assertNull(pd.getDescription());
		assertNotNull(pd.getData());
		assertNotNull(pd.getXHeaders());
		assertNotNull(pd.getYHeaders());
		
		assertEquals(0, pd.getData().length);
		assertEquals(0, pd.getXHeaders().length);
		assertEquals(0, pd.getYHeaders().length);
	}
	
	@Test
	public void testGetVariableNames() {
		PlotData pd = ChartreuseTestsUtil.generatePlotDataJunkData(true, 3, 3, 'A', 'A');
		Set<String> variableNames = pd.getVariableNames();
		assertTrue(variableNames.contains("A"));
		assertTrue(variableNames.contains("B"));
		assertTrue(variableNames.contains("C"));
		assertEquals(3, variableNames.size());
	}
	
	@Test
	public void testGetResponseNames() {
		PlotData pd = ChartreuseTestsUtil.generatePlotDataJunkData(false, 3, 3, 'A', 'A');
		Set<String> responseNames = pd.getResponseNames();
		assertTrue(responseNames.contains("A"));
		assertTrue(responseNames.contains("B"));
		assertTrue(responseNames.contains("C"));
		assertEquals(3, responseNames.size());
	}

	@Test
	public void testCheckVariableDataType() {
		PlotData pd = ChartreuseTestsUtil.generatePlotDataJunkData(false, 3, 3, 'A', 'D');
		assertEquals(NodeType.VARIABLE, pd.checkVariableDataType("A"));
		assertEquals(NodeType.VARIABLE, pd.checkVariableDataType("B"));
		assertEquals(NodeType.VARIABLE, pd.checkVariableDataType("C"));
		assertEquals(NodeType.RESPONSE, pd.checkVariableDataType("D"));
		assertEquals(NodeType.RESPONSE, pd.checkVariableDataType("E"));
		assertEquals(NodeType.RESPONSE, pd.checkVariableDataType("F"));
	}

	@Test
	public void testGetColumnWithHeaders() {
		PlotData pd = ChartreuseTestsUtil.generatePlotDataJunkData(false, 3, 3, 'A', 'D');
		LinkedHashMap<Pair<NodeType, String>, Double> returnedMap = pd.getColumnWithHeaders(0);
		Double value = returnedMap.get(new ImmutablePair<NodeType, String>(NodeType.VARIABLE, "A"));
		assertEquals(0.0, value, 1.0e-4);
	}

	@Test
	public void testGetXVariables() {
		PlotData pd = ChartreuseTestsUtil.generatePlotDataJunkData(false, 3, 3, 'A', 'D');
		List<Pair<NodeType, String>> returnedList = pd.getXVariables();
		assertEquals(NodeType.VARIABLE, returnedList.get(0).getLeft());
		assertEquals(NodeType.VARIABLE, returnedList.get(1).getLeft());
		assertEquals(NodeType.VARIABLE, returnedList.get(2).getLeft());
		assertEquals("A", returnedList.get(0).getRight());
		assertEquals("B", returnedList.get(1).getRight());
		assertEquals("C", returnedList.get(2).getRight());
	}

	@Test
	public void testGetXResponses() {
		PlotData pd = ChartreuseTestsUtil.generatePlotDataJunkData(false, 3, 3, 'A', 'D');
		List<Pair<NodeType, String>> returnedList = pd.getXResponses();
		assertEquals(0, returnedList.size());
	}

	@Test
	public void testGetYResponses() {
		PlotData pd = ChartreuseTestsUtil.generatePlotDataJunkData(false, 3, 3, 'A', 'D');
		List<Pair<NodeType, String>> returnedList = pd.getYResponses();
		assertEquals(NodeType.RESPONSE, returnedList.get(0).getLeft());
		assertEquals(NodeType.RESPONSE, returnedList.get(1).getLeft());
		assertEquals(NodeType.RESPONSE, returnedList.get(2).getLeft());
		assertEquals("D", returnedList.get(0).getRight());
		assertEquals("E", returnedList.get(1).getRight());
		assertEquals("F", returnedList.get(2).getRight());
	}

	@Test
	public void testGetYVariables() {
		PlotData pd = ChartreuseTestsUtil.generatePlotDataJunkData(false, 3, 3, 'A', 'D');
		List<Pair<NodeType, String>> returnedList = pd.getYVariables();
		assertEquals(0, returnedList.size());
	}	
}

