package edu.uoc.tfm.antonalag.cryptotracker.features.quote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Quote(
    @SerialName("author") val author: String,
    @SerialName("phrase") val quote: String
)
