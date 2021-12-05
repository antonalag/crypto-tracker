package edu.uoc.tfm.antonalag.cryptotracker.features.news.converter

import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.News
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.NewsListViewDto
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.NewsListViewResultDto
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.NewsResponse

class NewsConverterImpl: NewsConverter {

    override fun fromNewsResponseToNewsListViewDto(news: NewsResponse): NewsListViewDto {
        val next = news.next
        val data = news.results.map { NewsListViewResultDto(it.title, it.url) }
        return NewsListViewDto(next, data)
    }

}