package org.amoustakos.utils.io.preferences


import android.content.Context
import android.content.SharedPreferences

abstract class BaseSharedPreferences(fileName: String, mode: Int, con: Context) {

    protected var prefs: SharedPreferences = con.getSharedPreferences(fileName, mode)


    fun set(key: String, value: String) {
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun set(key: String, value: Set<String>) {
        val editor = prefs.edit()
        editor.putStringSet(key, value)
        editor.apply()
    }

    fun set(key: String, value: Int) {
        val editor = prefs.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun set(key: String, value: Float) {
        val editor = prefs.edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    fun set(key: String, value: Long) {
        val editor = prefs.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun set(key: String, value: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }



    fun get(key: String, defValue: String?): String? = prefs.getString(key, defValue)
    fun get(key: String, defValue: Set<String>?): MutableSet<String>? = prefs.getStringSet(key, defValue)
    fun get(key: String, defValue: Int) = prefs.getInt(key, defValue)
    fun get(key: String, defValue: Float) = prefs.getFloat(key, defValue)
    fun get(key: String, defValue: Long) = prefs.getLong(key, defValue)
    fun get(key: String, defValue: Boolean) = prefs.getBoolean(key, defValue)

}
