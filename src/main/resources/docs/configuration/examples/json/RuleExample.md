# Example:  Rule Example

## Overview

Watchr can respond to arbitrary logical rules based on the state of the data on the graphs that Watchr is monitoring.  Every rule is composed of two parts - a "condition" and an "action."  The condition specified when Watchr should activate the rule.  The action specifies what Watchr should do if the rule is activated.

For a more detailed listing of all possible conditions and actions that Watchr recognizes, refer to the [**Rule Expression Glossary.**](../../documentation/ruleExpressionGlossary.html)

## Watchr Config:  config.json
	{
	    "plots" : {
	        "files" : {
	            "fileName": "performance_*",
	            "type" : "json"
	        },
	        "plot" : [
	            {
	                "name" : "My First Plot",
	                "dataLines" : [
	                    {
	                        "name" : "My First Data Line",
	                        "x" : {
	                            "getPath": "performance-reports/*",
	                            "getKey" : "date",
	                            "unit" : "date",
								"strategy" : {
									"getFirstMatchOnly" : "false"
								}
	                        },
	                        "y" : {
	                            "getPath": "performance-reports/*",
	                            "getKey" : "measurement",
	                            "unit" : "seconds",
								"strategy" : {
									"getFirstMatchOnly" : "false",
                                    "iterateWith" : "x"
								}
	                        },
	                        "derivativeLines" : [
								{
									"type" : "average",
									"range" : 3
								}
							]
	                    }
	                ],
					"rules" : [
						{
							"condition" : "dataLine > average",
							"action" : "fail"
						}
					]
	            }
	        ]
	    }
	}

## Data File:  performance\_data.json

	{
	    "performance-reports" : [
	        {
	            "date" : "2021-04-05T22:21:21",
	            "measurement" : "1.0"
	        },
	        {
	            "date" : "2021-04-06T22:21:21",
	            "measurement" : "2.0"
	        },
	        {
	            "date" : "2021-04-07T22:21:21",
	            "measurement" : "3.0"
	        }
	    ]
	}

## What this example demonstrates:

* **The "rules" section** - The rules section of the configuration is an array of JSON objects.  Each JSON object should contain a "condition" and an "action" field.  In this example, the "condition" field is "dataLine > average", which specifies that the condition to respond to is the main data line of any graph rising above the rolling average line on the same graph.  The "action" is "fail," which will color the background of the graph red and declare to any outside non-Watchr processes that have a handle to the database that the graph is in a "failure" state.
* **The "derivativeLines" section** - The derivative lines section contains one derivative line specification, which is a rolling average line.  A rolling average line contains no data of its own, but rather reflects a rolling average relative to another line containing real data.  For rolling averages, you must also specify the range window for the rolling average to be calculated, which in this example is 3.

## Command Line:

	watchr config.json performance_data.json