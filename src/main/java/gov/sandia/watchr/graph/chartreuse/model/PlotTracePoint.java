package gov.sandia.watchr.graph.chartreuse.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PlotTracePoint {
    
    ////////////
    // FIELDS //
    ////////////

    public final String x;
    public final String y;
    public final String z;
    public final Map<String, String> metadata;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PlotTracePoint(double x, double y) {
        this(Double.toString(x),Double.toString(y),"");
    }

    public PlotTracePoint(double x, double y, double z) {
        this(Double.toString(x),Double.toString(y),Double.toString(z));
    }

    public PlotTracePoint(String x, String y) {
        this(x, y, "");
    }

    public PlotTracePoint(String x, String y, String z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.metadata = new HashMap<>();
    }

    public PlotTracePoint(PlotTracePoint copy) {
        this(copy.x, copy.y, copy.z);
        for(Entry<String, String> entry : copy.metadata.entrySet()) {
            this.metadata.put(entry.getKey(), entry.getValue());
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
			PlotTracePoint point = (PlotTracePoint) other;
            equals = point.x.equals(x);
            equals = equals && point.y.equals(y);
            equals = equals && point.z.equals(z);
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + x.hashCode());
        hash = 31 * (hash + y.hashCode());
        hash = 31 * (hash + z.hashCode());
        hash = 31 * (hash + metadata.hashCode());
        return hash;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + ", " + z + "]";
    }
}
