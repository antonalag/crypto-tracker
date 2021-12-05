package edu.uoc.tfm.antonalag.cryptotracker.core.request

data class Request<out T>(val status: RequestStatus, val data:T?, val message: String?){

    companion object {
        // Encapsulate data when request was successful
        fun <T> success(data: T): Request<T> {
            return Request(RequestStatus.SUCCESS, data, null)
        }

        // Encapsulate data when request failed
        fun <T> error(message: String, data: T? = null): Request<T> {
            return Request(RequestStatus.ERROR, data, message)
        }

        // Encapsulate data when request is loading
        fun <T> loading(): Request<T> {
            return Request(RequestStatus.LOADING, null, null)
        }
    }

}
