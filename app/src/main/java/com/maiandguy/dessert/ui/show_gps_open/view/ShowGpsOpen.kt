package com.maiandguy.dessert.ui.show_gps_open.view

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.maiandguy.constants.Status
import com.maiandguy.dessert.R
import com.maiandguy.dessert.services.LocationService
import com.maiandguy.dessert.ui.first_activity.view.FirstActivity

class ShowGpsOpen : AppCompatActivity() {
    private lateinit var open: Button
    private lateinit var mLocationManager: LocationManager
    private lateinit var textView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_gps_open)
        open = findViewById(R.id.open)
        textView = findViewById(R.id.text_gps)
        mLocationManager = this@ShowGpsOpen.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        open.setOnClickListener{
            if (LocationService(this).checkPermission(true) == Status.SUCCESS){
                val intent = Intent(this@ShowGpsOpen, FirstActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                finish()
                startActivity(intent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("onside","1")
        if (requestCode == 0) {
            val intent = Intent(this@ShowGpsOpen, FirstActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            finish()
            startActivity(intent)
        }
    }

}