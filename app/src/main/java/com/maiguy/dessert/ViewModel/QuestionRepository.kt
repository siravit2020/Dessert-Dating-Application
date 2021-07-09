package com.maiguy.dessert.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.maiguy.dessert.QAStore.data.EqualsQAObject
import com.maiguy.dessert.dialogs.LoadingDialog
import com.maiguy.dessert.QAStore.data.QAObject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@Suppress("UNCHECKED_CAST")
class QuestionRepository(context: Context) {
    private val loadingDialog = LoadingDialog(context).dialog()
    private val functions = Firebase.functions
    private val database = FirebaseDatabase.getInstance().reference
    private var result: MutableLiveData<ArrayList<QAObject>> = MutableLiveData()
    private var resultRegisterQA: MutableLiveData<ArrayList<QAObject>> = MutableLiveData()
    private var resultFeedbackQA: MutableLiveData<ArrayList<QAObject>> = MutableLiveData()
    private var resultEqualsQA:MutableLiveData<ArrayList<EqualsQAObject>> = MutableLiveData()
    private var resultOutOfQuestion:MutableLiveData<Boolean> = MutableLiveData()
    val responseOutOfQuestion: LiveData<Boolean>
        get() = resultOutOfQuestion
    val responseEqualsQA : LiveData<ArrayList<EqualsQAObject>>
        get() = resultEqualsQA
    val responseQuestion: LiveData<ArrayList<QAObject>>
        get() = result
    val responseRegisterQA: LiveData<ArrayList<QAObject>>
        get() = resultRegisterQA
    val responseFeedbackQA : LiveData<ArrayList<QAObject>>
        get() =  resultFeedbackQA
    fun fetchFeedbackQuestion(languageTag:String) {
        val addData:ArrayList<QAObject> = ArrayList()
        val arr: ArrayList<String> = ArrayList()
        val db = database.child("/QuestionFeedback/${languageTag}")
        db.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    val map:Map<*,*> = it.value as Map<*,*>
                    if(map["status"] == true){
                        val obj = QAObject(it.key.toString() ,map["question"].toString(),map["choice"] as ArrayList<String>)
                        addData.add(obj)
                        Log.d("QUESTION_FETCH", it.value.toString())
                    }
                }
                resultFeedbackQA.value = addData
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    fun fetchQuestion(languageTag:String,count:Int){
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
                            if (i + 1 < (count+1)) {
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

                    }
                    Observable.just(addData)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe{
                                result.value = it
                                loadingDialog.dismiss()
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
                                loadingDialog.dismiss()
                            }
                }
    }


    fun fetchEqualsQuestion(languageTag: String,uid:String) {
        val arrayList:ArrayList<EqualsQAObject> = ArrayList()
        loadingDialog.show()
        val data = hashMapOf(
            "oppositeUid" to uid,
            "locale" to languageTag
        )
        functions.getHttpsCallable("getListQuestionEqual")
            .call(data)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val data:Map<*,*> = it.result.data as Map<*, *>
                    val list :ArrayList<*> = data["result"] as ArrayList<*>
                    for(i in 0 until list.size){
                        val map:Map<*,*> = list[i] as Map<*, *>
                        val obj = EqualsQAObject(map["question"].toString() ,map["status"] as Boolean )
                        arrayList.add(obj)
                    }
                    Observable.just(arrayList)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe{
                            resultEqualsQA.postValue(it)
                            loadingDialog.dismiss()
                        }
                }else{
                    Log.d("DATA_FORM_ON_CALL","error")
                }
                loadingDialog.dismiss()
            }
    }

    fun getOutOfQuestionStatus() {
        functions.getHttpsCallable("outOfQuestion")
            .call()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val data:Map<*,*> = it.result.data as Map<*, *>
                    Log.d("OUT_OF_QUESTION","success : ${data["result"]}")
                    resultOutOfQuestion.postValue(data["result"] as Boolean)
                }else{
                    Log.d("OUT_OF_QUESTION","fails")
                }
            }
    }
}