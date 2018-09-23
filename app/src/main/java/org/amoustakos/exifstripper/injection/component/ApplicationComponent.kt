package org.amoustakos.exifstripper.injection.component

import android.app.Application
import android.content.Context
import dagger.Component
import io.realm.RealmConfiguration
import okhttp3.OkHttpClient
import org.amoustakos.exifstripper.Environment
import org.amoustakos.exifstripper.injection.annotations.context.ApplicationContext
import org.amoustakos.exifstripper.injection.annotations.network.DefaultOkHttpClient
import org.amoustakos.exifstripper.injection.annotations.network.DefaultOkHttpOptions
import org.amoustakos.exifstripper.injection.annotations.network.DefaultRetrofitEngine
import org.amoustakos.exifstripper.injection.annotations.network.DefaultRetrofitOptions
import org.amoustakos.exifstripper.injection.annotations.realm.DefaultRealmConfig
import org.amoustakos.exifstripper.injection.module.ApplicationModule
import org.amoustakos.exifstripper.injection.module.DBModule
import org.amoustakos.exifstripper.injection.module.NetworkModule
import org.amoustakos.utils.network.retrofit.OkhttpOptions
import org.amoustakos.utils.network.retrofit.RetrofitEngineOptions
import retrofit2.Retrofit
import javax.inject.Singleton

@Singleton
@Component(modules = [
	ApplicationModule::class,
	DBModule::class,
	NetworkModule::class
])
interface ApplicationComponent {


	// =========================================================================================
	// Base
	// =========================================================================================

	@ApplicationContext
	fun context(): Context

	fun application(): Application
	fun environment(): Environment


	// =========================================================================================
	// DB
	// =========================================================================================

	@DefaultRealmConfig
	fun defaultRealmConfig(): RealmConfiguration

	// =========================================================================================
	// Network
	// =========================================================================================

	@DefaultRetrofitOptions
	fun defaultRetrofitOptions(): RetrofitEngineOptions

	@DefaultOkHttpOptions
	fun defaultOkHttpOptions(): OkhttpOptions

	@DefaultOkHttpClient
	fun defaultOkHttpClient(): OkHttpClient

	@DefaultRetrofitEngine
	fun defaultRetrofitEngine(): Retrofit


}
