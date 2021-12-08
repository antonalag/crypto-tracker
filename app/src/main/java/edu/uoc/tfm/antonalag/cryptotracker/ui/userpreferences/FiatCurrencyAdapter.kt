package edu.uoc.tfm.antonalag.cryptotracker.ui.userpreferences

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.CryptocurrencyListViewDto
import edu.uoc.tfm.antonalag.cryptotracker.features.fiat.model.Fiat
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.GlideApp
import kotlinx.android.synthetic.main.cryptocurrency_list_item_view.view.*
import kotlinx.android.synthetic.main.fiat_currency_list_item_view.view.*

class FiatCurrencyAdapter() : ListAdapter<Fiat, FiatCurrencyAdapter.FiatCurrencyViewHolder>(
    fiatCurrencyListViewDiffCallback
) {

    lateinit var fiatSelected: String
    lateinit var fiatSymbolSelected: String
    var selectedItemView: View? = null
    // Variables that allow only one item selected
    var lastItemSelectedPos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FiatCurrencyViewHolder {
        return FiatCurrencyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fiat_currency_list_item_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FiatCurrencyViewHolder, position: Int) {
        holder.bindTo(getItem(position), position)
    }


    inner class FiatCurrencyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindTo(fiat: Fiat, position: Int) {
            itemView.fiat_name.text = fiat.name
            GlideApp.with(itemView)
                .load(fiat.imageUrl)
                .centerCrop()
                .into(itemView.fiat_image)

            if(fiat.name == fiatSelected) {
                itemView.fiat_container.background = ContextCompat.getDrawable(
                    itemView.context,
                    R.drawable.selected_border_shape
                )
                selectedItemView = itemView
            } else {
                itemView.fiat_container.background =
                    ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.background_selected_shape
                    )
            }

            itemView.setOnClickListener {
                if(position != lastItemSelectedPos) {
                    itemView.fiat_container.background = ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.selected_border_shape
                    )

                    selectedItemView?.let {
                        it.fiat_container.background =
                            ContextCompat.getDrawable(
                                itemView.context,
                                R.drawable.background_selected_shape
                            )
                    }
                    fiatSelected = fiat.name
                    fiatSymbolSelected = fiat.symbol
                    selectedItemView = itemView
                    lastItemSelectedPos = position
                }
            }
        }
    }

    companion object {
        private val fiatCurrencyListViewDiffCallback =
            object : DiffUtil.ItemCallback<Fiat>() {
                override fun areItemsTheSame(
                    oldItem: Fiat,
                    newItem: Fiat
                ): Boolean {
                    return oldItem.name == newItem.name
                }

                override fun areContentsTheSame(
                    oldItem: Fiat,
                    newItem: Fiat
                ): Boolean {
                    return oldItem == newItem
                }

            }
    }
}