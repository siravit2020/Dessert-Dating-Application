package com.jabirdeveloper.tinderswipe.ui.register.view_model_factory

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jabirdeveloper.tinderswipe.ui.register.view_model.RegisterFinishViewModel

class ViewModelFactory(private var intent: Intent,private var application: Application): ViewModelProvider.Factory  {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterFinishViewModel::class.java)) {
            return RegisterFinishViewModel(intent,application) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}