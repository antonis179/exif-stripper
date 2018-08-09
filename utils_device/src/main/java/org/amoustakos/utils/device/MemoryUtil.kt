package org.amoustakos.utils.device

import android.app.ActivityManager
import android.content.Context
import android.support.annotation.IntDef
import android.support.annotation.NonNull
import android.support.annotation.RequiresApi
import timber.log.Timber
import java.lang.Exception
import java.lang.ref.WeakReference

/**
 * This utility classifies the device based on memory thresholds and can also report the free memory of the device. <br />
 * Defaults: <br />
 * [.lowMemThres] <br />
 * [.highMemThres] <br />
 */
class MemoryUtil constructor (@NonNull context: Context) {

    //Thresholds
    private var lowMemThres = 512.0
    private var highMemThres = 1536.0

    //Dependencies
    private val context: WeakReference<Context> = WeakReference(context)

    /**
     * Constructor that allows setting the threshold parameters <br></br>
     * @param lowMemThres (mb): Devices with RAM below this are classified as [.LOW] [MemoryProfile]
     * *
     * @param highMemThres (mb): Devices with RAM at or over this are classified as [.HIGH] [MemoryProfile]
     * * <br></br> <br></br>
     * * Devices with RAM capacity between the 2 thresholds are defined as [.MEDIUM] [MemoryProfile]
     */
    constructor(context: Context, lowMemThres: Double, highMemThres: Double) : this(context) {
        this.lowMemThres = lowMemThres
        this.highMemThres = highMemThres
    }


    // =========================================================================================
    // Functionality
    // =========================================================================================

    /**
     * Returns free RAM in MB.
     */
    @RequiresApi(16)
    fun calculateRam(): Double = calculateRam(context.get())


    companion object {
        const val HIGH: Int = 0
        const val MEDIUM: Int = 1
        const val LOW: Int = 2
        const val UNKNOWN: Int = Int.MIN_VALUE

        /**
         * Returns free RAM in MB.
         */
        @RequiresApi(16)
        @JvmStatic
        fun calculateRam(@NonNull context: Context?): Double {
            if (context == null)
                throw NullPointerException("Context was null")

            return try {
                val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val memInfo = ActivityManager.MemoryInfo()
                actManager.getMemoryInfo(memInfo)
                val totalMemory = memInfo.totalMem
                totalMemory.toDouble() / 1024.0 / 1024.0
            } catch (ne: Exception) {
                Timber.w(ne)
                java.lang.Double.NaN
            }
        }

    }


    // =========================================================================================
    // Profile helpers
    // =========================================================================================

    @MemoryProfile
    fun profile(): Int {
        val memory: Double = calculateRam(context.get())

        if (memory == java.lang.Double.NaN)
            return UNKNOWN

        return when {
            memory <= lowMemThres -> LOW
            memory < highMemThres -> MEDIUM
            else -> HIGH
        }
    }


    // =========================================================================================
    // Inner classes
    // =========================================================================================

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(HIGH, MEDIUM, LOW, UNKNOWN)
    annotation class MemoryProfile
}
