package edu.uoc.tfm.antonalag.cryptotracker.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.BaseViewModel
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.User
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPassword
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPreferences
import edu.uoc.tfm.antonalag.cryptotracker.features.user.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val userRepository: UserRepository
): BaseViewModel() {

    private val TAG = ""

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User> = _user
    private val _userPreferences: MutableLiveData<UserPreferences> = MutableLiveData()
    val userPreferences: LiveData<UserPreferences> = _userPreferences
    private val _password: MutableLiveData<UserPassword> = MutableLiveData()
    val password = _password
    private val _isUserUpdated: MutableLiveData<Boolean> = MutableLiveData()
    val isUserUpdated: LiveData<Boolean> = _isUserUpdated
    private val _isSessionSaved: MutableLiveData<Boolean> = MutableLiveData()
    val isSessionSaved: LiveData<Boolean> = _isSessionSaved

    fun getUser(email: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.findUserByEmail(email)
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

    private fun handleUserPreferences(preferences: UserPreferences) {
        _userPreferences.value = preferences
    }

    fun getPassword(userId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.findPasswordByUserId(userId)
            }.fold(
                ::handleFail,
                ::handlePassword
            )
        }
    }

    private fun handlePassword(password: UserPassword) {
        _password.value = password
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

    fun login(userId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.login(userId)
            }.fold(
                ::handleFail,
                ::handleSessionSaved
            )
        }
    }

    private fun handleSessionSaved(saved: Boolean) {
        _isSessionSaved.value = saved
    }
}