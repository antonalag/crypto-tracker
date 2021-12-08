package edu.uoc.tfm.antonalag.cryptotracker.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.uoc.tfm.antonalag.cryptotracker.core.converter.BaseConverters
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.datasource.CryptocurrencyDao
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.LocalCryptocurrency
import edu.uoc.tfm.antonalag.cryptotracker.features.investment.datasource.InvestmentDao
import edu.uoc.tfm.antonalag.cryptotracker.features.investment.model.Investment
import edu.uoc.tfm.antonalag.cryptotracker.features.news.datasource.NewsDao
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.LocalNews
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.User
import edu.uoc.tfm.antonalag.cryptotracker.features.user.datasource.UserDao
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPassword
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPreferences
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(entities = [
    User::class,
    LocalCryptocurrency::class,
    LocalNews::class,
    UserPreferences::class,
    UserPassword::class,
    Investment::class],
    version = 2)
@TypeConverters(BaseConverters::class)
abstract class ApplicationDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun cryptocurrencyDao(): CryptocurrencyDao
    abstract fun newsDao(): NewsDao
    abstract fun investmentDao(): InvestmentDao

    companion object {
        val databaseWriteExecutor: ExecutorService = Executors.newFixedThreadPool(4)
    }
}