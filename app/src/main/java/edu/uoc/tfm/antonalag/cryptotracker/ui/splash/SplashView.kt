package edu.uoc.tfm.antonalag.cryptotracker.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

    private fun initUI() {
        val animation = AnimationManager.animation(AnimationManager.AnimationType.GROW_AND_SHRINK, logo)
        animation.start()
    }

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

    private fun handleIsUserAvailableSuccess(isAvailable: Boolean) {
        if(!isAvailable) {
            startActivity(Intent(this, LoginView::class.java))
        } else {
            viewModel.getSessionUserId()
        }
    }

    private fun handleSessionUserIdSuccess(userId: Long) {
        viewModel.getUser(userId)
    }

    private fun handleUserSuccess(user: User) {
        this.user = user
        viewModel.getUserPreferences(user.id)
    }

    private fun handleUserPreferencesSuccess(userPreferences: UserPreferences){
        this.userPreferences = userPreferences
        // Update login date
        user.lastTimeLogged = DateUtil.dateNow()
        viewModel.updateUser(user)
    }

    private fun handleIsUserUpdateSuccess(isUserUpdated: Boolean) {
        if(isUserUpdated) {
            // Update global user entity
            CryptoTrackerApp.instance.user = user
            // Update global user preferences entity
            CryptoTrackerApp.instance.userPreferences = userPreferences
            // Start home view
            Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this, HomeView::class.java))
            }, 5000)
        } else {
            handleSplashFail(Fail.LocalFail)
        }
    }

    private fun handleSplashFail(fail: Fail) {
        Toast.makeText(applicationContext, R.string.something_wrong, Toast.LENGTH_SHORT)
            .show()
    }

}