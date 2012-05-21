Management Internal
===================

This management shim allows the management libraries to run separate of any container or framework, on an second
port, making use of an internally implemented HTTP server.

The library will use the first port that it can bind to from 18080 to 18099 and, if permissions allow it will write
a file out to /var/run/ports/<name>.port containing the number of the port that it has bound to.

To make use of this library you should call ManagementServer.start and ManagementServer.shutdown at appropriate points
in your application.

```scala
val appName = "My Application Name"
val handler = new ManagementHandler {
    def pages = List() // return the list of pages here
}
ManagementServer.start(handler, appName)
```

```scala
// shutdown to unregister the port binding properly
ManagementServer.shutdown()
```

There is a plugin for Play 2.0 available in the management-play library.