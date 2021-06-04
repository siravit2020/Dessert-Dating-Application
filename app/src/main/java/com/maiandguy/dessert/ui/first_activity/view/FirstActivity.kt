package com.maiandguy.dessert.ui.first_activity.view

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.*
import com.maiandguy.dessert.ui.sign_in_activity.view.SignInActivity
import com.maiandguy.dessert.R
import com.maiandguy.dessert.ui.show_gps_open.view.ShowGpsOpen
import com.maiandguy.dessert.MainActivity
import com.maiandguy.dessert.services.LocationService
import com.maiandguy.dessert.ui.first_activity.view_model.FirstViewModel
import com.maiandguy.constants.CheckStatusUser
import com.maiandguy.constants.Status
import kotlinx.android.synthetic.main.activity_first_.*

class FirstActivity : AppCompatActivity() {

    private lateinit var aniFade: Animation
    private lateinit var aniFade2: Animation
    private lateinit var firstViewModel: FirstViewModel
    private lateinit var locationService:LocationService;
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_)
        //TransparentStatusBar(this)
        MobileAds.initialize(this) {}
        locationService = LocationService(this)
        setAnimation()
        firstViewModel = ViewModelProvider(this).get(FirstViewModel::class.java)
        if (locationService.checkPermission() == Status.SUCCESS){
            firstViewModel.addListener()
        }
        firstViewModel.getStatus().observe(this, Observer {
            Log.d("test observe",it.name)
            aniFade.setAnimationListener(null)
            aniFade2.setAnimationListener(null)
            if(it == CheckStatusUser.CHOOSE){
                val intent = Intent(this@FirstActivity, SignInActivity::class.java)
                startActivity(intent)
            }
            if(it == CheckStatusUser.SWITCH){
                val intent = Intent(this@FirstActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
        firstViewModel.getCheckUser().observe(this, Observer {
            Log.d("test observe",it.toString())
            if(!it){
                aniFade.setAnimationListener(null)
                aniFade2.setAnimationListener(null)
                val intent = Intent(this@FirstActivity, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        firstViewModel.getAnimationStart().observe(this, Observer {
            logo.startAnimation(aniFade)
        })

    }

    private fun setAnimation() {
        aniFade = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out)
        aniFade2 = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
        aniFade.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                logo.startAnimation(aniFade2)
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }
        })
        aniFade2.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                logo.startAnimation(aniFade)
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }
        })
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            recreate()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recreate()
            } else {
                val intent = Intent(this@FirstActivity, ShowGpsOpen::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("2", "2")
                finish()
                startActivity(intent)
            }
        }
    }


}
