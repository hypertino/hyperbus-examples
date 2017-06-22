crossScalaVersions := Seq("2.12.1", "2.11.8")

scalaVersion in Global := "2.11.8"

lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  organization := "com.hypertino",  
  resolvers ++= Seq(
    Resolver.sonatypeRepo("public")
  ),
  libraryDependencies += {
    macroParadise
  }
)

// external dependencies
lazy val hyperbus = "com.hypertino" %% "hyperbus" % "0.2-SNAPSHOT"
lazy val hyperFacade = "com.hypertino" %% "hyper-facade" % "0.2-SNAPSHOT"
lazy val scalaMock = "org.scalamock" %% "scalamock-scalatest-support" % "3.5.0" % "test"
lazy val logback = "ch.qos.logback" % "logback-classic" % "1.1.8"
lazy val quasiQuotes = "org.scalamacros" %% "quasiquotes" % "2.1.0" cross CrossVersion.binary
lazy val macroParadise = compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

lazy val hyperbusZMQ = "com.hypertino" %% "hyperbus-t-zeromq" % "0.2-SNAPSHOT"
lazy val hyperbusKafka = "com.hypertino" %% "hyperbus-t-kafka" % "0.2-SNAPSHOT"

lazy val serviceControl = "com.hypertino"        %% "service-control"             % "0.3-SNAPSHOT"
lazy val serviceConfig = "com.hypertino"        %% "service-config"              % "0.2-SNAPSHOT"

lazy val `example-facade` = project in file("example-facade") settings (
    commonSettings,
    name := "example-facade",
    libraryDependencies ++= Seq(
      hyperFacade,
      hyperbusZMQ,
      hyperbusKafka,
      logback
    )
  )

lazy val `example-service` = project in file("example-service") enablePlugins Raml2Hyperbus settings (
    commonSettings,
    name := "example-service",
    libraryDependencies ++= Seq(
      hyperbus,
      serviceControl,
      serviceConfig,
      hyperbusZMQ,
      logback
    ),
    ramlHyperbusSource := file("example-service.raml"),
    ramlHyperbusPackageName := "com.hypertino.exampleservice.api"
)

lazy val `example-root` = project.in(file(".")) aggregate (
    `example-facade`,
    `example-service`
  )