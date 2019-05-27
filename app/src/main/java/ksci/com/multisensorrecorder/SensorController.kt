package ksci.com.multisensorrecorder

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.github.mikephil.charting.data.Entry

class SensorController(_sensorManager: SensorManager, _type: String) : SensorEventListener {

    private val sensorManager = _sensorManager
    private val type = _type

    var minYAxis = 0f
    var maxYAxis = 0f

    override fun onSensorChanged(event: SensorEvent) {
        when (type) {
            "record" -> {
                sensorDataRecord(MainPageActivity.sensorData[event.sensor.type]!!, event)
            }
            "preview" -> {
                sensorPreview(event)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    fun registerSensor(sensor: Int,
                       register: Boolean,
                       mDelay: Int = SensorManager.SENSOR_DELAY_GAME) {

        if (register) {
            sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(sensor),
                mDelay
            )
        } else {
            sensorManager.unregisterListener(
                this,
                sensorManager.getDefaultSensor(sensor)
            )
        }
    }

    private fun sensorDataRecord(sensorData: FloatArray, event: SensorEvent) {
        for (i in 0 until event.values.size) {
            sensorData[i] = event.values[i]
        }
    }

    @SuppressLint("SetTextI18n")
    private fun sensorPreview(event: SensorEvent) {
        val mChart = PreviewPageActivity.mChart
        val data = mChart.data

        val sensorType = PreviewPageActivity.sensorType

        val s1 = PreviewPageActivity.s1
        val s2 = PreviewPageActivity.s2
        val s3 = PreviewPageActivity.s3
        val s4 = PreviewPageActivity.s4

        val sensorValue = PreviewPageActivity.sensorValue

        when (sensorType) {
            Sensor.TYPE_LIGHT,
            Sensor.TYPE_PROXIMITY -> {
                s1.addEntry(Entry(s1.entryCount.toFloat(), event.values[0]))

                data.getDataSetByIndex(0).addEntry(Entry(s1.entryCount.toFloat(), event.values[0]))

                sensorValue.text = "x: ${event.values[0]}"
            }
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_GRAVITY,
            Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_LINEAR_ACCELERATION,
            Sensor.TYPE_MAGNETIC_FIELD -> {
                s1.addEntry(Entry(s1.entryCount.toFloat(), event.values[0]))
                s2.addEntry(Entry(s2.entryCount.toFloat(), event.values[1]))
                s3.addEntry(Entry(s3.entryCount.toFloat(), event.values[2]))

                data.getDataSetByIndex(0).addEntry(Entry(s1.entryCount.toFloat(), event.values[0]))
                data.getDataSetByIndex(1).addEntry(Entry(s2.entryCount.toFloat(), event.values[1]))
                data.getDataSetByIndex(2).addEntry(Entry(s3.entryCount.toFloat(), event.values[2]))

                sensorValue.text = "x: ${event.values[0]}\ny: ${event.values[1]}\nz: ${event.values[2]}"
            }
            Sensor.TYPE_ROTATION_VECTOR -> {
                s1.addEntry(Entry(s1.entryCount.toFloat(), event.values[0]))
                s2.addEntry(Entry(s2.entryCount.toFloat(), event.values[1]))
                s3.addEntry(Entry(s3.entryCount.toFloat(), event.values[2]))
                s4.addEntry(Entry(s4.entryCount.toFloat(), event.values[3]))

                data.getDataSetByIndex(0).addEntry(Entry(s1.entryCount.toFloat(), event.values[0]))
                data.getDataSetByIndex(1).addEntry(Entry(s2.entryCount.toFloat(), event.values[1]))
                data.getDataSetByIndex(2).addEntry(Entry(s3.entryCount.toFloat(), event.values[2]))
                data.getDataSetByIndex(3).addEntry(Entry(s4.entryCount.toFloat(), event.values[3]))

                sensorValue.text = "x: ${event.values[0]}\ny: ${event.values[1]}\nz: ${event.values[2]}\nw: ${event.values[3]}"
            }
        }

        val yAxis = PreviewPageActivity.yAxis

        if (event.values.min()?:0f < minYAxis) {
            minYAxis = event.values.min()?:0f
            yAxis.axisMinimum = minYAxis * 12/10
        }
        if (event.values.max()?:0f > maxYAxis) {
            maxYAxis = event.values.max()?:0f
            yAxis.axisMaximum = maxYAxis * 12/10
        }

        mChart.data.notifyDataChanged()
        mChart.notifyDataSetChanged()
        mChart.setVisibleXRangeMaximum(150f)
        mChart.moveViewToX(data.entryCount.toFloat())
    }
}