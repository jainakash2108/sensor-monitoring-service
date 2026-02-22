package com.example

import com.example.domain.Measurement
import com.example.domain.Sensor
import com.example.domain.SensorType.Humidity
import com.example.domain.SensorType.Temperature

val temperatureSensor = Sensor("Temperature", 3344, 35.0, Temperature)
val humiditySensor = Sensor("Humidity", 3355, 50.0, Humidity)

val temperatureMeasurement = Measurement("t1", 36.5, Temperature)
val humidityMeasurement = Measurement("h1", 55.0, Humidity)

val temperatureMeasurement1 = Measurement("t1", 23.0, Temperature)
val temperatureMeasurement2 = Measurement("t1", 33.0, Temperature)

val humidityMeasurement1 = Measurement("h1", 33.0, Humidity)
val humidityMeasurement2 = Measurement("h1", 43.0, Humidity)

val temperatureMessage1: String = "sensor_id=t1; value=23"
val temperatureMessage2: String = "sensor_id=t2; value=33"

val humidityMessage1: String = "sensor_id=h1; value=33"
val humidityMessage2: String = "sensor_id=h2; value=43"