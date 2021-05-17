package gov.sandia.watchr.graph.chartreuse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import gov.sandia.watchr.graph.chartreuse.type.NodeType;
import gov.sandia.watchr.graph.chartreuse.type.PlotData;

public class ChartreuseTestsUtil {
	
	/**
	 * Generate a double array of a certain size, populated with random double values.
	 * @param size The length of the array.
	 * @param rand The {@link Random} object to use for double number generation.
	 * @return The generated double array.
	 */
	public static double[] doubleArrayOfSize(int size, Random rand) {
		double[] array = new double[size];
		for(int i = 0; i < size; i++) {
			array[i] = rand.nextDouble();
		}
		return array;
	}
	
	/**
	 * Generate a {@link Double} object array of a certain size, populated with random values.
	 * @param size The length of the array.
	 * @param rand The {@link Random} object to use for double number generation.
	 * @return The generated Double array.
	 */
	public static Double[] doubleObjArrayOfSize(int size, Random rand) {
		Double[] array = new Double[size];
		for(int i = 0; i < size; i++) {
			array[i] = rand.nextDouble();
		}
		return array;
	}

    public static Double[][] getRosenbrockExampleData() {
        return new Double[][]{
                { -2.0, -1.5, -1.0, -0.5, 0.0, 0.5, 1.0, 1.5, 2.0,
                  -2.0, -1.5, -1.0, -0.5, 0.0, 0.5, 1.0, 1.5, 2.0,
                  -2.0, -1.5, -1.0, -0.5, 0.0, 0.5, 1.0, 1.5, 2.0,
                  -2.0, -1.5, -1.0, -0.5, 0.0, 0.5, 1.0, 1.5, 2.0,
                  -2.0, -1.5, -1.0, -0.5, 0.0, 0.5, 1.0, 1.5, 2.0,
                  -2.0, -1.5, -1.0, -0.5, 0.0, 0.5, 1.0, 1.5, 2.0,
                  -2.0, -1.5, -1.0, -0.5, 0.0, 0.5, 1.0, 1.5, 2.0,
                  -2.0, -1.5, -1.0, -0.5, 0.0, 0.5, 1.0, 1.5, 2.0,
                  -2.0, -1.5, -1.0, -0.5, 0.0, 0.5, 1.0, 1.5, 2.0 },
                { -2.0, -2.0, -2.0, -2.0, -2.0, -2.0, -2.0, -2.0, -2.0,
                  -1.5, -1.5, -1.5, -1.5, -1.5, -1.5, -1.5, -1.5, -1.5,
                  -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0,
                  -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5,
                   0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,
                   0.5,  0.5,  0.5,  0.5,  0.5,  0.5,  0.5,  0.5,  0.5,
                   1.0,  1.0,  1.0,  1.0,  1.0,  1.0,  1.0,  1.0,  1.0,
                   1.5,  1.5,  1.5,  1.5,  1.5,  1.5,  1.5,  1.5,  1.5,
                   2.0,  2.0,  2.0,  2.0,  2.0,  2.0,  2.0,  2.0,  2.0 },
                { 3609.0, 1812.5, 904.0, 508.5, 401.0, 506.5, 900.0, 1806.5, 3601.0, 3034.0, 1412.5, 629.0, 308.5, 226.0, 306.5, 625.0, 1406.5, 3026.0, 2509.0, 1062.5, 404.0, 158.5, 101.0, 156.5, 400.0, 1056.5, 2501.0, 2034.0, 762.5, 229.0, 58.5, 26.0, 56.5, 225.0, 756.5, 2026.0, 1609.0, 512.5, 104.0, 8.5, 1.0, 6.5, 100.0, 506.5, 1601.0, 1234.0, 312.5, 29.0, 8.5, 26.0, 6.5, 25.0, 306.5, 1226.0, 909.0, 162.5, 4.0, 58.5, 101.0, 56.5, 0.0, 156.5, 901.0, 634.0, 62.5, 29.0, 158.5, 226.0, 156.5, 25.0, 56.5, 626.0, 409.0, 12.5, 104.0, 308.5, 401.0, 306.5, 100.0, 6.5, 401.0}
            };
    }

	/**
	 * Generate a junk {@link PlotData} object for unit testing.  Variable and response header labels are both
	 * monotonically-increasing chars, starting from the user-provided start labels.  Horizontal data will be of
	 * variable type, and vertical data will be of response type.
	 * 
	 * @param oneDimensional If true, the PlotData object will be one-dimensional (see PlotData's Javadoc for more
	 * details on what that means exactly).
	 * @param gridHorizontalLength The horizontal length of the PlotData.
	 * @param gridVerticalLength The vertical length of the PlotData.
	 * @param startHorizontalLabel The start char for horizontal/variable labels.
	 * @param startVerticalLabel The start char for vertical/response labels.
	 * @return The generated PlotData object.
	 */
	@SuppressWarnings("unchecked")
	public static PlotData generatePlotDataJunkData(
			boolean oneDimensional, int gridHorizontalLength, int gridVerticalLength, char startHorizontalLabel, char startVerticalLabel) {
		
		int horizontalLabel = startHorizontalLabel;
		int verticalLabel = startVerticalLabel;
		double dataCounter = 0.0;
		
		List<Pair<NodeType, String>> xHeaders = new ArrayList<>();
		List<Pair<NodeType, String>> yHeaders = new ArrayList<>();
		List<List<Double>> data = new ArrayList<>();
		
		for(int i = 0; i < gridHorizontalLength; i++) {
			Character horizontalLabelChar = (char)horizontalLabel;
			xHeaders.add(new ImmutablePair<>(NodeType.VARIABLE, horizontalLabelChar.toString()));
			horizontalLabel++;
		}
		if(!oneDimensional) {
			for(int i = 0; i < gridVerticalLength; i++) {
				Character verticalLabelChar = (char)verticalLabel;
				yHeaders.add(new ImmutablePair<>(NodeType.RESPONSE, verticalLabelChar.toString()));
				verticalLabel++;
			}
		}
		
		for(int i = 0; i < gridHorizontalLength; i++) {
			List<Double> row = new ArrayList<>();
			for(int j = 0; j < gridVerticalLength; j++) {
				row.add(dataCounter);
				dataCounter ++;
			}			
			data.add(row);
		}
		
		Pair<NodeType, String>[] xHeadersArr = new Pair[xHeaders.size()];
		Pair<NodeType, String>[] yHeadersArr = new Pair[yHeaders.size()];
		Double[][] dataArr = new Double[gridHorizontalLength][gridVerticalLength];
		
		for(int i = 0; i < xHeaders.size(); i++) {
			xHeadersArr[i] = xHeaders.get(i);
		}
		for(int i = 0; i < yHeaders.size(); i++) {
			yHeadersArr[i] = yHeaders.get(i);
		}
		for(int i = 0; i < data.size(); i++) {
			for(int j = 0; j < data.get(i).size(); j++) {
				dataArr[i][j] = data.get(i).get(j);
			}
		}
		
		return new PlotData("Junk", "", dataArr, xHeadersArr, yHeadersArr);
	}	

}
