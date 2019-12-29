package observatory

import observatory.Interaction._
import org.junit.Test

trait InteractionTest extends MilestoneSuite {
  private val milestoneTest = namedMilestoneTest("interactive visualization", 3) _

  @Test def `tile picture`(): Unit = {
    val t1: (Location, Temperature) = (Location(0, 0), -27d)
    val t2: (Location, Temperature) = (Location(-39, -60), 60d)
    val t3: (Location, Temperature) = (Location(90, -180), 0d)
    val temps = Seq(t1, t2, t3)

    val points = Seq(
      (60d, Color(255, 255, 255)),
      (32d, Color(255, 0, 0)),
      (12d, Color(255, 255, 0)),
      (0d, Color(0, 255, 255)),
      (-15d, Color(0, 0, 255)),
      (-27d, Color(255, 0, 255)),
      (-50d, Color(33, 0, 107)),
      (-60d, Color(0, 0, 0)))

    val result = tile(temps, points, Tile(0, 0, 0))
    result.output(new java.io.File("target/some-image2.png"))
  }

  @Test def `generated tiles test`(): Unit = {
    generateTiles(Iterable((2001, 1), (2002, 2)), (year: Year, tile: Tile, data: Int) => println((year, tile, data)))
  }

  @Test def `test main`(): Unit = {
    import Extraction._
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

    val generateImage = (year: Year, inputTile: Tile, data: Iterable[(Location, Temperature)]) => {
      val (x, y, zoom) = (inputTile.x, inputTile.y, inputTile.zoom)
      val dir = new java.io.File(s"target/temperatures/$year/$zoom/$x-$y")
      val result = tile(data, tempColors, inputTile)
      if (!dir.exists()) dir.mkdir() // maybe s
      result.output(new java.io.File(s"target/temperatures/$year/$zoom/$x-$y.png"))
      ()
    }

    //    generateTiles(yearlyData, generateImage)

    //    generateImage(1975, Tile(0,0,0), yearlyData.head._2.collect())

    val result = tile(yearlyData.head._2.collect(), tempColors, Tile(0, 0, 0))
    result.output(new java.io.File("target/some-image2.png"))
  }
}
