# Big-Data-Weather-Analysis

## Summary

This is a project meant to apply data processing skills to a relatively data-intensive application using real-world data from the [National Center for Environmental Information of the United States](https://www.ncei.noaa.gov/). It involves cleaning raw data, extracting useful information from it, and using spatial and linear interpolation techniques to visualize the data. An example of this is finding the average temperature of each point on the globe over the year, which is represented below using the following scale:

### Scale:

![Scale for temperature in images](img/tempscale.png)

### Average temperatures across the globe in 1975:

![Average temperatures across the globe in 1975](img/1975.png)

### Average temperatures across the globe in 2015:

![Average temperatures across the globe in 1975](img/2015.png)

## The Process

This project is for the "Functional Programming in Scala Capstone." It serves as a way for individuals to show what they have learned in the courses prior while also giving as much freedom as possible in the solution space.

For this project, I used Apache Spark with Scala along with Amazon EMR cluster computing in order to handle all the data processing. The steps in the project included:

- Extracting the data from the raw csv files and finding the average temperature for each location where data was given
- Visualizing the data using a color scale corresponding to each temperature
- Interpolating the temperature based on known points, which allows us to approximate all temperatures on the globe since temperature is continuous
- Finding the deviation of temperatures from the average of the previous years
- Trying different interpolation techniques to see how they impacted the image generated
