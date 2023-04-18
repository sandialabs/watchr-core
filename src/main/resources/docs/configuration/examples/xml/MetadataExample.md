# Example:  Metadata Example

## Overview

Arbitrary maps of metadata can be applied to individual points on Watchr graphs.  This is particularly useful if you have secondary, non-numeric information that you would like to record and visualize along with your main data line.  Watchr displays point-specific metadata when you hover the mouse over the associated point on the resulting graph.

## Watchr Config:  config.json
	{
	    "plots" : {
	        "files" : {
	            "fileName": "performance_*",
	            "type" : "xml"
	        },
	        "plot" : [
	            {
	                "name" : "Metadata Example",
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
	                            "getPath": "*/measurement",
	                            "getPathAttribute": "name",
	                            "getKey" : "value",
	                            "unit" : "seconds"
	                        },
							"metadata": [
								{
									"name": "branch",
									"extractor" : {
										"getElement" : "performance-report|metadata",
										"getPath" : "nightly_run_2021-04-05/branch",
										"getPathAttribute" : "name|key",
										"getKey" : "value"
									}
								}
							]
	                    }
	                ]
	            }
	        ]
	    }
	}

## Data File:  performance.xml

	<?xml version="1.0"?>
	<performance-report date="2021-04-05T22:21:21" name="nightly_run_2021-04-05">
	    <timing name="measurement" value="1.0"/>
	    <metadata key="branch" value="master"/>
	</performance-report>

## What this example demonstrates:

* **The "metadata" section**  - This section is responsible for capturing any additional metadata information associated with each point found by the "x" and "y" extractors above.
 * **name** - The name of your piece of metadata.
 * **extractor** - Similar to the "x" and "y" extractor section, this section dictates to Watchr how it will locate your metadata information within the data file.  In this example, both "performance-report" and "metadata" are allowed XML elements, and both "name" and "key" attributes are used to traverse the XML - we need both "name" and "key" because one of the two is missing from each element in the XML hierarchy.  Finally, the "value" attribute is used as the key for the metadata, and its value will be the extracted piece of metadata.  In this example, the extracted piece of metadata will be "master", which pairs with the key "branch," provided in the "name" field of the Watchr config JSON.

*Note:* On a given data file, if multiple x/y pairings are extracted, but only one piece of metadata information is extracted, that metadata will be applied uniformly across all x/y pairings.  Conversely, if multiple pieces of metadata are extracted for a single x/y pairing, and those pieces of metadata are duplicates (i.e. using the same "name" value), the point will only contain the most recently extracted metadata information, and earlier extractions will be overwritten.

## Command Line:

	watchr start
    watchr config config.json
    watchr add performance.xml
    watchr run