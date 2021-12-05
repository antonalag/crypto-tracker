package edu.uoc.tfm.antonalag.cryptotracker

import android.app.Application
import edu.uoc.tfm.antonalag.cryptotracker.core.di.dataModule
import edu.uoc.tfm.antonalag.cryptotracker.core.di.uiModule
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.User
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class CryptoTrackerApp: Application() {

    lateinit var user: User
    lateinit var userPreferences: UserPreferences

    override fun onCreate() {
        super.onCreate()
        // Init Dependency Injection
        startKoin {
            androidLogger()
            androidContext(this@CryptoTrackerApp)
            modules(uiModule, dataModule)
        }
        instance = this@CryptoTrackerApp
    }

    // Using companion object to return an instance oc CryptoTrackerApp class like static in java
    companion object {
        @JvmStatic
        lateinit var instance: CryptoTrackerApp
            private set
    }




}