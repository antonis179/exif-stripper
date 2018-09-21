package org.amoustakos.utils.android

import android.text.TextUtils
import timber.log.Timber
import java.net.MalformedURLException
import java.net.URL
import java.util.regex.Pattern


object ValidationUtil {


    fun isValidEmail(target: CharSequence): Boolean {
        try {
            return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
        } catch (e: Exception) {
            Timber.d(e)
            return false
        }

    }


    fun isValidURL(url: String): Boolean {
        try {
            URL(url)
            return true
        } catch (e: MalformedURLException) {
            Timber.d(e)
            return false
        }

    }


    fun isValidIPv4(ip: String): Boolean {
        try {
            val ipPattern = Pattern.compile(
                    "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$")
            return ipPattern.matcher(ip).matches()
        } catch (e: Exception) {
            Timber.d(e)
            return false
        }

    }


    fun isValidPort(port: String): Boolean {
        return try {
            val fPort = Integer.parseInt(port)
            fPort in 1..65535
        } catch (e: Exception) {
            Timber.d(e)
            false
        }

    }

}
