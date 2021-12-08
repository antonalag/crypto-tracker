package edu.uoc.tfm.antonalag.cryptotracker.features.investment.repository

import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either
import edu.uoc.tfm.antonalag.cryptotracker.features.investment.model.Investment

interface InvestmentRepository {

    /**
     * Get all investments by cryptocurrency ids
     */
    suspend fun findByCryptocurrencyIds(cryptocurrencyIds: List<Long>): Either<Fail, List<Investment>>

    /**
     * Get investment by cryptocurrency id
     */
    suspend fun findByCryptocurrencyId(cryptocurrencyId: Long): Either<Fail, Investment>

    /**
     * Get investment by id
     */
    suspend fun findById(id: Long): Either<Fail, Investment>

    /**
     * Save an investment
     */
    suspend fun save(investment: Investment): Either<Fail, Long>

    /**
     * Update an investment
     */
    suspend fun update(investment: Investment): Either<Fail, Int>

    /**
     * Delete an investment by id
     */
    suspend fun delete(id: Long): Either<Fail, Int>

    /**
     * Delete investments by cryptocurrency ids
     */
    suspend fun deleteByCryptocurrencyIds(cryptocurrencyIds: List<Long>): Either<Fail, Int>

}