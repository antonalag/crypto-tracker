package edu.uoc.tfm.antonalag.cryptotracker.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import edu.uoc.tfm.antonalag.cryptotracker.CryptoTrackerApp
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.observe
import edu.uoc.tfm.antonalag.cryptotracker.core.util.DateUtil
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.User
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPassword
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPreferences
import edu.uoc.tfm.antonalag.cryptotracker.ui.home.HomeView
import edu.uoc.tfm.antonalag.cryptotracker.ui.register.RegisterView
import kotlinx.android.synthetic.main.activity_login_view.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Class responsible of user login
 */
class LoginView : AppCompatActivity() {

    private val TAG = "LoginView"

    private val viewModel by viewModel<LoginViewModel>()
    private lateinit var user: User
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_view)
        initUI()
        initViewModelObservers()
    }

    /**
     * Initializes the properties of the UI elements
     */
    private fun initUI() {
        // Hide loading
        showLoading(false)
        // Set on click in sign up button
        sign_up.setOnClickListener {
            startActivity(Intent(this, RegisterView::class.java))
        }

        // Set on click in sign in button
        sign_in_button.setOnClickListener {
            if (validate()) {
                // Show loading
                showLoading(true)
                // Check if user exists
                viewModel.getUser(email_input.text.toString())
            } else {
                // Show error
                Toast.makeText(
                    applicationContext,
                    R.string.incorrect_login_inputs,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Configure the LoginViewModel's observers
     */
    private fun initViewModelObservers() {
        with(viewModel) {
            observe(user, ::handleUserSuccess)
            fail(fail, ::handleUserFail)
        }

        with(viewModel) {
            observe(userPreferences, ::handleUserPreferencesSuccess)
            fail(fail, ::handleUserPreferencesFail)
        }

        with(viewModel) {
            observe(password, ::handlePasswordSuccess)
            fail(fail, ::handlePasswordFail)
        }

        with(viewModel) {
            observe(isUserUpdated, ::handleIsUserUpdatedSuccess)
            fail(fail, ::handleLoginFail)
        }

        with(viewModel) {
            observe(isSessionSaved, ::handleIsSessionSavedSuccess)
            fail(fail, ::handleLoginFail)
        }
    }

    /**
     * It's called when the request for user data is successful
     */
    private fun handleUserSuccess(user: User) {
        Log.v(TAG, resources.getString(R.string.user_request_successful))
        this.user  = user
        // User exists on database, check if user and password is correct
        viewModel.getPassword(user.id)
    }

    /**
     * It's called when the request for user data has failed
     */
    private fun handleUserFail(fail: Fail) {
        Log.v(TAG, resources.getString(R.string.user_request_failed))
        when (fail) {
            is Fail.LocalFail -> {
                Log.e(TAG, resources.getString(R.string.local_error))
                // Hide loading
                showLoading(false)
                Toast.makeText(this, R.string.incorrect_user, Toast.LENGTH_SHORT)
                    .show()
            }
            is Fail.NotFoundFail -> {
                Log.e(TAG, resources.getString(R.string.not_found_error))
                // Hide loading
                showLoading(false)
                Toast.makeText(applicationContext, R.string.user_not_found, Toast.LENGTH_SHORT)
                    .show()
            }
            else -> {
                Log.e(TAG, resources.getString(R.string.unknown_error))
                // Hide loading
                showLoading(false)
            }
        }
    }

    /**
     * It's called when the request for user preferences data is successful
     */
    private fun handleUserPreferencesSuccess(userPreferences: UserPreferences) {
        Log.v(TAG, resources.getString(R.string.user_preferences_request_successful))
        this.userPreferences = userPreferences
        // update user
        updateUser()
    }

    /**
     * It's called when the request for user preferences data has failed
     */
    private fun handleUserPreferencesFail(fail: Fail) {
        Log.v(TAG, resources.getString(R.string.user_preferences_request_failed))
        when (fail) {
            is Fail.LocalFail -> {
                Log.e(TAG, resources.getString(R.string.local_error))
                // Hide loading
                showLoading(false)
                Toast.makeText(applicationContext, R.string.something_wrong, Toast.LENGTH_SHORT)
                    .show()
            }
            else -> {
                Log.e(TAG, resources.getString(R.string.unknown_error))
                // Hide loading
                showLoading(false)
            }
        }
    }

    /**
     * It's called when the request for user password data is successful
     */
    private fun handlePasswordSuccess(userPassword: UserPassword) {
        Log.v(TAG, resources.getString(R.string.user_password_request_successful))
        val password = userPassword.password
        if(password == password_input.text.toString()) {
            viewModel.getUserPreferences(userPassword.userId)
        } else {
            handlePasswordFail(Fail.LocalFail)
        }
    }

    /**
     * It's called when the request for user password data has failed
     */
    private fun handlePasswordFail(fail: Fail) {
        Log.v(TAG, resources.getString(R.string.user_password_request_failed))
        when (fail) {
            is Fail.LocalFail -> {
                Log.e(TAG, resources.getString(R.string.local_error))
                // Hide loading
                showLoading(false)
                Toast.makeText(this, R.string.incorrect_password, Toast.LENGTH_SHORT)
                    .show()
            }
            else -> {
                Log.e(TAG, resources.getString(R.string.unknown_error))
                // Hide loading
                showLoading(false)
            }
        }
    }

    /**
     * It's called when the request to update user is successful
     */
    private fun handleIsUserUpdatedSuccess(isUserUpdated: Boolean) {
        if(isUserUpdated) {
            Log.v(TAG, resources.getString(R.string.user_update_request_successful))
            // Update global user entity
            CryptoTrackerApp.instance.user = user
            // Update global user preferences entity
            CryptoTrackerApp.instance.userPreferences = userPreferences
            // Login
            viewModel.login(user.id)
        } else {
            Log.v(TAG, resources.getString(R.string.user_update_request_failed))
            handleLoginFail(Fail.LocalFail)
        }
    }

    /**
     * It's called when the request to save user session is successful
     */
    private fun handleIsSessionSavedSuccess(isSessionSaved: Boolean) {
        if(isSessionSaved) {
            Log.v(TAG, resources.getString(R.string.user_session_request_successful))
            // Show Home view
            startActivity(Intent(this, HomeView::class.java))
        } else {
            Log.v(TAG, resources.getString(R.string.user_session_request_failed))
            handleLoginFail(Fail.LocalFail)
        }
    }

    /**
     * It's called when the requests to make login have failed
     */
    private fun handleLoginFail(fail: Fail) {
        when (fail) {
            is Fail.LocalFail -> {
                Log.e(TAG, resources.getString(R.string.local_error))
                // Hide loading
                showLoading(false)
                Toast.makeText(applicationContext, R.string.something_wrong, Toast.LENGTH_SHORT)
                    .show()
            }
            else -> {
                Log.e(TAG, resources.getString(R.string.unknown_error))
                // Hide loading
                showLoading(false)
            }
        }
    }

    /**
     * Update user information
     */
    private fun updateUser() {
        // Set new logged in date
        user.lastTimeLogged = DateUtil.dateNow()
        // update user information
        viewModel.updateUser(user)
    }

    /**
     * Validate data
     */
    private fun validate(): Boolean {
        return !TextUtils.isEmpty(email_input.text.toString()) && !TextUtils.isEmpty(password_input.text.toString())
    }

    /**
     * Show loading
     */
    private fun showLoading(show: Boolean) {
        login_progress_bar.visibility = if (show) View.VISIBLE else View.GONE
    }

}