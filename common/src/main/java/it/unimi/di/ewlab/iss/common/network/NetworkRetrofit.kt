package it.unimi.di.ewlab.iss.common.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkRetrofit {
    val retrofitApi: Retrofit = Retrofit.Builder()
        .baseUrl("https://develop.ewlab.di.unimi.it/playaccess/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}