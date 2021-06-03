package com.maiandguy.dessert

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.maiandguy.dessert.ui.sign_in_activity.view.SignInActivity

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