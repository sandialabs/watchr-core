# Example:  Scatter plot

This example demonstrates plot types of type **scatter plot**.  A scatter plot is the type of plot you are probably most familiar with - a two-dimensional Cartesian axis with a series of points that represent x,y coordinates, with a line drawn between the points.

The "scatter plot" plot type is explicitly set using the "type" property belonging to a "plot" element in the configuration below.  However, if no plot type is specified, Watchr will default to creating scatter plots.

## Watchr Config:  config.json
    {
		"plots" : {
			"files" : {
				"fileName": "performance_*",
				"type" : "xml"
			},
			"plot" : [
				{
					"name" : "Scatter Plot Example",
                    "type" : "scatterPlot",
					"dataLines" : [
						{
							"name" : "Data Line",
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

## Data File:  performance\_day\_2.xml

    <?xml version="1.0"?>
    <performance-report date="2021-04-06T12:21:21" name="nightly_run_2021-04-06">
        <timing name="measurement" value="2.0"/>
    </performance-report>

## Data File:  performance\_day\_3.xml

    <?xml version="1.0"?>
    <performance-report date="2021-04-07T02:21:21" name="nightly_run_2021-04-07">
        <timing name="measurement" value="3.0"/>
    </performance-report>

## Watchr's Actions:

For each file beginning with "performance\_*" (see `"fileName": "performance_*"`)...

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
3. Three points will be extracted, one from each XML:
 - `x="2021-04-05T22:21:21", y="1.0"`
 - `x="2021-04-06T12:21:21", y="2.0"`
 - `x="2021-04-07T02:21:21", y="3.0"`
4. Finally, Watchr will plot a single 2D scatter plot graph, with the X axis "date" and the Y axis "seconds" (as dictated by the `unit` fields).  The single point `x="2021-04-05T22:21:21", y="1.0"` will be plotted.

## Command Line:

	watchr start
    watchr config config.json
    watchr add <folder containing three performance files>
    watchr run