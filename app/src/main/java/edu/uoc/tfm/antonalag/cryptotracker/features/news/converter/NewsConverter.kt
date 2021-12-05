package edu.uoc.tfm.antonalag.cryptotracker.features.news.converter

import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.News
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.NewsListViewDto
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.NewsResponse

interface NewsConverter {

    fun fromNewsResponseToNewsListViewDto(news: NewsResponse): NewsListViewDto

}