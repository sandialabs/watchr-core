#What is Watchr?<a name="what-is-watchr"></a>

Watchr is a Java application designed to help you gather and visualize data over time.  

#Some example use cases for Watchr<a name="use-cases"></a>

* You have a nightly job that gathers metrics at the end of the process, and you'd like to visualize those metrics, with automatic updates to the graphs as more data is added.
* You have a large collection of timestamped report files that are all formatted similarly, and you would like to read all of these and generate graphs from the data.

#What are Watchr's features?<a name="features"></a>

* Watchr's flexible JSON configuration file allows you to create graphs tailored to your use case.
* Watchr graphs can be organized hierarchically (for example, if you have data on child processes that subdivide the value of a longer parent process).
* Automatically generate derivative data from your dataset, such as rolling averages.
* User-defined rules allow Watchr to react to new changes in incoming data.
* Easily filter out bad data points - no database access required
 
#What is Watchr compatible with?<a name="compatibility"></a>

**Inputs**

* Watchr can read data out of any arbitrary XML file.
* Watchr can read data out of any arbitrary JSON file.

**Graphing**

* Watchr uses [**plotly.js**](https://plotly.com/javascript/) to display its graphs by exporting to static HTML pages at the end of executing Watchr.
* [**Log an issue**](https://gitlab-ex.sandia.gov/SEMS/watchr-core) if you would like Watchr to support another graphing library.

**Final Display**

* Watchr can be executed on the command line as a standalone Java application.
* Watchr is also available as a [**Jenkins plugin**](https://gitlab-ex.sandia.gov/SEMS/jenkins_performance_plugin).
* [**Log an issue**](https://gitlab-ex.sandia.gov/SEMS/watchr-core) if you would like to see Watchr integrated with your favorite data dashboard.
 
#How can I build Watchr?<a name="build-steps"></a>

To get the basic functionality that Watchr provides, you will need to build **watchr-core** at minimum:

    git clone git@github.com:sandialabs/watchr-core.git watchr-core
    cd watchr-core

If building watchr-core as a standalone application:

    mvn package appassembler:assemble
    sh target/appassembler/bin/watchr

If building watchr-core as a dependency for another Maven jar:

    mvn clean install
