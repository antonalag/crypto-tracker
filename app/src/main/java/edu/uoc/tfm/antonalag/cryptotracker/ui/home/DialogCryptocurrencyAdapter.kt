package edu.uoc.tfm.antonalag.cryptotracker.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import edu.uoc.tfm.antonalag.cryptotracker.CryptoTrackerApp
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.CryptocurrencyListViewDto
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.LocalCryptocurrency
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.GlideApp
import kotlinx.android.synthetic.main.cryptocurrency_list_item_view.view.*

class DialogCryptocurrencyAdapter() :
    ListAdapter<CryptocurrencyListViewDto, DialogCryptocurrencyAdapter.DialogCryptocurrencyViewHolder>(
        cryptocurrencyListViewDiffCallback
    ) {

    private val userId = CryptoTrackerApp.instance.user.id

    lateinit var savedCryptocurrencies: List<LocalCryptocurrency>

    var selectedCryptocurrencies: MutableList<LocalCryptocurrency> = mutableListOf()

    private fun cryptocurrencyIsSaved(name: String): Boolean {
        return savedCryptocurrencies.any { it.name.equals(name, ignoreCase = true) }
    }

    private fun cryptocurrencyIsSelected(name: String): Boolean {
        return selectedCryptocurrencies.any { it.name.equals(name, ignoreCase = true) }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DialogCryptocurrencyViewHolder {
        return DialogCryptocurrencyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cryptocurrency_list_item_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DialogCryptocurrencyViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    inner class DialogCryptocurrencyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindTo(cryptocurrencyListViewDto: CryptocurrencyListViewDto) {
            itemView.cryptocurrency_name.text = cryptocurrencyListViewDto.name
            itemView.cryptocurrency_symbol.text = cryptocurrencyListViewDto.symbol

            GlideApp.with(itemView)
                .load(cryptocurrencyListViewDto.icon)
                .centerCrop()
                .into(itemView.cryptocurrency_icon)

            if (cryptocurrencyIsSaved(cryptocurrencyListViewDto.name)) {
                itemView.isEnabled = false
            } else {
                itemView.setOnClickListener {
                    if (cryptocurrencyIsSelected(cryptocurrencyListViewDto.name)) {
                        itemView.cryptocurrency_list_item_view_container.background =
                            ContextCompat.getDrawable(
                                itemView.context,
                                R.drawable.background_selected_shape
                            )
                        selectedCryptocurrencies.removeIf {
                            it.name.equals(
                                cryptocurrencyListViewDto.name,
                                ignoreCase = true
                            )
                        }
                    } else {
                        selectedCryptocurrencies.add(
                            LocalCryptocurrency(
                                cryptocurrencyListViewDto.name.toLowerCase(),
                                cryptocurrencyListViewDto.symbol,
                                cryptocurrencyListViewDto.icon,
                                cryptocurrencyListViewDto.price,
                                userId
                            )
                        )
                        itemView.cryptocurrency_list_item_view_container.background =
                            ContextCompat.getDrawable(
                                itemView.context,
                                R.drawable.selected_border_shape
                            )
                    }
                }
            }


        }
    }

    companion object {
        private val cryptocurrencyListViewDiffCallback =
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