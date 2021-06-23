package com.maiguy.dessert.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

class RelativeMaxWidth @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    private var flagWidth:Int = 0
    fun onSetIndex(index:Int) {
        flagWidth = when(index) {
            0 -> 0
            1 -> 1
            else -> 0
        }
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = (context.resources.displayMetrics.widthPixels - dpToPx(100))
        if(flagWidth == 1)  width = (context.resources.displayMetrics.widthPixels - dpToPx(115))
        val widthMaxSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST)
        super.onMeasure(widthMaxSpec, heightMeasureSpec)
    }

    private fun dpToPx(px:Int) : Int {
        return (px * context.resources.displayMetrics.density).toInt()
    }
}