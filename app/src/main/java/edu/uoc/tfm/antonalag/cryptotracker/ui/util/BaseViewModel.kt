package edu.uoc.tfm.antonalag.cryptotracker.ui.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.uoc.tfm.antonalag.cryptotracker.core.exception.Fail

/**
 * Class with default Failure handling
 */
abstract class BaseViewModel: ViewModel() {

    private val _fail: MutableLiveData<Fail> = MutableLiveData()
    val fail: LiveData<Fail> = _fail

    /**
     * Called when any API external or local request that implement this class has failed
     */
    protected fun handleFail(fail: Fail){
        _fail.value = fail
    }
}