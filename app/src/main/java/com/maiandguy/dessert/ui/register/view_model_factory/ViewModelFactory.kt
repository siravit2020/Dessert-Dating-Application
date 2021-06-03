package com.maiandguy.dessert.ui.register.view_model_factory

import android.app.Application
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.maiandguy.dessert.ui.register.view_model.RegisterViewModel

class ViewModelFactory(private var intent: Intent,private var application: Application): ViewModelProvider.Factory  {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(intent,application) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}