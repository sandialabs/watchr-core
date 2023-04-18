package gov.sandia.watchr.config.filter;

public class ConditionalExpressionElement extends FilterExpressionElement {

    private String left;
    private String middle;
    private String right;

    public ConditionalExpressionElement(String stringRepresentation) {
        parseString(stringRepresentation);
    }

    public void parseString(String stringRepresentation) {
        String[] tokens = stringRepresentation.split("\\s+");
        if(tokens.length == 3) {
            this.left = tokens[0];
            this.middle = tokens[1];
            this.right = tokens[2];
        }
    }

    public String getLeft() {
        return left;
    }

    public String getMiddle() {
        return middle;
    }

    public String getRight() {
        return right;
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
			ConditionalExpressionElement otherCEE = (ConditionalExpressionElement) other;
            equals = left.equals(otherCEE.left);
            equals = equals && middle.equals(otherCEE.middle);
            equals = equals && right.equals(otherCEE.right);
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + left.hashCode());
        hash = 31 * (hash + middle.hashCode());
        hash = 31 * (hash + right.hashCode());
        return hash;
    }

    @Override
    public String toString() {
        return left + middle + right;
    }
}
