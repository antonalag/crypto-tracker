package edu.uoc.tfm.antonalag.cryptotracker.ui.news

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.BaseViewModel
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.LocalNews
import edu.uoc.tfm.antonalag.cryptotracker.features.news.repository.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsDetailViewModel(
    private val newsRepository: NewsRepository
): BaseViewModel() {

    private val TAG = "NewsDetailViewModel"

    private val _savedNewsId: MutableLiveData<Long> = MutableLiveData()
    val savedNewsId: LiveData<Long> = _savedNewsId
    private val _newsExists: MutableLiveData<Boolean> = MutableLiveData()
     val newsExists: LiveData<Boolean> = _newsExists

    /**
     * Local request to save a news
     */
    fun saveNews(localNews: LocalNews) {
        Log.v(TAG, "Request to save a news")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                newsRepository.save(localNews)
            }.fold(
                ::handleFail,
                ::handleSaveNews
            )
        }
    }

    /**
     * Called when saveNews function is successful
     */
    private fun handleSaveNews(id: Long) {
        _savedNewsId.value = id
    }

    /**
     * Local request to check if a news is already saved
     */
    fun newsExists(url: String) {
        Log.v(TAG, "Request to check if a news is already saved. Url: $url")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                newsRepository.newsExistsByUrl(url)
            }.fold(
                ::handleFail,
                ::handleNewsExists
            )
        }
    }

    /**
     * Called when newsExists function is successful
     */
    private fun handleNewsExists(exists: Boolean) {
        _newsExists.value = exists
    }

}