package com.jabirdeveloper.tinderswipe.Matches

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.jabirdeveloper.tinderswipe.R

class MatchesAdapter(private val matchesList: ArrayList<MatchesObject>, private val context: Context?, private val currentUid: String?) : RecyclerView.Adapter<MatchesViewHolders?>() {
    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchesViewHolders {
        val layoutView:View = LayoutInflater.from(parent.context).inflate(R.layout.item_matches, null, false)
        layoutView.layoutParams =  RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return MatchesViewHolders(layoutView, context, matchesList) }
    override fun onBindViewHolder(holder: MatchesViewHolders, position: Int) {
        holder.set(position)
        if (matchesList.elementAt(position).time != "-1") {
            holder.mLateView?.hint = matchesList[position].time
            holder.mLateView?.visibility = View.VISIBLE
        } else {
            holder.mLateView?.visibility = View.INVISIBLE }
        if (matchesList[position].count_unread == -1 || matchesList[position].count_unread == 0) { holder.mRead?.visibility = View.GONE }
        else { holder.mRead?.visibility = View.VISIBLE
             holder.mRead?.text = matchesList[position].count_unread.toString() }
        holder.mLate?.visibility = View.VISIBLE
        when {
            matchesList[position].late == "audio$currentUid" -> {
                holder.mLate?.hint = context?.getString(R.string.send_audio) }
            matchesList[position].late == "audio" + matchesList[position].userId -> {
                holder.mLate?.hint = matchesList[position].name + " " + context?.getString(R.string.receive_audio) }
            matchesList[position].late == "photo$currentUid" -> {
                holder.mLate?.hint = context?.getString(R.string.send_picture) }
            matchesList[position].late == "photo" + matchesList[position].userId -> {
                holder.mLate?.hint = matchesList[position].name + " " + context?.getString(R.string.receive_picture) }
            matchesList[position].late != "" -> {
                holder.mLate?.hint = matchesList[position].late }
            else -> { holder.mLate?.hint = context?.getString(R.string.let_start_chat) }
        }
        holder.mMatchId?.text = matchesList[position].userId
        holder.mMatchName?.text = matchesList[position].name
        holder.mDistance?.visibility = View.GONE
        if (matchesList[position].status == "offline") {
            Glide.with(context!!).load(R.drawable.offline_user).into(holder.mStatus!!)
        }
        else {
            Glide.with(context!!).load(R.drawable.online_user).into(holder.mStatus!!)
        }
        if (matchesList[position].profileImageUrl != "") {
            Glide.with(context).load(matchesList[position].profileImageUrl).listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                    return false }
                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    holder.progressBar!!.visibility = View.GONE
                    return false }
            }).apply(RequestOptions().override(100, 100)).into(holder.mMatchImage)
        } else {
            val drawable: Int = R.drawable.ic_man
            Glide.with(context).load(drawable).listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                    return false
                }
                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    holder.progressBar!!.visibility = View.GONE
                    return false
                }
            }).apply(RequestOptions().override(100, 100)).into(holder.mMatchImage)
        }
    }
    override fun getItemCount(): Int { return matchesList.size }

}