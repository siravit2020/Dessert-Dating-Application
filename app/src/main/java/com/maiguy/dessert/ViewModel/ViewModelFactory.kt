package com.maiguy.dessert.ViewModel

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private var context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(QuestionViewModel::class.java)) {
            return QuestionViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown class name")

    }
}