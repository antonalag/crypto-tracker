package edu.uoc.tfm.antonalag.cryptotracker.core.request

import android.util.Log
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either.Left
import edu.uoc.tfm.antonalag.cryptotracker.core.platform.Either.Right
import io.ktor.client.*
import io.ktor.client.request.*

/**
 * Class that allows doing requests to external APIs by HttpClient
 */
abstract class BaseService {

    /**
     * Call external GET request and convert the response
     */
    suspend inline fun <reified T,R> requestGET(
        httpClient: HttpClient,
        endpoint: String,
        params: Map<String, Any?>,
        converter: (T) -> R
    ): Either<Fail, R> {
        return try {
            val response = httpClient.get<T>(endpoint) {
                params?.let {
                    it.forEach{ (k, v) -> parameter(k, v)}
                }
            }
            return response?.let { Right(converter(response)) } ?: Left(Fail.NotFoundFail)
        } catch(exception: Throwable){
            Log.e("BaseService", exception.stackTraceToString())
            Left(Fail.ServerFail)
        }
    }

    /**
     * Call external GET request
     */
    suspend inline fun <reified T> requestGETRaw(
        httpClient: HttpClient,
        endpoint: String,
        params: Map<String, Any?>
    ): Either<Fail, T> {
        return try {
            val response = httpClient.get<T>(endpoint) {
                params?.let {
                    it.forEach{ (k, v) -> parameter(k, v)}
                }
            }
            return response?.let { Right(response) } ?: Left(Fail.NotFoundFail)
        } catch(exception: Throwable){
            Log.e("BaseService", exception.stackTraceToString())
            Left(Fail.ServerFail)
        }
    }



}