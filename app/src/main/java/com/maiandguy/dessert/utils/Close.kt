package com.maiandguy.dessert.utils

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.maiandguy.dessert.ui.sign_in_activity.view.SignInActivity

class Close(private var currentUid: String, private var context: Context) {
    fun delete() {
        val user = Firebase.auth.currentUser!!
        user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val filepath = FirebaseStorage.getInstance().reference.child("profileImages").child(currentUid).listAll()
                        filepath.addOnSuccessListener {
                            for (x in 0..it.items.size-2) {
                                it.items[x].delete()
                            }
                            it.items[it.items.size-1].delete().addOnSuccessListener {

                            }
                        }
                        val userDb = FirebaseDatabase.getInstance().reference.child("Users").child(currentUid)
                        if(userDb.removeValue().isSuccessful){
                            val intent = Intent(context, SignInActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
                            FirebaseAuth.getInstance().signOut()
                        }

                    }
                }

    }
}