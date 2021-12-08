package edu.uoc.tfm.antonalag.cryptotracker.features.news.repository

import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.LocalNews
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.NewsListViewDto
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.NewsResponse

interface NewsRepository {

    /**
     * Get all saved news by user id
     */
    suspend fun findAllByUserId(userId: Long): Either<Fail, List<LocalNews>>

    /**
     * Get all news. Apply filters if necessary
     */
    suspend fun findAll(filters: Map<String,String>, next: String?): Either<Fail, NewsListViewDto>

    /**
     * Get saved news by id
     */
    suspend fun findById(id: Long): Either<Fail,LocalNews>

    /**
     * Check if a saved news exists by URL
     */
    suspend fun newsExistsByUrl(url: String): Either<Fail, Boolean>

    /**
     * Save a local news
     */
    suspend fun save(news: LocalNews): Either<Fail, Long>

    /**
     * Delete a local news y id
     */
    suspend fun delete(id: Long) : Either<Fail, Int>

    /**
     * Delete local news by user id
     */
    suspend fun deleteByUserId(userId: Long): Either<Fail, Int>

}