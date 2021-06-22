package com.maiguy.dessert.activity.band_user.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.maiguy.dessert.R
import com.maiguy.dessert.activity.sign_in.view.SignInActivity

class BandUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_band_user)
        (findViewById<Button>(R.id.button_back)).setOnClickListener {

            startActivity(Intent(applicationContext, SignInActivity::class.java))
            finish()
        }
    }
}