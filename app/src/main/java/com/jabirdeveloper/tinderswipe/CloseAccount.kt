package com.jabirdeveloper.tinderswipe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jabirdeveloper.tinderswipe.Functions.CloseDialog

class CloseAccount : AppCompatActivity(), View.OnClickListener {
    private lateinit var toolbar: Toolbar
    private lateinit var cardRe: MaterialCardView
    private lateinit var cardLove: MaterialCardView
    private lateinit var cardBad: MaterialCardView
    private lateinit var cardProblem: MaterialCardView
    private lateinit var cardOther: MaterialCardView
    private lateinit var dB: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_close_account)
        dB = FirebaseDatabase.getInstance().reference.child("CloseAccount")
        toolbar = findViewById(R.id.my_tools)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Close Account"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        cardBad = findViewById(R.id.close_bad)
        cardLove = findViewById(R.id.close_love)
        cardRe = findViewById(R.id.close_re)
        cardOther = findViewById(R.id.close_other)
        cardProblem = findViewById(R.id.close_problem)
        cardRe.setOnClickListener(this)
        cardLove.setOnClickListener(this)
        cardBad.setOnClickListener(this)
        cardProblem.setOnClickListener(this)
        cardOther.setOnClickListener(this)
    }

    override fun onClick(v: View) {


            if (v == cardRe) {
                dB.child("restart").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        fun send() {
                            val count = snapshot.value.toString().toInt() + 1
                            dB.child("restart").setValue(count)}
                        CloseDialog(this@CloseAccount, FirebaseAuth.getInstance().uid!!) { send() }.show()


                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }
            if (v == cardLove) {
                dB.child("love").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        fun send(){
                            val count = snapshot.value.toString().toInt() + 1
                            dB.child("love").setValue(count)
                        }
                        CloseDialog(this@CloseAccount, FirebaseAuth.getInstance().uid!!) { send() }.show()
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }
            if (v == cardBad) {
                val intent = Intent(applicationContext, IDontLike::class.java)
                startActivity(intent)
            }
            if (v == cardProblem) {
                val intent = Intent(applicationContext, ProblemList::class.java)
                startActivity(intent)
            }
            if (v == cardOther) {
                val intent = Intent(applicationContext, SendProblem::class.java)
                startActivity(intent)
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