package edu.uoc.tfm.antonalag.cryptotracker.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import edu.uoc.tfm.antonalag.cryptotracker.CryptoTrackerApp
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.observe
import edu.uoc.tfm.antonalag.cryptotracker.core.util.DateUtil
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.User
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPreferences
import edu.uoc.tfm.antonalag.cryptotracker.ui.home.HomeView
import edu.uoc.tfm.antonalag.cryptotracker.ui.login.LoginView
import edu.uoc.tfm.antonalag.cryptotracker.ui.util.AnimationManager
import kotlinx.android.synthetic.main.activity_splash_view.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Class that handles the initialization of the application by a user
 */
class SplashView : AppCompatActivity() {

    private val TAG = "SplashView"

    private val viewModel by viewModel<SplashViewModel>()
    private lateinit var user: User
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_view)
        initUI()
        initViewModelObservers()
        viewModel.isUserAvailable()
    }

    /**
     * Initializes the properties of the UI elements
     */
    private fun initUI() {
        // set animation
        val animation = AnimationManager.animation(AnimationManager.AnimationType.GROW_AND_SHRINK, logo)
        animation.start()
    }

    /**
     * Configure the SplashViewModel's observers
     */
    private fun initViewModelObservers() {
        with(viewModel) {
            observe(isUserAvailable, ::handleIsUserAvailableSuccess)
            fail(fail, :: handleSplashFail)
        }

        with(viewModel) {
            observe(sesssionUserId, ::handleSessionUserIdSuccess)
            fail(fail, ::handleSplashFail)
        }

        with(viewModel) {
            observe(user, ::handleUserSuccess)
            fail(fail, ::handleSplashFail)
        }

        with(viewModel) {
            observe(userPreferences, ::handleUserPreferencesSuccess)
            fail(fail, ::handleSplashFail)
        }

        with(viewModel) {
            observe(isUserUpdated, ::handleIsUserUpdateSuccess)
            fail(fail, ::handleSplashFail)
        }
    }

    /**
     * It's called when the request to check if an user is available is successful
     */
    private fun handleIsUserAvailableSuccess(isAvailable: Boolean) {
        if(!isAvailable) {
            // If there is not user session stored, LoginView is launched
                Log.v(TAG, resources.getString(R.string.user_session_not_stored))
            startActivity(Intent(this, LoginView::class.java))
        } else {
            // If user session stored exists, get user id
            viewModel.getSessionUserId()
        }
    }

    /**
     * It's called when the request to get user id session stored is successful
     */
    private fun handleSessionUserIdSuccess(userId: Long) {
        // Get user
        viewModel.getUser(userId)
    }

    /**
     * It's called when the request to get user is successful
     */
    private fun handleUserSuccess(user: User) {
        // Ser user
        this.user = user
        // Get user preferences
        viewModel.getUserPreferences(user.id)
    }

    /**
     * It's called when the request to get user preferences is successful
     */
    private fun handleUserPreferencesSuccess(userPreferences: UserPreferences){
        // Set user preferences
        this.userPreferences = userPreferences
        // Update login date
        user.lastTimeLogged = DateUtil.dateNow()
        viewModel.updateUser(user)
    }

    /**
     * It's called when the request to update user is successful
     */
    private fun handleIsUserUpdateSuccess(isUserUpdated: Boolean) {
        if(isUserUpdated) {
            Log.v(TAG, resources.getString(R.string.user_update_request_successful))
            // Update global user entity
            CryptoTrackerApp.instance.user = user
            // Update global user preferences entity
            CryptoTrackerApp.instance.userPreferences = userPreferences
            // Start home view
            Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this, HomeView::class.java))
            }, 5000)
        } else {
            Log.v(TAG, resources.getString(R.string.user_update_request_failed))
            handleSplashFail(Fail.LocalFail)
        }
    }

    /**
     * It's called when the request to update user has failed
     */
    private fun handleSplashFail(fail: Fail) {
        Toast.makeText(applicationContext, R.string.something_wrong, Toast.LENGTH_SHORT)
            .show()
    }

}