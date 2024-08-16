Global / onChangedBuildSource := ReloadOnSourceChanges

// Main tasks:
// compile
// clean
// fastOptJS

lazy val tyrianVersion    = "0.11.0"
lazy val scala3Version    = "3.4.1"
lazy val circeVersion     = "0.14.5"
lazy val organizationName = "com.testswithme"


lazy val webApiScala = (crossProject(JSPlatform, JVMPlatform) in file("web-api-scala"))
  .settings(
    name         := "web-api-scala",
    scalaVersion := scala3Version,
    organization := organizationName
  )