package com.siravit.dessert.dialogs

import android.app.AlertDialog
import android.content.Context
import androidx.core.content.ContextCompat
import com.siravit.dessert.R
import com.siravit.dessert.utils.Close

class CloseDialog(private var context: Context, private var currentUid:String, private var FU:() -> Unit) {
    fun show(){

        val mBuilder = AlertDialog.Builder(context)
            mBuilder.setTitle(R.string.close_account)
            mBuilder.setMessage(R.string.close_account_confirm)
            mBuilder.setCancelable(true)
            mBuilder.setPositiveButton(R.string.ok) { _, _ -> Close(currentUid,context).delete();  FU()}
            mBuilder.setNegativeButton(R.string.cancle) { _, _ -> }
            val mDialog = mBuilder.create()
            mDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.myrect2))
            mDialog.show()

    }
}