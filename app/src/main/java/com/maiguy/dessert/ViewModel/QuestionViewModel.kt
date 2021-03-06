package com.maiguy.dessert.ViewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.maiguy.dessert.QAStore.data.EqualsQAObject
import com.maiguy.dessert.QAStore.data.QAObject

class QuestionViewModel(context: Context) : ViewModel() {
    private val repository = QuestionRepository(context)
    val fetchEqualsQA: LiveData<ArrayList<EqualsQAObject>> by lazy {
        repository.responseEqualsQA
    }
    val fetchQA:LiveData<ArrayList<QAObject>> by lazy {
        repository.responseQuestion
    }
    val fetchQAFeedback: LiveData<ArrayList<QAObject>> by lazy {
        repository.responseFeedbackQA
    }
    val fetchRegisterQA: LiveData<ArrayList<QAObject>> by lazy {
        repository.responseRegisterQA
    }
    val outOfQuestion: LiveData<Boolean> by lazy {
        repository.responseOutOfQuestion
    }
    fun responseOutOfQuestion() {
        repository.getOutOfQuestionStatus()
    }
    fun responseEqualsQA(languageTag: String,uid:String){
        repository.fetchEqualsQuestion(languageTag,uid)
    }
    fun response(languageTag: String,count:Int) {
        repository.fetchQuestion(languageTag,count)
    }
    fun responseRegisterQA(languageTag: String){
        repository.fetchQuestionRegister(languageTag)
    }
    fun responseFeedbackQA(languageTag: String){
        repository.fetchFeedbackQuestion(languageTag)
    }
}