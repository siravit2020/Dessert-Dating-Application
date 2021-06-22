package com.maiguy.dessert.activity.like_you.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.maiguy.dessert.utils.TimeStampToDate
import com.maiguy.dessert.activity.profile_information_opposite.view.ProfileInformationOppositeUserActivity
import com.maiguy.dessert.R
import com.maiguy.dessert.activity.LikeYou.LikeYouModel
import java.text.DecimalFormat

class LikeYouAdapter(private val like: MutableList<LikeYouModel>, private val context: Activity) : RecyclerView.Adapter<LikeYouAdapter.ViewHolder?>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.Match_Image)
        private val status: ImageView = itemView.findViewById(R.id.on_off_matches)
        private val name: TextView = itemView.findViewById(R.id.Matches_name)
        private val tag: TextView = itemView.findViewById(R.id.tagkm)
        private val city: TextView = itemView.findViewById(R.id.status_time)
        private var time: TextView = itemView.findViewById(R.id.time_text_likeYou)
        private var container: LinearLayout = itemView.findViewById(R.id.dd_22)
        private val userID: String = FirebaseAuth.getInstance().currentUser!!.uid
        private var seeDB: DatabaseReference? = null

        @SuppressLint("SetTextI18n")
        fun set(position: Int) {
            val df2 = DecimalFormat("#.#")

            val time = TimeStampToDate(like[position].time)
            val dateUser = time.date()
            if (time.getCurrentTime() != dateUser) {
                this.time.text = dateUser
            } else {
                this.time.text = context.getString(R.string.today) + " " + time.time()
            }

            Glide.with(context).load(like[position].profileImageUrl).apply(RequestOptions().override(100, 100)).into(imageView)
            container.animation = AnimationUtils.loadAnimation(context, R.anim.item_animation_fall_down)
            name.text = like[position].name
            if (like[position].status == "0") {
                Glide.with(context).load(R.drawable.offline_user).into(status)
            } else {
                Glide.with(context).load(R.drawable.online_user).into(status)
            }
            if (like[position].gender == "Male") {
                tag.text = context.getString(R.string.male_semi) + " " + like[position].Age
            } else {
                tag.text = context.getString(R.string.female_semi) + " " + like[position].Age
            }
            city.text = like[position].city + ", " + df2.format(like[position].distance) + " km"
            container.setOnClickListener {
                seeDB = FirebaseDatabase.getInstance().reference.child("Users").child(like[position].userId!!).child("see_profile").child(userID)
                seeDB!!.setValue(true)
                val intent = Intent(context, ProfileInformationOppositeUserActivity::class.java)
                intent.putExtra("User_opposite", like[position].userId)
                intent.putExtra("form_like", "1")
                intent.putExtra("position", position)
                context.startActivityForResult(intent, 11)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_likeyou, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.set(position)
    }

    override fun getItemCount(): Int {
        return like.size
    }


}