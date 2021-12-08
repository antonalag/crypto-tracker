package edu.uoc.tfm.antonalag.cryptotracker.ui.news

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import edu.uoc.tfm.antonalag.cryptotracker.CryptoTrackerApp
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.observe
import edu.uoc.tfm.antonalag.cryptotracker.core.util.DateUtil
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.LocalNews
import edu.uoc.tfm.antonalag.cryptotracker.ui.home.HomeView
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.BaseActivity
import kotlinx.android.synthetic.main.activity_cryptocurrency_statistics_view.*
import kotlinx.android.synthetic.main.activity_news_detail_view.*
import kotlinx.android.synthetic.main.detail_toolbar_add_button.*
import kotlinx.android.synthetic.main.detail_toolbar_add_button.menu_back_button
import kotlinx.android.synthetic.main.detail_toolbar_refresh_button.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Class responsible of displaying a detailed news
 */
class NewsDetailView : BaseActivity() {

    private val TAG = "NewsDetailView"

    private val viewModel by viewModel<NewsDetailViewModel>()
    private lateinit var newsUrl: String
    private lateinit var newsTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSetToolbar(R.layout.detail_toolbar_add_button, R.id.detail_add_button_toolbar)
        initSetLayout(R.layout.activity_news_detail_view)
        getDataFromIntent()
        initUI()
        initViewModelObservers()
        showNews()
    }

    /**
     * Handle data sent from NewsView activity
     */
    private fun getDataFromIntent() {
        newsUrl = intent.getStringExtra("url").toString()
        newsTitle = intent.getStringExtra("title").toString()
    }

    /**
     * Initializes the properties of the UI elements
     */
    private fun initUI() {
        showView(news_detail_progress_bar, true)
        // Set toolbar title
        detail_add_button_toolbar_title.text = resources.getString(R.string.news_detail)
        // Set back button
        menu_back_button.setOnClickListener {
            super.onBackPressed()
        }
        // Set add button
        menu_add_button.setOnClickListener {
            showView(news_detail_progress_bar, true)
            Handler(Looper.getMainLooper()).postDelayed({
                viewModel.newsExists(newsUrl)
            }, 2000)
        }
    }

    /**
     * Configure the NewsDetailViewModel's observers
     */
    private fun initViewModelObservers() {
        with(viewModel) {
            observe(savedNewsId, ::handleSavedNewsIdSuccess)
            fail(fail, ::handleNewsDetailFail)
        }

        with(viewModel) {
            observe(newsExists, ::handleNewsExistsSuccess)
            fail(fail, ::handleNewsDetailFail)
        }
    }

    /**
     * Save a news
     */
    private fun saveNews() {
        // Get news entity instance
        val news = LocalNews(newsTitle, newsUrl, DateUtil.dateNow(), CryptoTrackerApp.instance.user.id)
        // Save news
        viewModel.saveNews(news)
    }

    /**
     * It's called when the request to save a news is successful
     */
    private fun handleSavedNewsIdSuccess(newsId: Long) {
        Log.v(TAG, resources.getString(R.string.news_save_request_successful))
        showView(news_detail_progress_bar, false)
        Toast.makeText(this, resources.getString(R.string.saved_news), Toast.LENGTH_SHORT).show()
    }

    /**
     * It's called when the request to check if a news is already saved is successful
     */
    private fun handleNewsExistsSuccess(exists: Boolean) {
        if(!exists) {
            saveNews()
        } else {
            showView(news_detail_progress_bar, false)
            Toast.makeText(this, resources.getString(R.string.news_already_exists), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * It's called when the requests of news details have failed
     */
    private fun handleNewsDetailFail(fail: Fail) {
        when (fail) {
            is Fail.LocalFail -> {
                Log.e(TAG, resources.getString(R.string.local_error))
                showView(news_detail_progress_bar, false)
                showView(news_detail_error, true)
            }
            else -> {
                Log.e(TAG, resources.getString(R.string.unknown_error))
                showView(news_detail_progress_bar, false)
                showView(news_detail_error, true)
            }
        }
    }

    /**
     * Show detailed news
     */
    private fun showNews() {
        if(newsUrl.isNullOrEmpty()) {
            showView(news_detail_progress_bar, false)
            showView(news_detail_error, true)
        } else {
            // Create Uri
            val uri = Uri.parse(newsUrl)
            webView.settings.javaScriptEnabled = true
            webView.loadUrl(uri.toString())
            showView(news_detail_progress_bar, false)
        }

    }

}