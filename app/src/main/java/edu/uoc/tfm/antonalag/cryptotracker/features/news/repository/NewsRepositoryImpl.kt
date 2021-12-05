package edu.uoc.tfm.antonalag.cryptotracker.features.news.repository

import edu.uoc.tfm.antonalag.cryptotracker.core.db.ApplicationDatabase
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either
import edu.uoc.tfm.antonalag.cryptotracker.features.news.datasource.NewsDao
import edu.uoc.tfm.antonalag.cryptotracker.features.news.datasource.NewsService
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.LocalNews
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.NewsListViewDto
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.NewsResponse

class NewsRepositoryImpl(
    private val newsService: NewsService,
    private val newsDao: NewsDao
): NewsRepository {

    override suspend fun findAllByUserId(userId: Long): Either<Fail, List<LocalNews>> {
        return try {
            val response = newsDao.findAllByUserId(userId)
            Either.Right(response)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun findAll(filters: Map<String, String>, next: String?): Either<Fail, NewsListViewDto> {
        return newsService.getNews(filters, next)
    }

    override suspend fun findById(id: Long): Either<Fail, LocalNews> {
        return try {
            val response = newsDao.findById(id)
            Either.Right(response)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun newsExistsByUrl(url: String): Either<Fail, Boolean> {
        return try {
            val response = newsDao.newsExistsByUrl(url)
            Either.Right(response)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun save(news: LocalNews): Either<Fail, Long> {
        return try {
            val response = newsDao.save(news)
            Either.Right(response)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun delete(id: Long): Either<Fail, Int> {
        return try {
            val response = newsDao.delete(id)
            Either.Right(response)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

    override suspend fun deleteByUserId(userId: Long): Either<Fail, Int> {
        return try {
            val response = newsDao.deleteByUserId(userId)
            Either.Right(response)
        } catch(exception: Throwable) {
            Either.Left(Fail.LocalFail)
        }
    }

}