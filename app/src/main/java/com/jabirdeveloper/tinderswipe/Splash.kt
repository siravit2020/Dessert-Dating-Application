package com.jabirdeveloper.tinderswipe

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.jabirdeveloper.tinderswipe.ui.first_activity.view.FirstActivity

class Splash  : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler().postDelayed({
            startActivity(Intent(this, FirstActivity::class.java))
            finishAffinity()
        }, 2000)
    }
}