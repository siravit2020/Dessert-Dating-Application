package com.jabirdeveloper.tinderswipe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.jabirdeveloper.tinderswipe.Functions.CloseDialog

class SendProblem : AppCompatActivity() {
    private lateinit var editText: EditText
    private lateinit var button: Button
    private lateinit var dB: DatabaseReference
    private lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_problem)
        editText = findViewById(R.id.textSend)
        button = findViewById(R.id.button_send)
        dB = FirebaseDatabase.getInstance().reference.child("CloseAccount").child("other")

        toolbar = findViewById(R.id.my_tools)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Close Account"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        editText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                Log.d("count",s.length.toString())
                button.isEnabled = s.length >= 10
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

            }
        })
        button.setOnClickListener {

            fun send(){
                val sendMap2 = hashMapOf<String, Any>()
                sendMap2[editText.text.toString()] = ServerValue.TIMESTAMP
                dB.updateChildren(sendMap2)
            }
            if(intent.hasExtra("formFeed"))
            CloseDialog(this, FirebaseAuth.getInstance().uid!!){ send() }.show()
            else{
                val sendMap2 = hashMapOf<String, Any>()
                sendMap2["fb"] = editText.text.toString()
                sendMap2["id"] = FirebaseAuth.getInstance().uid.toString()
                sendMap2["time"] = ServerValue.TIMESTAMP
                FirebaseDatabase.getInstance().reference.child("FeedBacks").push().updateChildren(sendMap2)
                setResult(14)
                finish()
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
}