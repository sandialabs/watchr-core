package gov.sandia.watchr.graph.chartreuse.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import gov.sandia.watchr.config.filter.DataFilter;
import gov.sandia.watchr.config.filter.FilterExpression;
import gov.sandia.watchr.config.filter.DataFilter.DataFilterPolicy;
import gov.sandia.watchr.config.filter.DataFilter.DataFilterType;
import gov.sandia.watchr.graph.chartreuse.ChartreuseException;

public class PlotTraceModelMetadataFilteringTests {
    	
	///////////
	// TESTS //
	///////////

	@Test
	public void testIsPointFiltered_UseMetadataToExcludeByBlacklist() {
		try {
			PlotTraceModel traceModel = new PlotTraceModel(null);
			PlotTracePoint point = new PlotTracePoint("1", "2", "3");
            point.metadata.put("Build", "A");

			traceModel.add(point);
			FilterExpression expression = new FilterExpression("Build == A");
			DataFilter df = new DataFilter(DataFilterType.METADATA, expression, DataFilterPolicy.BLACKLIST);
			traceModel.addFilterValue(df);

			assertTrue(traceModel.isPointFiltered(point));
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}

	@Test
	public void testIsPointFiltered_UseMetadataToExcludeByWhitelist() {
		try {
			PlotTraceModel traceModel = new PlotTraceModel(null);
			PlotTracePoint point = new PlotTracePoint("1", "2", "3");
            point.metadata.put("Build", "B");

			traceModel.add(point);
			FilterExpression expression = new FilterExpression("Build == A");
			DataFilter df = new DataFilter(DataFilterType.METADATA, expression, DataFilterPolicy.WHITELIST);
			traceModel.addFilterValue(df);

			assertTrue(traceModel.isPointFiltered(point));
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}

	@Test
	public void testIsPointFiltered_UseMetadataToIncludeByBlacklist() {
		try {
			PlotTraceModel traceModel = new PlotTraceModel(null);
			PlotTracePoint point = new PlotTracePoint("1", "2", "3");
            point.metadata.put("Build", "B");

			traceModel.add(point);
			FilterExpression expression = new FilterExpression("Build == A");
			DataFilter df = new DataFilter(DataFilterType.METADATA, expression, DataFilterPolicy.BLACKLIST);
			traceModel.addFilterValue(df);

			assertFalse(traceModel.isPointFiltered(point));
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}

	@Test
	public void testIsPointFiltered_UseMetadataToIncludeByWhitelist() {
		try {
			PlotTraceModel traceModel = new PlotTraceModel(null);
			PlotTracePoint point = new PlotTracePoint("1", "2", "3");
            point.metadata.put("Build", "A");

			traceModel.add(point);
			FilterExpression expression = new FilterExpression("Build == A");
			DataFilter df = new DataFilter(DataFilterType.METADATA, expression, DataFilterPolicy.WHITELIST);
			traceModel.addFilterValue(df);

			assertFalse(traceModel.isPointFiltered(point));
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}

	@Test
	public void testIsPointFiltered_BlacklistFromOneMetadata() {
		try {
			PlotTraceModel traceModel = new PlotTraceModel(null);
			PlotTracePoint point = new PlotTracePoint("1", "2", "3");
            point.metadata.put("build", "test");
			point.metadata.put("branch", "master");

			traceModel.add(point);
			FilterExpression expression = new FilterExpression("branch == master");
			DataFilter df = new DataFilter(DataFilterType.METADATA, expression, DataFilterPolicy.BLACKLIST);
			traceModel.addFilterValue(df);

			assertTrue(traceModel.isPointFiltered(point));
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}

	@Test
	public void testIsPointFiltered_BlacklistFromTwoMetadata() {
		try {
			PlotTraceModel traceModel = new PlotTraceModel(null);
			PlotTracePoint point = new PlotTracePoint("1", "2", "3");
            point.metadata.put("build", "test");
			point.metadata.put("branch", "master");

			traceModel.add(point);
			FilterExpression expression = new FilterExpression("branch == master && build == test");
			DataFilter df = new DataFilter(DataFilterType.METADATA, expression, DataFilterPolicy.BLACKLIST);
			traceModel.addFilterValue(df);

			assertTrue(traceModel.isPointFiltered(point));
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}

	@Test
	public void testIsPointFiltered_FailBlacklistBecauseOfOneMetadata() {
		try {
			PlotTraceModel traceModel = new PlotTraceModel(null);
			PlotTracePoint point = new PlotTracePoint("1", "2", "3");
            point.metadata.put("build", "test");
			point.metadata.put("branch", "master");

			traceModel.add(point);
			FilterExpression expression = new FilterExpression("branch == master && build == prod");
			DataFilter df = new DataFilter(DataFilterType.METADATA, expression, DataFilterPolicy.BLACKLIST);
			traceModel.addFilterValue(df);

			assertFalse(traceModel.isPointFiltered(point));
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}
}
