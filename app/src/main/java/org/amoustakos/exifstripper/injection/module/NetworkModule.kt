package org.amoustakos.exifstripper.injection.module

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import org.amoustakos.exifstripper.BuildConfig
import org.amoustakos.exifstripper.examples.io.remote.ExampleNetCall
import org.amoustakos.exifstripper.injection.annotations.network.DefaultOkHttpClient
import org.amoustakos.exifstripper.injection.annotations.network.DefaultOkHttpOptions
import org.amoustakos.exifstripper.injection.annotations.network.DefaultRetrofitEngine
import org.amoustakos.exifstripper.injection.annotations.network.DefaultRetrofitOptions
import org.amoustakos.exifstripper.net.RetrofitConfig
import org.amoustakos.utils.network.retrofit.OkhttpOptions
import org.amoustakos.utils.network.retrofit.RetrofitBuilder
import org.amoustakos.utils.network.retrofit.RetrofitEngineOptions
import retrofit2.Retrofit

/**
 * Created by antonis on 15/01/2018.
 */

@Module
object NetworkModule {


	@Provides
	@DefaultRetrofitOptions
	internal fun provideDefaultRetrofitOptions() = RetrofitConfig.defaultOptions()

	@Provides
	@DefaultOkHttpOptions
	internal fun provideDefaultOkHttpOptions() = OkhttpOptions(BuildConfig.DEBUG)

	@Provides
	@DefaultOkHttpClient
	internal fun provideDefaultOkHttpClient(@DefaultOkHttpOptions opts: OkhttpOptions): OkHttpClient {
		return RetrofitBuilder.getHttpClient(opts)
	}

	@Provides
	@DefaultRetrofitEngine
	internal fun provideDefaultRetrofitEngine(
			@DefaultOkHttpClient client: OkHttpClient,
			@DefaultRetrofitOptions opts: RetrofitEngineOptions): Retrofit {
		return RetrofitBuilder.getRetrofitEngine(client, opts)
	}



	// =========================================================================================
	// APIs
	// =========================================================================================

	@Provides
	internal fun provideExampleApiService(@DefaultRetrofitEngine engine: Retrofit) =
			RetrofitBuilder.newService(engine, ExampleNetCall.ApiService::class.java)


}
