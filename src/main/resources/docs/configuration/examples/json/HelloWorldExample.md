# Example:  Hello world!

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

## Data File:  performance\_day\_1.json

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

## Watchr's Actions:

1. Locate the x data point
 - at the specified path in the JSON (`"getPath": "performance-report/nightly_run_*"`)
 - with the specified key (`"getKey" : "date"`)
 - The X value will be located within the "date" attribute.
2. Locate the y data point
 - at the specified path in the JSON (`"getPath": "performance-report/nightly_run_*/measurement"`)
 - with the specified key (`"getKey" : "value"`)
 - The Y value will be located within the "value" attribute. (`"getKey" : "value"`).
3. A single point `x="2021-04-05T22:21:21", y="1.0"` will be extracted from the XML.
4. Finally, Watchr will plot a single 2D scatter plot graph, with the X axis "date" and the Y axis "seconds" (as dictated by the `unit` fields).  The single point `x="2021-04-05T22:21:21", y="1.0"` will be plotted.

## Command Line:

	watchr config.json performance_day_1.json