package org.amoustakos.exifstripper

import android.content.Context
import io.realm.Realm
import org.amoustakos.exifstripper.di.annotations.context.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class Environment @Inject constructor(
    @ApplicationContext val context: Context
) {

    fun init() {
        initPrefs()
        initLog()
        initRealm()
    }



    private fun initPrefs() {}

    private fun initLog() {
        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }

    private fun initRealm() {
        Realm.init(context)

        //Get config here or realm config will be requested before realm init and crash
        Realm.setDefaultConfiguration(
                ExifApplication[context].component.defaultRealmConfig()
        )
    }

}
