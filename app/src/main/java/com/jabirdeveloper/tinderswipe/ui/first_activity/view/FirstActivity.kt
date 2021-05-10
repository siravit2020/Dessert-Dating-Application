package com.jabirdeveloper.tinderswipe.ui.first_activity.view

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.jabirdeveloper.tinderswipe.ChooseLoginRegistrationActivity
import com.jabirdeveloper.tinderswipe.R
import com.jabirdeveloper.tinderswipe.ShowGpsOpen
import com.jabirdeveloper.tinderswipe.SwitchpageActivity
import com.jabirdeveloper.tinderswipe.ui.first_activity.view_model.FirstViewModel
import com.jabirdeveloper.tinderswipe.utils.CheckStatusUser
import kotlinx.android.synthetic.main.activity_first_.*

class FirstActivity : AppCompatActivity() {

    private lateinit var aniFade: Animation
    private lateinit var aniFade2: Animation
    private lateinit var firstViewModel: FirstViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_)
        setAnimation()
        firstViewModel = ViewModelProvider(this).get(FirstViewModel::class.java)
        firstViewModel.getStatus().observe(this, Observer {
            Log.d("test observe",it.name)
            aniFade.setAnimationListener(null)
            aniFade2.setAnimationListener(null)
            if(it == CheckStatusUser.CHOOSE){
                val intent = Intent(this@FirstActivity, ChooseLoginRegistrationActivity::class.java)
                startActivity(intent)
            }
            if(it == CheckStatusUser.SWITCH){
                val intent = Intent(this@FirstActivity, SwitchpageActivity::class.java)
                startActivity(intent)
                finish()
            }

        })
        firstViewModel.getCheckUser().observe(this, Observer {
            Log.d("test observe",it.toString())
            if(!it){
                aniFade.setAnimationListener(null)
                aniFade2.setAnimationListener(null)
                val intent = Intent(this@FirstActivity, ChooseLoginRegistrationActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
        firstViewModel.getAniamtionStart().observe(this, Observer {
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
