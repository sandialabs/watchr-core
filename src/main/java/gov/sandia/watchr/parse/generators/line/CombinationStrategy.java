/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.generators.line;

public enum CombinationStrategy {
    ONE_X_ONE_Y,
    ONE_X_MULTIPLE_Y,
    MULTIPLE_X_ONE_Y,
    MULTIPLE_COMBINATORIAL,
    MULTIPLE_ITERATE
}
