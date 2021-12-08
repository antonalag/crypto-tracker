package edu.uoc.tfm.antonalag.cryptotracker.ui.util

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.view.View

/**
 * Clas that manages animations
 */
class AnimationManager {

    enum class AnimationType {
        GROW_AND_SHRINK
    }

    /**
     * add grow and shirnk animation in a view
     */
    fun growAndShrink(view: View): ObjectAnimator {
        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat("scaleX", 0.5f),
            PropertyValuesHolder.ofFloat("scaleY", 0.5f)
        )

        scaleDown.duration = 2000
        scaleDown.repeatMode = ValueAnimator.REVERSE
        scaleDown.repeatCount = ValueAnimator.INFINITE
        return scaleDown
    }


    companion object {

        fun animation(type: AnimationType, view: View): ObjectAnimator {
            return when (type) {
                AnimationType.GROW_AND_SHRINK -> {
                    AnimationManager().growAndShrink(view)
                }
            }
        }

    }
}