package edu.uoc.tfm.antonalag.cryptotracker.ui.news

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

    fun saveNews(localNews: LocalNews) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                newsRepository.save(localNews)
            }.fold(
                ::handleFail,
                ::handleSaveNews
            )
        }
    }

    private fun handleSaveNews(id: Long) {
        _savedNewsId.value = id
    }

    fun newsExists(url: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                newsRepository.newsExistsByUrl(url)
            }.fold(
                ::handleFail,
                ::handleNewsExists
            )
        }
    }

    private fun handleNewsExists(exists: Boolean) {
        _newsExists.value = exists
    }

}