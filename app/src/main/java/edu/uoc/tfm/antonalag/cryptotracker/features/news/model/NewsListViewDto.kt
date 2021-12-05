package edu.uoc.tfm.antonalag.cryptotracker.features.news.model

data class NewsListViewDto (
    val next: String,
    val data: List<NewsListViewResultDto>
)

data class NewsListViewResultDto (
    val title: String,
    val url: String
)
