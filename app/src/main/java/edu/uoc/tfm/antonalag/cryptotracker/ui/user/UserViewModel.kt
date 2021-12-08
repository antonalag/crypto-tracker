package edu.uoc.tfm.antonalag.cryptotracker.ui.user

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.BaseViewModel
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.getOrElse
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.LocalCryptocurrency
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.repository.CryptocurrencyRepository
import edu.uoc.tfm.antonalag.cryptotracker.features.investment.repository.InvestmentRepository
import edu.uoc.tfm.antonalag.cryptotracker.features.news.repository.NewsRepository
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.User
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPassword
import edu.uoc.tfm.antonalag.cryptotracker.features.user.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(
    private val userRepository: UserRepository,
    private val cryptocurrencyRepository: CryptocurrencyRepository,
    private val newsRepository: NewsRepository,
    private val investmentRepository: InvestmentRepository
    ): BaseViewModel() {

    private val TAG = "UserViewModel"

    private val _userExists: MutableLiveData<Boolean> = MutableLiveData()
    val userExists: LiveData<Boolean> = _userExists

    private val _isUserUpdated: MutableLiveData<Boolean> = MutableLiveData()
    val isUserUpdated: LiveData<Boolean> = _isUserUpdated

    private val _userPassword: MutableLiveData<UserPassword> = MutableLiveData()
    val userPassword: LiveData<UserPassword> = _userPassword

    private val _isPasswordUpdated: MutableLiveData<Boolean> = MutableLiveData()
    val isPasswordUpdated: LiveData<Boolean> = _isPasswordUpdated

    private val _isLoggedOut: MutableLiveData<Boolean> = MutableLiveData()
    val isLoggedOut: LiveData<Boolean> = _isLoggedOut

    private val _isAllDeleted: MutableLiveData<Boolean> = MutableLiveData()
    val isAllDeleted: LiveData<Boolean> = _isAllDeleted

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
     * Local request to get user password
     */
    fun getPassword(userId: Long) {
        Log.v(TAG, "Request to get user password with user id: $userId")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.findPasswordByUserId(userId)
            }.fold(
                ::handleFail,
                ::handleUserPassword
            )
        }
    }

    /**
     * Called when getPassword function is successful
     */
    private fun handleUserPassword(userPassword: UserPassword) {
        _userPassword.value = userPassword
    }

    /**
     * Local request to update user password
     */
    fun updatePassword(userPassword: UserPassword) {
        Log.v(TAG, "Request to update user password")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.updatePassword(userPassword)
            }.fold(
                ::handleFail,
                ::handleUpdatePassword
            )
        }
    }

    /**
     * Called when updatePassword function is successful
     */
    private fun handleUpdatePassword(id: Int) {
        _isPasswordUpdated.value = id > 0
    }

    /**
     * Local request to user logout
     */
    fun logout(){
        Log.v(TAG, "Request to user logout")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.logout()
            }.fold(
                ::handleFail,
                ::handleLogout
            )
        }
    }

    /**
     * Called when logout function is successful
     */
    private fun handleLogout(isLoggedOut: Boolean) {
        _isLoggedOut.value = isLoggedOut
    }

    /**
     * Local request to delete user account
     */
    fun deleteAll(userId: Long) {
        Log.v(TAG, "Request to delete user account")
        viewModelScope.launch {
            // Delete all saved news
            withContext(Dispatchers.IO) {
                newsRepository.deleteByUserId(userId)
            }
            // Get all cryptocurrencies
            val cryptocurrencies: List<LocalCryptocurrency> = withContext(Dispatchers.IO) {
                cryptocurrencyRepository.findAllByUserId(userId)
            }.getOrElse(emptyList())

            if(cryptocurrencies.isEmpty()) {
                // Delete all investment
                withContext(Dispatchers.IO) {
                    investmentRepository.deleteByCryptocurrencyIds(cryptocurrencies.map { it.id })
                }
                // Delete all cryptocurrencies
                withContext(Dispatchers.IO) {
                    cryptocurrencyRepository.deleteByUserId(userId)
                }
            }

            // Delete user password
            withContext(Dispatchers.IO) {
                userRepository.deletePasswordByUserId(userId)
            }
            // Delete preferences
            withContext(Dispatchers.IO) {
                userRepository.deletePreferencesByUserId(userId)
            }
            // Delete user
            withContext(Dispatchers.IO) {
                userRepository.deleteUser(userId)
            }
            // Remove user from session manager
            withContext(Dispatchers.IO) {
                userRepository.logout()
            }
            handleDeleteAll(true)
        }
    }

    /**
     * Called when deleteAll function is successful
     */
    private fun handleDeleteAll(isAllDeleted: Boolean) {
        _isAllDeleted.value = isAllDeleted
    }



}