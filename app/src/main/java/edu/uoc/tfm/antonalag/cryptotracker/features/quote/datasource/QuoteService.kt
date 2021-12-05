package edu.uoc.tfm.antonalag.cryptotracker.features.quote.datasource

import android.util.Log
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.request.BaseService
import edu.uoc.tfm.antonalag.cryptotracker.features.quote.model.Quote
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Endpoints
import edu.uoc.tfm.antonalag.cryptotracker.features.quote.model.Constants
import io.ktor.client.*
import io.ktor.client.request.*
import org.json.JSONObject

class QuoteService(
    private val httpClient: HttpClient
) : BaseService() {

    private val TAG = "QuoteService"

    suspend fun getPhraseOfTheDay(): Either<Fail, Quote> {
        return try {
            val response = httpClient.get<String>(Endpoints.phraseUrl)
            val quote = if (response.isNullOrEmpty()) {
                Quote(Constants.DEFAULT_AUTHOR, Constants.DEFAULT_PHRASE)
            } else {
                /**
                 * DISCLAIMER: Implement deserialization on this way because response format:
                 *
                 */
                val element = response.replace("\\", "")
                val jsonObject = JSONObject(
                    element.substring(
                        element.indexOf("{"),
                        element.lastIndexOf("}") + 1
                    )
                )
                Quote(jsonObject.getString("author"), "\"" + jsonObject.getString("phrase") + "\"")

            }
            return response?.let { Either.Right(quote) } ?: Either.Left(Fail.NotFoundFail)
        } catch (exception: Throwable) {
            exception.message?.let { Log.e(TAG, it) }
            Either.Left(Fail.ServerFail)
        }
        /*return requestGETRaw<Quote>(
            httpClient,
            Endpoints.phraseUrl,
            mapOf()
        )*/
    }
}