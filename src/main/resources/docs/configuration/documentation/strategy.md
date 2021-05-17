# Configuration Documentation: strategy

The "strategy" section allows a Watchr user to control the extractor's behavior in ambiguous situations.  In particular, this is the main mechanism within Watchr config files for activating parent-child relationships between graphs.

## Properties

* **getFirstMatchOnly** : *boolean* : If set to true, only the first matching data value will be returned if an extractor locates more than one match.  If set to false, each matching data value will be returned, which may result in more than one graph being created, depending on the extractor's setup.
* **recurseChildGraphs** : *boolean* : If true, once an extractor finds a matching data value for the given extraction instructions, it will recursively look for more child data values, using the same extraction instructions (except for "getPath", since we are now looking at child elements deeper within the data file).  All child elements that match the extraction instructions will be returned, and the resulting graphs will have a parent-child relationship with the original data once it is placed into Watchr's database.

## Child Elements

None.

## Parent Element

- [**extractor**](extractor.html)