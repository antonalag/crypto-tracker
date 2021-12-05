package edu.uoc.tfm.antonalag.cryptotracker.features.fiat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Fiat(
    @SerialName("name") val name: String,
    @SerialName("rate") val rate: Double,
    @SerialName("symbol") val symbol: String,
    @SerialName("imageUrl") val imageUrl: String
)
