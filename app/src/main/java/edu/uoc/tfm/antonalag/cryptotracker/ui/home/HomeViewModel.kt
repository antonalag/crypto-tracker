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

    fun saveCryptocurrencies(list: List<LocalCryptocurrency>) {
        Log.v(TAG, "Save cryptocurrencies")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cryptocurrencyRepository.save(list)
            }.fold(
                ::handleFail,
                ::handleSavedCryptocurrencies
            )
        }
    }

    private fun handleSavedCryptocurrencies(list: List<Long>) {
        _saveCryptocurrencies.value = list
    }

    fun getLocalCryptocurrencies(userId: Long) {
        Log.v(TAG, "Request cryptocurrencies saved in local storage for user[$userId]")
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
        _localCryptocurencies.value = list
    }

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

    private fun handleQuote(phrase: Quote) {
        _quote.value = phrase
    }

    fun getCryptocurrencytListViewDtoList(currency: String, skip: Int? = 0, limit: Int? = 20) {
        Log.v(TAG, "Request paged cryptocurrency list views from remote API")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cryptocurrencyRepository.findAllCryptocurrencyListViewDto(currency, skip, limit)
            }.fold(
                ::handleFail,
                ::handleListViewDtoList
            )
        }
    }

    private fun handleListViewDtoList(list: List<CryptocurrencyListViewDto>) {
        _cryptocurrencyListViewDtoList.value = list
    }

    fun getCryptocurrencyCardViewDtoList(names: List<String>, currency: String, period: String) {
        Log.v(
            TAG,
            "Request cryptocurrency card views from remote API for " + names.joinToString { it })
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
                        else -> null
                    }
                }
                handleCardViewDtoList(successList, period)
            }
        }
    }

    private fun handleCardViewDtoList(
        cardViewDtoList: List<CryptocurrencyCardViewDto>,
        period: String
    ) {
        Log.v(
            TAG,
            "Request cryptocurrency charts from remote API for " + cardViewDtoList.joinToString { it.name })
        viewModelScope.launch {
            cardViewDtoList.forEach {
                val eitherChart = withContext(Dispatchers.IO) {
                    cryptocurrencyRepository.findChart(period, it.name.toLowerCase())
                }
                when(eitherChart) {
                    is Either.Left -> it.chart = emptyList()
                    is Either.Right -> it.chart = eitherChart.success
                }
                /*when (val eitherChart =
                    cryptocurrencyRepository.findChart(period, it.name.toLowerCase())) {
                    is Either.Left -> it.chart = emptyList()
                    is Either.Right -> it.chart = eitherChart.success
                }*/
            }
            _cryptocurrencyCardViewDtoList.value = cardViewDtoList
        }
    }

    fun getInvestments(cryptocurrencyIds: List<Long>) {
        Log.v(
            TAG,
            "Request investments saved in local storage for " + cryptocurrencyIds.joinToString { it.toString() })
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                investmentRepository.findByCryptocurrencyIds(cryptocurrencyIds)
            }.fold(
                ::handleFail,
                ::handleInvestments
            )
        }
    }

    private fun handleInvestments(investments: List<Investment>) {
        _investments.value = investments
    }

    fun saveInvestment(investment: Investment) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                investmentRepository.save(investment)
            }.fold(
                ::handleFail,
                ::handleSavedInvestments
            )
        }
    }

    private fun handleSavedInvestments(investmentId: Long) {
        _saveInvestment.value = investmentId
    }

    fun deleteInvestment(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                investmentRepository.delete(id)
            }.fold(
                ::handleFail,
                ::handleDeleteInvestment
            )
        }
    }

    private fun handleDeleteInvestment(count: Int) {
        _isInvestmentDeleted.value = count > 0
    }

    fun updateInvestment(investment: Investment) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                investmentRepository.update(investment)
            }.fold(
                ::handleFail,
                ::handleUpdateInvestment
            )
        }
    }

    private fun handleUpdateInvestment(count: Int) {
        _isInvestmentUpdated.value = count > 0
    }

    fun deleteLocalCryptocurrency(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cryptocurrencyRepository.delete(id)
            }.fold(
                ::handleFail,
                ::handleDeleteLocalCryptocurrency
            )
        }
    }

    private fun handleDeleteLocalCryptocurrency(count: Int) {
        _isLocalCryptocurrencyDeleted.value = count > 0
    }

}