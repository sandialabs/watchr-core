/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.util;

public class RGBA extends RGB {

    public final double alpha;

    public RGBA(int red, int green, int blue, double alpha) {
        super(red, green, blue);
        
        if(alpha < 0.0) this.alpha = 0.0;
        else if(alpha > 1.0) this.alpha = 1.0;
        else this.alpha = alpha;
    }

    public RGBA(RGBA copy) {
        super(copy);
        this.alpha = copy.alpha;
    }

    @Override
    public boolean equals(Object other) {
		if(other == null) {
			return false;
		} else if(other == this) {
			return true;
		} else if(getClass() != other.getClass()) {
			return false;
		} else {
            RGBA otherRgba = (RGBA) other;
            boolean equals = super.equals(otherRgba);
            equals = equals && otherRgba.alpha == alpha;
            return equals;
        }
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 31 * (hash + Double.hashCode(alpha));
        return hash;
    }

    @Override
    public String toString() {
        return "(" + red + "," + green + "," + blue + "," + alpha + ")";
    }
}
