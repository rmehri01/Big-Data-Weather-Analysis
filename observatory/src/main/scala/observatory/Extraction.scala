package observatory

import java.time.LocalDate

import SparkSessionSetup.spark
import org.apache.spark.sql.DataFrame

/**
  * 1st milestone: data extraction
  */
object Extraction extends ExtractionInterface {

  import spark.implicits._

  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(year: Year, stationsFile: String, temperaturesFile: String): Iterable[(LocalDate, Location, Temperature)] = {
    sparkLocateTemperatures(year, stationsFile, temperaturesFile).as[(LocalDate, Location, Temperature)].collect()
  }

  def sparkLocateTemperatures(year: Year, stationsFile: String, temperaturesFile: String): DataFrame = {

    val emptyFillValue = -1

    val stationsDF = spark.read.format("csv")
      .option("inferSchema", "true")
      .load(s"src/main/resources/${stationsFile}")
      .coalesce(5)
      .toDF("STN", "WBAN", "lat", "lon")
      .na.drop(Seq("lat", "lon"))
      .na.fill(emptyFillValue)
    stationsDF.cache()

    val temperaturesDF = spark.read.format("csv")
      .option("inferSchema", "true")
      .load(s"src/main/resources/${temperaturesFile}")
      .coalesce(5)
      .toDF("STN", "WBAN", "month", "day", "temp")
      .na.drop(Seq("month", "day", "temp"))
      .na.fill(emptyFillValue)
    temperaturesDF.cache()

    val joinExpression = temperaturesDF.col("STN") === stationsDF.col("STN") &&
      temperaturesDF.col("WBAN") === stationsDF.col("WBAN")
    val joinedDF = temperaturesDF
      .join(stationsDF, joinExpression)
      .drop("STN", "WBAN")

    import org.apache.spark.sql.functions.{to_date, lit, expr, concat_ws, udf}

    def createLocation(lat: Double, lon: Double): Location = {
      Location(lat, lon)
    }

    val createLocationUdf = udf(createLocation(_: Double, _: Double): Location)

    val tripletDF = joinedDF
      .select(
        to_date(concat_ws("-", lit(year), $"month", $"day")) as "date",
        createLocationUdf($"lat", $"lon") as "location",
        expr("(temp - 32) * 5 / 9") as "temp"
      )
    println(s"successfully loaded in $year dataset")
    tripletDF
  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Temperature)]): Iterable[(Location, Temperature)] = {
    sparkLocationYearlyAverageRecords(records.toSeq.toDF("date", "location", "temp"))
      .as[(Location, Temperature)]
      .collect()
  }

  def sparkLocationYearlyAverageRecords(records: DataFrame): DataFrame = {
    import org.apache.spark.sql.functions.avg

    val result = records.groupBy("location")
      .agg(avg("temp"))
    result
  }

}
