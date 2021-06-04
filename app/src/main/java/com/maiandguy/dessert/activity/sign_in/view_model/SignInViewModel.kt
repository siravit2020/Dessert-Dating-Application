package com.maiandguy.dessert.activity.sign_in.view_model

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import android.util.Patterns
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.maiandguy.dessert.R
import com.maiandguy.dessert.utils.Resource

class SignInViewModel(application: Application) : AndroidViewModel(application) {


    private var firebaseAuthStateListener: FirebaseAuth.AuthStateListener
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 0
    private var resource = MutableLiveData<Resource<String>>()
    private var dialog = MutableLiveData<Boolean>()
    private var app = application
    private var thisEmail:Boolean = false

    init {
        firebaseAuthStateListener = FirebaseAuth.AuthStateListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val userDb = FirebaseDatabase.getInstance().reference
                userDb.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        when {
                            dataSnapshot.child("BlackList").hasChild(user.uid) -> {
                                mAuth.signOut()
                                resource.value = Resource.success("blackList")
                            }
                            dataSnapshot.child("Users").child(user.uid).hasChild("sex") -> {
                                resource.value = Resource.success("main")
                            }
                            !user.isEmailVerified
                            -> {
                                if(thisEmail)
                                    resource.value = Resource.success("verification")
                                else resource.value = Resource.success("register")

                            }
                            user.isEmailVerified -> {
                                if(thisEmail)
                                resource.value = Resource.success("register")
                            }
                        }
                        onCleared()

                        dialog.value = false
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
        }
        mAuth.addAuthStateListener(firebaseAuthStateListener)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(application.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(application, gso)

    }

    fun authenticationWithEmail(email: String, password: String) {
        if (email.trim().isEmpty() && password.trim().isEmpty()) {
            resource.value = Resource.error("กรุณากรอกข้อมูลให้ครบถ้วน", "email")
            return
        }
        else if (!email.isValidEmail()) {
            resource.value = Resource.error("กรุณากรอกอีเมลให้ถูกต้อง", "email")
            return
        }
        thisEmail = true
        if (email.trim { it <= ' ' } != "" && password.trim { it <= ' ' } != "") {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(ContextCompat.getMainExecutor(app)) { task ->
                if (!task.isSuccessful) {
                    resource.value = Resource.error("Email or Password incorrect", "email")
                }
            }
        }
    }


    fun googleSignIn(activity: Activity) {
        signIn(activity)
    }

    private fun signIn(activity: Activity) {
        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        thisEmail = false
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(ContextCompat.getMainExecutor(app)) { task ->
                    dialog.value = true
                    if (!task.isSuccessful) {

                        resource.value = Resource.error("Please try again later", "google")
                    }

                }
    }

    fun result(requestCode: Int?, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken)
            } catch (e: ApiException) {

                Log.d("TAG", "Google sign in failed", e)
                resource.value = Resource.error("Please try again later", "google")
            }
        }

    }

    fun getResource(): LiveData<Resource<String>> {
        return resource
    }

    fun getStatusDialog(): LiveData<Boolean> {
        return dialog
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("result","clean")
        resource = MutableLiveData<Resource<String>>()
        mAuth.removeAuthStateListener(firebaseAuthStateListener)
    }
    fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}