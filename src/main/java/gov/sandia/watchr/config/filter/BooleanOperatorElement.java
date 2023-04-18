package gov.sandia.watchr.config.filter;

public class BooleanOperatorElement extends FilterExpressionElement {

    public enum BooleanOperator {
        AND,
        OR
    }
    private BooleanOperator operator;

    public BooleanOperatorElement(String stringRepresentation) {
        parseString(stringRepresentation);
    }

    public void parseString(String stringRepresentation) {
        if(stringRepresentation.equals("&&")) {
            this.operator = BooleanOperator.AND;
        } else if(stringRepresentation.equals("||")) {
            this.operator = BooleanOperator.OR;
        } else {
            this.operator = null;
        }
    }

    public BooleanOperator getOperator() {
        return operator;
    }

    public void setOperator(BooleanOperator operator) {
        this.operator = operator;
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
			BooleanOperatorElement otherBOE = (BooleanOperatorElement) other;
            equals = operator == otherBOE.operator;
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + operator.hashCode());
        return hash;
    }

    @Override
    public String toString() {
        if(this.operator == BooleanOperator.AND) {
            return "&&";
        } else if(this.operator == BooleanOperator.OR) {
            return "||";
        }
        return "";
    }
}
