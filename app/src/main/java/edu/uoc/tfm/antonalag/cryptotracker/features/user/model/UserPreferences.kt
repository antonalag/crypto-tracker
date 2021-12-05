package edu.uoc.tfm.antonalag.cryptotracker.features.user.model

import androidx.annotation.NonNull
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserPreferences")
data class UserPreferences(
    @PrimaryKey(autoGenerate = true) @NonNull val id: Long,
    val fiat: String,
    val fiatSymbol: String,
    val timeInterval: String,
    val dataUpdate: String,
    val userId: Long
) {
    constructor(
        fiat: String,
        fiatSymbol: String,
        timeInterval: String,
        dataUpdate: String,
        userId: Long
    ) : this(0L, fiat, fiatSymbol, timeInterval, dataUpdate, userId)
}
