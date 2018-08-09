package org.amoustakos.utils.network.retrofit

import com.google.gson.Gson
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Arrays.asList


/**
 * Use this class to provide arguments for [RetrofitBuilder.getRetrofitEngine].
 *
 * By default a [GsonConverterFactory] and an [RxJava2CallAdapterFactory] are used.
 *
 * Note: To grab a string add a ScalarsConverterFactory.create() and enable the dependency
 */

data class RetrofitEngineOptions @JvmOverloads constructor(

        val baseUrl: String,


        val converterFactories: List<Converter.Factory> = asList(
                GsonConverterFactory.create(Gson())),
        val adapterFactories: List<CallAdapter.Factory> = asList(
                RxJava2CallAdapterFactory.create())
)