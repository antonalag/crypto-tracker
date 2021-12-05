package edu.uoc.tfm.antonalag.cryptotracker.features.alarm.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.uoc.tfm.antonalag.cryptotracker.features.alarm.model.Alarm

@Dao
interface AlarmDao {

    @Query(value = "SELECT * FROM Alarm WHERE cryptoconcurrencyId IN (:cryptocurrencyIds) ORDER BY date")
    suspend fun findByCryptocurrencyIds(cryptocurrencyIds: List<Integer>): List<Alarm>

    @Query(value = "SELECT * FROM Alarm WHERE cryptoconcurrencyId = :cryptcurrencyId ORDER BY date")
    suspend fun findByCryptocurrencyId(cryptcurrencyId: Integer): List<Alarm>

    @Query(value = "SELECT * FROM Alarm WHERE id = :id")
    suspend fun findById(id: Integer): Alarm?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(alarm: Alarm): Long

    @Query(value = "DELETE FROM Alarm WHERE id = :id")
    fun delete(id: Integer)

}
