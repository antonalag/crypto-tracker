package edu.uoc.tfm.antonalag.cryptotracker.core.platform

object Endpoints {

    // Coinstats API endpoints
    private const val coinstatsBaseUrl = "https://api.coinstats.app/public/v1"
    const val cryptocurrenciesUrl = "$coinstatsBaseUrl/coins"
    val cryptocurrencyUrl = "$cryptocurrenciesUrl/%%cryptocurrency%%"
    const val fiatsUrl = "$coinstatsBaseUrl/fiats"
    const val chartsUrl = "$coinstatsBaseUrl/charts"

    // Cryptopanic API endpoints
    private const val cryptoPanicBaseUrl = "https://cryptopanic.com/api/v1"
    const val newsUrl = "$cryptoPanicBaseUrl/posts/"

    // Phrase API
    private const val phraseBaseUrl = "https://frasedeldia.azurewebsites.net/api"
    const val phraseUrl = "$phraseBaseUrl/phrase"

}