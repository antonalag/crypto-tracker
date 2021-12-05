package edu.uoc.tfm.antonalag.cryptotracker.ui.news

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

    private fun getDataFromIntent() {
        newsUrl = intent.getStringExtra("url").toString()
        newsTitle = intent.getStringExtra("title").toString()
    }

    private fun initUI() {
        showLoading(true)
        detail_add_button_toolbar_title.text = resources.getString(R.string.news_detail)
        // Set back button
        menu_back_button.setOnClickListener {
            super.onBackPressed()
        }
        // Set add button
        menu_add_button.setOnClickListener {
            showLoading(true)
            Handler(Looper.getMainLooper()).postDelayed({
                viewModel.newsExists(newsUrl)
            }, 2000)

        }
    }

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

    private fun saveNews() {
        val news = LocalNews(newsTitle, newsUrl, DateUtil.dateNow(), CryptoTrackerApp.instance.user.id)
        viewModel.saveNews(news)
    }

    private fun handleSavedNewsIdSuccess(newsId: Long) {
        showLoading(false)
        Toast.makeText(this, resources.getString(R.string.saved_news), Toast.LENGTH_SHORT).show()
    }

    private fun handleNewsExistsSuccess(exists: Boolean) {
        if(!exists) {
            saveNews()
        } else {
            showLoading(false)
            Toast.makeText(this, resources.getString(R.string.news_already_exists), Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleNewsDetailFail(fail: Fail) {
        when (fail) {
            is Fail.LocalFail -> {
                showLoading(false)
                showError(true)
            }
            else -> {
                // Nothing to do
            }
        }
    }

    private fun showNews() {
        if(newsUrl.isNullOrEmpty()) {
            showLoading(false)
            showError(true)
        } else {
            // Create Uri
            val uri = Uri.parse(newsUrl)
            webView.settings.javaScriptEnabled = true
            webView.loadUrl(uri.toString())
            showLoading(false)
        }

    }

    private fun showLoading(show: Boolean) {
        news_detail_progress_bar.visibility = if(show) View.VISIBLE else View.GONE
    }

    private fun showError(show: Boolean) {
        news_detail_error.visibility = if(show) View.VISIBLE else View.GONE
    }
}