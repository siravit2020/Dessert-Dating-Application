package com.maiguy.dessert.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.maiguy.dessert.R
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maiguy.dessert.QAStore.data.EqualsQAObject
import com.maiguy.dessert.dialogs.adapter.EqualsQAAdapter


class DialogAskQuestion(private var context: Context) {
    fun equalsDialog(data:ArrayList<EqualsQAObject>) : Dialog {
        /** set View content */
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_equals_questions,null)
        val layoutManager = LinearLayoutManager(context)
        val recyclerViewAdapter:RecyclerView.Adapter<*> = EqualsQAAdapter(data,context)
        /** recyclerView config */
        val mRecycler:RecyclerView  = view.findViewById(R.id.recycler_equals_question)
        mRecycler.setHasFixedSize(true)
        mRecycler.layoutManager = layoutManager
        mRecycler.adapter = recyclerViewAdapter
        recyclerViewAdapter.notifyDataSetChanged()
        /** dialog config */
        val closeButton:Button = view.findViewById(R.id.close_equals_question)
        val dialog = Dialog(context)
        dialog.setContentView(view)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val height = (context.resources.displayMetrics.heightPixels * 0.60).toInt()
        val width = (context.resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog.window!!.setLayout(width, height)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }
        return  dialog
    }
}