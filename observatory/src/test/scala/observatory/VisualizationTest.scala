package observatory

import observatory.Visualization._
import org.junit._

trait VisualizationTest extends MilestoneSuite {
  private val milestoneTest = namedMilestoneTest("raw data display", 2) _

  @Test def `predict temp`(): Unit = {
    val t1 = (Location(0 , 0), 0d)
    val t2 = (Location(39 , 60), 20d)
    val temps = Seq(t1, t2)
    val predict = Location(10, 10)
    val result = predictTemperature(temps, predict)
    assert(result > 0 && result < 10) // closer to t1
  }

}
