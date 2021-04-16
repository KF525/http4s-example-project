name := "restaurant-reservation"

version := "0.1"

scalaVersion := "2.13.5"

libraryDependencies ++= Seq(
  "org.http4s"             %% "http4s-dsl"              % "0.21.20",
  "org.http4s"             %% "http4s-blaze-server"     % "0.21.20",
  "org.http4s"             %% "http4s-blaze-client"     % "0.21.20",
  "org.http4s"             %% "http4s-circe"            % "0.21.20",
  "io.circe"               %% "circe-generic"           % "0.13.0",
  "io.circe"               %% "circe-parser"            % "0.13.0",
  "io.monix"               %% "monix-catnap"            % "3.3.0",
  "io.monix"               %% "monix"                   % "3.3.0",
  "org.scalactic"          %% "scalactic"               % "3.2.7",
  "org.scalatest"          %% "scalatest"               % "3.2.7" % Test,
  "org.mockito"             % "mockito-core"            % "2.7.19" % Test
)