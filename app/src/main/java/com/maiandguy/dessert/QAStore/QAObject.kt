package com.maiandguy.dessert.QAStore

data class QAObject(val questionId:String,
                    val questions: String,
                    val choice: ArrayList<String>)