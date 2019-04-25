package ksci.com.multisensorrecorder

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.os.Handler
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.Writer
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class DataRecord(_sensorHandler: MutableMap<Int, Boolean>, _locationHandler: Boolean) {

    private val sensorHandler = _sensorHandler.filter {it.value}.map {it.key}
    private val locationHandler = _locationHandler

    private val sensorType = hashMapOf(
        Sensor.TYPE_ACCELEROMETER to "accel_x, accel_y, accel_z",
        Sensor.TYPE_GRAVITY to "gravity_x, gravity_y, gravity_z",
        Sensor.TYPE_GYROSCOPE to "gyro_x, gyro_y, gyro_z",
        Sensor.TYPE_LIGHT to "light",
        Sensor.TYPE_LINEAR_ACCELERATION to "linear_accel_x, linear_accel_y, linear_accel_z",
        Sensor.TYPE_MAGNETIC_FIELD to "mag_field_x, mag_field_y, mag_field_z",
        Sensor.TYPE_PROXIMITY to "proximity",
        Sensor.TYPE_ROTATION_VECTOR to "rot_vector_x, rot_vector_y, rot_vector_z, rot_vector_w, rot_vector_heading"
    )

    @SuppressLint("SimpleDateFormat")
    fun sensorDataWrite(path: File, mDelay: Int) {

        // FILE directory
        var currentDataCount = 0
        val currentDate = SimpleDateFormat("yyyy-M-dd-hh-mm-ss").format(Date())

        val letDirectory = File(path, currentDate.substring(0, 9))
        letDirectory.mkdirs()

        val file = File(letDirectory, "${currentDate.substring(10)}.csv")
        if (!file.exists()) {
            file.createNewFile()
        }

        val output: Writer = BufferedWriter(FileWriter(file))

        // CSV header
        output.write("time")
        for (type in sensorHandler) {
            output.write(",${sensorType[type]}")
        }
        if (locationHandler) output.write(",GPS_pos_lat, GPS_pos_long")

        // CSV body
        val writeData = object : Runnable {
            override fun run() {
                if (currentDataCount == 500000) {
                    output.close()
                    sensorDataWrite(path, mDelay)
                } else {
                    output.write("\n") // new line
                    output.write(SimpleDateFormat("M-dd-hh-mm-ss-SSS").format(Date())) // time column

                    for (type in sensorHandler) { // sensor columns
                        for (data in MainPageActivity.sensorData[type]!!) {
                            output.write(",$data")
                        }
                    }
                    if (locationHandler) { // gps position column
                        for (data in MainPageActivity.locationData) {
                            output.write(",$data")
                        }
                    }
                    currentDataCount++
                    println("recording $currentDataCount line")
                    if (MainPageActivity.mRecord) Handler().postDelayed(this, mDelay.toLong())
                    else output.close()
                }
            }
        }
        Handler().post(writeData)
    }

}