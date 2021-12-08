package edu.uoc.tfm.antonalag.cryptotracker.ui.news

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import edu.uoc.tfm.antonalag.cryptotracker.CryptoTrackerApp
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.observe
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.LocalNews
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.BaseActivity
import kotlinx.android.synthetic.main.activity_saved_news_view.*
import kotlinx.android.synthetic.main.detail_toolbar.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Class responsible of displaying saved news related with cryptocurrencies
 */
class SavedNewsView : BaseActivity(), SavedNewsAdapter.SavedNewsApadterClickListener {

    private val TAG = "SavedNewsView"

    private lateinit var localNews: List<LocalNews>
    private val viewModel by viewModel<SavedNewsViewModel>()
    private val user = CryptoTrackerApp.instance.user
    private val savedNewsAdapter = SavedNewsAdapter(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSetToolbar(R.layout.detail_toolbar, R.id.detail_toolbar)
        initSetLayout(R.layout.activity_saved_news_view)
        initUI()
        initViewModelObservers()
        initRequest()
    }

    /**
     * Initializes the properties of the UI elements
     */
    private fun initUI() {
        // Set toolbar title
        detail_toolbar_title.text = resources.getString(R.string.saved_news_title)
        // set back button
        menu_back_button.setOnClickListener {
            super.onBackPressed()
        }
        // Show loading
        showView(saved_news_progress_bar, true)
        // set recycler view
        saved_news_list_recyclerView.layoutManager = LinearLayoutManager(this)
        saved_news_list_recyclerView.adapter = savedNewsAdapter
    }

    /**
     * Initializes the necessary requests
     */
    private fun initRequest() {
        viewModel.getLocalNews(user.id)
    }

    /**
     * Configure the SavedNewsViewModel's observers
     */
    private fun initViewModelObservers() {
        with(viewModel) {
            observe(localNews, ::handleLocalNewsSuccess)
            fail(fail, ::handleLocalNewsFail)
        }

        with(viewModel) {
            observe(deletedNewsCount, ::handleDeletedNewsSuccess)
            fail(fail, ::handleLocalNewsFail)
        }
    }

    /**
     * It's called when the request for saved news is successful
     */
    private fun handleLocalNewsSuccess(list: List<LocalNews>) {
        Log.v(TAG, resources.getString(R.string.news_request_successful))
        showView(saved_news_progress_bar, false)
        // Set saved news
        localNews = list

        if(list.isEmpty()) {
            showView(saved_news_empty, true)
        } else {
            showView(saved_news_empty, false)
            // Submit list to adapter
            savedNewsAdapter.submitList(localNews)
        }

    }

    /**
     * It's called when the request to delete a saved news is successful
     */
    private fun handleDeletedNewsSuccess(count: Int) {
        if (count == 0) {
            handleLocalNewsFail(Fail.LocalFail)
        } else {
            Log.v(TAG, resources.getString(R.string.news_delete_request_successful))
            // get saved news
            viewModel.getLocalNews(user.id)
        }
    }

    /**
     * It's called when the requests for Saved News View have failed
     */
    private fun handleLocalNewsFail(fail: Fail) {
        Log.v(TAG, resources.getString(R.string.news_delete_request_failed))
        when (fail) {
            is Fail.LocalFail -> {
                Log.e(TAG, resources.getString(R.string.local_error))
                // Hide loading
                showView(saved_news_progress_bar, false)
                // Show error
                showView(saved_news_error, true)
            }
            else -> {
                Log.e(TAG, resources.getString(R.string.unknown_error))
                // Hide loading
                showView(saved_news_progress_bar, false)
                // Show error
                showView(saved_news_error, true)
            }
        }
    }

    /**
     * Callback function that detect when the user clicks on SavedNewsAdapter delete button
     */
    override fun onDeleteClickListener(localNewsId: Long) {
        // Show loading
        showView(saved_news_progress_bar, true)
        // Delete news
        viewModel.deleteNews(localNews.first { it.id == localNewsId }.id)
    }

    override fun onClickListener(localNewsUrl: String, localNewsTitle: String) {
        val intent = Intent(this, NewsDetailView::class.java).apply {
            putExtra("url", localNewsUrl)
            putExtra("title", localNewsTitle)
        }
        startActivity(intent)
    }

}