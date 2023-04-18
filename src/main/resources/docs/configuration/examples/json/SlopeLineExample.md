# Example: Slope Derivative Line

## Overview

Given a [**data line**](dataLines.html) on a Watchr graph, additional "slope" derivative lines can be added.  A slope line is expressed with the classic sloped line equation, *y = mx + b*.

Alternately, x can be treated as the dependent variable by expressing the equation as *x = my + b*.

A slope derivative line is not "derivative" in the mathematical sense, but rather in the sense that it relies on primary data line's data points to determine where to place its own data points on the independent axis.

## Watchr Config:  config.json
	{
	    "plots" : {
	        "files" : {
	            "fileName": "performance_*",
	            "type" : "json"
	        },
	        "plot" : [
	            {
	                "name" : "Slope Lines",
	                "dataLines" : [
	                    {
	                        "name" : "Data Line",
	                        "x" : {
	                            "getPath": "performance-reports/*",
	                            "getKey" : "time",
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
	                                "type" : "slope",
	                                "name" : "Slope Line",
	                                "y" : "( 2.0 * x ) + 5.0"
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
	            "time" : "0.0",
	            "measurement" : "1.0"
	        },
	        {
	            "time" : "1.0",
	            "measurement" : "2.0"
	        },
	        {
	            "time" : "2.0",
	            "measurement" : "3.0"
	        }
	    ]
	}

## What this example demonstrates:

* **The "derivativeLines" section** - The derivative lines section contains one sloped line.
 * It uses a slope of 2.0 and a y-intercept of 5.0.
 * It will have x-points at 0.0, 1.0, and 2.0, because these are the x-data points collected from performance\_data.json.
 * It will have y-points at 5.0, 7.0, and 9.0, according to the given sloped line equation.

## Command Line:

	watchr start 
    watchr config config.json
    watchr add performance_data.json
    watchr run