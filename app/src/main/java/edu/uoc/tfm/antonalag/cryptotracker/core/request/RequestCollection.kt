package edu.uoc.tfm.antonalag.cryptotracker.core.request

data class RequestCollection<out Collection>(val status: RequestStatus, val data:Collection?, val message: String?){

    companion object {
        // Encapsulate data when request was successful
        fun <Collection> success(data: Collection): RequestCollection<Collection> {
            return RequestCollection(RequestStatus.SUCCESS, data, null)
        }

        // Encapsulate data when request failed
        fun <Collection> error(message: String, data: Collection? = null): RequestCollection<Collection> {
            return RequestCollection(RequestStatus.ERROR, data, message)
        }

        // Encapsulate data when request is loading
        fun <Collection> loading(): RequestCollection<Collection> {
            return RequestCollection(RequestStatus.LOADING, null, null)
        }
    }

}
