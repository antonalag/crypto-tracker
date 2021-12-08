package edu.uoc.tfm.antonalag.cryptotracker.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import edu.uoc.tfm.antonalag.cryptotracker.CryptoTrackerApp
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.observe
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.User
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPassword
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPreferences
import edu.uoc.tfm.antonalag.cryptotracker.ui.login.LoginView
import kotlinx.android.synthetic.main.activity_register_view.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Class responsible of handling the information necessary for a user to register
 */
class RegisterView : AppCompatActivity() {

    private val TAG = "RegisterView"

    private val viewModel by viewModel<RegisterViewModel>()
    private lateinit var createdUser: User
    private lateinit var createdPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_view)
        initUI()
        initViewModelObservers()
    }

    /**
     * Initializes the properties of the UI elements
     */
    private fun initUI() {
        // Hide loading
        showLoading(false)
        // Set on click sign in button
        sign_in_button.setOnClickListener {
            startActivity(Intent(this, LoginView::class.java))
        }
        // Set on click sign up button
        sign_up_button.setOnClickListener {
            val errorCode = validate()
            if (errorCode == 0) {
                // Show loading
                showLoading(true)
                // Check if exists an user with the same email
                viewModel.userExists(email_input.text.toString())
            } else {
                // Show error
                Toast.makeText(
                    applicationContext,
                    errorCode,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Configure the RegisterViewModel's observers
     */
    private fun initViewModelObservers() {
        with(viewModel) {
            observe(userExists, ::handleUserExistsSuccess)
            fail(fail, ::handleRegisterFail)
        }

        with(viewModel) {
            observe(user, ::handleUserSuccess)
            fail(fail, ::handleRegisterFail)
        }

        with(viewModel) {
            observe(userPassword, ::handleUserPasswordSuccess)
            fail(fail, ::handleRegisterFail)
        }

        with(viewModel) {
            observe(userPreferences, ::handleUserPreferencesSuccess)
            fail(fail, ::handleRegisterFail)
        }

    }

    /**
     * It's called when the request to check if user exists is successful
     */
    private fun handleUserExistsSuccess(userExists: Boolean) {
        if (userExists) {
            Log.v(TAG, resources.getString(R.string.user_already_exists))
            // Hide loading
            showLoading(false)
            // Show message
            Toast.makeText(applicationContext, R.string.user_already_exists, Toast.LENGTH_SHORT)
                .show()
        } else {
            // Create user
            val user = User(
                name_input.text.toString(),
                surname_input.text.toString(),
                email_input.text.toString()
            )
            viewModel.createUser(user)
        }
    }

    /**
     * It's called when the request to save a user is successful
     */
    private fun handleUserSuccess(user: User) {
        // When the user is saved, keep on the global variable
        createdUser = user
        // Create password
        val password = UserPassword(password_input.text.toString(), user.id)
        viewModel.createUserPassword(password)
    }

    /**
     * It's called when the request to save a user password is successful
     */
    private fun handleUserPasswordSuccess(password: UserPassword) {
        // create user peferences with default values
        val preferences = UserPreferences("EUR", "€", "1m", "6h", password.userId)
        viewModel.createUserPreferences(preferences)
    }

    /**
     * It's called when the request to save a user preferences is successful
     */
    private fun handleUserPreferencesSuccess(preferences: UserPreferences) {
        // When user preferences is saved, keep oon the global variable
        createdPreferences = preferences
        // Keep variables in CryptoCurrencyApp instance
        CryptoTrackerApp.instance.user = createdUser
        CryptoTrackerApp.instance.userPreferences = createdPreferences
        Log.v(TAG, resources.getString(R.string.user_register_succesful))
        // Throw login activity
        startActivity(Intent(this, LoginView::class.java))
    }

    /**
     * It's called when any requests to register user fails
     */
    private fun handleRegisterFail(fail: Fail) {
        Log.v(TAG, resources.getString(R.string.user_register_failed))
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
                Toast.makeText(applicationContext, R.string.something_wrong, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    /**
     * Validates the registration data
     */
    private fun validate(): Int {
        var error = 0
        // Check if all fields are filled
        val allFieldsFilled = !TextUtils.isEmpty(name_input.text.toString())
                && !TextUtils.isEmpty(surname_input.text.toString())
                && !TextUtils.isEmpty(email_input.text.toString())
                && !TextUtils.isEmpty(password_input.text.toString())
                && !TextUtils.isEmpty(repeat_password_input.text.toString())
        if (!allFieldsFilled)
            return R.string.all_fields_are_mandatory

        // Check if password and confirm password are the same
        val arePasswordsTheSame =
            password_input.text.toString() == repeat_password_input.text.toString()
        if (!arePasswordsTheSame)
            return R.string.different_passwords

        // Check if email has a valid format
        if (!Patterns.EMAIL_ADDRESS.matcher(email_input.text.toString()).matches())
            return R.string.invalid_email_format

        return error
    }

    /**
     * Show loading
     */
    private fun showLoading(show: Boolean) {
        register_progress_bar.visibility = if (show) View.VISIBLE else View.GONE
    }
}