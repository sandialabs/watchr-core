/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config.reader;

public class Shorthand {
    
    ////////////
    // FIELDS //
    ////////////

    private String axis;
    private String groupingField;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public Shorthand(String str) {
        String[] tokens = str.split("/");
        if(tokens.length >= 2) {
            axis = tokens[0];
            groupingField = tokens[1];
        }
    }

    /////////////
    // GETTERS //
    /////////////

    public String getAxis() {
        return axis;
    }

    public String getGroupingField() {
        return groupingField;
    }
}
