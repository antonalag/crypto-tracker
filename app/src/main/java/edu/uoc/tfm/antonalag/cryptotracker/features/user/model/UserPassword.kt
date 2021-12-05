package edu.uoc.tfm.antonalag.cryptotracker.features.user.model

import androidx.annotation.NonNull
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserPassword")
data class UserPassword(
    @PrimaryKey(autoGenerate = true) @NonNull val id: Long,
    val password: String,
    val userId: Long
) {
    constructor(password: String, userId: Long): this(0L, password, userId)
}
