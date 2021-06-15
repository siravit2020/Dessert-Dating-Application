package com.siravit.dessert.QAStore

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.siravit.dessert.utils.GlobalVariable
import com.siravit.dessert.utils.StatusQuestions
import com.siravit.dessert.R
import org.json.JSONArray
import org.json.JSONObject

class QAPagerAdapter(val context: Context, private val choice: ArrayList<QAObject>, val dialog: Dialog, val viewpager: ViewPager2) : RecyclerView.Adapter<QAPagerAdapter.Holder?>() {
    private val hashMapQA: HashMap<String, Any> = HashMap()
    private val json:JSONArray = JSONArray()


    private val uid = FirebaseAuth.getInstance().currentUser!!.uid
    private val db = FirebaseDatabase.getInstance().reference.child("Users").child(uid)
    val userId = FirebaseAuth.getInstance().currentUser!!.uid

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val radioGroupChoice: RadioGroup = itemView.findViewById(R.id.radioGroup_QA)
        val radioGroupChoiceWeight: RadioGroup = itemView.findViewById(R.id.radioGroup_QAWeight)
        val choice1: RadioButton = itemView.findViewById(R.id.radioButton_QA1)
        val choice2: RadioButton = itemView.findViewById(R.id.radioButton_QA2)
        val questions: TextView = itemView.findViewById(R.id.message_QA)
        val valPage: TextView = itemView.findViewById(R.id.page_QA)
        val confirmButton: Button = itemView.findViewById(R.id.QA_confirm)
        val skipButton: Button = itemView.findViewById(R.id.QA_skip)
        val dismissButton: Button = itemView.findViewById(R.id.QA_dismiss)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = (context as Activity).layoutInflater
        return Holder(inflater.inflate(R.layout.question_dialog, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.valPage.hint = "${position + 1} / $itemCount"
        holder.questions.text = choice[position].questions
        holder.choice1.text = choice[position].choice[0]
        holder.choice2.text = choice[position].choice[1]
        when (position) {
            0 -> {
                holder.dismissButton.visibility = View.GONE
                holder.confirmButton.text = context.getString(R.string.next)
                holder.dismissButton.text = context.getString(R.string.cancel)
                holder.dismissButton.setOnClickListener {
                    dialog.dismiss()
                }
            }
            itemCount - 1 -> {
                holder.confirmButton.text = context.getString(R.string.ok)
                holder.dismissButton.text = context.getString(R.string.previous)
                holder.dismissButton.setOnClickListener {
                    viewpager.setCurrentItem(--viewpager.currentItem, false)
                    json.remove(json.length()-1)
                    Log.d("Check_IsCheck", json.toString())
                }
            }
            else -> {
                holder.dismissButton.visibility = View.VISIBLE
                holder.confirmButton.text = context.getString(R.string.next)
                holder.dismissButton.text = context.getString(R.string.previous)
                holder.dismissButton.setOnClickListener {
                    viewpager.setCurrentItem(--viewpager.currentItem, false)
                    json.remove(json.length()-1)
                    Log.d("Check_IsCheck", json.toString())
                }
            }
        }
        holder.skipButton.setOnClickListener {
            viewpager.setCurrentItem(++viewpager.currentItem, false)
            val inputMap = mapOf("id" to choice[position].questionId, "question" to -1, "weight" to -1)
            hashMapQA[choice[position].questionId] = inputMap
            val obj = JSONObject(inputMap)
            json.put(obj)
            Toast.makeText(context, context.getString(R.string.warning_skip), Toast.LENGTH_SHORT).show()
            if(itemCount-1 == position){
                finish()
            }
        }
        holder.confirmButton.setOnClickListener {
            Log.d("ButtonQA","Confirm")
            var answerWeight: Int = 0
            var answerQA: Int = 0
            val chk1 = holder.radioGroupChoice.checkedRadioButtonId
            val chk2 = holder.radioGroupChoiceWeight.checkedRadioButtonId
            if (chk1 == -1 || chk2 == -1) {
                Toast.makeText(context, context.getString(R.string.selected_questions), Toast.LENGTH_SHORT).show()
            } else {
                when (chk1) {
                    R.id.radioButton_QA1 -> answerQA = 1
                    R.id.radioButton_QA2 -> answerQA = 0
                }
                when (chk2) {
                    R.id.radioButton_QAWeight1 -> answerWeight = 1
                    R.id.radioButton_QAWeight2 -> answerWeight = 10
                    R.id.radioButton_QAWeight3 -> answerWeight = 100
                    R.id.radioButton_QAWeight4 -> answerWeight = 150
                    R.id.radioButton_QAWeight5 -> answerWeight = 250
                }
                val inputMap = mapOf("id" to choice[position].questionId, "question" to answerQA, "weight" to answerWeight)
                hashMapQA[choice[position].questionId] = inputMap
                val obj = JSONObject(inputMap)
                json.put(obj)
                viewpager.setCurrentItem(++viewpager.currentItem, false)
                if (position == itemCount - 1) {
                    finish()
                }
            }

        }

    }

    private fun finish() {
        var check = true
        for(i in 0 until json.length()){
            val item = json.getJSONObject(i)["question"]
            if(item == -1){
                check = false
            }
            Log.d("GET_QUESTION", item.toString())
        }
        if(check){
            GlobalVariable.maxLike = ++GlobalVariable.maxLike
            Log.d("GLOBAL_MAX_LIKE__Adapter",GlobalVariable.maxLike.toString())
            db.child("MaxLike").setValue(GlobalVariable.maxLike)
        }
        StatusQuestions().questionStats(json)
        FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Questions").updateChildren(hashMapQA)
        dialog.dismiss()
    }

    override fun getItemCount(): Int {
        return choice.size
    }
}