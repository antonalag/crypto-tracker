package edu.uoc.tfm.antonalag.cryptotracker.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.LocalCryptocurrency
import kotlinx.android.synthetic.main.cryptocurrency_list_item_view.view.*

class DialogInvestmentAdapter() :
    ListAdapter<LocalCryptocurrency, DialogInvestmentAdapter.DialogInvestmentViewHolder>(
        localCryptocurrencyListViewDiffCallback
    ) {

    var selectedCryptocurrency: LocalCryptocurrency? = null
    var selectedItemView: View? = null
    // Variables that allow only one item selected
    var lastItemSelectedPos = -1



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogInvestmentViewHolder {
        return DialogInvestmentViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cryptocurrency_list_item_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DialogInvestmentViewHolder, position: Int) {
        holder.bindTo(getItem(position), position)
    }


    inner class DialogInvestmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindTo(localCryptocurrency: LocalCryptocurrency, position: Int) {
            itemView.cryptocurrency_name.text = localCryptocurrency.name
            itemView.cryptocurrency_symbol.text = localCryptocurrency.symbol
            Glide.with(itemView)
                .load(localCryptocurrency.icon)
                .centerCrop()
                .into(itemView.cryptocurrency_icon)

            itemView.setOnClickListener {
                if (position != lastItemSelectedPos) {
                    itemView.cryptocurrency_list_item_view_container.background =
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.selected_border_shape
                        )

                    selectedItemView?.let {
                        it.cryptocurrency_list_item_view_container.background =
                            ContextCompat.getDrawable(
                                itemView.context,
                                R.drawable.background_selected_shape
                            )
                    }

                    selectedCryptocurrency = localCryptocurrency
                    selectedItemView = itemView
                    lastItemSelectedPos = position
                }
            }
        }
    }

    companion object {
        private val localCryptocurrencyListViewDiffCallback =
            object : DiffUtil.ItemCallback<LocalCryptocurrency>() {
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