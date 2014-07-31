libraryDependencies ++= List (
  "com.typesafe.slick" %% "slick" % "latest.integration",
  "org.scalatest" %% "scalatest" % "latest.integration",
  "org.xerial" % "sqlite-jdbc" % "latest.integration",
  "org.slf4j" % "slf4j-simple" % "latest.integration",
  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
  "org.specs2" %% "specs2" % "latest.integration" % "test"
)

parallelExecution in Test := false
