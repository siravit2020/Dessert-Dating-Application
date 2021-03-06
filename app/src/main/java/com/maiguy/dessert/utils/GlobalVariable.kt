package com.maiguy.dessert.utils

import android.app.Application

class GlobalVariable : Application(){
    companion object{
        lateinit var image: String
        lateinit var y: String
        lateinit var x: String
        lateinit var distance: String
        lateinit var oppositeUserSex: String
        lateinit var idAds:String
        var oppositeUserAgeMax: Int = 0
        var maxStar: Int = 0
        var oppositeUserAgeMin: Int = 0
        var maxAdmob: Int = 0
        var maxChat: Int = 0
        var maxLike: Int = 0
        var age: Int = 0
        var name: String = ""
        var buyLike: Boolean = false
        var seeYou: Int = 0
        var vip = false
        var likeYou = 0
        var feedbackOn = false
        var feedbackResult = false
        var priceVip = 79
        var priceLike = 29
        var countMatch = 0
        var outOfQuestion = false
    }
}