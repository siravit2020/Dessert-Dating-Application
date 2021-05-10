package com.jabirdeveloper.tinderswipe.Functions

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.jabirdeveloper.tinderswipe.R

class CloseLoading(private val context: Context?, private val view: View) {
    operator fun invoke(){
        val anim: Animation = AnimationUtils.loadAnimation(context, R.anim.fade_out2)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                view.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }
        })
        view.startAnimation(anim)
    }
}