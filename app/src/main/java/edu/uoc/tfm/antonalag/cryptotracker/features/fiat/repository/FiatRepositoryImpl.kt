package edu.uoc.tfm.antonalag.cryptotracker.features.fiat.repository

import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either
import edu.uoc.tfm.antonalag.cryptotracker.features.fiat.datasource.FiatService
import edu.uoc.tfm.antonalag.cryptotracker.features.fiat.model.Fiat

class FiatRepositoryImpl(
    private val fiatService: FiatService
): FiatRepository {

    override suspend fun findAll(): Either<Fail, List<Fiat>> {
        return fiatService.getFiats()
    }

}