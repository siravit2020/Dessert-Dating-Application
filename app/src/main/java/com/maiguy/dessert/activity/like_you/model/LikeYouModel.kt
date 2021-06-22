package com.maiguy.dessert.activity.LikeYou

data class LikeYouModel(val userId: String?,
                        val profileImageUrl: String?,
                        val name: String?,
                        val status: String?,
                        val Age: String?,
                        val gender: String?,
                        val myself: String?,
                        val distance: Double,
                        val city: String?,
                        val time: Long)

data class DateModel(val key: String,
                     val time: Long)