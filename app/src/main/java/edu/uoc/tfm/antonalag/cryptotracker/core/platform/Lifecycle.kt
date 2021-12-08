package edu.uoc.tfm.antonalag.cryptotracker.core.platform

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail

/**
 * LifecycleOwner is a single method interface that indicates that a class has a Lifecycle.
 * Lifecycle defines objects that have a life cycle in Android along with their state and event information.
 * For more information please visit: https://developer.android.com/topic/libraries/architecture/lifecycle
 */

/**
 * Allows a viewmodel to handle a LiveData object an apply a function when the live data finished correctly.
 */
fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T) -> Unit) =
    liveData.observe(this, Observer(body))

/**
 * Allows a viewmodel to handle a LiveData object an apply a function when the live data finished incorrectly.
 */
fun <L : LiveData<Fail>> LifecycleOwner.fail(liveData: L, body: (Fail) -> Unit) =
    liveData.observe(this, Observer(body))