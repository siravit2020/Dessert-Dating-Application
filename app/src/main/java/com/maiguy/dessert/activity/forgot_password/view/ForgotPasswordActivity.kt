package com.maiguy.dessert.activity.forgot_password.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.maiguy.dessert.R
import com.maiguy.dessert.activity.register.view.SendVerificationActivity

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        toolbar = findViewById(R.id.my_tools)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.forgot_password)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val button:Button = findViewById(R.id.send_button)
        val editText:EditText = findViewById(R.id.email_edit_text)
        button.setOnClickListener {
            val email = editText.text.toString()
            if (!email.isValidEmail())
                Snackbar.make(editText, getString(R.string.valid_email), Snackbar.LENGTH_SHORT).show().also { return@setOnClickListener }
            Firebase.auth.sendPasswordResetEmail(editText.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            val intent = Intent(this@ForgotPasswordActivity, SendVerificationActivity::class.java)
                            intent.putExtra("forgot", true)
                            startActivity(intent)
                        }
                    }
        }

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}