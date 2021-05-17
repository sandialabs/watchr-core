# Example:  Template Example

## Overview

If you find yourself specifying configurations for the same type of plot over and over, with only minor modifications to the plot configuration each time, you may want to take advantage of **templates**.

Template plots are similar in concept to superclasses in programming.  If a plot configuration is specified as a template, any property of that template can be inherited by another plot configuration that only declares what it wishes to override.

## Watchr Config:  config.json

	{
	    "plots" : {
	        "files" : {
	            "fileName": "performance_*",
	            "type" : "xml"
	        },
	       	"plot" : [
				{
                    "name" : "Template Plot",
					"template" : "plot_template",
					"category" : "cpu-time-max",
					"dataLines" : [
						{
							"name" : "Data Line",
							"x" : {
								"getPath": "*",
		                        "getElement" : "performance-report",
		                        "getKey" : "date",
								"unit" : "timestamp"
							},
							"y" : {
								"getElement" : "performance-report|timing",
		                        "getPath": "nightly_run_*/*/*",
								"getPathAttribute": "name",
		                        "getKey" : "cpu-time-max",							
								"unit" : "seconds"
							}
						}
					]
				},
		
		        {
                    "name" : "Inheriting Plot",
					"inherit" : "plot_template",
					"category" : "cpu-time-min",
					"dataLines" : [
						{
							"y" : {
								"getKey" : "cpu-time-min"
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
	    <timing name="measurement" cpu_time_max="2.0" cpu_time_min="1.0"/>
	</performance-report>

## What this example demonstrates:

* Note that there are two plot configurations in the "plot" array.
* **"template" in the first plot configuration** - In the first plot configuration, the "template" property appears to indicate that this configuration can be used a template for other plots. 
* **"inherit" in the second plot configuration** - In the second configuration, note that the field "inherit" refers to our plot template by name.  Also note that this second configuration is much shorter, only overriding the property that it needs to (the y extractor's "getKey" field).

## Command Line:

	watchr config.json performance.xml