package com.jabirdeveloper.tinderswipe.Functions

import java.text.SimpleDateFormat
import java.util.*

class TimeStampToDate(private val time:Long) {
    private val currentTime = SimpleDateFormat("HH:mm", Locale.UK)
    private val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.UK)
    fun date(): String {
        return currentDate.format(time)
    }
    fun time(): String {
        return currentTime.format(time)
    }
    fun getCurrentTime(): String {
        return currentDate.format(Calendar.getInstance().time)
    }
}