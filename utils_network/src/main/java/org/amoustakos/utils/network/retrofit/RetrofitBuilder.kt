package org.amoustakos.utils.network.retrofit

import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.io.File
import java.util.concurrent.TimeUnit


object RetrofitBuilder {


    @JvmStatic
    fun getRetrofitEngine(okHttpClient: OkHttpClient, opts: RetrofitEngineOptions): Retrofit {
        val builder = Retrofit.Builder()
                .baseUrl(opts.baseUrl)
                .client(okHttpClient)

        opts.adapterFactories.forEach { adapter -> builder.addCallAdapterFactory(adapter) }
        opts.converterFactories.forEach { converter -> builder.addConverterFactory(converter) }

        return builder.build()
    }


    @JvmStatic
    fun <T> newService(engine: Retrofit, service: Class<T>): T = engine.create(service)


    @JvmStatic
    fun getHttpClient(opts: OkhttpOptions): OkHttpClient {
        val client = OkHttpClient.Builder()

        // Cache

        if (opts.cacheSize > 0 && opts.cacheDir != null) {
            val cache = Cache(
                    File(opts.cacheDir, opts.cacheSubDirectory),
                    opts.cacheSize)
            client.cache(cache)
        } else if (opts.cacheSize > 0 && opts.cacheDir == null)
            throw IllegalArgumentException("You need to provide a directory to enable caching")


        // Connection

        client.connectTimeout(opts.connectTimeout.toLong(), TimeUnit.SECONDS)
        client.readTimeout(opts.readTimeout.toLong(), TimeUnit.SECONDS)
        client.writeTimeout(opts.writeTimeout.toLong(), TimeUnit.SECONDS)


        // Logging interceptor

        val loggingInterceptor = HttpLoggingInterceptor()
        if (opts.isDebug)
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        else
        // every time you log in production a puppy dies.
            loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE

        client.addInterceptor(loggingInterceptor)


        // Provided interceptors

        if (opts.interceptors != null)
            opts.interceptors.forEach { interceptor -> client.addInterceptor(interceptor) }


        return client.build()
    }

}
