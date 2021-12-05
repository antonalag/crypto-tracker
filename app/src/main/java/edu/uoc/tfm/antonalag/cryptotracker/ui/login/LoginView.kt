package edu.uoc.tfm.antonalag.cryptotracker.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
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

    private fun handleUserSuccess(user: User) {
        this.user  = user
        // User exists on database, check if user and password is correct
        viewModel.getPassword(user.id)
    }

    private fun handleUserFail(fail: Fail) {
        when (fail) {
            is Fail.LocalFail -> {
                // Hide loading
                showLoading(false)
                Toast.makeText(this, R.string.incorrect_user, Toast.LENGTH_SHORT)
                    .show()
            }
            is Fail.NotFoundFail -> {
                showLoading(false)
                Toast.makeText(applicationContext, R.string.user_not_found, Toast.LENGTH_SHORT)
                    .show()
            }
            else -> {
                // Nothing to do
            }
        }
    }

    private fun handleUserPreferencesSuccess(userPreferences: UserPreferences) {
        this.userPreferences = userPreferences
        updateUser()
    }

    private fun handleUserPreferencesFail(fail: Fail) {
        when (fail) {
            is Fail.LocalFail -> {
                // Hide loading
                showLoading(false)
                Toast.makeText(applicationContext, R.string.something_wrong, Toast.LENGTH_SHORT)
                    .show()
            }
            else -> {
                // Nothing to do
            }
        }
    }

    private fun handlePasswordSuccess(userPassword: UserPassword) {
        val password = userPassword.password
        if(password == password_input.text.toString()) {
            viewModel.getUserPreferences(userPassword.userId)
        } else {
            handlePasswordFail(Fail.LocalFail)
        }
    }

    private fun handlePasswordFail(fail: Fail) {
        when (fail) {
            is Fail.LocalFail -> {
                // Hide loading
                showLoading(false)
                Toast.makeText(this, R.string.incorrect_password, Toast.LENGTH_SHORT)
                    .show()
            }
            else -> {
                // Nothing to do
            }
        }
    }

    private fun handleIsUserUpdatedSuccess(isUserUpdated: Boolean) {
        if(isUserUpdated) {
            // Update global user entity
            CryptoTrackerApp.instance.user = user
            // Update global user preferences entity
            CryptoTrackerApp.instance.userPreferences = userPreferences
            // Login
            viewModel.login(user.id)
        } else {
            handleLoginFail(Fail.LocalFail)
        }
    }

    private fun handleIsSessionSavedSuccess(isSessionSaved: Boolean) {
        if(isSessionSaved) {
            // Show Home view
            startActivity(Intent(this, HomeView::class.java))
        } else {
            handleLoginFail(Fail.LocalFail)
        }
    }

    private fun handleLoginFail(fail: Fail) {
        when (fail) {
            is Fail.LocalFail -> {
                // Hide loading
                showLoading(false)
                Toast.makeText(applicationContext, R.string.something_wrong, Toast.LENGTH_SHORT)
                    .show()
            }
            else -> {
                // Nothing to do
            }
        }
    }

    private fun updateUser() {
        user.lastTimeLogged = DateUtil.dateNow()
        viewModel.updateUser(user)
    }

    private fun validate(): Boolean {
        return !TextUtils.isEmpty(email_input.text.toString()) && !TextUtils.isEmpty(password_input.text.toString())
    }

    private fun showLoading(show: Boolean) {
        login_progress_bar.visibility = if (show) View.VISIBLE else View.GONE
    }

}