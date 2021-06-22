package com.maiguy.dessert.activity.card.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.maiguy.dessert.activity.card.view.CardActivity
import com.maiguy.dessert.activity.profile_information_opposite.view.ProfileInformationOppositeUserActivity
import com.maiguy.dessert.R
import com.maiguy.dessert.activity.card.model.CardModel
import java.util.*

/**
 * Created by manel on 9/5/2017.
 */
class CardAdapter(private var items: ArrayList<CardModel>, private val context: Context?, private val activity: CardActivity) : RecyclerView.Adapter<CardAdapter.Holder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.set(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val name: TextView?
        private val age: TextView?
        private val dis: TextView?
        private val myself: TextView?
        private val image: ImageView?
        private val onOff: ImageView?
        private val currentUid: String?
        private val linearLayout: LinearLayout?

        init {
            currentUid = FirebaseAuth.getInstance().currentUser!!.uid
            name = itemView.findViewById(R.id.cname)
            age = itemView.findViewById(R.id.cage)
            dis = itemView.findViewById(R.id.cdis)
            image = itemView.findViewById(R.id.image)
            onOff = itemView.findViewById(R.id.on_off)
            myself = itemView.findViewById(R.id.myselfcard)
            linearLayout = itemView.findViewById(R.id.layout_star)
        }

        @SuppressLint("SetTextI18n")
        fun set(position: Int) {


            if (items[position].star) {
                linearLayout!!.visibility = View.VISIBLE
            }
            name!!.text = items[position].name
            if (!items[position].off_status) {
                onOff!!.visibility = View.VISIBLE
                if (items[position].status == "offline") {
                    Glide.with(context!!).load(R.drawable.offline_user).into(onOff)
                } else {
                    Glide.with(context!!).load(R.drawable.online_user).into(onOff)
                }
            } else onOff!!.visibility = View.GONE
            age!!.text = ",  " + items[position].age
            dis!!.text = items[position].city + ",  " + items[position].dis + " " + context!!.getString(R.string.kilometer)
            Glide.with(context).load(items[position].profileImageUrl).into(image!!)
            if (items[position].myself != "") {
                myself!!.visibility = View.VISIBLE
                myself.text = items[position].myself
            } else myself!!.visibility = View.GONE
            image.setOnClickListener(View.OnClickListener {
                val currentUserConnectionDb = FirebaseDatabase.getInstance().reference
                        .child("Users")
                        .child(items[position].userId!!)
                        .child("see_profile").child(currentUid!!)

                val newDate = hashMapOf<String, Any>()
                newDate["date"] = ServerValue.TIMESTAMP
                currentUserConnectionDb.updateChildren(newDate)
                val intent = Intent(context, ProfileInformationOppositeUserActivity::class.java)
                intent.putExtra("User_opposite", items[position].userId)
                intent.putExtra("form_main", "1")
                activity.startActivityForResult(intent, 115)
            })
        }

    }


}