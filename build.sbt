// ThisBuild acts as a special subproject name that you can use to define default value for the build.
ThisBuild / version      := "0.1"
ThisBuild / scalaVersion := "2.13.5"

lazy val http4sVersion          = "0.21.4"
lazy val circeVersion           = "0.13.0"
lazy val doobieVersion          = "0.13.4"
lazy val pureconfigVersion      = "0.15.0"
lazy val zioVersion             = "1.0.12"
lazy val zioInteropCatsVersion  = "2.1.4.0"
lazy val catsEffectVersion      = "2.2.0"

// root is the parent and we don't package it. It aggregates other subprojects.
lazy val root = project.in(file("."))
  .settings(name := "compound_poem")
  .aggregate(compound_poem_app)

//TODO: Add organization structure?
lazy val compound_poem_app = project.in(file("application"))
  .settings(
    libraryDependencies ++= dependencies
  )

val dependencies = Seq(
  "io.circe"                %% "circe-generic"           % circeVersion,
  "io.circe"                %% "circe-parser"            % circeVersion,
  "org.http4s"              %% "http4s-dsl"              % http4sVersion,
  "org.http4s"              %% "http4s-blaze-server"     % http4sVersion,
  "org.http4s"              %% "http4s-blaze-client"     % http4sVersion,
  "org.http4s"              %% "http4s-circe"            % http4sVersion,
  "org.http4s"              %% "http4s-core"             % http4sVersion,
  "org.typelevel"           %% "cats-effect"             % catsEffectVersion,
  "com.github.pureconfig"   %% "pureconfig"              % pureconfigVersion,
  "com.github.pureconfig"   %% "pureconfig-http4s"       % pureconfigVersion,
  "com.github.pureconfig"   %% "pureconfig-cats"         % pureconfigVersion,
  "dev.zio"                 %% "zio"                     % zioVersion,
  "dev.zio"                 %% "zio-interop-cats"        % zioInteropCatsVersion,
  "org.tpolecat"            %% "doobie-core"             % doobieVersion,
  "org.tpolecat"            %% "doobie-postgres"         % doobieVersion,
  "org.tpolecat"            %% "doobie-hikari"           % doobieVersion,
  "org.scalactic"           %% "scalactic"               % "3.2.7",
  "org.flywaydb"             % "flyway-core"             % "7.8.2",
  "dev.zio"                 %% "zio-test"                % zioVersion % Test,
  "org.scalatestplus"       %% "mockito-3-4"             % "3.2.7.0" % Test,
  "org.scalatest"           %% "scalatest"               % "3.2.7" % Test,
  "org.mockito"             % "mockito-core"             % "2.7.19" % Test,
  "org.tpolecat"            %% "doobie-specs2"           % doobieVersion % Test, //support for typechecking
  "org.tpolecat"            %% "doobie-scalatest"        % doobieVersion % Test
)

lazy val options = Seq(
  "-unchecked",
  "-feature",
  "-language:existentials",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-language:higherKinds",
  "-Xfatal-warnings",
  "-Xmacro-settings:materialize-derivations",
  "utf8"
)