package com.jabirdeveloper.tinderswipe.ui.first_activity.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.jabirdeveloper.tinderswipe.data.api.FirstAuthentication
import com.jabirdeveloper.tinderswipe.services.LocationService
import com.jabirdeveloper.tinderswipe.utils.CheckStatusUser
import com.jabirdeveloper.tinderswipe.utils.Status
import kotlinx.android.synthetic.main.activity_first_.*

class FirstViewModel(application: Application) : AndroidViewModel(application) {
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firstAuthentication: FirstAuthentication = FirstAuthentication()
    private var statusUser: MutableLiveData<CheckStatusUser> = firstAuthentication.getStatusUser()
    private var checkUserNull = MutableLiveData<Boolean>()
    private var animation = MutableLiveData<Boolean>()
    private var firebaseAuthStateListener: FirebaseAuth.AuthStateListener
    private var locationService = LocationService(application)

    init {
        animation.value = true
        firebaseAuthStateListener = setListener()
        if (locationService.checkPermission() == Status.SUCCESS)
            mAuth.addAuthStateListener(firebaseAuthStateListener)
    }

    private fun setListener(): FirebaseAuth.AuthStateListener {
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



    fun getStatus(): LiveData<CheckStatusUser> {
        return statusUser
    }

    fun getCheckUser(): LiveData<Boolean> {
        return checkUserNull
    }



    fun getAniamtionStart(): LiveData<Boolean> {
        return animation
    }

    override fun onCleared() {
        super.onCleared()
        mAuth.removeAuthStateListener(firebaseAuthStateListener)
    }
}