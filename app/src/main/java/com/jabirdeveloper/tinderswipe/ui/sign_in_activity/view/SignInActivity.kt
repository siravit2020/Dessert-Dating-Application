package com.jabirdeveloper.tinderswipe.ui.sign_in_activity.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jabirdeveloper.tinderswipe.BandUser
import com.jabirdeveloper.tinderswipe.Functions.ChangLanguage
import com.jabirdeveloper.tinderswipe.Functions.LoadingDialog
import com.jabirdeveloper.tinderswipe.LoginWithEmailActivity
import com.jabirdeveloper.tinderswipe.R
import com.jabirdeveloper.tinderswipe.Register.PhoneActivity
import com.jabirdeveloper.tinderswipe.Register.Regis_name_Activity
import com.jabirdeveloper.tinderswipe.Register.RegistrationActivity
import com.jabirdeveloper.tinderswipe.MainActivity
import com.jabirdeveloper.tinderswipe.data.api.Resource
import com.jabirdeveloper.tinderswipe.services.TransparentStatusBar
import com.jabirdeveloper.tinderswipe.ui.sign_in_activity.view_model.SignInViewModel
import com.jabirdeveloper.tinderswipe.utils.Status
import java.util.*

class SignInActivity : AppCompatActivity() {
    private lateinit var mLogin: Button
    private lateinit var mRegister: Button
    private lateinit var mCallbackManager: CallbackManager
    private lateinit var mAuth: FirebaseAuth
    private val localizationDelegate = LocalizationActivityDelegate(this)
    private val language: ChangLanguage = ChangLanguage(this)
    private lateinit var thai: TextView
    private lateinit var eng: TextView
    private lateinit var face: LinearLayout
    private lateinit var google: LinearLayout
    private lateinit var mPhone: LinearLayout
    private lateinit var dialog: Dialog
    private lateinit var signInViewModel: SignInViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        TransparentStatusBar(this)
        super.onCreate(savedInstanceState)
        language.setLanguage()
        setContentView(R.layout.activity_sign_in)
        findViewById<TextView>(R.id.clickToTest).setOnClickListener { findViewById<LinearLayout>(R.id.testlogin).visibility = View.VISIBLE; findViewById<TextView>(R.id.clickToTest).visibility = View.GONE }
        mAuth = FirebaseAuth.getInstance()
        thai = findViewById(R.id.thai_lang)
        eng = findViewById(R.id.eng_lang)
        mLogin = findViewById(R.id.button3)
        mRegister = findViewById(R.id.button4)
        mPhone = findViewById(R.id.button7)
        face = findViewById(R.id.face)
        google = findViewById(R.id.google)
        dialog = LoadingDialog(this).dialog()
        if (localizationDelegate.getLanguage(this).toLanguageTag() == "th") {
            thai.setTextColor(ContextCompat.getColor(applicationContext, R.color.c4))
            eng.setTextColor(ContextCompat.getColor(applicationContext, R.color.c4tran))
        } else {
            thai.setTextColor(ContextCompat.getColor(applicationContext, R.color.c4tran))
            eng.setTextColor(ContextCompat.getColor(applicationContext, R.color.c4))
        }
        signInViewModel = ViewModelProvider(this).get(SignInViewModel::class.java)
        signInViewModel.getResource().observe(this, androidx.lifecycle.Observer {
            if (it.status === Status.SUCCESS) {
                when (it.data) {
                    "blackList" -> {
                        val intent = Intent(this@SignInActivity, BandUser::class.java)
                        startActivity(intent)
                    }
                    "main" -> {
                        val intent = Intent(this@SignInActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent.putExtra("first", "0")
                        startActivity(intent)
                        finish()
                    }
                    "register" -> {
                        val intent = Intent(this@SignInActivity, Regis_name_Activity::class.java)
                        intent.putExtra("Type", "face")
                        startActivity(intent)
                    }

                }
            } else {
                if (it.data === "face") {
                    Snackbar.make(face, it.message!!, Snackbar.LENGTH_SHORT).show();
                } else Snackbar.make(google, it.message!!, Snackbar.LENGTH_SHORT).show();
            }
        })
        signInViewModel.getStatusDialog().observe(this, androidx.lifecycle.Observer {
            if(it)
                dialog.show()
            else dialog.dismiss()
        })

        mCallbackManager = CallbackManager.Factory.create()
        face.setOnClickListener {
            signInViewModel.facebookSigIn(this@SignInActivity,mCallbackManager)
        }

        google.setOnClickListener {
            signInViewModel.googleSignIn(this@SignInActivity)
        }
        mLogin.setOnClickListener {
            val intent = Intent(this@SignInActivity, LoginWithEmailActivity::class.java)
            startActivity(intent)
        }
        mRegister.setOnClickListener {
            val intent = Intent(this@SignInActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }
        mPhone.setOnClickListener {
            val intent = Intent(this@SignInActivity, PhoneActivity::class.java)
            startActivity(intent)
        }
        thai.setOnClickListener {
            localizationDelegate.setLanguage(this, "th")
            language.setLanguage()
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
        eng.setOnClickListener {
            localizationDelegate.setLanguage(this, "en")
            language.setLanguage()
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data)
        signInViewModel.result(requestCode, data,this)
        super.onActivityResult(requestCode, resultCode, data)
    }


}