package com.maiguy.dessert.activity.register.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth

import com.google.firebase.ktx.Firebase
import com.maiguy.dessert.R
import com.maiguy.dessert.activity.sign_in.view.SignInActivity
import kotlinx.android.synthetic.main.activity_send_verification.*


class SendVerificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_verification)
        val user = Firebase.auth.currentUser
        val button: Button = findViewById(R.id.button_back)
        if(intent.hasExtra("forgot")){
            text.text = getString(R.string.reset_password_2)
        }
        if(intent.hasExtra("register")){
            user!!.sendEmailVerification()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("email send", "Email sent.")
                            button.text = getString(R.string.go_to_sign_in)
                            button.isEnabled = true
                            Firebase.auth.signOut()
                        }
                    }
        }
        else{
            button.text = getString(R.string.go_to_sign_in)
            button.isEnabled = true
        }
        button.setOnClickListener {
            if(!intent.hasExtra("login")){
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
            }
            else{
                onBackPressed()
            }

        }

    }
}