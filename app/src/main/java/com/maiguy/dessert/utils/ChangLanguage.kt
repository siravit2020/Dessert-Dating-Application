package com.maiguy.dessert.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate
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