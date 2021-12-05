package edu.uoc.tfm.antonalag.cryptotracker.features.user.datasource

import android.content.Context

class SessionManager(context: Context) {

    private val sharedPreferencesName = "sessionPreferences"
    private val sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
    private val accessTokenKey = "userId"

    fun isUserAvailable(): Boolean {
        return getAccessToken() != 0L
    }

    fun getAccessToken(): Long {
        return sharedPreferences.getLong(accessTokenKey, 0)
    }

    fun saveAccessToken(accessToken: Long) {
        sharedPreferences.edit().apply{
            putLong(accessTokenKey, accessToken)
        }.apply()
    }

    fun clearAccessToken() {
        sharedPreferences.edit().apply {
            remove(accessTokenKey)
        }.apply()
    }

}