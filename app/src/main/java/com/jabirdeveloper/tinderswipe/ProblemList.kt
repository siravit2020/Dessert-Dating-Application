package com.jabirdeveloper.tinderswipe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jabirdeveloper.tinderswipe.Functions.CloseDialog
import kotlinx.android.synthetic.main.activity_problem_list.c1
import kotlinx.android.synthetic.main.activity_problem_list.c2
import kotlinx.android.synthetic.main.activity_problem_list.c3
import kotlinx.android.synthetic.main.activity_problem_list.c4
import kotlinx.android.synthetic.main.activity_problem_list.c5

class ProblemList : AppCompatActivity(),View.OnClickListener {
    private lateinit var editText: EditText
    private lateinit var button: Button
    private lateinit var toolbar: Toolbar
    private var st = false
    private lateinit var dB: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_problem_list)
        dB = FirebaseDatabase.getInstance().reference.child("CloseAccount").child("problem")
        editText = findViewById(R.id.textSend)
        button = findViewById(R.id.button_send)
        c1.setOnClickListener(this)
        c2.setOnClickListener(this)
        c3.setOnClickListener(this)
        c4.setOnClickListener(this)
        c5.setOnClickListener(this)
        button.setOnClickListener(this)
        toolbar = findViewById(R.id.my_tools)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Close Account"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        editText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                Log.d("count",s.length.toString())
                st = s.isNotEmpty()
                check()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

            }
        })
    }

    private fun check(){
        when {
            c1.isChecked -> {
                button.isEnabled = true
            }
            c2.isChecked -> {
                button.isEnabled = true
            }
            c3.isChecked -> {
                button.isEnabled = true
            }
            c4.isChecked -> {
                button.isEnabled = true
            }
            st -> {button.isEnabled = true}
            else -> button.isEnabled = c5.isChecked
        }
    }

    override fun onClick(v: View) {
        check()
        if(v == button){
            fun send(){
                dB.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val sendMap =  hashMapOf<String, Any>()
                        val sendMap2 =  hashMapOf<String, Any>()
                        val map:Map<*,*> = snapshot.value as Map<*, *>
                        Log.d("map",map.toString())
                        if(c1.isChecked){
                            sendMap["notFound"]  = map["notFound"].toString().toInt() + 1
                        }
                        if(c2.isChecked){
                            sendMap["badApp"]  = map["badApp"].toString().toInt() + 1
                        }
                        if(c3.isChecked){
                            sendMap["MatchMiss"]  = map["MatchMiss"].toString().toInt() + 1
                        }
                        if(c4.isChecked){
                            sendMap["overAndOver"]  = map["overAndOver"].toString().toInt() + 1
                        }
                        if(c5.isChecked){
                            sendMap["badChat"]  = map["badChat"].toString().toInt() + 1
                        }
                        if(st){
                            sendMap2[editText.text.toString()]  = ServerValue.TIMESTAMP
                        }
                        dB.updateChildren(sendMap)
                        dB.child("other").updateChildren(sendMap2)

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }
            CloseDialog(this, FirebaseAuth.getInstance().uid!!){ send() }.show()
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