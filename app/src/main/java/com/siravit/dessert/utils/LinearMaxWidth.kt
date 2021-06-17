package com.siravit.dessert.utils

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.RelativeLayout

class LinearMaxWidth @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    private var flagWidth:Int = 0
    fun onsetIndex(index:Int) {
        flagWidth = when(index) {
            0 -> 0
            1 -> 1
            else -> 0
        }
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = (context.resources.displayMetrics.widthPixels * 0.80).toInt()
        if(flagWidth == 1)  width = (context.resources.displayMetrics.widthPixels * 0.65).toInt()
        val widthMaxSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST)
        super.onMeasure(widthMaxSpec, heightMeasureSpec)
    }
}