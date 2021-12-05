package edu.uoc.tfm.antonalag.cryptotracker.ui.news

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
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
        showLoading(true)
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

    private fun initRequests() {
        viewModel.getCryptocurrencies(user.id)
    }

    private fun handleLocalCryptocurrenciesSuccess(cryptocurrencies: List<LocalCryptocurrency>) {
        // Set global variable and add ALL element
        localCryptocurrencies = listOf(LocalCryptocurrency("All", "ALL", "", 0.0,0L)).plus(cryptocurrencies)
        // Initial Request news
        isLoading = true
        val filters = makeFilters(localCryptocurrencies)
        viewModel.getNews(filters, null)
    }

    private fun handleNewsSuccess(newsListViewDto: NewsListViewDto) {
        showLoading(false)
        isLoading = false
        news = newsListViewDto
        newsAdapter.submitList(newsAdapter.currentList.plus(newsListViewDto.data))
        newsAdapter.notifyDataSetChanged()
        // Set filterNews dialog
        filterNewsDialogFragment = FilterNewsDialogFragment.newInstance(localCryptocurrencies)
        // Enable Buttons
        enableButtons(true)
    }

    private fun handleNewsFail(fail: Fail) {
        when (fail) {
            is Fail.LocalFail -> {
                // Hide loading
                showLoading(false)
                news_error.visibility = View.VISIBLE
            }
            else -> {
                // Nothing to do
            }
        }
    }

    private fun makeFilters(
        selectedCryptocurrencies: List<LocalCryptocurrency>,
        orderFilter: String? = null
    ): Map<String, String> {
        val symbolList = selectedCryptocurrencies.map { it.symbol }.filter { it != "ALL" }
        var map = mutableMapOf(
            KIND_PARAM to "news",
            REGIONS_PARAMS to "es"

        )

        if(symbolList.isNotEmpty()) {
            val symbols = symbolList.joinToString { it }
            map[CURRENCIES_PARAM] = symbols
        }


        orderFilter?.let {
            map.put(FILTER_PARAMS, it)
        }
        return map
    }

    private fun showLoading(show: Boolean) {
        news_progress_bar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun enableButtons(enable: Boolean) {
        saved_button.isClickable = enable
        filters_button.isClickable = enable
    }

    override fun onFilterNewsDialogConfirmClick(dialog: DialogFragment) {
        val cryptocurrencyFiltersSelected = (dialog as FilterNewsDialogFragment).localCryptocurrencySelected
        val sortValue = dialog.sortValue
        val filters = makeFilters(listOf(cryptocurrencyFiltersSelected), sortValue)
        // Close dialog
        filterNewsDialogFragment.dismiss()
        // Show progress bar
        showLoading(true)
        // reset adapter
        (news_list_recyclerView.adapter as NewsAdapter).apply {
            submitList(emptyList())
        }
        // Disabled Buttons
        enableButtons(false)
        isLoading = true
        viewModel.getNews(filters, null)
    }

    override fun onFilterNewsDialogCancelClick(dialog: DialogFragment) {
        filterNewsDialogFragment.dismiss()
    }

    private fun showMenuDialog() {
        if(!menuOptionsDialog.isAdded) {
            menuOptionsDialog.show(supportFragmentManager, "MenuOptionsDialog")
        }
    }

    fun onRadioButtonClicked(view: View) {
        filterNewsDialogFragment.onRadioButtonClicked(view)
    }

}