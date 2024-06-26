package com.thosijulio.currencyview.data.api

import com.thosijulio.currencyview.data.models.CurrencyRateResponse
import com.thosijulio.currencyview.data.models.CurrencySymbolResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {
    @GET("symbols")
    suspend fun getSymbols(): Response<CurrencySymbolResponse>

    @GET("latest")
    suspend fun getLatestRates(
        @Query("base") baseCurrency: String,
    ): Response<CurrencyRateResponse>
}
