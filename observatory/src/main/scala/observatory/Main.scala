package observatory

import org.apache.log4j.{Level, Logger}
//import org.apache.spark.sql._
//import org.apache.spark.sql.functions._


object Main extends App {
  // Reduces number of log files for the grader
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

  // Spark setup
  import org.apache.spark.sql.SparkSession

  val spark: SparkSession =
    SparkSession
      .builder()
      .appName("Weather Analysis")
      .master("local[4]")
      .getOrCreate()

  // Main

}
