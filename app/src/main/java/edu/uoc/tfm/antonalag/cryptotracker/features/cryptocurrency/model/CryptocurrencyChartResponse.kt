package edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate

@Serializable
data class CryptocurrencyChartResponse(
    @SerialName("chart")
    val chart: List<List<String>>
)

data class CryptocurrencyChart(
    val date: Float,
    val price: Float
)


