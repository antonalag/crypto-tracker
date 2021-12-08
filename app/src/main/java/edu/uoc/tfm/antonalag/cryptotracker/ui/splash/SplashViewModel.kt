package edu.uoc.tfm.antonalag.cryptotracker.ui.splash

import android.util.Log
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

    private val TAG = "SplashViewModel"

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

    /**
     * Local request to check if there any user session stored
     */
    fun isUserAvailable() {
        Log.v(TAG, "Request to check if there any user session stored")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.isUserAvailable()
            }.fold(
                ::handleFail,
                ::handleIsUserAvailable
            )
        }
    }

    /**
     * Called when isUserAvailable function is successful
     */
    private fun handleIsUserAvailable(isAvailable: Boolean) {
        _isUserAvailable.value = isAvailable
    }

    /**
     * Local request to get user session id
     */
    fun getSessionUserId() {
        Log.v(TAG, "Request to get user session id stored")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.getSessionUserId()
            }.fold(
                ::handleFail,
                ::handleSessionUserId
            )
        }
    }

    /**
     * Called when getSessionUserId function is successful
     */
    fun handleSessionUserId(userId: Long) {
        _sessionUserId.value = userId
    }

    /**
     * Local request to get user
     */
    fun getUser(id: Long) {
        Log.v(TAG,"Request to get user with id: $id")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.findUserById(id)
            }.fold(
                ::handleFail,
                ::handleUser
            )
        }
    }

    /**
     * Called when getUser function is successful
     */
    private fun handleUser(user: User) {
        _user.value = user
    }

    /**
     * Local request to get user preferences
     */
    fun getUserPreferences(userId: Long) {
        Log.v(TAG,"Request to get user preferences with user id: $userId")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.findPreferencesByUserId(userId)
            }.fold(
                ::handleFail,
                ::handleUserPreferences
            )
        }
    }

    /**
     * Called when getUserPreferences function is successful
     */
    private fun handleUserPreferences(preferences: UserPreferences) {
        _userPreferences.value = preferences
    }

    /**
     * Local request to update user
     */
    fun updateUser(user: User) {
        Log.v(TAG, "Request to update user")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.updateUser(user)
            }.fold(
                ::handleFail,
                ::handleUpdateUser
            )
        }
    }

    /**
     * Called when updateUser function is successful
     */
    private fun handleUpdateUser(id: Int) {
        _isUserUpdated.value = id > 0
    }

}