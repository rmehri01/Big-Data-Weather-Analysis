package observatory

import org.junit.Test
import Visualization2._
import Manipulation._

trait Visualization2Test extends MilestoneSuite {
  private val milestoneTest = namedMilestoneTest("value-added information visualization", 5) _

  // Implement tests for methods of the `Visualization2` object

  @Test def `visualizeGrid test`(): Unit = {
    val colors = Seq(
      (7d, Color(0, 0, 0)),
      (4d, Color(255, 0, 0)),
      (2d, Color(255, 255, 0)),
      (0d, Color(255, 255, 255)),
      (-2d, Color(0, 255, 255)),
      (-7d, Color(0, 0, 255))
    )

    val t1: (Location, Temperature) = (Location(0, 0), 0d)
    val t2: (Location, Temperature) = (Location(-39, -60), 60d)
    val t3: (Location, Temperature) = (Location(90, -180), -60d)
    val temps = Seq(t1, t2, t3)

    val result = visualizeGrid(makeGrid(temps), colors, Tile(0, 0, 0))
    result.output(new java.io.File("target/some-image3.png"))
  }
}
