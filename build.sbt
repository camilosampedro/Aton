name := """aton"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  //jdbc,
  cache,
  ws,
  specs2 % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "mysql" % "mysql-connector-java" % "5.1.34",
  "com.typesafe.play" %% "play-slick" % "1.1.0",
  "org.webjars" %% "webjars-play" % "2.5.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.1.0",
  "fr.janalyse"  %% "janalyse-ssh" % "0.9.19" % "compile"
)

libraryDependencies ++= Seq(
  "org.webjars" % "bootstrap" % "3.3.6",
  "org.webjars" % "jquery" % "2.2.1"
)

resolvers += "JAnalyse Repository" at "http://www.janalyse.fr/repository/"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
