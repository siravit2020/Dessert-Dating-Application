package com.siravit.dessert.activity.register.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.siravit.dessert.R
import com.siravit.dessert.dialogs.LoadingDialog


class RegistrationActivity : AppCompatActivity() {
    private lateinit var register: Button
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var mAuth: FirebaseAuth
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        toolbar = findViewById(R.id.my_tools)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.register)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mAuth = FirebaseAuth.getInstance()
        register = findViewById(R.id.finish_button)
        email = findViewById(R.id.email_edit_text)
        password = findViewById(R.id.password_edit_text)
        confirmPassword = findViewById(R.id.confirm_password_edit_text)
        val dialog = LoadingDialog(this).dialog()
        register.setOnClickListener(View.OnClickListener {
            val email = email.text.toString()
            val password = password.text.toString()
            val confirmPassword = confirmPassword.text.toString()
            if (email.trim().isEmpty() && password.trim().isEmpty() && confirmPassword.trim().isEmpty())
                Snackbar.make(this.email, getString(R.string.information_alert), Snackbar.LENGTH_SHORT).show().also { return@OnClickListener }
            else if (password != confirmPassword)
                Snackbar.make(this.email, getString(R.string.same_password), Snackbar.LENGTH_SHORT).show().also { return@OnClickListener }
            else if (!email.isValidEmail())
                Snackbar.make(this.email, getString(R.string.valid_email), Snackbar.LENGTH_SHORT).show().also { return@OnClickListener }
            dialog.show()
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        dialog.dismiss()
                        if (task.isSuccessful) {
                            val intent = Intent(this@RegistrationActivity, SendVerificationActivity::class.java)
                            intent.putExtra("register", true)
                            startActivity(intent)
                        } else {
                            Log.w("authen failed", "createUserWithEmail:failure", task.exception)
                            Snackbar.make(this.email, getString(R.string.try_again), Snackbar.LENGTH_SHORT).show()

                        }
                    }


        })
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