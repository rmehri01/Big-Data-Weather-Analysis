package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import org.apache.spark.sql.DataFrame

/**
  * 2nd milestone: basic visualization
  */
object Visualization extends VisualizationInterface {

  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location     Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Temperature)], location: Location): Temperature = {

    val withDistances = temperatures.par.map { case (loc, temp) => (distance(loc, location), temp) }
    lazy val filteredDistances = withDistances.filter(_._1 < 1)

    def inverseDistance = {
      val inversePairs = for {
        (dist, temp) <- withDistances
        inverseDistance = 1d / Math.pow(dist, 4)
      } yield (inverseDistance * temp, inverseDistance)
      val (numerator, denominator) = inversePairs.unzip
      numerator.sum / denominator.sum
    }

    if (filteredDistances.nonEmpty) filteredDistances.head._2
    else inverseDistance
  }

  def sparkPredictTemperature(temperatures: DataFrame, location: Location): DataFrame = {

    //    val withDistances = temperatures.par.map { case (loc, temp) => (distance(loc, location), temp) }
    //    lazy val filteredDistances = withDistances.filter(_._1 < 1)
    import org.apache.spark.sql.functions.sum
    import SparkSessionSetup.spark.implicits._
    val withDistances = temperatures.as[(Location, Temperature)]
      .map { case (loc, temp) => (temp, distance(loc, location)) }
    withDistances.cache()
    val filteredDistances = withDistances.where("_2 < 1")
        .select("_1")
    filteredDistances.cache()

    //    def inverseDistance = {
    //      val inversePairs = for {
    //        (dist, temp) <- withDistances
    //        inverseDistance = 1d / Math.pow(dist, 4)
    //      } yield (inverseDistance * temp, inverseDistance)
    //      val (numerator, denominator) = inversePairs.unzip
    //      numerator.sum / denominator.sum
    //    }

    val inversePair = withDistances.map { case (temp, dist) =>
      val inverseDistance = 1d / Math.pow(dist, 4)
      (inverseDistance * temp, inverseDistance)
    }.select(sum($"_1") as "sumTemp", sum($"_2") as "sumDist")

    if (filteredDistances.count() > 0) filteredDistances
    else inversePair.selectExpr("sumTemp / sumDist")
  }

  def distance(l1: Location, l2: Location): Temperature = {
    import Math._

    val RADIUS = 6371d // radius of earth in km
    val (lat1, lon1) = (toRadians(l1.lat), toRadians(l1.lon))
    val (lat2, lon2) = (toRadians(l2.lat), toRadians(l2.lon))

    val centralAngle =
      if (lat1 == lat2 && lon1 == lon2) 0
      else if (-toDegrees(lat1) == toDegrees(lat2) && abs(toDegrees(lon1) - toDegrees(lon2)) == 180d) PI
      else acos(sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(abs(lon1 - lon2)))

    RADIUS * centralAngle
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value  The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Double, Color)], value: Double): Color = {
    val sortedPoints = points.toList.sortWith(_._1 < _._1).toArray
    interpolateColor(sortedPoints, value)
  }

  def interpolateColor(sortedPoints: Array[(Double, Color)], value: Double): Color = {

    for (i <- 0 until sortedPoints.length - 1) {
      (sortedPoints(i), sortedPoints(i + 1)) match {
        case ((v1, Color(r1, g1, b1)), (v2, Color(r2, g2, b2))) => {
          if (v1 > value) {
            return Color(r1, g1, b1)
          }
          else if (v2 > value) {
            val ratio = (value - v1) / (v2 - v1)
            return Color(
              math.round(r1 + (r2 - r1) * ratio).toInt,
              math.round(g1 + (g2 - g1) * ratio).toInt,
              math.round(b1 + (b2 - b1) * ratio).toInt
            )
          }
        }
      }
    }
    // Value is not within the colormap.  Return maximum color
    sortedPoints(sortedPoints.length - 1)._2
  }

  /**
    * @param temperatures Known temperatures
    * @param colors       Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)]): Image = {
    //    import Extraction.spark
    //    sparkVisualize(spark.parallelize(temperatures.toSeq), spark.parallelize(colors.toSeq))
    val pixels = for {
      y <- 90 to -89 by -1 // check boundaries (-90, 180)
      x <- -180 to 179
      filtered = temperatures.filter { case (location, _) => location.lat == y && location.lon == x } // not the best
      headPixel = filtered.headOption match {
        case None =>
          val interpolated = interpolateColor(colors, predictTemperature(temperatures, Location(y, x)))
          Pixel(interpolated.red, interpolated.green, interpolated.blue, 255)
        case Some((_, temp)) =>
          val interpolated = interpolateColor(colors, temp)
          Pixel(interpolated.red, interpolated.green, interpolated.blue, 255)
      }
    } yield headPixel

    Image.apply(360, 180, pixels.toArray)
  }


}

