package edu.uoc.tfm.antonalag.cryptotracker.features.news.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class News(
    @SerialName("id") val id: Int,
    @SerialName("kind") val kind: String,
    @SerialName("domain") val domain: String,
    @SerialName("votes") val votes: Votes,
    @SerialName("source") val source: Source,
    @SerialName("title") val title: String,
    @SerialName("published_at") val publishedAt: String,
    @SerialName("slug") val slug: String,
    @SerialName("currencies") val currencies: List<Currency>,
    @SerialName("url") val url: String,
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class NewsResponse(
    @SerialName("count") val count: Int,
    @SerialName("next") val next: String,
    @SerialName("previous") val previous: String? = null,
    @SerialName("results") val results: List<News>
)

@Serializable
data class Votes(
    @SerialName("negative") val negative: Int,
    @SerialName("positive") val positive: Int,
    @SerialName("important") val important: Int,
    @SerialName("liked") val liked: Int,
    @SerialName("disliked") val disliked: Int,
    @SerialName("lol") val lol: Int,
    @SerialName("toxic") val toxic: Int,
    @SerialName("saved") val saved: Int,
    @SerialName("comments") val comments: Int
)

@Serializable
data class Source(
    @SerialName("title") val title: String,
    @SerialName("region") val region: String,
    @SerialName("domain") val domain: String,
    @SerialName("path") val path: String? = null
)

@Serializable
data class Currency(
    @SerialName("code") val code: String,
    @SerialName("title") val title: String,
    @SerialName("slug") val slug: String,
    @SerialName("url") val url: String
)


