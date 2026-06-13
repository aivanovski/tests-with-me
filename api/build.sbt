ThisBuild / scalaVersion := "3.6.2"

lazy val api = project
  .in(file("."))
  .settings(
    name := "tests-with-me-api-client",
    Compile / unmanagedSourceDirectories := Seq(baseDirectory.value / "main" / "src"),
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % "0.14.10",
      "io.circe" %% "circe-generic" % "0.14.10",
      "io.circe" %% "circe-parser" % "0.14.10",
      "com.squareup.okhttp3" % "okhttp" % "4.7.2"
    ),
    assembly / mainClass := Some("Main"),
    assembly / assemblyJarName := "api-client.jar",
    assembly / assemblyOutputPath := target.value / "api-client.jar"
  )
