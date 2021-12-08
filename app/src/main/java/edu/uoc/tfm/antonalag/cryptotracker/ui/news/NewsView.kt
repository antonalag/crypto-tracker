package edu.uoc.tfm.antonalag.cryptotracker.ui.news

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Filter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.room.util.StringUtil
import edu.uoc.tfm.antonalag.cryptotracker.CryptoTrackerApp
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.APIConstants.CURRENCIES_PARAM
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.APIConstants.FILTER_PARAMS
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.APIConstants.KIND_PARAM
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.APIConstants.REGIONS_PARAMS
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.observe
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.LocalCryptocurrency
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.NewsListViewDto
import edu.uoc.tfm.antonalag.cryptotracker.ui.home.HomeView
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.BaseActivity
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.MenuFragment
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.OnSwipeTouchListener
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.PaginationScrollListener
import kotlinx.android.synthetic.main.activity_news_view.*
import kotlinx.android.synthetic.main.main_toolbar.*
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates

/**
 * Class responsible of displaying news related with cryptocurrencies
 */
class NewsView : BaseActivity(), FilterNewsDialogFragment.FilterNewsDialogListener {

    private val TAG = "NewsView"

    private lateinit var localCryptocurrencies: List<LocalCryptocurrency>
    private val viewModel by viewModel<NewsViewModel>()
    private val user = CryptoTrackerApp.instance.user
    private var newsAdapter = NewsAdapter()
    private var isLoading by Delegates.notNull<Boolean>()
    private lateinit var news: NewsListViewDto
    private lateinit var filterNewsDialogFragment: FilterNewsDialogFragment
    private val menuOptionsDialog: MenuFragment = MenuFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSetToolbar(R.layout.main_toolbar, R.id.main_toolbar)
        initSetLayout(R.layout.activity_news_view)
        initUI()
        initViewModelObservers()
        initRequests()
    }

    /**
     * Initializes the properties of the UI elements
     */
    private fun initUI() {
        // Set toolbar title
        main_toolbar_title.text = resources.getString(R.string.app_name)
        // Set menu button listener
        menu_options_button.setOnClickListener {
            showMenuDialog()
        }
        // Add swipe listener layout
        val newsViewLayout = findViewById<View>(R.id.news_view_id)
        newsViewLayout.setOnTouchListener(object: OnSwipeTouchListener(this) {
            override fun onSwipeRight() {
                startActivity(Intent(context, HomeView::class.java))
                overridePendingTransition(R.anim.slide_left, R.anim.slide_out_right)
            }
        })
        // Show loading
        showView(news_progress_bar, true)
        // Set on click listeners
        saved_button.setOnClickListener {
            startActivity(Intent(this, SavedNewsView::class.java))
        }
        filters_button.setOnClickListener {
            if(!filterNewsDialogFragment.isAdded) {
                filterNewsDialogFragment.show(supportFragmentManager, "FilterNewsDialogFragment")
            }
        }
        // Disabled Buttons
        enableButtons(false)
        // set a StaggeredGridLayoutManager with 2 number of columns and horizontal orientation
         val layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        news_list_recyclerView.layoutManager = layoutManager
        // Set adapter
        news_list_recyclerView.adapter = newsAdapter
        // Set pagination listener
        news_list_recyclerView.addOnScrollListener(object: PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                this@NewsView.isLoading = true
                viewModel.getNews(mapOf(), news.next)
            }

            override val isLastPage: Boolean
                get() = TextUtils.isEmpty(news.next)

            override val isLoading: Boolean
                get() = this@NewsView.isLoading

        })
    }

    /**
     * Configure the NewsViewModel's observers
     */
    private fun initViewModelObservers() {
        with(viewModel) {
            observe(localCryptocurrencies, ::handleLocalCryptocurrenciesSuccess)
            fail(fail, ::handleNewsFail)
        }

        with(viewModel) {
            observe(news, ::handleNewsSuccess)
            fail(fail, ::handleNewsFail)
        }
    }

    /**
     * Initializes the necessary requests
     */
    private fun initRequests() {
        // get local cryptocurrencies
        viewModel.getCryptocurrencies(user.id)
    }

    /**
     * It's called when the request for local cryptocurrencies data is successful
     */
    private fun handleLocalCryptocurrenciesSuccess(cryptocurrencies: List<LocalCryptocurrency>) {
        Log.v(TAG, resources.getString(R.string.local_cryptocurrencies_request_succesful))
        // Set global variable and add ALL element
        localCryptocurrencies = listOf(LocalCryptocurrency("All", "ALL", "", 0.0,0L)).plus(cryptocurrencies)
        // Initial Request news
        isLoading = true
        val filters = makeFilters(localCryptocurrencies)
        viewModel.getNews(filters, null)
    }

    /**
     * It's called when the request for news is successful
     */
    private fun handleNewsSuccess(newsListViewDto: NewsListViewDto) {
        Log.v(TAG, resources.getString(R.string.news_request_successful))
        showView(news_progress_bar,false)
        isLoading = false
        news = newsListViewDto
        newsAdapter.submitList(newsAdapter.currentList.plus(newsListViewDto.data))
        newsAdapter.notifyDataSetChanged()
        // Set filterNews dialog
        filterNewsDialogFragment = FilterNewsDialogFragment.newInstance(localCryptocurrencies)
        // Enable Buttons
        enableButtons(true)
    }

    /**
     * It's called when the request for news has failed
     */
    private fun handleNewsFail(fail: Fail) {
        Log.v(TAG, resources.getString(R.string.news_request_failed))
        when (fail) {
            is Fail.LocalFail -> {
                Log.e(TAG, resources.getString(R.string.local_error))
                showView(news_progress_bar, false)
                showView(news_error, true)
            }
            is Fail.ServerFail -> {
                Log.e(TAG, resources.getString(R.string.server_error))
                showView(news_progress_bar, false)
                showView(news_error, true)
            }
            else -> {
                Log.e(TAG, resources.getString(R.string.unknown_error))
                showView(news_progress_bar, false)
                showView(news_error, true)
            }
        }
    }

    /**
     * Build the filters to be passed as parameters to the external API request
     * to obatin the filtered news items
     */
    private fun makeFilters(
        selectedCryptocurrencies: List<LocalCryptocurrency>,
        orderFilter: String? = null
    ): Map<String, String> {
        // currency ALL not exists, so it deleted
        val symbolList = selectedCryptocurrencies.map { it.symbol }.filter { it != "ALL" }
        // Set kind and region
        var map = mutableMapOf(
            KIND_PARAM to "news",
            REGIONS_PARAMS to "es"

        )
        // Set currencies
        if(symbolList.isNotEmpty()) {
            val symbols = symbolList.joinToString { it }
            map[CURRENCIES_PARAM] = symbols
        }

        // Set order
        orderFilter?.let {
            map.put(FILTER_PARAMS, it)
        }
        // Return filters map
        return map
    }

    /**
     * Enable click action in saved and filters buttons
     */
    private fun enableButtons(enable: Boolean) {
        saved_button.isClickable = enable
        filters_button.isClickable = enable
    }

    /**
     * Callback function that detect when the user clicks on FilterNewsDialog confirm button
     */
    override fun onFilterNewsDialogConfirmClick(dialog: DialogFragment) {
        // get cryptocurrencyt selected
        val cryptocurrencyFiltersSelected = (dialog as FilterNewsDialogFragment).localCryptocurrencySelected
        // Set sort value
        val sortValue = dialog.sortValue
        // buid filters
        val filters = makeFilters(listOf(cryptocurrencyFiltersSelected), sortValue)
        // Close dialog
        filterNewsDialogFragment.dismiss()
        // Show progress bar
        showView(news_progress_bar, true)
        // reset adapter
        (news_list_recyclerView.adapter as NewsAdapter).apply {
            submitList(emptyList())
        }
        // Disabled Buttons
        enableButtons(false)
        // Get filtered news
        viewModel.getNews(filters, null)
    }

    /**
     * Callback function that detect when the user clicks on FilterNewsDialog cancel button
     */
    override fun onFilterNewsDialogCancelClick(dialog: DialogFragment) {
        // Close dialog
        filterNewsDialogFragment.dismiss()
    }

    /**
     * Show menu dialog
     */
    private fun showMenuDialog() {
        if(!menuOptionsDialog.isAdded) {
            menuOptionsDialog.show(supportFragmentManager, "MenuOptionsDialog")
        }
    }

    /**
     * Function that it's called when the user click on filter radio buttons
     */
    fun onRadioButtonClicked(view: View) {
        filterNewsDialogFragment.onRadioButtonClicked(view)
    }

}