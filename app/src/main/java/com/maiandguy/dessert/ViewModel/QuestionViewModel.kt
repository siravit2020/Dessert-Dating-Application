package com.maiandguy.dessert.ViewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.maiandguy.dessert.QAStore.QAObject

class QuestionViewModel(private val context: Context) : ViewModel() {
    private val repository = QuestionRepository(context)
    val fetchQA:LiveData<ArrayList<QAObject>> by lazy {
        repository.responseQuestion
    }
    val fetchRegisterQA: LiveData<ArrayList<QAObject>> by lazy {
        repository.responseRegisterQA
    }
    fun response(languageTag: String) {
        repository.fetchQuestion(languageTag)
    }
    fun responseRegisterQA(languageTag: String){
        repository.fetchQuestionRegister(languageTag)
    }
}