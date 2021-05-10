package com.jabirdeveloper.tinderswipe.LikeYou

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
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
import com.jabirdeveloper.tinderswipe.Functions.NetWorkState
import com.jabirdeveloper.tinderswipe.Functions.TimeStampToDate
import com.jabirdeveloper.tinderswipe.ProfileUserOppositeActivity2
import com.jabirdeveloper.tinderswipe.R
import java.text.DecimalFormat

class LikeYouAdapter(private val Like: MutableList<LikeYouObject>, private val context: Activity) : RecyclerView.Adapter<LikeYouAdapter.Holder?>() {

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

            val t = TimeStampToDate(Like[position].time)
            val dateUser = t.date()
            if (t.getCurrentTime() != dateUser) {
                time.text = dateUser
            } else {
                time.text = context.getString(R.string.today) + " " + t.time()
            }

            Glide.with(context).load(Like[position].profileImageUrl).apply(RequestOptions().override(100, 100)).into(imageView)
            container.animation = AnimationUtils.loadAnimation(context, R.anim.item_animation_fall_down)
            name.text = Like[position].name
            if (Like[position].status == "0") {
                Glide.with(context).load(R.drawable.offline_user).into(status)
            } else {
                Glide.with(context).load(R.drawable.online_user).into(status)
            }
            if (Like[position].gender == "Male") {
                tag.text = context.getString(R.string.Male_semi) + " " + Like[position].Age
            } else {
                tag.text = context.getString(R.string.Female_semi) + " " + Like[position].Age
            }
            city.text = Like[position].city + ", " + df2.format(Like[position].distance) + " km"
            container.setOnClickListener{
                seeDB = FirebaseDatabase.getInstance().reference.child("Users").child(Like[position].userId!!).child("see_profile").child(userID)
                seeDB!!.setValue(true)
                val intent = Intent(context, ProfileUserOppositeActivity2::class.java)
                intent.putExtra("User_opposite", Like[position].userId)
                intent.putExtra("form_like", "1")
                intent.putExtra("position",position)
                context.startActivityForResult(intent,11)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_likeyou, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.set(position)
    }

    override fun getItemCount(): Int {
        return Like.size
    }


}