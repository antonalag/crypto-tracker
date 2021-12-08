package edu.uoc.tfm.antonalag.cryptotracker.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.BaseViewModel
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.CryptocurrencyCardViewDto
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.CryptocurrencyListViewDto
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.LocalCryptocurrency
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.repository.CryptocurrencyRepository
import edu.uoc.tfm.antonalag.cryptotracker.features.investment.model.Investment
import edu.uoc.tfm.antonalag.cryptotracker.features.investment.repository.InvestmentRepository
import edu.uoc.tfm.antonalag.cryptotracker.features.quote.model.Quote
import edu.uoc.tfm.antonalag.cryptotracker.features.quote.repository.QuoteRepository
import kotlinx.coroutines.*

class HomeViewModel(
    private val cryptocurrencyRepository: CryptocurrencyRepository,
    private val investmentRepository: InvestmentRepository,
    private val quoteRepository: QuoteRepository
) : BaseViewModel() {

    private val TAG = "HomeViewModel"

    private val _saveCryptocurrencies: MutableLiveData<List<Long>> = MutableLiveData()
    val saveCryptocurrencies: LiveData<List<Long>> = _saveCryptocurrencies

    private val _localCryptocurencies: MutableLiveData<List<LocalCryptocurrency>> =
        MutableLiveData()
    val localCryptocurrencies: LiveData<List<LocalCryptocurrency>> = _localCryptocurencies

    private val _quote: MutableLiveData<Quote> = MutableLiveData()
    val quote: LiveData<Quote> = _quote

    private val _cryptocurrencyListViewDtoList: MutableLiveData<List<CryptocurrencyListViewDto>> =
        MutableLiveData()
    val cryptocurrencyListViewDtoList: LiveData<List<CryptocurrencyListViewDto>> =
        _cryptocurrencyListViewDtoList

    private val _cryptocurrencyCardViewDtoList: MutableLiveData<List<CryptocurrencyCardViewDto>> =
        MutableLiveData()
    val cryptocurrencyCardViewDtoList: LiveData<List<CryptocurrencyCardViewDto>> =
        _cryptocurrencyCardViewDtoList

    private val _investments: MutableLiveData<List<Investment>> = MutableLiveData()
    val investments: LiveData<List<Investment>> = _investments

    private val _saveInvestment: MutableLiveData<Long> = MutableLiveData()
    val saveInvestment: LiveData<Long> = _saveInvestment

    private val _isInvestmentDeleted: MutableLiveData<Boolean> = MutableLiveData()
    val isInvestmentDeleted: LiveData<Boolean> = _isInvestmentDeleted

    private val _isInvestmentUpdated: MutableLiveData<Boolean> = MutableLiveData()
    val isInvestmentUpdated: LiveData<Boolean> = _isInvestmentUpdated

    private val _isLocalCryptocurrencyDeleted: MutableLiveData<Boolean> = MutableLiveData()
    val isLocalCryptocurrencyDeleted: LiveData<Boolean> = _isLocalCryptocurrencyDeleted

    /**
     * Local request to save cryptocurrencies
     */
    fun saveCryptocurrencies(list: List<LocalCryptocurrency>) {
        Log.v(TAG, "Request to save cryptocurrencies")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cryptocurrencyRepository.save(list)
            }.fold(
                ::handleFail,
                ::handleSavedCryptocurrencies
            )
        }
    }

    /**
     * Called when saveCryptocurrencies function is successful
     */
    private fun handleSavedCryptocurrencies(list: List<Long>) {
        _saveCryptocurrencies.value = list
    }

    /**
     * Local request to get local cryptocurrencies
     */
    fun getLocalCryptocurrencies(userId: Long) {
        Log.v(TAG, "Request cryptocurrencies saved in local storage for user id: $userId")
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
     * Called when handleLocalCryptocurrencies function is successful
     */
    private fun handleLocalCryptocurrencies(list: List<LocalCryptocurrency>) {
        _localCryptocurencies.value = list
    }

    /**
     * External API request to get quote of the day
     */
    fun getPhraseOfTheDay() {
        Log.v(TAG, "Request phrase of the day")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                quoteRepository.getPhraseOfTheDay()
            }.fold(
                ::handleFail,
                ::handleQuote
            )
        }
    }

    /**
     * Called when getPhraseOfTheDay function is successful
     */
    private fun handleQuote(phrase: Quote) {
        _quote.value = phrase
    }

    /**
     * External API request to get cryptocurrencies converted in specific entity
     */
    fun getCryptocurrencytListViewDtoList(currency: String, skip: Int? = 0, limit: Int? = 20) {
        Log.v(
            TAG,
            "Request paged cryptocurrency list views for currency: $currency. Skip: $skip, limit: $limit"
        )
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cryptocurrencyRepository.findAllCryptocurrencyListViewDto(currency, skip, limit)
            }.fold(
                ::handleFail,
                ::handleListViewDtoList
            )
        }
    }

    /**
     * Called when getCryptocurrencytListViewDtoList function is successful
     */
    private fun handleListViewDtoList(list: List<CryptocurrencyListViewDto>) {
        _cryptocurrencyListViewDtoList.value = list
    }

    /**
     * External API request to get cryptocurrencies converted in specific entity
     */
    fun getCryptocurrencyCardViewDtoList(names: List<String>, currency: String, period: String) {
        Log.v(
            TAG,
            "Request cryptocurrency card views from remote API for names: " + names.joinToString { it } + " and currency: $currency and period: $period")
        viewModelScope.launch {
            coroutineScope {
                val deferredCardDtos = names.map {
                    async {
                        cryptocurrencyRepository.findByNameAndCurrencyCardViewDto(
                            it,
                            currency
                        )
                    }
                }
                // Await all async requests
                val cardDtos: List<Either<Fail, CryptocurrencyCardViewDto>> =
                    deferredCardDtos.awaitAll()
                // Check if any petition is failed and log it
                val failed = cardDtos.filter { it.isLeft }.count()
                if (failed > 0) {
                    Log.w(TAG, "Unable to obtain $failed cryptocurrencies card views")
                }
                val successList = cardDtos.mapNotNull {
                    when (it) {
                        is Either.Right -> it.success
                        is Either.Left -> null
                    }
                }
                handleCardViewDtoList(successList, period)
            }
        }
    }

    /**
     * Handle data requested from getCryptocurrencyCardViewDtoList when is successful and request an external API to get cryptocurrency charts
     */
    private fun handleCardViewDtoList(
        cardViewDtoList: List<CryptocurrencyCardViewDto>,
        period: String
    ) {
        Log.v(
            TAG,
            "Request cryptocurrency charts from remote API for names: " + cardViewDtoList.joinToString { it.name } + " and period: $period")
        viewModelScope.launch {
            cardViewDtoList.forEach {
                val eitherChart = withContext(Dispatchers.IO) {
                    cryptocurrencyRepository.findChart(period, it.name.toLowerCase())
                }
                when (eitherChart) {
                    is Either.Left -> it.chart = emptyList()
                    is Either.Right -> it.chart = eitherChart.success
                }
            }
            _cryptocurrencyCardViewDtoList.value = cardViewDtoList
        }
    }

    /**
     * Local request to get investments
     */
    fun getInvestments(cryptocurrencyIds: List<Long>) {
        Log.v(
            TAG,
            "Request investments saved in local storage for cryptocurrency ids: " + cryptocurrencyIds.joinToString { it.toString() })
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                investmentRepository.findByCryptocurrencyIds(cryptocurrencyIds)
            }.fold(
                ::handleFail,
                ::handleInvestments
            )
        }
    }

    /**
     * Called when getInvestments function is successful
     */
    private fun handleInvestments(investments: List<Investment>) {
        _investments.value = investments
    }

    /**
     * Local request to save investment
     */
    fun saveInvestment(investment: Investment) {
        Log.v(TAG, "Request to save investment")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                investmentRepository.save(investment)
            }.fold(
                ::handleFail,
                ::handleSavedInvestments
            )
        }
    }

    /**
     * Called when saveInvestment function is successful
     */
    private fun handleSavedInvestments(investmentId: Long) {
        _saveInvestment.value = investmentId
    }

    /**
     * Local request to delete investment
     */
    fun deleteInvestment(id: Long) {
        Log.v(TAG, "Request to delete investment with id: $id")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                investmentRepository.delete(id)
            }.fold(
                ::handleFail,
                ::handleDeleteInvestment
            )
        }
    }

    /**
     * Called when deleteInvestment function is successful
     */
    private fun handleDeleteInvestment(count: Int) {
        _isInvestmentDeleted.value = count > 0
    }

    /**
     * Local request to update investment
     */
    fun updateInvestment(investment: Investment) {
        Log.v(TAG, "Request to update investment")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                investmentRepository.update(investment)
            }.fold(
                ::handleFail,
                ::handleUpdateInvestment
            )
        }
    }

    /**
     * Called when updateInvestment function is successful
     */
    private fun handleUpdateInvestment(count: Int) {
        _isInvestmentUpdated.value = count > 0
    }

    /**
     * Local request to delete local cryptocurrency
     */
    fun deleteLocalCryptocurrency(id: Long) {
        Log.v(TAG, "Request to delete local cryptocurrency with id: $id")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cryptocurrencyRepository.delete(id)
            }.fold(
                ::handleFail,
                ::handleDeleteLocalCryptocurrency
            )
        }
    }

    /**
     * Called when deleteLocalCryptocurrency function is successful
     */
    private fun handleDeleteLocalCryptocurrency(count: Int) {
        _isLocalCryptocurrencyDeleted.value = count > 0
    }

}