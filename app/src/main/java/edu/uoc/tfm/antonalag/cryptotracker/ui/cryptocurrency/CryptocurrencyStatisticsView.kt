package edu.uoc.tfm.antonalag.cryptotracker.ui.cryptocurrency

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import edu.uoc.tfm.antonalag.cryptotracker.core.util.NumberUtil
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.Cryptocurrency
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.CryptocurrencyChart
import edu.uoc.tfm.antonalag.cryptotracker.features.investment.model.Investment
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.BaseActivity
import kotlinx.android.synthetic.main.activity_cryptocurrency_statistics_view.*
import kotlinx.android.synthetic.main.detail_toolbar_refresh_button.*
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates

/**
 * Class responsible for displaying detailed information about cryptocurrency.
 */
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

    /**
     * Handle data sent from HomeView activity
     */
    private fun handleIntent() {
        val intent = intent
        localCryptocurrencyId = intent.getLongExtra("localCryptocurrencyId", 0L)
        localCryptocurrencyName = intent.getStringExtra("localCryptocurrencyName") ?: ""
    }

    /**
     * Initializes the properties of the UI elements
     */
    private fun initUI() {
        // Set toolbar title
        detail_refresh_button_toolbar_title.text = resources.getString(R.string.statistics)
        // Set back button
        menu_back_button.setOnClickListener {
            super.onBackPressed()
        }
        // Set refresh button
        menu_refresh_button.setOnClickListener {
            showView(cryptocurrency_principal_data_progress_bar, true)
            showView(cryptocurrency_secondary_data_progress_bar, true)
            showView(cryptocurrency_links_progress_bar, true)
            showView(cryptocurrency_chart_progress_bar, true)
            // Get Cryptocurrency
            viewModel.getCryptocurrencyData(localCryptocurrencyName, userPreferences.fiat)
        }
        // Set time interval layouts
        timeIntervals =
            listOf<LinearLayout>(one_day, one_week, one_month, three_month, six_month, one_year)
        // Disable activity buttons
        enableTimeIntervalButtons(false)
        enableLinkButtons(false)
        // Set backgrounds time interval's buttons
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

    /**
     * Initializes the necessary requests
     */
    private fun initRequests() {
        // Get Cryptocurrency
        viewModel.getCryptocurrencyData(localCryptocurrencyName, userPreferences.fiat)
    }

    /**
     * Configure the CryptocurrencyStatisticsViewModel's observers
     */
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

    /**
     * It's called when the request for cryptocurrency information is successful
     */
    private fun handleCryptocurrencySuccess(cryptocurrencyFromRequest: Cryptocurrency) {
        Log.v(
            TAG,
            resources.getString(R.string.cryptocurrency_statistics_view_cryptocurrency_request_successful)
        )
        cryptocurrency = cryptocurrencyFromRequest
        // Set cryptocurrency data on screen
        setCryptocurrencyData()
        // Hide progress bars
        showView(cryptocurrency_principal_data_progress_bar, false)
        showView(cryptocurrency_secondary_data_progress_bar, false)
        showView(cryptocurrency_links_progress_bar, false)

        // set on click listener in links
        setOnClickListenerLinkButtons(
            cryptocurrencyFromRequest.websiteUrl,
            cryptocurrencyFromRequest.redditUrl,
            cryptocurrencyFromRequest.twitterUrl
        )

        // Enable links buttons
        enableLinkButtons(true)

        // Get chart info
        viewModel.getCryptocurrencyChartData(localCryptocurrencyName, userPreferences.timeInterval)
        // Get investment
        viewModel.getInvestmentData(localCryptocurrencyId)
    }

    /**
     * It's called when the request for cryptocurrency information has failed
     */
    private fun handleCryptocurrencyFail(fail: Fail) {
        Log.v(
            TAG,
            resources.getString(R.string.cryptocurrency_statistics_view_cryptocurrency_request_failed)
        )
        when (fail) {
            is Fail.ServerFail -> {
                Log.e(TAG, resources.getString(R.string.server_error))
                showView(cryptocurrency_principal_data_progress_bar, false)
                showView(cryptocurrency_links_progress_bar, false)
                showView(cryptocurrency_secondary_data_progress_bar, false)
                showView(cryptocurrency_chart_progress_bar, false)

                showView(cryptocurrency_principal_data_error, true)
                showView(cryptocurrency_links_error, true)
                showView(cryptocurrency_secondary_data_error, true)
                showView(cryptocurrency_chart_error, true)
            }
            else -> {
                Log.e(TAG, resources.getString(R.string.unknown_error))
                showView(cryptocurrency_principal_data_progress_bar, false)
                showView(cryptocurrency_links_progress_bar, false)
                showView(cryptocurrency_secondary_data_progress_bar, false)
                showView(cryptocurrency_chart_progress_bar, false)

                showView(cryptocurrency_principal_data_error, true)
                showView(cryptocurrency_links_error, true)
                showView(cryptocurrency_secondary_data_error, true)
                showView(cryptocurrency_chart_error, true)
            }
        }
    }

    /**
     * It's called when the request for investment information is successful
     */
    private fun handleInvestmentSuccess(investment: Investment) {
        // Check if investment not exists
        if (investment.id == 0L) {
            Log.v(
                TAG,
                resources.getString(R.string.cryptocurrency_statistics_view_investment_request_not_found)
            )
            showView(investment_data_progress_bar, false)
            showView(investment_data_empty, true)
        } else {
            Log.v(
                TAG,
                resources.getString(R.string.cryptocurrency_statistics_view_investment_request_successful)
            )
            // Set investment data
            investment_value_value.text =
                NumberUtil.ruleOfThreeCalculateYValue(
                    cryptocurrency.price,
                    1,
                    investment.totalInvested
                ).roundToString(2) + " ${userPreferences.fiatSymbol}"
            investment_quantity_value.text =
                investment.totalInvested.toString() + " ${userPreferences.fiatSymbol}"
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
            showView(investment_data_progress_bar, false)
        }
    }

    /**
     * It's called when the request for investment information has failed
     */
    private fun handleInvestmentFail(fail: Fail) {
        Log.v(
            TAG,
            resources.getString(R.string.cryptocurrency_statistics_view_investment_request_failed)
        )
        when (fail) {
            is Fail.LocalFail -> {
                Log.e(TAG, resources.getString(R.string.local_error))
                showView(investment_data_progress_bar, false)
                showView(investment_data_error, true)
            }
            is Fail.NotFoundFail -> {
                Log.e(TAG, resources.getString(R.string.not_found_error))
                showView(investment_data_progress_bar, false)
                showView(investment_data_empty, true)
            }
            else -> {
                Log.e(TAG, resources.getString(R.string.unknown_error))
                showView(investment_data_progress_bar, false)
                showView(investment_data_error, true)
            }
        }
    }

    /**
     * It's called when the request for cryptocurrency chart data is successful
     */
    private fun handleCryptocurrencyChartSuccess(chart: List<CryptocurrencyChart>) {
        Log.v(
            TAG,
            resources.getString(R.string.cryptocurrency_statistics_view_cryptocurrency_chart_request_successful)
        )
        // Configure chart data
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

        val rightYAxis: YAxis = chartView.axisRight
        rightYAxis.isEnabled = false

        chartView.axisLeft.textColor =
            ContextCompat.getColor(this, R.color.white)
        chartView.axisLeft.axisLineColor =
            ContextCompat.getColor(this, R.color.white)
        chartView.legend.textColor = ContextCompat.getColor(this, R.color.white)

        chartView.invalidate()

        enableTimeIntervalButtons(true)
        showView(cryptocurrency_chart_progress_bar, false)
    }

    /**
     * It's called when the request for cryptocurrency chart data has failed
     */
    private fun handleCryptocurrencyChartFail(fail: Fail) {
        Log.v(
            TAG,
            resources.getString(R.string.cryptocurrency_statistics_view_cryptocurrency_chart_request_failed)
        )
        when (fail) {
            is Fail.ServerFail -> {
                Log.e(TAG, resources.getString(R.string.server_error))
                showView(cryptocurrency_chart_progress_bar, false)
                showView(cryptocurrency_chart_error, true)
            }
            else -> {
                Log.e(TAG, resources.getString(R.string.unknown_error))
                showView(cryptocurrency_chart_progress_bar, false)
                showView(cryptocurrency_chart_error, true)
            }
        }
    }

    /**
     * Enable click action in time interval buttons
     */
    private fun enableTimeIntervalButtons(enable: Boolean) {
        one_day.isClickable = enable
        one_week.isClickable = enable
        one_month.isClickable = enable
        three_month.isClickable = enable
        six_month.isClickable = enable
    }

    /**
     * Enable click action in link buttons
     */
    private fun enableLinkButtons(enable: Boolean) {
        reddit_link.isClickable = enable
        twitter_link.isClickable = enable
        web_link.isClickable = enable
    }

    /**
     * Add click action listeners in link buttons. When link button is clicked,
     * the user's configured browser will open the link that has been established
     */
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

    /**
     * Set all cryptocurrency-related information in the UI
     */
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
        cryptocurrency_price.text =
            cryptocurrency.price.roundToString(2) + " ${userPreferences.fiatSymbol}"
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

    /**
     * Configure the time interval buttons so that when user clicks on them, the background color changes.
     */
    private fun configureTimeIntervalButtons(selected: String) {

        timeIntervals.first { (it.getChildAt(0) as TextView).text == selected }.apply {
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