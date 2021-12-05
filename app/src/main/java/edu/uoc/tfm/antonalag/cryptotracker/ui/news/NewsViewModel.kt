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

    private fun handleNews(news: NewsListViewDto) {
        _news.value = news
    }

    fun getCryptocurrencies(userId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cryptocurrencyRepository.findAllByUserId(userId)
            }.fold(
                ::handleFail,
                ::handleCryptocurrencies
            )
        }
    }

    private fun handleCryptocurrencies(list: List<LocalCryptocurrency>) {
        _localCryptocurrencies.value = list
    }

}