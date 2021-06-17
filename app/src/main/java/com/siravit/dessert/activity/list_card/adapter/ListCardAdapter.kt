package com.siravit.dessert.activity.list_card.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.siravit.dessert.activity.list_card.model.ListCardModel
import com.siravit.dessert.R

class ListCardAdapter(private val matchesList: ArrayList<ListCardModel?>, private val context: Context) : RecyclerView.Adapter<ListCardViewHolders>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListCardViewHolders {

        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.item_matches, null, false)
        val lp = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutView.layoutParams = lp
        return ListCardViewHolders(layoutView, context)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ListCardViewHolders, position: Int) {
        holder.bind(matchesList[position])

    }

    override fun getItemCount(): Int {
        return matchesList.size
    }

}