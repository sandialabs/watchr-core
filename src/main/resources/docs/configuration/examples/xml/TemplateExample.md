# Example:  Template Example

## Overview

If you find yourself specifying configurations for the same type of plot over and over, with only minor modifications to the plot configuration each time, you may want to take advantage of **templates**.

Template plots are similar in concept to superclasses in programming.  If a plot configuration is specified as a template, any property of that template can be inherited by another plot configuration that only declares what it wishes to override.

## Watchr Config:  config.json

	{
	    "plots" : {
	        "files" : {
	            "fileName": "performance",
	            "type" : "xml"
	        },
	        "categories" : [
	            "cpu-time-max",
	            "cpu-time-min",
	            "cpu-time-sum"
	        ],
	        "plot" : [
	            {
	                "name" : "Plot for cpu-time-max",
	                "category" : "cpu-time-max",
	                "template" : "plot_template",
	                "dataLines" : [
	                    {
	                        "name" : "Data Line",
	                        "template" : "line_template",
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
	                            "getKey" : "cpu-time-max",
	                            "unit" : "seconds"
	                        }
	                    }
	                ]
	            },{
	                "name" : "Plot for cpu-time-min",
					"category" : "cpu-time-min",
	                "inherit" : "plot_template",
					"dataLines" : [
						{
	                        "inherit" : "line_template",
							"y" : {
								"getKey" : "cpu-time-min"
							}
						}
					]
				}, {
					"inherit" : "plot_template",
	                "name" : "Plot for cpu-time-sum",
					"category" : "cpu-time-sum",
					"dataLines" : [
						{
	                        "inherit" : "line_template",
							"y" : {
								"getKey" : "cpu-time-sum"
							}
						}
					]
				}
	        ]
	    },
	    "graphDisplay" : {
	        "exportMode" : "perCategory"
	    }
	}

## Data File:  performance.xml

	<?xml version="1.0"?>
	<performance-report date="2021-04-05T22:21:21" name="nightly_run_2021-04-05">
	    <timing name="measurement" cpu-time-max="2.0" cpu-time-min="1.0"/>
	</performance-report>

## What this example demonstrates:

* Note that there are two plot configurations in the "plot" array.
 * **"template" in the first plot configuration** - In the first plot configuration, the "template" property appears to indicate that this configuration can be used a template for other plots. 
 * **"inherit" in the second plot configuration** - In the second configuration, note that the field "inherit" refers to our plot template by name.  Also note that this second configuration is much shorter, only overriding the property that it needs to (the y extractor's "getKey" field).
* Note also the differences between the two configured data lines. 
 * **"template" in the first data line** - As with the plot, the data line is labeled as a template, thanks to the appearance of the "template" property. This will allow other data lines to inherit from this data line and build on its properties.
 * **"inherit" in the second data line** - For the second data line, its configuration is much shorter, because it uses the "inherit" property to get all of the properties from the parent line "line_template." The only thing that has changed for this second line is the "getKey" property.
* **"exportMode" in the "graphDisplay" section** - Because we are collecting graphs in two different categories, we set the "graphDisplay" section to export per category.

## Command Line:

	watchr start
    watchr config config.json
    watchr add performance.xml
    watchr run