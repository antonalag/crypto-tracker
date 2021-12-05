package edu.uoc.tfm.antonalag.cryptotracker.core.platform

import android.app.Application
import edu.uoc.tfm.antonalag.cryptotracker.core.di.dataModule
import edu.uoc.tfm.antonalag.cryptotracker.core.di.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class CryptoTrackerApp: Application() {

    override fun onCreate() {
        super.onCreate()
        // Init dependency injection
        startKoin {
            androidLogger()
            androidContext(this@CryptoTrackerApp)
            modules(dataModule, uiModule)
        }
    }

}