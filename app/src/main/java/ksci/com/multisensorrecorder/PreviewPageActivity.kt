/*
Copyright 2019 Philipp Jahoda

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions
and limitations under the License.
*/

package ksci.com.multisensorrecorder

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class PreviewPageActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var sensorController: SensorController

    companion object {
        var sensorType: Int = 0

        lateinit var mChart: LineChart
        lateinit var yAxis: YAxis
        lateinit var s1: LineDataSet
        lateinit var s2: LineDataSet
        lateinit var s3: LineDataSet
        lateinit var s4: LineDataSet

        lateinit var sensorValue: TextView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_page)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorType = intent.getIntExtra("sensor", 0)

        sensorController = SensorController(sensorManager, "preview")
        sensorController.registerSensor(sensorType, true)

        createSensorInfo(sensorType)
        sensorValue = findViewById(R.id.sensor_value)
        createChart(sensorType)
    }

    private fun createSensorInfo(sensorType: Int) {
        findViewById<TextView>(R.id.sensor_name).text = when(sensorType) {
            Sensor.TYPE_ACCELEROMETER -> getString(R.string.accelerometer)
            Sensor.TYPE_GRAVITY -> getString(R.string.gravity)
            Sensor.TYPE_GYROSCOPE -> getString(R.string.gyroscope)
            Sensor.TYPE_LIGHT -> getString(R.string.light)
            Sensor.TYPE_LINEAR_ACCELERATION -> getString(R.string.linearAcceleration)
            Sensor.TYPE_MAGNETIC_FIELD -> getString(R.string.magneticField)
            Sensor.TYPE_PROXIMITY -> getString(R.string.proximity)
            Sensor.TYPE_ROTATION_VECTOR -> getString(R.string.rotationVector)
            else -> ""
        }
        findViewById<TextView>(R.id.sensor_type).text = when(sensorType) {
            Sensor.TYPE_ACCELEROMETER -> getString(R.string.accelerometer_type)
            Sensor.TYPE_GRAVITY -> getString(R.string.gravity_type)
            Sensor.TYPE_GYROSCOPE -> getString(R.string.gyroscope_type)
            Sensor.TYPE_LIGHT -> getString(R.string.light_type)
            Sensor.TYPE_LINEAR_ACCELERATION -> getString(R.string.linearAcceleration_type)
            Sensor.TYPE_MAGNETIC_FIELD -> getString(R.string.magneticField_type)
            Sensor.TYPE_PROXIMITY -> getString(R.string.proximity_type)
            Sensor.TYPE_ROTATION_VECTOR -> getString(R.string.rotationVector_type)
            else -> ""
        }

        findViewById<TextView>(R.id.sensor_info).text = when(sensorType) {
            Sensor.TYPE_ACCELEROMETER -> getString(R.string.accelerometer_info)
            Sensor.TYPE_GRAVITY -> getString(R.string.gravity_info)
            Sensor.TYPE_GYROSCOPE -> getString(R.string.gyroscope_info)
            Sensor.TYPE_LIGHT -> getString(R.string.light_info)
            Sensor.TYPE_LINEAR_ACCELERATION -> getString(R.string.linearAcceleration_info)
            Sensor.TYPE_MAGNETIC_FIELD -> getString(R.string.magneticField_info)
            Sensor.TYPE_PROXIMITY -> getString(R.string.proximity_info)
            Sensor.TYPE_ROTATION_VECTOR -> getString(R.string.rotationVector_info)
            else -> ""
        }

        findViewById<TextView>(R.id.sensor_uses).text = when(sensorType) {
            Sensor.TYPE_ACCELEROMETER -> getString(R.string.accelerometer_uses)
            Sensor.TYPE_GRAVITY -> getString(R.string.gravity_uses)
            Sensor.TYPE_GYROSCOPE -> getString(R.string.gyroscope_uses)
            Sensor.TYPE_LIGHT -> getString(R.string.light_uses)
            Sensor.TYPE_LINEAR_ACCELERATION -> getString(R.string.linearAcceleration_uses)
            Sensor.TYPE_MAGNETIC_FIELD -> getString(R.string.magneticField_uses)
            Sensor.TYPE_PROXIMITY -> getString(R.string.proximity_uses)
            Sensor.TYPE_ROTATION_VECTOR -> getString(R.string.rotationVector_uses)
            else -> ""
        }
    }

    private fun createChart(sensorType: Int) {
        mChart = findViewById(R.id.chart)
        mChart.description.isEnabled = false
//        mChart.setBackgroundColor(Color.WHITE)
        mChart.setDrawBorders(true)
        mChart.data = LineData() // add LineDataSet() s1, s2, s3, or s4 to LineData()
        mChart.xAxis.isEnabled = false
        mChart.xAxis.setDrawGridLines(true)
        mChart.axisRight.isEnabled = false

        val legend = mChart.legend
        legend.form = Legend.LegendForm.LINE
        legend.textColor = Color.BLACK

        yAxis = mChart.axisLeft
        yAxis.setDrawGridLines(true)
        yAxis.textColor = Color.BLACK

        s1 = createSet("x", Color.BLUE)
        s2 = createSet("y", Color.RED)
        s3 = createSet("z", Color.GREEN)
        s4 = createSet("w", Color.GRAY)

        when (sensorType) {
            Sensor.TYPE_LIGHT,
            Sensor.TYPE_PROXIMITY -> {
                mChart.data.addDataSet(s1)
            }
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_GRAVITY,
            Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_LINEAR_ACCELERATION,
            Sensor.TYPE_MAGNETIC_FIELD -> {
                mChart.data.addDataSet(s1)
                mChart.data.addDataSet(s2)
                mChart.data.addDataSet(s3)
            }
            Sensor.TYPE_ROTATION_VECTOR -> {
                mChart.data.addDataSet(s1)
                mChart.data.addDataSet(s2)
                mChart.data.addDataSet(s3)
                mChart.data.addDataSet(s4)
            }
        }
    }

    private fun createSet(label:String, color:Int): LineDataSet {
        val set = LineDataSet(null, label)
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.lineWidth = 3f
        set.color = color
        set.setDrawValues(false)
        set.setDrawCircles(false)
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.cubicIntensity = 1.5f
        return set
    }

    override fun onPause() {
        super.onPause()

        sensorController.registerSensor(sensorType, false)
    }
}
