package gov.sandia.watchr.config.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import gov.sandia.watchr.config.filter.BooleanOperatorElement.BooleanOperator;

public class FilterExpressionTest {
    
    @Test
    public void testOneConditionalExpression() {
        FilterExpression exp = new FilterExpression("x == 1.0");
        assertEquals(1, exp.getElements().size());
        assertTrue(exp.getElements().get(0) instanceof ConditionalExpressionElement);

        ConditionalExpressionElement cee = (ConditionalExpressionElement) exp.getElements().get(0);
        assertEquals("x", cee.getLeft());
        assertEquals("==", cee.getMiddle());
        assertEquals("1.0", cee.getRight());
    }

    @Test
    public void testTwoConditionalExpressionsWithBoolean() {
        FilterExpression exp = new FilterExpression("x == 1.0 && y <= 5.0");
        assertEquals(3, exp.getElements().size());
        assertTrue(exp.getElements().get(0) instanceof ConditionalExpressionElement);
        assertTrue(exp.getElements().get(1) instanceof BooleanOperatorElement);
        assertTrue(exp.getElements().get(2) instanceof ConditionalExpressionElement);

        ConditionalExpressionElement cee1 = (ConditionalExpressionElement) exp.getElements().get(0);
        assertEquals("x", cee1.getLeft());
        assertEquals("==", cee1.getMiddle());
        assertEquals("1.0", cee1.getRight());

        ConditionalExpressionElement cee2 = (ConditionalExpressionElement) exp.getElements().get(2);
        assertEquals("y", cee2.getLeft());
        assertEquals("<=", cee2.getMiddle());
        assertEquals("5.0", cee2.getRight());

        BooleanOperatorElement boe = (BooleanOperatorElement) exp.getElements().get(1);
        assertEquals(BooleanOperator.AND, boe.getOperator());
    }

    @Test
    public void testThreeConditionalOperatorsWithTwoBooleans() {
        FilterExpression exp = new FilterExpression("x == 1.0 && y <= 5.0 || z >= 10.0");
        assertEquals(5, exp.getElements().size());
        assertTrue(exp.getElements().get(0) instanceof ConditionalExpressionElement);
        assertTrue(exp.getElements().get(1) instanceof BooleanOperatorElement);
        assertTrue(exp.getElements().get(2) instanceof ConditionalExpressionElement);
        assertTrue(exp.getElements().get(3) instanceof BooleanOperatorElement);
        assertTrue(exp.getElements().get(4) instanceof ConditionalExpressionElement);

        ConditionalExpressionElement cee1 = (ConditionalExpressionElement) exp.getElements().get(0);
        assertEquals("x", cee1.getLeft());
        assertEquals("==", cee1.getMiddle());
        assertEquals("1.0", cee1.getRight());

        ConditionalExpressionElement cee2 = (ConditionalExpressionElement) exp.getElements().get(2);
        assertEquals("y", cee2.getLeft());
        assertEquals("<=", cee2.getMiddle());
        assertEquals("5.0", cee2.getRight());

        ConditionalExpressionElement cee3 = (ConditionalExpressionElement) exp.getElements().get(4);
        assertEquals("z", cee3.getLeft());
        assertEquals(">=", cee3.getMiddle());
        assertEquals("10.0", cee3.getRight());

        BooleanOperatorElement boe1 = (BooleanOperatorElement) exp.getElements().get(1);
        assertEquals(BooleanOperator.AND, boe1.getOperator());

        BooleanOperatorElement boe2 = (BooleanOperatorElement) exp.getElements().get(3);
        assertEquals(BooleanOperator.OR, boe2.getOperator());
    }    
}
