package com.jabirdeveloper.tinderswipe.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.jabirdeveloper.tinderswipe.Functions.LoadingDialog
import com.jabirdeveloper.tinderswipe.QAStore.QAObject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class QuestionRepository(context: Context) {
    private val loadingDialog = LoadingDialog(context).dialog()
    private var functions = Firebase.functions
    private var result: MutableLiveData<ArrayList<QAObject>> = MutableLiveData()
    private var resultRegisterQA: MutableLiveData<ArrayList<QAObject>> = MutableLiveData()
    val responseQuestion: LiveData<ArrayList<QAObject>>
        get() = result
    val responseRegisterQA: LiveData<ArrayList<QAObject>>
        get() = resultRegisterQA
    fun fetchQuestion(languageTag:String){
        loadingDialog.show()
        val addData:ArrayList<QAObject> = ArrayList()
        val data = hashMapOf(
                "type" to "Question",
                "language" to languageTag
        )
        functions
                .getHttpsCallable("addQuestions")
                .call(data)
                .addOnSuccessListener { task ->
                    Log.d("TAG_QUESTION",task.data.toString())
                    val data: Map<*, *> = task.data as Map<*, *>
                    val questions:Map<*,*> = data["questions"] as Map<*, *>
                    Log.d("TAG_QUESTION",questions.size.toString())
                    if(questions.size > 3) {
                        for ((i, entry) in questions.keys.withIndex()) {
                            if (i + 1 < 3) {
                                val questionId = entry.toString()
                                Log.d("testGetQuestionData", questionId)
                                val questionSet = questions[questionId] as Map<*, *>
                                val arr: ArrayList<String> = ArrayList()
                                arr.add(questionSet["0"].toString())
                                arr.add(questionSet["1"].toString())
                                Log.d("testGetQuestionData", arr.toString())
                                val ob = QAObject(questionId, questionSet["question"].toString(), arr)
                                addData.add(ob)
                            }
                        }
                        Observable.just(addData)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe{
                                    result.value = it
                                    loadingDialog.dismiss()
                                }
                    }
                }.addOnFailureListener{error -> error.printStackTrace()}
    }

    fun fetchQuestionRegister(languageTag: String) {
        val obj: ArrayList<QAObject> = ArrayList()
        val data = hashMapOf(
                "type" to "RegisterQuestion",
                "language" to languageTag
        )
        functions
                .getHttpsCallable("addQuestions")
                .call(data)
                .addOnSuccessListener { task ->
                    val data: Map<*, *> = task.data as Map<*, *>
                    val questions: Map<*, *> = data["questions"] as Map<*, *>
                    for (entry in questions.keys) {
                        val questionId = entry.toString()
                        val questionSet = questions[questionId] as Map<*, *>
                        val arr: ArrayList<String> = ArrayList()
                        arr.add(questionSet["0"].toString())
                        arr.add(questionSet["1"].toString())
                        val ob = QAObject(questionId, questionSet["question"].toString(), arr)
                        obj.add(ob)
                        Log.d("tagRegisterQuestion", questionSet["question"].toString())
                    }
                    Observable.just(obj)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                resultRegisterQA.postValue(it)
                            }
                }
    }
}