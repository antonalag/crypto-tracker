package edu.uoc.tfm.antonalag.cryptotracker.features.investment.model

data class InvestmentCryptocurrency(
    val name: String,
    val symbol: String,
    val icon: String,
    val price: Double,
    val localCryptocurrencyId: Long,
    val totalInvested: Double
)
