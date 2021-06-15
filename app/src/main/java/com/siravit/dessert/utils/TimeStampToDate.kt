package com.siravit.dessert.utils

import java.text.SimpleDateFormat
import java.util.*

class TimeStampToDate(private val time:Long) {
    private val currentTime = SimpleDateFormat("HH:mm", Locale.UK)
    private val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.UK)
    private val currentDateWOYear = SimpleDateFormat("dd/MM", Locale.UK)
    fun date(): String {
        return currentDate.format(time)
    }
    fun time(): String {
        return currentTime.format(time)
    }
    fun dateWOYear(): String {
        return  currentDateWOYear.format(time)
    }
    fun getCurrentTime(): String {
        return currentDate.format(Calendar.getInstance().time)
    }
}