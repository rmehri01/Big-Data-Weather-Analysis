package observatory

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql
import org.apache.spark.sql.SparkSession

object SparkSessionSetup {
  Logger.getLogger("org").setLevel(Level.WARN)

  val spark: sql.SparkSession = SparkSession.builder
    .master("local[*]")
    .appName("Word Count")
    .config("spark.some.config.option", "some-value")
    .getOrCreate()

  spark.conf.set("spark.sql.shuffle.partitions", "4")

}
