package edu.uoc.tfm.antonalag.cryptotracker.core.di

import edu.uoc.tfm.antonalag.cryptotracker.ui.cryptocurrency.CryptocurrencyStatisticsViewModel
import edu.uoc.tfm.antonalag.cryptotracker.ui.exchange.ExchangeViewModel
import edu.uoc.tfm.antonalag.cryptotracker.ui.home.HomeViewModel
import edu.uoc.tfm.antonalag.cryptotracker.ui.login.LoginViewModel
import edu.uoc.tfm.antonalag.cryptotracker.ui.news.NewsDetailViewModel
import edu.uoc.tfm.antonalag.cryptotracker.ui.news.NewsViewModel
import edu.uoc.tfm.antonalag.cryptotracker.ui.news.SavedNewsViewModel
import edu.uoc.tfm.antonalag.cryptotracker.ui.register.RegisterViewModel
import edu.uoc.tfm.antonalag.cryptotracker.ui.splash.SplashViewModel
import edu.uoc.tfm.antonalag.cryptotracker.ui.user.UserViewModel
import edu.uoc.tfm.antonalag.cryptotracker.ui.userpreferences.UserPreferencesViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {

    // HomeViewModel
    viewModel {
        HomeViewModel(
            cryptocurrencyRepository = get(),
            investmentRepository = get(),
            quoteRepository = get()
        )
    }
    // CryptocurrencyStatisticsViewModel
    viewModel {
        CryptocurrencyStatisticsViewModel(
            cryptocurrencyRepository = get(),
            investmentRepository = get()
        )
    }
    // SplashViewModel
    viewModel {
        SplashViewModel(
            userRepository = get()
        )
    }
    // LoginViewModel
    viewModel {
        LoginViewModel(
            userRepository = get()
        )
    }
    // RegisterViewModel
    viewModel {
        RegisterViewModel(
            userRepository = get()
        )
    }
    // NewsViewModel
    viewModel {
        NewsViewModel(
            newsRepository = get(),
            cryptocurrencyRepository = get()
        )
    }
    // NewsDetailViewModel
    viewModel {
        NewsDetailViewModel(
            newsRepository = get()
        )
    }
    // SavedNewsViewModel
    viewModel {
        SavedNewsViewModel(
            newsRepository = get()
        )
    }
    // UserViewModel
    viewModel {
        UserViewModel(
            userRepository = get(),
            cryptocurrencyRepository = get(),
            newsRepository = get(),
            investmentRepository = get()
        )
    }
    // UserPreferencesViewModel
    viewModel {
        UserPreferencesViewModel(
            userRepository = get(),
            fiatRepository = get()
        )
    }
    // FiatConverterViewModel
    viewModel {
        ExchangeViewModel(
            cryptocurrencyRepository = get(),
            fiatRepository = get()
        )
    }
}