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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_page)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorType = intent.getIntExtra("sensor", 0)

        sensorController = SensorController(sensorManager, "preview")
        sensorController.registerSensor(sensorType, true)

        createChart(sensorType)
    }

    private fun createChart(sensorType: Int) {
        mChart = findViewById(R.id.chart)
        mChart.description.isEnabled = false
        mChart.setBackgroundColor(Color.WHITE)
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
