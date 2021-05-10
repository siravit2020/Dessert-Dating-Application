package com.jabirdeveloper.tinderswipe.Functions

import android.annotation.SuppressLint
import android.app.Activity

import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jabirdeveloper.tinderswipe.R
import com.tapadoo.alerter.Alerter
import java.text.SimpleDateFormat
import java.util.*

class ReportUser(private var context: Activity, private var matchId: String) {
    private var i = 0;
    private var usersDb = FirebaseDatabase.getInstance().reference.child("Users")
    private var reportDb = FirebaseDatabase.getInstance().reference.child("Report")
    private var currentUserId = FirebaseAuth.getInstance().uid!!
    fun reportDialog(): AlertDialog {

        val choice = context.resources.getStringArray(R.array.report_item)
        val checkedItem = BooleanArray(choice.size)
        val mBuilder = AlertDialog.Builder(context)
        mBuilder.setTitle(R.string.dialog_reportUser)
        mBuilder.setMultiChoiceItems(R.array.report_item, checkedItem) { _, position, isChecked ->
            checkedItem[position] = isChecked
        }
        mBuilder.setCancelable(true)
        mBuilder.setPositiveButton(R.string.ok) { _, _ ->
            i = 0
            while (i < choice.size) {
                val checked = checkedItem[i]
                if (checked) {
                    update(i.toString())
                }
                i++
            }
        }
        mBuilder.setNegativeButton(R.string.cancle) { _, _ -> }
        val mDialog = mBuilder.create()
        mDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.myrect2))

        return mDialog
    }

    private fun update(Child: String) {
        usersDb.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SimpleDateFormat")
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var dateBefore = true

                if (dataSnapshot.child(currentUserId).child("PutReportId").hasChild(matchId)) {
                    dateBefore = false
                } else {
                    val dateUser: String
                    val currentDate = SimpleDateFormat("dd/MM/yyyy")
                    val calendar = Calendar.getInstance()
                    dateUser = currentDate.format(calendar.time)
                    val ff = hashMapOf<String, Any>()
                    ff["date"] = dateUser
                    usersDb.child(currentUserId).child("PutReportId").child(matchId).updateChildren(ff)
                }
                Log.d("test_boolean", "$dateBefore , $matchId")
                if (dateBefore) {
                        report(Child)
//                    Alerter.create(context)
//                            .setTitle(context.getString(R.string.report_suc))
//                            .setText(context.getString(R.string.report_suc2))
//                            .setBackgroundColorInt(ContextCompat.getColor(context, R.color.c3))
//                            .setIcon(ContextCompat.getDrawable(context, R.drawable.ic_baseline_done_24)!!)
//                            .show()
//                    if (!dataSnapshot.child(matchId).hasChild("Report")) {
//                        val jj = hashMapOf<String, Any>()
//                        jj[Child] = "1"
//                        usersDb.child(matchId).child("Report").updateChildren(jj)
//                    } else if (dataSnapshot.child(matchId).hasChild("Report")) {
//                        if (dataSnapshot.child(matchId).child("Report").hasChild(Child)) {
//                            val countRep = Integer.valueOf(dataSnapshot.child(matchId).child("Report").child(Child).value.toString()) + 1
//                            val inputCount = countRep.toString()
//                            val jj = hashMapOf<String, Any>()
//                            jj[Child] = inputCount
//                            usersDb.child(matchId).child("Report").updateChildren(jj)
//                        } else {
//                            val jj = hashMapOf<String, Any>()
//                            jj[Child] = "1"
//                            usersDb.child(matchId).child("Report").updateChildren(jj)
//                        }
//                    }
                } else {

                    Alerter.create(context)
                            .setTitle(context.getString(R.string.report_failed))
                            .setText(context.getString(R.string.report_reset))
                            .setBackgroundColorInt(ContextCompat.getColor(context, R.color.c1))
                            .setIcon(ContextCompat.getDrawable(context, R.drawable.ic_do_not_disturb_black_24dp)!!)
                            .show()

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
    private fun report(Child: String){
        reportDb.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val datetime = hashMapOf<String, Any>()
                datetime["date"] = ServerValue.TIMESTAMP
                reportDb.child(matchId).updateChildren(datetime)
                val jj = hashMapOf<String, Any>()
                Alerter.create(context)
                        .setTitle(context.getString(R.string.report_suc))
                        .setText(context.getString(R.string.report_suc2))
                        .setBackgroundColorInt(ContextCompat.getColor(context, R.color.c3))
                        .setIcon(ContextCompat.getDrawable(context, R.drawable.ic_baseline_done_24)!!)
                        .show()
                if (!snapshot.hasChild(matchId)) {

                    jj[Child] = 1
                    reportDb.child(matchId).child("report").updateChildren(jj)

                } else  {
                    if (snapshot.child(matchId).hasChild(Child)) {
                        val countRep = Integer.valueOf(snapshot.child(matchId).child(Child).value.toString()) + 1
                        jj[Child] = countRep
                        reportDb.child(matchId).child("report").updateChildren(jj)
                    } else {
                        jj[Child] = 1
                        reportDb.child(matchId).child("report").updateChildren(jj)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}