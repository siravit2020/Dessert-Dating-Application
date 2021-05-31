package com.jabirdeveloper.tinderswipe.ui.sign_in_activity.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate
import com.facebook.CallbackManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.jabirdeveloper.tinderswipe.BandUser
import com.jabirdeveloper.tinderswipe.Functions.ChangLanguage
import com.jabirdeveloper.tinderswipe.Functions.LoadingDialog
import com.jabirdeveloper.tinderswipe.MainActivity
import com.jabirdeveloper.tinderswipe.R
import com.jabirdeveloper.tinderswipe.ui.register.PhoneActivity
import com.jabirdeveloper.tinderswipe.ui.register.view.RegisterNameActivity
import com.jabirdeveloper.tinderswipe.ui.sign_in_activity.view_model.SignInViewModel
import com.jabirdeveloper.tinderswipe.ui.register.view.RegistrationActivity
import com.jabirdeveloper.tinderswipe.utils.Status
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
    private val language: ChangLanguage = ChangLanguage(this)
    private lateinit var thai: TextView
    private lateinit var eng: TextView

    private lateinit var google: LinearLayout
    private lateinit var phoneButton: LinearLayout
    private lateinit var dialog: Dialog
    private lateinit var signInViewModel: SignInViewModel
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        language.setLanguage()
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
                        val intent = Intent(this@SignInActivity, RegisterNameActivity::class.java)
                        intent.putExtra("Type", "face")
                        startActivity(intent)
                    }
                }
            } else {
                if (it.data === "face") {
                    // Snackbar.make(face, it.message!!, Snackbar.LENGTH_SHORT).show();
                } else Snackbar.make(google, it.message!!, Snackbar.LENGTH_SHORT).show();
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

        }
        registerButton.setOnClickListener {
            val intent = Intent(this@SignInActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }
        phoneButton.setOnClickListener {
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
        signInViewModel.result(requestCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }


}