package com.jabirdeveloper.tinderswipe

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_profile_user_opposite2.*
import java.util.*

class ScreenAdapter(private val ctx: Context, private val length: Int, m0: String?, m1: String?, m2: String?, m3: String?, m4: String?, m5: String?, private val ic: Int) : PagerAdapter() {

    private val m: Array<String?> = arrayOfNulls<String?>(6)
    private val Items: ArrayList<Int> = ArrayList()

    init {
        m[0] = m0
        m[1] = m1
        m[2] = m2
        m[3] = m3
        m[4] = m4
        m[5] = m5
        for (p in 0..5) {
            if (m[p] !== "null") Items.add(p)
        }
    }

    override fun getCount(): Int {
        return length
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = layoutInflater.inflate(R.layout.item_image_slide, container, false)
        val imageView = itemView.findViewById<View?>(R.id.slide_1) as ImageView
        Log.d("rty", "0")
        Glide.with(ctx).load(m[Items[position]]).placeholder(R.drawable.tran).transition(DrawableTransitionOptions.withCrossFade(100)).into(imageView)
        if (ic != 0) {
            Glide.with(ctx).load(ic).placeholder(R.drawable.tran).transition(DrawableTransitionOptions.withCrossFade(100)).into(imageView)
        }
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        //container.removeView((View) object);
    }


}