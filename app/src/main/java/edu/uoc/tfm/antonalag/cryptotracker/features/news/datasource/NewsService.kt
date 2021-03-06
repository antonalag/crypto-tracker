package edu.uoc.tfm.antonalag.cryptotracker.features.news.datasource

import android.util.Log
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.APIConstants
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Endpoints
import edu.uoc.tfm.antonalag.cryptotracker.core.request.BaseService
import edu.uoc.tfm.antonalag.cryptotracker.features.news.converter.NewsConverter
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.NewsListViewDto
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.NewsResponse
import io.ktor.client.*
import io.ktor.client.request.*


class NewsService(
    private val httpClient: HttpClient,
    private val newsConverter: NewsConverter
) : BaseService() {

    suspend fun getNews(
        filters: Map<String, String>,
        next: String?
    ): Either<Fail, NewsListViewDto> {
        return next?.let { nextUrl ->
            requestGET<NewsResponse, NewsListViewDto>(
                httpClient,
                nextUrl,
                mapOf()
            ) { newsConverter.fromNewsResponseToNewsListViewDto(it) }
        } ?: requestGET<NewsResponse, NewsListViewDto>(
            httpClient,
            Endpoints.newsUrl,
            mutableMapOf(APIConstants.NEWS_AUTH_TOKEN_PARAM to APIConstants.NEWS_API_AUTH_TOKEN).plus(
                filters
            )
        ) { newsConverter.fromNewsResponseToNewsListViewDto(it) }
    }
}