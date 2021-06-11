package com.maiandguy.dessert.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.maiandguy.dessert.R

class ErrorDialog(private val context:Context) {
     fun questionAskDialog(text:String = "dads") : Dialog {
         val dialog = Dialog(context)
         val view = LayoutInflater.from(context).inflate(R.layout.dialog_error,null)
         dialog.setContentView(view)
         val width = (context.resources.displayMetrics.widthPixels * 0.90).toInt()
         dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
         dialog.window!!.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
         val bynDismiss = view.findViewById<Button>(R.id.closeErrorDialog)
         val detailText = view.findViewById<TextView>(R.id.textErrorDialog)
         detailText.text = text
         bynDismiss.setOnClickListener {
            dialog.dismiss()
         }
         return  dialog
    }
}