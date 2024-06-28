package com.thosijulio.currencyview.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.textview.MaterialTextView
import com.thosijulio.currencyview.R

class CurrencyExchangeAdapter(private val currencyExchanges: Map<String, Double>) : Adapter<CurrencyExchangeAdapter.CurrencyExchangeViewHolder>(){
    class CurrencyExchangeViewHolder(view: View) : ViewHolder(view) {
        val currencyName: MaterialTextView = view.findViewById(R.id.currency_list_name)
        val currencyValue: MaterialTextView = view.findViewById(R.id.currency_list_value)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyExchangeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_currency_exchange, parent, false)
        return CurrencyExchangeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return currencyExchanges.count()
    }

    override fun onBindViewHolder(holder: CurrencyExchangeViewHolder, position: Int) {
        val currencyList = currencyExchanges.toList()

        val currency = currencyList[position].first
        val value = currencyList[position].second

        holder.currencyName.text = currency
        holder.currencyValue.text = value.toString()
    }
}