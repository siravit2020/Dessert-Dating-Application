package com.jabirdeveloper.tinderswipe.Functions

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.jabirdeveloper.tinderswipe.R

class WarningDialog(val context: Context, var nameAndValue:String) {

    fun show(){
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.warning_dialog, null)
        val dialog: Dialog = Dialog(context)
        val textView = view.findViewById<TextView>(R.id.text_warning)
        textView.text = nameAndValue
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(view)
        val width = (context.resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog.window!!.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialog.show()
    }
}