libraryDependencies ++= List (
  "com.typesafe.slick" %% "slick" % "latest.integration",
  "org.scalatest" %% "scalatest" % "latest.integration",
  "org.xerial" % "sqlite-jdbc" % "latest.integration",
  "org.slf4j" % "slf4j-simple" % "latest.integration",
  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
  "org.specs2" %% "specs2" % "latest.integration" % "test"
)

val platformDependentLibraries: List[ModuleID] = {
  val archString = sys.props.get("os.arch").get
  val archBit =
    if (archString.contains("64")) "64"
    else "32"
  val osName = sys.props.get("os.name").get
  val osString =
    if (osName.contains("Linux")) "linux"
    else if (osName.contains("Windows")) "win"
    else if (osName.contains("Mac")) "mac"
    else
      throw new RuntimeException("Unknown operating system.")
  val platform = osString + archBit
  val qtJambiVersion = if (platform == "win64") "4.5.2_01" else "4.6.3.2"
    ("net.sf.qtjambi" % "qtjambi" % qtJambiVersion) ::
    ("net.sf.qtjambi" % s"qtjambi-base-$platform" % qtJambiVersion) ::
    ("net.sf.qtjambi" % s"qtjambi-platform-$platform" % qtJambiVersion) :: Nil
}

libraryDependencies ++= platformDependentLibraries

resolvers += "Qt Jambi Maven2 Repository" at "http://repository.qt-jambi.org/nexus/content/repositories/public/"

parallelExecution in Test := false
