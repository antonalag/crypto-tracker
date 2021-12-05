package edu.uoc.tfm.antonalag.cryptotracker.features.user.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "User")
data class User(
    @PrimaryKey(autoGenerate = true) @NonNull val id: Long,
    val name:String,
    val surname:String,
    val email:String,
    var imageName:String?,
    var lastTimeLogged: Date?
) {
    constructor(name:String, surname:String, email:String): this(0L, name, surname, email, null, null)
}
