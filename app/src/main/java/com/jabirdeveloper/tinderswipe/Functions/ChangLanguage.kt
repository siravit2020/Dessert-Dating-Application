package com.jabirdeveloper.tinderswipe.Functions

import android.app.Activity
import android.app.Application
import android.content.ContentResolver.wrap
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.util.Log
import androidx.appcompat.widget.TintContextWrapper.wrap
import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate
import java.nio.ByteBuffer.wrap
import java.util.*


class ChangLanguage(val context: Context) : Application() {
    private val localizationDelegate = LocalizationActivityDelegate(context as Activity)
    fun setLanguage() {
        Log.d("tagLanguage", localizationDelegate.getLanguage(context).toString())
        val locale = Locale(localizationDelegate.getLanguage(context).toString())
        Locale.setDefault(locale)
        val configuration = Configuration()
        context.resources.configuration.setLocale(locale)
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
    }
}