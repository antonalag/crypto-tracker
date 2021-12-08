package edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.datasource

import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.APIConstants
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Endpoints
import edu.uoc.tfm.antonalag.cryptotracker.core.request.BaseService
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.converter.CryptocurrencyConverter
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.*
import io.ktor.client.*

class CryptocurrencyService(
    private val httpClient: HttpClient,
    private val cryptocurrencyConverter: CryptocurrencyConverter
): BaseService() {

    suspend fun getCurrencies(skip: Int? = 0, limit: Int? = 20): Either<Fail, List<Cryptocurrency>> {
        val params = mapOf(APIConstants.SKIP_PARAM to skip, APIConstants.LIMIT_PARAM to limit)
        return requestGETRaw(
            httpClient,
            Endpoints.cryptocurrenciesUrl,
            params)
    }

    suspend fun getCurrenciesListViewDto(currency: String, skip: Int? = 0, limit: Int? = 20): Either<Fail, List<CryptocurrencyListViewDto>> {
        val params = mapOf(APIConstants.SKIP_PARAM to skip, APIConstants.LIMIT_PARAM to limit, APIConstants.CURRENCIES_PARAM to currency)
        return requestGET<CryptocurrenciesResponse, List<CryptocurrencyListViewDto>>(
            httpClient,
            Endpoints.cryptocurrenciesUrl,
            params
        ) { cryptocurrencyConverter.fromCryptocurrencyListToCryptocurrencyListViewDtos(it.data) }
    }

    suspend fun getCurrencyCardViewDto(name: String, currency: String): Either<Fail, CryptocurrencyCardViewDto> {
        val params = mapOf(APIConstants.CURRENCY_PARAM to currency)
        val endpoint = Endpoints.cryptocurrencyUrl.replace(APIConstants.CRYPTOCURRENCY_NAME, name.toLowerCase())
        return requestGET<CryptocurrencyResponse, CryptocurrencyCardViewDto>(
            httpClient,
            endpoint,
            params
        ) { cryptocurrencyConverter.fromCryptocurrencyToCryptocurrencyCardViewDto(it?.data) }
    }

    suspend fun getCurrencyRaw(name: String, currency: String): Either<Fail, CryptocurrencyResponse> {
        val params = mapOf(APIConstants.CURRENCY_PARAM to currency)
        val endpoint = Endpoints.cryptocurrencyUrl.replace(APIConstants.CRYPTOCURRENCY_NAME, name.toLowerCase())
        return requestGETRaw(
            httpClient,
            endpoint,
            params
        )
    }

    suspend fun getCurrencyChart(period: String, name: String): Either<Fail, List<CryptocurrencyChart>> {
        val params = mapOf(APIConstants.PERIOD_PARAM to period, APIConstants.COIN_ID_PARAM to name.toLowerCase())
        return requestGET<CryptocurrencyChartResponse, List<CryptocurrencyChart>>(
            httpClient,
            Endpoints.chartsUrl,
            params
        ) { cryptocurrencyConverter.fromCryptocurrencyChartResponseToCryptoCurrencyChart(it) }
    }

}