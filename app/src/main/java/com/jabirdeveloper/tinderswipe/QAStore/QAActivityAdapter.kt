package com.jabirdeveloper.tinderswipe.QAStore

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.util.Log

import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserInfo
import com.google.firebase.database.FirebaseDatabase
import com.jabirdeveloper.tinderswipe.Functions.StatusQuestions
import com.jabirdeveloper.tinderswipe.R
import org.json.JSONArray
import org.json.JSONObject

class QAActivityAdapter(private val context:Context, private val result:ArrayList<QAObject>,
                        private val viewPager: ViewPager2,
                        private val intent:Intent) : RecyclerView.Adapter<QAActivityAdapter.Holder>() {
    private val hashMapQA: HashMap<String, Map<*, *>> = HashMap()
    private val json: JSONArray = JSONArray()
    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val question:TextView = itemView.findViewById(R.id.questionQARegister)
        val choiceOne:RadioButton = itemView.findViewById(R.id.radioButtonRegisterQA1)
        val choiceTwo:RadioButton = itemView.findViewById(R.id.radioButtonRegisterQA2)
        val confirmButton:Button = itemView.findViewById(R.id.registerQABtn)
         val dismissButton:Button = itemView.findViewById(R.id.registerQABtnCancel)
        val skipButton:Button = itemView.findViewById(R.id.registerQABtnSkip)
        val count:TextView = itemView.findViewById(R.id.countRegisterQA)
        val radioGroupChoice: RadioGroup = itemView.findViewById(R.id.radioGroupRegisterQA)
        val radioGroupChoiceWeight: RadioGroup = itemView.findViewById(R.id.radioGroupRegisterQAAns)
    }

    override fun getItemCount(): Int {
        return result.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        //Log.d("showItemList", userInfo["Type"].toString())
        holder.count.text = "${position+1}/${itemCount}"
        holder.question.text = "${position+1}. ${result[position].questions}"
        holder.choiceOne.text = result[position].choice[0]
        holder.choiceTwo.text = result[position].choice[1]
        holder.skipButton.setOnClickListener {
            viewPager.setCurrentItem(++viewPager.currentItem, false)
            val inputMap = mapOf("id" to result[position].questionId, "question" to -1, "weight" to -1)
            hashMapQA[result[position].questionId] = inputMap as Map<*, *>
            val obj = JSONObject(inputMap)
            json.put(obj)
            if(itemCount-1 == position){
                finish()
            }
        }
        holder.confirmButton.setOnClickListener {
            viewPager.setCurrentItem(++viewPager.currentItem, false)
            var answerWeight: Int = 0
            var answerQA: Int = 0
            val chkAns = holder.radioGroupChoice.checkedRadioButtonId
            val chkWeight = holder.radioGroupChoiceWeight.checkedRadioButtonId
            if (chkAns == -1 || chkWeight == -1) {
                Toast.makeText(context, context.getString(R.string.selected_questions), Toast.LENGTH_SHORT).show()
            } else {
                when (chkAns) {
                    R.id.radioButtonRegisterQA1 -> answerQA = 0
                    R.id.radioButtonRegisterQA2 -> answerQA = 1
                }
                when (chkWeight) {
                    R.id.radioButtonRegisterQAAns1 -> answerWeight = 1
                    R.id.radioButtonRegisterQAAns2 -> answerWeight = 10
                    R.id.radioButtonRegisterQAAns3 -> answerWeight = 100
                    R.id.radioButtonRegisterQAAns4 -> answerWeight = 150
                    R.id.radioButtonRegisterQAAns5 -> answerWeight = 250
                }
                val inputMap = mapOf("id" to result[position].questionId, "question" to answerQA, "weight" to answerWeight)
                hashMapQA[result[position].questionId] = inputMap as Map<*, *>
                val obj = JSONObject(inputMap)
                json.put(obj)
                if(itemCount-1 == position){
                    finish()
                }
            }

        }
        when (position) {
            0 -> {
                holder.confirmButton.text = context.getString(R.string.next_QA)
                holder.dismissButton.text = context.getString(R.string.dismiss_label)
                holder.dismissButton.setOnClickListener {
                    intent.apply {
                        putExtra("MapQA",hashMapQA)
                    }
                    context.startActivity(intent)
                }
            }
            itemCount - 1 -> {
                holder.confirmButton.text = context.getString(R.string.ok_QA)
                holder.dismissButton.text = context.getString(R.string.previous_QA)
                json.remove(json.length()-1)
                holder.dismissButton.setOnClickListener {
                    viewPager.setCurrentItem(--viewPager.currentItem, false)
                }
            }
            else -> {
                holder.confirmButton.text = context.getString(R.string.next_QA)
                holder.dismissButton.text = context.getString(R.string.previous_QA)
                json.remove(json.length()-1)
                holder.dismissButton.setOnClickListener {
                    viewPager.setCurrentItem(--viewPager.currentItem, false)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = (context as Activity).layoutInflater
        return Holder(inflater.inflate(R.layout.item_question, parent, false))
    }

    private fun finish(){
        StatusQuestions().questionStats(json)

        intent.apply {
            putExtra("MapQA",hashMapQA)
        }
        context.startActivity(intent)
    }

}