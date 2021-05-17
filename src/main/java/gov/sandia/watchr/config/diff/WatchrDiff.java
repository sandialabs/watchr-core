/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config.diff;

import gov.sandia.watchr.util.OsUtil;

public class WatchrDiff<E> {

    ////////////
    // FIELDS //
    ////////////
    
    private final String path;
    private final DiffCategory property;

    private E beforeValue;
    private E nowValue;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public WatchrDiff(String path, DiffCategory property) {
        this.path = path;
        this.property = property;
    }

    /////////////
    // GETTERS //
    /////////////

    public DiffCategory getProperty() {
        return property;
    }

    public String getPath() {
        return path;
    }

    public E getBeforeValue() {
        return beforeValue;
    }

    public E getNowValue() {
        return nowValue;
    }

    /////////////
    // SETTERS //
    /////////////

    public void setBeforeValue(E beforeValue) {
        this.beforeValue = beforeValue;
    }

    public void setNowValue(E nowValue) {
        this.nowValue = nowValue;
    }

    /////////////
    // UTILITY //
    /////////////

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(path).append(", ").append(property.toString()).append(OsUtil.getOSLineBreak());
        if(beforeValue != null) {
            sb.append("\tBefore:").append(beforeValue.toString()).append(OsUtil.getOSLineBreak());
        } else {
            sb.append("\tBefore: NULL").append(OsUtil.getOSLineBreak());
        }
        if(nowValue != null) {
            sb.append("\tNow:").append(nowValue.toString()).append(OsUtil.getOSLineBreak());
        } else {
            sb.append("\tNow: NULL").append(OsUtil.getOSLineBreak());
        }
        return sb.toString();
    }

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
			WatchrDiff<?> otherDiff = (WatchrDiff<?>) other;

            equals = path.equals(otherDiff.path);
            equals = equals && property.equals(otherDiff.property);
            equals = equals && beforeValue.equals(otherDiff.beforeValue);
            equals = equals && nowValue.equals(otherDiff.nowValue);
        }
        return equals;        
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + path.hashCode());
        hash = 31 * (hash + property.hashCode());
        hash = 31 * (hash + beforeValue.hashCode());
        hash = 31 * (hash + nowValue.hashCode());
        return hash;
    }
}
