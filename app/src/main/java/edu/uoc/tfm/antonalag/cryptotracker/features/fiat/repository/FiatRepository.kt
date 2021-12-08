package edu.uoc.tfm.antonalag.cryptotracker.features.fiat.repository

import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either
import edu.uoc.tfm.antonalag.cryptotracker.features.fiat.model.Fiat

interface FiatRepository {

    /**
     * Get all fiats
     */
    suspend fun findAll(): Either<Fail, List<Fiat>>

}