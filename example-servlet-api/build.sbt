

libraryDependencies ++= Seq(
    "javax.servlet" % "servlet-api" % "2.4" % "provided")
    
// include web plugin settings in this project
seq(webSettings :_*)

// and use this version of jetty for jetty run
libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "7.3.1.v20110307" % "container"

publishArtifact := false

seq(scalariformSettings: _*)