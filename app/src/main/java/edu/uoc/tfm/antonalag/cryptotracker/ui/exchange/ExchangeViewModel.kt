package edu.uoc.tfm.antonalag.cryptotracker.ui.exchange

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

    private val _fiats: MutableLiveData<List<Fiat>> = MutableLiveData()
    val fiats: LiveData<List<Fiat>> = _fiats

    private val _cryptocurrencyListViewDtos: MutableLiveData<List<CryptocurrencyListViewDto>> = MutableLiveData()
    val cryptocurrencyListViewDtos: LiveData<List<CryptocurrencyListViewDto>> = _cryptocurrencyListViewDtos

    private val _localCryptocurrencies: MutableLiveData<List<LocalCryptocurrency>> = MutableLiveData()
    val localCryptocurrencies: LiveData<List<LocalCryptocurrency>> = _localCryptocurrencies

    fun getFiats() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                fiatRepository.findAll()
            }.fold(
                ::handleFail,
                ::handleFiats
            )
        }
    }

    private fun handleFiats(list: List<Fiat>) {
        _fiats.value = list
    }

    fun getCryptocurrencies(currency: String, skip: Int? = 0, limit: Int? = 20) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cryptocurrencyRepository.findAllCryptocurrencyListViewDto(currency, skip, limit)
            }.fold(
                ::handleFail,
                ::handleCryptocurrencies
            )
        }
    }

    private fun handleCryptocurrencies(list: List<CryptocurrencyListViewDto>) {
        _cryptocurrencyListViewDtos.value = list
    }

    fun getLocalCryptocurrencies(userId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cryptocurrencyRepository.findAllByUserId(userId)
            }.fold(
                ::handleFail,
                ::handleLocalCryptocurrencies
            )
        }
    }

    private fun handleLocalCryptocurrencies(list: List<LocalCryptocurrency>) {
        _localCryptocurrencies.value = list
    }

}