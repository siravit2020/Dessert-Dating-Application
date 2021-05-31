package com.jabirdeveloper.tinderswipe.services

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.jabirdeveloper.tinderswipe.R

class AuthenticationsService(private val activity: Activity) {
    private lateinit var googleSignInClient: GoogleSignInClient

    fun authenticationWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(activity, gso)
        googleSignInClient.signInIntent
    }
}