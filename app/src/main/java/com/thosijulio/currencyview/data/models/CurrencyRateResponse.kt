package com.thosijulio.currencyview.data.models

data class CurrencyRateResponse(
    val base: String,
    val success: Boolean,
    val rates: Map<String, Double>,
    val date: String,
    val timestamp: Long
)
