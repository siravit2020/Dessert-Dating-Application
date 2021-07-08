package com.maiguy.dessert.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.maiguy.dessert.R

class FeedbackDialog(private var context: Context,private var getFeedbackQuestion:() -> Unit) {
    fun show(){
        val dialog = Dialog(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.feedback_dialog, null)
        view.findViewById<Button>(R.id.ok).setOnClickListener {
            getFeedbackQuestion()
            dialog.dismiss()
        }
        view.findViewById<Button>(R.id.cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(view)
        dialog.setCancelable(true)
        val width = (context.resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog.window?.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialog.show()

    }
}