package org.amoustakos.exifstripper

import android.content.Context
import timber.log.Timber

class Environment (
    val context: Context
) {

    init {
        initPrefs()
        initLog()
//        initRealm()
    }



    private fun initPrefs() {}

    private fun initLog() {
        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }

//    private fun initRealm() {
//        Realm.init(context)
//
//        //Get config here or realm config will be requested before realm init and crash
//        Realm.setDefaultConfiguration(
//                RealmConfig.defaultConfig()
//        )
//    }

}
