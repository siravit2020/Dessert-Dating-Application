package com.maiguy.dessert.dialogs.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.maiguy.dessert.QAStore.data.EqualsQAObject
import com.maiguy.dessert.R

class EqualsQAAdapter(private val data:ArrayList<EqualsQAObject>,private val context: Context) :
    RecyclerView.Adapter<EqualsQAAdapter.ViewHolder>() {
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        private val textQuestion:TextView = itemView.findViewById(R.id.text_equals_question)
        private val iconQuestion:ImageView = itemView.findViewById(R.id.icon_equals_item)
        fun set(position:Int){
            textQuestion.text = data[position].question
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.item_equals_question,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.set(position)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}