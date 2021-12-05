package edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.converter

import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.*

interface CryptocurrencyConverter {

    //fun fromAnyToCryptoCurrencyChart(list: List<Any>): CryptocurrencyChart

    fun fromListToCryptoCurrencyChart(list: List<String>): CryptocurrencyChart

    fun fromCryptocurrencyChartResponseToCryptoCurrencyChart(response: CryptocurrencyChartResponse): List<CryptocurrencyChart>

    fun fromCryptocurrencyListToCryptocurrencyListViewDtos(list: List<Cryptocurrency>?): List<CryptocurrencyListViewDto>

    fun fromCryptocurrencyToCryptocurrencyCardViewDto(cryptocurency: Cryptocurrency): CryptocurrencyCardViewDto

}