package edu.uoc.tfm.antonalag.cryptotracker.features.alarm.repository

import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either
import edu.uoc.tfm.antonalag.cryptotracker.features.alarm.model.Alarm

interface AlarmRepository {

    suspend fun findByCryptocurrencyIds(cryptocurrencyIds: List<Integer>): Either<Fail, List<Alarm>>

    suspend fun findByCryptocurrencyId(cryptocurrencyId: Integer): Either<Fail, List<Alarm>>

    suspend fun findById(id: Integer): Either<Fail, Alarm>

    fun save(alarm: Alarm)

    fun delete(id: Integer)

}