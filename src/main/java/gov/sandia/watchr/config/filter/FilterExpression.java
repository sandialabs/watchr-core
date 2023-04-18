package gov.sandia.watchr.config.filter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class FilterExpression {
    
    ////////////
    // FIELDS //
    ////////////

    private List<FilterExpressionElement> elements;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FilterExpression(String expression) {
        this.elements = new ArrayList<>();

        String[] tokens = expression.split("\\s+");
        Deque<String> tokenStack = new ArrayDeque<>(Arrays.asList(tokens));

        List<String> partialExpression = new ArrayList<>();
        while(!tokenStack.isEmpty()) {
            FilterExpressionElement nextElement = parseNext(partialExpression, tokenStack.pop().trim());
            if(nextElement != null) {
                elements.add(nextElement);
                partialExpression.clear();
            }
        }
    }

    /////////////
    // GETTERS //
    /////////////

    public List<FilterExpressionElement> getElements() {
        return elements;
    }

    ///////////
    // PARSE //
    ///////////

    private FilterExpressionElement parseNext(List<String> partialExpression, String nextToken) {
        if(nextToken.equals("&&") || nextToken.equals("||")) {
            return new BooleanOperatorElement(nextToken);
        } else if(nextToken.equals("==") || nextToken.equals(">") ||
                  nextToken.equals(">=") || nextToken.equals("<") ||
                  nextToken.equals("<=")) {
            if(partialExpression.size() != 1) {
                throw new IllegalStateException("Middle operator in the wrong place");
            } else {
                partialExpression.add(nextToken);
            }
        } else {
            if(partialExpression.size() == 1) {
                throw new IllegalStateException("Left or right operator in the wrong place");
            } else if(partialExpression.size() == 3) {
                throw new IllegalStateException("Too many operators");
            } else if(partialExpression.size() == 2) {
                String stringRepresentation = partialExpression.get(0) + " " + partialExpression.get(1) + " " + nextToken;
                return new ConditionalExpressionElement(stringRepresentation);
            } else {
                partialExpression.add(nextToken);
            }
        }
        return null;
    }

    //////////////
    // OVERRIDE //
    //////////////
    
    @Override
    public boolean equals(Object other) {
        boolean equals = false;
		if(other == null) {
            return false;
        } else if(other == this) {
            return true;
        } else if(getClass() != other.getClass()) {
            return false;
        } else {
			FilterExpression otherFE = (FilterExpression) other;
            equals = elements.equals(otherFE.elements);
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + elements.hashCode());
        return hash;
    }    
}
