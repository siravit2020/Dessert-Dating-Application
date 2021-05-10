package com.jabirdeveloper.tinderswipe.Functions

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray

class StatusQuestions {
    private val db = FirebaseDatabase.getInstance().reference.child("QuestionStatus")
    fun questionStats(array:JSONArray){
       for(i in 0 until array.length()){
           val b = array.getJSONObject(i)
           val id = b.optString("id")
           val question = b.optInt("question")
           val weight = b.optInt("weight")
            CoroutineScope(Dispatchers.IO).launch {
                db.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChild(id)) {
                            when (question) {
                                -1 -> {
                                    var value = snapshot.child(id).child("skip").value.toString().toInt()
                                    db.child(id).child("skip").setValue(++value)
                                }
                                0 -> {
                                    var value = snapshot.child(id).child("question0").value.toString().toInt()
                                    db.child(id).child("question0").setValue(++value)
                                }
                                1 -> {
                                    var value = snapshot.child(id).child("question1").value.toString().toInt()
                                    db.child(id).child("question1").setValue(++value)
                                }
                            }
                            when (weight) {
                                0 -> {
                                    var value = snapshot.child(id).child("weight1").value.toString().toInt()
                                    db.child(id).child("weight1").setValue(++value)
                                }
                                10 -> {
                                    var value = snapshot.child(id).child("weight10").value.toString().toInt()
                                    db.child(id).child("weight10").setValue(++value)
                                }
                                100 -> {
                                    var value = snapshot.child(id).child("weight100").value.toString().toInt()
                                    db.child(id).child("weight100").setValue(++value)
                                }
                                150 -> {
                                    var value = snapshot.child(id).child("weight150").value.toString().toInt()
                                    db.child(id).child("weight150").setValue(++value)
                                }
                                250 -> {
                                    var value = snapshot.child(id).child("weight250").value.toString().toInt()
                                    db.child(id).child("weight250").setValue(++value)
                                }
                            }
                        } else {
                            val mapNoChild: HashMap<String, Any> = hashMapOf(
                                    "qid" to id,
                                    "question0" to 0,
                                    "question1" to 0,
                                    "weight1" to 0,
                                    "weight10" to 0,
                                    "weight100" to 0,
                                    "weight150" to 0,
                                    "weight250" to 0,
                                    "skip" to 0,
                            )
                            when (question) {
                                -1 -> mapNoChild["skip"] = 1
                                0 -> mapNoChild["question0"] = 1
                                1 -> mapNoChild["question1"] = 1
                            }
                            when (weight) {
                                1 -> mapNoChild["weight1"] = 1
                                10 -> mapNoChild["weight10"] = 1
                                100 -> mapNoChild["weight100"] = 1
                                150 -> mapNoChild["weight150"] = 1
                                250 -> mapNoChild["weight250"] = 1
                            }
                            db.child(id).updateChildren(mapNoChild)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            }
           Log.d("TAG_JSON","$id : qa $question , wa : $weight")
       }
    }
}