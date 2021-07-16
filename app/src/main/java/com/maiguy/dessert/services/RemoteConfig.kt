package com.maiguy.dessert.services

import android.app.Activity
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.maiguy.dessert.R
import com.maiguy.dessert.utils.GlobalVariable

class RemoteConfig(private var activity: Activity) {
    fun remote(){
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {

            minimumFetchIntervalInSeconds = 600
        }
        remoteConfig.setDefaultsAsync(R.xml.remote_config)
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    GlobalVariable.feedbackOn = remoteConfig.getString("feedback").toBoolean()
                    GlobalVariable.priceVip = remoteConfig.getString("price_vip").toInt()
                    GlobalVariable.priceLike = remoteConfig.getString("price_like").toInt()
                    GlobalVariable.idAds = remoteConfig.getString("id_ads")
                }
            }
    }
}