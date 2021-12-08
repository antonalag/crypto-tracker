package edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.repository

import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.*

interface CryptocurrencyRepository {

    /**
     * Get all local cryptocurrencies by user id
     */
    suspend fun findAllByUserId(userId: Long): Either<Fail, List<LocalCryptocurrency>>

    /**
     * Get a cryptocurrencies paginated list
     */
    suspend fun findAll(skip: Int? = 0, limit: Int? = 20): Either<Fail, List<Cryptocurrency>>

    /**
     * Get cryptocurrencies paginated list and convert to CryptocurrencyListViewDto entities
     */
    suspend fun findAllCryptocurrencyListViewDto(currency: String, skip: Int? = 0, limit: Int? = 20): Either<Fail, List<CryptocurrencyListViewDto>>

    /**
     * Get local cryptocurrency by id
     */
    suspend fun findById(id: Long): Either<Fail,LocalCryptocurrency>

    /**
     * Get cryptocurrency by name and currency and convert to CryptocurrencyCardViewDto
     */
    suspend fun findByNameAndCurrencyCardViewDto(name: String, currency: String): Either<Fail,CryptocurrencyCardViewDto>

    /**
     * Get cryptocurrency by name and currency
     */
    suspend fun findByNameAndCurrency(name: String, currency: String): Either<Fail, CryptocurrencyResponse>

    /**
     * Get cryptocurrency chart by period and name
     */
    suspend fun findChart(period: String, name: String): Either<Fail, List<CryptocurrencyChart>>

    /**
     * Save a local cryptocurrency list
     */
    suspend fun save(cryptocurrencies: List<LocalCryptocurrency>): Either<Fail, List<Long>>

    /**
     * Delete a local cryptocurrency by id
     */
    suspend fun delete(id: Long): Either<Fail, Int>

    /**
     * Delete a local cryptocurrency by user id
     */
    suspend fun deleteByUserId(userId: Long): Either<Fail, Int>

}