package com.maiandguy.dessert.ui.register.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.ktx.auth

import com.google.firebase.ktx.Firebase
import com.maiandguy.dessert.R
import com.maiandguy.dessert.ui.sign_in_activity.view.SignInActivity
import kotlinx.android.synthetic.main.activity_send_verification.*


class SendVerificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_verification)
        val user = Firebase.auth.currentUser
        val button: Button = findViewById(R.id.button_back)
        if(intent.hasExtra("forgot")){
            text.text = "ระบบได้ส่งลิงค์สำหรับรีเซ็ตรหัสผ่านไปที่อีเมลของท่านแล้ว"
        }
        if(intent.hasExtra("register")){
            user!!.sendEmailVerification()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("email send", "Email sent.")
                            button.text = "กลับสู่หน้าเข้าสู่ระบบ"
                            button.isEnabled = true
                            Firebase.auth.signOut()
                        }
                    }
        }
        else{
            button.text = "กลับสู่หน้าเข้าสู่ระบบ"
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