
name := """aton"""
version := "0.2.1"
scalaVersion := "2.11.8"

// Angular2 - Typescript options
incOptions := incOptions.value.withNameHashing(true)
updateOptions := updateOptions.value.withCachedResolution(cachedResoluton = true)

lazy val root = (project in file(".")).enablePlugins(PlayScala, DebianPlugin, RpmPlugin, LinuxPlugin, UniversalPlugin, WindowsPlugin, JDKPackagerPlugin, JavaServerAppPackaging, SbtWeb)

parallelExecution in Test := false

fork in Test := true

fork in run := true

// Linux builds
// - Debian
maintainer in Linux := "Camilo Sampedro <camilo.sampedro@udea.edu.co>"
packageSummary in Linux := "Aton, Laboratory Administrator"

// - RHEL
rpmRelease := "0.2.1"

rpmVendor := "co.edu.udea"
rpmUrl := Some("http://projectaton.github.io/AtonLab")
rpmLicense := Some("GPL 3.0")
packageDescription := "Computer laboratory administrator with useful tools. Built on top of SSH."

// Repositories
resolvers ++= Seq(
  "JAnalyse Repository" at "http://www.janalyse.fr/repository/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
)

// Server side dependencies
libraryDependencies ++= Seq(
  "jp.t2v" %% "play2-auth" % "0.14.2",
  "jp.t2v" %% "play2-auth-test" % "0.14.2" % "test",
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  // https://mvnrepository.com/artifact/com.h2database/h2
  "com.h2database" % "h2" % "1.4.193",
  "com.typesafe.play" %% "play-slick" % "2.0.2",
  "org.webjars" %% "webjars-play" % "2.5.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.2",
  "com.typesafe.akka" %% "akka-actor" % "2.4.5",
  "fr.janalyse" %% "janalyse-ssh" % "0.9.19" % "compile",
  "org.mindrot" % "jbcrypt" % "0.3m",

  // Tests dependencies
  specs2,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
  "org.mockito" % "mockito-all" % "1.10.19",
  // Password salting
  "org.mindrot" % "jbcrypt" % "0.3m"
)

/*// Web dependencies
libraryDependencies ++= {
  val ngVersion = "2.4.3"
  Seq(
    // Angular 2 dependencies
    "org.webjars.npm" % "angular2" % "2.0.0-beta.21",
    "org.webjars.npm" % "angular__common" % ngVersion,
    "org.webjars.npm" % "angular__compiler" % ngVersion,
    "org.webjars.npm" % "angular__core" % ngVersion,
    "org.webjars.npm" % "angular__forms" % ngVersion,
    "org.webjars.npm" % "angular__platform-browser-dynamic" % ngVersion,
    "org.webjars.npm" % "angular__platform-browser" % ngVersion,
    "org.webjars.npm" % "angular__http" % ngVersion,
    "org.webjars.npm" % "angular__router" % "3.2.0",
    "org.webjars.npm" % "ng2-idle" % "1.0.0-alpha.18",
    "org.webjars.npm" % "js-cookie" % "2.1.3",
    //"org.webjars.npm" % "angular__router" % ngVersion,
    "org.webjars.npm" % "systemjs" % "0.19.39",
    "org.webjars.npm" % "rxjs" % "5.0.0-beta.12",
    "org.webjars.npm" % "es6-promise" % "3.1.2",
    "org.webjars.npm" % "es6-shim" % "0.35.1",
    "org.webjars.npm" % "reflect-metadata" % "0.1.8",
    "org.webjars.npm" % "zone.js" % "0.6.25",
    "org.webjars.npm" % "core-js" % "2.4.1",
    "org.webjars.npm" % "symbol-observable" % "1.0.1",

    // Traceur transpiler
    "org.webjars.npm" % "traceur" % "0.0.111",

    // Typescript
    "org.webjars.npm" % "typescript" % "2.2.1",

    // tslint dependency
    "org.webjars.npm" % "tslint-eslint-rules" % "3.4.0",
    "org.webjars.npm" % "tslint-microsoft-contrib" % "4.0.0",
    "org.webjars.npm" % "codelyzer" % "0.0.28",
    "org.webjars.npm" % "types__jasmine" % "2.2.26-alpha" % "test",

    // tests
    "org.webjars.npm" % "jasmine-core" % "2.4.1",

    // Bootstrap
    "org.webjars" % "bootstrap" % "3.3.6",
    // Semantic UI with Angular2
    "org.webjars" % "Semantic-UI" % "2.2.2",
    "org.webjars.npm" % "ng-semantic" % "1.1.13",
    // JQuery
    "org.webjars" % "jquery" % "3.1.0",
    // RequireJS
    "org.webjars" % "requirejs" % "2.2.0",
    // Ionicons
    "org.webjars" % "ionicons" % "2.0.1"
  )
}*/

//dependencyOverrides += "org.webjars.npm" % "minimatch" % "3.0.0"

/*// the typescript typing information is by convention in the typings directory
// It provides ES6 implementations. This is required when compiling to ES5.
typingsFile := Some(baseDirectory.value / "typings" / "index.d.ts")

// use the webjars npm directory (target/web/node_modules ) for resolution of module imports of angular2/core etc
resolveFromWebjarsNodeModulesDir := true

// use the combined tslint and eslint rules plus ng2 lint rules
(rulesDirectories in tslint) := Some(List(
  tslintEslintRulesDir.value,
  ng2LintRulesDir.value
))*/

//routesGenerator := InjectedRoutesGenerator


/*
 * UI Build Scripts
 */

val Success = 0 // 0 exit code
val Error = 1 // 1 exit code

PlayKeys.playRunHooks <+= baseDirectory.map(UIBuild.apply)

def runScript(script: String)(implicit dir: File): Int = Process(script, dir) !

def uiWasInstalled(implicit dir: File): Boolean = (dir / "node_modules").exists()

def runNpmInstall(implicit dir: File): Int =
  if (uiWasInstalled) Success else runScript("npm install")

def ifUiInstalled(task: => Int)(implicit dir: File): Int =
  if (runNpmInstall == Success) task
  else Error

def runProdBuild(implicit dir: File): Int = ifUiInstalled(runScript("npm run build-prod"))

def runDevBuild(implicit dir: File): Int = ifUiInstalled(runScript("npm run build"))

def runUiTests(implicit dir: File): Int = ifUiInstalled(runScript("npm run test-no-watch"))

lazy val `ui-dev-build` = TaskKey[Unit]("Run UI build when developing the application.")

`ui-dev-build` := {
  implicit val UIroot = baseDirectory.value / "ui"
  if (runDevBuild != Success) throw new Exception("Oops! UI Build crashed.")
}

lazy val `ui-prod-build` = TaskKey[Unit]("Run UI build when packaging the application.")

`ui-prod-build` := {
  implicit val UIroot = baseDirectory.value / "ui"
  if (runProdBuild != Success) throw new Exception("Oops! UI Build crashed.")
}

lazy val `ui-test` = TaskKey[Unit]("Run UI tests when testing application.")

`ui-test` := {
  implicit val UIroot = baseDirectory.value / "ui"
  if (runUiTests != 0) throw new Exception("UI tests failed!")
}

`ui-test` <<= `ui-test` dependsOn `ui-dev-build`

dist <<= dist dependsOn `ui-prod-build`

stage <<= stage dependsOn `ui-prod-build`

test <<= (test in Test) dependsOn `ui-test`