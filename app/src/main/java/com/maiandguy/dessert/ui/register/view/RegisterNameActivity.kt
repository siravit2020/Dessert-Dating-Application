package com.maiandguy.dessert.ui.register.view

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import com.google.firebase.auth.FirebaseAuth
import com.maiandguy.dessert.R
import com.tapadoo.alerter.Alerter


class RegisterNameActivity : AppCompatActivity() {
    private lateinit var b1: Button
    private lateinit var t1: EditText
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regis_name_)
        b1 = findViewById(R.id.button_name)
        t1 = findViewById(R.id.editText5)
        toolbar = findViewById(R.id.my_tools)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.register)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        b1.setOnClickListener {
            if (t1.text.toString().trim { it <= ' ' }.isNotEmpty()) {
                Alerter.hide()
                val intent = Intent(this@RegisterNameActivity, RegisterGpsActivity::class.java)
                intent.apply {
                    putExtra("Name", t1.text.toString())
                    putExtra("Type", getIntent().getStringExtra("Type"))
                    putExtra("email", getIntent().getStringExtra("email"))
                    putExtra("password", getIntent().getStringExtra("password"))
                }
                startActivity(intent)
            } else {
                Alerter.create(this@RegisterNameActivity)
                        .setTitle(R.string.Noti)
                        .setText(getString(R.string.enter_name))
                        .setBackgroundColorRes(R.color.c3)
                        .show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
       FirebaseAuth.getInstance().signOut()
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
}