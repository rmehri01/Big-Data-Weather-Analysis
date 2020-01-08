package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import org.apache.spark.rdd.RDD
import Visualization._

/**
  * 3rd milestone: interactive visualization
  */
object Interaction extends InteractionInterface {

  /**
    * @param tile Tile coordinates
    * @return The latitude and longitude of the top-left corner of the tile, as per http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
    */
  def tileLocation(tile: Tile): Location = {
    import Math._
    val (x, y, z) = (tile.x, tile.y, tile.zoom)
    val lat = atan(sinh(PI - y.toDouble / pow(2, z) * 2 * PI)) * 180d / PI
    val lon = x.toDouble / pow(2, z) * 360d - 180d
    Location(lat, lon)
  }

  /**
    * @param temperatures Known temperatures
    * @param colors       Color scale
    * @param tile         Tile coordinates
    * @return A 256×256 image showing the contents of the given tile
    */
  def tile(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)], tile: Tile): Image = {
    val pixels = for {
      y <- (0 until 128).par
      x <- (0 until 128).par
      curLocation = tileLocation(Tile(tile.x * 128 + x, tile.y * 128 + y, tile.zoom + 7))
      predictedColor = interpolateColor(colors, predictTemperature(temperatures, curLocation))
    } yield Pixel(predictedColor.red, predictedColor.green, predictedColor.blue, 127)

    Image(128, 128, pixels.toArray).scale(2)
  }

  def sparkTile(temperatures: RDD[(Location, Temperature)], colors: Iterable[(Temperature, Color)], tile: Tile): Image = {
    temperatures.persist()
    val pixels = for {
      y <- 0 until 128
      x <- 0 until 128
      curLocation = tileLocation(Tile(tile.x * 128 + x, tile.y * 128 + y, tile.zoom + 7))
      predictedColor = interpolateColor(colors, sparkPredictTemperature(temperatures, curLocation))
    } yield Pixel(predictedColor.red, predictedColor.green, predictedColor.blue, 127)

    Image(128, 128, pixels.toArray).scale(2)
  }

  /**
    * Generates all the tiles for zoom levels 0 to 3 (included), for all the given years.
    *
    * @param yearlyData    Sequence of (year, data), where `data` is some data associated with
    *                      `year`. The type of `data` can be anything.
    * @param generateImage Function that generates an image given a year, a zoom level, the x and
    *                      y coordinates of the tile and the data to build the image from
    */
  def generateTiles[Data](
                           yearlyData: Iterable[(Year, Data)],
                           generateImage: (Year, Tile, Data) => Unit
                         ): Unit = {
    @scala.annotation.tailrec
    def loop(currentZoom: Int): Unit = {
      for (y <- 0 until Math.pow(2, currentZoom).toInt; x <- 0 until Math.pow(2, currentZoom).toInt)
        yearlyData.par.foreach { case (year, data) => generateImage(year, Tile(x, y, currentZoom), data) }
      currentZoom match {
        case 3 =>
        case _ => loop(currentZoom + 1)
      }
    }

    loop(0)
  }

}
