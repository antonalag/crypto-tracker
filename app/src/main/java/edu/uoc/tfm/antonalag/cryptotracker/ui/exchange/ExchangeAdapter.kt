package edu.uoc.tfm.antonalag.cryptotracker.ui.exchange

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.roundToString
import edu.uoc.tfm.antonalag.cryptotracker.core.util.NumberUtil
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.LocalCryptocurrency
import edu.uoc.tfm.antonalag.cryptotracker.features.fiat.model.Fiat
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.GlideApp
import kotlinx.android.synthetic.main.exchange_cryptocurrency_item_view.view.*
import kotlin.properties.Delegates


class ExchangeAdapter(): ListAdapter<LocalCryptocurrency, ExchangeAdapter.ExchangeViewHolder>(
    exchangeDiffCallback
) {
    private lateinit var fiatSelected: Fiat
    private lateinit var fiatUserPreferences: Fiat
    private var quantity by Delegates.notNull<Double>()

    fun setData(fiatSelected: Fiat, fiatUserPreferences: Fiat, quantity: Double) {
        this.fiatSelected = fiatSelected
        this.fiatUserPreferences = fiatUserPreferences
        this.quantity = quantity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeViewHolder {
        return ExchangeViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.exchange_cryptocurrency_item_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ExchangeViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    inner class ExchangeViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bindTo(localCryptocurrency: LocalCryptocurrency) {
            val priceInDolars = NumberUtil.ruleOfThreeCalculateYValue(fiatUserPreferences.rate, 1, localCryptocurrency.price)
            val priceInFiatSelected = NumberUtil.ruleOfThreeCalculateXValue(fiatSelected.rate, 1, priceInDolars)
            val cryptocurrencyValue = NumberUtil.ruleOfThreeCalculateYValue(priceInFiatSelected, 1, quantity)

            itemView.exchange_item_cryptocurrency_name.text = localCryptocurrency.name
            GlideApp.with(itemView)
                .load(localCryptocurrency.icon)
                .centerCrop()
                .into(itemView.exchange_item_cryptocurrency_icon)
            itemView.exchange_item_cryptocurrency_value.text = cryptocurrencyValue.roundToString(4)
        }
    }

    companion object {
        val exchangeDiffCallback = object : DiffUtil.ItemCallback<LocalCryptocurrency>() {
            override fun areItemsTheSame(
                oldItem: LocalCryptocurrency,
                newItem: LocalCryptocurrency
            ): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(
                oldItem: LocalCryptocurrency,
                newItem: LocalCryptocurrency
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}