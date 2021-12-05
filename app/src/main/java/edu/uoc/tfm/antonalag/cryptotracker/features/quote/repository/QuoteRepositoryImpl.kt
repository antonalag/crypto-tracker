package edu.uoc.tfm.antonalag.cryptotracker.features.quote.repository

import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either
import edu.uoc.tfm.antonalag.cryptotracker.features.quote.datasource.QuoteService
import edu.uoc.tfm.antonalag.cryptotracker.features.quote.model.Quote

class QuoteRepositoryImpl(
    private val quoteService: QuoteService
): QuoteRepository {

    override suspend fun getPhraseOfTheDay(): Either<Fail, Quote> {
        return quoteService.getPhraseOfTheDay()
    }

}