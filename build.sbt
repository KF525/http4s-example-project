name := "restaurant-reservation"

version := "0.1"

scalaVersion := "2.13.5"

lazy val doobieVersion = "0.12.1"
lazy val http4sVersion = "0.21.20"
lazy val circeVersion = "0.13.0"
lazy val monixVersion = "3.3.0"

libraryDependencies ++= Seq(
  "io.circe"                %% "circe-generic"           % circeVersion,
  "io.circe"                %% "circe-parser"            % circeVersion,
  "org.http4s"              %% "http4s-dsl"              % http4sVersion,
  "org.http4s"              %% "http4s-blaze-server"     % http4sVersion,
  "org.http4s"              %% "http4s-blaze-client"     % http4sVersion,
  "org.http4s"              %% "http4s-circe"            % http4sVersion,
  "io.monix"                %% "monix-catnap"            % monixVersion,
  "io.monix"                %% "monix"                   % monixVersion,
  "com.github.pureconfig"   %% "pureconfig"              % "0.15.0",
  "com.github.pureconfig"   %% "pureconfig-http4s"       % "0.15.0",
  "org.tpolecat"            %% "doobie-core"             % doobieVersion,
  "org.tpolecat"            %% "doobie-postgres"         % doobieVersion,
  "org.tpolecat"            %% "doobie-hikari"           % "0.13.1",
  "org.scalactic"           %% "scalactic"               % "3.2.7",
  "org.scalatest"           %% "scalatest"               % "3.2.7" % Test,
  "org.mockito"             % "mockito-core"             % "2.7.19" % Test,
  "org.tpolecat"            %% "doobie-specs2"           % doobieVersion % Test, // Specs2 support for typechecking statements.
  "org.tpolecat"            %% "doobie-scalatest"        % doobieVersion % Test
)