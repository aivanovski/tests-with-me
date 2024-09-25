Global / onChangedBuildSource := ReloadOnSourceChanges

// Main tasks:
// compile
// clean
// fastOptJS

lazy val tyrianVersion    = "0.11.0"
lazy val scala3Version    = "3.4.1"
lazy val circeVersion     = "0.14.5"
lazy val munitVersion     = "0.7.29"
lazy val organizationName = "com.testswithme"

lazy val webApiScala = (crossProject(JSPlatform, JVMPlatform) in file("web-api-scala"))
  .settings(
    name         := "web-api-scala",
    scalaVersion := scala3Version,
    organization := organizationName
  )

lazy val webFrontendCommon = (crossProject(JSPlatform, JVMPlatform) in file("web-frontend-common"))
  .settings(
    name         := "web-frontend-common",
    scalaVersion := scala3Version,
    organization := organizationName,
    libraryDependencies ++= Seq(
      "org.scalameta" %%% "munit" % munitVersion % Test
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )
  .dependsOn(webApiScala)

lazy val webFrontend = (project in file("web-frontend"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name         := "web-frontend",
    scalaVersion := scala3Version,
    organization := organizationName,
    libraryDependencies ++= Seq(
      "io.indigoengine" %%% "tyrian-io"     % tyrianVersion,
      "io.circe"        %%% "circe-core"    % circeVersion,
      "io.circe"        %%% "circe-parser"  % circeVersion,
      "io.circe"        %%% "circe-generic" % circeVersion
    ),
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
    semanticdbEnabled := true,
    autoAPIMappings   := true
  )
  .dependsOn(webFrontendCommon.js, webApiScala.js)
