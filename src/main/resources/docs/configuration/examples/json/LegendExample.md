# Example:  Legend Example

## Overview

Adding a legend to Watchr graphs is easy.  All you need to do is add `"legend": "true"` to the "plot" section of your config, as shown below. 

## Watchr Config:  config.json
	{
	    "plots" : {
	        "files" : {
	            "fileName": "performance_*",
	            "type" : "json"
	        },
	        "plot" : [
	            {
	                "name" : "Plot with Legend",
	                "legend" : "true",
	                "dataLines" : [
	                    {
	                        "name" : "Data Line",
	                        "x" : {
	                            "getPath": "performance-report/nightly_run_*",
	                            "getKey" : "date",
	                            "unit" : "date"
	                        },
	                        "y" : {
	                            "getPath": "performance-report/nightly_run_*/measurement",
	                            "getKey" : "value",
	                            "unit" : "seconds"
	                        }
	                    }
	                ]
	            }
	        ]
	    }
	}

## Data File:  performance\_data.json
	
	{
	    "performance-report" : {
	        "nightly_run_2021-04-05" : {
	            "date" : "2021-04-05T22:21:21",
	            "measurement": {
	                "value":"1.0"
	            }
	        }
	    }
	}

## Command Line:

	watchr config.json performance_data.json