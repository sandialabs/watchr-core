# Example:  Derivative Line Example

## Overview

Given a [**data line**](dataLines.html) on a Watchr graph, additional "derivative lines" can be added.  These lines are derivative in the sense that they do not contain their own independent data, but rather are processed versions of data from an original line.  One example of a derivative line would be the rolling average of data from an original line, shown below.

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
	        },
	        {
	            "date" : "2021-04-08T22:21:21",
	            "measurement" : "4.0"
	        },
	        {
	            "date" : "2021-04-09T22:21:21",
	            "measurement" : "1.0"
	        },
	        {
	            "date" : "2021-04-10T22:21:21",
	            "measurement" : "-1.0"
	        }
	    ]
	}

## What this example demonstrates:

* **The "derivativeLines" section** - The derivative lines section contains one derivative line specification, which is a rolling average line.  A rolling average line contains no data of its own, but rather reflects a rolling average relative to another line containing real data.  For rolling averages, you must also specify the range window for the rolling average to be calculated, which in this example is 3.

## Command Line:

	watchr config.json performance_data.json