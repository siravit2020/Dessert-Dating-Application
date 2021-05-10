package com.jabirdeveloper.tinderswipe.Functions

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.jabirdeveloper.tinderswipe.ChooseLoginRegistrationActivity

class Close(private var currentUid: String, private var context: Context) {
    fun delete() {
        val filepath = FirebaseStorage.getInstance().reference.child("profileImages").child(currentUid).listAll()
        filepath.addOnSuccessListener {
            for (x in 0..it.items.size-2) {
                it.items[x].delete()
            }
            it.items[it.items.size-1].delete().addOnSuccessListener {
                val intent = Intent(context, ChooseLoginRegistrationActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
                FirebaseAuth.getInstance().signOut()
            }
        }
        val userDb = FirebaseDatabase.getInstance().reference.child("Users").child(currentUid)
        userDb.removeValue()
        val userAllDb = FirebaseDatabase.getInstance().reference.child("Users")
        userAllDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.child("connection").child("yep").hasChild(currentUid)) {
                    FirebaseDatabase.getInstance().reference
                            .child("Users")
                            .child(dataSnapshot.key.toString())
                            .child("connection")
                            .child("yep")
                            .child(currentUid)
                            .removeValue()
                }
                if (dataSnapshot.child("connection").child("matches").hasChild(currentUid)) {
                    FirebaseDatabase.getInstance().reference
                            .child("Users")
                            .child(dataSnapshot.key.toString())
                            .child("connection")
                            .child("matches")
                            .child(currentUid)
                            .removeValue()
                }
                if (dataSnapshot.child("connection").child("chatna").hasChild(currentUid)) {
                    FirebaseDatabase.getInstance().reference
                            .child("Users")
                            .child(dataSnapshot.key.toString())
                            .child("connection")
                            .child("chatna")
                            .child(currentUid)
                            .removeValue()
                }
                if (dataSnapshot.child("connection").child("nope").hasChild(currentUid)) {
                    FirebaseDatabase.getInstance().reference
                            .child("Users")
                            .child(dataSnapshot.key.toString())
                            .child("connection")
                            .child("nope")
                            .child(currentUid)
                            .removeValue()
                }
                if (dataSnapshot.child("see_profile").hasChild(currentUid)) {
                    FirebaseDatabase.getInstance().reference
                            .child("Users")
                            .child(dataSnapshot.key.toString())
                            .child("see_profile")
                            .child(currentUid)
                            .removeValue()
                }

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })


    }


}