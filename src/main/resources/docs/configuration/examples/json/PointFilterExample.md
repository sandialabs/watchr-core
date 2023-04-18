# Example:  Point Filter Example

## Overview

The "filters" section of a Watchr config file provides a convenient way to selectively filter "bad" points that have already entered the database of previously-parsed data files.

## Watchr Config:  config.json
	{
	    "plots" : {
	        "files" : {
	            "fileName": "performance_*",
	            "type" : "json"
	        },
	        "plot" : [
	            {
	                "name" : "Point Filter Plot",
	                "dataLines" : [
	                    {
	                        "name" : "Data Line",
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
	                        }
	                    }
	                ],
					"filters": {
                        "type" : "point",
                        "expression" : "x == 2021-04-07T22:21:21 && y == -1.0"
                        "policy" : "blacklist"
					}
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

* **The "filters" section** : This filters section contains an expression ```x == 2021-04-07T22:21:21 && y == -1.0```, which will match on all points that have an x-value of "2021-04-07T22:21:21" AND have a y-value of "-1.0." Because the filter policy is set to "blacklist," these points will be removed if they match. Finally, because of this filter block's placement in the configuration file, it will apply to all data lines on the plot. If another "plot" configuration block were to be added, however, the "filter" block would not apply to that newly-added plot. 

## Command Line:

	watchr start
    watchr config config.json
    watchr add performance_data.json
    watchr run