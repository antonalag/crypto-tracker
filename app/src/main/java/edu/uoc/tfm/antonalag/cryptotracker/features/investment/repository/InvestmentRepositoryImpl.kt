package edu.uoc.tfm.antonalag.cryptotracker.features.investment.repository

import android.util.Log
import edu.uoc.tfm.antonalag.cryptotracker.core.db.ApplicationDatabase
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either
import edu.uoc.tfm.antonalag.cryptotracker.features.investment.datasource.InvestmentDao
import edu.uoc.tfm.antonalag.cryptotracker.features.investment.model.Investment

class InvestmentRepositoryImpl(
    private val investmentDao: InvestmentDao
): InvestmentRepository {

    private val TAG = "InvestmentRepositoryImpl"

    override suspend fun findByCryptocurrencyIds(cryptocurrencyIds: List<Long>): Either<Fail, List<Investment>> {
        return try {
            val response = investmentDao.findByCryptocurrencyIds(cryptocurrencyIds)
            Either.Right(response)
        } catch(exception: Throwable) {
            Log.e(TAG, exception.stackTraceToString())
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun findByCryptocurrencyId(cryptocurrencyId: Long): Either<Fail, Investment> {
        return try {
            val response = investmentDao.findByCryptocurrencyId(cryptocurrencyId)
            response?.let {it -> Either.Right(it) } ?: Either.Right(Investment(0L, 0.0, 0L))
        } catch(exception: Throwable) {
            Log.e(TAG, exception.stackTraceToString())
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun findById(id: Long): Either<Fail, Investment> {
        return try {
            val response = investmentDao.findById(id)
            response?.let {it -> Either.Right(it) } ?: Either.Left(Fail.NotFoundFail)
        } catch(exception: Throwable) {
            Log.e(TAG, exception.stackTraceToString())
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun save(investment: Investment): Either<Fail, Long> {
        return try {
            val response = investmentDao.save(investment)
            Either.Right(response)
        } catch(exception: Throwable) {
            Log.e(TAG, exception.stackTraceToString())
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun update(investment: Investment): Either<Fail, Int> {
        return try {
            val response = investmentDao.update(investment)
            Either.Right(response)
        } catch(exception: Throwable) {
            Log.e(TAG, exception.stackTraceToString())
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun delete(id: Long): Either<Fail, Int> {
        return try {
            val response = investmentDao.delete(id)
            Either.Right(response)
        } catch(exception: Throwable) {
            Log.e(TAG, exception.stackTraceToString())
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun deleteByCryptocurrencyIds(cryptocurrencyIds: List<Long>): Either<Fail, Int> {
        return try {
            val response = investmentDao.deleteByCryptocurrencyIds(cryptocurrencyIds)
            Either.Right(response)
        } catch(exception: Throwable) {
            Log.e(TAG, exception.stackTraceToString())
            Either.Left(Fail.LocalFail)
        }
    }

}