package edu.uoc.tfm.antonalag.cryptotracker.features.user.datasource

import androidx.room.*
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.User
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPassword
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPreferences

@Dao
interface UserDao {

    @Query(value = "SELECT * FROM User WHERE id = :id")
    suspend fun findUserById(id: Long): User?

    @Query(value = "SELECT * FROM User WHERE email = :email")
    suspend fun findUserByEmail(email: String): User?

    @Query(value = "SELECT * FROM UserPreferences WHERE userId = :userId")
    suspend fun findPreferencesByUserId(userId: Long): UserPreferences?

    @Query(value = "SELECT * FROM UserPreferences WHERE id = :id")
    suspend fun findPreferencesById(id: Long): UserPreferences?

    @Query(value = "SELECT * FROM UserPassword WHERE userId = :userId")
    suspend fun findPasswordByUserId(userId: Long): UserPassword?

    @Query(value = "SELECT * FROM UserPassword WHERE id = :id")
    suspend fun findPasswordById(id: Long): UserPassword?

    @Query(value = "SELECT EXISTS(SELECT id FROM User WHERE email = :email )")
    suspend fun userExistsByEmail(email: String): Boolean

    @Insert
    suspend fun saveUser(user: User): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUser(user: User): Int

    @Insert
    suspend fun savePreferences(preferences: UserPreferences): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePreferences(preferences: UserPreferences): Int

    @Insert
    suspend fun savePassword(password: UserPassword): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePassword(password: UserPassword): Int

    @Query(value = "DELETE FROM User WHERE id = :id")
    suspend fun deleteUser(id: Long): Int

    @Query(value = "DELETE FROM UserPreferences WHERE id = :id")
    suspend fun deletePreferences(id: Long): Int

    @Query(value = "DELETE FROM UserPreferences WHERE userId = :userId")
    suspend fun deletePreferencesByUserId(userId: Long): Int

    @Query(value = "DELETE FROM UserPassword WHERE id = :id")
    suspend fun deletePassword(id: Long): Int

    @Query(value = "DELETE FROM UserPassword WHERE userId = :userId")
    suspend fun deletePasswordByUserId(userId: Long): Int

}