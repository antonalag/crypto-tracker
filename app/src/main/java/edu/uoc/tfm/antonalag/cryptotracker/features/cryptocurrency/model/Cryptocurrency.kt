package edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Cryptocurrency(
    @SerialName("id") val id: String,
    @SerialName("icon") val icon: String,
    @SerialName("name") val name: String,
    @SerialName("symbol") val symbol: String,
    @SerialName("rank") val rank: Int = 0,
    @SerialName("price") val price: Double = 0.0,
    @SerialName("priceBtc") val priceBtc:  Double = 0.0,
    @SerialName("volume") val volume: Double = 0.0,
    @SerialName("marketCap") val marketCap: Double = 0.0,
    @SerialName("availableSupply") val availableSupply: Double = 0.0,
    @SerialName("totalSupply") val totalSupply: Double = 0.0,
    @SerialName("priceChange1h") val priceChange1h: Double = 0.0,
    @SerialName("priceChange1d") val priceChange1d: Double = 0.0,
    @SerialName("priceChange1w") val priceChange1w: Double = 0.0,
    @SerialName("websiteUrl") val websiteUrl: String? = null,
    @SerialName("redditUrl") val redditUrl: String? = null,
    @SerialName("twitterUrl") val twitterUrl: String? = null,
    @SerialName("exp") val exp: List<String>? = null
)

@Serializable
data class CryptocurrenciesResponse(
    @SerialName("coins") val data: List<Cryptocurrency>? = null
)

@Serializable
data class CryptocurrencyResponse(
    @SerialName("coin") val data: Cryptocurrency
)
