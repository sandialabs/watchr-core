package gov.sandia.watchr.graph.chartreuse.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import gov.sandia.watchr.config.filter.DataFilter;
import gov.sandia.watchr.config.filter.FilterExpression;
import gov.sandia.watchr.config.filter.BooleanOperatorElement.BooleanOperator;
import gov.sandia.watchr.config.filter.DataFilter.DataFilterPolicy;
import gov.sandia.watchr.config.filter.DataFilter.DataFilterType;
import gov.sandia.watchr.graph.chartreuse.ChartreuseException;

public class PlotTraceModelPointFilteringTests {
	
	///////////
	// TESTS //
	///////////

	@Test
	public void testIsPointFiltered_UseXToBlacklist() {
		try {
			PlotTraceModel traceModel = new PlotTraceModel(null);
			PlotTracePoint point = new PlotTracePoint("1", "2", "3");
			traceModel.add(point);
			FilterExpression expression = new FilterExpression("x == 1.0");
			DataFilter df = new DataFilter(DataFilterType.POINT, expression, DataFilterPolicy.BLACKLIST);
			traceModel.addFilterValue(df);

			assertTrue(traceModel.isPointFiltered(point));
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}

	@Test
	public void testIsPointFiltered_UseXandYToBlacklist() {
		try {
			PlotTraceModel traceModel = new PlotTraceModel(null);
			PlotTracePoint point = new PlotTracePoint("1", "2", "3");
			traceModel.add(point);
			FilterExpression expression = new FilterExpression("x == 1.0 && y == 2.0");
			DataFilter df = new DataFilter(DataFilterType.POINT, expression, DataFilterPolicy.BLACKLIST);
			traceModel.addFilterValue(df);

			assertTrue(traceModel.isPointFiltered(point));
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}

	@Test
	public void testIsPointFiltered_UseXandYandZToBlacklist() {
		try {
			PlotTraceModel traceModel = new PlotTraceModel(null);
			PlotTracePoint point = new PlotTracePoint("1", "2", "3");
			traceModel.add(point);
			FilterExpression expression = new FilterExpression("x == 1.0 && y == 2.0 && z == 3.0");
			DataFilter df = new DataFilter(DataFilterType.POINT, expression, DataFilterPolicy.BLACKLIST);
			traceModel.addFilterValue(df);

			assertTrue(traceModel.isPointFiltered(point));
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}

	@Test
	public void testIsPointFiltered_UseXorYorZToBlacklist() {
		try {
			PlotTraceModel traceModel = new PlotTraceModel(null);
			PlotTracePoint point = new PlotTracePoint("1", "2", "3");
			traceModel.add(point);
			FilterExpression expression = new FilterExpression("x == 1.0 || y == 2.0 || z == 3.0");
			DataFilter df = new DataFilter(DataFilterType.POINT, expression, DataFilterPolicy.BLACKLIST);
			traceModel.addFilterValue(df);

			assertTrue(traceModel.isPointFiltered(point));
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}

	@Test
	public void testIsPointFiltered_FailToBlacklistBecauseOfX() {
		try {
			PlotTraceModel traceModel = new PlotTraceModel(null);
			PlotTracePoint point = new PlotTracePoint("1", "2", "3");
			traceModel.add(point);
			FilterExpression expression = new FilterExpression("x == 4.0 && y == 2.0 && z == 3.0");
			DataFilter df = new DataFilter(DataFilterType.POINT, expression, DataFilterPolicy.BLACKLIST);
			traceModel.addFilterValue(df);

			assertFalse(traceModel.isPointFiltered(point));
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}

	@Test
	public void testIsPointFiltered_BlacklistEvenThoughXFails() {
		try {
			PlotTraceModel traceModel = new PlotTraceModel(null);
			PlotTracePoint point = new PlotTracePoint("1", "2", "3");
			traceModel.add(point);
			FilterExpression expression = new FilterExpression("x == 4.0 || y == 2.0 && z == 3.0");
			DataFilter df = new DataFilter(DataFilterType.POINT, expression, DataFilterPolicy.BLACKLIST);
			traceModel.addFilterValue(df);

			assertTrue(traceModel.isPointFiltered(point));
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}

	@Test
	public void testIsPointFiltered_UseMultipleFiltersToBlacklist() {
		try {
			PlotTraceModel traceModel = new PlotTraceModel(null);
			PlotTracePoint point = new PlotTracePoint("1", "2", "3");
			traceModel.add(point);
			DataFilter df1 = new DataFilter(DataFilterType.POINT, new FilterExpression("x == 1.0"), DataFilterPolicy.BLACKLIST);
			DataFilter df2 = new DataFilter(DataFilterType.POINT, new FilterExpression("y == 2.0"), DataFilterPolicy.BLACKLIST);
			DataFilter df3 = new DataFilter(DataFilterType.POINT, new FilterExpression("z == 3.0"), DataFilterPolicy.BLACKLIST);
			traceModel.addFilterValue(df1);
			traceModel.addFilterValue(df2);
			traceModel.addFilterValue(df3);

			assertTrue(traceModel.isPointFiltered(point));
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}

	@Test
	public void testIsPointFiltered_UseMultipleFiltersToBlacklistWithoutX() {
		try {
			PlotTraceModel traceModel = new PlotTraceModel(null);
			PlotTracePoint point = new PlotTracePoint("1", "2", "3");
			traceModel.add(point);
			DataFilter df1 = new DataFilter(DataFilterType.POINT, new FilterExpression("x == 4.0"), DataFilterPolicy.BLACKLIST);
			DataFilter df2 = new DataFilter(DataFilterType.POINT, new FilterExpression("y == 2.0"), DataFilterPolicy.BLACKLIST);
			DataFilter df3 = new DataFilter(DataFilterType.POINT, new FilterExpression("z == 3.0"), DataFilterPolicy.BLACKLIST);
			traceModel.addFilterValue(df1);
			traceModel.addFilterValue(df2);
			traceModel.addFilterValue(df3);

			assertTrue(traceModel.isPointFiltered(point));
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}

	@Test
	public void testIsPointFiltered_FailToBlacklistWithMultipleFiltersBecauseOfX() {
		try {
			PlotTraceModel traceModel = new PlotTraceModel(null);
			PlotTracePoint point = new PlotTracePoint("1", "2", "3");
			traceModel.add(point);
			DataFilter df1 = new DataFilter(DataFilterType.POINT, new FilterExpression("x == 4.0"), DataFilterPolicy.BLACKLIST);
			DataFilter df2 = new DataFilter(DataFilterType.POINT, new FilterExpression("y == 2.0"), DataFilterPolicy.BLACKLIST);
			DataFilter df3 = new DataFilter(DataFilterType.POINT, new FilterExpression("z == 3.0"), DataFilterPolicy.BLACKLIST);
			traceModel.addFilterValue(df1);
			traceModel.addFilterValue(df2);
			traceModel.addFilterValue(df3);

			assertTrue(traceModel.isPointFiltered(point, BooleanOperator.AND));
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}
}

