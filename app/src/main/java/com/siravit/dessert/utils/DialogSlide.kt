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
        pagerModels.add(PagerModel("ปัดขวาได้เต็มที่ ไม่ต้องรอเวลา", "ถูกใจได้ไม่จำกัด", R.drawable.ic_heart))
        pagerModels.add(PagerModel("ทักทายคนที่คุณอยากทำความรู้จักได้ไม่จำกัดจำนวน", "ทักทายได้ไม่จำกัด", R.drawable.ic_hand))
        pagerModels.add(PagerModel("คนที่คุณส่งดาวให้จะเห็นคุณก่อนใคร", "รับ 5 Star ฟรีทุกวัน", R.drawable.ic_starss))
        pagerModels.add(PagerModel("ดูว่าใครบ้างที่เข้ามากดถูกใจให้คุณ", "ใครถูกใจคุณ", R.drawable.ic_love2))

        val adapter = VipSlideAdapter(context, pagerModels)
        val pager: AutoScrollViewPager = dialog.findViewById(R.id.viewpage)
        pager.adapter = adapter
        pager.startAutoScroll()
        val indicator: CircleIndicator = view.findViewById(R.id.indicator)
        indicator.setViewPager(pager)
    }
}