libraryDependencies ++= List(
  "com.typesafe.slick" %% "slick" % "latest.integration",
  "org.scalatest" %% "scalatest" % "latest.integration"
)

val qtJambi = RootProject(uri("http://git.gitorious.org/qt-jambi/qtjambi-community.git"))

val imageTagger = project.in(file(".")).dependsOn(qtJambi)
