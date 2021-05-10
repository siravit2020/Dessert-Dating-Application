package com.jabirdeveloper.tinderswipe

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_first_.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class First_Activity : AppCompatActivity() {
    private lateinit var firebaseAuthStateListener: AuthStateListener
    private lateinit var mAuth: FirebaseAuth
    private lateinit var usersDb: DatabaseReference
    private lateinit var mContext: Context
    private lateinit var aniFade: Animation
    private lateinit var aniFade2: Animation
    private lateinit var mLocationManager: LocationManager
    override fun onCreate(savedInstanceState: Bundle?) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        usersDb = FirebaseDatabase.getInstance().reference.child("Users")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_)
        setAnimation()
        mContext = applicationContext
        mAuth = FirebaseAuth.getInstance()

        CoroutineScope(Job()).launch(Dispatchers.Unconfined) {
            firebaseAuthStateListener = AuthStateListener {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {

                    logo.startAnimation(aniFade)
                    usersDb.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.child(mAuth.currentUser!!.uid).hasChild("sex")) {
                               
                                pushToken()

                            } else {
                                mAuth.signOut()
                                val intent = Intent(this@First_Activity, ChooseLoginRegistrationActivity::class.java)
                                startActivity(intent)
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
                } else {
                    val intent = Intent(this@First_Activity, ChooseLoginRegistrationActivity::class.java)
                    startActivity(intent)
                    finish()
                    return@AuthStateListener
                }


            }
            mLocationManager = this@First_Activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showGPSDisabledDialog()
            } else if (ActivityCompat.checkSelfPermission(this@First_Activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this@First_Activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@First_Activity, arrayOf<String?>(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET
                ), 1)
            } else mAuth.addAuthStateListener(firebaseAuthStateListener)

        }

    }

    private fun pushToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            FirebaseDatabase.getInstance().reference.child("Users").child(mAuth.currentUser!!.uid).child("token").setValue(token)
            val intent = Intent(this@First_Activity, SwitchpageActivity::class.java)
            startActivity(intent)
            finish()
        })
    }

    private fun showGPSDisabledDialog() {
        val builder = AlertDialog.Builder(this@First_Activity)
        builder.setTitle(R.string.GPS_Disabled)
        builder.setMessage(R.string.GPS_open)
        builder.setPositiveButton(R.string.open_gps) { _, _ -> startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0) }.setNegativeButton(R.string.report_close) { dialog, which ->
            val intent = Intent(this@First_Activity, ShowGpsOpen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            finish()
            startActivity(intent)
        }
        val mGPSDialog: Dialog = builder.create()
        mGPSDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(this@First_Activity, R.drawable.myrect2))
        mGPSDialog.show()
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

    override fun onStop() {
        super.onStop()
        mAuth.removeAuthStateListener(firebaseAuthStateListener)
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
                val intent = Intent(this@First_Activity, ShowGpsOpen::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("2", "2")
                finish()
                startActivity(intent)
            }
        }
    }


}
