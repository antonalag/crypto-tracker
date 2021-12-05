package edu.uoc.tfm.antonalag.cryptotracker.features.investment.datasource

import androidx.room.*
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either
import edu.uoc.tfm.antonalag.cryptotracker.features.alarm.model.Alarm
import edu.uoc.tfm.antonalag.cryptotracker.features.investment.model.Investment

@Dao
interface InvestmentDao {

    @Query(value = "SELECT * FROM Investment WHERE cryptocurrencyId IN (:cryptocurrencyIds)")
    suspend fun findByCryptocurrencyIds(cryptocurrencyIds: List<Long>): List<Investment>

    @Query(value = "SELECT * FROM Investment WHERE cryptocurrencyId = :cryptoconcurrencyId")
    suspend fun findByCryptocurrencyId(cryptoconcurrencyId: Long): Investment?

    @Query(value = "SELECT * FROM Investment WHERE id = :id")
    suspend fun findById(id: Long): Investment?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(investment: Investment): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(investment: Investment): Int

    @Query(value = "DELETE FROM Investment WHERE id = :id")
    suspend fun delete(id: Long): Int

    @Query(value = "DELETE FROM Investment WHERE cryptocurrencyId IN (:cryptocurrencyIds)")
    suspend fun deleteByCryptocurrencyIds(cryptocurrencyIds: List<Long>): Int

}