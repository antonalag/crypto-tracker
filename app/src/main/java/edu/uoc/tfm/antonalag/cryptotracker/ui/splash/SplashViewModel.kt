package edu.uoc.tfm.antonalag.cryptotracker.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.BaseViewModel
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.User
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPreferences
import edu.uoc.tfm.antonalag.cryptotracker.features.user.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashViewModel(
    private val userRepository: UserRepository
): BaseViewModel() {

    private val _isUserAvailable: MutableLiveData<Boolean> = MutableLiveData()
    val isUserAvailable: LiveData<Boolean> = _isUserAvailable
    private val _sessionUserId: MutableLiveData<Long> = MutableLiveData()
    val sesssionUserId: LiveData<Long> = _sessionUserId
    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User> = _user
    private val _userPreferences: MutableLiveData<UserPreferences> = MutableLiveData()
    val userPreferences: LiveData<UserPreferences> = _userPreferences
    private val _isUserUpdated: MutableLiveData<Boolean> = MutableLiveData()
    val isUserUpdated: LiveData<Boolean> = _isUserUpdated

    fun isUserAvailable() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.isUserAvailable()
            }.fold(
                ::handleFail,
                ::handleIsUserAvailable
            )
        }
    }

    private fun handleIsUserAvailable(isAvailable: Boolean) {
        _isUserAvailable.value = isAvailable
    }

    fun getSessionUserId() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.getSessionUserId()
            }.fold(
                ::handleFail,
                ::handleSessionUserId
            )
        }
    }

    fun handleSessionUserId(userId: Long) {
        _sessionUserId.value = userId
    }

    fun getUser(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.findUserById(id)
            }.fold(
                ::handleFail,
                ::handleUser
            )
        }
    }

    private fun handleUser(user: User) {
        _user.value = user
    }

    fun getUserPreferences(userId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.findPreferencesByUserId(userId)
            }.fold(
                ::handleFail,
                ::handleUserPreferences
            )
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.updateUser(user)
            }.fold(
                ::handleFail,
                ::handleUpdateUser
            )
        }
    }

    private fun handleUpdateUser(id: Int) {
        _isUserUpdated.value = id > 0
    }

    private fun handleUserPreferences(preferences: UserPreferences) {
        _userPreferences.value = preferences
    }

}