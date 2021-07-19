package com.maiguy.dessert.dialogs

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentManager
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.maiguy.dessert.QAStore.DialogFragment
import com.maiguy.dessert.QAStore.data.QAObject

class DialogQuestion(private val fragment: FragmentManager, context: Context) {
    private val loadingDialog = LoadingDialog(context).dialog()
    private var functions = Firebase.functions
    private var resultFetchQA: ArrayList<QAObject> = ArrayList()
    fun questionDataOnCall(languageTag: String): Task<HttpsCallableResult> {
        loadingDialog.show()
        val data = hashMapOf(
            "type" to "Question",
            "language" to languageTag
        )
        return functions
            .getHttpsCallable("addQuestions")
            .call(data)
            .addOnSuccessListener { task ->

                val data: Map<*, *> = task.data as Map<*, *>
                val questions: Map<*, *> = data["questions"] as Map<*, *>

                if (questions.size > 3) {
                    for ((i, entry) in questions.keys.withIndex()) {
                        if (i + 1 < 3) {
                            val questionId = entry.toString()
                            val questionSet = questions[questionId] as Map<*, *>
                            val arr: ArrayList<String> = ArrayList()
                            arr.add(questionSet["0"].toString())
                            arr.add(questionSet["1"].toString())
                            val ob = QAObject(questionId, questionSet["question"].toString(), arr)
                            resultFetchQA.add(ob)
                        }
                    }
                } else {

                }
                openDialog(resultFetchQA)
            }.addOnFailureListener {

            }
    }

    private fun openDialog(ListChoice: ArrayList<QAObject>) {
        loadingDialog.dismiss()
        val dialogFragment: DialogFragment = DialogFragment()
        dialogFragment.setData(ListChoice, "Question")
        dialogFragment.show(fragment, "example Dialog")
    }
}