package edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.LocalCryptocurrency

@Dao
interface CryptocurrencyDao {

    @Query(value = "SELECT * FROM Cryptocurrency WHERE userId = :userId ORDER BY name")
    suspend fun findAllByUserId(userId: Long): List<LocalCryptocurrency>?

    @Query(value = "SELECT * FROM Cryptocurrency WHERE id = :id")
    suspend fun findById(id: Long): LocalCryptocurrency?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(cryptocurrencies: List<LocalCryptocurrency>): List<Long>

    @Query(value = "DELETE FROM Cryptocurrency WHERE id = :id")
    suspend fun delete(id: Long): Int

    @Query(value = "DELETE FROM Cryptocurrency WHERE userId = :userId")
    suspend fun deleteByUserId(userId: Long): Int

}