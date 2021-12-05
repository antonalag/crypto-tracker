package edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CryptocurrencyListViewDto(
    @SerialName("icon") val icon: String,
    @SerialName("name") val name: String,
    @SerialName("symbol") val symbol: String,
    @SerialName("price") val price: Double
)
