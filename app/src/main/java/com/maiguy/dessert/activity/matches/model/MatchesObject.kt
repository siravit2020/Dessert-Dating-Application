package com.maiguy.dessert.activity.matches.model

data class MatchesObject(var userId: String?,
                         var name: String?,
                         var profileImageUrl: String?,
                         var status: Boolean?,
                         var late: String?,
                         var time: Long?,
                         var count_unread: Int, )