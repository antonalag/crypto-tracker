package edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model

import androidx.annotation.NonNull
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.User
import java.io.Serializable


@Entity(tableName = "Cryptocurrency")
data class LocalCryptocurrency(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val symbol: String,
    val icon: String,
    val price: Double,
    val userId: Long
): Serializable {
    constructor(name: String, symbol: String, icon: String, price: Double, userId: Long): this( 0L, name, symbol, icon, price, userId)
}
