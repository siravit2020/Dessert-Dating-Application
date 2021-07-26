package com.maiguy.dessert.activity.sign_in.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate
import com.facebook.CallbackManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.maiguy.dessert.activity.band_user.view.BandUser
import com.maiguy.dessert.utils.ChangLanguage
import com.maiguy.dessert.dialogs.LoadingDialog
import com.maiguy.dessert.activity.main.view.MainActivity
import com.maiguy.dessert.R
import com.maiguy.dessert.activity.forgot_password.view.ForgotPasswordActivity
import com.maiguy.dessert.activity.phone.view.PhoneActivity
import com.maiguy.dessert.activity.register.view.RegisterNameActivity
import com.maiguy.dessert.activity.sign_in.view_model.SignInViewModel
import com.maiguy.dessert.activity.register.view.RegistrationActivity
import com.maiguy.dessert.activity.register.view.SendVerificationActivity
import com.maiguy.dessert.constants.Status
import java.util.*


class SignInActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: TextView
    private lateinit var forgotButton: TextView
    private lateinit var mCallbackManager: CallbackManager
    private lateinit var mAuth: FirebaseAuth
    private val localizationDelegate = LocalizationActivityDelegate(this)

    private lateinit var thai: TextView
    private lateinit var eng: TextView
    private lateinit var facebook: LinearLayout
    private lateinit var google: LinearLayout
    private lateinit var phoneButton: LinearLayout
    private lateinit var dialog: Dialog
    private lateinit var signInViewModel: SignInViewModel
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        //TransparentStatusBar(this)

        mAuth = FirebaseAuth.getInstance()
        thai = findViewById(R.id.thai_lang)
        eng = findViewById(R.id.eng_lang)
        emailEditText = findViewById(R.id.email_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        loginButton = findViewById(R.id.login_button)
        registerButton = findViewById(R.id.register_button)
        forgotButton = findViewById(R.id.forgot_button)
        phoneButton = findViewById(R.id.button7)
        google = findViewById(R.id.google)
        facebook = findViewById(R.id.facebook_login)
        dialog = LoadingDialog(this).dialog()
        val sharedPref = this.getSharedPreferences(
            "language", MODE_PRIVATE)
        when {
            !sharedPref.contains("language") -> {
                localizationDelegate.setLanguage(this,"th")
            }
            localizationDelegate.getLanguage(this).toLanguageTag() == "th" -> {
                thai.setTextColor(ContextCompat.getColor(applicationContext, R.color.c4))
                eng.setTextColor(ContextCompat.getColor(applicationContext, R.color.c4tran))
            }
            else -> {
                thai.setTextColor(ContextCompat.getColor(applicationContext, R.color.c4tran))
                eng.setTextColor(ContextCompat.getColor(applicationContext, R.color.c4))
            }
        }
        signInViewModel = ViewModelProvider(this).get(SignInViewModel::class.java)
        signInViewModel.setLanguage(this)
        signInViewModel.getResource().observe(this, androidx.lifecycle.Observer {
            if (it.status === Status.SUCCESS) {
                Log.d("result",it.data.toString())
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
                        val intent = Intent(this@SignInActivity, RegisterNameActivity::class.java)
                        intent.putExtra("Type", "face")
                        startActivity(intent)
                    }
                    "verification" -> {
                        val intent = Intent(this@SignInActivity, SendVerificationActivity::class.java)
                        intent.putExtra("login", true)
                        startActivity(intent)
                    }

                }
            } else {
                Snackbar.make(google, getString( signInViewModel.getError()), Snackbar.LENGTH_SHORT).show();
            }
        })
        signInViewModel.getStatusDialog().observe(this, androidx.lifecycle.Observer {
            if (it)
                dialog.show()
            else dialog.dismiss()
        })

        mCallbackManager = CallbackManager.Factory.create()


        google.setOnClickListener {
            signInViewModel.googleSignIn(this@SignInActivity)
        }
        loginButton.setOnClickListener {
            signInViewModel.authenticationWithEmail(emailEditText.text.toString(), passwordEditText.text.toString())
        }
        forgotButton.setOnClickListener {
            val intent = Intent(this@SignInActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
        registerButton.setOnClickListener {
            val intent = Intent(this@SignInActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }
        phoneButton.setOnClickListener {
            val intent = Intent(this@SignInActivity, PhoneActivity::class.java)
            startActivity(intent)
        }
        facebook.setOnClickListener {
            signInViewModel.facebookSigIn(this,mCallbackManager)
        }
        thai.setOnClickListener {
            localizationDelegate.setLanguage(this, "th")
            val sharedPref = this.getPreferences(MODE_PRIVATE)
            if(sharedPref != null){
                with (sharedPref.edit()) {
                    putString("language", "th")
                    apply()
                }
            }

            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
        eng.setOnClickListener {
            localizationDelegate.setLanguage(this, "en")
            val sharedPref = this.getPreferences(MODE_PRIVATE)
            if(sharedPref != null){
                with (sharedPref.edit()) {
                    putString("language", "en")
                    apply()
                }
            }
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data)
        signInViewModel.result(requestCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }


}