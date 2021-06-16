package com.siravit.dessert.activity.band_user.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.siravit.dessert.R
import com.siravit.dessert.activity.sign_in.view.SignInActivity

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