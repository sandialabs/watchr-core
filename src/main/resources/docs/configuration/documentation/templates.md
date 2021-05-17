# Templates

If you find yourself specifying configurations for the same type of plot over and over, with only minor modifications to the plot configuration each time, you may want to take advantage of **templates**.

Template plots are similar in concept to superclasses in programming.  If a plot configuration is specified as a template, any property of that template can be inherited by another plot configuration that only declares what it wishes to override.

##Example

	"plot" : [
		{
			"template" : "plot_template",
			"category" : "cpu-time-max",
			"dataLines" : [
				{
					"name" : "Data Line",
					"x" : {
						"getPath": "*",
                        "getElement" : "performance-report",
                        "getKey" : "date",
						"unit" : "timestamp"
					},
					"y" : {
						"getElement" : "performance-report|timing",
                        "getPath": "nightly_run_*/*/*",
						"getPathAttribute": "name",
                        "getKey" : "cpu-time-max",							
						"unit" : "seconds"
					}
				}
			]
		},

        {
			"inherit" : "plot_template",
			"category" : "cpu-time-min",
			"dataLines" : [
				{
					"y" : {
						"getKey" : "cpu-time-min"
					}
				}
			]
		}


* Note that there are two plot configurations in the "plot" array.
* **"template" in the first plot configuration** - In the first plot configuration, the "template" property appears to indicate that this configuration can be used a template for other plots. 
* **"inherit" in the second plot configuration** - In the second configuration, note that the field "inherit" refers to our plot template by name.  Also note that this second configuration is much shorter, only overriding the property that it needs to (the y extractor's "getKey" field).

##Template override behavior

Depending on the type of property being overridden, templates will behave in different ways:

 - Data lines are overridden according to *their position in the JSON config's data line array*
 - Derivative lines are overridden based on matching line type.
 - Metadata is overridden based on matching name.
 - Rules are overridden based on matching conditional statements.