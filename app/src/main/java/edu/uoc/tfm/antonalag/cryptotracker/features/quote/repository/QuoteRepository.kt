package edu.uoc.tfm.antonalag.cryptotracker.features.quote.repository

import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either
import edu.uoc.tfm.antonalag.cryptotracker.features.quote.model.Quote

interface QuoteRepository {

    suspend fun getPhraseOfTheDay(): Either<Fail, Quote>

}