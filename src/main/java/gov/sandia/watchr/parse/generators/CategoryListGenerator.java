/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.generators;

import java.util.List;

import gov.sandia.watchr.config.CategoryConfiguration;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.parse.WatchrParseException;

public class CategoryListGenerator extends AbstractGenerator<CategoryConfiguration> {

    ////////////
    // FIELDS //
    ////////////

    private final IDatabase db;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public CategoryListGenerator(IDatabase db) {
        super(db.getLogger());
        this.db = db;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void generate(CategoryConfiguration config, List<WatchrDiff<?>> diffs) throws WatchrParseException {
        if(diffed(config, diffs, DiffCategory.CATEGORIES)) {
            db.getCategories().addAll(config.getCategories());
        }
    }
}
