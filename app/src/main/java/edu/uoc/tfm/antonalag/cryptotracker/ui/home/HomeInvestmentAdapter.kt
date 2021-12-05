package edu.uoc.tfm.antonalag.cryptotracker.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chauthai.swipereveallayout.ViewBinderHelper
import edu.uoc.tfm.antonalag.cryptotracker.CryptoTrackerApp
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.roundToString
import edu.uoc.tfm.antonalag.cryptotracker.core.util.NumberUtil
import edu.uoc.tfm.antonalag.cryptotracker.features.investment.model.InvestmentCryptocurrency
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPreferences
import kotlinx.android.synthetic.main.investment_list_item_swipe_view.view.*
import java.lang.ClassCastException

class HomeInvestmentAdapter(private val context: Context) :
    ListAdapter<InvestmentCryptocurrency, HomeInvestmentAdapter.HomeInvestmentViewHolder>(
        investmentCryptocurrencyDiffCallback
    ) {
    private lateinit var listener: HomeInvestmentAdapterClickListener
    private val viewBinderHelper = ViewBinderHelper()
    private val userPreferences: UserPreferences = CryptoTrackerApp.instance.userPreferences

    /**
     * The activity that creates an instance of this Adapter must
     * implement this interface in order to receive event callbacks. Each
     * method passes the id of the element selected
     */
    interface HomeInvestmentAdapterClickListener {
        fun onDeleteInvestmentClickListener(localCryptocurrencyId: Long)
        fun onEditInvestmentClickListener(localCryptocurrencyId: Long)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeInvestmentAdapter.HomeInvestmentViewHolder {
        return HomeInvestmentViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.investment_list_item_swipe_view, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: HomeInvestmentAdapter.HomeInvestmentViewHolder,
        position: Int
    ) {
        viewBinderHelper.setOpenOnlyOne(true)
        viewBinderHelper.bind(holder.itemView.investment_list_item_swipe, getItem(position).name)
        viewBinderHelper.closeLayout(getItem(position).name)
        holder.bindTo(getItem(position))
    }

    inner class HomeInvestmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindTo(investmentCryptocurrency: InvestmentCryptocurrency) {
            itemView.investment_iv_swipe_cryptocurrency_name.text = investmentCryptocurrency.name
            val cryptocurrencyQuantity = NumberUtil.ruleOfThreeCalculateYValue(
                investmentCryptocurrency.price,
                1,
                investmentCryptocurrency.totalInvested
            )
            itemView.investment_iv_swipe_cryptocurrency_quantity.text = cryptocurrencyQuantity.roundToString(2)
            itemView.investment_iv_swipe_investment_quantity.text = investmentCryptocurrency.totalInvested.roundToString(2) + " ${userPreferences.fiatSymbol}"
            itemView.investment_iv_swipe_cryptocurrency_symbol.text = investmentCryptocurrency.symbol

            Glide.with(itemView)
                .load(investmentCryptocurrency.icon)
                .centerCrop()
                .into(itemView.investment_iv_swipe_cryptocurrency_icon)

            // se on click listeners
            itemView.edit_investment.setOnClickListener {
                listener.onEditInvestmentClickListener(investmentCryptocurrency.localCryptocurrencyId)
            }
            itemView.delete_investment.setOnClickListener {
                listener.onDeleteInvestmentClickListener(investmentCryptocurrency.localCryptocurrencyId)
            }
        }
    }

    // Override onAattach method to instantiate DeleteCLickListener
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        // Verify that the host acitivity implements the callback interface
        try {
            // Instantiate the DeleteCLickListener to be able to send events to the host
            listener = context as HomeInvestmentAdapterClickListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement HomeInvestmentAdapterClickListener")
        }
    }

    companion object {
        private val investmentCryptocurrencyDiffCallback =
            object : DiffUtil.ItemCallback<InvestmentCryptocurrency>() {
                override fun areItemsTheSame(
                    oldItem: InvestmentCryptocurrency,
                    newItem: InvestmentCryptocurrency
                ): Boolean {
                    return oldItem.name == newItem.name
                }

                override fun areContentsTheSame(
                    oldItem: InvestmentCryptocurrency,
                    newItem: InvestmentCryptocurrency
                ): Boolean {
                    return oldItem == newItem
                }

            }
    }
}