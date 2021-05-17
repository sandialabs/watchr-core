# Example:  File Filter Example

## Overview

In addition to the "fileName" field provided in the top-level "files" section of a Watchr config JSON file, which provides a whitelist to Watchr of files to look at for performance report data, you can also provide a filename whitelist filter at the level of individual plots.  This is achieved with the "fileFilter" section, demonstrated below.

## Watchr Config:  config.json
	{
	    "plots" : {
	        "files" : {
	            "fileName": "*",
	            "type" : "json"
	        },
	        "plot" : [
	            {
	                "name" : "My First Plot",
	                "fileFilter" : {
	                    "namePattern" : "ReadThisFile*"
	                },                
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

## Data File:  ReadThisFile.json
	
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

## Data File:  DoNotRead.json

	{
	    "performance-report" : {
	        "nightly_run_2021-04-05" : {
	            "date" : "2021-04-12T12:34:56",
	            "measurement": {
	                "value":"2.0"
	            }
	        }
	    }
	}

## What this example demonstrates:

* **The "fileFilter" section** - The fileFilter section contains a "namePattern" field, which allows you to specify a pattern for allowing only files that contain certain characters to be used for gathering plot data.  The "namePattern" field recognizes simple wildcards (i.e. asterisks).

In the example above, the configuration file will read data from the "ReadThisFile.json" file, but NOT the "DoNotRead.json" file, according to the "namePattern" value of "ReadThisFile*".

## Command Line:

	watchr config.json <folder containing data JSON files>