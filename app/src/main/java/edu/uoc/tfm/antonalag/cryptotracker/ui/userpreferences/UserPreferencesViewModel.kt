package edu.uoc.tfm.antonalag.cryptotracker.ui.userpreferences

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.BaseViewModel
import edu.uoc.tfm.antonalag.cryptotracker.features.fiat.model.Fiat
import edu.uoc.tfm.antonalag.cryptotracker.features.fiat.repository.FiatRepository
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPreferences
import edu.uoc.tfm.antonalag.cryptotracker.features.user.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserPreferencesViewModel(
    private val userRepository: UserRepository,
    private val fiatRepository: FiatRepository
): BaseViewModel() {

    private val _isUserPreferencesUpdated: MutableLiveData<Boolean> = MutableLiveData()
    val isUserPreferencesUpdated: LiveData<Boolean> = _isUserPreferencesUpdated

    private val _fiatCurrencies: MutableLiveData<List<Fiat>> = MutableLiveData()
    val fiatCurrencies: LiveData<List<Fiat>> = _fiatCurrencies

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
        _fiatCurrencies.value = list
    }

    fun updatePreferences(userPreferences: UserPreferences) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.updatePreferences(userPreferences)
            }.fold(
                ::handleFail,
                ::handleUpdatePreferences
            )
        }
    }

    private fun handleUpdatePreferences(rowUpdated: Int) {
        _isUserPreferencesUpdated.value = rowUpdated > 0
    }
}