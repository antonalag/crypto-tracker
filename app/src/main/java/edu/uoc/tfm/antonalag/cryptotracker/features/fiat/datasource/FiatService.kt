package edu.uoc.tfm.antonalag.cryptotracker.features.fiat.datasource

import android.util.Log
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Endpoints
import edu.uoc.tfm.antonalag.cryptotracker.core.request.BaseService
import edu.uoc.tfm.antonalag.cryptotracker.features.fiat.model.Fiat
import io.ktor.client.*
import io.ktor.client.request.*

class FiatService(
    private val httpClient: HttpClient
): BaseService() {

    /*suspend fun getFiats(): List<Fiat>{
        try{
            return httpClient.get(Endpoints.fiatsUrl)
        } catch(t: Throwable){
            Log.w(TAG, "Error getting cryptocurrencies, t")
            return emptyList()
        }
    }*/

    suspend fun getFiats(): Either<Fail, List<Fiat>> {
        return requestGETRaw(
            httpClient,
            Endpoints.fiatsUrl,
            emptyMap()
        )
    }

}