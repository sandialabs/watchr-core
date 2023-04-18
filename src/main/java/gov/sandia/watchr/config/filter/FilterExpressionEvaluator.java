package gov.sandia.watchr.config.filter;

import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

import gov.sandia.watchr.config.filter.BooleanOperatorElement.BooleanOperator;
import gov.sandia.watchr.util.StringUtil;

public class FilterExpressionEvaluator {

    private FilterExpressionEvaluator() {}
    
    public static boolean evaluate(FilterExpression expression, Map<String, String> arguments) {
        boolean trueSoFar = true;
        boolean result = false;
        BooleanOperator nextRelationship = null;

        for(FilterExpressionElement nextElement : expression.getElements()) {
            if(nextElement instanceof BooleanOperatorElement) {
                BooleanOperatorElement boolElement = (BooleanOperatorElement) nextElement;
                if(!trueSoFar && boolElement.getOperator() == BooleanOperator.AND) {
                    return false; // Short circuit out
                }
                nextRelationship = boolElement.getOperator();
            } else if(nextElement instanceof ConditionalExpressionElement) {
                ConditionalExpressionElement conditionalElement = (ConditionalExpressionElement) nextElement;
                String argument = conditionalElement.getLeft();
                String operator = conditionalElement.getMiddle();
                String expectedValue = conditionalElement.getRight();

                if(arguments.containsKey(argument)) {
                    String actualValue = arguments.get(argument);
                    if(NumberUtils.isCreatable(actualValue) && NumberUtils.isCreatable(expectedValue)) {
                        result = compareAsNumbers(Double.parseDouble(actualValue), operator, Double.parseDouble(expectedValue));
                    } else {
                        result = compareAsStrings(actualValue, operator, expectedValue);
                    }

                    if(nextRelationship == null) {
                        trueSoFar = result;
                    } else if(nextRelationship == BooleanOperator.AND) {
                        trueSoFar = trueSoFar && result;
                    } else if(nextRelationship == BooleanOperator.OR) {
                        trueSoFar = trueSoFar || result;
                    }
                }
            }
        }
        return trueSoFar;
    }

    private static boolean compareAsNumbers(Double actualValue, String operator, Double expectedValue) {
        if(operator.equals("==")) {
            return actualValue.equals(expectedValue);
        } else if(operator.equals(">=")) {
            return actualValue >= expectedValue;
        } else if(operator.equals(">")) {
            return actualValue > expectedValue;
        } else if(operator.equals("<=")) {
            return actualValue <= expectedValue;
        } else if(operator.equals("<")) {
            return actualValue < expectedValue;
        } else {
            throw new UnsupportedOperationException("Operator " + operator + " cannot be applied for comparing " + actualValue + " and " + expectedValue);
        }
    }

    private static boolean compareAsStrings(String actualValue, String operator, String expectedValue) {
        if(operator.equals("==")) {
            if(expectedValue.contains("*")) {
                String expectedRegex = StringUtil.convertToRegex(expectedValue);
                return actualValue.matches(expectedRegex);
            } else {
                return actualValue.equals(expectedValue);
            }
        } else {
            throw new UnsupportedOperationException("Operator " + operator + " cannot be applied for comparing " + actualValue + " and " + expectedValue);
        }
    }
}
