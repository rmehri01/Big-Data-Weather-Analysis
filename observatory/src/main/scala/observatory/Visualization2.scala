package observatory

import com.sksamuel.scrimage.{Image, Pixel}

/**
  * 5th milestone: value-added information visualization
  */
object Visualization2 extends Visualization2Interface {

  /**
    * @param point (x, y) coordinates of a point in the grid cell
    * @param d00   Top-left value
    * @param d01   Bottom-left value
    * @param d10   Top-right value
    * @param d11   Bottom-right value
    * @return A guess of the value at (x, y) based on the four known values, using bilinear interpolation
    *         See https://en.wikipedia.org/wiki/Bilinear_interpolation#Unit_Square
    */
  def bilinearInterpolation(
                             point: CellPoint,
                             d00: Temperature,
                             d01: Temperature,
                             d10: Temperature,
                             d11: Temperature
                           ): Temperature = {
    val (x, y) = (point.x, point.y)
    d00 * (1 - x) * (1 - y) + d10 * x * (1 - y) + d01 * (1 - x) * y + d11 * x * y
  }

  /**
    * @param grid   Grid to visualize
    * @param colors Color scale to use
    * @param tile   Tile coordinates to visualize
    * @return The image of the tile at (x, y, zoom) showing the grid using the given color scale
    */
  def visualizeGrid(
                     grid: GridLocation => Temperature,
                     colors: Iterable[(Temperature, Color)],
                     tile: Tile
                   ): Image = {
    import Interaction.tileLocation
    import Visualization._
    val pixels = for {
      y <- 0 until 256
      x <- 0 until 256
      curLocation = tileLocation(Tile(tile.x * 256 + x, tile.y * 256 + y, tile.zoom + 8))
      d00 = grid(GridLocation(curLocation.lat.toInt, curLocation.lon.toInt))
      d01 = grid(GridLocation(curLocation.lat.toInt, (curLocation.lon + 1).toInt))
      d10 = grid(GridLocation((curLocation.lat + 1).toInt, curLocation.lon.toInt))
      d11 = grid(GridLocation((curLocation.lat + 1).toInt, (curLocation.lon + 1).toInt))
      cellPoint = CellPoint(curLocation.lat - curLocation.lat.toInt, curLocation.lon - curLocation.lon.toInt)
      predictedColor = interpolateColor(colors, bilinearInterpolation(cellPoint, d00, d01, d10, d11))
    } yield Pixel(predictedColor.red, predictedColor.green, predictedColor.blue, 127)

    Image(256, 256, pixels.toArray)
  }

}
