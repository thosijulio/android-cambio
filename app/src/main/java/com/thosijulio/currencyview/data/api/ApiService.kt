package com.thosijulio.currencyview.data.api

import com.thosijulio.currencyview.data.models.CurrencyRateResponse
import com.thosijulio.currencyview.data.models.CurrencySymbolResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {
    @GET("/symbols")
    suspend fun getSymbols(@Header("apikey") apiKey: String): Response<CurrencySymbolResponse>

    @GET("/latest")
    suspend fun getLatestRates(
        @Header("apikey") apiKey: String,
        @Query("symbols")symbols: List<String>,
        @Query("base") baseCurrency: String
    ): Response<CurrencyRateResponse>
}
