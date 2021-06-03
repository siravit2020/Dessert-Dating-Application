package com.maiandguy.dessert.data.api

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.maiandguy.dessert.utils.CheckStatusUser


class FirstAuthentication {
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var usersDb: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
    private  var status = MutableLiveData<CheckStatusUser>()

    fun check() {
        usersDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(mAuth.currentUser!!.uid).hasChild("sex")) {
                    pushToken()
                } else {
                    mAuth.signOut()
                    status.value = CheckStatusUser.CHOOSE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun pushToken() {

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                status.value = CheckStatusUser.ERROR
                return@OnCompleteListener
            }
            status.value = CheckStatusUser.SWITCH
            val token = task.result
            FirebaseDatabase.getInstance().reference.child("Users").child(mAuth.currentUser!!.uid).child("token").setValue(token)

        })
    }

    fun getStatusUser():MutableLiveData<CheckStatusUser>{
        return status
    }
}