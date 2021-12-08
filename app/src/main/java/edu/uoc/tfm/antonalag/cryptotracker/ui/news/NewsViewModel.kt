package edu.uoc.tfm.antonalag.cryptotracker.ui.news

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.BaseViewModel
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.LocalCryptocurrency
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.repository.CryptocurrencyRepository
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.NewsListViewDto
import edu.uoc.tfm.antonalag.cryptotracker.features.news.repository.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsViewModel(
    private val newsRepository: NewsRepository,
    private val cryptocurrencyRepository: CryptocurrencyRepository
): BaseViewModel() {

    private val TAG = "NewsViewModel"

    private val _news: MutableLiveData<NewsListViewDto> = MutableLiveData()
    val news: LiveData<NewsListViewDto> = _news

    private val _localCryptocurrencies: MutableLiveData<List<LocalCryptocurrency>> = MutableLiveData()
    val localCryptocurrencies: LiveData<List<LocalCryptocurrency>> = _localCryptocurrencies

    /**
     * External API request to get news
     */
    fun getNews(filters: Map<String, String>, next: String?) {
        Log.v(TAG, "Request news")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                newsRepository.findAll(filters, next)
            }.fold(
                ::handleFail,
                ::handleNews
            )
        }
    }

    /**
     * Called when getNews function is successful
     */
    private fun handleNews(news: NewsListViewDto) {
        _news.value = news
    }

    /**
     * Local request to get local cryptocurrencies
     */
    fun getCryptocurrencies(userId: Long) {
        Log.v(TAG, "Request local cryptocurrencies data for user id: $userId")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cryptocurrencyRepository.findAllByUserId(userId)
            }.fold(
                ::handleFail,
                ::handleCryptocurrencies
            )
        }
    }

    /**
     * Called when getCryptocurrencies function is successful
     */
    private fun handleCryptocurrencies(list: List<LocalCryptocurrency>) {
        _localCryptocurrencies.value = list
    }

}