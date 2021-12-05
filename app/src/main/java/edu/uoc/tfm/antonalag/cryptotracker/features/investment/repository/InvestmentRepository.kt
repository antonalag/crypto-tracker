package edu.uoc.tfm.antonalag.cryptotracker.features.investment.repository

import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either
import edu.uoc.tfm.antonalag.cryptotracker.features.alarm.model.Alarm
import edu.uoc.tfm.antonalag.cryptotracker.features.investment.model.Investment

interface InvestmentRepository {

    suspend fun findByCryptocurrencyIds(cryptocurrencyIds: List<Long>): Either<Fail, List<Investment>>

    suspend fun findByCryptocurrencyId(cryptocurrencyId: Long): Either<Fail, Investment>

    suspend fun findById(id: Long): Either<Fail, Investment>

    suspend fun save(investment: Investment): Either<Fail, Long>

    suspend fun update(investment: Investment): Either<Fail, Int>

    suspend fun delete(id: Long): Either<Fail, Int>

    suspend fun deleteByCryptocurrencyIds(cryptocurrencyIds: List<Long>): Either<Fail, Int>

}