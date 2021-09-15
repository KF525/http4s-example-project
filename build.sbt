// ThisBuild acts as a special subproject name that you can use to define default value for the build.
ThisBuild / version      := "0.1"
ThisBuild / scalaVersion := "2.13.5"

lazy val doobieVersion = "0.12.1"
lazy val http4sVersion = "0.21.20"
lazy val circeVersion = "0.13.0"
lazy val monixVersion = "3.3.0"
lazy val pureconfigVersion = "0.15.0"

// root is the parent and we don't package it. It aggregates other subprojects.
lazy val root = project.in(file("."))
  .settings(name := "compound_poem")
  .aggregate(compound_poem_app)

//lazy val compound_poem_ui = project.in(file("ui"))

lazy val compound_poem_app = project.in(file("application"))
  .settings(
    libraryDependencies ++= Seq(
      "io.circe"                %% "circe-generic"           % circeVersion,
      "io.circe"                %% "circe-parser"            % circeVersion,
      "org.http4s"              %% "http4s-dsl"              % http4sVersion,
      "org.http4s"              %% "http4s-blaze-server"     % http4sVersion,
      "org.http4s"              %% "http4s-blaze-client"     % http4sVersion,
      "org.http4s"              %% "http4s-circe"            % http4sVersion,
      "io.monix"                %% "monix-catnap"            % monixVersion,
      "io.monix"                %% "monix"                   % monixVersion,
      "com.github.pureconfig"   %% "pureconfig"              % pureconfigVersion,
      "com.github.pureconfig"   %% "pureconfig-http4s"       % pureconfigVersion,
      "org.tpolecat"            %% "doobie-core"             % doobieVersion,
      "org.tpolecat"            %% "doobie-postgres"         % doobieVersion,
      "org.tpolecat"            %% "doobie-hikari"           % "0.13.1",
      "org.scalactic"           %% "scalactic"               % "3.2.7",
      "org.flywaydb"             % "flyway-core"             % "7.8.2",
      "org.scalatestplus"       %% "mockito-3-4"             % "3.2.7.0" % Test,
      "org.scalatest"           %% "scalatest"               % "3.2.7" % Test,
      "org.mockito"             % "mockito-core"             % "2.7.19" % Test,
      "org.tpolecat"            %% "doobie-specs2"           % doobieVersion % Test, //support for typechecking
      "org.tpolecat"            %% "doobie-scalatest"        % doobieVersion % Test
    )
  )