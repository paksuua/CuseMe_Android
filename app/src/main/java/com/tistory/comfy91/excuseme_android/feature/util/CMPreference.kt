package com.tistory.comfy91.excuseme_android.feature.util

import android.content.Context
import android.content.SharedPreferences
import com.tistory.comfy91.excuseme_android.feature.util.BaseApplication.Companion.app

object CMPreference {

    private val preference: SharedPreferences =
        app.applicationContext.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)

    private val IS_FIRST_LOGIN = "IS_FIRST_LOGIN"

    private operator fun set(key: String, value: Any?) {
        when (value) {
            is String? -> preference.edit().putString(key, value).apply()
            is Int -> preference.edit().putInt(key, value).apply()
            is Boolean -> preference.edit().putBoolean(key, value).apply()
            is Float -> preference.edit().putFloat(key, value).apply()
            is Long -> preference.edit().putLong(key, value).apply()
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }

    private inline operator fun <reified T : Any> get(key: String, defaultValue: T? = null): T? {
        return when (T::class) {
            String::class -> preference.getString(key, defaultValue as? String) as T?
            Int::class -> preference.getInt(key, defaultValue as? Int ?: -1) as T?
            Boolean::class -> preference.getBoolean(key, defaultValue as? Boolean ?: true) as T?
            Float::class -> preference.getFloat(key, defaultValue as? Float ?: -1f) as T?
            Long::class -> preference.getLong(key, defaultValue as? Long ?: -1) as T?
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }

    fun getIsFirstLogin(): Boolean? {
        return get(IS_FIRST_LOGIN)
    }

    fun setIsFirstLogin(isFirst: Boolean) {
        set(IS_FIRST_LOGIN, isFirst)
    }
}