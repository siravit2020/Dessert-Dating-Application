package com.siravit.dessert.dialogs

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.siravit.dessert.R

class FeedbackDialog(private var context: Context,private var getFeedbackQuestion:() -> Unit) {
    fun show(){
        val mBuilder = AlertDialog.Builder(context)
        mBuilder.setTitle("ตอบความพึงพอใจค่ะ")
        mBuilder.setMessage("ตอบๆมาเถอะ เดี๋ยวให้ 20 ไลค์")
        mBuilder.setCancelable(true)
        mBuilder.setPositiveButton(R.string.ok) { _, _ ->
            getFeedbackQuestion()
        }
        mBuilder.setNegativeButton(R.string.cancel) { _, _ -> }
        val mDialog = mBuilder.create()
        mDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.myrect2))
        mDialog.show()
    }
}