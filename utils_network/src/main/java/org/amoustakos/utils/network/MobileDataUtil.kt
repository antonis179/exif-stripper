package org.amoustakos.utils.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import timber.log.Timber
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


/**
 * <h1>WARNING</h1>
 * NOTE: NEED TO INSTALL AS SYSTEM APP FOR THIS UTILITY TO WORK.
 */
@SuppressLint("PrivateApi")
object MobileDataUtil {

    /**
     * Checks if the device is connected to a mobile network
     */
    @Throws(NullPointerException::class)
    fun isConnected(context: Context): Boolean {
        val conManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = conManager.activeNetworkInfo

        return (net != null
                && net.isConnected
                && ConnectivityManager.TYPE_MOBILE == net.type)
    }


    /**
     * Retrieves the state of the mobile data (on = true, off = false)
     */
    fun getState(context: Context): Boolean {
        try {
            val telephonyService =
                    context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val getMobileDataEnabledMethod =
                    telephonyService.javaClass.getDeclaredMethod("getDataEnabled")

            if (null != getMobileDataEnabledMethod) {
                return getMobileDataEnabledMethod.invoke(telephonyService) as Boolean
            }
        } catch (e: Exception) {
            Timber.w(e)
        }

        return false
    }


    /**
     * Sets the state of the mobile data (on = true, off = false)
     */
    fun setState(context: Context, active: Boolean): Boolean {
        val conman = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val dataMtd: Method
        try {
            dataMtd = ConnectivityManager::class.java.
                    getDeclaredMethod("setMobileDataEnabled",
                            Boolean::class.javaPrimitiveType)
        } catch (e: NoSuchMethodException) {
            Timber.w(e, e.message)
            return false
        }

        dataMtd.isAccessible = true
        try {
            dataMtd.invoke(conman, active)
        } catch (e: IllegalAccessException) {
            Timber.w(e)
            return false
        } catch (e: InvocationTargetException) {
            Timber.w(e)
            return false
        }

        return true
    }


    /**
     * Checks if there is fast connectivity
     */
    fun isConnectedFast(context: Context): Boolean {
        val info = NetworkUtil.getNetworkInfo(context)
        return info != null && info.isConnected && isConnectionFast(info.type, info.subtype)
    }


    /**
     * Checks if the connection is fast
     */
    fun isConnectionFast(type: Int, subType: Int): Boolean {
        return if (type == ConnectivityManager.TYPE_WIFI)
            true
        else if (type == ConnectivityManager.TYPE_MOBILE)
            when (subType) {
                TelephonyManager.NETWORK_TYPE_1xRTT -> false // ~ 50-100 kbps
                TelephonyManager.NETWORK_TYPE_CDMA -> false // ~ 14-64 kbps
                TelephonyManager.NETWORK_TYPE_EDGE -> false // ~ 50-100 kbps
                TelephonyManager.NETWORK_TYPE_EVDO_0 -> true // ~ 400-1000 kbps
                TelephonyManager.NETWORK_TYPE_EVDO_A -> true // ~ 600-1400 kbps
                TelephonyManager.NETWORK_TYPE_GPRS -> false // ~ 100 kbps
                TelephonyManager.NETWORK_TYPE_HSDPA -> true // ~ 2-14 Mbps
                TelephonyManager.NETWORK_TYPE_HSPA -> true // ~ 700-1700 kbps
                TelephonyManager.NETWORK_TYPE_HSUPA -> true // ~ 1-23 Mbps
                TelephonyManager.NETWORK_TYPE_UMTS -> true // ~ 400-7000 kbps
                TelephonyManager.NETWORK_TYPE_EHRPD -> true // ~ 1-2 Mbps
                TelephonyManager.NETWORK_TYPE_EVDO_B -> true // ~ 5 Mbps
                TelephonyManager.NETWORK_TYPE_HSPAP -> true // ~ 10-20 Mbps
                TelephonyManager.NETWORK_TYPE_IDEN -> false // ~25 kbps
                TelephonyManager.NETWORK_TYPE_LTE -> true // ~ 10+ Mbps
            // Unknown
            //TelephonyManager.NETWORK_TYPE_UNKNOWN:
                else -> false
            }
        else
            false
    }
}
