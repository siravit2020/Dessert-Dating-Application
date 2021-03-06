package com.maiguy.dessert.activity.image_chat.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.maiguy.dessert.activity.image_chat.model.ScreenModel
import com.maiguy.dessert.R

class ScreenAdapterImage(private val ctx: Context?, private val imageList: MutableList<ScreenModel?>?) : PagerAdapter() {
    private val length = 0
    private val url: String? = null
    override fun getCount(): Int {
        return imageList!!.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = ctx!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = layoutInflater.inflate(R.layout.item_image_slide, container, false)
        val imageView = itemView.findViewById<ImageView>(R.id.slide_1)
        Glide.with(ctx).load(imageList!![position]!!.Url).into(imageView)
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }

}