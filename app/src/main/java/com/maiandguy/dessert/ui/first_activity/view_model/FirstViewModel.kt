package com.maiandguy.dessert.ui.first_activity.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.maiandguy.dessert.data.api.FirstAuthentication
import com.maiandguy.dessert.utils.CheckStatusUser

class FirstViewModel : ViewModel() {
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firstAuthentication: FirstAuthentication = FirstAuthentication()
    private var statusUser: MutableLiveData<CheckStatusUser> = firstAuthentication.getStatusUser()
    private var checkUserNull = MutableLiveData<Boolean>()
    private var animation = MutableLiveData<Boolean>()
    private var firebaseAuthStateListener: FirebaseAuth.AuthStateListener
    init {
        animation.value = true
        firebaseAuthStateListener = listener()
    }
    private fun listener(): FirebaseAuth.AuthStateListener {
        return FirebaseAuth.AuthStateListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                checkUserNull.value = true
                checkUser()
            } else {
                checkUserNull.value = false
            }
        }
    }
    private fun checkUser() {
        firstAuthentication.check()
    }
    fun addListener(){
        mAuth.addAuthStateListener(firebaseAuthStateListener)
    }
    fun getStatus(): LiveData<CheckStatusUser> {
        return statusUser
    }
    fun getCheckUser(): LiveData<Boolean> {
        return checkUserNull
    }
    fun getAnimationStart(): LiveData<Boolean> {
        return animation
    }
    override fun onCleared() {
        super.onCleared()
        mAuth.removeAuthStateListener(firebaseAuthStateListener)
    }
}