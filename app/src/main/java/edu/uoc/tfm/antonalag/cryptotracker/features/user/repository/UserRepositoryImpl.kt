package edu.uoc.tfm.antonalag.cryptotracker.features.user.repository

import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either
import edu.uoc.tfm.antonalag.cryptotracker.features.user.datasource.SessionManager
import edu.uoc.tfm.antonalag.cryptotracker.features.user.datasource.UserDao
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.User
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPassword
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.UserPreferences

class UserRepositoryImpl(
    private val sessionManager: SessionManager,
    private val userDao: UserDao
): UserRepository {

    override suspend fun isUserAvailable(): Either<Fail, Boolean> {
        return try {
            val response = sessionManager.isUserAvailable()
            Either.Right(response)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun getSessionUserId(): Either<Fail, Long> {
        return try {
            val response = sessionManager.getAccessToken()
            Either.Right(response)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun login(userId: Long): Either<Fail, Boolean> {
        return try {
            sessionManager.saveAccessToken(userId)
            Either.Right(true)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun logout(): Either<Fail, Boolean> {
        return try {
            sessionManager.clearAccessToken()
            Either.Right(true)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun findUserById(id: Long): Either<Fail, User> {
        return try {
            val response = userDao.findUserById(id)
            response?.let { Either.Right(it) } ?: Either.Left(Fail.NotFoundFail)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun findUserByEmail(email: String): Either<Fail, User> {
        return try {
            val response = userDao.findUserByEmail(email)
            response?.let { Either.Right(it) } ?: Either.Left(Fail.NotFoundFail)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun findPreferencesByUserId(userId: Long): Either<Fail, UserPreferences> {
        return try {
            val response = userDao.findPreferencesByUserId(userId)
            response?.let { Either.Right(it) } ?: Either.Left(Fail.NotFoundFail)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun findPreferencesById(id: Long): Either<Fail, UserPreferences> {
        return try {
            val response = userDao.findPreferencesById(id)
            response?.let { Either.Right(it) } ?: Either.Left(Fail.NotFoundFail)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun findPasswordByUserId(userId: Long): Either<Fail, UserPassword> {
        return try {
            val response = userDao.findPasswordByUserId(userId)
            response?.let { Either.Right(it) } ?: Either.Left(Fail.NotFoundFail)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun findPasswordById(id: Long): Either<Fail, UserPassword> {
        return try {
            val response = userDao.findPasswordById(id)
            response?.let { Either.Right(it) } ?: Either.Left(Fail.NotFoundFail)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun userExistsByEmail(email: String): Either<Fail, Boolean> {
        return try {
            val response = userDao.userExistsByEmail(email)
            Either.Right(response)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun saveUser(user: User): Either<Fail, Long> {
        return try {
            val response = userDao.saveUser(user)
            Either.Right(response)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun updateUser(user: User): Either<Fail, Int> {
        return try {
            val response = userDao.updateUser(user)
            Either.Right(response)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun savePreferences(preferences: UserPreferences): Either<Fail, Long> {
        return try {
            val response = userDao.savePreferences(preferences)
            Either.Right(response)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun updatePreferences(preferences: UserPreferences): Either<Fail, Int> {
        return try {
            val response = userDao.updatePreferences(preferences)
            Either.Right(response)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun savePassword(password: UserPassword): Either<Fail, Long> {
        return try {
            val response = userDao.savePassword(password)
            Either.Right(response)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun updatePassword(password: UserPassword): Either<Fail, Int> {
        return try {
            val response = userDao.updatePassword(password)
            Either.Right(response)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun deleteUser(id: Long): Either<Fail, Int> {
        return try {
            val response = userDao.deleteUser(id)
            Either.Right(response)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun deletePreferences(id: Long): Either<Fail, Int> {
        return try {
            val response = userDao.deletePreferences(id)
            Either.Right(response)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun deletePreferencesByUserId(userId: Long): Either<Fail, Int> {
        return try {
            val response = userDao.deletePreferencesByUserId(userId)
            Either.Right(response)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun deletePassword(id: Long): Either<Fail, Int> {
        return try {
            val response = userDao.deletePassword(id)
            Either.Right(response)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun deletePasswordByUserId(userId: Long): Either<Fail, Int> {
        return try {
            val response = userDao.deletePasswordByUserId(userId)
            Either.Right(response)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }


}