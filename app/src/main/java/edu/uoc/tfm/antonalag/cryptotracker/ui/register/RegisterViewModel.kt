package edu.uoc.tfm.antonalag.cryptotracker.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.BaseViewModel
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.User
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPassword
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPreferences
import edu.uoc.tfm.antonalag.cryptotracker.features.user.repository.UserRepository
import kotlinx.coroutines.*

class RegisterViewModel(
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val TAG = "RegisterViewModel"

    private val _userExists: MutableLiveData<Boolean> = MutableLiveData()
    val userExists: LiveData<Boolean> = _userExists

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User> = _user

    private val _userPreferences: MutableLiveData<UserPreferences> = MutableLiveData()
    val userPreferences: LiveData<UserPreferences> = _userPreferences

    private val _userPassword: MutableLiveData<UserPassword> = MutableLiveData()
    val userPassword: LiveData<UserPassword> = _userPassword

    /**
     * Local request to check if exists an user with specific email
     */
    fun userExists(email: String) {
        Log.v(TAG,"Request to check if user already exists by email: $email")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.userExistsByEmail(email)
            }.fold(
                ::handleFail,
                ::handleUserExists
            )
        }
    }

    /**
     * Called when userExists function is successful
     */
    private fun handleUserExists(userExists: Boolean) {
        _userExists.value = userExists
    }

    /**
     * Local request to create an user
     */
    fun createUser(user: User) {
        Log.v(TAG, "Request to create user")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.saveUser(user)
            }.fold(
                ::handleFail,
                ::getUser
            )
        }
    }

    /**
     * Local request to get user
     */
    private fun getUser(id: Long) {
        Log.v(TAG, "Request to get an user with id: $id")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.findUserById(id)
            }.fold(
                ::handleFail,
                ::handleGetUser
            )

        }
    }

    /**
     * Called when getUser function is successful
     */
    private fun handleGetUser(user: User) {
        _user.value = user
    }

    /**
     * Local request to create user preferences
     */
    fun createUserPreferences(userPreferences: UserPreferences) {
        Log.v(TAG, "Request to create user preferences")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.savePreferences(userPreferences)
            }.fold(
                ::handleFail,
                ::getUserPreferences
            )
        }
    }

    /**
     * Local request to get user preferences
     */
    private fun getUserPreferences(id: Long) {
        Log.v(TAG, "Request to get user preferences with id: $id")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.findPreferencesById(id)
            }.fold(
                ::handleFail,
                ::handleUserPreferences
            )
        }
    }

    /**
     * Called when getUserPreferences function is successful
     */
    private fun handleUserPreferences(userPreferences: UserPreferences) {
        _userPreferences.value = userPreferences
    }

    /**
     * Local request to create user password
     */
    fun createUserPassword(password: UserPassword) {
        Log.v(TAG, "Request to create user password")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.savePassword(password)
            }.fold(
                ::handleFail,
                ::getUserPassword
            )
        }
    }

    /**
     * Local request to get user password
     */
    private fun getUserPassword(id: Long) {
        Log.v(TAG, "Request to get user password with id: $id")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.findPasswordById(id)
            }.fold(
                ::handleFail,
                ::handleGetUserPassword
            )
        }
    }

    /**
     * Called when getUserPassword function is successful
     */
    private fun handleGetUserPassword(userPassword: UserPassword) {
        _userPassword.value = userPassword
    }

}