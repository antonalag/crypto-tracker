package edu.uoc.tfm.antonalag.cryptotracker.ui.cryptocurrency

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import edu.uoc.tfm.antonalag.cryptotracker.CryptoTrackerApp
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.observe
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.roundToString
import edu.uoc.tfm.antonalag.cryptotracker.core.util.DateXAxisValueFormatter
import edu.uoc.tfm.antonalag.cryptotracker.core.util.NumberUtil
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.Cryptocurrency
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.CryptocurrencyChart
import edu.uoc.tfm.antonalag.cryptotracker.features.investment.model.Investment
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.BaseActivity
import kotlinx.android.synthetic.main.activity_cryptocurrency_statistics_view.*
import kotlinx.android.synthetic.main.detail_toolbar_refresh_button.*
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates


class CryptocurrencyStatisticsView : BaseActivity() {

    private val TAG = "CryptocurrencyStatisticsView"

    private val viewModel by viewModel<CryptocurrencyStatisticsViewModel>()
    private var localCryptocurrencyId by Delegates.notNull<Long>()
    private lateinit var localCryptocurrencyName: String
    private val userPreferences = CryptoTrackerApp.instance.userPreferences
    private var redditLink: String? = null
    private var webLink: String? = null
    private var twitterLink: String? = null
    private lateinit var cryptocurrency: Cryptocurrency
    private lateinit var timeIntervals: List<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSetToolbar(R.layout.detail_toolbar_refresh_button, R.id.detail_refresh_button_toolbar)
        initSetLayout(R.layout.activity_cryptocurrency_statistics_view)
        handleIntent()
        initUI()
        initViewModelObservers()
        initRequests()
    }

    private fun handleIntent() {
        val intent = intent
        localCryptocurrencyId = intent.getLongExtra("localCryptocurrencyId", 0L)
        localCryptocurrencyName = intent.getStringExtra("localCryptocurrencyName") ?: ""
    }

    private fun initUI() {
        // Set toolbar title
        detail_refresh_button_toolbar_title.text = resources.getString(R.string.statistics)
        // Set back button
        menu_back_button.setOnClickListener {
            super.onBackPressed()
        }
        // Set refresh button
        menu_refresh_button.setOnClickListener {
            cryptocurrency_principal_data_progress_bar.visibility = View.GONE
            cryptocurrency_secondary_data_progress_bar.visibility = View.GONE
            cryptocurrency_links_progress_bar.visibility = View.GONE
            cryptocurrency_chart_progress_bar.visibility = View.GONE
            // Get Cryptocurrency
            viewModel.getCryptocurrencyData(localCryptocurrencyName, userPreferences.fiat)
        }
        // Set time interval layouts
        timeIntervals = listOf<LinearLayout>(one_day, one_week, one_month, three_month, six_month, one_year)
        enableTimeIntervalButtons(false)
        enableLinkButtons(false)
        // Set time interval
        configureTimeIntervalButtons(userPreferences.timeInterval)
        // Set time interval on click listener
        timeIntervals.forEach {
            it.setOnClickListener { _ ->
                val timeInterval = (it.getChildAt(0) as TextView).text.toString()
                configureTimeIntervalButtons(timeInterval)
                viewModel.getCryptocurrencyChartData(localCryptocurrencyName, timeInterval)
                enableTimeIntervalButtons(false)
            }
        }

    }

    private fun initRequests() {
        // Get Cryptocurrency
        viewModel.getCryptocurrencyData(localCryptocurrencyName, userPreferences.fiat)
    }

    private fun initViewModelObservers() {
        with(viewModel) {
            observe(cryptocurrency, ::handleCryptocurrencySuccess)
            fail(fail, ::handleCryptocurrencyFail)
        }

        with(viewModel) {
            observe(investment, ::handleInvestmentSuccess)
            fail(fail, ::handleInvestmentFail)
        }

        with(viewModel) {
            observe(cryptocurrencyChart, ::handleCryptocurrencyChartSuccess)
            fail(fail, ::handleCryptocurrencyChartFail)
        }
    }

    private fun handleCryptocurrencySuccess(cryptocurrencyFromRequest: Cryptocurrency) {
        cryptocurrency = cryptocurrencyFromRequest
        // Set cryptocurrency data on screen
        setCryptocurrencyData()
        // Hide progress bars
        cryptocurrency_principal_data_progress_bar.visibility = View.GONE
        cryptocurrency_secondary_data_progress_bar.visibility = View.GONE
        cryptocurrency_links_progress_bar.visibility = View.GONE

        // set on click listener in links
        setOnClickListenerLinkButtons(
            cryptocurrencyFromRequest.websiteUrl,
            cryptocurrencyFromRequest.redditUrl,
            cryptocurrencyFromRequest.twitterUrl
        )

        // Enable alarm and links buttons
        enableLinkButtons(true)

        // Get chart info
        viewModel.getCryptocurrencyChartData(localCryptocurrencyName, userPreferences.timeInterval)
        // Get investment
        viewModel.getInvestmentData(localCryptocurrencyId)
    }

    private fun handleCryptocurrencyFail(fail: Fail) {
        when (fail) {
            is Fail.ServerFail -> {
                cryptocurrency_principal_data_progress_bar.visibility = View.GONE
                cryptocurrency_principal_data_error.visibility = View.VISIBLE
                cryptocurrency_links_progress_bar.visibility = View.GONE
                cryptocurrency_links_error.visibility = View.VISIBLE
                cryptocurrency_secondary_data_progress_bar.visibility = View.GONE
                cryptocurrency_secondary_data_error.visibility = View.VISIBLE
                cryptocurrency_chart_progress_bar.visibility = View.GONE
                cryptocurrency_chart_error.visibility = View.VISIBLE
            }
            else -> {
                // Nothing to do
            }
        }
    }

    private fun handleInvestmentSuccess(investment: Investment) {
        // Set investment data
        investment_quantity_value.text = NumberUtil.ruleOfThreeCalculateYValue(cryptocurrency.price, 1, investment.totalInvested).roundToString(2)
        investment_value_value.text = investment.totalInvested.toString() + " ${userPreferences.fiatSymbol}"
        when {
            cryptocurrency.priceChange1d < 0 -> {
                investment_arrow.rotation = 90.0F
                // Set tint
                investment_arrow.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.google_color
                    )
                )
            }
            cryptocurrency.priceChange1d == 0.0 -> {
                investment_arrow.visibility = View.GONE
            }
            else -> {
                investment_arrow.rotation = -90.0F
                // Set tint
                investment_arrow.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.detail_chart_line_color
                    )
                )
            }
        }
        investment_data_progress_bar.visibility = View.GONE
    }

    private fun handleInvestmentFail(fail: Fail) {
        when (fail) {
            is Fail.LocalFail -> {
                investment_data_progress_bar.visibility = View.GONE
                investment_data_error.visibility = View.VISIBLE
            }
            else -> {
                // Nothing to do
            }
        }
    }

    private fun handleCryptocurrencyChartSuccess(chart: List<CryptocurrencyChart>) {
        val chartView = cryptocurrency_chart
        val entries: List<Entry> = chart.map { Entry(it.date, it.price) }
        val dataSet = LineDataSet(entries, "$localCryptocurrencyName price evolution")
        dataSet.color = ContextCompat.getColor(this, R.color.detail_chart_line_color)
        dataSet.fillColor = ContextCompat.getColor(this, R.color.detail_chart_background_color)
        dataSet.lineWidth = 2f
        dataSet.setDrawCircles(false)
        dataSet.setDrawValues(false)
        val lineData = LineData(dataSet)
        chartView.description.text = ""
        chartView.data = lineData
        chartView.animateX(1800, Easing.EaseInExpo)
        chartView.setTouchEnabled(false)

        val xAxis: XAxis = chartView.xAxis
        xAxis.isEnabled = false
        xAxis.valueFormatter = DateXAxisValueFormatter()

        val rightYAxis: YAxis = chartView.axisRight
        rightYAxis.isEnabled = false

        chartView.axisLeft.textColor =
            ContextCompat.getColor(this, R.color.white)
        chartView.axisLeft.axisLineColor =
            ContextCompat.getColor(this, R.color.white)
        chartView.legend.textColor = ContextCompat.getColor(this, R.color.white)

        chartView.invalidate()

        enableTimeIntervalButtons(true)
        cryptocurrency_chart_progress_bar.visibility = View.GONE
    }

    private fun handleCryptocurrencyChartFail(fail: Fail) {
        when (fail) {
            is Fail.ServerFail -> {
                cryptocurrency_chart_progress_bar.visibility = View.GONE
                cryptocurrency_chart_error.visibility = View.VISIBLE
            }
            else -> {
                // Nothing to do
            }
        }
    }

    private fun enableTimeIntervalButtons(enable: Boolean) {
        one_day.isClickable = enable
        one_week.isClickable = enable
        one_month.isClickable = enable
        three_month.isClickable = enable
        six_month.isClickable = enable
    }

    private fun enableLinkButtons(enable: Boolean) {
        reddit_link.isClickable = enable
        twitter_link.isClickable = enable
        web_link.isClickable = enable
    }

    private fun setOnClickListenerLinkButtons(
        webLink: String?,
        redditLink: String?,
        twitterLink: String?
    ) {
        webLink?.let { link ->
            web_link.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(link)
                startActivity(intent)
            }
        }

        redditLink?.let { link ->
            reddit_link.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(link)
                startActivity(intent)
            }
        }

        twitterLink?.let { link ->
            twitter_link.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(link)
                startActivity(intent)
            }
        }
    }

    private fun setCryptocurrencyData() {
        // Set image
        Glide.with(this)
            .load(cryptocurrency.icon)
            .centerCrop()
            .into(investment_iv_cryptocurrency_icon)
        // Set name
        investment_iv_cryptocurrency_name.text = cryptocurrency.name
        // Set symbol
        investment_iv_cryptocurrency_symbol.text = cryptocurrency.symbol
        // Set arrow
        when {
            cryptocurrency.priceChange1d < 0 -> {
                cryptocurrency_arrow.rotation = 90.0F
                // Set tint
                cryptocurrency_arrow.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.google_color
                    )
                )
            }
            cryptocurrency.priceChange1d == 0.0 -> {
                cryptocurrency_arrow.visibility = View.GONE
            }
            else -> {
                cryptocurrency_arrow.rotation = -90.0F
                // Set tint
                cryptocurrency_arrow.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.detail_chart_line_color
                    )
                )
            }
        }
        // Set price
        cryptocurrency_price.text = cryptocurrency.price.roundToString(2) + " ${userPreferences.fiatSymbol}"
        // Set percentage change
        cryptocurrency_change_percentage.text = cryptocurrency.priceChange1d.roundToString(2) + " %"
        // Set ranking value
        cryptocurrency_ranking_value.text = cryptocurrency.rank.toString()
        // Set BTC Price value
        cryptocurrency_btc_price_value.text = cryptocurrency.priceBtc.roundToString(2)
        // Set volume value
        cryptocurrency_volume_value.text = cryptocurrency.volume.roundToString(2)
        // Set available supply value
        cryptocurrency_available_supply_value.text = cryptocurrency.availableSupply.roundToString(2)
        // Set total supply value
        cryptocurrency_total_suply_value.text = cryptocurrency.totalSupply.roundToString(2)
        // Set marketcap value
        cryptocurrency_market_cap_value.text = cryptocurrency.marketCap.roundToString(2)
        // Set web link
        webLink = cryptocurrency.websiteUrl
        // Set reddit link
        redditLink = cryptocurrency.redditUrl
        // Set twitter link
        twitterLink = cryptocurrency.twitterUrl
    }

    private fun configureTimeIntervalButtons(selected: String) {

        timeIntervals.first{ (it.getChildAt(0) as TextView).text == selected }.apply {
            this.background = ContextCompat.getDrawable(
                applicationContext,
                R.drawable.background_selected_shape
            )
        }

        timeIntervals.filterNot { (it.getChildAt(0) as TextView).text == selected }.forEach {
            it.background = ContextCompat.getDrawable(
                applicationContext,
                R.drawable.background_transparent_shape
            )
        }
    }


}