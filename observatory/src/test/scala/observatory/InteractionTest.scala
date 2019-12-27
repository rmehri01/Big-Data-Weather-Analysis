package observatory

import scala.collection.concurrent.TrieMap
import org.junit.Assert._
import org.junit.Test
import Interaction._

trait InteractionTest extends MilestoneSuite {
  private val milestoneTest = namedMilestoneTest("interactive visualization", 3) _

  @Test def `tile picture`(): Unit = {
    val t1: (Location, Temperature) = (Location(0, 0), -27d)
    val t2: (Location, Temperature) = (Location(-39, -60), 60d)
    val t3: (Location, Temperature) = (Location(90, -180), -60d)
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
}
