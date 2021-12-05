package edu.uoc.tfm.antonalag.cryptotracker.ui.cryptocurrency

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.BaseViewModel
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.Cryptocurrency
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.CryptocurrencyChart
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.CryptocurrencyResponse
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.repository.CryptocurrencyRepository
import edu.uoc.tfm.antonalag.cryptotracker.features.investment.model.Investment
import edu.uoc.tfm.antonalag.cryptotracker.features.investment.repository.InvestmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CryptocurrencyStatisticsViewModel(
    private val cryptocurrencyRepository: CryptocurrencyRepository,
    private val investmentRepository: InvestmentRepository,
): BaseViewModel() {

    private val TAG = "CryptocurrencyViewModel"

    private val _cryptocurrency: MutableLiveData<Cryptocurrency> = MutableLiveData()
    val cryptocurrency: LiveData<Cryptocurrency> = _cryptocurrency
    private val _investment: MutableLiveData<Investment> = MutableLiveData()
    val investment: LiveData<Investment> = _investment
    private val _cryptocurrencyChart: MutableLiveData<List<CryptocurrencyChart>> = MutableLiveData()
    val cryptocurrencyChart: LiveData<List<CryptocurrencyChart>> = _cryptocurrencyChart

    fun getCryptocurrencyData(name: String, currency: String) {
        Log.v(TAG, "Request cryptocurrency data for $name")
        viewModelScope.launch {
            cryptocurrencyRepository.findByNameAndCurrency(name, currency).fold(
                ::handleFail,
                ::handleCryptocurrency
            )
        }
    }

    private fun handleCryptocurrency(cryptocurrencyResponse: CryptocurrencyResponse){
        _cryptocurrency.value = cryptocurrencyResponse.data
    }

    fun getInvestmentData(cryptocurrencyId: Long) {
        Log.v(TAG, "Request investment data")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                investmentRepository.findByCryptocurrencyId(cryptocurrencyId)
            }.fold(
                ::handleFail,
                ::handleInvestment
            )
        }
    }

    private fun handleInvestment(data: Investment) {
        _investment.value = data
    }

    fun getCryptocurrencyChartData(name: String, period: String) {
        Log.v(TAG, "Request cryptocurrency char data for $name")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cryptocurrencyRepository.findChart(period, name.toLowerCase())
            }.fold(
                ::handleFail,
                ::handleChart
            )
        }
    }

    private fun handleChart(data: List<CryptocurrencyChart>) {
        _cryptocurrencyChart.value = data
    }

}