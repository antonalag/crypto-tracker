package edu.uoc.tfm.antonalag.cryptotracker.core.di

import androidx.room.Room
import edu.uoc.tfm.antonalag.cryptotracker.core.db.ApplicationDatabase
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Network
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.converter.CryptocurrencyConverter
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.converter.CryptocurrencyConverterImpl
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.datasource.CryptocurrencyService
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.repository.CryptocurrencyRepository
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.repository.CryptocurrencyRepositoryImpl
import edu.uoc.tfm.antonalag.cryptotracker.features.fiat.datasource.FiatService
import edu.uoc.tfm.antonalag.cryptotracker.features.fiat.repository.FiatRepository
import edu.uoc.tfm.antonalag.cryptotracker.features.fiat.repository.FiatRepositoryImpl
import edu.uoc.tfm.antonalag.cryptotracker.features.investment.repository.InvestmentRepository
import edu.uoc.tfm.antonalag.cryptotracker.features.investment.repository.InvestmentRepositoryImpl
import edu.uoc.tfm.antonalag.cryptotracker.features.news.converter.NewsConverter
import edu.uoc.tfm.antonalag.cryptotracker.features.news.converter.NewsConverterImpl
import edu.uoc.tfm.antonalag.cryptotracker.features.news.datasource.NewsService
import edu.uoc.tfm.antonalag.cryptotracker.features.news.repository.NewsRepository
import edu.uoc.tfm.antonalag.cryptotracker.features.news.repository.NewsRepositoryImpl
import edu.uoc.tfm.antonalag.cryptotracker.features.quote.datasource.QuoteService
import edu.uoc.tfm.antonalag.cryptotracker.features.quote.repository.QuoteRepository
import edu.uoc.tfm.antonalag.cryptotracker.features.quote.repository.QuoteRepositoryImpl
import edu.uoc.tfm.antonalag.cryptotracker.features.user.datasource.SessionManager
import edu.uoc.tfm.antonalag.cryptotracker.features.user.repository.UserRepository
import edu.uoc.tfm.antonalag.cryptotracker.features.user.repository.UserRepositoryImpl
import io.ktor.client.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {

    // Http client
    single<HttpClient> {
        Network.createHttpClient(androidContext())
    }

    // Datasources
    single<SessionManager> { SessionManager(androidContext()) }
    single<CryptocurrencyService> { CryptocurrencyService(httpClient = get(), cryptocurrencyConverter = get())}
    single<FiatService> { FiatService(httpClient = get())}
    single<NewsService> { NewsService(httpClient = get(), newsConverter = get())}
    single<QuoteService> { QuoteService(httpClient = get()) }
    single<ApplicationDatabase> { Room.databaseBuilder(androidContext(), ApplicationDatabase::class.java, "crypto_tracker_db").build()}
    single { get<ApplicationDatabase>().cryptocurrencyDao()}
    single { get<ApplicationDatabase>().investmentDao()}
    single { get<ApplicationDatabase>().newsDao()}
    single { get<ApplicationDatabase>().userDao()}

    // Repositories
    single<CryptocurrencyRepository> {
        CryptocurrencyRepositoryImpl(
            cryptocurrencyService = get(),
            cryptocurrencyDao = get()
        )
    }

    single<FiatRepository> {
        FiatRepositoryImpl(
            fiatService = get()
        )
    }

    single<InvestmentRepository> {
        InvestmentRepositoryImpl(
            investmentDao = get()
        )
    }

    single<NewsRepository> {
        NewsRepositoryImpl(
            newsService = get(),
            newsDao = get()
        )
    }

    single<QuoteRepository> {
        QuoteRepositoryImpl(
            quoteService = get()
        )
    }

    single<UserRepository> {
        UserRepositoryImpl(
            sessionManager = get(),
            userDao = get()
        )
    }

    // Converters
    single<CryptocurrencyConverter> {
        CryptocurrencyConverterImpl()
    }

    single<NewsConverter> {
        NewsConverterImpl()
    }

}