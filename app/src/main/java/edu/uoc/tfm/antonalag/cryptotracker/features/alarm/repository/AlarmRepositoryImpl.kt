package edu.uoc.tfm.antonalag.cryptotracker.features.alarm.repository

import android.content.res.Resources
import edu.uoc.tfm.antonalag.cryptotracker.core.db.ApplicationDatabase
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either.Left
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either.Right
import edu.uoc.tfm.antonalag.cryptotracker.features.alarm.datasource.AlarmDao
import edu.uoc.tfm.antonalag.cryptotracker.features.alarm.model.Alarm
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail.LocalFail
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail.NotFoundFail

class AlarmRepositoryImpl(
    private val alarmDao: AlarmDao
): AlarmRepository {

    override suspend fun findByCryptocurrencyIds(cryptocurrencyIds: List<Integer>): Either<Fail, List<Alarm>> {
        return try{
            val response = alarmDao.findByCryptocurrencyIds(cryptocurrencyIds)
            Right(response)
        }catch(exception: Throwable){
            Left(LocalFail)
        }
    }

    override suspend fun findByCryptocurrencyId(cryptoconcurrencyId: Integer): Either<Fail, List<Alarm>> {
        return try {
            val response = alarmDao.findByCryptocurrencyId(cryptoconcurrencyId)
            Right(response)
        } catch(exception: Throwable){
            Left(LocalFail)
        }
    }

    override suspend fun findById(id: Integer): Either<Fail, Alarm> {
        return try {
            val response = alarmDao.findById(id)
            response?.let {it ->  Right(it) } ?: Left(NotFoundFail)
        } catch(exception: Throwable){
            Left(LocalFail)
        }
    }

    override fun save(alarm: Alarm) {
        ApplicationDatabase.databaseWriteExecutor.execute {
            alarmDao.save(alarm)
        }
    }

    override fun delete(id: Integer) {
        ApplicationDatabase.databaseWriteExecutor.execute {
            alarmDao.delete(id)
        }
    }
}