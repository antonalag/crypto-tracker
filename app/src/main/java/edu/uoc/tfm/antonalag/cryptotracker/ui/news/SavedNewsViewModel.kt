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

class SavedNewsViewModel(
    private val newsRepository: NewsRepository
): BaseViewModel() {

    private val TAG = "SavedNewsViewModel"

    private val _localNews: MutableLiveData<List<LocalNews>> = MutableLiveData()
    val localNews: LiveData<List<LocalNews>> = _localNews

    private val _deletedNewsCount: MutableLiveData<Int> = MutableLiveData()
    val deletedNewsCount: LiveData<Int> = _deletedNewsCount

    /**
     * Local request to get saved news
     */
    fun getLocalNews(userId: Long) {
        Log.v(TAG, "Request to get saved news for user id: $userId")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                newsRepository.findAllByUserId(userId)
            }.fold(
                ::handleFail,
                ::handleLocalNews
            )
        }
    }

    /**
     * Called when getLocalNews function is successful
     */
    private fun handleLocalNews(list: List<LocalNews>) {
        _localNews.value = list
    }

    /**
     * Local request to delete a saved news
     */
    fun deleteNews(id: Long) {
        Log.v(TAG, "Request to delete a saved news with id: $id")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                newsRepository.delete(id)
            }.fold(
                ::handleFail,
                ::handleDeleteNews
            )
        }
    }

    /**
     * Called when deleteNews function is successful
     */
    private fun handleDeleteNews(count: Int) {
        _deletedNewsCount.value = count
    }

}