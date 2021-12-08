package edu.uoc.tfm.antonalag.cryptotracker.core.exception

/**
 * Base class for handling errors, exceptions or fails
 */
sealed class Fail {

    object LocalFail: Fail()
    object ServerFail: Fail()
    object NotFoundFail: Fail()

}
