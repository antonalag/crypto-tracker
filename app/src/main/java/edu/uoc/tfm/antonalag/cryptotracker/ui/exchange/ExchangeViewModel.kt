package edu.uoc.tfm.antonalag.cryptotracker.ui.exchange

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.BaseViewModel
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.CryptocurrencyListViewDto
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.LocalCryptocurrency
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.repository.CryptocurrencyRepository
import edu.uoc.tfm.antonalag.cryptotracker.features.fiat.model.Fiat
import edu.uoc.tfm.antonalag.cryptotracker.features.fiat.repository.FiatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExchangeViewModel(
    val cryptocurrencyRepository: CryptocurrencyRepository,
    val fiatRepository: FiatRepository
): BaseViewModel() {

    private val TAG = "ExchangeViewModel"

    private val _fiats: MutableLiveData<List<Fiat>> = MutableLiveData()
    val fiats: LiveData<List<Fiat>> = _fiats

    private val _cryptocurrencyListViewDtos: MutableLiveData<List<CryptocurrencyListViewDto>> = MutableLiveData()
    val cryptocurrencyListViewDtos: LiveData<List<CryptocurrencyListViewDto>> = _cryptocurrencyListViewDtos

    private val _localCryptocurrencies: MutableLiveData<List<LocalCryptocurrency>> = MutableLiveData()
    val localCryptocurrencies: LiveData<List<LocalCryptocurrency>> = _localCryptocurrencies

    /**
     * External API request to get fiats data
     */
    fun getFiats() {
        Log.v(TAG, "Request fiats data")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                fiatRepository.findAll()
            }.fold(
                ::handleFail,
                ::handleFiats
            )
        }
    }

    /**
     * Called when getFiats function is successful
     */
    private fun handleFiats(list: List<Fiat>) {
        _fiats.value = list
    }

    /**
     * External API request to get cryptocurrencies paginated data
     */
    fun getCryptocurrencies(currency: String, skip: Int? = 0, limit: Int? = 20) {
        Log.v(TAG, "Request cryptocurrencies data for currency: $currency. Skipped: $skip, limit: $limit")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cryptocurrencyRepository.findAllCryptocurrencyListViewDto(currency, skip, limit)
            }.fold(
                ::handleFail,
                ::handleCryptocurrencies
            )
        }
    }

    /**
     * Called when getCryptocurrencies function is successful
     */
    private fun handleCryptocurrencies(list: List<CryptocurrencyListViewDto>) {
        _cryptocurrencyListViewDtos.value = list
    }

    /**
     * Local request to get cryptocurrencies data
     */
    fun getLocalCryptocurrencies(userId: Long) {
        Log.v(TAG, "Request cryptocurrencies data for user id: $userId")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cryptocurrencyRepository.findAllByUserId(userId)
            }.fold(
                ::handleFail,
                ::handleLocalCryptocurrencies
            )
        }
    }

    /**
     * Called when getLocalCryptocurrencies function is successful
     */
    private fun handleLocalCryptocurrencies(list: List<LocalCryptocurrency>) {
        _localCryptocurrencies.value = list
    }

}