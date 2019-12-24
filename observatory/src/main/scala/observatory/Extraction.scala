package observatory

import java.time.LocalDate

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
    import org.apache.spark.sql.SparkSession

    val spark: SparkSession =
      SparkSession
        .builder()
        .appName("Weather Analysis")
        .master("local[4]")
        .getOrCreate()

    val stationsDf = spark.read.options(Map("inferSchema" -> "true")).csv(stationsFile)
    val temperaturesDf = spark.read.options(Map("inferSchema" -> "true")).csv(temperaturesFile)
    stationsDf.join(temperaturesDf)
    ???
  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Temperature)]): Iterable[(Location, Temperature)] = {
    ???
  }

}
