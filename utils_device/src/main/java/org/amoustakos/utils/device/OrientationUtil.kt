package org.amoustakos.utils.device

import android.annotation.SuppressLint
import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.ExifInterface
import android.support.annotation.IntDef

/**
 * Calculates and reports device rotation based on accelerometer and magnetometer.
 */
class OrientationUtil(
        context: Context,
        @Orientation defaultOrientation: Int
) : DefaultLifecycleObserver {

    private val smoothness = 1
    private var averagePitch = 0f
    private var averageRoll = 0f
    @get:Orientation
    @get:Synchronized
    var orientation = ORIENTATION_PORTRAIT
        private set

    private val pitches: FloatArray
    private val rolls: FloatArray

    private val sManager: SensorManager

    init {
        pitches = FloatArray(smoothness)
        rolls = FloatArray(smoothness)
        orientation = defaultOrientation
        sManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
    }

    fun registerListeners() {
        //Accelerometer
        if (sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null)
            sManager.registerListener(
                    eventListener,
                    sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL
            )

        //Magnetometer
        if (sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null)
            sManager.registerListener(
                    eventListener,
                    sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                    SensorManager.SENSOR_DELAY_NORMAL
            )
    }

    fun unregisterListeners() {
        sManager.unregisterListener(eventListener)
    }


    // =========================================================================================
    // Calculations
    // =========================================================================================

    @Synchronized private fun addValue(value: Float, values: FloatArray): Float {
        val v: Float = Math.round(Math.toDegrees(value.toDouble())).toFloat()
        var average = 0f
        for (i in 1 until smoothness) {
            values[i - 1] = values[i]
            average += values[i]
        }
        values[smoothness - 1] = v
        average = (average + v) / smoothness
        return average
    }


    @Orientation
    private fun calculateOrientation(): Int {
        // finding local orientation dip
        if ((orientation == ORIENTATION_PORTRAIT || orientation == ORIENTATION_PORTRAIT_REVERSE)
                && averageRoll > -30 && averageRoll < 30) {
            return if (averagePitch > 0)
                ORIENTATION_PORTRAIT_REVERSE
            else
                ORIENTATION_PORTRAIT
        } else {
            // divides between all orientations
            return if (Math.abs(averagePitch) >= 30) {
                if (averagePitch > 0)
                    ORIENTATION_PORTRAIT_REVERSE
                else
                    ORIENTATION_PORTRAIT
            } else {
                if (averageRoll > 0)
                    ORIENTATION_LANDSCAPE_REVERSE
                else
                    ORIENTATION_LANDSCAPE
            }
        }
    }


    val cameraRotationDegrees: Float
        get() = getCameraRotationDegrees(orientation)


    // =========================================================================================
    // Getters - Setters
    // =========================================================================================

    private val eventListener: SensorEventListener = object : SensorEventListener {
        var mGravity: FloatArray? = null
        var mGeomagnetic: FloatArray? = null


        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER)
                mGravity = event.values
            if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD)
                mGeomagnetic = event.values
            if (mGravity != null && mGeomagnetic != null) {
                val R = FloatArray(9)
                val I = FloatArray(9)
                val success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)
                if (success) {
                    val orientationData = FloatArray(3)
                    SensorManager.getOrientation(R, orientationData)
                    averagePitch = addValue(orientationData[1], pitches)
                    averageRoll = addValue(orientationData[2], rolls)
                    orientation = calculateOrientation()
                }
            }
        }
    }


    // =========================================================================================
    // Implemented
    // =========================================================================================

    override fun onResume(owner: LifecycleOwner) {
        registerListeners()
    }

    override fun onPause(owner: LifecycleOwner) {
        unregisterListeners()
    }


    // =========================================================================================
    // Companion
    // =========================================================================================

    companion object {
        const val ORIENTATION_PORTRAIT: Int = ExifInterface.ORIENTATION_ROTATE_90 // 6
        const val ORIENTATION_LANDSCAPE_REVERSE: Int = ExifInterface.ORIENTATION_ROTATE_180 // 3
        const val ORIENTATION_LANDSCAPE: Int = ExifInterface.ORIENTATION_NORMAL // 1
        const val ORIENTATION_PORTRAIT_REVERSE: Int = ExifInterface.ORIENTATION_ROTATE_270 // 8

        @SuppressLint("SwitchIntDef")
        @JvmStatic
        fun getCameraRotationDegrees(@Orientation orientation: Int): Float {
            return when (orientation) {
                ORIENTATION_LANDSCAPE -> -90f
                ORIENTATION_PORTRAIT -> 0f
                ORIENTATION_LANDSCAPE_REVERSE -> 90f
                ORIENTATION_PORTRAIT_REVERSE -> 180f
                else -> java.lang.Float.MIN_VALUE
            }
        }
    }


    // =========================================================================================
    // Inner classes
    // =========================================================================================

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(
            ORIENTATION_PORTRAIT,
            ORIENTATION_LANDSCAPE_REVERSE,
            ORIENTATION_LANDSCAPE,
            ORIENTATION_PORTRAIT_REVERSE)
    annotation class Orientation

}