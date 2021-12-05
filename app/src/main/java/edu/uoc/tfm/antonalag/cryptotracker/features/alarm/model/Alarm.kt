package edu.uoc.tfm.antonalag.cryptotracker.features.alarm.model

import androidx.annotation.NonNull
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.LocalCryptocurrency
import java.util.*

@Entity(tableName = "Alarm")
data class Alarm(
    @PrimaryKey(autoGenerate = true) @NonNull val id: Integer,
    val name: String,
    val increase: Double,
    val decrease : Double,
    val type: String,
    val date: Date,
    val cryptoconcurrencyId: Integer
)

