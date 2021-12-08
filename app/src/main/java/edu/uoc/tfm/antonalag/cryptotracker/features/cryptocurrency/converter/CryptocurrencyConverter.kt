package edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.converter

import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.*

interface CryptocurrencyConverter {

    /**
     * Convert list (Obtained from CryptocurrencyChartResponse) to CryptocurrencyChart
     */
    fun fromListToCryptoCurrencyChart(list: List<String>): CryptocurrencyChart

    /**
     * Convert CryptocurrencyChartResponse to CryptocurrencyChart
     */
    fun fromCryptocurrencyChartResponseToCryptoCurrencyChart(response: CryptocurrencyChartResponse): List<CryptocurrencyChart>

    /**
     * Convert CryptocurrencyList to CryptocurrencyListViewDto
     */
    fun fromCryptocurrencyListToCryptocurrencyListViewDtos(list: List<Cryptocurrency>?): List<CryptocurrencyListViewDto>

    /**
     * Convert Cryptocurrency to CryptocurrencyCardViewDto
     */
    fun fromCryptocurrencyToCryptocurrencyCardViewDto(cryptocurency: Cryptocurrency): CryptocurrencyCardViewDto

}