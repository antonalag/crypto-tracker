package edu.uoc.tfm.antonalag.cryptotracker.ui.login

import android.util.Log
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

    private val TAG = "LoginViewModel"

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

    /**
     * Local request to get user data
     */
    fun getUser(email: String) {
        Log.v(TAG, "Request to get user data by email: $email")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.findUserByEmail(email)
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
     * Local request to get user preferences data
     */
    fun getUserPreferences(userId: Long) {
        Log.v(TAG, "Request to get user preferences data by user id: $userId")
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
     * Local request to get user password data
     */
    fun getPassword(userId: Long) {
        Log.v(TAG, "Request to get user password data by user id: $userId")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.findPasswordByUserId(userId)
            }.fold(
                ::handleFail,
                ::handlePassword
            )
        }
    }

    /**
     * Called when getPassword function is successful
     */
    private fun handlePassword(password: UserPassword) {
        _password.value = password
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

    /**
     * Local request to login user
     */
    fun login(userId: Long) {
        Log.v(TAG, "Request to login by user id: $userId")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.login(userId)
            }.fold(
                ::handleFail,
                ::handleSessionSaved
            )
        }
    }

    /**
     * Called when login function is successful
     */
    private fun handleSessionSaved(saved: Boolean) {
        _isSessionSaved.value = saved
    }
}