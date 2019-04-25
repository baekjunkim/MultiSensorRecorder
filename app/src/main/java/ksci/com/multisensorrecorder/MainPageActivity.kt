package ksci.com.multisensorrecorder

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast

/*
Sensors types supported:
    Accelerometer
    Gravity
    Gyroscope
    Light
    Linear Acceleration
    Magnetic Field
    Proximity
    Rotation Vector
 */

class MainPageActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager
    private var sensorHandler = mutableMapOf<Int, Boolean>(
        Sensor.TYPE_ACCELEROMETER to false,
        Sensor.TYPE_GRAVITY to false,
        Sensor.TYPE_GYROSCOPE to false,
        Sensor.TYPE_LIGHT to false,
        Sensor.TYPE_LINEAR_ACCELERATION to false,
        Sensor.TYPE_MAGNETIC_FIELD to false,
        Sensor.TYPE_PROXIMITY to false,
        Sensor.TYPE_ROTATION_VECTOR to false
    )
    private lateinit var sensorController: SensorController

    private lateinit var locationManager: LocationManager
    private var locationHandler = false
    private lateinit var locationController: LocationController
    private val requestLocation = 2

    private var dataRecord: DataRecord? = null

    companion object {
        val sensorData = mutableMapOf<Int, FloatArray>(
            Sensor.TYPE_ACCELEROMETER to FloatArray(3),
            Sensor.TYPE_GRAVITY to FloatArray(3),
            Sensor.TYPE_GYROSCOPE to FloatArray(3),
            Sensor.TYPE_LIGHT to FloatArray(1),
            Sensor.TYPE_LINEAR_ACCELERATION to FloatArray(3),
            Sensor.TYPE_MAGNETIC_FIELD to FloatArray(3),
            Sensor.TYPE_PROXIMITY to FloatArray(1),
            Sensor.TYPE_ROTATION_VECTOR to FloatArray(5)
        )
        var locationData = FloatArray(2)
        var locationPermission = false

        var mRecord = false
        var mDelay: Int = 1000 // 1000 = every 1 second
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        sensorController = SensorController(sensorManager, "record")
        locationAvailable()
        locationController = LocationController(locationManager)

        addListenerOnSensorButton()
        addListenerOnImageButton()
        addListenerOnRecordButton()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (locationPermission) {
            if (requestCode == requestLocation) {
                if (locationHandler) locationController.registerLocation()
            }
        } else {
            locationAvailable()
        }
    }

    private fun locationAvailable() {
        locationPermission = if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                requestLocation
            )
            false
        } else {
            true
        }
    }

    private fun addListenerOnSensorButtonHelper(sensorId: Int, sensorType: Int, sensorTypeToString: String) {
        findViewById<Button>(sensorId).setOnClickListener {
            sensorHandler[sensorType] = !sensorHandler[sensorType]!!
            if (sensorHandler[sensorType]!!) {
                if (sensorManager.getDefaultSensor(sensorType) != null) {
                    it.setBackgroundResource(R.drawable.rounded_button_selected)
                } else {
                    Toast.makeText(this,
                        "$sensorTypeToString sensor is not available on this device",
                        Toast.LENGTH_LONG).show()
                    sensorHandler[sensorType] = !sensorHandler[sensorType]!!
                }
            } else {
                it.setBackgroundResource(R.drawable.rounded_button)
            }
        }
    }

    private fun addListenerOnSensorButton() {
        addListenerOnSensorButtonHelper(R.id.button_accelerometer, Sensor.TYPE_ACCELEROMETER,
            "Accelerometer")
        addListenerOnSensorButtonHelper(R.id.button_gravity, Sensor.TYPE_GRAVITY,
            "Gravity")
        addListenerOnSensorButtonHelper(R.id.button_gyroscope, Sensor.TYPE_GYROSCOPE,
            "Gyroscope")
        addListenerOnSensorButtonHelper(R.id.button_light, Sensor.TYPE_LIGHT,
            "Light")
        addListenerOnSensorButtonHelper(R.id.button_linearAcceleration, Sensor.TYPE_LINEAR_ACCELERATION,
            "Linear Acceleration")
        addListenerOnSensorButtonHelper(R.id.button_magneticField, Sensor.TYPE_MAGNETIC_FIELD,
            "Magnetic Field")
        addListenerOnSensorButtonHelper(R.id.button_proximity, Sensor.TYPE_PROXIMITY,
            "Proximity")
        addListenerOnSensorButtonHelper(R.id.button_rotationVector, Sensor.TYPE_ROTATION_VECTOR,
            "Rotation Vector")
        findViewById<Button>(R.id.button_gpsPosition).setOnClickListener {
            if (locationPermission) {
                locationHandler = !locationHandler
                if (locationHandler) {
                    it.setBackgroundResource(R.drawable.rounded_button_selected)
                } else {
                    it.setBackgroundResource(R.drawable.rounded_button)
                }
            } else {
                locationAvailable()
            }
        }
    }

    private fun addListenerOnImageButtonHelper(intent: Intent, sensorId: Int, sensorType: Int, sensorTypeToString: String) {
        findViewById<ImageButton>(sensorId).setOnClickListener {
            if (sensorManager.getDefaultSensor(sensorType) != null) {
                intent.putExtra("sensor", Sensor.TYPE_ACCELEROMETER)
                startActivity(intent)
            } else {
                Toast.makeText(this,
                    "$sensorTypeToString sensor is not available on this device",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun addListenerOnImageButton() {
        val intent = Intent(this, PreviewPageActivity::class.java)
        addListenerOnImageButtonHelper(intent, R.id.button_accelerometer, Sensor.TYPE_ACCELEROMETER,
            "Accelerometer")
        addListenerOnImageButtonHelper(intent, R.id.button_gravity, Sensor.TYPE_GRAVITY,
            "Gravity")
        addListenerOnImageButtonHelper(intent, R.id.button_gyroscope, Sensor.TYPE_GYROSCOPE,
            "Gyroscope")
        addListenerOnImageButtonHelper(intent, R.id.button_light, Sensor.TYPE_LIGHT,
            "Light")
        addListenerOnImageButtonHelper(intent, R.id.button_linearAcceleration, Sensor.TYPE_LINEAR_ACCELERATION,
            "Linear Acceleration")
        addListenerOnImageButtonHelper(intent, R.id.button_magneticField, Sensor.TYPE_MAGNETIC_FIELD,
            "Magnetic Field")
        addListenerOnImageButtonHelper(intent, R.id.button_proximity, Sensor.TYPE_PROXIMITY,
            "Proximity")
        addListenerOnImageButtonHelper(intent, R.id.button_rotationVector, Sensor.TYPE_ROTATION_VECTOR,
            "Rotation Vector")
    }

    @SuppressLint("SetTextI18n")
    private fun addListenerOnRecordButton() {
        findViewById<Button>(R.id.button_record).setOnClickListener {
            if (dataRecord == null) {
                if (sensorHandler.filter { s -> s.value }.isEmpty() && !locationHandler) {
                    Toast.makeText(this,
                        "Select at least one sensor",
                        Toast.LENGTH_LONG).show()
                } else try {
                    // catch NumberFormatException if there is no input for delay
                    mDelay = 1000 /
                            Integer.valueOf(findViewById<EditText>(R.id.editText_mDelay).text.toString())
                    Toast.makeText(this,
                        "Start recording selected sensor(s)",
                        Toast.LENGTH_LONG).show()
                    it.setBackgroundResource(R.drawable.rounded_button)
                    findViewById<Button>(R.id.button_record).text = "stop"
                    // register selected sensors
                    for (sensorType in sensorHandler.filter {s -> s.value}.map {s -> s.key}) {
                        sensorController.registerSensor(sensorType, true, mDelay)
                    }
                    // register location
                    if (locationHandler) {
                        locationController.registerLocation()
                    }
                    // initialize dataRecord and start recording
                    mRecord = true
                    dataRecord = DataRecord(sensorHandler, locationHandler)
                    dataRecord!!.sensorDataWrite(this.getExternalFilesDir(null)!!, mDelay)
                } catch (e: java.lang.NumberFormatException) {
                    Toast.makeText(this,
                        "Invalid input",
                        Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this,
                    "Saving data at ${this.getExternalFilesDir(null)}",
                    Toast.LENGTH_LONG).show()
                it.setBackgroundResource(R.drawable.rounded_button_selected)
                findViewById<Button>(R.id.button_record).text = "record"
                // unregister sensors
                for (sensorType in sensorHandler.filter {s -> s.value}.map {s -> s.key}) {
                    sensorController.registerSensor(sensorType, false)
                }
                // delete dataRecord
                mRecord = false
                dataRecord = null
            }
        }
    }
}
