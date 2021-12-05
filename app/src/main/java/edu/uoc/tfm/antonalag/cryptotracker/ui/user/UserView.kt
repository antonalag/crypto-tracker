package edu.uoc.tfm.antonalag.cryptotracker.ui.user

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
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
import kotlinx.android.synthetic.main.activity_user_view.*
import kotlinx.android.synthetic.main.main_toolbar.*
import org.koin.android.viewmodel.ext.android.viewModel

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
                showLoading(true)
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
            showLoading(true)
            viewModel.logout()
        }
        // Set delete account button listener
        delete_account_button.setOnClickListener {
            showDeleteAccountDialog()
        }

        initRequests()
    }

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

    private fun initRequests() {
        // get user password
        viewModel.getPassword(globalUser.id)
    }

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
        return error
    }

    private fun handleUserPasswordSuccess(userPassword: UserPassword) {
        showLoading(false)
        this.userPassword = userPassword
    }

    private fun handleUserExistsSuccess(userExists: Boolean) {
        if(userExists) {
            // Hide loading
            showLoading(false)
            // Show message to the user
            Toast.makeText(applicationContext, R.string.user_already_exists, Toast.LENGTH_SHORT)
                .show()
        } else {
            saveUser()
        }
    }

    private fun handleIsUserUpdateSuccess(isUserUpdated: Boolean) {
        if(isUserUpdated) {
            // Update global entity user
            CryptoTrackerApp.instance.user = userWithNewData
            globalUser = userWithNewData
            // Update password
            savePassword()
        } else {
            Log.v(TAG, "User hasn't been updated correctly")
            handleUpdateUserFail(Fail.LocalFail)
        }
    }

    private fun handleIsPasswordUpdateSuccess(isPasswordUpdated: Boolean) {
        if(isPasswordUpdated) {
            showViewModeFields()
            showLoading(false)
        } else {
            Log.v(TAG, "User password hasn't been updated correctly")
            handleUpdateUserFail(Fail.LocalFail)
        }
    }

    private fun handleLoggedOutSuccess(isLoggedOut: Boolean) {
        if(isLoggedOut) {
            Log.v(TAG, "User logged our correctly")
            showLoading(false)
            startActivity(Intent(this, LoginView::class.java))
        } else {
            Log.v(TAG, "User hasn't been logged out correctly")
            handleUpdateUserFail(Fail.LocalFail)
        }
    }

    private fun handleIsAllDeletedSuccess(isAllDeleted: Boolean) {
        if(isAllDeleted) {
            Log.v(TAG, "Account deleted correctly")
            showLoading(false)
            startActivity(Intent(this, LoginView::class.java))
        } else {
            Log.v(TAG, "Cannot delete account correctly")
            handleUpdateUserFail(Fail.LocalFail)
        }
    }

    private fun handleUpdateUserFail(fail: Fail) {
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

    private fun saveUser() {
        userWithNewData = User(
            globalUser.id,
            name_edit_text.text.toString(),
            surname_edit_text.text.toString(),
            email_edit_text.text.toString(),
            null,
            null
        )
        viewModel.updateUser(userWithNewData)
    }

    private fun savePassword() {
        if(userPassword.password == password_edit_text.text.toString()) {
            Log.v(TAG, "Same password, not change...")
            showViewModeFields()
            showLoading(false)
        } else {
            val userPassword = UserPassword(
                userPassword.id,
                password_edit_text.text.toString(),
                globalUser.id
            )
            viewModel.updatePassword(userPassword)
        }
    }

    private fun showMenuDialog() {
        if(!menuOptionsDialog.isAdded) {
            menuOptionsDialog.show(supportFragmentManager, "MenuOptionsDialog")
        }
    }

    private fun showLoading(show: Boolean) {
        user_progress_bar.visibility = if(show) View.VISIBLE else View.GONE
    }

    private fun showDeleteAccountDialog() {
        if(!deleteAccountDialog.isAdded) {
            deleteAccountDialog.show(supportFragmentManager, "DeleteAccountDialog")
        }
    }

    override fun onDialogConfirmClick(dialog: DialogFragment) {
        deleteAccountDialog.dismiss()
        showLoading(true)
        viewModel.deleteAll(globalUser.id)
    }

    override fun onDialogCancelClick(dialog: DialogFragment) {
        deleteAccountDialog.dismiss()
    }
}