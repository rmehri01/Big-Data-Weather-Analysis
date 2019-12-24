package observatory

import java.time.LocalDate

import org.scalatest._

// trait ExtractionTest extends MilestoneSuite {
trait ExtractionTest extends FlatSpec with Matchers {
  //  private val milestoneTest = namedMilestoneTest("data extraction", 1) _

  import Extraction._

  // locate temperatures test
  val year = 2015
  val stationsFile = "src/test/resources/stationsT1.csv"
  val temperaturesFile = "src/test/resources/temperaturesT1.csv"
  val result = locateTemperatures(year, stationsFile, temperaturesFile)

  result.size shouldBe 3
  result.head shouldBe (LocalDate.of(2015, 8, 11), Location(37.35, -78.433), 27.3)
  result.tail.head shouldBe (LocalDate.of(2015, 12, 6), Location(37.358, -78.438), 0.0)
  result.tail.tail.head shouldBe (LocalDate.of(2015, 1, 29), Location(37.358, -78.438), 2.0)
}
