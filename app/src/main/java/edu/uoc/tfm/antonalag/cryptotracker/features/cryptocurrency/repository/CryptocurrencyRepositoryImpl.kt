package edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.repository

import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either.Left
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either.Right
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.datasource.CryptocurrencyDao
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.datasource.CryptocurrencyService
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.*

class CryptocurrencyRepositoryImpl(
    private val cryptocurrencyService: CryptocurrencyService,
    private val cryptocurrencyDao: CryptocurrencyDao
) : CryptocurrencyRepository {

    override suspend fun findAllByUserId(
        userId: Long
    ): Either<Fail, List<LocalCryptocurrency>> {
        return try {
            val response = cryptocurrencyDao.findAllByUserId(userId)
            response?.let { it -> Right(it) } ?: Left(Fail.NotFoundFail)
        } catch (exception: Throwable) {
            Left(Fail.LocalFail)
        }

    }

    override suspend fun findAll(
        skip: Int?,
        limit: Int?
    ): Either<Fail, List<Cryptocurrency>> {
        return cryptocurrencyService.getCurrencies(skip, limit)
    }

    override suspend fun findAllCryptocurrencyListViewDto(
        currency: String,
        skip: Int?,
        limit: Int?
    ): Either<Fail, List<CryptocurrencyListViewDto>> {
        return cryptocurrencyService.getCurrenciesListViewDto(currency,skip, limit)
    }

    override suspend fun findById(
        id: Long
    ): Either<Fail, LocalCryptocurrency> {
        return try {
            val response = cryptocurrencyDao.findById(id)
            response?.let { it -> Right(it) } ?: Left(Fail.NotFoundFail)
        } catch (exception: Throwable) {
            Left(Fail.LocalFail)
        }
    }

    override suspend fun findByNameAndCurrencyCardViewDto(
        name: String,
        currency: String
    ): Either<Fail, CryptocurrencyCardViewDto> {
        return cryptocurrencyService.getCurrencyCardViewDto(name, currency)
    }

    override suspend fun findByNameAndCurrency(
        name: String,
        currency: String
    ): Either<Fail, CryptocurrencyResponse> {
        return cryptocurrencyService.getCurrencyRaw(name, currency)
    }

    override suspend fun findChart(
        period: String,
        name: String
    ): Either<Fail, List<CryptocurrencyChart>> {
        return cryptocurrencyService.getCurrencyChart(period, name)
    }

    override suspend fun save(
        cryptocurrencies: List<LocalCryptocurrency>
    ): Either<Fail, List<Long>> {
        return try {
            val response = cryptocurrencyDao.save(cryptocurrencies)
            Right(response)
        } catch (exception: Throwable) {
            Left(Fail.LocalFail)
        }
    }

    override suspend fun delete(
        id: Long
    ): Either<Fail, Int> {
        return try {
            val response = cryptocurrencyDao.delete(id)
            Right(response)
        } catch (exception: Throwable) {
            Left(Fail.LocalFail)
        }
    }

    override suspend fun deleteByUserId(userId: Long): Either<Fail, Int> {
        return try {
            val response = cryptocurrencyDao.deleteByUserId(userId)
            Right(response)
        } catch (exception: Throwable) {
            Left(Fail.LocalFail)
        }
    }

}