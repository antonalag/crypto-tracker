package edu.uoc.tfm.antonalag.cryptotracker.ui.user

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import edu.uoc.tfm.antonalag.cryptotracker.CryptoTrackerApp
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.observe
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.User
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPassword
import edu.uoc.tfm.antonalag.cryptotracker.ui.login.LoginView
import edu.uoc.tfm.antonalag.cryptotracker.ui.userpreferences.UserPreferencesView
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.BaseActivity
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.MenuFragment
import kotlinx.android.synthetic.main.activity_register_view.*
import kotlinx.android.synthetic.main.activity_user_view.*
import kotlinx.android.synthetic.main.main_toolbar.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Class responsible to handle user information
 */
class UserView : BaseActivity(), DeleteUserAccountDialogFragment.DeleteUserAccountDialogListener {

    private val TAG = "UserView"

    private val viewModel by viewModel<UserViewModel>()
    private val menuOptionsDialog: MenuFragment = MenuFragment()
    private lateinit var globalUser: User
    private lateinit var userWithNewData: User
    private lateinit var userPassword: UserPassword
    private val deleteAccountDialog = DeleteUserAccountDialogFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSetToolbar(R.layout.main_toolbar, R.id.main_toolbar)
        initSetLayout(R.layout.activity_user_view)
        initUI()
        initViewModelObservers()
        initRequests()
    }

    /**
     * Initializes the properties of the UI elements
     */
    private fun initUI() {
        // Set toolbar title
        main_toolbar_title.text = resources.getString(R.string.app_name)
        // Set menu button listener
        menu_options_button.setOnClickListener {
            showMenuDialog()
        }
        // Get user data
        globalUser = CryptoTrackerApp.instance.user
        // Set data
        showViewModeFields()
        // Set user preferences button listener
        user_preferences_button.setOnClickListener {
            startActivity(Intent(this, UserPreferencesView::class.java))
        }
        // Set edit button preferences listener
        edit_user_button.setOnClickListener {
            showEditModeFields()
        }
        // Set cancel button listener
        cancel_button.setOnClickListener {
            showViewModeFields()
        }
        // Set accept button listener
        accept_button.setOnClickListener {
            val errorCode = validate()
            if(errorCode == 0) {
                // Check if user
                showView(user_progress_bar, true)
                // Check if the email exists in database
                if(globalUser.email != email_edit_text.text.toString()) {
                    viewModel.userExists(email_edit_text.text.toString())
                } else {
                    saveUser()
                }
            }
        }
        // Set log out button listener
        log_out_button.setOnClickListener {
            showView(user_progress_bar, true)
            viewModel.logout()
        }
        // Set delete account button listener
        delete_account_button.setOnClickListener {
            showDeleteAccountDialog()
        }

        initRequests()
    }

    /**
     * Configure the CryptocurrencyStatisticsViewModel's observers
     */
    private fun initViewModelObservers() {
        with(viewModel) {
            observe(userExists, ::handleUserExistsSuccess)
            fail(fail, ::handleUpdateUserFail)
        }

        with(viewModel) {
            observe(isUserUpdated, ::handleIsUserUpdateSuccess)
            fail(fail, ::handleUpdateUserFail)
        }

        with(viewModel) {
            observe(isPasswordUpdated, ::handleIsPasswordUpdateSuccess)
            fail(fail, ::handleUpdateUserFail)
        }

        with(viewModel) {
            observe(userPassword, ::handleUserPasswordSuccess)
            fail(fail, ::handleUpdateUserFail)
        }

        with(viewModel) {
            observe(isLoggedOut, ::handleLoggedOutSuccess)
            fail(fail, ::handleUpdateUserFail)
        }

        with(viewModel) {
            observe(isAllDeleted, ::handleIsAllDeletedSuccess)
            fail(fail, ::handleUpdateUserFail)
        }
    }

    /**
     * Initializes the necessary requests
     */
    private fun initRequests() {
        // get user password
        viewModel.getPassword(globalUser.id)
    }

    /**
     * Show fields in view mode
     */
    private fun showViewModeFields() {
        // Show view mode fields and buttons and set data
        name_text_view.text = globalUser.name
        name_text_view.visibility = View.VISIBLE
        surname_text_view.text = globalUser.surname
        surname_text_view.visibility = View.VISIBLE
        email_text_view.text = globalUser.email
        email_text_view.visibility = View.VISIBLE
        log_out_button.visibility = View.VISIBLE
        delete_account_button.visibility = View.VISIBLE
        // Hide edit mode fields and buttons
        name_edit_text.visibility = View.GONE
        surname_edit_text.visibility = View.GONE
        email_edit_text.visibility = View.GONE
        password_edit_text.visibility = View.GONE
        confirm_password_edit_text.visibility = View.GONE
        accept_button.visibility = View.GONE
        cancel_button.visibility = View.GONE
    }

    /**
     * Show fields in edit mode
     */
    private fun showEditModeFields() {
        // Show edit mode fields and buttons and set data
        name_edit_text.setText(globalUser.name)
        name_edit_text.visibility = View.VISIBLE
        surname_edit_text.setText(globalUser.surname)
        surname_edit_text.visibility = View.VISIBLE
        email_edit_text.setText(globalUser.email)
        email_edit_text.visibility = View.VISIBLE
        password_edit_text.visibility = View.VISIBLE
        confirm_password_edit_text.visibility = View.VISIBLE
        accept_button.visibility = View.VISIBLE
        cancel_button.visibility = View.VISIBLE
        // Hide view mode fields and buttons
        name_text_view.visibility = View.GONE
        surname_text_view.visibility = View.GONE
        email_text_view.visibility = View.GONE
        log_out_button.visibility = View.GONE
        delete_account_button.visibility = View.GONE
    }

    /**
     * Validate data
     */
    private fun validate(): Int {
        var error = 0
        val allFieldsFilled = !TextUtils.isEmpty(name_edit_text.text.toString())
                && !TextUtils.isEmpty(surname_edit_text.text.toString())
                && !TextUtils.isEmpty(email_edit_text.text.toString())
                && !TextUtils.isEmpty(password_edit_text.text.toString())
                && !TextUtils.isEmpty(confirm_password_edit_text.text.toString())
        if(!allFieldsFilled)
            return R.string.all_fields_are_mandatory
        val arePasswordsTheSame = password_edit_text.text.toString() == confirm_password_edit_text.text.toString()
        if(!arePasswordsTheSame)
            return R.string. different_passwords
        // Check if email has a valid format
        if (!Patterns.EMAIL_ADDRESS.matcher(email_edit_text.text.toString()).matches())
            return R.string.invalid_email_format
        return error
    }

    /**
     * It's called when the request for user password information is successful
     */
    private fun handleUserPasswordSuccess(userPassword: UserPassword) {
        showView(user_progress_bar, false)
        this.userPassword = userPassword
    }

    /**
     * It's called when the request if user exists is successful
     */
    private fun handleUserExistsSuccess(userExists: Boolean) {
        if(userExists) {
            // Hide loading
            showView(user_progress_bar, false)
            // Show message to the user
            Toast.makeText(applicationContext, R.string.user_already_exists, Toast.LENGTH_SHORT)
                .show()
        } else {
            // Update user
            saveUser()
        }
    }

    /**
     * It's called when the request to update user is successful
     */
    private fun handleIsUserUpdateSuccess(isUserUpdated: Boolean) {
        if(isUserUpdated) {
            // Update global entity user
            CryptoTrackerApp.instance.user = userWithNewData
            globalUser = userWithNewData
            // Update password
            savePassword()
        } else {
            Log.v(TAG, resources.getString(R.string.user_update_request_failed))
            handleUpdateUserFail(Fail.LocalFail)
        }
    }

    /**
     * It's called when the request to update user password is successful
     */
    private fun handleIsPasswordUpdateSuccess(isPasswordUpdated: Boolean) {
        if(isPasswordUpdated) {
            // Show data in view mode
            showViewModeFields()
            showView(user_progress_bar, false)
        } else {
            Log.v(TAG, resources.getString(R.string.user_password_request_failed))
            handleUpdateUserFail(Fail.LocalFail)
        }
    }

    /**
     * It's called when the request to logout is successful
     */
    private fun handleLoggedOutSuccess(isLoggedOut: Boolean) {
        if(isLoggedOut) {
            Log.v(TAG, resources.getString(R.string.logout_successful))
            showView(user_progress_bar, false)
            // Star Login view
            startActivity(Intent(this, LoginView::class.java))
        } else {
            Log.v(TAG, resources.getString(R.string.logout_failed))
            handleUpdateUserFail(Fail.LocalFail)
        }
    }

    /**
     * It's called when the request to delete all data related to an user is successful
     */
    private fun handleIsAllDeletedSuccess(isAllDeleted: Boolean) {
        if(isAllDeleted) {
            Log.v(TAG, resources.getString(R.string.account_delete_successful))
            showView(user_progress_bar, false)
            // Star Login view
            startActivity(Intent(this, LoginView::class.java))
        } else {
            Log.v(TAG, resources.getString(R.string.account_delete_failed))
            handleUpdateUserFail(Fail.LocalFail)
        }
    }

    /**
     * It's called when the request to update user has failed
     */
    private fun handleUpdateUserFail(fail: Fail) {
        when (fail) {
            is Fail.LocalFail -> {
                Log.e(TAG, resources.getString(R.string.local_error))
                // Hide loading
                showView(user_progress_bar, false)
                Toast.makeText(applicationContext, R.string.something_wrong, Toast.LENGTH_SHORT)
                    .show()
            }
            else -> {
                Log.e(TAG, resources.getString(R.string.unknown_error))
                // Hide loading
                showView(user_progress_bar, false)
                Toast.makeText(applicationContext, R.string.something_wrong, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    /**
     * Save user
     */
    private fun saveUser() {
        // Set the new data entered by the ser
        userWithNewData = User(
            globalUser.id,
            name_edit_text.text.toString(),
            surname_edit_text.text.toString(),
            email_edit_text.text.toString(),
            null,
            null
        )
        // Update user
        viewModel.updateUser(userWithNewData)
    }

    /**
     * Save user password
     */
    private fun savePassword() {
        // Check if the new password is the same that old password.
        if(userPassword.password == password_edit_text.text.toString()) {
            // Same password, not update
            showViewModeFields()
            showView(user_progress_bar, false)
        } else {
            // Set new password
            val userPassword = UserPassword(
                userPassword.id,
                password_edit_text.text.toString(),
                globalUser.id
            )
            // Update password
            viewModel.updatePassword(userPassword)
        }
    }

    /**
     * Show menu dialog
     */
    private fun showMenuDialog() {
        if(!menuOptionsDialog.isAdded) {
            menuOptionsDialog.show(supportFragmentManager, "MenuOptionsDialog")
        }
    }

    /**
     * Show delete account dialog
     */
    private fun showDeleteAccountDialog() {
        if(!deleteAccountDialog.isAdded) {
            deleteAccountDialog.show(supportFragmentManager, "DeleteAccountDialog")
        }
    }

    /**
     * Callback function that detect when the user clicks on DeleteAccountDialog confirm button
     */
    override fun onDialogConfirmClick(dialog: DialogFragment) {
        // Close dialog
        deleteAccountDialog.dismiss()
        // Show loading
        showView(user_progress_bar, true)
        // Delete account
        viewModel.deleteAll(globalUser.id)
    }

    /**
     * Callback function that detect when the user clicks on DeleteAccountDialog cancel button
     */
    override fun onDialogCancelClick(dialog: DialogFragment) {
        // Close dialog
        deleteAccountDialog.dismiss()
    }
}