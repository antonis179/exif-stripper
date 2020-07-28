package org.amoustakos.exifstripper.utils

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import org.amoustakos.exifstripper.usecases.privacy.AnalyticsUtil

object FBRemoteConfigUtility {

	private const val KEY_SHOW = "ads_show"
	private const val KEY_NETWORK = "ads_network"

	private val defaults = mapOf(
			KEY_SHOW to true,
			KEY_NETWORK to "APPODEAL"
	)


	private var fetched: Boolean = false
		@Synchronized get
		@Synchronized set

	private var defaultsSet: Boolean = false
		@Synchronized get
		@Synchronized set

	private val observers: MutableList<FBRemoteConfigInitListener> = mutableListOf()

	fun registerCallback(listener: FBRemoteConfigInitListener) {
		if (observers.contains(listener)) return
		observers.add(listener)
		if (fetched)
			listener.onRemoteInit()
		else if (defaultsSet)
			listener.onDefaultsLoaded()
	}

	fun unregisterCallback(listener: FBRemoteConfigInitListener) {
		observers.remove(listener)
	}


	fun initialize() {
		fetched = false
		Firebase.remoteConfig.apply {
			remoteConfigSettings {
				minimumFetchIntervalInSeconds = 720
				setDefaultsAsync(defaults)
                        .addOnCompleteListener {
                            defaultsSet = it.isSuccessful
                            remoteFetch()
                        }
						.addOnSuccessListener {
							observers.forEach { it.onDefaultsLoaded() }
						}.addOnFailureListener {
							observers.forEach { it.onFailDefaults() }
						}
			}
		}
	}

    private fun remoteFetch() {
        Firebase.remoteConfig.fetchAndActivate()
                .addOnCompleteListener { fetched = it.isSuccessful }
                .addOnSuccessListener { observers.forEach { it.onRemoteInit() } }
                .addOnFailureListener { exc ->
                    AnalyticsUtil.logException(exc)
                    observers.forEach { it.onFailRemote() }
                }
    }


    fun shouldShowAds() = Firebase.remoteConfig.getBoolean(KEY_SHOW)

	fun adNetwork() = Firebase.remoteConfig.getString(KEY_NETWORK)


}

interface FBRemoteConfigInitListener {
	fun onRemoteInit()
	fun onDefaultsLoaded()
	fun onFailDefaults()
	fun onFailRemote()
}