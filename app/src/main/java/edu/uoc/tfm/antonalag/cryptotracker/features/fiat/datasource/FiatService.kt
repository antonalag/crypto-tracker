package edu.uoc.tfm.antonalag.cryptotracker.features.fiat.datasource

import android.util.Log
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Endpoints
import edu.uoc.tfm.antonalag.cryptotracker.features.fiat.model.Fiat
import io.ktor.client.*
import io.ktor.client.request.*

class FiatService(
    private val httpClient: HttpClient
) {

    private val TAG = "FiatService"

    // Servicio para obtener todas las monedas
    suspend fun getFiats(): List<Fiat>{
        try{
            return httpClient.get(Endpoints.fiatsUrl)
        } catch(t: Throwable){
            Log.w(TAG, "Error getting cryptocurrencies, t")
            return emptyList()
        }
    }

}