package com.maiguy.dessert.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.maiandguy.dessert.utils.ErrorDialog

import com.maiguy.dessert.activity.sign_in.view.SignInActivity
import com.maiguy.dessert.dialogs.LoadingDialog


class Close(private var currentUid: String, private var context: Context) {
    private var functions: FirebaseFunctions = Firebase.functions
    private val loadingDialog = LoadingDialog(context).dialog()
    private val errorDialog = ErrorDialog(context)
    fun delete() {
        loadingDialog.show()
        val userDb = Firebase.database.reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())
        val user = Firebase.auth.currentUser!!
        val data = hashMapOf(
                "uid" to currentUid
        )
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
                         functions.getHttpsCallable("closeAccount")
                                 .call(data)
                                 .addOnSuccessListener {
                                     Log.d("CloseAccountEvent",it.data.toString())
                                     userDb.onDisconnect().cancel()
                                     val intent = Intent(context, SignInActivity::class.java)
                                     intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                     context.startActivity(intent)
                                     FirebaseAuth.getInstance().signOut()
                                     errorDialog.questionAskDialog(it.data.toString()).show()
                                     loadingDialog.dismiss()
                                 }
                                 .addOnFailureListener {
                                     Log.d("CloseAccountEvent",it.toString())
                                     errorDialog.questionAskDialog(it.toString()).show()
                                     loadingDialog.dismiss()
                                 }

                     }
                 }
                 .addOnFailureListener{
                     Log.d("CloseAccountEvent",it.toString())
                     errorDialog.questionAskDialog("กรุณาล็อคอินใหม่และลองใหม่อีกครั้ง").show()
                     loadingDialog.dismiss()
                 }

    }
}