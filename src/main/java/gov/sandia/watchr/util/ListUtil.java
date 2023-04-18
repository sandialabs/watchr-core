/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Various list utility methods.
 * @author Elliott Ridgway
 *
 */
public class ListUtil {

	/////////////////
	// CONSTRUCTOR //
	/////////////////

	private ListUtil() {}
	
	///////////
	// CHECK //
	///////////
	
	/**
	 * Checks whether an {@link Object} is a {@link List} of a specific {@link Class} type.
	 * @param obj The object to test.
	 * @param clazz The Class to test for.
	 * @return True if the object is a List of type {@code clazz}.
	 */
	public static boolean isListOf(Object obj, Class<?> clazz) {
		if(obj instanceof List || obj.getClass().isAssignableFrom(List.class)) {
			List<?> list = (List<?>) obj;
			return everyElementInListIsOfType(list, clazz);
		}
		return false;
	}
	
	/**
	 * Checks whether every element in a {@link List} is of a specific {@link Class} type.
	 * @param elements The List of elements.
	 * @param clazz The Class type to check for.
	 * @return True if all List elements are of a specific Class type.
	 */
	public static boolean everyElementInListIsOfType(List<?> elements, Class<?> clazz) {
		for(Object element : elements) {
			if(element.getClass() != clazz && !element.getClass().isAssignableFrom(clazz)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks whether the provided element is the last element in a {@link List}.
	 * @param elements The List to search.
	 * @param element The element to check.
	 * @return True if the provided element is the last element in the List.
	 */
	public static boolean isLastElement(List<?> elements, Object element) {
		int elementIndex = elements.indexOf(element);
		return elementIndex == elements.size() - 1;
	}
	
	/**
	 * Filters a {@link List} using elements provided in {@code filterData}.  If
	 * {@code filterData} does not contain an element from {@code originalData}, that
	 * element from {@code originalData} is discarded from the final returned list.
	 * If {@code filterData} is empty, the original list is returned with no elements
	 * removed.
	 * @param originalData The data to filter.
	 * @param filterData The data to filter by.
	 * @return The filtered list, only containing elements that appeared in {@code filterData};
	 * or the original list if {@code filterData} is empty.
	 */
	public static List<String> filterList(List<String> originalData, List<String> filterData) {
		List<String> finalList = new ArrayList<>();
		if(filterData.isEmpty()) {
			finalList.addAll(originalData);
		} else {
			for(int i = 0; i < originalData.size(); i++) {
				if(filterData.contains(originalData.get(i))) {
					finalList.add(originalData.get(i));
				}
			}
		}
		return finalList;
	}
	
	/**
	 * Filters NaN values out of a given {@link List} of {@link Double}s.
	 * 
	 * @param values The List of values to filter.
	 * @return A new List containing all the original values except for NaNs.
	 */
	public static List<Double> filterListNanValues(List<Double> values) {
		final List<Double> filteredList = new ArrayList<>();
		for(Double value : values) {
			if(!value.equals(Double.NaN)) {
				filteredList.add(value);
			}
		}
		return filteredList;
	}
	
	/**
	 * Filters duplicated {@link String} values out of the provided list.
	 * @param originalData The original List of Strings.
	 * @return The filtered List with no duplicates.
	 */
	public static List<String> filterListDuplicates(List<String> originalData) {
		List<String> finalList = new ArrayList<>();
		for(int i = 0; i < originalData.size(); i++) {
			if(!finalList.contains(originalData.get(i))) {
				finalList.add(originalData.get(i));
			}
		}
		return finalList;
	}

	/**
	 * Truncate a List based on a start and end position.
	 * @param oldList The original List.
	 * @param startIndex The start index (included).
	 * @param endIndex The end index (excluded).
	 * @return The truncated List.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static List<?> truncate(List oldList, int startIndex, int endIndex) {
		List newList = new ArrayList<>();
		for(int i = startIndex; i < endIndex && i < oldList.size(); i++) {
			newList.add(oldList.get(i));
		}
		return newList;
	}
}

