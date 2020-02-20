package observatory

import Extraction._
import Interaction._
//import awscala._
//import s3._

object Main extends App {
  //  // aws s3 setup
  //  implicit val s3 = S3.at(Region.Tokyo)
  //  val bucket: Bucket = s3.createBucket("weather-photos-rmehri01")
  //
  // Main for temperatures

  import SparkSessionSetup.spark.implicits._

  val yearlyData = for {
    year <- 1975 to 1994
    resultOfYearlyAvgRecords = sparkLocationYearlyAverageRecords(sparkLocateTemperatures(year, "stations.csv", s"$year.csv"))
  } yield (year, resultOfYearlyAvgRecords.as[(Location, Temperature)].collect())
  //  } yield (year, resultOfYearlyAvgRecords)
  // TODO: handle caching and decide whether to use dfs in processing the rest, likely yes


  val tempColors = Seq(
    (60d, Color(255, 255, 255)),
    (32d, Color(255, 0, 0)),
    (12d, Color(255, 255, 0)),
    (0d, Color(0, 255, 255)),
    (-15d, Color(0, 0, 255)),
    (-27d, Color(255, 0, 255)),
    (-50d, Color(33, 0, 107)),
    (-60d, Color(0, 0, 0))
  )
  // local setup
  //  val generateImage = (year: Year, inputTile: Tile, data: Array[(Location, Temperature)]) => {
  //    val (x, y, zoom) = (inputTile.x, inputTile.y, inputTile.zoom)
  //    //      val dir = new java.io.File(s"s3a://weatherdata-analytics/$year-$zoom")
  //    //      println(s"Tiling: $year-$zoom-$x-$y.png")
  //    val dir = new java.io.File(s"target/temperatures/yeet/$year/$zoom/")
  //    val result = tile(data, tempColors, inputTile)
  //    println(s"Done Tiling: $year-$zoom-$x-$y.png")
  //    if (!dir.exists()) dir.mkdirs()
  //    //      result.output(new java.io.File(s"s3a://weatherdata-analytics/$year-$zoom-$x-$y.png"))
  //    //      println(s"Result.outputting: $year-$zoom-$x-$y.png")
  //    //    val output = result.output(new java.io.File(s"$year-$zoom-$x-$y.png"))
  //    //      println(s"Done Result: $year-$zoom-$x-$y.png")
  //    //      println(s"Putting in bucket: $year-$zoom-$x-$y.png")
  //    //      bucket.put(s"$year-$zoom-$x-$y.png", output)
  //    //      println(s"Done putting in bucket: $year-$zoom-$x-$y.png")
  //    result.output(new java.io.File(s"target/temperatures/yeet/$year/$zoom/$x-$y.png"))
  //    println(s"Done putting out file: $year-$zoom-$x-$y.png")
  //    ()
  //  }

  // gcloud setup

  val generateImage = (year: Year, inputTile: Tile, data: Array[(Location, Temperature)]) => {
    val (x, y, zoom) = (inputTile.x, inputTile.y, inputTile.zoom)
    //      val dir = new java.io.File(s"s3a://weatherdata-analytics/$year-$zoom")
    //      println(s"Tiling: $year-$zoom-$x-$y.png")
//    val dir = new java.io.File(s"target/temperatures/yeet/$year/$zoom/")
    val result = tile(data, tempColors, inputTile)
    println(s"Done Tiling: $year-$zoom-$x-$y.png")
//    if (!dir.exists()) dir.mkdirs()
    //      result.output(new java.io.File(s"s3a://weatherdata-analytics/$year-$zoom-$x-$y.png"))
    //      println(s"Result.outputting: $year-$zoom-$x-$y.png")
    //    val output = result.output(new java.io.File(s"$year-$zoom-$x-$y.png"))
    //      println(s"Done Result: $year-$zoom-$x-$y.png")
    //      println(s"Putting in bucket: $year-$zoom-$x-$y.png")
    //      bucket.put(s"$year-$zoom-$x-$y.png", output)
    //      println(s"Done putting in bucket: $year-$zoom-$x-$y.png")
    val file = result.output(new java.io.File(s"$year/$zoom/$x-$y.png"))

    import com.google.cloud.storage.BlobId
    import com.google.cloud.storage.BlobInfo
    import com.google.cloud.storage.StorageOptions
    import java.nio.file.Files
    import java.nio.file.Paths

    val storage = StorageOptions.newBuilder.setProjectId("weatherdata-268722").build.getService
    val blobId = BlobId.of("weather-data-jars", s"$year-$zoom-$x-$y.png")
    val blobInfo = BlobInfo.newBuilder(blobId).build
    storage.create(blobInfo, Files.readAllBytes(Paths.get(file.getPath)))

    println(s"Done putting out file: $year-$zoom-$x-$y.png")
    ()
  }

  //  val generateImage = (year: Year, inputTile: Tile, data: DataFrame) => {
  //    val (x, y, zoom) = (inputTile.x, inputTile.y, inputTile.zoom)
  //    //      val dir = new java.io.File(s"s3a://weatherdata-analytics/$year-$zoom")
  //    //      println(s"Tiling: $year-$zoom-$x-$y.png")
  //    val dir = new java.io.File(s"target/temperatures/yeet/$year/$zoom/")
  //    val result = sparkTile(data, tempColors, inputTile)
  //    println(s"Done Tiling: $year-$zoom-$x-$y.png")
  //    if (!dir.exists()) dir.mkdirs()
  //    //      result.output(new java.io.File(s"s3a://weatherdata-analytics/$year-$zoom-$x-$y.png"))
  //    //      println(s"Result.outputting: $year-$zoom-$x-$y.png")
  //    //    val output = result.output(new java.io.File(s"$year-$zoom-$x-$y.png"))
  //    //      println(s"Done Result: $year-$zoom-$x-$y.png")
  //    //      println(s"Putting in bucket: $year-$zoom-$x-$y.png")
  //    //      bucket.put(s"$year-$zoom-$x-$y.png", output)
  //    //      println(s"Done putting in bucket: $year-$zoom-$x-$y.png")
  //    result.output(new java.io.File(s"target/temperatures/yeet/$year/$zoom/$x-$y.png"))
  //    println(s"Done putting out file: $year-$zoom-$x-$y.png")
  //    ()
  //  }

  generateTiles(yearlyData.toDS(), generateImage)


  //  Extraction.sparkLocationYearlyAverageRecords(Extraction.sparkLocateTemperatures(1975, "stations.csv", "1975.csv"))

}
