package edu.uoc.tfm.antonalag.cryptotracker.ui.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import edu.uoc.tfm.antonalag.cryptotracker.CryptoTrackerApp
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.roundToString
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.CryptocurrencyCardViewDto
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.LocalCryptocurrency
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPreferences
import edu.uoc.tfm.antonalag.cryptotracker.ui.cryptocurrency.CryptocurrencyStatisticsView
import kotlinx.android.synthetic.main.cryptocurrency_card_item_view.view.*
import java.lang.ClassCastException


class HomeCryptocurrencyAdapter(private val context: Context): ListAdapter<CryptocurrencyCardViewDto, HomeCryptocurrencyAdapter.HomeCryptocurrencyViewHolder>(
    cryptocurrencyCardViewDiffCallback
) {

    lateinit var savedCryptocurrencies: List<LocalCryptocurrency>
    private val userpreferences: UserPreferences = CryptoTrackerApp.instance.userPreferences
    private lateinit var listener: HomeCryptocurrencyAdapterClickListener

    /**
     * The activity that creates an instance of this Adapter must
     * implement this interface in order to receive event callbacks. Each
     * method passes the id of the element selected
     */
    interface HomeCryptocurrencyAdapterClickListener {
        fun onDeleteCryptocurrencyClickListener(localCryptocurrencyId: Long)
        fun onRefresCryptocurrencyClickListener(localCryptocurrencyId: Long)
    }

    fun getSavedCryptocurrency(name: String): LocalCryptocurrency {
        return savedCryptocurrencies.first { it.name.equals(name, ignoreCase = true) }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeCryptocurrencyViewHolder {
        return HomeCryptocurrencyViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.cryptocurrency_card_item_view,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HomeCryptocurrencyViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }


    inner class HomeCryptocurrencyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bindTo(cryptoCurrencyCardViewDto: CryptocurrencyCardViewDto) {
            itemView.cv_cryptocurrency_name.text = cryptoCurrencyCardViewDto.name
            itemView.cv_cryptocurrency_symbol.text = cryptoCurrencyCardViewDto.symbol
            itemView.cv_cryptocurrency_price.text = cryptoCurrencyCardViewDto.price.roundToString(2) + " ${userpreferences.fiatSymbol}"
            itemView.cv_cryptocurrency_change_percentage.text = cryptoCurrencyCardViewDto.priceChange1d.toString() + " %"

            when {
                cryptoCurrencyCardViewDto.priceChange1d < 0 -> {
                    itemView.arrow.rotation = 90.0F
                    // Set drawable tint
                    itemView.arrow.setColorFilter(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.google_color
                        )
                    )
                }
                cryptoCurrencyCardViewDto.priceChange1d == 0.0 -> {
                    itemView.arrow.visibility = View.GONE
                }
                else -> {
                    itemView.arrow.rotation = -90F
                    // Set drawable tint
                    itemView.arrow.setColorFilter(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.detail_chart_line_color
                        )
                    )
                }
            }

            Glide.with(itemView)
                .load(cryptoCurrencyCardViewDto.icon)
                .centerCrop()
                .into(itemView.cv_cryptocurrency_icon)

            val entries: List<Entry> = cryptoCurrencyCardViewDto.chart?.map { Entry(
                it.date,
                it.price
            ) } ?: emptyList()

            setChart(itemView, entries)

            // card onClick
            itemView.setOnClickListener {

                val savedCryptocurrency: LocalCryptocurrency = getSavedCryptocurrency(
                    cryptoCurrencyCardViewDto.name
                )

                val intent = Intent(it.context, CryptocurrencyStatisticsView::class.java).apply {
                    putExtra("localCryptocurrencyId", savedCryptocurrency.id)
                    putExtra("localCryptocurrencyName", savedCryptocurrency.name)
                }

                it.context.startActivity(intent)
            }

            itemView.trash.setOnClickListener {
                listener.onDeleteCryptocurrencyClickListener(getSavedCryptocurrency(
                    cryptoCurrencyCardViewDto.name
                ).id)
            }

            itemView.refresh.setOnClickListener {
                listener.onRefresCryptocurrencyClickListener(getSavedCryptocurrency(
                    cryptoCurrencyCardViewDto.name
                ).id)
            }
        }
    }


    private fun setChart(itemView: View, entries: List<Entry>) {
        val chart = itemView.cryptocurrency_chart
        val dataSet = LineDataSet(entries, "Prices")
        val lineData = LineData(dataSet)

        dataSet.color = ContextCompat.getColor(itemView.context, R.color.detail_chart_line_color)
        dataSet.setDrawCircles(false)
        dataSet.lineWidth = 2f
        dataSet.setDrawValues(false)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        chart.description.text = ""
        chart.legend.isEnabled = false
        chart.data = lineData
        chart.animateX(1800, Easing.EaseInExpo)
        chart.setTouchEnabled(false)
        chart.invalidate()



        val leftYAxis: YAxis = chart.axisLeft
        leftYAxis.isEnabled = false


        val rightYAxis: YAxis = chart.axisRight
        rightYAxis.isEnabled = false

        val xAxis: XAxis = chart.xAxis
        xAxis.isEnabled = false
    }

    /**
     * Override onAattach method to instantiate HomeCryptocurrencyAdapterClickListener
     */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        // Verify that the host acitivity implements the callback interface
        try {
            // Instantiate the HomeCryptocurrencyAdapterClickListener to be able to send events to the host
            listener = context as HomeCryptocurrencyAdapterClickListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement HomeCryptocurrencyAdapterClickListener")
        }
    }

    companion object {
        private val cryptocurrencyCardViewDiffCallback = object: DiffUtil.ItemCallback<CryptocurrencyCardViewDto>() {

            override fun areItemsTheSame(
                oldItem: CryptocurrencyCardViewDto,
                newItem: CryptocurrencyCardViewDto
            ): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(
                oldItem: CryptocurrencyCardViewDto,
                newItem: CryptocurrencyCardViewDto
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

}
