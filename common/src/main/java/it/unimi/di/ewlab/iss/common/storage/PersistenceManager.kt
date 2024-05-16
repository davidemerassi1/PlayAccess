package it.unimi.di.ewlab.iss.common.storage

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersistenceManager @Inject constructor(
    @ApplicationContext val context: Context
) {

    companion object {
        private const val TAG = "PersistanceManager"
    }

    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences("data", Context.MODE_PRIVATE)

    fun <T> setValue(key: String, value: T) =
        with(sharedPreferences.edit()) {
            when (value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
                null -> remove(key)
            }
            apply()
        }

    fun remove(key: String) =
        with(sharedPreferences.edit()) {
            remove(key)
            apply()
        }

    fun <T : Any> getValue(key: String, defaultVal: T?): Serializable {
        return when (defaultVal) {
            is String, null -> sharedPreferences.getString(key, defaultVal.toString())
                ?: defaultVal.toString()
            is Int -> sharedPreferences.getInt(key, defaultVal.toInt())
            is Boolean -> sharedPreferences.getBoolean(key, defaultVal)
            is Float -> sharedPreferences.getFloat(key, defaultVal.toFloat())
            is Long -> sharedPreferences.getLong(key, defaultVal.toLong())
            else -> throw IllegalArgumentException("Type mismatch")
        }
    }

    fun contains(key: String): Boolean {
        return sharedPreferences.contains(key)
    }

}