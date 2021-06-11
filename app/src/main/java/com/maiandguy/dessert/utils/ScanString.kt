package com.maiandguy.dessert.utils

import java.util.*

class ScanString {
    companion object {
        fun scan(text:String): Boolean {
            val words = listOf("ควย","หี","เย็ด","เยด","แตด","จู๋","รับงาน","จิ๋ม","เสียบ","มิดด้าม","หำ","ขายตัว","เงี่ยน","pussy","dick","fuck")
            words.forEach {
                val boo:Boolean = text.toLowerCase(Locale.ROOT).contains(it)
                print(boo)
                if(boo) return false
            }
            return true
        }
    }
}
