package observatory

import observatory.Visualization._
import org.junit._

trait VisualizationTest extends MilestoneSuite {
  private val milestoneTest = namedMilestoneTest("raw data display", 2) _
  val t1: (Location, Temperature) = (Location(0, 0), 0d)
  val t2: (Location, Temperature) = (Location(39, 60), 20d)
  val temps = Seq(t1, t2)

  @Test def `predict temp`(): Unit = {
    val predict = Location(10, 10)
    val result = predictTemperature(temps, predict)
    assert(result > 0 && result < 10) // closer to t1
  }

  val points = Seq(
    (60d, Color(255, 255, 255)),
    (32d, Color(255, 0, 0)),
    (12d, Color(255, 255, 0)),
    (0d, Color(0, 255, 255)),
    (-15d, Color(0, 0, 255)),
    (-27d, Color(255, 0, 255)),
    (-50d, Color(33, 0, 107)),
    (-60d, Color(0, 0, 0))
  )

  @Test def `predict color`(): Unit = {
    val result = interpolateColor(points, -7.5)
    println(result)
    assert(result == Color(0, 128, 255))
  }

  @Test def `visualize test`(): Unit = {
    val result = visualize((Location(90, -180), 60d) +: temps, points)
    result.output(new java.io.File("target/some-image.png"))
    assert(result.exists(pixel => pixel.red == 255 && pixel.green == 255 && pixel.blue == 255 && pixel.alpha == 255))
  }

}
