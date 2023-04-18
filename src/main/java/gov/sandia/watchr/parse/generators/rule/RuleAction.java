/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.generators.rule;

public enum RuleAction {
    DELETE_SOME,
    DELETE_EVERYTHING,
    FAIL_DATABASE,
    FAIL_PLOT,
    WARN_PLOT
}
