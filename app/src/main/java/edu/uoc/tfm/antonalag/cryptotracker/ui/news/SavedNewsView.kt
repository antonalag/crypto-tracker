package edu.uoc.tfm.antonalag.cryptotracker.ui.news

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import edu.uoc.tfm.antonalag.cryptotracker.CryptoTrackerApp
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.observe
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.LocalNews
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.BaseActivity
import kotlinx.android.synthetic.main.activity_news_view.*
import kotlinx.android.synthetic.main.activity_saved_news_view.*
import kotlinx.android.synthetic.main.detail_toolbar.*
import org.koin.android.viewmodel.ext.android.viewModel

class SavedNewsView : BaseActivity(), SavedNewsAdapter.DeleteClickListener {

    private val TAG = "SavednewsView"

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

    private fun initUI() {
        // Set toolbar title
        detail_toolbar_title.text = resources.getString(R.string.saved_news_title)
        // set back button
        menu_back_button.setOnClickListener {
            super.onBackPressed()
        }
        // Show loading
        showLoading(true)
        // set recycler view
        saved_news_list_recyclerView.layoutManager = LinearLayoutManager(this)
        saved_news_list_recyclerView.adapter = savedNewsAdapter
    }

    private fun initRequest() {
        viewModel.getLocalNews(user.id)
    }

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

    private fun handleLocalNewsSuccess(list: List<LocalNews>) {
        showLoading(false)
        localNews = list
        savedNewsAdapter.submitList(localNews)
    }

    private fun handleDeletedNewsSuccess(count: Int) {
        if (count == 0) {
            handleLocalNewsFail(Fail.LocalFail)
        } else {
            viewModel.getLocalNews(user.id)
        }
    }

    private fun handleLocalNewsFail(fail: Fail) {
        when (fail) {
            is Fail.LocalFail -> {
                // Hide loading
                showLoading(false)
                saved_news_error.visibility = View.VISIBLE
            }
            else -> {
                // Nothing to do
            }
        }
    }

    private fun showLoading(show: Boolean) {
        saved_news_progress_bar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDeleteClickListener(localNewsId: Long) {
        showLoading(true)
        viewModel.deleteNews(localNews.first { it.id == localNewsId }.id)
    }

}