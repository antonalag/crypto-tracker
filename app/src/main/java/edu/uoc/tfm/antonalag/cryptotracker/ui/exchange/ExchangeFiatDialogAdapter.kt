package edu.uoc.tfm.antonalag.cryptotracker.ui.exchange

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.features.fiat.model.Fiat
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.GlideApp
import kotlinx.android.synthetic.main.fiat_currency_list_item_view.view.*

class ExchangeFiatDialogAdapter(): ListAdapter<Fiat, ExchangeFiatDialogAdapter.ExchangeFiatViewHolder>(
    exchangeFiatDiffCallback
) {

    var fiatSelected: Fiat? = null
    lateinit var fiatSymbolSelected: String
    var selectedItemView: View? = null
    // Variables that allow only one item selected
    var lastItemSelectedPos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeFiatViewHolder {
        return ExchangeFiatViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fiat_currency_list_item_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ExchangeFiatViewHolder, position: Int) {
        holder.bindTo(getItem(position), position)
    }


    inner class ExchangeFiatViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bindTo(fiat: Fiat, position: Int) {
            itemView.fiat_name.text = fiat.name
            GlideApp.with(itemView)
                .load(fiat.imageUrl)
                .centerCrop()
                .into(itemView.fiat_image)

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
                    fiatSelected = fiat
                    fiatSymbolSelected = fiat.symbol
                    selectedItemView = itemView
                    lastItemSelectedPos = position
                }
            }
        }
    }

    companion object {
        private val exchangeFiatDiffCallback =
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