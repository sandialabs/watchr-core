# Example:  Point Filter Example

## Overview

The "filter" section of a Watchr config file provides a convenient way to selectively filter "bad" points that have already entered the database of previously-parsed data files.

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
					"filter": {
						"x": [ "2021-04-07T22:21:21" ],
						"y": [ "-1.0" ]
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

* **The "filter" section** - A filter section contains two JSON arrays - "x" and "y", for filtering x and y data points respectively.  In this example, all data points where the x value  equals "2021-04-07T22:21:21" will be hidden from view.  Likewise, all data points where the y value  equals "-1.0" will be hidden from view.

## Command Line:

	watchr config.json performance_data.json