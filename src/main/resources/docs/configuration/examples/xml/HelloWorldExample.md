# Example:  Hello world!

## Watchr Config:  config.json
    {
		"plots" : {
			"files" : {
				"fileName": "performance_*",
				"type" : "xml"
			},
			"plot" : [
				{
					"name" : "My First Plot",
					"dataLines" : [
						{
							"name" : "My First Data Line",
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
								"getKey" : "value",
								"unit" : "seconds"
							}
						}
					]
				}
			]
		}
    }

## Data File:  performance\_day\_1.xml

    <?xml version="1.0"?>
    <performance-report date="2021-04-05T22:21:21" name="nightly_run_2021-04-05">
        <timing name="measurement" value="1.0"/>
    </performance-report>

## Watchr's Actions:

1. Locate the x data point
 - at the first element of the XML (`"getPath": "*"`)
 - with the element name performance-report (`"getElement" : "performance-report"`)
 - and attribute "date" (`"getKey" : "date"`).
 - The X value will be located within the "date" attribute.
2. Locate the y data point
 - at the path "nightly\_run\_2021-04-05 / measurement" (`"getPath": "*/measurement"`)
 - with the element name "performance-report" or "timing" (`"getElement" : "performance-report|timing"`)
 - and the guiding path attribute "name" (`"getPathAttribute": "name"`).
 - The Y value will be located within the "value" attribute. (`"getKey" : "value"`).
3. A single point `x="2021-04-05T22:21:21", y="1.0"` will be extracted from the XML.
4. Finally, Watchr will plot a single 2D scatter plot graph, with the X axis "date" and the Y axis "seconds" (as dictated by the `unit` fields).  The single point `x="2021-04-05T22:21:21", y="1.0"` will be plotted.

## Command Line:

	watchr start 
    watchr config config.json
    watchr add performance_day_1.xml
    watchr run