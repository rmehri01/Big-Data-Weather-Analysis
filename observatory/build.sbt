course := "capstone"
assignment := "observatory"

scalaVersion := "2.12.10"

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-encoding", "UTF-8",
  "-unchecked",
  "-Xlint",
)

libraryDependencies ++= Seq(
  "com.sksamuel.scrimage" %% "scrimage-core" % "2.1.8", // for visualization
  "org.apache.spark" %% "spark-sql" % "2.4.5" % "provided",
  //  "com.github.seratch" %% "awscala" % "0.8.+",
)

libraryDependencies += "com.google.cloud" % "google-cloud-storage" % "1.103.1"
//libraryDependencies += "org.scalactic" %% "scalactic" % "3.1.0"
//libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.0" % "test"

testOptions in Test += Tests.Argument(TestFrameworks.JUnit, "-a", "-v", "-s")

parallelExecution in Test := false // So that tests are executed for each milestone, one after the other

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}