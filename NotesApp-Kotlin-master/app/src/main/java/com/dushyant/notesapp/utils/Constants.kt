package com.dushyant.notesapp.utils

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import com.dushyant.notesapp.R
import java.util.ArrayList

object Constants {

    const val users : String = "users"

    fun animateProgressBar(progressDots: Array<View?>) {

        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.5f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.5f)
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0.5f, 1f)
        val translateY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 1f, -10f)

        val animatorSet = AnimatorSet()
        val objectAnimators: MutableList<Animator> = ArrayList()

        val duration: Long = 500
        for (i in progressDots.indices) {
            val progressDot = progressDots[i]!!
            progressDot.alpha = 0.5f
            val objectAnimator = ObjectAnimator.ofPropertyValuesHolder(
                progressDot,
                translateY,
                scaleX,
                scaleY,
                alpha
            )
            objectAnimator.duration = duration
            objectAnimator.startDelay = i * duration / 2
            objectAnimator.repeatCount = ObjectAnimator.INFINITE
            objectAnimator.repeatMode = ObjectAnimator.REVERSE
            objectAnimators.add(objectAnimator)
        }
        animatorSet.playTogether(objectAnimators)
        animatorSet.start()
    }

    fun slideUpAnim(view: View) {
        val animation = ObjectAnimator.ofPropertyValuesHolder(view,
            PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 200.0f, 10.0f));
        animation.duration = 1000
        animation.startDelay = 500
        animation.start()
    }

}