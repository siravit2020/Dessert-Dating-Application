package com.jabirdeveloper.tinderswipe.Register

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.chaos.view.PinView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jabirdeveloper.tinderswipe.Functions.LoadingDialog
import com.jabirdeveloper.tinderswipe.R
import com.jabirdeveloper.tinderswipe.SwitchpageActivity
import com.tapadoo.alerter.Alerter
import java.util.concurrent.TimeUnit

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class VerifyActivity : AppCompatActivity() {
    private var verificationCodeBysystem: String? = ""
    private lateinit var b1: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseAuthStateListener: AuthStateListener
    private lateinit var pinGroup: PinView
    private lateinit var commend: TextView
    private lateinit var dialog: Dialog
    private lateinit var toolbar: Toolbar

    @SuppressLint("SetTextI18n", "InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_)
        toolbar = findViewById(R.id.my_tools)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.signin_phone)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mAuth = FirebaseAuth.getInstance()
        b1 = findViewById(R.id.button8)
        pinGroup = findViewById(R.id.secondPinView)
        commend = findViewById(R.id.commend)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.progress_dialog, null)
        dialog = LoadingDialog(this).dialog()
        firebaseAuthStateListener = AuthStateListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val userDb = FirebaseDatabase.getInstance().reference.child("Users").child(user.uid)
                userDb.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        dialog.dismiss()
                        if (dataSnapshot.hasChild("sex")) {
                            val intent = Intent(this@VerifyActivity, SwitchpageActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intent.putExtra("first", "0")
                            startActivity(intent)
                            finish()
                            return
                        } else {
                            val intent = Intent(this@VerifyActivity, Regis_name_Activity::class.java)
                            intent.putExtra("Type", "face")
                            startActivity(intent)
                            return
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
        }
        val phoneNo = intent.getStringExtra("Phone")!!
        Toast.makeText(this@VerifyActivity, phoneNo, Toast.LENGTH_SHORT).show()
        sendVerification(phoneNo)
        commend.text = "${getString(R.string.verification)} $phoneNo ${getString(R.string.please)}"
        b1.setOnClickListener(View.OnClickListener {
            val code = pinGroup.text.toString()
            if (code.isEmpty() || code.length < 6) {
                pinGroup.requestFocus()
                return@OnClickListener
            }
            verifyCode(code)
        })
    }

    private fun sendVerification(phoneNo: String) {
        val options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNo)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            if (verificationCodeBysystem != "") {
                val code = credential.smsCode
                verifyCode(code)
                Log.d("Vericom", "1")
            } else {
                Log.d("Vericom", "2")
                singInTheUserByCredentials(credential)
                Alerter.create(this@VerifyActivity)
                        .setTitle(getString(R.string.Sign))
                        .setText(getString(R.string.logging))
                        .setBackgroundColorRes(R.color.c2)
                        .show()
                // dialog.show()
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w("TAG", "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                Log.d("error", e.message!!)
            } else if (e is FirebaseTooManyRequestsException) {
                Log.d("error", e.message!!)
            }

            // Show a message and update the UI
            // ...
        }

        override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
        ) {
            //dialog.show()
            Log.d("Verisend", "1")
            verificationCodeBysystem = verificationId

        }
    }


    private fun verifyCode(codeByUser: String?) {
        if (verificationCodeBysystem != "") {
            val credential = PhoneAuthProvider.getCredential(verificationCodeBysystem!!, codeByUser!!)
            singInTheUserByCredentials(credential)
            pinGroup.setText(codeByUser)
            dialog.show()
        } else {
            dialog.dismiss()
            Snackbar.make(pinGroup, "กำลังส่งรหัส OTP ไป", Snackbar.LENGTH_SHORT).show()
        }

    }

    private fun singInTheUserByCredentials(credential: PhoneAuthCredential?) {
        mAuth.signInWithCredential(credential!!).addOnCompleteListener(this@VerifyActivity) { task ->
            if (task.isSuccessful) {
                dialog.show()
            } else {
                Snackbar.make(pinGroup, "Try again later.", Snackbar.LENGTH_SHORT).show()
                dialog.dismiss()
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