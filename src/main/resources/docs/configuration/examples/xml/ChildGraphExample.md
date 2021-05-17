# Example:  Child Graph Example

## Overview

With a few extra settings, it is possible to configure Watchr to gather additional child data and create new graphs based on that child information, using the configuration you have specified to gather data to construct the parent graphs.  This is most useful in cases where your initial report contains hierarchical-but-similar data.

## Watchr Config:  config.json
    {
		"plots" : {
			"files" : {
				"fileName": "performance",
				"type" : "xml"
			},
			"plot" : [
				{
					"autoname" : {
						"useProperty" : "y/path"
					},
					"dataLines" : [
						{
							"name" : "Data Line",
							"x" : {
								"getElement" : "performance-report",
								"getPath": "*",
								"getKey" : "date",
								"unit" : "date"
							},
							"y" : {
								"getElement" : "performance-report|timing",
								"getPath": "*/measurement_A",
								"getPathAttribute": "name",
								"getKey" : "value",
								"unit" : "seconds",
								"strategy" : {
									"getFirstMatchOnly" : "false",
									"recurseChildGraphs" : "true"
								}
							}
						}
					]
				}
			]
		}
    }

## Data File:  performance.xml

    <?xml version="1.0"?>
    <performance-report date="2021-04-05T22:21:21" name="nightly_run_2021-04-05">
        <timing name="measurement_A" value="100.0">
			<timing name="measurement_A1" value="25.0">
				<timing name="measurement_A1A" value="15.0"/>
				<timing name="measurement_A1B" value="10.0"/>
			</timing>
			<timing name="measurement_A2" value="75.0">
		</timing>
    </performance-report>

## What this example demonstrates:

Note that the data file "performance.xml" contains hierarchical "timing" elements.  The "name" attributes are different at each level, and the hierarchy indicates the granularity of the measurements in the report.  In other words, the top-level measurement, "measurement\_A", records a value of "100.0."  It has two children - "measurement\_A1" and "measurement\_A2", with subdivide the measurement of "100.0" into "25.0" and "75.0", respectively.  Additionally, "measurement\_A1" contains two children of its own - "measurement\_A1A" and "measurement\_A1B".  These two children subdivide the value of "25.0" into "15.0" and "10.0."

To gather all of this information and also maintain the hierarchical relationship, we can employ the following new sections in our config file:

* **The "strategy" section**  - The "strategy" section provides guidance to Watchr in ambiguous situations.
 * **getFirstMatchOnly** - This tells Watchr what to do if the data file contains two possible candidates for data extraction (i.e. two children at the same level that satisfy the data extraction conditions laid out in the configuration file).  If true, only the first match will be extracted; if false, all matches will receive their own graph.
 * **recurseChildGraphs** - This tells Watchr whether or not to look for child data once the data extraction conditions for the parent have been specified.  The same extraction conditions will be applied to the child (though "getPath" will now be treated as a prefix for the child path, and not a match for the entire path).  It is not currently possible to change the specification for extracting child graphs using this method. 
* **The "autoName" section** - Since more than one plot will be created (and mostly auto-generated off of the parent's configuration), it may not be appropriate to apply the same "name" field to all child plots.  Instead, you can auto-name the plot based on some property of the parent/child graphs.  In this example, the "useProperty" field is set to "y/path", indicating that each graph will be named after the "getPath" value of the "y" extractor.

## Notes 

It is not possible to set "recurseChildGraphs" to true for more than one extractor at the same level.

## Command Line:

	watchr config.json performance.xml