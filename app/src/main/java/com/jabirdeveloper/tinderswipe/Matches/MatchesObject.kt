package com.jabirdeveloper.tinderswipe.Matches

import android.graphics.Bitmap

data class MatchesObject(var userId: String?,
                         var name: String?,
                         var profileImageUrl: String?,
                         var status: String?,
                         var late: String?,
                         var time: Long?,
                         var count_unread: Int, )