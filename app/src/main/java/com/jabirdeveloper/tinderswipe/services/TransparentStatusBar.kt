package com.jabirdeveloper.tinderswipe.services

import android.app.Activity
import android.app.Application
import android.os.Build
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager

class TransparentStatusBar (activity: Activity){
    init {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            activity.window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }
}