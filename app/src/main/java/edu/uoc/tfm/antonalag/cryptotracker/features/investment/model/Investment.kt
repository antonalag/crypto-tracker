package edu.uoc.tfm.antonalag.cryptotracker.features.investment.model

import androidx.annotation.NonNull
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.LocalCryptocurrency

@Entity(tableName = "Investment")
data class Investment(
    @PrimaryKey(autoGenerate = true) @NonNull val id: Long,
    val totalInvested: Double,
    val cryptocurrencyId: Long
) {
    constructor(totalInvested: Double, cryptocurrencyId: Long): this(0L, totalInvested, cryptocurrencyId)
}
