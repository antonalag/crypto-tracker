package edu.uoc.tfm.antonalag.cryptotracker.core.platform

object APIConstants {

    const val NEWS_API_AUTH_TOKEN = "bfce4a16b765e3f28fcb6ab6094e9cd5be1bd124"

    // Cryptocurrency query params
    // https://api.coinstats.app/public/v1/coins?skip=0&limit=5&currency=EUR
    // https://api.coinstats.app/public/v1/coins/bitcoin?currency=AMD
    // https://api.coinstats.app/public/v1/charts?period=1m&coinId=ethereum
    const val SKIP_PARAM = "skip"
    const val LIMIT_PARAM = "limit"
    const val CURRENCY_PARAM = "currency"
    const val PERIOD_PARAM = "period"
    const val COIN_ID_PARAM = "coinId"
    const val CRYPTOCURRENCY_NAME = "%%cryptocurrency%%"


    // News query params
    // https://cryptopanic.com/api/v1/posts/?auth_token=bfce4a16b765e3f28fcb6ab6094e9cd5be1bd124&currencies=BTC,ETH
    const val NEWS_AUTH_TOKEN_PARAM = "auth_token"
    const val CURRENCIES_PARAM = "currencies"
    const val KIND_PARAM = "kind"
    const val PUBLIC_PARAM = "public"
    const val REGIONS_PARAMS = "regions"
    const val FILTER_PARAMS = "filter"
    const val PAGE_FILTER = "page"

    // Tipos de filtro
    // rising -> destacados
    // hot -> populares
    // important -> importantes
    // lol -> desternillantes



}