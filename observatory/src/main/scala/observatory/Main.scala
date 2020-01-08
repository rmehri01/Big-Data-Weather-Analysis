package observatory

import org.apache.log4j.{Level, Logger}
import Extraction._
import Interaction._
import org.apache.spark.rdd.RDD

import awscala._, s3._

object Main extends App {
  // aws s3 setup
  implicit val s3 = S3.at(Region.Tokyo)
  val bucket: Bucket = s3.createBucket("weather-photos-rmehri01")

  // Reduces number of log files for the grader
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

  // Main for temperatures
  val yearlyData = for {
    year <- (1975 to 2015).par
    resultOfYearlyAvgRecords = sparkLocationYearlyAverageRecords(sparkLocateTemperatures(year, "stations.csv", s"$year.csv"))
  } yield (year, resultOfYearlyAvgRecords)

  val tempColors = Seq(
    (60d, Color(255, 255, 255)),
    (32d, Color(255, 0, 0)),
    (12d, Color(255, 255, 0)),
    (0d, Color(0, 255, 255)),
    (-15d, Color(0, 0, 255)),
    (-27d, Color(255, 0, 255)),
    (-50d, Color(33, 0, 107)),
    (-60d, Color(0, 0, 0))
  )

  val generateImage = (year: Year, inputTile: Tile, data: RDD[(Location, Temperature)]) => {
    val (x, y, zoom) = (inputTile.x, inputTile.y, inputTile.zoom)
    //      val dir = new java.io.File(s"s3a://weatherdata-analytics/$year-$zoom")
    println(s"Tiling: $year-$zoom-$x-$y.png")
    val result = sparkTile(data, tempColors, inputTile)
    println(s"Done Tiling: $year-$zoom-$x-$y.png")
    //      if (!dir.exists()) dir.mkdirs()
    //      result.output(new java.io.File(s"s3a://weatherdata-analytics/$year-$zoom-$x-$y.png"))
    println(s"Result.outputting: $year-$zoom-$x-$y.png")
    val output = result.output(new java.io.File(s"$year-$zoom-$x-$y.png"))
    println(s"Done Result: $year-$zoom-$x-$y.png")
    println(s"Putting in bucket: $year-$zoom-$x-$y.png")
    bucket.put(s"$year-$zoom-$x-$y.png", output)
    println(s"Done putting in bucket: $year-$zoom-$x-$y.png")
    ()
  }

  generateTiles(yearlyData.toIndexedSeq, generateImage)
}
