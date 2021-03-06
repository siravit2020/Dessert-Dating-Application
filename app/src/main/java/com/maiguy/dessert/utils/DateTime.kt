package com.maiguy.dessert.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTime {
    private val calendar = Calendar.getInstance()
    private val currentTime = SimpleDateFormat("HH:mm", Locale.UK)
    private val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.UK)
    fun date(): String {
        return currentDate.format(calendar.time)
    }

    fun time(): String {
        return currentTime.format(calendar.time)
    }
}