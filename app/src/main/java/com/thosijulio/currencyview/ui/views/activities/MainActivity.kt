package com.thosijulio.currencyview.ui.views.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.thosijulio.currencyview.R
import com.thosijulio.currencyview.common.ApiIdlingResource
import com.thosijulio.currencyview.data.api.ApiServiceClient
import com.thosijulio.currencyview.ui.adapters.CurrencyExchangeAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val autoCompleteTextView: AutoCompleteTextView by lazy { findViewById(R.id.currency_selection_input_layout)}
    private val loadCurrencyState: MaterialTextView by lazy { findViewById(R.id.load_currency_state) }
    private val selectCurrencyState: MaterialTextView by lazy { findViewById(R.id.select_currency_state) }
    private val currencyRecyclerView: RecyclerView by lazy { findViewById(R.id.currency_rates_state) }
    private val waitingResponseState: FrameLayout by lazy { findViewById(R.id.waiting_response_state) }

    private val apiInstance = ApiServiceClient.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                ApiIdlingResource.increment()

                withContext(Dispatchers.Main) {
                    loadCurrencyState.visibility = View.VISIBLE
                }

                val response = apiInstance.getSymbols()
                yield()

                withContext(Dispatchers.Main) {
                    loadCurrencyState.visibility = View.GONE
                }

                if(response.isSuccessful) {
                    val body = response.body()
                    val symbolsList = body?.symbols?.map { it -> it.key} ?: emptyList()

                    withContext(Dispatchers.Main) {
                        selectCurrencyState.visibility = View.VISIBLE
                        val adapter = ArrayAdapter(baseContext, android.R.layout.simple_dropdown_item_1line, symbolsList)
                        autoCompleteTextView.setAdapter(adapter)

                        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
                            selectCurrencyState.visibility = View.GONE
                            waitingResponseState.visibility = View.VISIBLE
                            val selectedCurrency = symbolsList[position]

                            CoroutineScope(Dispatchers.IO).launch {
                                ApiIdlingResource.increment()
                                val currencyRatesResponse = apiInstance.getLatestRates(selectedCurrency)

                                if (currencyRatesResponse.isSuccessful) {
                                    withContext(Dispatchers.Main) {
                                        waitingResponseState.visibility = View.GONE
                                        currencyRecyclerView.visibility = View.VISIBLE
                                        val currencyAdapter = CurrencyExchangeAdapter(currencyRatesResponse.body()?.rates ?: emptyMap())
                                        currencyRecyclerView.layoutManager = LinearLayoutManager(baseContext)
                                        currencyRecyclerView.adapter = currencyAdapter

                                    }
                                } else {
                                    withContext(Dispatchers.Main) {
                                        waitingResponseState.visibility = View.GONE
                                        MaterialAlertDialogBuilder(baseContext)
                                            .setTitle("Erro").setMessage("Erro ao carregar as taxas de câmbio")
                                            .setPositiveButton("OK", null).show()
                                        Log.e("API Error", "Error: ${currencyRatesResponse.code()} - ${currencyRatesResponse.errorBody()?.string()}")
                                    }
                                }
                                ApiIdlingResource.decrement()
                            }
                        }
                    }
                }   else {
                    MaterialAlertDialogBuilder(baseContext).setTitle("Erro").setMessage("Erro ao carregar as moedas disponíveis").setPositiveButton("OK", null).show()
                    val errorBody = response.errorBody()?.string()
                    Log.e("API Error", "Error: ${response.code()} - $errorBody")

                }
                ApiIdlingResource.decrement()
            } catch (exception: HttpException) {
                ApiIdlingResource.decrement()
                MaterialAlertDialogBuilder(baseContext).setTitle("Erro").setMessage("Erro ao carregar as moedas disponíveis").setPositiveButton("OK", null).show()
                Log.e("HttpException", exception.message())
            } catch (exception: IOException) {
                ApiIdlingResource.decrement()
                MaterialAlertDialogBuilder(baseContext).setTitle("Erro").setMessage("Erro ao realizar a conexão com a API. Verifique a internet e tente novamente.").setPositiveButton("OK", null).show()
                Log.e("IoException", exception.message ?: "Io Error")
            } catch (exception: Exception) {
                ApiIdlingResource.decrement()
                MaterialAlertDialogBuilder(baseContext).setTitle("Erro").setMessage("Ocorreu um erro inesperado. Tente novamente.").setPositiveButton("OK", null).show()
                Log.e("Generic Exception", exception.message ?: "Unknown Error")
            }
        }
    }
}
