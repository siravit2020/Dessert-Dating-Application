package com.siravit.dessert.utils

import android.app.Dialog
import android.content.Context
import android.view.View
import com.github.demono.AutoScrollViewPager
import com.siravit.dessert.model.PagerModel
import com.siravit.dessert.R
import com.siravit.dessert.dialogs.adapter.VipSlideAdapter
import me.relex.circleindicator.CircleIndicator

class DialogSlide(private var context: Context, private var dialog: Dialog, private var view: View) {
    fun start() {

        val pagerModels: ArrayList<PagerModel?> = ArrayList()
        with(context){
            pagerModels.add(PagerModel(getString(R.string.unlimited_like), getString(R.string.full_right_swipe), R.drawable.ic_heart))
            pagerModels.add(PagerModel(getString(R.string.get_5_star), getString(R.string.you_send_star), R.drawable.ic_starss))
            pagerModels.add(PagerModel(getString(R.string.unlimited_say_hi_2), getString(R.string.unlimited_say_hi_3), R.drawable.ic_hand))
            pagerModels.add(PagerModel(getString(R.string.who_like_you), getString(R.string.see_who_has_like), R.drawable.ic_love2))
        }

        val adapter = VipSlideAdapter(context, pagerModels)
        val pager: AutoScrollViewPager = dialog.findViewById(R.id.viewpage)
        pager.adapter = adapter
        pager.startAutoScroll()
        val indicator: CircleIndicator = view.findViewById(R.id.indicator)
        indicator.setViewPager(pager)
    }
}