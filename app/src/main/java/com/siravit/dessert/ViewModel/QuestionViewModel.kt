package com.siravit.dessert.ViewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.siravit.dessert.QAStore.QAObject

class QuestionViewModel(context: Context) : ViewModel() {
    private val repository = QuestionRepository(context)
    val fetchQA:LiveData<ArrayList<QAObject>> by lazy {
        repository.responseQuestion
    }
    val fetchQAFeedback: LiveData<ArrayList<QAObject>> by lazy {
        repository.responseFeedbackQA
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
    fun responseFeedbackQA(languageTag: String){
        repository.fetchFeedbackQuestion(languageTag)
    }
}