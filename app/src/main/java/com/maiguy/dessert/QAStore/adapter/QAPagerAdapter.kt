package com.maiguy.dessert.QAStore.adapter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.maiguy.dessert.QAStore.data.QAObject
import com.maiguy.dessert.R
import com.maiguy.dessert.utils.GlobalVariable
import com.maiguy.dessert.utils.StatusQuestions
import org.json.JSONArray
import org.json.JSONObject


class QAPagerAdapter(val context: Context, private val choice: ArrayList<QAObject>, val dialog: Dialog, val viewpager: ViewPager2, val type:String) : RecyclerView.Adapter<QAPagerAdapter.Holder?>() {
    private val hashMapQA: HashMap<String, Any> = HashMap()
    private val json:JSONArray = JSONArray()
    private val uid = FirebaseAuth.getInstance().currentUser!!.uid
    private val phoneWidth:Int = (context.resources.displayMetrics.widthPixels * 0.90).toInt()
    private val phoneWidthDp:Int = dpFromPx(phoneWidth)
    private val db = FirebaseDatabase.getInstance().reference.child("Users").child(uid)
    val userId = FirebaseAuth.getInstance().currentUser!!.uid
    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val radioGroupChoice: RadioGroup = itemView.findViewById(R.id.radioGroup_QA)
        val radioGroupChoiceWeight: RadioGroup = itemView.findViewById(R.id.radioGroup_QAWeight)
        val choice1: RadioButton = itemView.findViewById(R.id.radioButton_QA1)
        val choice2: RadioButton = itemView.findViewById(R.id.radioButton_QA2)
        val choice3: RadioButton = itemView.findViewById(R.id.radioButton_QA3)
        val questions: TextView = itemView.findViewById(R.id.message_QA)
        val valPage: TextView = itemView.findViewById(R.id.page_QA)
        val confirmButton: Button = itemView.findViewById(R.id.QA_confirm)
        val dismissButton: Button = itemView.findViewById(R.id.QA_dismiss)
        val textImportant: TextView = itemView.findViewById(R.id.howImpotant)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = (context as Activity).layoutInflater
        return Holder(inflater.inflate(R.layout.question_dialog, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        if(type == "Feedback") {
            holder.textImportant.visibility = View.GONE
            holder.radioGroupChoiceWeight.visibility = View.GONE
            holder.choice3.text = choice[position].choice[2]
        }else{
            holder.choice3.visibility = View.GONE
        }
        holder.valPage.hint = "${position + 1} / $itemCount"
        holder.questions.text = choice[position].questions
        holder.choice1.text = choice[position].choice[0]
        holder.choice2.text = choice[position].choice[1]
        when (position) {
            0 -> {
                if(type == "Question") {
                    holder.dismissButton.visibility = View.GONE
                    val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                        widthButton(), LinearLayout.LayoutParams.WRAP_CONTENT,
                        0.0f
                    )
                    holder.confirmButton.layoutParams = params
                }
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
        holder.confirmButton.setOnClickListener {
            val chk1 = holder.radioGroupChoice.checkedRadioButtonId
            val chk2 = holder.radioGroupChoiceWeight.checkedRadioButtonId
            Log.d("ButtonQA","Confirm")
            when(type) {
                "Feedback" -> questionFbConfirm(chk1,position)
                "Question" -> questionConfirm(chk1,chk2,position)
            }

        }

    }
    private fun questionFbConfirm(chk1:Int,position: Int){
        var answerQA: Int = 0
        if(chk1 != -1){
            when(chk1) {
                R.id.radioButton_QA1 -> answerQA = 0
                R.id.radioButton_QA2 -> answerQA = 1
                R.id.radioButton_QA3 -> answerQA = 2
            }
            val inputMap = mapOf("id" to choice[position].questionId, "question" to answerQA)
            val obj = JSONObject(inputMap)
            json.put(obj)
            viewpager.setCurrentItem(++viewpager.currentItem, false)
            if (position == itemCount - 1) {
                feedbackFinish()
            }
        }
    }
    private fun questionConfirm(chk1:Int,chk2:Int,position: Int) {
        var answerWeight: Int = 0
        var answerQA: Int = 0
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
                questionFinish()
            }
        }
    }
    private fun feedbackFinish() {
        StatusQuestions().feedbackStatus(json)
        dialog.dismiss()
    }
    private fun questionFinish() {
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

    private fun widthButton():Int {
        return if(phoneWidthDp > 400){
            pxFromDp(200)
        }else{
            pxFromDp((phoneWidthDp/2))
        }
    }

    private fun dpFromPx (px: Int): Int {
        return (px / context.resources.displayMetrics.density).toInt()
    }
    private fun pxFromDp(dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    override fun getItemCount(): Int {
        return choice.size
    }
}