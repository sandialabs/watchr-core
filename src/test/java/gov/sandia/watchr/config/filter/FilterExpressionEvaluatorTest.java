package gov.sandia.watchr.config.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class FilterExpressionEvaluatorTest {
    
    private void testFilterExpressionEvaluatorTrue(String expression, Map<String, String> filterArgs) {
        FilterExpression filter = new FilterExpression(expression);
        assertTrue(FilterExpressionEvaluator.evaluate(filter, filterArgs));
    }

    private void testFilterExpressionEvaluatorFalse(String expression, Map<String, String> filterArgs) {
        FilterExpression filter = new FilterExpression(expression);
        assertFalse(FilterExpressionEvaluator.evaluate(filter, filterArgs));
    }

    @Test
    public void testEvaluateEquality() {
        Map<String, String> filterArgs = new HashMap<>();
        filterArgs.put("x", "1.0");
        testFilterExpressionEvaluatorTrue("x == 1.0", filterArgs);
    }

    @Test
    public void testEvaluateGreaterThanOrEqual() {
        Map<String, String> filterArgs = new HashMap<>();
        filterArgs.put("x", "2.0");
        testFilterExpressionEvaluatorTrue("x >= 1.0", filterArgs);
    }

    @Test
    public void testEvaluateGreaterThanOrEqualFailure() {
        Map<String, String> filterArgs = new HashMap<>();
        filterArgs.put("x", "0.0");
        testFilterExpressionEvaluatorFalse("x >= 1.0", filterArgs);
    }

    @Test
    public void testEvaluateLessThanOrEqual() {
        Map<String, String> filterArgs = new HashMap<>();
        filterArgs.put("x", "0.0");
        testFilterExpressionEvaluatorTrue("x <= 1.0", filterArgs);
    }

    @Test
    public void testEvaluateLessThanOrEqualFailure() {
        Map<String, String> filterArgs = new HashMap<>();
        filterArgs.put("x", "2.0");
        testFilterExpressionEvaluatorFalse("x <= 1.0", filterArgs);
    }

    @Test
    public void testEvaluateGreaterThan() {
        Map<String, String> filterArgs = new HashMap<>();
        filterArgs.put("x", "2.0");
        testFilterExpressionEvaluatorTrue("x > 1.0", filterArgs);
    }

    @Test
    public void testEvaluateGreaterThanFailure() {
        Map<String, String> filterArgs = new HashMap<>();
        filterArgs.put("x", "1.0");
        testFilterExpressionEvaluatorFalse("x > 1.0", filterArgs);
    }

    @Test
    public void testEvaluateLessThan() {
        Map<String, String> filterArgs = new HashMap<>();
        filterArgs.put("x", "0.0");
        testFilterExpressionEvaluatorTrue("x < 1.0", filterArgs);
    }

    @Test
    public void testEvaluateLessThanFailure() {
        Map<String, String> filterArgs = new HashMap<>();
        filterArgs.put("x", "1.0");
        testFilterExpressionEvaluatorFalse("x < 1.0", filterArgs);
    }

    @Test
    public void testEvaluateEqualityForTwoExpressions() {
        Map<String, String> filterArgs = new HashMap<>();
        filterArgs.put("x", "1.0");
        filterArgs.put("y", "2.0");
        testFilterExpressionEvaluatorTrue("x == 1.0 && y == 2.0", filterArgs);
    }

    @Test
    public void testEvaluateEqualityFalseIfSecondExpressionIsBad() {
        Map<String, String> filterArgs = new HashMap<>();
        filterArgs.put("x", "1.0");
        filterArgs.put("y", "0.0");
        testFilterExpressionEvaluatorFalse("x == 1.0 && y == 2.0", filterArgs);
    }

    @Test
    public void testEvaluateEqualityFalseIfFirstExpressionIsBad() {
        Map<String, String> filterArgs = new HashMap<>();
        filterArgs.put("x", "0.0");
        filterArgs.put("y", "2.0");
        testFilterExpressionEvaluatorFalse("x == 1.0 && y == 2.0", filterArgs);
    }

    @Test
    public void testEvaluateEqualityTrueIfSecondExpressionIsBadButUsesOr() {
        Map<String, String> filterArgs = new HashMap<>();
        filterArgs.put("x", "1.0");
        filterArgs.put("y", "0.0");
        testFilterExpressionEvaluatorTrue("x == 1.0 || y == 2.0", filterArgs);
    }

    @Test
    public void testEvaluateEqualityFalseIfFirstExpressionIsBadButUsesOr() {
        Map<String, String> filterArgs = new HashMap<>();
        filterArgs.put("x", "0.0");
        filterArgs.put("y", "2.0");
        testFilterExpressionEvaluatorTrue("x == 1.0 || y == 2.0", filterArgs);
    }
}
