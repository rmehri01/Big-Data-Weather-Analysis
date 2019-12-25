package observatory

import java.time.LocalDate

import observatory.Extraction._
import org.scalatest._

// trait ExtractionTest extends MilestoneSuite {
class ExtractionTest extends FunSuite with Matchers {
  //  private val milestoneTest = namedMilestoneTest("data extraction", 1) _

  val year = 2015
  val stationsFile = "/stationsT1.csv"
  val temperaturesFile = "/temperaturesT1.csv"
  val located = locateTemperatures(year, stationsFile, temperaturesFile)

  test("locate temperatures") {
    located.size shouldBe 3
    located.head shouldBe(LocalDate.of(2015, 8, 11), Location(37.35, -78.433), 27.3)
    located.tail.head shouldBe(LocalDate.of(2015, 12, 6), Location(37.358, -78.438), 0.0)
    located.tail.tail.head shouldBe(LocalDate.of(2015, 1, 29), Location(37.358, -78.438), 2.0)
  }

  test("locationYearlyAverageRecords test") {
    val averages = locationYearlyAverageRecords(located)

    averages.size shouldBe 2
    averages.head shouldBe(Location(37.35, -78.433), 27.3)
    averages.tail.head shouldBe(Location(37.358, -78.438), 1.0)
  }
}
