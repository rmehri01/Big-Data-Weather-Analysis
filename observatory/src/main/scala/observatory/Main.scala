package observatory

import org.apache.log4j.{Level, Logger}
import Extraction._
import Interaction._
import org.apache.spark.rdd.RDD

object Main extends App {
  // Reduces number of log files for the grader
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

  // Main for temperatures
  val yearlyData = for {
    year <- 1975 to 1975
    resultOfYearlyAvgRecords = sparkLocationYearlyAverageRecords(sparkLocateTemperatures(year, "/stations.csv", s"/$year.csv"))
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
      val dir = new java.io.File(s"target/temperatures/$year/$zoom/$x-$y")
      val result = tile(data.collect(), tempColors, inputTile)
      if (!dir.exists()) dir.mkdir() // maybe s
      result.output(new java.io.File(s"target/temperatures/$year/$zoom/$x-$y.png"))
      ()
    }

    generateTiles(yearlyData, generateImage)
}
