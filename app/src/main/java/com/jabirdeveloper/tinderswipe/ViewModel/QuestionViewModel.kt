package com.jabirdeveloper.tinderswipe.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.jabirdeveloper.tinderswipe.Functions.LoadingDialog
import com.jabirdeveloper.tinderswipe.QAStore.QAObject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

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