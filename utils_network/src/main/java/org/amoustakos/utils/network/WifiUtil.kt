package org.amoustakos.utils.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager


object WifiUtil {

    /**
     * Checks if the device is connected to a WiFi network
     */
    fun isConnected(context: Context): Boolean {
        val conManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = conManager.activeNetworkInfo

        return (net != null
                && net.isConnected
                && ConnectivityManager.TYPE_WIFI == net.type)
    }

    /**
     * Sets the state of the WiFi antenna (on = true, off = false) <br></br>
     * USES: android.permission.CHANGE_WIFI_STATE
     */
    fun setState(context: Context, active: Boolean): Boolean {
        val wifiManager =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.setWifiEnabled(active)
    }

    /**
     * Returns the state of the WiFi antenna (on = true, off = false)
     */
    fun getState(context: Context): Boolean {
        val wifiManager =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.isWifiEnabled
    }

}
