package com.jabirdeveloper.tinderswipe.Matches

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jabirdeveloper.tinderswipe.Chat.ChatActivity
import com.jabirdeveloper.tinderswipe.Functions.ReportUser
import com.jabirdeveloper.tinderswipe.R

@SuppressLint("CutPasteId")
class MatchesViewHolders(itemView: View, private val context: Context?, private val matchesList: ArrayList<MatchesObject>) : RecyclerView.ViewHolder(itemView) {
    var mMatchId: TextView?
    var mMatchName: TextView?
    var mDistance: TextView?
    var mLate: TextView?
    var mLateView: TextView?
    var mRead: TextView?
    var last: TextView?
    var mMatchImage: ImageView
    var mStatus: ImageView?
    private var mLinear: LinearLayout?
    private val userID: String?
    private var chatId: String? = null
    private var show: String? = null
    private val intent: Intent?
    private var position: Int? = 0
    private val mDataReport: DatabaseReference?
    var progressBar: ProgressBar?
    fun set(position: Int) {
        this.position = position
    }

    private fun showDialog() {
        val dd = PopupMenu(context, mLate)
        dd.menuInflater.inflate(R.menu.popup_menu, dd.menu)
        dd.gravity = Gravity.END
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dd.setForceShowIcon(true)
        }
        dd.setOnMenuItemClickListener { item ->
            when {
                item.toString() == context?.getString(R.string.start_chat) -> {
                    val intent = Intent(context, ChatActivity::class.java)
                    val b = Bundle()
                    b.apply {
                        putString("matchId", mMatchId?.text.toString())
                        putString("time_chk", mLateView?.text.toString())
                        putString("nameMatch", mMatchName?.text.toString())
                        putString("first_chat", last?.text.toString())
                        putString("unread", mRead?.text.toString())
                    }
                    matchesList!!.elementAt(position!!)?.count_unread = 0
                    intent.putExtras(b)
                    context.startActivity(intent)
                    mRead?.apply {
                        visibility = View.GONE
                        text = "0"
                    }
                }
                item.toString() == context?.getString(R.string.cancel_match) -> {
                    show = mMatchId?.text.toString()
                    val mBuilder = AlertDialog.Builder(context)
                    mBuilder.setTitle(context.getString(R.string.cancel_match2))
                    mBuilder.setMessage(context.getString(R.string.cancel_match_confirm))
                    mBuilder.setCancelable(true)
                    mBuilder.setPositiveButton(R.string.ok) { _, _ ->
                        deleteChild()
                    }
                    mBuilder.setNegativeButton(R.string.cancle) { _, _ -> }
                    val mDialog = mBuilder.create()
                    mDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.myrect2))
                    mDialog.show() }
                item.toString() == context?.getString(R.string.dialog_report) -> {
                    val mDialog = ReportUser(context as Activity, mMatchId!!.text.toString()).reportDialog()
                    mDialog.show() }
                else -> { Toast.makeText(context, "" + item, Toast.LENGTH_SHORT).show() }
            }
            mLinear?.background = ContextCompat.getDrawable(context!!, R.drawable.background_click)
            true
        }
        dd.setOnDismissListener { mLinear!!.background = ContextCompat.getDrawable(context!!, R.drawable.background_click) }
        dd.show()
    }

    private fun deleteChild() {
        val dataDelete = FirebaseDatabase.getInstance().reference.child("Users")
        FirebaseDatabase.getInstance().reference
                .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                chatId = dataSnapshot.child("Users")
                        .child(userID.toString())
                        .child("connection")
                        .child("matches")
                        .child(show.toString())
                        .child("ChatId").value.toString()
                if (dataSnapshot.child("Chat").hasChild(chatId.toString())) {
                    FirebaseDatabase.getInstance().reference
                            .child("Chat")
                            .child(chatId.toString())
                            .removeValue()
                }
                dataDelete.child(userID.toString()).child("connection")
                        .child("matches")
                        .child(show.toString()).removeValue()
                dataDelete.child(userID.toString()).child("connection")
                        .child("yep")
                        .child(show.toString()).removeValue()
                dataDelete.child(show.toString()).child("connection")
                        .child("matches")
                        .child(userID.toString()).removeValue()
                dataDelete.child(show.toString()).child("connection")
                        .child("yep")
                        .child(userID.toString()).removeValue()
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    init {
        intent = Intent(context, MatchesActivity::class.java)
        mDataReport = FirebaseDatabase.getInstance().reference.child("Users")
        mLateView = itemView.findViewById(R.id.Latest_time)
        mRead = itemView.findViewById(R.id.chat_unread)
        mDistance = itemView.findViewById(R.id.distance_text)
        mStatus = itemView.findViewById(R.id.on_off_matches)
        mMatchId = itemView.findViewById(R.id.id)
        userID = FirebaseAuth.getInstance().uid
        mLate = itemView.findViewById(R.id.Latest_chat)
        mMatchName = itemView.findViewById(R.id.Matches_name)
        mMatchImage = itemView.findViewById(R.id.Match_Image)
        last = itemView.findViewById(R.id.Latest_chat)
        mLinear = itemView.findViewById(R.id.dd_22)
        progressBar = itemView.findViewById(R.id.progress_image)
        mLinear?.setOnLongClickListener {
            mLinear?.background = ContextCompat.getDrawable(context!!, R.drawable.background_click_tran)
            showDialog()
            true
        }
        mLinear?.setOnClickListener{
            val intent = Intent(context, ChatActivity::class.java)
            val b = Bundle()
            b.apply {
                putString("time_chk", mLateView?.hint.toString())
                putString("matchId", mMatchId?.text.toString())
                putString("nameMatch", mMatchName?.text.toString())
                putString("first_chat", last?.text.toString())
                putString("unread", mRead?.text.toString())
            }
            matchesList?.get(position!!)?.count_unread = 0
            intent.putExtras(b)
            context?.startActivity(intent)
            mRead?.apply {
                visibility = View.GONE
                text = "0"
            }
        }
    }
}