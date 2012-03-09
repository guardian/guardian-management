Guardian Management
===================

This project contains various helpers to ease administrative management
of production java apps.

Our policy is that each app exposes its user facing pages on a sub url,
and administrative pages on `/management`. So, for example, `content-api.war`
when deployed to a container has the actual api under `/content-api/api` and
the management pages on `/content-api/management`.

The `/management` url should return a html page that links to all management
pages.

This simple framework aims to make it simple to generate the standard management
pages and easy to create new app-specific ones.


Note for the Old Skool
======================

The old web-framework specific management libraries, can be found in the
[3.x](https://github.com/guardian/guardian-management/tree/3.x) branch of this project.
There is no intention to maintain these further.

Going from CACTI to GANGLIA
=====================
There is a major, breaking, change when you use the ganglia (5.x) version of guardian management.

(1) When you initialise your StatusPage object, you need to name it as being the status page of your app:

`new StatusPage("My App Name", Metrics.....)`

So for example identity has 2 status pages:

`new StatusPage("identity-webapp", Metrics.....)`
`new StatusPage("identity-api", Metrics.....)`

(2) Creating A Metric

You need to provide four arguments, with an optional fifth.

`new TimingMetic("group", "name", "title", "description")`

    GROUP: this is used as a logical grouping. Think of it as a noun. For example "emails"
    NAME: This is a verb related to the noun defined in group. For example "sent"
    TITLE: This is a text string used to title the graphs. Keep it short. For example "Emails Sent"
    DESCRIPTION: This is a longer description of the metric. Used on the hover over.
    For example "Total number of emails sent"

Group and Name are munged together in ganglia to give the actual name used on the console.
There is another console in Graphite (graphing tool) which will allow subdivision on group.
Allowing you to see all the "emails" metrics and to create mash ups of all of these.
Using the groups as it's top level option.

As a rule use underscores (_) not hyphens (-) as delimiters.

So in scala:

`object SuccessfulEmails extends CountMetric("emails", "sent", "Emails Sent", "Number of emails sent")`

The fifth metric is the field master. This takes Option[Metric] with a default of None.
This is used to indicate that the metric you are creating is a child of a
another metric. The parameter is the metric you wish to be a child of

For example. Imagine there is a time taken for a request metric:

`object Requests extends TimingMetric("requests", "api", "Api Requests Timer", "Total number and time taken for API request")`

You may have a mongoDB requests metric, which is a child of the overall HTTP request:

`object MongoRequests extends TimingMetric("requests", "mongodb", "Mongodb Requests", "Mongo request timer", Some(Requests))`

This allows Ganglia to give the proportion of time of the HTTP request taken talking to mongo. You could have another:

`object MongoRequests extends TimingMetric("requests", "oracle", "Oracle Requests", "Oracle request timer", Some(Requests))`

Ganglia would now be able to show you propertions of each DB request as a proportion of total HTTP time.


(3) Types of metric

    - Timing: standard timing metric, takes number of events over a time period.
    - Count: Constantly incrementing counter.
    - Gauge: Count at a particular point in time.

(4) Extra docs:

See guardian google docs for "Web Applications Specifications"



Getting Started (Servlet API)
===============

The management pages are web framework agnostic: they use their own mini
framework, blatently inspired/ripped off from [lift](http://www.liftweb.net).

It is incorrect to assume the Serlvet API as has been done in previous
versions of this library because not all the web frameworks we want to use
are necessarily written on a Serlvet API stack, eg Play, Blueeyes. In practice,
the management library is web framework agnostic to a large extent but you will
need a small adapter library to adapt the request and response abstractions to
the HTTP interface in use in your desired web framework.

Add the dependency to your build
-----------------------------------

In sbt 0.7.x:

    val guardianGithubSnapshots = "Guardian Github Snapshots" at "http://guardian.github.com/maven/repo-snapshots"
    val guManagement = "com.gu" %% "management-servlet-api" % "5.7-SNAPSHOT"

In your build.sbt for sbt 0.10:

    resolvers += "Guardian Github Snapshots" at "http://guardian.github.com/maven/repo-snapshots"
    libraryDependencies += "com.gu" %% "management-servlet-api" % "5.7-SNAPSHOT"

As of 4.1-SNAPSHOT, scala 2.8.1 and 2.9.0-1 are supported.

Add the management filter to your web.xml
--------------------------------------------

To avoid any conflict with your choice of web framework, the managment
pages are implemented as a filter. So, for example:

    <!DOCTYPE web-app PUBLIC
            "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
            "http://java.sun.com/dtd/web-app_2_3.dtd" >

    <web-app>

        <filter>
            <filter-name>managementFilter</filter-name>
            <filter-class>com.gu.management.example.MyAppManagementFilter</filter-class>
        </filter>

        <filter-mapping>
            <filter-name>managementFilter</filter-name>
            <url-pattern>/management/*</url-pattern>
        </filter-mapping>

    </web-app>

The filter-class is a class that you are going to implement.

Implement the filter class
-----------------------------

Your filter class should derive from `com.gu.management.ManagementFilter` and implement
the pages member:

    class MyAppManagementFilter extends ManagementFilter {
      lazy val pages =
        new DummyPage() ::
        new ManifestPage() ::
        new Switchboard(Switches.all) ::
        new StatusPage(TimingMetrics.all) ::
        Nil
    }

Even for mostly java projects, you'll need to write your management pages in scala. However,
things like timing metrics and switches have a java-friendly interface and are usable from java.

Look at the example!
-----------------------

The [example project](https://github.com/guardian/guardian-management/tree/master/example-servlet-api) has
a filter set up and uses some switches and timing metrics from both scala and java.

    $ git clone git@github.com:guardian/guardian-management.git
    $ cd guardian-management
    $ ./sbt010
    > project example-servlet-api
    > container:start

Try the following URLs locally:

 * http://localhost:8080/java-app
 * http://localhost:8080/scala-app
 * http://localhost:8080/management
 * http://localhost:8080/management/switchboard

Also, enable the `take-it-down` switch and retry `/scala-app` and `/java-app`.

The application also has very simple custom management page, but the best thing to do if you want to write your
own management pages is to look at how the pre-defined ones are implemented: a simple readonly page to look at is
the
[status page](https://github.com/guardian/guardian-management/blob/master/management/src/main/scala/com/gu/management/StatusPage.scala),
and a more complex page that supports POSTs is
[the switchboard](https://github.com/guardian/guardian-management/blob/master/management/src/main/scala/com/gu/management/switchables.scala).
