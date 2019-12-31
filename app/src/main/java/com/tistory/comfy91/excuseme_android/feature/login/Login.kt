package com.tistory.comfy91.excuseme_android.feature.login

import android.content.Context
import java.util.*

object Login {
    private const val LOGIN_KEY = "login"
    private const val USER_KEY = "user"

    fun getUser(context: Context): String {
        context.getSharedPreferences(LOGIN_KEY, Context.MODE_PRIVATE)
            .let {
                return it.getString(USER_KEY, "") ?: ""
            }
    }

    fun saveUser(context: Context, user: String){
        context.getSharedPreferences(LOGIN_KEY, Context.MODE_PRIVATE)
            .let {
                it.edit()
                    .putString(USER_KEY, user)
                    .apply()

            }
    }

    fun clearUser(context: Context) {
        context.getSharedPreferences(LOGIN_KEY, Context.MODE_PRIVATE)
            .let {
                it.edit()
                    .clear()
                    .apply()
            }

    }

    fun getUUID(): String {
        return UUID.randomUUID().toString()
    }


    fun login(context: Context): String {
        getUser(context).let {
            when (it) {
                "" -> {
                    getUUID().let { uuid ->
                        saveUser(
                            context,
                            uuid
                        )
                        return uuid
                    }
                }
                else -> return it
            }
        }
    }
}