libraryDependencies ++= List (
  "com.typesafe.slick" %% "slick" % "latest.integration",
  "org.scalatest" %% "scalatest" % "latest.integration",
  "net.sf.qtjambi" % "qtjambi" % "4.6.3",
  "net.sf.qtjambi" % "qtjambi-base-linux64" % "4.6.3",
  "net.sf.qtjambi" % "qtjambi-platform-linux64" % "4.6.3"
)

resolvers += "Qt Jambi Maven Repository" at "http://qtjambi.sourceforge.net/maven2/"