package edu.uoc.tfm.antonalag.cryptotracker.features.user.repository

import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.User
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPassword
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPreferences

interface UserRepository {

    suspend fun isUserAvailable(): Either<Fail, Boolean>

    suspend fun getSessionUserId(): Either<Fail, Long>

    suspend fun login(userId: Long): Either<Fail, Boolean>

    suspend fun logout(): Either<Fail, Boolean>

    suspend fun findUserById(id: Long): Either<Fail, User>

    suspend fun findUserByEmail(email: String): Either<Fail, User>

    suspend fun findPreferencesByUserId(userId: Long): Either<Fail, UserPreferences>

    suspend fun findPreferencesById(id: Long): Either<Fail, UserPreferences>

    suspend fun findPasswordByUserId(userId: Long): Either<Fail, UserPassword>

    suspend fun findPasswordById(id: Long): Either<Fail, UserPassword>

    suspend fun userExistsByEmail(email: String): Either<Fail, Boolean>

    suspend fun saveUser(user: User): Either<Fail, Long>

    suspend fun updateUser(user: User): Either<Fail, Int>

    suspend fun savePreferences(preferences: UserPreferences): Either<Fail, Long>

    suspend fun updatePreferences(preferences: UserPreferences): Either<Fail, Int>

    suspend fun savePassword(password: UserPassword): Either<Fail, Long>

    suspend fun updatePassword(password: UserPassword): Either<Fail, Int>

    suspend fun deleteUser(id: Long): Either<Fail, Int>

    suspend fun deletePreferences(id: Long): Either<Fail, Int>

    suspend fun deletePreferencesByUserId(userId: Long): Either<Fail, Int>

    suspend fun deletePassword(id: Long): Either<Fail, Int>

    suspend fun deletePasswordByUserId(userId: Long): Either<Fail, Int>

}