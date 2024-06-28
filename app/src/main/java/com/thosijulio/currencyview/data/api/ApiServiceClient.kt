package com.thosijulio.currencyview.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiServiceClient {
    private const val BASE_URL = "https://api.apilayer.com/exchangerates_data/"
    private const val API_KEY = "exsfkvqXJVabIILvPq5i7kIEWK1cDSen"

    val instance: ApiService by lazy {
        val apiInterceptor = ApiInterceptor(API_KEY)

        val client = OkHttpClient.Builder().addInterceptor(apiInterceptor).build()

        val retrofit = Retrofit.Builder().client(client)
            .baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
        retrofit.create(ApiService::class.java)
    }

}