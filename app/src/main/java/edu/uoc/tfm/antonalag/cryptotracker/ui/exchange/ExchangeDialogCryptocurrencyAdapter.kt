package edu.uoc.tfm.antonalag.cryptotracker.ui.exchange

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.CryptocurrencyListViewDto
import kotlinx.android.synthetic.main.cryptocurrency_list_item_view.view.*

class ExchangeDialogCryptocurrencyAdapter(): ListAdapter<CryptocurrencyListViewDto, ExchangeDialogCryptocurrencyAdapter.ExchangeCryptocurrencyViewHolder>(
    exchangeCryptocurrencyListViewDiffCallback
) {

    var selectedCryptocurrencyLisViewDto: CryptocurrencyListViewDto? = null
    var selectedItemView: View? = null
    // Variables that allow only one item selected
    var lastItemSelectedPos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeDialogCryptocurrencyAdapter.ExchangeCryptocurrencyViewHolder {
        return ExchangeCryptocurrencyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cryptocurrency_list_item_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ExchangeDialogCryptocurrencyAdapter.ExchangeCryptocurrencyViewHolder, position: Int) {
        holder.bindTo(getItem(position), position)
    }

    inner class ExchangeCryptocurrencyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bindTo(cryptocurrencyListViewDto: CryptocurrencyListViewDto, position: Int) {
            itemView.cryptocurrency_name.text = cryptocurrencyListViewDto.name
            itemView.cryptocurrency_symbol.text = cryptocurrencyListViewDto.symbol
            Glide.with(itemView)
                .load(cryptocurrencyListViewDto.icon)
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

                    selectedCryptocurrencyLisViewDto = cryptocurrencyListViewDto
                    selectedItemView = itemView
                    lastItemSelectedPos = position
                }
            }
        }
    }

    companion object {
        private val exchangeCryptocurrencyListViewDiffCallback =
            object : DiffUtil.ItemCallback<CryptocurrencyListViewDto>() {
                override fun areItemsTheSame(
                    oldItem: CryptocurrencyListViewDto,
                    newItem: CryptocurrencyListViewDto
                ): Boolean {
                    return oldItem.name == newItem.name
                }

                override fun areContentsTheSame(
                    oldItem: CryptocurrencyListViewDto,
                    newItem: CryptocurrencyListViewDto
                ): Boolean {
                    return oldItem == newItem
                }

            }
    }
}