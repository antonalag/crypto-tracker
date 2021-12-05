package edu.uoc.tfm.antonalag.cryptotracker.ui.register

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

    fun userExists(email: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.userExistsByEmail(email)
            }.fold(
                ::handleFail,
                ::handleUserExists
            )
        }
    }

    private fun handleUserExists(userExists: Boolean) {
        _userExists.value = userExists
    }

    fun createUser(user: User) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.saveUser(user)
            }.fold(
                ::handleFail,
                ::getUser
            )
        }
    }

    private fun getUser(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.findUserById(id)
            }.fold(
                ::handleFail,
                ::handleGetUser
            )

        }
    }

    private fun handleGetUser(user: User) {
        _user.value = user
    }


    fun createUserPreferences(userPreferences: UserPreferences) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.savePreferences(userPreferences)
            }.fold(
                ::handleFail,
                ::getUserPreferences
            )
        }
    }

    private fun getUserPreferences(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.findPreferencesById(id)
            }.fold(
                ::handleFail,
                ::handleUserPreferences
            )
        }
    }

    private fun handleUserPreferences(userPreferences: UserPreferences) {
        _userPreferences.value = userPreferences
    }

    fun createUserPassword(password: UserPassword) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.savePassword(password)
            }.fold(
                ::handleFail,
                ::getUserPassword
            )
        }
    }

    private fun getUserPassword(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.findPasswordById(id)
            }.fold(
                ::handleFail,
                ::handleGetUserPassword
            )
        }
    }

    private fun handleGetUserPassword(userPassword: UserPassword) {
        _userPassword.value = userPassword
    }

}