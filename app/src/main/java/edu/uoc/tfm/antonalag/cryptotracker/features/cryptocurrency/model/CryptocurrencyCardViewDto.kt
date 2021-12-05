package edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model

data class CryptocurrencyCardViewDto(
    val name: String,
    val symbol: String,
    val icon: String,
    val price: Double,
    val priceChange1d: Double,
    var chart: List<CryptocurrencyChart>? = null
)
