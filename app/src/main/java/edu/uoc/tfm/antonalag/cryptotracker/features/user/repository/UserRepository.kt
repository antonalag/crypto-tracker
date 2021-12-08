package edu.uoc.tfm.antonalag.cryptotracker.features.user.repository

import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.User
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPassword
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPreferences

interface UserRepository {

    /**
     * Check if exists a user session stored
     */
    suspend fun isUserAvailable(): Either<Fail, Boolean>

    /**
     * Get value from user session stored
     */
    suspend fun getSessionUserId(): Either<Fail, Long>

    /**
     * Store user session
     */
    suspend fun login(userId: Long): Either<Fail, Boolean>

    /**
     * Remove user session stored
     */
    suspend fun logout(): Either<Fail, Boolean>

    /**
     * Get user by id
     */
    suspend fun findUserById(id: Long): Either<Fail, User>

    /**
     * Get user by email
     */
    suspend fun findUserByEmail(email: String): Either<Fail, User>

    /**
     * Get user preferences by user user id
     */
    suspend fun findPreferencesByUserId(userId: Long): Either<Fail, UserPreferences>

    /**
     * Get user preferences by id
     */
    suspend fun findPreferencesById(id: Long): Either<Fail, UserPreferences>

    /**
     * Get user password by user id
     */
    suspend fun findPasswordByUserId(userId: Long): Either<Fail, UserPassword>

    /**
     * Get user password by id
     */
    suspend fun findPasswordById(id: Long): Either<Fail, UserPassword>

    /**
     * Check if user exists with specific email
     */
    suspend fun userExistsByEmail(email: String): Either<Fail, Boolean>

    /**
     * Save user
     */
    suspend fun saveUser(user: User): Either<Fail, Long>

    /**
     * Update user
     */
    suspend fun updateUser(user: User): Either<Fail, Int>

    /**
     * Save user preferences
     */
    suspend fun savePreferences(preferences: UserPreferences): Either<Fail, Long>

    /**
     * Update user preferences
     */
    suspend fun updatePreferences(preferences: UserPreferences): Either<Fail, Int>

    /**
     * Save user password
     */
    suspend fun savePassword(password: UserPassword): Either<Fail, Long>

    /**
     * Update user password
     */
    suspend fun updatePassword(password: UserPassword): Either<Fail, Int>

    /**
     * Delete user by id
     */
    suspend fun deleteUser(id: Long): Either<Fail, Int>

    /**
     * Delete user preferences by id
     */
    suspend fun deletePreferences(id: Long): Either<Fail, Int>

    /**
     * Delete user preferences by user id
     */
    suspend fun deletePreferencesByUserId(userId: Long): Either<Fail, Int>

    /**
     * Delete user passwrod by id
     */
    suspend fun deletePassword(id: Long): Either<Fail, Int>

    /**
     * Delete user password by user id
     */
    suspend fun deletePasswordByUserId(userId: Long): Either<Fail, Int>

}