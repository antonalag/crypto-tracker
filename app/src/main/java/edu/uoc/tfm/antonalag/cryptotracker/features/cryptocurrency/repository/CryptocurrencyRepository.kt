package edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.repository

import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.*

interface CryptocurrencyRepository {

    suspend fun findAllByUserId(userId: Long): Either<Fail, List<LocalCryptocurrency>>

    suspend fun findAll(skip: Int? = 0, limit: Int? = 20): Either<Fail, List<Cryptocurrency>>

    suspend fun findAllCryptocurrencyListViewDto(currency: String, skip: Int? = 0, limit: Int? = 20): Either<Fail, List<CryptocurrencyListViewDto>>

    suspend fun findById(id: Long): Either<Fail,LocalCryptocurrency>

    suspend fun findByNameAndCurrencyCardViewDto(name: String, currency: String): Either<Fail,CryptocurrencyCardViewDto>

    suspend fun findByNameAndCurrency(name: String, currency: String): Either<Fail, CryptocurrencyResponse>

    suspend fun findChart(period: String, name: String): Either<Fail, List<CryptocurrencyChart>>

    suspend fun save(cryptocurrencies: List<LocalCryptocurrency>): Either<Fail, List<Long>>

    suspend fun delete(id: Long): Either<Fail, Int>

    suspend fun deleteByUserId(userId: Long): Either<Fail, Int>

}