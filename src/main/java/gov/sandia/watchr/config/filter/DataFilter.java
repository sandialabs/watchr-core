/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config.filter;

public class DataFilter {

    //////////
    // ENUM //
    //////////

    public enum DataFilterType {
        POINT,
        METADATA
    }

    public enum DataFilterPolicy {
        WHITELIST,
        BLACKLIST
    }

    ////////////
    // FIELDS //
    ////////////

    private DataFilterType type;
    private FilterExpression expression;
    private DataFilterPolicy policy;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public DataFilter() {
        this(null, null, null);
    }

    public DataFilter(DataFilter copy) {
        this(copy.type, copy.expression, copy.policy);
    }

    public DataFilter(DataFilterType type, FilterExpression expression, DataFilterPolicy policy) {
        this.type = type;
        this.expression = expression;
        this.policy = policy;
    }

    /////////////
    // GETTERS //
    /////////////

    public DataFilterType getType() {
        return type;
    }

    public FilterExpression getExpression() {
        return expression;
    }

    public DataFilterPolicy getPolicy() {
        return policy;
    }

    /////////////
    // SETTERS //
    /////////////

    public void setType(String typeAsString) {
        if(typeAsString.equalsIgnoreCase("POINT")) {
            this.type = DataFilterType.POINT;
        } else if(typeAsString.equalsIgnoreCase("METADATA")) {
            this.type = DataFilterType.METADATA;
        } else {
            throw new IllegalStateException("Unknown filter type " + typeAsString);
        }
    }

    public void setExpression(String expression) {
        this.expression = new FilterExpression(expression);
    }

    public void setPolicy(String policyAsString) {
        if(policyAsString.equalsIgnoreCase("WHITELIST")) {
            this.policy = DataFilterPolicy.WHITELIST;
        } else if(policyAsString.equalsIgnoreCase("BLACKLIST")) {
            this.policy = DataFilterPolicy.BLACKLIST;
        } else {
            throw new IllegalStateException("Unknown filter policy " + policyAsString);
        }
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
			DataFilter otherDF = (DataFilter) other;
            equals = expression.equals(otherDF.expression);
            equals = equals && policy == otherDF.policy;
            equals = equals && type == otherDF.type;
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + expression.hashCode());
        hash = 31 * (hash + policy.hashCode());
        hash = 31 * (hash + type.hashCode());
        return hash;
    }
}
