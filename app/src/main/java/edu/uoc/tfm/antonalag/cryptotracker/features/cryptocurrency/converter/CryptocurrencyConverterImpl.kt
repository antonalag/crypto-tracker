package edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.converter

import edu.uoc.tfm.antonalag.cryptotracker.core.util.DateUtil
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.*
import java.time.LocalDate

class CryptocurrencyConverterImpl: CryptocurrencyConverter {

    override fun fromListToCryptoCurrencyChart(list: List<String>): CryptocurrencyChart {
        val date:Float = list[0].toFloat()
        val price:Float = list[1].toFloat()
        return CryptocurrencyChart(date, price)
    }

    override fun fromCryptocurrencyChartResponseToCryptoCurrencyChart(response: CryptocurrencyChartResponse): List<CryptocurrencyChart> {
        return response.chart.map { fromListToCryptoCurrencyChart(it) }
    }

    override fun fromCryptocurrencyListToCryptocurrencyListViewDtos(list: List<Cryptocurrency>?): List<CryptocurrencyListViewDto> {
        return list?.let { value ->
            value.map {
                //CryptocurrencyListViewDto(it.icon, it.id[0].toUpperCase() + it.id.substring(1), it.symbol)
                CryptocurrencyListViewDto(it.icon, it.id[0].toUpperCase() + it.id.substring(1), it.symbol, it.price)
            }
        } ?: listOf()
    }

    override fun fromCryptocurrencyToCryptocurrencyCardViewDto(cryptocurency: Cryptocurrency): CryptocurrencyCardViewDto {
        return CryptocurrencyCardViewDto(
            cryptocurency.id,
            cryptocurency.symbol,
            cryptocurency.icon,
            cryptocurency.price,
            cryptocurency.priceChange1d
        )
    }
}