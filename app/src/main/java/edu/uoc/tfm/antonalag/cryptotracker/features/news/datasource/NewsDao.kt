package edu.uoc.tfm.antonalag.cryptotracker.features.news.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.LocalNews

@Dao
interface NewsDao {

    @Query(value = "SELECT * FROM SavedNews WHERE userId = :userId ORDER BY Date")
    suspend fun findAllByUserId(userId: Long): List<LocalNews>

    @Query(value = "SELECT * FROM SavedNews WHERE id = :id")
    suspend fun findById(id: Long): LocalNews

    @Query(value = "SELECT EXISTS(SELECT id FROM SavedNews WHERE url = :url)")
    suspend fun newsExistsByUrl(url: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(localNews: LocalNews): Long

    @Query(value = "DELETE FROM SavedNews WHERE id =:id")
    suspend fun delete(id: Long): Int

    @Query(value = "DELETE FROM SavedNews WHERE userId = :userId")
    suspend fun deleteByUserId(userId: Long): Int

}