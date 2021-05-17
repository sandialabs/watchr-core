#Watchr Manual

**Manual by [Elliott Ridgway](mailto:emridgw@sandia.gov)**

## Introduction to Watchr

1. [**What is Watchr?**](intro.html#what-is-watchr)
2. [**Some example use cases for Watchr**](intro.html#use-cases)
3. [**What are Watchr's features?**](intro.html#features) 
4. [**What is Watchr compatible with?**](intro.html#compatibility) 
5. [**How can I build Watchr?**](intro.html#build-steps)

## Configuration Documentation

Watchr uses a single JSON configuration file to drive all of its operations.  This configuration file is responsible for everything from parsing your data files to generating graphs.

1. [**plots**](configuration/documentation/plots.html)
    1. [**files**](configuration/documentation/files.html)
    2. [**categories**](configuration/documentation/categories.html)
    3. [**plot**](configuration/documentation/plot.html)
		1. [**dataLines**](configuration/documentation/dataLines.html)
			1. [**x/y extractors**](configuration/documentation/extractor.html)
				1. [**strategy**](configuration/documentation/strategy.html)
			2. [**derivativeLines**](configuration/documentation/derivativeLines.html)
			3. [**metadata**](configuration/documentation/metadata.html)
				1. [**extractor**](configuration/documentation/extractor.html)
					1. [**strategy**](configuration/documentation/strategy.html) 
		2. [**rules**](configuration/documentation/rules.html)
		3. [**autoname**](configuration/documentation/autoname.html)
		4. [**filter**](configuration/documentation/filter.html)
		5. [**fileFilter**](configuration/documentation/fileFilter.html)		
2. [**graphDisplay**](configuration/documentation/graphDisplay.html)

## Configuration Examples

### Getting Started

1. **Hello World Example** : [**XML**](configuration/examples/xml/HelloWorldExample.html) | [**JSON**](configuration/examples/json/HelloWorldExample.html)
2. **Multiple Points on a Data Line** : [**XML**](configuration/examples/xml/ThreePointsOnOneLine.html)

### Topics

1. **Categories Example** : [**XML**](configuration/examples/xml/Categories.html)
2. **Child Graph Example** : [**XML**](configuration/examples/xml/ChildGraphExample.html)
3. **Derivative Line Example** : [**JSON**](configuration/examples/json/DerivativeLineExample.html)
4. **File Filter Example** : [**JSON**](configuration/examples/json/FileFilterExample.html)
5. **Legend Example** : [**JSON**](configuration/examples/json/LegendExample.html)
6. **Metadata Example**: [**XML**](configuration/examples/xml/MetadataExample.html)
7. **Point Filter Example**: [**JSON**](configuration/examples/json/PointFilterExample.html)
8. **Rule Example** : [**JSON**](configuration/examples/json/RuleExample.html)
9. **Template Example** : [**XML**](configuration/examples/xml/TemplateExample.html)

## Other

- [**Rule Expression Glossary**](configuration/documentation/ruleExpressionGlossary.html)
- [**Special JSON Array Syntax**](configuration/documentation/jsonArrays.html)
- [**Templates**](configuration/documentation/templates.html)

## Source Code

* **watchr-core** : [**Gitlab**](https://gitlab-ex.sandia.gov/SEMS/watchr-core)
* **watchr-jenkins** : [**Gitlab**](https://gitlab-ex.sandia.gov/SEMS/watchr-jenkins) | [**Github**](https://github.com/sandialabs/watchr-jenkins)
* **watchr-import** : [**Gitlab**](https://gitlab-ex.sandia.gov/SEMS/watchr-import)