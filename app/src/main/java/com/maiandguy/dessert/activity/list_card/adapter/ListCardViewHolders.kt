package com.maiandguy.dessert.activity.list_card.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.maiandguy.dessert.activity.list_card.model.ListCardModel
import com.maiandguy.dessert.dialogs.ReportUser
import com.maiandguy.dessert.activity.profile_information_opposite.view.ProfileInformationOppositeUserActivity
import com.maiandguy.dessert.R
import java.util.*

class ListCardViewHolders(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {
    var mMatchId: TextView = itemView.findViewById(R.id.id)
    var mMatchName: TextView = itemView.findViewById(R.id.Matches_name)
    var mDistance: TextView = itemView.findViewById(R.id.distance_text)
    var mStatus: TextView = itemView.findViewById(R.id.status_time)
    var tag: TextView = itemView.findViewById(R.id.tagkm)
    var percent:TextView = itemView.findViewById(R.id.Latest_chat)
    var myself: TextView = itemView.findViewById(R.id.myself)
    var mMatchImage: ImageView = itemView.findViewById(R.id.Match_Image)
    var onOffList: ImageView = itemView.findViewById(R.id.on_off_matches)
    var container: LinearLayout = itemView.findViewById(R.id.dd_22)
    var mLinear: LinearLayout = itemView.findViewById(R.id.dd_22)
    var viewOnline: LinearLayout = itemView.findViewById(R.id.view_online)
    private var seeDB: DatabaseReference? = null
    private val userID: String = FirebaseAuth.getInstance().uid!!
    private var i = 0
    var progressBar: ProgressBar? = itemView.findViewById(R.id.progress_image)
    init {

        mLinear.setOnLongClickListener{
            mLinear.background = ContextCompat.getDrawable(context, R.drawable.background_click_tran)
            showDialog()
            true
        }
        mLinear.setOnClickListener{
            seeDB = FirebaseDatabase.getInstance().reference.child("Users").child(mMatchId.text.toString()).child("see_profile").child(userID)
            val newDate: MutableMap<String?, Any?> = HashMap()
            newDate["date"] =  ServerValue.TIMESTAMP
            seeDB!!.updateChildren(newDate)
            val intent = Intent(context, ProfileInformationOppositeUserActivity::class.java)
            intent.putExtra("User_opposite", mMatchId.text.toString())
            intent.putExtra("form_list", mMatchId.text.toString())
            context.startActivity(intent)
        }
    }
    fun bind(listCardItem: ListCardModel?) {
        var placeHolder = R.drawable.ic_man;
        if(listCardItem!!.gender == "Female") placeHolder = R.drawable.ic_woman;
        percent.visibility = View.VISIBLE
        percent.text = "ความเข้ากัน ${(listCardItem!!.percent.toString())} %"
        Glide.with(context).load(listCardItem!!.profileImageUrl).listener(object : RequestListener<Drawable?> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                Log.d("failLoadPhoto" , "fails");
                progressBar!!.visibility = View.GONE
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                viewOnline.visibility = View.VISIBLE
                progressBar!!.visibility = View.GONE
                return false
            }
        }).apply(RequestOptions().override(100, 100)).error(placeHolder).into(mMatchImage)
        container.animation = AnimationUtils.loadAnimation(context, R.anim.item_animation_fall_down)
        if (!listCardItem.off_status) {
            onOffList.visibility = View.VISIBLE
            if (listCardItem.status_opposite == "offline") {
                Glide.with(context).load(R.drawable.offline_user).into(onOffList)
                if (listCardItem.typeTime != "") {
                    val time = listCardItem.time
                    when (listCardItem.typeTime) {
                        "d" -> {
                            mStatus.text = time + " " + context.getString(R.string.days_ago)
                        }
                        "h" -> {
                            mStatus.text = time + " " + context.getString(R.string.hours_ago)
                        }
                        "m" -> {
                            mStatus.text = time + " " + context.getString(R.string.minutes_ago)
                        }
                        "0" -> {
                            mStatus.text = "เมื่อสักครู่"
                        }
                    }
                }

            } else {
                Glide.with(context).load(R.drawable.online_user).into(onOffList)
                mStatus.text = context.getString(R.string.online)
            }
        } else {
            onOffList.visibility = View.GONE
            mStatus.text = ""
        }
        mDistance.text = listCardItem.distance + " " + context.getString(R.string.kilometer)
        tag.visibility = View.VISIBLE
        tag.text = listCardItem.Age
        mMatchId.text = listCardItem.userId
        mMatchId.visibility = View.GONE
        mMatchName.text = listCardItem.name
        if (listCardItem.myself != "") {
            myself.visibility = View.VISIBLE
            myself.text = listCardItem.myself
        } else {
            myself.visibility = View.GONE
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun showDialog() {
        val dd = PopupMenu(context, tag)
        dd.menuInflater.inflate(R.menu.popup_listmenu, dd.menu)
        dd.gravity = Gravity.START
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dd.setForceShowIcon(true)
        }
        dd.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.menu_unmatch2) {
                val mDialog = ReportUser(context as Activity, mMatchId.text.toString()).reportDialog()
                mDialog.show()
            }
            mLinear.background = ContextCompat.getDrawable(context, R.drawable.background_click)
            true
        }
        dd.setOnDismissListener { mLinear.background = ContextCompat.getDrawable(context, R.drawable.background_click) }
        dd.show()
    }

}