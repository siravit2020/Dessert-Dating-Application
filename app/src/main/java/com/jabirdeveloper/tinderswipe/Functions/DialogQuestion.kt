package com.jabirdeveloper.tinderswipe.Functions

import android.app.Dialog
import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentManager
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.jabirdeveloper.tinderswipe.QAStore.DialogFragment
import com.jabirdeveloper.tinderswipe.QAStore.QAObject
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext as withContext

class DialogQuestion(private val fragment: FragmentManager,context: Context) {
    private val loadingDialog = LoadingDialog(context).dialog()
    private var functions = Firebase.functions
    private var resultFetchQA: ArrayList<QAObject> = ArrayList()
    fun questionDataOnCall(languageTag:String): Task<HttpsCallableResult> {
        loadingDialog.show()
        val data = hashMapOf(
                "type" to "Question",
                "language" to languageTag
        )
        return  functions
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
                                resultFetchQA.add(ob)
                            }
                        }
                    }else{

                    }
                    openDialog(resultFetchQA)
                }.addOnFailureListener{

                }
     }

    private fun openDialog(ListChoice: ArrayList<QAObject>) {
        loadingDialog.dismiss()
        val dialogFragment: DialogFragment = DialogFragment()
        dialogFragment.setData(ListChoice)
        dialogFragment.show(fragment, "example Dialog")
    }
}