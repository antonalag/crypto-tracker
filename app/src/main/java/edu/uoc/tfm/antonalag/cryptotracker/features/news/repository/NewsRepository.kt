package edu.uoc.tfm.antonalag.cryptotracker.features.news.repository

import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.LocalNews
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.NewsListViewDto
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.NewsResponse

interface NewsRepository {

    suspend fun findAllByUserId(userId: Long): Either<Fail, List<LocalNews>>

    suspend fun findAll(filters: Map<String,String>, next: String?): Either<Fail, NewsListViewDto>

    suspend fun findById(id: Long): Either<Fail,LocalNews>

    suspend fun newsExistsByUrl(url: String): Either<Fail, Boolean>

    suspend fun save(news: LocalNews): Either<Fail, Long>

    suspend fun delete(id: Long) : Either<Fail, Int>

    suspend fun deleteByUserId(userId: Long): Either<Fail, Int>

}