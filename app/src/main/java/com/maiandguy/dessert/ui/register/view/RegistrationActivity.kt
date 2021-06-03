package com.maiandguy.dessert.ui.register.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.maiandguy.dessert.CardActivity
import com.maiandguy.dessert.R

class RegistrationActivity : AppCompatActivity() {
    private lateinit var mRegister: Button
    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseAuthStateListener: AuthStateListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        mAuth = FirebaseAuth.getInstance()
        firebaseAuthStateListener = AuthStateListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val intent = Intent(this@RegistrationActivity, CardActivity::class.java)
                startActivity(intent)
                finish()
                return@AuthStateListener
            }
        }
        mRegister = findViewById(R.id.finish_button)
        mEmail = findViewById(R.id.email_edit_text)
        mPassword = findViewById(R.id.password_edit_text)
        mRegister.setOnClickListener(View.OnClickListener {
            val email = mEmail.text.toString()
            val password = mPassword.text.toString()
            if (email.trim { it <= ' ' } != "" && password.trim { it <= ' ' } != "") {
                mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val check = task.result!!.signInMethods!!.isEmpty()
                        if (check) {
                            val intent = Intent(this@RegistrationActivity, RegisterNameActivity::class.java)
                            intent.putExtra("Type", "email")
                            intent.putExtra("email", mEmail.text.toString())
                            intent.putExtra("password", mPassword.text.toString())
                            startActivity(intent)
                            return@OnCompleteListener
                        } else Toast.makeText(this@RegistrationActivity, "มีแล้ว", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@RegistrationActivity, "กรอกให้ถูกต้อง", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        })
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