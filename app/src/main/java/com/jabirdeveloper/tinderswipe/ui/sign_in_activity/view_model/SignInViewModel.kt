package com.jabirdeveloper.tinderswipe.ui.sign_in_activity.view_model

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MethodCallsLogger
import androidx.lifecycle.MutableLiveData
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jabirdeveloper.tinderswipe.Functions.LoadingDialog
import com.jabirdeveloper.tinderswipe.R
import com.jabirdeveloper.tinderswipe.data.api.Resource

class SignInViewModel(application: Application) : AndroidViewModel(application) {


    private var firebaseAuthStateListener: FirebaseAuth.AuthStateListener
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 0

    private var resource = MutableLiveData<Resource<String>>()
    private var dialog = MutableLiveData<Boolean>()
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
                            else -> {
                                resource.value = Resource.success("register")
                            }
                        }
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

    fun facebookSigIn(activity: Activity, mCallbackManager:CallbackManager) {

        LoginManager.getInstance().logInWithReadPermissions(activity, listOf("email", "public_profile", "user_friends"))
        LoginManager.getInstance().registerCallback(mCallbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                handleFacebookToken(loginResult?.accessToken,activity)
            }

            override fun onCancel() {

            }
            override fun onError(exception: FacebookException?) {

                resource.value = Resource.error(exception.toString(), null)
            }
        })
    }

    private fun handleFacebookToken(token: AccessToken?,activity: Activity) {

        dialog.value = true
        val credential = FacebookAuthProvider.getCredential(token!!.token)
        mAuth.signInWithCredential(credential).addOnCompleteListener(activity) { task ->
            if (!task.isSuccessful) {

                resource.value = Resource.error("Please try again later", "face")
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

    private fun firebaseAuthWithGoogle(idToken: String?,activity: Activity) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { task ->
                    dialog.value = true
                    if (!task.isSuccessful) {

                        resource.value = Resource.error("Please try again later", "google")
                    }
                    resource.value = Resource.success(null)
                }
    }

    fun result(requestCode: Int?, data: Intent?,activity: Activity) {
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.d("TAG", "firebaseAuthWithGoogle:" + account?.id)
                firebaseAuthWithGoogle(account?.idToken,activity)
            } catch (e: ApiException) {

                Log.d("TAG", "Google sign in failed", e)
                resource.value = Resource.error("Please try again later", "google")
            }
        }

    }

    fun getResource(): LiveData<Resource<String>> {
        return resource
    }

    fun getStatusDialog(): LiveData<Boolean>{
        return dialog
    }

    override fun onCleared() {
        super.onCleared()
        mAuth.removeAuthStateListener(firebaseAuthStateListener)
    }
}