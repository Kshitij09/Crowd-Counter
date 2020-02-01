package com.kshitijpatil.crowdcounter.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {
    var logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
    var client: OkHttpClient = OkHttpClient.Builder().addInterceptor(logging).build()

    private var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://192.168.43.111:5000")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun <S> createService(serviceClass: Class<S>): S {
        return retrofit.create(serviceClass)
    }

}