package observatory

import java.time.LocalDate

import org.apache.spark.rdd.RDD

/**
  * 1st milestone: data extraction
  */
object Extraction extends ExtractionInterface {

  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(year: Year, stationsFile: String, temperaturesFile: String): Iterable[(LocalDate, Location, Temperature)] = {
    sparkLocateTemperatures(year, stationsFile, temperaturesFile).collect()
  }

  def sparkLocateTemperatures(year: Year, stationsFile: String, temperaturesFile: String): RDD[(LocalDate, Location, Temperature)] = {
    import org.apache.spark.rdd.RDD
    import org.apache.spark.{SparkConf, SparkContext}

    val conf: SparkConf = new SparkConf().setAppName("Wikipedia Language Ranker").setMaster("local[4]")
    val spark: SparkContext = new SparkContext(conf)

    case class Data(month: Int, day: Int, lat: Double, lon: Double, temp: Double)

    val stationsRDD = spark.textFile(stationsFile)
      .filter(line => line.split(",").length >= 3) // only want to be missing WBAN
      .map { line =>
        val arr = line.split(",")
        ((arr(0), arr(1)), (arr(2).toDouble, arr(3).toDouble))
      }

    val temperaturesRDD = spark.textFile(temperaturesFile)
      .filter(line => line.split(",").length >= 4)
      .map { line =>
        val arr = line.split(",")
        ((arr(0), arr(1)), (arr(2).toInt, arr(3).toInt, arr(4).toDouble))
      }

    def toCelsius(num: Double): Double = roundToTens((num - 32) * 5 / 9)

    def roundToTens(num: Double): Double = (num * 10).round / 10d

    stationsRDD.join(temperaturesRDD)
      .mapValues {
        case ((lat, lon), (month, day, temp)) => (LocalDate.of(year, month, day), Location(lat, lon), toCelsius(temp))
      }
      .values
  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Temperature)]): Iterable[(Location, Temperature)] = {
    ???
  }

}
