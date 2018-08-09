package org.amoustakos.utils.network.retrofit

import okhttp3.Interceptor
import java.io.File


/**
 * Use this class to provide options for [RetrofitBuilder.getHttpClient].
 *
 * Defaults are provided for all options.
 */

data class OkhttpOptions @JvmOverloads constructor(

        // Debug
        val isDebug: Boolean = false,

        // Caching
        val cacheSize: Long = -1L,                      // MB
        val cacheDir: File? = null,                     // Root folder
        val cacheSubDirectory: String = "http",         // Subfolder

        // Connection
        val connectTimeout: Int = 15,                   // Seconds
        val readTimeout: Int = 15,                      // Seconds
        val writeTimeout: Int = 15,                     // Seconds

        // Interceptors
        val interceptors: List<Interceptor>? = null

)
