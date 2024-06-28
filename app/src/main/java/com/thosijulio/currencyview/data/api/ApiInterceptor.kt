package com.thosijulio.currencyview.data.api

import okhttp3.Interceptor
import okhttp3.Response

class ApiInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newRequest = request.newBuilder().addHeader("apikey", apiKey).build()
        return chain.proceed(newRequest)
    }
}