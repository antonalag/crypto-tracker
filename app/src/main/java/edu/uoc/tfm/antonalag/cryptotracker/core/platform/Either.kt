package edu.uoc.tfm.antonalag.cryptotracker.core.platform

/**
 * Represents a value of one of two possible types. This instances are either
 * an instance of Left or Right. Functional programming convention dictates
 * that Left is used for "failure" and Right is used for "success".
 */
sealed class Either<out L, out R> {
    // Represent the left side
    data class Left<out L>(val failure: L): Either<L, Nothing>()
    // Represent the right side
    data class Right<out R>(val success: R): Either<Nothing, R>()

    // Return true if this is a Right, false otherwise
    val isRight get() = this is Right<R>
    // Return true if this is a Left, false otherwise
    val isLeft get() = this is Left<L>


    // Creates a Left Type
    fun <L> left(failure: L) = Either.Left(failure)
    // Create a Right Type
    fun<R> right(success: R) = Either.Right(success)
    // Applies fnL if this is a Left or fnR if this is a Right
    fun fold(fnL: (L) -> Any, fnR: (R) -> Any): Any =
        when(this) {
            is Left -> fnL(failure)
            is Right -> fnR(success)
        }
}

// Returns the value from this `Right` or the given argument if this is a `Left`.
fun <L, R> Either<L, R>.getOrElse(value: R): R =
    when (this) {
        is Either.Left -> value
        is Either.Right -> success
    }
