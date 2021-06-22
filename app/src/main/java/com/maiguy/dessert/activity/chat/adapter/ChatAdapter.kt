package com.maiguy.dessert.activity.chat.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maiguy.dessert.R
import com.maiguy.dessert.activity.chat.model.ChatModel
import java.util.ArrayList

class ChatAdapter(private val chatList: ArrayList<ChatModel>, private val context: Context) : RecyclerView.Adapter<ChatViewHolders?>() {
    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolders {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, null, false)
        val lp = RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutView.layoutParams = lp
        return ChatViewHolders(layoutView, context)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ChatViewHolders, position: Int) {
        holder.start(chatList[position])

    }

    override fun getItemCount(): Int {
        return chatList.size
    }

}