/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.extractors;

import java.util.ArrayList;
import java.util.List;

public class ExtractionResult {
    
    ////////////
    // FIELDS //
    ////////////

    private final String sourceFileAbsPath;
    private final String path;
    private final String key;
    private final String value;
    private final List<ExtractionResult> children;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ExtractionResult() {
        this(null, "", "", "");
    }

    public ExtractionResult(String sourceFileAbsPath, String path, String key, String value) {
        this.sourceFileAbsPath = sourceFileAbsPath;
        this.path = path;
        this.key = key;
        this.value = value;
        this.children = new ArrayList<>();
    }

    /////////////
    // GETTERS //
    /////////////

    public String getSourceFile() {
        return sourceFileAbsPath;
    }

    public String getPath() {
        return path;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public List<ExtractionResult> getChildren() {
        return children;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public String toString() {
        return prettyPrint(0);
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
			ExtractionResult otherResult = (ExtractionResult) other;
            equals = path.equals(otherResult.path);
            equals = equals && key.equals(otherResult.key);
            equals = equals && value.equals(otherResult.value);
            equals = equals && children.equals(otherResult.children);
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + path.hashCode());
        hash = 31 * (hash + key.hashCode());
        hash = 31 * (hash + value.hashCode());
        hash = 31 * (hash + children.hashCode());
        return hash;        
    }

    /////////////
    // PRIVATE //
    /////////////

    private String prettyPrint(int depth) {
        StringBuilder depthSb = new StringBuilder();
        for(int i = 0; i < depth; i++) {
            depthSb.append("\t");
        }
        String depthStr = depthSb.toString();

        StringBuilder sb = new StringBuilder();
        sb.append(depthStr).append("[").append(path).append(", key=").append(key).append(", value=").append(value).append("]\n");
        sb.append(depthStr).append("[Children:\n");
        for(int i = 0; i < children.size(); i++) {
            sb.append(children.get(i).prettyPrint(depth+1));
        }
        sb.append(depthStr).append("]\n");
        return sb.toString();
    }
}
