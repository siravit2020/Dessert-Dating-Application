package com.maiandguy.dessert

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.android.gms.ads.rewarded.RewardedAd

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.maiandguy.dessert.ui.first_activity.view.FirstActivity

class LoginWithEmailActivity : AppCompatActivity() {
    private lateinit var mLogin: Button
    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseAuthStateListener: AuthStateListener

    private lateinit var rewardedAd: RewardedAd
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_with_email)

        mAuth = FirebaseAuth.getInstance()
        mLogin = findViewById(R.id.BLogin)
        mEmail = findViewById(R.id.Edit_L_email)
        mPassword = findViewById(R.id.Edit_L_password)
        firebaseAuthStateListener = AuthStateListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val intent = Intent(this@LoginWithEmailActivity, FirstActivity::class.java)
                startActivity(intent)
                finish()
                return@AuthStateListener
            }
        }
        mLogin.setOnClickListener {

            val email = mEmail.text.toString()
            val password = mPassword.text.toString()
            if (email.trim { it <= ' ' } != "" && password.trim { it <= ' ' } != "") {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this@LoginWithEmailActivity) { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(this@LoginWithEmailActivity, "สวย", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }



    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(firebaseAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        mAuth.removeAuthStateListener(firebaseAuthStateListener)
    }
}