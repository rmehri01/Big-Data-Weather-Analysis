package observatory

import com.sksamuel.scrimage.Image

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
    def distance(l1: Location, l2: Location) = {
      import Math._

      val RADIUS = 6371 // radius of earth in km
      val (lat1, lon1) = (l1.lat, l1.lon)
      val (lat2, lon2) = (l2.lat, l2.lon)

      val centralAngle =
        if (lat1 == lat2 && lon1 == lon2) 0
        else if (abs(lat1) == abs(lat2) && abs(lon1 - lon2) == 180) PI
        else acos(sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(abs(lon1 - lon2)))

      RADIUS * centralAngle
    }

    val withDistances = temperatures.map { case (loc, temp) => (distance(loc, location), temp) }
    val filteredDistances = withDistances.filter(_._1 < 1)

    def inverseDistance = {
      val inversePairs = for {
        (dist, temp) <- withDistances
        inverseDistance = 1d / Math.pow(dist, 2)
      } yield (inverseDistance * temp, inverseDistance)
      val (numerator, denominator) = inversePairs.unzip
      numerator.sum / denominator.sum
    }

    if (filteredDistances.nonEmpty) filteredDistances.head._2
    else inverseDistance
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value  The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Temperature, Color)], value: Temperature): Color = {
    ???
  }

  /**
    * @param temperatures Known temperatures
    * @param colors       Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)]): Image = {
    ???
  }

}

