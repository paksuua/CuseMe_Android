package com.tistory.comfy91.excuseme_android.feature.login

import android.content.Context
import java.util.*

object Login {
    private const val LOGIN_KEY = "login"
    private const val USER_KEY = "user"
    private const val PASSWORD = "password"

    fun getPwFlag(context: Context):Boolean{
        context.getSharedPreferences(LOGIN_KEY, Context.MODE_PRIVATE)
            .let{
                return it.getBoolean(PASSWORD, false)
            }
    }

    fun savePwFlag(context: Context, flag: Boolean){
        context.getSharedPreferences(LOGIN_KEY, Context.MODE_PRIVATE)
            .let{
                it.edit()
                    .putBoolean(PASSWORD, flag)
                    .apply()
            }
    }

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

    private fun makeUUID(): String {
        return UUID.randomUUID().toString()
    }


    fun getUUID(context: Context): String {
        getUser(context).let {
            when (it) {
                "" -> {
                    makeUUID().let { uuid ->
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