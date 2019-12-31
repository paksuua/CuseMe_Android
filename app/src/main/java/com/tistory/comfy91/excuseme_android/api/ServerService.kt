package com.tistory.comfy91.excuseme_android.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServerService{
    private const val BASE_URL = ""

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: Service = retrofit.create(Service::class.java)

}