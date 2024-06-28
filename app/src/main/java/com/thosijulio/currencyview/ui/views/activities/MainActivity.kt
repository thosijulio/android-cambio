package com.thosijulio.currencyview.ui.views.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.textview.MaterialTextView
import com.thosijulio.currencyview.R
import com.thosijulio.currencyview.common.ApiIdlingResource
import com.thosijulio.currencyview.data.api.ApiServiceClient
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
                    }
                }   else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("API Error", "Error: ${response.code()} - $errorBody")

                }
                ApiIdlingResource.decrement()
            } catch (exception: HttpException) {
                ApiIdlingResource.decrement()
                Log.e("Erro", exception.message())
            } catch (exception: IOException) {
                ApiIdlingResource.decrement()
                Log.e("Io Error", exception.message ?: "Io Error")
            } catch (exception: Exception) {
                ApiIdlingResource.decrement()
                Log.e("Erro genérico", exception.message ?: "Unknown Error")
            }
        }
    }
}
