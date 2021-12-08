package edu.uoc.tfm.antonalag.cryptotracker.ui.util

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

/**
 * Class that allows to detect the swipe action on a view and
 * perform an action according to the swipe target
 */
open class OnSwipeTouchListener(val context: Context?): View.OnTouchListener {

    private val TAG = "OnSwipeTouchListener"

    private val gestureDetector: GestureDetector = GestureDetector(context, GestureListener())

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private inner class GestureListener: GestureDetector.SimpleOnGestureListener() {

        private val swipeThreshold = 100
        private val swipeVelocityThreshold = 100

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            var result = false

            try {
                val diffY: Float = e1?.let { e2?.y?.minus(it.y) } ?: 0f
                val diffX: Float = e1?.let { e2?.x?.minus(it.x) } ?: 0f
                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > swipeThreshold && abs(velocityX) > swipeVelocityThreshold) {
                        if (diffX > 0) {
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                        result = true
                    }
                }
            } catch(exception: Exception) {
                Log.e(TAG, exception.stackTraceToString())
            }

            return result
        }
    }

    open fun onSwipeRight() {}
    open fun onSwipeLeft() {}

}