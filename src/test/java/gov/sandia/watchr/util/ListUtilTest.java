package gov.sandia.watchr.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ListUtilTest {

	@Test
	public void testIsListOf() {
		List<String> values = new ArrayList<>();
		values.add("1.0");
		values.add("2.0");
		values.add("3.0");
		values.add("4.0");
		values.add("5.0");
		values.add("6.0");
		
		assertTrue(ListUtil.isListOf(values, String.class));
	}
	
	@Test
	public void testIsListOfWithWrongObject() {
		String value = "1.0";
		assertFalse(ListUtil.isListOf(value, String.class));
	}
	
	@Test
	public void testEveryElementInListIsOfType() {
		List<String> values = new ArrayList<>();
		values.add("1.0");
		values.add("2.0");
		values.add("3.0");
		values.add("4.0");
		values.add("5.0");
		values.add("6.0");
		
		assertTrue(ListUtil.everyElementInListIsOfType(values, String.class));
	}
	
	@Test
	public void testEveryElementInListIsOfTypeWithBadList() {
		List<Object> values = new ArrayList<>();
		values.add("1.0");
		values.add("2.0");
		values.add("3.0");
		values.add("4.0");
		values.add(5.0);
		values.add("6.0");
		
		assertFalse(ListUtil.everyElementInListIsOfType(values, String.class));
	}
	
	@Test
	public void testIsLastElement() {
		List<String> values = new ArrayList<>();
		values.add("1.0");
		values.add("2.0");
		values.add("3.0");
		values.add("4.0");
		values.add("5.0");
		values.add("6.0");
		
		assertTrue(ListUtil.isLastElement(values, "6.0"));
		assertFalse(ListUtil.isLastElement(values, "5.0"));
	}
	
	@Test
	public void testFilterList() {
		List<String> values = new ArrayList<>();
		values.add("1.0");
		values.add("2.0");
		values.add("3.0");
		values.add("4.0");
		values.add("5.0");
		values.add("6.0");
		values.add("6.0");
		
		List<String> filterValues = new ArrayList<>();
		filterValues.add("3.0");
		filterValues.add("4.0");
		filterValues.add("5.0");
		
		List<String> filteredList = ListUtil.filterList(values, filterValues);
		assertEquals(3, filteredList.size());
		assertEquals("3.0", filteredList.get(0));
		assertEquals("4.0", filteredList.get(1));
		assertEquals("5.0", filteredList.get(2));
	}
	
	@Test
	public void testFilterListWithEmptyFilter() {
		List<String> values = new ArrayList<>();
		values.add("1.0");
		values.add("2.0");
		values.add("3.0");
		values.add("4.0");
		values.add("5.0");
		values.add("6.0");
		
		List<String> filterValues = new ArrayList<>();
		
		List<String> filteredList = ListUtil.filterList(values, filterValues);
		assertEquals(6, filteredList.size());
		assertEquals("1.0", filteredList.get(0));
		assertEquals("2.0", filteredList.get(1));
		assertEquals("3.0", filteredList.get(2));
		assertEquals("4.0", filteredList.get(3));
		assertEquals("5.0", filteredList.get(4));
		assertEquals("6.0", filteredList.get(5));
		
		// Returned list should not be the same list object.
		assertNotSame(filteredList, values);
	}
	
	@Test
	public void testFilterListNanValues() {
		List<Double> values = new ArrayList<>();
		values.add(Double.NaN);
		values.add(1.0);
		values.add(Double.NaN);
		values.add(2.0);
		values.add(Double.NaN);
		values.add(3.0);
		values.add(Double.NaN);
		
		List<Double> filteredList = ListUtil.filterListNanValues(values);
		assertEquals(3, filteredList.size());
		assertEquals(1.0, filteredList.get(0), 1.0e-4);
		assertEquals(2.0, filteredList.get(1), 1.0e-4);
		assertEquals(3.0, filteredList.get(2), 1.0e-4);
	}
	
	@Test
	public void testFilterListDuplicates() {
		List<String> values = new ArrayList<>();
		values.add("1.0");
		values.add("2.0");
		values.add("3.0");
		values.add("3.0");
		values.add("3.0");
		
		List<String> filteredList = ListUtil.filterListDuplicates(values);
		assertEquals(3, filteredList.size());
		assertEquals("1.0", filteredList.get(0));
		assertEquals("2.0", filteredList.get(1));
		assertEquals("3.0", filteredList.get(2));
	}
}

