package com.maiandguy.dessert.model

data class UserModel (
        val age: Long,
        val distance: String,
        val location: Location,
        val maxAdmob: Long,
        val maxChat: Long,
        val maxLike: Long,
        val maxStar: Long,
        val oppositeUserAgeMax: Long,
        val oppositeUserAgeMin: Long,
        val oppositeUserSex: String,
        val profileImage: ProfileImage,
        val questions: Map<String, Question>,
        val vip: Long,
        val birth: Long,
        val career: String,
        val connection: Connection,
        val date: Long,
        val hobby: Hobby,
        val language: Language,
        val myself: String,
        val name: String,
        val religion: Long,
        val seeProfile: Map<String, SeeProfile>,
        val sex: String,
        val starS: StarS,
        val status: Long,
        val study: String,
        val token: String
)

data class Connection (
        val chatna: Chatna,
        val matches: Matches,
        val nope: Nope,
        val yep: Yep
)

data class Chatna (
        val the0LP6VdpR92SsPfdZ4U4CHFVa4T53: String
)

data class Matches (
        val the3At4MCitPuT9XQSRmd4BjUQY3CF3: The3At4MCitPuT9XQSRmd4BjUQY3CF3
)

data class The3At4MCitPuT9XQSRmd4BjUQY3CF3 (
        val chatID: String
)

data class Nope (
        val fmuN0TYDCIUJeYvb6WEPK4Hsodl1: Boolean
)

data class Yep (
        val the0LP6VdpR92SsPfdZ4U4CHFVa4T53: SeeProfile,
        val the4LlRDIJVQTV98ODR0ASj06BDIUt1: The4LlRDIJVQTV98ODR0ASj06BDIUt1
)

data class SeeProfile (
        val date: Long
)

data class The4LlRDIJVQTV98ODR0ASj06BDIUt1 (
        val date: Long,
        val the4LlRDIJVQTV98ODR0ASj06BDIUt1Super: Boolean
)

data class Hobby (
        val hobby0: Long,
        val hobby1: Long,
        val hobby2: Long,
        val hobby3: Long
)

data class Language (
        val language0: Long,
        val language1: Long
)

data class Location (
        val x: Double,
        val y: Double
)

data class ProfileImage (
        val profileImageUrl0: String,
        val profileImageUrl1: String
)

data class Question (
        val id: String,
        val question: Long,
        val weight: Long
)

data class StarS (
        val heavyzebra142: Boolean
)
