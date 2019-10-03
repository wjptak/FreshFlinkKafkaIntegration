ThisBuild / resolvers ++= Seq(
  "Apache Development Snapshot Repository" at "https://repository.apache.org/content/repositories/snapshots/",
  Resolver.mavenLocal
)

name := "Fresh Flink Integration"

version := "0.1"

organization := "com.freshmail"

ThisBuild / scalaVersion := "2.12.8"

val flinkVersion = "1.9.0"

val flinkDependencies = Seq(
  "org.apache.flink" %% "flink-scala" % flinkVersion % Compile,
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion % Compile,
  "org.apache.flink" %% "flink-connector-kafka" % flinkVersion,
  "org.apache.flink" % "flink-avro-confluent-registry" % flinkVersion
)

val otherDependencies = Seq(
  "io.confluent" % "kafka-avro-serializer" % "5.0.0",
  "org.apache.kafka" % "kafka-clients" % "2.0.0"
)

lazy val root = (project in file(".")).
  settings(
    libraryDependencies ++= flinkDependencies ++ otherDependencies
  )

assembly / mainClass := Some("com.freshmail.Job")

// make run command include the provided dependencies
Compile / run  := Defaults.runTask(Compile / fullClasspath,
  Compile / run / mainClass,
  Compile / run / runner
).evaluated

// stays inside the sbt console when we press "ctrl-c" while a Flink programme executes with "run" or "runMain"
Compile / run / fork := true
Global / cancelable := true

// exclude Scala library from assembly
assembly / assemblyOption  := (assembly / assemblyOption).value.copy(includeScala = false)

// Generate Avro schemas
// sourceGenerators in Compile += (avroScalaGenerateSpecific in Compile).taskValue