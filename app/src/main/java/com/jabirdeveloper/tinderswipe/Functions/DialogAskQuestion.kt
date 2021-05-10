package com.jabirdeveloper.tinderswipe.Functions

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.jabirdeveloper.tinderswipe.R
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager


class DialogAskQuestion(private val context: Context) {
    fun dialogOutOfQuestion() : Dialog{
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_out_of_questions,null)
        val buttonClose:Button = view.findViewById(R.id.closeOutOfQuestion)
        val dialog = Dialog(context)
        dialog.setContentView(view)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val width = (context.resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog.window!!.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        buttonClose.setOnClickListener {
            dialog.dismiss()
        }
        return dialog
    }
}