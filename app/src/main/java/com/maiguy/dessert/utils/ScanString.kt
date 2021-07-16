package com.maiguy.dessert.utils

import java.util.*

class ScanString {
    companion object {
        fun scan(text:String): Boolean {
            val words = listOf("ควย","ค วย","ค ว ย","คว ย","หี","เย็ด","เ ย็ด","เย็ ด","เ ย็ ด","เยด","เ ยด","เย ด","เ ย ด","เย","เ ย",
                "แตด","แ ตด","แ ต ด","แต ด","จู๋","รับงาน","จิ๋ม","เสียบ","มิดด้าม","หำ","ขายตัว","เงี่ยน","เ งี่ยน","เงี ่ยน","เงี่ย น","เ งี่ ยน","เ งี ่ย น","เงี่ ย น","pussy","dick","fuck")
            words.forEach {
                val boo:Boolean = text.toLowerCase(Locale.ROOT).contains(it)
                print(boo)
                if(boo) return false
            }
            return true
        }
    }
}
