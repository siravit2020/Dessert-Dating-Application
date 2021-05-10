package com.jabirdeveloper.tinderswipe.Functions

import android.app.AlertDialog
import android.content.Context
import androidx.core.content.ContextCompat
import com.jabirdeveloper.tinderswipe.R

class CloseDialog(private var context: Context, private var currentUid:String, private var FU:() -> Unit) {
    fun show(){
        var b = false
        val mBuilder = AlertDialog.Builder(context)
            mBuilder.setTitle(R.string.Close_account)
            mBuilder.setMessage(R.string.Close_account_confirm)
            mBuilder.setCancelable(true)
            mBuilder.setPositiveButton(R.string.ok) { _, _ -> Close(currentUid,context).delete();  FU()}
            mBuilder.setNegativeButton(R.string.cancle) { _, _ -> }
            val mDialog = mBuilder.create()
            mDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.myrect2))
            mDialog.show()

    }
}