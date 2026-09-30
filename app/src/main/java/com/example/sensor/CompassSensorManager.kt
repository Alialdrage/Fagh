package com.example.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.model.SensorAccuracyLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.*

class CompassSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val rotationVectorSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val accelerometerSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometerSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val _azimuth = MutableStateFlow(0f)
    val azimuth: StateFlow<Float> = _azimuth.asStateFlow()

    private val _accuracy = MutableStateFlow(SensorAccuracyLevel.HIGH)
    val accuracy: StateFlow<SensorAccuracyLevel> = _accuracy.asStateFlow()

    private val _isFlat = MutableStateFlow(true)
    val isFlat: StateFlow<Boolean> = _isFlat.asStateFlow()

    private val _isSupported = MutableStateFlow(true)
    val isSupported: StateFlow<Boolean> = _isSupported.asStateFlow()

    // Filter variables for smooth needle movement (Vector low-pass on sin/cos)
    private var smoothedSin = 0.0
    private var smoothedCos = 1.0
    private val smoothingFactor = 0.22 // Smooth response without lag

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    private val gravityValues = FloatArray(3)
    private val geomagneticValues = FloatArray(3)
    private var hasGravity = false
    private var hasGeomagnetic = false

    private var isListening = false

    init {
        val supported = rotationVectorSensor != null || (accelerometerSensor != null && magnetometerSensor != null)
        _isSupported.value = supported
    }

    fun startListening() {
        if (isListening) return

        if (rotationVectorSensor != null) {
            sensorManager.registerListener(
                this,
                rotationVectorSensor,
                SensorManager.SENSOR_DELAY_UI
            )
            isListening = true
        } else if (accelerometerSensor != null && magnetometerSensor != null) {
            sensorManager.registerListener(
                this,
                accelerometerSensor,
                SensorManager.SENSOR_DELAY_UI
            )
            sensorManager.registerListener(
                this,
                magnetometerSensor,
                SensorManager.SENSOR_DELAY_UI
            )
            isListening = true
        }
    }

    fun stopListening() {
        if (!isListening) return
        sensorManager.unregisterListener(this)
        isListening = false
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_ROTATION_VECTOR -> {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                computeOrientation(rotationMatrix)
            }
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(event.values, 0, gravityValues, 0, 3)
                hasGravity = true
                if (hasGeomagnetic) {
                    processAccelMag()
                }
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, geomagneticValues, 0, 3)
                hasGeomagnetic = true
                if (hasGravity) {
                    processAccelMag()
                }
            }
        }
    }

    private fun processAccelMag() {
        val success = SensorManager.getRotationMatrix(rotationMatrix, null, gravityValues, geomagneticValues)
        if (success) {
            computeOrientation(rotationMatrix)
        }
    }

    private fun computeOrientation(rMatrix: FloatArray) {
        SensorManager.getOrientation(rMatrix, orientationAngles)

        // Azimuth (yaw) in radians [-PI, PI]
        val rawAzimuthRad = orientationAngles[0]
        val rawPitchRad = orientationAngles[1]
        val rawRollRad = orientationAngles[2]

        // Check if device is kept reasonably flat (within ~38 degrees of level)
        val pitchDeg = Math.toDegrees(abs(rawPitchRad.toDouble()))
        val rollDeg = Math.toDegrees(abs(rawRollRad.toDouble()))
        val isDeviceLevel = pitchDeg < 38.0 && rollDeg < 38.0
        _isFlat.value = isDeviceLevel

        // Circular exponential moving average on sin/cos avoids 0/360 wrap discontinuities
        val currentSin = sin(rawAzimuthRad.toDouble())
        val currentCos = cos(rawAzimuthRad.toDouble())

        smoothedSin = smoothedSin * (1.0 - smoothingFactor) + currentSin * smoothingFactor
        smoothedCos = smoothedCos * (1.0 - smoothingFactor) + currentCos * smoothingFactor

        val smoothedRad = atan2(smoothedSin, smoothedCos)
        var smoothedDeg = Math.toDegrees(smoothedRad).toFloat()
        if (smoothedDeg < 0) {
            smoothedDeg += 360f
        }

        _azimuth.value = smoothedDeg
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        _accuracy.value = when (accuracy) {
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> SensorAccuracyLevel.HIGH
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> SensorAccuracyLevel.MEDIUM
            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> SensorAccuracyLevel.LOW
            else -> SensorAccuracyLevel.UNRELIABLE
        }
    }
}
