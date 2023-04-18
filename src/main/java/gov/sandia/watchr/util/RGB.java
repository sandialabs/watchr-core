/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.util;

public class RGB {

    public final int red;
    public final int green;
    public final int blue;

    public RGB(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public RGB(RGB copy) {
        if(copy != null) {
            this.red = copy.red;
            this.green = copy.green;
            this.blue = copy.blue;
        } else {
            this.red = 0;
            this.green = 0;
            this.blue = 0;
        }
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
            RGB otherRgb = (RGB) other;
            boolean equals = otherRgb.red == red;
            equals = equals && otherRgb.green == green;
            equals = equals && otherRgb.blue == blue;
            return equals;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + Integer.hashCode(red));
        hash = 31 * (hash + Integer.hashCode(green));
        hash = 31 * (hash + Integer.hashCode(blue));
        return hash;
    }

    @Override
    public String toString() {
        return "(" + red + "," + green + "," + blue + ")";
    }
}
