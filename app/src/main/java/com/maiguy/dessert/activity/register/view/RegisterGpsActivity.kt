package com.maiguy.dessert.activity.register.view

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.maiguy.dessert.activity.sign_in.view.SignInActivity
import com.maiguy.dessert.R
import com.maiguy.dessert.constants.Status
import com.maiguy.dessert.services.LocationService

class RegisterGpsActivity : AppCompatActivity(), LocationListener {
    private lateinit var mGPSDialog: Dialog
    private lateinit var mLocationManager: LocationManager
    private lateinit var imageView: ImageView
    private lateinit var anime1: ImageView
    private lateinit var anime2: ImageView
    private lateinit var handler: Handler
    private var chk = 0
    private lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regis_gps)
        handler = Handler(Looper.getMainLooper())
        imageView = findViewById(R.id.imageView3)
        anime1 = findViewById(R.id.anime1)
        anime2 = findViewById(R.id.anime2)
        runnable!!.run()
        toolbar = findViewById(R.id.my_tools)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.register)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mLocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (LocationService(this).checkPermission() == Status.SUCCESS){
            val location =
                    mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if(location != null) {
                getLocation(location)
            }else {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
            }
        }

    }

    private fun getLocation(location: Location) {
        val intent = Intent(this@RegisterGpsActivity, RegisterSexActivity::class.java)
        intent.putExtra("X", location.latitude)
        intent.putExtra("Y", location.longitude)
        intent.putExtra("Name", getIntent().getStringExtra("Name"))
        intent.putExtra("email", getIntent().getStringExtra("email"))
        intent.putExtra("password", getIntent().getStringExtra("password"))
        startActivity(intent)
    }

    override fun onLocationChanged(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude
        if (chk == 0) {
            chk = -1
            val intent = Intent(this@RegisterGpsActivity, RegisterSexActivity::class.java)
            intent.putExtra("X", latitude)
            intent.putExtra("Y", longitude)
            intent.putExtra("Name", getIntent().getStringExtra("Name"))
            intent.putExtra("Type", getIntent().getStringExtra("Type"))
            intent.putExtra("email", getIntent().getStringExtra("email"))
            intent.putExtra("password", getIntent().getStringExtra("password"))
            startActivity(intent)
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recreate()
            } else {
                startActivity(Intent(this@RegisterGpsActivity, SignInActivity::class.java))
            }
        }
    }

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            anime1.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(900).withEndAction {
                anime1.scaleX = 1f
                anime1.scaleY = 1f
                anime1.alpha = 1f
            }
            anime2.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(1400).withEndAction {
                anime2.scaleX = 1f
                anime2.scaleY = 1f
                anime2.alpha = 1f
            }
            handler.postDelayed(this, 1500)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}